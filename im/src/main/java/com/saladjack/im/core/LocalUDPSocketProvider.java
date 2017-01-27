/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * LocalUDPSocketProvider.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.core;

import java.net.DatagramSocket;

import com.saladjack.im.ClientCoreSDK;
import com.saladjack.im.conf.ConfigEntity;

import android.util.Log;


/**
 * 本地 UDP Socket 实例封装实用类。
 * <br>
 * 本类提供存取本地UDP Socket通信对象引用的方便方法，封装了
 * Socket有效性判断以及异常处理等，以便确保调用者通过方法 {@link #getLocalUDPSocket()}
 * 拿到的Socket对象是健康有效的。
 * <p>
 * 依据作者对MobileIMSDK API的设计理念，本类将以单例的形式提供给调用者使用。
 *
 */
public class LocalUDPSocketProvider
{
	private final static String TAG = LocalUDPSocketProvider.class.getSimpleName();
	
	/** 本地UDP Socket实例 */
	private DatagramSocket localUDPSocket = null;
	
	private static LocalUDPSocketProvider instance= null;
	
	public static LocalUDPSocketProvider getInstance()
	{
		if(instance == null)
			instance = new LocalUDPSocketProvider();
		return instance;
	}
	
	private LocalUDPSocketProvider()
	{
		//
	}
	
	/**
	 * 重置并新建一个全新的Socket对象。
	 * 
	 * @return 新建的全新Socket对象引用
	 * @see DatagramSocket
	 * @see ConfigEntity#localUDPPort
	 */
	private DatagramSocket resetLocalUDPSocket()
	{
		try
		{
			//
			closeLocalUDPSocket();
			if(ClientCoreSDK.DEBUG)
				Log.d(TAG, "new DatagramSocket()中...");
			localUDPSocket = (ConfigEntity.localUDPPort == 0?
					new DatagramSocket():new DatagramSocket(ConfigEntity.localUDPPort));//_Utils.LOCAL_UDP_SEND$LISTENING_PORT);
			localUDPSocket.setReuseAddress(true);
			if(ClientCoreSDK.DEBUG)
				Log.d(TAG, "new DatagramSocket()已成功完成.");
			
//			// 设置本地消息监听（并启动监听处理线程）
////			localUDPDataReciever.setLocalUDPSocket(localUDPSocket);
//			localUDPDataReciever.startup();
			
			return localUDPSocket;
		}
		catch (Exception e)
		{
			Log.w(TAG, "localUDPSocket创建时出错，原因是："+e.getMessage(), e);
			//
			closeLocalUDPSocket();
			return null;
		}
	}
	
	/**
	 * 本类中的Socket对象是否是健康的。
	 * 
	 * @return true表示是健康的，否则不是
	 */
	private boolean isLocalUDPSocketReady()
	{
		return localUDPSocket != null && !localUDPSocket.isClosed();
	}
	
	/**
	 * 获得本地UDPSocket的实例引用.
	 * <p>
	 * 本方法内封装了Socket有效性判断以及异常处理等，以便确保调用者通过本方法 
	 * 拿到的Socket对象是健康有效的。
	 * 
	 * @return 如果该实例正常则返回它的引用，否则返回null
	 * @see #isLocalUDPSocketReady()
	 * @see #resetLocalUDPSocket()
	 */
	public DatagramSocket getLocalUDPSocket()
	{
		if(isLocalUDPSocketReady())
		{
			if(ClientCoreSDK.DEBUG)
				Log.d(TAG, "isLocalUDPSocketReady()==true，直接返回本地socket引用哦。");
			return localUDPSocket;
		}
		else
		{
			if(ClientCoreSDK.DEBUG)
				Log.d(TAG, "isLocalUDPSocketReady()==false，需要先resetLocalUDPSocket()...");
			return resetLocalUDPSocket();
		}
	}
	
	/**
	 * 强制关闭本地UDP Socket侦听。
	 * <br>
	 * 一旦调用本方法后，再次调用 
	 * {@link #getLocalUDPSocket()}将会返回一个全新的Socket对象引用。
	 * <p>
	 * <b>本方法通常在两个场景下被调用：</b><br>
	 * 1) 真正需要关闭Socket时（如所在的APP通出时）；<br>
	 * 2) 当调用者检测到网络发生变动后希望重置以便获得健康的Socket引用对象时。
	 * 
	 * @see java.net.DatagramSocket#close()
	 */
	public void closeLocalUDPSocket()
	{
		try
		{
			if(ClientCoreSDK.DEBUG)
				Log.d(TAG, "正在closeLocalUDPSocket()...");
			if(localUDPSocket != null)
			{
				localUDPSocket.close();
				localUDPSocket = null;
			}
			else
			{
				Log.d(TAG, "Socket处于未初化状态（可能是您还未登陆），无需关闭。");
			}	
		}
		catch (Exception e)
		{
			Log.w(TAG, "lcloseLocalUDPSocket时出错，原因是："+e.getMessage(), e);
		}
	}
}
