/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ChatTransDataEvent.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.event;

import com.saladjack.im.ClientCoreSDK;

/**
 * MobileIMSDK的通用数据通信消息的回调事件接口（如：收到聊天数据事件
 * 通知、服务端返回的错误信息事件通知等）。
 * <br>
 * 实现此接口后，通过 {@link ClientCoreSDK
 * #setChatTransDataEvent(ChatTransDataEvent)}
 * 方法设置之，可实现回调事件的通知和处理。
 * 
 */
public interface ChatTransDataEvent
{
	/**
	 * 收到普通消息的回调事件通知。
	 * <br>应用层可以将此消息进一步按自已的IM协议进行定义，
	 * 从而实现完整的即时通信软件逻辑。
	 * 
	 * @param fingerPrintOfProtocal 当该消息需要QoS支持时本回调参数为该消息的特征指纹码，否则为null
	 * @param dwUserid 消息的发送者id（MobileIMSDK框架中规定发送者id=0即表示是由服务端主动发过的，否则
	 * 表示的是其它客户端发过来的消息）
	 * @param dataContent 消息内容的文本表示形式
	 */
	public void onTransBuffer(String fingerPrintOfProtocal, int dwUserid, String dataContent);
	
	/**
	 * 服务端反馈的出错信息回调事件通知。
	 * 
	 * @param errorCode 错误码，定义在常量表{@link ErrorCode.ForS}中
	 * @param errorMsg 描述错误内容的文本信息
	 */
	public void onErrorResponse(int errorCode, String errorMsg);
}