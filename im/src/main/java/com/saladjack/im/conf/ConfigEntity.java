package com.saladjack.im.conf;

import com.saladjack.im.core.KeepAliveDaemon;

/**
 * Created by SaladJack on 2017/1/10.
 */
public class ConfigEntity {

	public static String appKey = null;
	

	public static String serverIP = "123";
	

	public static int serverUDPPort = 7901;
	

	public static int localUDPPort = 0;//7801;

    public static void setSenseMode(SenseMode mode) {
    	int keepAliveInterval = 0;
    	int networkConnectionTimeout = 0;
    	switch(mode) {
    		case MODE_3S:
    			// 心跳间隔3秒
    			keepAliveInterval = 3000;// 3s
    			// 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续3 个心跳间隔后仍未收到服务端反馈）
    			networkConnectionTimeout = 3000 * 3 + 1000;// 10s
    			break;
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
    	
    	if(keepAliveInterval > 0) {
    		KeepAliveDaemon.KEEP_ALIVE_INTERVAL = keepAliveInterval;
    	}
    	if(networkConnectionTimeout > 0) {
    		KeepAliveDaemon.NETWORK_CONNECTION_TIME_OUT = networkConnectionTimeout;
    	}

    }

    public enum SenseMode {
    	/** 
    	 * KeepAlive心跳问隔为3秒；
    	 * 10秒后未收到服务端心跳反馈即认为连接已断开（相当于连续3 个心跳间隔后仍未收到服务端反馈）。
    	 */
    	MODE_3S,
    	
    	/**
    	 * KeepAlive心跳问隔为10秒；
    	 * 21秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
    	 */
    	MODE_10S,
    	
    	/**
    	 * KeepAlive心跳问隔为30秒；
    	 * 61秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
    	 */
    	MODE_30S,
    	
    	/** 
    	 * * KeepAlive心跳问隔为60秒；
    	 * * 121秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
    	 */
    	MODE_60S,
    	
    	/** 
    	 * KeepAlive心跳问隔为120秒；
    	 * 241秒后未收到服务端心跳反馈即认为连接已断开（相当于连续2 个心跳间隔后仍未收到服务端反馈）。
    	 */
    	MODE_120S
    }
}
