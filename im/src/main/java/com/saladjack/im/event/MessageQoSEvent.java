/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * MessageQoSEvent.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.event;

import java.util.ArrayList;

import net.openmob.mobileimsdk.server.protocal.Protocal;

/**
 * MobileIMSDK的QoS质量保证机制的回调事件接口.
 * <p>
 * 当前MobileIMSDK的QoS机制支持全部的C2C、C2S、S2C共3种消息交互场景下的
 * 消息送达质量保证.
 * <p>
 * MobileIMSDK QoS机制的目标是：尽全力送达消息，即使无法送达也会通过回调
 * 告之应用层，尽最大可能避免因UDP协议天生的不可靠性而发生消息黑洞情况（
 * 何为黑洞消息？即消息发出后发送方完全不知道到底送达了还是没有送达，而
 * MobileIMSDK的QoS机制将会即时准确地告之发送方：“已送达”或者“没有送达”，
 * 没有第3种可能）。
 * 
 */
public interface MessageQoSEvent
{
	/**
	 * 消息未送达的回调事件通知.
	 * 
	 * @param lostMessages 由MobileIMSDK QoS算法判定出来的未送达消息列表（此列表
	 * 中的Protocal对象是原对象的clone（即原对象的深拷贝），请放心使用哦），应用层
	 * 可通过指纹特征码找到原消息并可以UI上将其标记为”发送失败“以便即时告之用户
	 */
	void messagesLost(ArrayList<Protocal> lostMessages);

	/**
	 * 消息已被对方收到的回调事件通知.
	 * <p>
	 * <b>目前，判定消息被对方收到是有两种可能：</b><br>
	 * 1) 对方确实是在线并且实时收到了；<br>
	 * 2) 对方不在线或者服务端转发过程中出错了，由服务端进行离线存储成功后的反馈
	 * （此种情况严格来讲不能算是“已被收到”，但对于应用层来说，离线存储了的消息
	 * 原则上就是已送达了的消息：因为用户下次登录时肯定能通过HTTP协议取到）。
	 * 
	 * @param theFingerPrint 已被收到的消息的指纹特征码（唯一ID），应用层可据此ID
	 * 来找到原先已发生的消息并可在UI是将其标记为”已送达“或”已读“以便提升用户体验
	 */
	void messagesBeReceived(String theFingerPrint);
}
