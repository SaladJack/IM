/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * MessageQoSEventImpl.java at 2016-2-20 11:20:18, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.ui.chat.event;

import java.util.ArrayList;


import com.saladjack.im.event.MessageQoSEvent;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import com.saladjack.im.ui.chat.ChatView;
import com.saladjack.im.ui.chat.ContentType;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MessageQoSEventImpl implements MessageQoSEvent {
	private void sendBroadCast(Bundle bundle, String action){
		Intent intent = new Intent(action);
		intent.putExtra("bundle",bundle);
		context.sendBroadcast(intent);
	}
	private final static String TAG = MessageQoSEventImpl.class.getSimpleName();
	private final Context context;

	private ChatView chatView = null;

	public MessageQoSEventImpl(Context context) {
		this.context = context;
	}

	@Override
	public void messagesLost(ArrayList<Protocal> lostMessages)
	{
		Log.d(TAG, "【DEBUG_UI】收到系统的未实时送达事件通知，当前共有"+lostMessages.size()+"个包QoS保证机制结束，判定为【无法实时送达】！");
	
//		if(this.chatView != null) {
//			this.chatView.showSendMessageFail("[消息未成功送达]共"+lostMessages.size()+"条!(网络状况不佳或对方id不存在)");
//		}
//		Bundle bundle = new Bundle();
//		bundle.putInt("contentType", ContentType.);
//		bundle.putString("content",errorMsg);
//		bundle.putInt("errorCode",errorCode);
//		sendBroadCast(bundle,"chat");
	}

	@Override
	public void messagesBeReceived(String theFingerPrint)
	{
		if(theFingerPrint != null)
		{
			Log.d(TAG, "【DEBUG_UI】收到对方已收到消息事件的通知，fp="+theFingerPrint);
//			if(this.chatView != null) {
//				this.chatView.showIMInfo_blue("[收到对方消息应答]fp="+theFingerPrint);
//			}
		}
	}
	
	public MessageQoSEventImpl setChatView(ChatView chatView)
	{
		this.chatView = chatView;
		return this;
	}
}
