/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * AutoResigninDaemon.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.core;

import com.saladjack.im.ClientCoreSDK;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.saladjack.im.conf.ConfigEntity;


/**
 * 与服务端通信中断后的自动登录（重连）独立线程。
 * <br>
 * 鉴于无线网络的不可靠性和特殊性，移动端的即时通讯经常存在网络通信断断续续的
 * 状况，可能的原因有（但不限于）：无线网络信号不稳定、WiFi与2G/3G/4G等同开情
 * 况下的网络切换、手机系统的省电策略等。这就使得即时通信框架拥有对上层透明且健
 * 壮的健康度探测和自动治愈机制非常有必要。
 * <p>
 * 本类的存在使得MobileIMSDK框架拥有通信自动治愈的能力。
 * <p>
 * <b>注意：</b>自动登录（重连）只可能发生在登录成功后与服务端的网络通信断开时。
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 */
public class AutoReSigninDaemon {
	private final static String TAG = AutoReSigninDaemon.class.getSimpleName();
	
	/** 
	 * 自动重新登录时间间隔（单位：毫秒），默认2000毫秒。
	 * <p>
	 * 此参数只会影响断线后与服务器连接的即时性，不受任何配置参数
	 * 的影响。请基于重连（重登录）即时性和手机能耗上作出权衡。
	 * <p>
	 * 除非对MobileIMSDK的整个即时通讯算法非常了解，否则请勿尝试单独设置本参数。如
	 * 需调整心跳频率请见 {@link ConfigEntity
	 * #setSenseMode(ConfigEntity.SenseMode)}。
	 */
	public static int AUTO_RE$signin_INTERVAL = 2000;

	private Handler handler = null;
	private Runnable runnable = null;
	
	/** 当前心跳线程是否正在运行中 */
	private boolean autoResigninRunning = false;
	
	private boolean _excuting = false;
	
	private static AutoReSigninDaemon instance = null;
	
	private Context context = null;
	
	public static AutoReSigninDaemon getInstance(Context context)
	{
		if(instance == null)
			instance = new AutoReSigninDaemon(context);
		return instance;
	}
	
	private AutoReSigninDaemon(Context context)
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
				if(!_excuting)
				{
					// Handler的机制是在主线程中执行的，所以此处在放在另一个线程里，否则会报错哦
					new AsyncTask<Object, Integer, Integer>(){
						@Override
						protected Integer doInBackground(Object... params)
						{
							_excuting = true;
							if(ClientCoreSDK.DEBUG)
								Log.d(TAG, "自动重新登录线程执行中, autoResignin?"+ClientCoreSDK.autoResignin+"...");
							int code = -1;
							// 是否允许自动重新登录哦
							if(ClientCoreSDK.autoResignin)
							{
								code = LocalUDPDataSender.getInstance(context).sendsignin(
										ClientCoreSDK.getInstance().getCurrentAccount()
										, ClientCoreSDK.getInstance().getCurrentsigninPsw()
										, ClientCoreSDK.getInstance().getCurrentsigninExtra());
							}
							return code;
						}

						@Override
						protected void onPostExecute(Integer result)
						{
							if(result == 0)
							{
								// *********************** 同样的代码也存在于LocalUDPDataSender.SendsigninDataAsync中的代码
								// 登录消息成功发出后就启动本地消息侦听线程：
								// 第1）种情况：首次使用程序时，登录信息发出时才启动本地监听线程是合理的；
								// 第2）种情况：因网络原因（比如服务器关闭或重启）而导致本地监听线程中断的问题：
								//      当首次登录后，因服务端或其它网络原因导致本地监听出错，将导致中断本地监听线程，
								//	          所以在此处在自动登录重连或用户自已手机尝试再次登录时重启监听线程就可以恢复本地
								//	          监听线程的运行。
								LocalUDPDataReciever.getInstance(context).startup();
							}

							//
							_excuting = false;
							// 开始下一个心跳循环
							handler.postDelayed(runnable, AUTO_RE$signin_INTERVAL);
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
		autoResigninRunning = false;
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
		handler.postDelayed(runnable, immediately ? 0 : AUTO_RE$signin_INTERVAL);
		//
		autoResigninRunning = true;
	}
	
	/**
	 * 线程是否正在运行中。
	 * 
	 * @return true表示是，否则线路处于停止状态
	 */
	public boolean isautoResigninRunning()
	{
		return autoResigninRunning;
	}
}
