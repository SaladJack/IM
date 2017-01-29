/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * KeepAliveDaemon.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.core;

import java.util.Observer;

import com.saladjack.im.ClientCoreSDK;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;


/**
 * 用于保持与服务端通信活性的Keep alive独立线程。
 * <p>
 * <b>Keep alive的目的有2个：</b>
 * <br>
 * 1、<u>防止NAT路由算法导致的端口老化</u>：
 * <br>
 * <code>
 * 路由器的NAT路由算法存在所谓的“端口老化”概念
 * （理论上NAT算法中UDP端口老化时间为300S，但这不是标准，而且中高端路由器
 * 可由网络管理员自行设定此值），Keep alive机制可确保在端口老化时间到来前
 * 重置老化时间，进而实现端口“保活”的目的，否则端口老化导致的后果是服务器
 * 将向客户端发送的数据将被路由器抛弃。
 * </code>
 * <br>
 * 2、<u>即时探测由于网络状态的变动而导致的通信中断</u>（进而自动触发自动治愈机制）：
 * <br>
 * <code>
 * 此种情况可的原因有（但不限于）：无线网络信号不稳定、WiFi与2G/3G/4G等同开情
 * 况下的网络切换、手机系统的省电策略等。
 * </code>
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 * 
 * @author Jack Jiang, 2013-10-09
 * @version 1.0
 */
public class KeepAliveDaemon
{
	private final static String TAG = KeepAliveDaemon.class.getSimpleName();
	
	/** 
	 * 收到服务端响应心跳包的超时间时间（单位：毫秒），默认（3000 * 3 + 1000）＝ 10000 毫秒.
	 * <p>
	 * 超过这个时间客户端将判定与服务端的网络连接已断开（此间隔建议为(KEEP_ALIVE_INTERVAL * 3) + 1 秒），
	 * 没有上限，但不可太长，否则将不能即时反映出与服务器端的连接断开（比如掉掉线时），请从
	 * 能忍受的反应时长和即时性上做出权衡。
	 * <p>
	 * 本参数除与{@link KeepAliveDaemon#KEEP_ALIVE_INTERVAL}有关联外，不受其它设置影响。
	 */
	public static int NETWORK_CONNECTION_TIME_OUT = 10 * 1000;
	
	/** 
	 * Keep Alive 心跳时间间隔（单位：毫秒），默认3000毫秒.
	 * <p>
	 * 心跳间隔越短则保持会话活性的健康度更佳，但将使得在大量客户端连接情况下服务端因此而增加负载，
	 * 且手机将消耗更多电量和流量，所以此间隔需要权衡（建议为：>=1秒 且 < 300秒）！
	 * <p>
	 * 说明：此参数用于设定客户端发送到服务端的心跳间隔，心跳包的作用是用来保持与服务端的会话活性（
	 * 更准确的说是为了避免客户端因路由器的NAT算法而导致UDP端口老化）. 
	 * <p>
	 * 参定此参数的同时，也需要相应设置服务端的ServerLauncher.SESION_RECYCLER_EXPIRE参数。
	 */
	public static int KEEP_ALIVE_INTERVAL = 3000;//1000;

	private Handler handler = null;
	private Runnable runnable = null;
	
	/** 当前心跳线程是否正在执行中 */
	private boolean keepAliveRunning = false;
	
	// 记录最近一次服务端的心跳响应包时间
	private long lastGetKeepAliveResponseFromServerTimstamp = 0;
	
	private static KeepAliveDaemon instance = null;
	
	/** 网络断开事件观察者 */
	private Observer networkConnectionLostObserver = null;
	
	private boolean _excuting = false;
	
	private Context context = null;
	
	public static KeepAliveDaemon getInstance(Context context)
	{
		if(instance == null)
			instance = new KeepAliveDaemon(context);
		return instance;
	}
	
	private KeepAliveDaemon(Context context)
	{
		this.context = context;
		init();
	}
	
	private void init()
	{
		handler = new Handler();
		runnable = new Runnable(){
			@Override
			public void run()
			{
				// 极端情况下本次循环内可能执行时间超过了时间间隔，此处是防止在前一
				// 次还没有运行完的情况下又重复过劲行，从而出现无法预知的错误
				if(!_excuting)
				{
					// Handler的机制是在主线程中执行的，所以此处在放在另一个线程里，否则会报错哦
					new AsyncTask<Object, Integer, Integer>()
					{
						private boolean willStop = false;
						
						@Override
						protected Integer doInBackground(Object... params)
						{
							_excuting = true;
							if(ClientCoreSDK.DEBUG)
								Log.d(TAG, "心跳线程执行中...");
							int code = LocalUDPDataSender.getInstance(context).sendKeepAlive();
							
							return code;
						}

						@Override
						protected void onPostExecute(Integer code)
						{
							// 首先执行Keep Alive心跳包时，把此时的时间作为第1次收到服务响应的时间（初始化）
							boolean isInitialedForKeepAlive = (lastGetKeepAliveResponseFromServerTimstamp == 0);
							if(code == 0 && lastGetKeepAliveResponseFromServerTimstamp == 0)
								lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();

							// 首先启动心跳时就不判断了，否则就是逻辑有问题
							if(!isInitialedForKeepAlive)
							{
								long now = System.currentTimeMillis();
//								System.out.println(">>>> t1="+now+", t2="+lastGetKeepAliveResponseFromServerTimstamp+" -> 差："
//										+(now - lastGetKeepAliveResponseFromServerTimstamp));
								// 当当前时间与最近一次服务端的心跳响应包时间间隔>= 10秒就判定当前与服务端的网络连接已断开
								if(now - lastGetKeepAliveResponseFromServerTimstamp >= NETWORK_CONNECTION_TIME_OUT)
								{
									// 先停止心跳线程
									stop();
									// 再通知“网络连接已断开”
									if(networkConnectionLostObserver != null)
										networkConnectionLostObserver.update(null, null);

									willStop = true;
								}
							}
							
							//
							_excuting = false;
							if(!willStop)
								// 开始下一个心跳循环
								handler.postDelayed(runnable, KEEP_ALIVE_INTERVAL);
						}
					}.execute();
				}
			}
		};
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
		keepAliveRunning = false;
		//
		lastGetKeepAliveResponseFromServerTimstamp = 0;
	}
	
	/**
	 * 启动线程。
	 * <p>
	 * 无论本方法调用前线程是否已经在运行中，都会尝试首先调用 {@link #stop()}方法，
	 * 以便确保线程被启动前是真正处于停止状态，这也意味着可无害调用本方法。
	 * <p>
	 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
	 * 
	 * @param immediately true表示立即执行线程作业，否则直到 {@link #AUTO_RE$signin_INTERVAL}
	 * 执行间隔的到来才进行首次作业的执行
	 */
	public void start(boolean immediately)
	{
		//
		stop();
		
		//
		handler.postDelayed(runnable, immediately ? 0 : KEEP_ALIVE_INTERVAL);
		//
		keepAliveRunning = true;
	}
	
	/**
	 * 线程是否正在运行中。
	 * 
	 * @return true表示是，否则线路处于停止状态
	 */
	public boolean isKeepAliveRunning()
	{
		return keepAliveRunning;
	}
	
	/**
	 * 收到服务端反馈的心跳包时调用此方法：作用是更新服务端最背后的响应时间戳.
	 * <p>
	 * <b>本方法的调用，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
	 */
	public void updateGetKeepAliveResponseFromServerTimstamp()
	{
		lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();
	}

	/**
	 * 设置网络断开事件观察者.
	 * <p>
	 * <b>本方法的调用，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
	 * 
	 * @param networkConnectionLostObserver
	 */
	public void setNetworkConnectionLostObserver(Observer networkConnectionLostObserver)
	{
		this.networkConnectionLostObserver = networkConnectionLostObserver;
	}
}
