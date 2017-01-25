/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * UDPUtils.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.util.Log;

/**
 * 一个本地UDP消息发送工具类。
 * 
 */
public class UDPUtils
{
	private final static String TAG = UDPUtils.class.getSimpleName();
	
	/**
	 * 发送一条UDP消息。
	 * <p>
	 * 本方法中以对可能发生的异常进行了处理，调用者只会知道有否成功而不会被异常打断。
	 * 
	 * @param skt DatagramSocket对象引用
	 * @param d 要发送的比特数组
	 * @param dataLen 比特数组长长矛
	 * @return true表示成功发出，否则表示发送失败
	 * @see #send(DatagramSocket, DatagramPacket)
	 */
	public static boolean send(DatagramSocket skt, byte[] d, int dataLen)
	{
		if(skt != null && d != null)
		{
			try
			{
				return send(skt, new DatagramPacket(d, dataLen));
			}
			catch (Exception e)
			{
				Log.e(TAG, "send方法中》》发送UDP数据报文时出错了：remoteIp="+skt.getInetAddress()
						+", remotePort="+skt.getPort()+".原因是："+e.getMessage(), e);
				return false;
			}
		}
		else
		{
			Log.e(TAG, "send方法中》》无效的参数：skt="+skt);//
			// 解决google统计报告的bug: NullPointerException (@UDPUtils:send:30) {AsyncTask #4}
//					+", d="+d+", remoteIp="+skt.getInetAddress()+", remotePort="+skt.getPort());
			return false;
		}
	}
	
	/**
	 * 发送一条UDP消息。
	 * <p>
	 * 本方法中以对可能发生的异常进行了处理，调用者只会知道有否成功而不会被异常打断。
	 * 
	 * @param skt DatagramSocket对象引用
	 * @param p 要发送的UDP数据报
	 * @return true表示成功发出，否则表示发送失败
	 */
	public synchronized static boolean send(DatagramSocket skt, DatagramPacket p)
	{
		boolean sendSucess = true;
		if(skt != null && p != null)
		{
//			Log.d(TAG, "正在send()UDP数据报中，[d.len="+p.getData().length+",remoteIp="
//					+skt.getInetAddress()+",remotePort="+skt.getPort()+"]，本地端口是："+skt.getLocalPort()+" ...");
			if(skt.isConnected())
			{
				try
				{
//					Log.d(TAG, "<<<< --------------- >>>> isClosed?"+skt.isClosed()
//							+", isConnected?"+skt.isConnected()+", isBound?"+skt.isBound());
					skt.send(p);
//					Log.d(TAG, "本次UDP数据报文发送成功！");
				}
				catch (Exception e)
				{
					sendSucess = false;
					Log.e(TAG, "send方法中》》发送UDP数据报文时出错了，原因是："+e.getMessage(), e);
				}
			}
		}
		else
		{
			Log.w(TAG, "在send()UDP数据报时没有成功执行，原因是：skt==null || p == null!");
		}
			
		return sendSucess;
	}
}
