/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ChatTransDataEventImpl.java at 2016-2-20 11:20:18, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.ui.chat.event;


import com.saladjack.im.event.ChatTransDataEvent;
import com.saladjack.im.ui.chat.ChatView;
import com.saladjack.im.ui.chat.ContentType;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ChatTransDataEventImpl implements ChatTransDataEvent {
	private void sendBroadCast(Bundle bundle, String action){
		Intent intent = new Intent(action);
		intent.putExtra("bundle",bundle);
		context.sendBroadcast(intent);
	}
	private final static String TAG = ChatTransDataEventImpl.class.getSimpleName();
	private final Context context;
	public ChatTransDataEventImpl(Context context) {
		this.context = context;
	}

	@Override public void onTransBuffer(String fingerPrintOfProtocal, int dwUserid, String dataContent) {
		Log.d(TAG, "【DEBUG_UI】收到来自用户"+dwUserid+"的消息:"+dataContent);
		
		//！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
//		if(chatView != null) {
//			Toast.makeText(chatView, dwUserid+"说："+dataContent, Toast.LENGTH_SHORT).show();
//			this.chatView.showResponseMessage(dwUserid+"说："+dataContent);
//		}
		Bundle bundle = new Bundle();
		bundle.putInt("contentType", ContentType.RESPONSE);
		bundle.putString("content",dataContent);
		bundle.putInt("friendId",dwUserid);
		sendBroadCast(bundle,"chat");
	}
	

	@Override public void onErrorResponse(int errorCode, String errorMsg) {
		Log.d(TAG, "【DEBUG_UI】收到服务端错误消息，errorCode="+errorCode+", errorMsg="+errorMsg);
//		this.chatView.onDisconnect("Server反馈错误码："+errorCode+",errorMsg="+errorMsg);
		Bundle bundle = new Bundle();
		bundle.putInt("contentType", ContentType.DISCONNECT);
		bundle.putString("content",errorMsg);
		bundle.putInt("errorCode",errorCode);
		sendBroadCast(bundle,"chat");
	}
}
