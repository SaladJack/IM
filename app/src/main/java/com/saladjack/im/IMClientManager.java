
package com.saladjack.im;

import com.saladjack.im.conf.ConfigEntity;
import com.saladjack.im.ui.chat.event.ChatBaseEventImpl;
import com.saladjack.im.ui.chat.event.ChatTransDataEventImpl;
import com.saladjack.im.ui.chat.event.MessageQoSEventImpl;

import android.content.Context;

public class IMClientManager {
	private static String TAG = IMClientManager.class.getSimpleName();
	
	private static IMClientManager instance = null;
	
	/** IMCORE是否已被初始化. true表示已初化完成，否则未初始化. */
	private boolean init = false;
	
	// 
	private ChatBaseEventImpl baseEventListener = null;
	//
	private ChatTransDataEventImpl transDataListener = null;
	//
	private MessageQoSEventImpl messageQoSListener = null;
	
	private Context context = null;

	public static IMClientManager getInstance(Context context) {
		if(instance == null)
			instance = new IMClientManager(context);
		return instance;
	}
	
	private IMClientManager(Context context) {
		this.context = context;
		initIM();
	}

	public void initIM()
	{
		if(!init)
		{
			// 设置AppKey
			ConfigEntity.appKey = "5418023dfd98c579b6001741";
	    
			// 开启/关闭DEBUG信息输出
//	    	IMCore.DEBUG = false;
			

			IMCore.getInstance().init(this.context);
	    
			// 设置事件回调
			baseEventListener = new ChatBaseEventImpl(context);
			transDataListener = new ChatTransDataEventImpl(context);
			messageQoSListener = new MessageQoSEventImpl(context);
			IMCore.getInstance().setChatBaseEvent(baseEventListener);
			IMCore.getInstance().setChatTransDataEvent(transDataListener);
			IMCore.getInstance().setMessageQoSEvent(messageQoSListener);
			
			init = true;
		}
	}

	public void release() {
		IMCore.getInstance().release();
	}

	public ChatTransDataEventImpl getTransDataListener()
	{
		return transDataListener;
	}
	public ChatBaseEventImpl getBaseEventListener()
	{
		return baseEventListener;
	}
	public MessageQoSEventImpl getMessageQoSListener()
	{
		return messageQoSListener;
	}
}
