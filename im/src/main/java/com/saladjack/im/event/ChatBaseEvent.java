/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ChatBaseEvent.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.event;

import com.saladjack.im.ClientCoreSDK;

/**
 * MobileIMSDK的基础通信消息的回调事件接口（如：登录成功事件
 * 通知、掉线事件通知等）。
 * <br>
 * 实现此接口后，通过 {@link ClientCoreSDK
 * #setChatBaseEvent(ChatBaseEvent)}
 * 方法设置之，可实现回调事件的通知和处理。
 * 
 */
public interface ChatBaseEvent
{
	/**
	 * 本地用户的登录结果回调事件通知。
	 * 
	 * @param dwUserId 当回调参数dwErrorCode=0时，本回调参数值表示登录成功后服务
	 * 端分配的用户id，否则本回调参数值无意义
	 * @param dwErrorCode 服务端反馈的登录结果：0 表示登录成功，否则为服务端自定义
	 * 的出错代码（按照约定通常为>=1025的数）
	 */
    public void onLoginMessage(int dwUserId, int dwErrorCode,String username);
    
	/**
	 * 与服务端的通信断开的回调事件通知。
	 * <br>
	 * 该消息只有在客户端连接服务器成功之后网络异常中断之时触发。
	 * 导致与服务端的通信断开的原因有（但不限于）：无线网络信号不稳定、WiFi与2G/3G/4G等同开情
	 * 况下的网络切换、手机系统的省电策略等。
	 * 
	 * @param dwErrorCode 本回调参数表示表示连接断开的原因，目前错误码没有太多意义，仅作保留字段，目前通常为-1
	 */
    public void onLinkCloseMessage(int dwErrorCode);	
	
}
