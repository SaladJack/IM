/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * QoS4ReciveDaemon.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.core;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.saladjack.im.ClientCoreSDK;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * QoS机制中提供对已收到包进行有限生命周期存储并提供重复性判断的守护线程。
 * <p>
 * <b>原理是：</b>当收到需QoS机制支持消息包时，会把它的唯一特征码（即指纹id）
 * 存放于本类的“已收到”消息队列中，寿命约为 {@link #MESSAGES_VALID_TIME}指明
 * 的时间，每当{@link #CHECH_INTERVAL}定时检查间隔到来时会对其存活期进行检查
 * ，超期将被移除，否则允许其继续存活。理论情况下，一个包的最大寿命不可能超过2
 * 倍的 {@link #CHECH_INTERVAL}时长。
 * <br>
 * <b><u>补充说明</u>：</b>“超期”即意味着对方要么已收到应答包（这是QoS机制正
 * 常情况下的表现）而无需再次重传、要么是已经达到QoS机制的重试极限而无可能再收
 * 到重复包（那么在本类列表中该表也就没有必要再记录了）。总之，“超期”是队列中
 * 这些消息包的正常生命周期的终止，无需过多解读。
 * <p>
 * <b>本类存在的意义在于：</b>极端情况下QoS机制中存在因网络丢包导致应答包的
 * 丢失而触发重传机制从而导致消息重复，而本类将维护一个有限时间段内收到的所有
 * 需要QoS支持的消息的指纹列表且提供“重复性”判断机制，从而保证应用层绝不会因为
 * QoS的重传机制而导致重复收到消息的情况。
 * <p>
 * 当前MobileIMSDK的QoS机制支持全部的C2C、C2S、S2C共3种消息交互场景下的
 * 消息送达质量保证.
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 * 
 */
public class QoS4ReciveDaemon
{
	private final static String TAG = QoS4ReciveDaemon.class.getSimpleName();
	
	/** 检查线程执行间隔（单位：毫秒），默认5分钟 */
	public final static int CHECH_INTERVAL = 5 * 60 * 1000; // 5分钟
	
	/** 一个消息放到在列表中（用于判定重复时使用）的生存时长（单位：毫秒），默认10分钟 */
	public final static int MESSAGES_VALID_TIME = 10 * 60 * 1000;// 10分钟
	
	/** 
	 * 时间间隔内接收到的需要QoS质量保证的消息指纹特征列表.
	 * key=消息包指纹码，value=最近1次收到该包的时间戳（时间戳用于判定该包是否已失效时有
	 * 用，收到重复包时用最近一次收到时间更新时间戳从而最大限度保证不重复） */
	private ConcurrentHashMap<String, Long> recievedMessages = new ConcurrentHashMap<String, Long>();
	
	private Handler handler = null;
	private Runnable runnable = null;
	
	/** 当前线程是否正在执行中 */
	private boolean running = false;
	
	private boolean _excuting = false;
	
	private Context context = null;
	
	private static QoS4ReciveDaemon instance = null;
	
	public static QoS4ReciveDaemon getInstance(Context context)
	{
		if(instance == null)
			instance = new QoS4ReciveDaemon(context);
		
//		if(!instance.isRunning())
//			instance.startup(true);
		
		return instance;
	}
	
	public QoS4ReciveDaemon(Context context)
	{
		this.context = context;
		
		init();
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
				// 次还没有运行完的情况下又重复过劲行，从而出现无法预知的错误
				if(!_excuting)
				{
					_excuting = true;
					
					if(ClientCoreSDK.DEBUG)
						Log.d(TAG, "【QoS接收方】++++++++++ START 暂存处理线程正在运行中，当前长度"+recievedMessages.size()+".");
					
					// 
					for(String key : recievedMessages.keySet())
					{
						long delta = System.currentTimeMillis() - recievedMessages.get(key);
						// 该消息包超过了生命时长，去掉之
						if(delta >= MESSAGES_VALID_TIME)
						{
							if(ClientCoreSDK.DEBUG)
								Log.d(TAG, "【QoS接收方】指纹为"+key+"的包已生存"+delta
									+"ms(最大允许"+MESSAGES_VALID_TIME+"ms), 马上将删除之.");
							recievedMessages.remove(key);
						}
					}
				}

				if(ClientCoreSDK.DEBUG)
					Log.d(TAG, "【QoS接收方】++++++++++ END 暂存处理线程正在运行中，当前长度"+recievedMessages.size()+".");
				
				//
				_excuting = false;
				// 开始下一个心跳循环
				handler.postDelayed(runnable, CHECH_INTERVAL);
			}
		};
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
	public void startup(boolean immediately)
	{
		//
		stop();
		
		// ** 如果列表不为空则尝试重置生成起始时间
		// 当重启时列表可能是不为空的（此场景目前出现在掉线后重新恢复时），
		// 那么为了防止在网络状况不好的情况下，登录能很快恢复时对方可能存在
		// 的重传，此时也能一定程序避免消息重复的可能
		if(recievedMessages != null && recievedMessages.size() > 0)
		{
			for(String key : recievedMessages.keySet())
			{
				// 重置列表中的生存起始时间
				putImpl(key);
			}
		}
		
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
	 * 向列表中加入一个包的特征指纹。
	 * <br>注意：本方法只会将指纹码推入，而不是将整个Protocal对象放入列表中。
	 * <p>
	 * <b>本方法的调用，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
	 * 
	 * @param p
	 * @see #addRecieved(String)
	 */
	public void addRecieved(Protocal p)
	{
		if(p != null && p.isQoS())
			addRecieved(p.getFp());
	}
	/**
	 * 向列表中加入一个包的特征指纹。
	 * <p>
	 * <b>本方法的调用，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
	 * 
	 * @param fingerPrintOfProtocal 消息包的特纹特征码（理论上是唯一的）
	 * @see #putImpl(String)
	 */
	public void addRecieved(String fingerPrintOfProtocal)
	{
		if(fingerPrintOfProtocal == null)
		{
			Log.w(TAG, "无效的 fingerPrintOfProtocal==null!");
			return;
		}
		
		if(recievedMessages.containsKey(fingerPrintOfProtocal))
			Log.w(TAG, "【QoS接收方】指纹为"+fingerPrintOfProtocal
					+"的消息已经存在于接收列表中，该消息重复了（原理可能是对方因未收到应答包而错误重传导致），更新收到时间戳哦.");
		
		// 无条件放入已收到列表（如果已存在则覆盖之，已在存则意味着消息重复被接收，那么就用最新的时间戳更新之）
		putImpl(fingerPrintOfProtocal);
	}
	
	private void putImpl(String fingerPrintOfProtocal)
	{
		if(fingerPrintOfProtocal != null)
			recievedMessages.put(fingerPrintOfProtocal, System.currentTimeMillis());
	}
	
	/**
	 * 指定指纹码的Protocal是否已经收到过.
	 * <p>
	 * 此方法用于QoS机制中在防止因网络丢包导致对方未收到应答时而再次发送消息从而导致消息重复时的判断依赖.
	 * 
	 * @param fingerPrintOfProtocal 消息包的特纹特征码（理论上是唯一的）
	 * @return
	 */
	public boolean hasRecieved(String fingerPrintOfProtocal)
	{
		return recievedMessages.containsKey(fingerPrintOfProtocal);
	}
	
	/**
	 * 当前“已收到消息”队列列表的大小.
	 * 
	 * @return
	 * @see ArrayList#size()
	 */
	public int size()
	{
		return recievedMessages.size();
	}
}
