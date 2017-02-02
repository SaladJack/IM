/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project.
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 *
 * QoS4SendDaemon.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.saladjack.im.ClientCoreSDK;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * QoS机制中提供消息送达质量保证的守护线程。
 * <br>
 * 本类是QoS机制的核心，极端情况下将弥补因UDP协议天生的不可靠性而带来的
 * 丢包情况。
 * <p>
 * 当前MobileIMSDK的QoS机制支持全部的C2C、C2S、S2C共3种消息交互场景下的
 * 消息送达质量保证.
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 * <p>
 * <b>FIXME: </b>按照目前MobileIMSDK通信机制的设计原理，有1种非常极端的情况目前的QoS
 * 重传绝对不会成功，那就是当对方非正常退出，而本地并未及时（在服务会话超时时间内）收到
 * 他下线通知，此时间间隔内发的消息，本地将尝试重传。但对方在重传重试期限内正常登录也将
 * 绝不会收到，为什么呢？因为对方再次登录时user_id已经更新成新的了，之前的包记录的发送
 * 目的地还是老user_id。这种情况可以改善，那就是这样的包里还记录它的登录名，服务端将据
 * user_id尝试给目标发消息，但user_id不存在的情况下（即刚才这种情况）可以用登录名尝试
 * 找到它的新user_id，从而向新user_id发消息就可以让对方收到了。目前为了最大程度保证算
 * 法的合理性和简洁性暂不实现这个了，好在客户端业务层可无条件判定并提示该消息没有成功发
 * 送，那此种情况在应用层的体验上也是可接受的！
 *
 */
public class QoS4SendDaemon
{
	private final static String TAG = QoS4SendDaemon.class.getSimpleName();

	// 并发Hash，因为本类中可能存在不同的线程同时remove或遍历之
	private ConcurrentHashMap<String, Protocal> sentMessages = new ConcurrentHashMap<String, Protocal>();
	// 并发Hash，因为本类中可能存在不同的线程同时remove或遍历之
	/**
	 * 本Hash表目前仅用于QoS重传判断是否是“刚刚”发出的消息之用，别无它用。
	 *
	 * @author Jack Jiang, 2015-11-02 22:32
	 * @see MESSAGES_JUST$NOW_TIME
	 * @since 2.1.1
	 */
	private ConcurrentHashMap<String, Long> sendMessagesTimestamp = new ConcurrentHashMap<String, Long>();

	/**
	 * QoS质量保证线程心跳间隔（单位：毫秒），默认5000ms.
	 * <p>
	 * 间隔越短则为用户重发越即时，但将使得重复发送的可能性增大（因为可能在应答
	 * 包尚在途中时就判定丢包了的错误情况），当然，即使真存在重复发送的可能也是无害的
	 * ，因为MobileIMSDK的QoS机制本身就有防重能力。请根据您的应用所处的网络延迟情况进行
	 * 权衡，本参数为非关键参数，如无特殊情况则建议无需调整本参数。
	 */
	public final static int CHECH_INTERVAL = 5000;

	/**
	 * “刚刚”发出的消息阀值定义（单位：毫秒），默认3000毫秒。
	 * <b>注意：此值通常无需由开发者自行设定，保持默认即可。</b>
	 * <p>
	 * 此阀值的作用在于：在QoS=true的情况下，一条刚刚发出的消息会同时保存到本类中的QoS保证队列，
	 * 在接收方的应答包还未被发出方收到时（已经发出但因为存在数十毫秒的网络延迟，应答包正在路上）
	 * ，恰好遇到本次QoS质量保证心跳间隔的到来，因为之前的QoS队列逻辑是只要存在本队列中还未被去掉
	 * 的包，就意味着是要重传的——那么此逻辑在我们本次讨论的情况下就存在漏洞而导致没有必要的重传了。
	 * 如果有本阀值存在，则即使刚刚发出的消息刚放到QoS队列就遇到QoS心跳到来，则只要当前放入队列的时间
	 * 小于或等于本值，就可以被认为是刚刚放入，那么也就避免被误重传了。
	 * <p>
	 * 基于以上考虑，本值的定义，只要设定为大于一条消息的发出起到收到它应答包为止这样一个时间间隔即可
	 * （其实就相于一个客户端到服务端的网络延迟时间4倍多一点点即可）。
	 * 此处定为3秒其实是为了保守起见哦。
	 * <p>
	 * 本参数将决定真正因为UDP丢包而重传的即时性问题，即当MobileIMSDK的UDP丢包时，QoS首次重传的
	 * 响应时间为> {@link #MESSAGES_JUST$NOW_TIME}(即{@value #MESSAGES_JUST$NOW_TIME}毫秒) 而 <= {@link #CHECH_INTERVAL}(即{@value #CHECH_INTERVAL}毫秒)。
	 *
	 * @author Jack Jiang, 2015-11-02 22:32
	 * @since 2.1.1
	 */
	public final static int MESSAGES_JUST$NOW_TIME = 3 * 1000;

	/**
	 * 一个包允许的最大重发次数，默认3次。
	 * <p>
	 * 次数越多，则整个UDP的可靠性越好，但在网络确实很烂的情况下可能会导致重传的泛滥而失去
	 * “即时”的意义。请根据网络状况和应用体验来权衡设定，本参数为0表示不重传，建议使用1到5
	 * 之间的数字。
	 */
	public final static int QOS_TRY_COUNT = 3;

	private Handler handler = null;
	private Runnable runnable = null;

	/** 当前线程是否正在执行中 */
	private boolean running = false;

	private boolean _excuting = false;

	private Context context = null;

	private static QoS4SendDaemon instance = null;
	private Subscription cycleSubscribe;
	private Subscription subscribe;

	public static QoS4SendDaemon getInstance(Context context)
	{
		if(instance == null)
			instance = new QoS4SendDaemon(context);

		return instance;
	}

	private QoS4SendDaemon(Context context)
	{
		this.context = context;
		init();
	}



	/**
	 * 将未送达信息反馈给消息监听者。
	 *
	 * @param lostMessages 已被判定为“消息未送达”的消息列表
	 * @see {@link ClientCoreSDK.getInstance().getMessageQoSEvent()}
	 */
	protected void notifyMessageLost(ArrayList<Protocal> lostMessages)
	{
		if(ClientCoreSDK.getInstance().getMessageQoSEvent() != null)
			ClientCoreSDK.getInstance().getMessageQoSEvent().messagesLost(lostMessages);
	}

	/**
	 * 启动线程。
	 * <p>
	 * 无论本方法调用前线程是否已经在运行中，都会尝试首先调用 {@link #stop()}方法，
	 * 以便确保线程被启动前是真正处于停止状态，这也意味着可无害调用本方法。
	 * <p>
	 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
	 *
	 * @param immediately true表示立即执行线程作业，否则直到 {@link #CHECH_INTERVAL}
	 * 执行间隔的到来才进行首次作业的执行
	 */
	public void startup(boolean immediately)
	{
		//
		stop();

		//
		handler.postDelayed(runnable, immediately ? 0 : CHECH_INTERVAL);
		//
		running = true;
	}

	/**
	 * 无条件中断本线程的运行。
	 * <p>
	 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
	 */
	public void stop()
	{
		//
		handler.removeCallbacks(runnable);
		//
		running = false;
	}

	/**
	 * 线程是否正在运行中。
	 *
	 * @return true表示是，否则线路处于停止状态
	 */
	public boolean isRunning()
	{
		return running;
	}

	/**
	 * 该包是否已存在于队列中。
	 *
	 * @param fingerPrint 消息包的特纹特征码（理论上是唯一的）
	 * @return
	 */
	boolean exist(String fingerPrint)
	{
		return sentMessages.get(fingerPrint) != null;
	}

	/**
	 * 推入一个消息包的指纹特征码.
	 * <br>注意：本方法只会将指纹码推入，而不是将整个Protocal对象放入列表中。
	 *
	 * @param p
	 */
	public void put(Protocal p)
	{
		if(p == null)
		{
			Log.w(TAG, "Invalid arg p==null.");
			return;
		}
		if(p.getFp() == null)
		{
			Log.w(TAG, "Invalid arg p.getFp() == null.");
			return;
		}

		if(!p.isQoS())
		{
			Log.w(TAG, "This protocal is not QoS pkg, ignore it!");
			return;
		}

		// 如果列表中已经存则仅提示（用于debug）
		if(sentMessages.get(p.getFp()) != null)
			Log.w(TAG, "【QoS】指纹为"+p.getFp()+"的消息已经放入了发送质量保证队列，该消息为何会重复？（生成的指纹码重复？还是重复put？）");

		// save it
		sentMessages.put(p.getFp(), p);
		// 同时保存时间戳
		sendMessagesTimestamp.put(p.getFp(), System.currentTimeMillis());
	}

	/**
	 * 移除一个消息包.
	 * <p>
	 * 此操作是在步异线程中完成，目的是尽一切可能避免可能存在的阻塞本类中的守望护线程.
	 *
	 * @param fingerPrint 消息包的特纹特征码（理论上是唯一的）
	 * @return
	 */
	public void remove(final String fingerPrint)
	{
		// remove it
		new AsyncTask(){
			@Override
			protected Object doInBackground(Object... params)
			{
				sendMessagesTimestamp.remove(fingerPrint);
				return sentMessages.remove(fingerPrint);
			}
			protected void onPostExecute(Object result)
			{
				Log.w(TAG, "【QoS】指纹为"+fingerPrint+"的消息已成功从发送质量保证队列中移除(可能是收到接收方的应答也可能是达到了重传的次数上限)，重试次数="
						+(result != null?((Protocal)result).getRetryCount():"none呵呵."));
			}
		}.execute();
	}

	/**
	 * 队列大小.
	 *
	 * @return
	 * @see HashMap#size()
	 */
	public int size()
	{
		return sentMessages.size();
	}

	private void init()
	{
		handler = new Handler();
		runnable = new Runnable()
		{
			@Override
			public void run()
			{
				// 极端情况下本次循环内可能执行时间超过了时间间隔，此处是防止在前一
				// 次还没有运行完的情况下又重复执行，从而出现无法预知的错误
				if(!_excuting)
				{
					// Handler的机制是在主线程中执行的，所以此处在放在另一个线程里，否则会报错哦
					new AsyncTask<Object, Integer, ArrayList<Protocal>>()
					{
						// 丢包列表
						private ArrayList<Protocal> lostMessages = new ArrayList<Protocal>();

						@Override
						protected ArrayList<Protocal> doInBackground(Object... params)
						{
							_excuting = true;
							try
							{
								if(ClientCoreSDK.DEBUG)
									Log.d(TAG, "【QoS】=========== 消息发送质量保证线程运行中, 当前需要处理的列表长度为"+sentMessages.size()+"...");

								// 开始处理中 ************************************************
								for(String key : sentMessages.keySet())
								{
									final Protocal p = sentMessages.get(key);
									if(p != null && p.isQoS())
									{
										// 达到或超过了最大重试次数（判定丢包）
										if(p.getRetryCount() >= QOS_TRY_COUNT)
										{
											if(ClientCoreSDK.DEBUG)
												Log.d(TAG, "【QoS】指纹为"+p.getFp()
														+"的消息包重传次数已达"+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次)上限，将判定为丢包！");

											// 将这个包加入到丢包列表（该Protocal对象将是一个clone的全新对象而非原来的引用哦！）
											lostMessages.add((Protocal)p.clone());

											// 从列表中称除之
											remove(p.getFp());
										}
										// 没有达到重传上限则开始进行重传
										else
										{
											//### 2015103 Bug Fix: 解决了无线网络延较大时，刚刚发出的消息在其应答包还在途中时被错误地进行重传
											long delta = System.currentTimeMillis() - sendMessagesTimestamp.get(key);
											// 该消息包是“刚刚”发出的，本次不需要重传它哦
											if(delta <= MESSAGES_JUST$NOW_TIME)
											{
												if(ClientCoreSDK.DEBUG)
													Log.w(TAG, "【QoS】指纹为"+key+"的包距\"刚刚\"发出才"+delta
															+"ms(<="+MESSAGES_JUST$NOW_TIME+"ms将被认定是\"刚刚\"), 本次不需要重传哦.");
											}
											//### 2015103 Bug Fix END
											else
											{
												new LocalUDPDataSender.SendCommonDataAsync(context, p){
													@Override
													protected void onPostExecute(Integer code)
													{
														// 已成功重传
														if(code == 0)
														{
															// 重传次数+1
															p.increaseRetryCount();

															if(ClientCoreSDK.DEBUG)
																Log.d(TAG, "【QoS】指纹为"+p.getFp()
																		+"的消息包已成功进行重传，此次之后重传次数已达"
																		+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次).");
														}
														else
														{
															Log.w(TAG, "【QoS】指纹为"+p.getFp()
																	+"的消息包重传失败，它的重传次数之前已累计为"
																	+p.getRetryCount()+"(最多"+QOS_TRY_COUNT+"次).");
														}
													}
												}.execute();
											}
										}
									}
									// value值为null，从列表中去掉吧
									else
									{
//										sentMessages.remove(key);
										remove(key);
									}
								}
							}
							catch (Exception eee)
							{
								Log.w(TAG, "【QoS】消息发送质量保证线程运行时发生异常,"+eee.getMessage(), eee);
							}

							return lostMessages;
						}

						@Override
						protected void onPostExecute(ArrayList<Protocal> al)
						{
							if(al != null && al.size() > 0)
								// 通知观察者这些包丢包了（目标接收者没有收到）
								notifyMessageLost(al);

							//
							_excuting = false;
							// 开始下一个心跳循环
							handler.postDelayed(runnable, CHECH_INTERVAL);
						}
					}.execute();
				}
			}
		};
	}
}
