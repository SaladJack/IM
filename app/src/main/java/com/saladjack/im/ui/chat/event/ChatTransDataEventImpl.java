package com.saladjack.im.ui.chat.event;


import com.saladjack.im.event.ChatTransDataEvent;
import com.saladjack.im.ui.chat.ContentType;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ChatTransDataEventImpl implements ChatTransDataEvent {
	private void sendOrderedBroadCast(Bundle bundle, String action){
		Intent intent = new Intent(action);
		intent.putExtra("bundle",bundle);
		context.sendOrderedBroadcast(intent,null);
	}
	private final static String TAG = ChatTransDataEventImpl.class.getSimpleName();
	private final Context context;
	public ChatTransDataEventImpl(Context context) {
		this.context = context;
	}

	@Override public void onTransBuffer(String fingerPrintOfProtocal, int dwUserid, String dataContent) {
		Log.d(TAG, "【DEBUG_UI】收到来自用户"+dwUserid+"的消息:"+dataContent);
		Bundle bundle = new Bundle();
		bundle.putInt("contentType", ContentType.RESPONSE);
		bundle.putString("content",dataContent);
		bundle.putInt("friendId",dwUserid);
		sendOrderedBroadCast(bundle,"chat");
	}
	

	@Override public void onErrorResponse(int errorCode, String errorMsg) {
		Log.d(TAG, "【DEBUG_UI】收到服务端错误消息，errorCode="+errorCode+", errorMsg="+errorMsg);
//		this.chatView.onDisconnect("Server反馈错误码："+errorCode+",errorMsg="+errorMsg);
		Bundle bundle = new Bundle();
		bundle.putInt("contentType", ContentType.DISCONNECT);
		bundle.putString("content",errorMsg);
		bundle.putInt("errorCode",errorCode);
		sendOrderedBroadCast(bundle,"chat");
	}
}
