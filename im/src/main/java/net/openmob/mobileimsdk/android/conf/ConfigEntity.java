/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ConfigEntity.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.conf;

import net.openmob.mobileimsdk.android.core.KeepAliveDaemon;

/**
 * MobileIMSDK的全局参数控制类。
 * 
 * @author Jack Jiang, 2015-08-17
 * @version 1.0
 * @since 2.0
 */
public class ConfigEntity
{
	/**
	 * 全局设置：AppKey。
	 */
	public static String appKey = null;
	
	/**
	 * 全局设置：服务端IP或域名。
	 * 
	 * <p>
	 * 如需设置本参数，请在登陆前调用，否则将不起效。
	 */
	public static String serverIP = "rbcore.openmob.net";
	
	/**
	 * 全局设置：服务端UDP服务侦听端口号。
	 * 
	 * <p>
	 * 如需设置本参数，请在登陆前调用，否则将不起效。
	 */
	public static int serverUDPPort = 7901;
	
	/**
	 * 全局设置：本地UDP数据发送和侦听端口。默认是0。
	 * 
	 * <p>
	 * 如需设置本参数，请在登陆前调用，否则将不起效。
	 * 
	 * <p>
	 * 本参数为0时表示由系统自动分配端口（这意味着同时开启两个及以上本SDK
	 * 的实例也不会出现端口占用冲突），否则使用指定端口。
	 * <br>
	 * 在什么场景下需要使用固定端口号呢？通常用于debug时，比如观察3G网络下的运营商外网端口分配情况。
	 */
	public static int localUDPPort = 0;//7801;
	
	/**
     * 设置MobileIMSDK即时通讯核心框架预设的敏感度模式。
     * 
     * <p>
     * 请在登陆前调用，否则将不起效.
     * 
     * <p>
     * <b>重要说明：</b><u>客户端本模式的设定必须要与服务端的模式设制保持一致</u>，否则
     * 可能因参数的不一致而导致IM算法的不匹配，进而出现不可预知的问题。
     * 
     * @param mode 
	 * @see SenseMode
     */
    public static void setSenseMode(SenseMode mode)
    {
    	int keepAliveInterval = 0;
    	int networkConnectionTimeout = 0;
    	switch(mode)
    	{
    		case MODE_3S:
    		{
    			// 心跳间隔3秒
    			keepAliveInterval = 3000;// 3s
    			// 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续3 个心跳间隔后仍未收到服务端反馈）
    			networkConnectionTimeout = 3000 * 3 + 1000;// 10s
    			break;
    		}
    		case MODE_10S:
    			// 心跳间隔10秒
    			keepAliveInterval = 10000;// 10s
    			// 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）
    			networkConnectionTimeout = 10000 * 2 + 1000;// 21s
        		break;
    		case MODE_30S:
    			// 心跳间隔30秒
    			keepAliveInterval = 30000;// 30s
    			// 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）
    			networkConnectionTimeout = 30000 * 2 + 1000;// 61s
        		break;
    		case MODE_60S:
    			// 心跳间隔60秒
    			keepAliveInterval = 60000;// 60s
    			// 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）
    			networkConnectionTimeout = 60000 * 2 + 1000;// 121s
        		break;
    		case MODE_120S:
    			// 心跳间隔120秒
    			keepAliveInterval = 120000;// 120s
    			// 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）
    			networkConnectionTimeout = 120000 * 2 + 1000;// 241s
        		break;
    	}
    	
    	if(keepAliveInterval > 0)
    	{
    		// 设置Kepp alive心跳间隔
    		KeepAliveDaemon.KEEP_ALIVE_INTERVAL = keepAliveInterval;
    	}
    	if(networkConnectionTimeout > 0)
    	{
    		// 设置与服务端掉线的超时时长
    		KeepAliveDaemon.NETWORK_CONNECTION_TIME_OUT = networkConnectionTimeout;
    	}
//	    // 与服务端掉线后的重连尝试间隔
//	    AutoReLoginDaemon.AUTO_RE$LOGIN_INTERVAL = 2 * 1000;// 5s
    }
	
	/**
     * MobileIMSDK即时通讯核心框架预设的敏感度模式.
     * 
     * <p>
     * 对于客户端而言，此模式决定了用户与服务端网络会话的健康模式，原则上超敏感客户端的体验越好。
     * 
     * <p>
     * <b>重要说明：</b><u>客户端本模式的设定必须要与服务端的模式设制保持一致</u>，否则
     * 可能因参数的不一致而导致IM算法的不匹配，进而出现不可预知的问题。
     * 
     * @author Jack Jiang, 2015-09-07
     * @version 2.1
     */
    public enum SenseMode
    {
    	/** 
    	 * 此模式下：<br>
    	 * * KeepAlive心跳问隔为3秒；<br>
    	 * * 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续3 个心跳间隔后仍未收到服务端反馈）。
    	 */
    	MODE_3S,
    	
    	/** 
    	 * 此模式下：<br>
    	 * * KeepAlive心跳问隔为10秒；<br>
    	 * * 21秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
    	 */
    	MODE_10S,
    	
    	/** 
    	 * 此模式下：<br>
    	 * * KeepAlive心跳问隔为30秒；<br>
    	 * * 61秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
    	 */
    	MODE_30S,
    	
    	/** 
    	 * 此模式下：<br>
    	 * * KeepAlive心跳问隔为60秒；<br>
    	 * * 121秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
    	 */
    	MODE_60S,
    	
    	/** 
    	 * 此模式下：<br>
    	 * * KeepAlive心跳问隔为120秒；<br>
    	 * * 241秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
    	 */
    	MODE_120S
    }
}
