/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ClientCoreSDK.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im;

import com.saladjack.im.core.AutoReLoginDaemon;
import com.saladjack.im.core.KeepAliveDaemon;
import com.saladjack.im.core.LocalUDPDataReciever;
import com.saladjack.im.core.LocalUDPSocketProvider;
import com.saladjack.im.core.QoS4ReciveDaemon;
import com.saladjack.im.core.QoS4SendDaemon;
import com.saladjack.im.event.ChatBaseEvent;
import com.saladjack.im.event.ChatTransDataEvent;
import com.saladjack.im.event.MessageQoSEvent;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * MobileIMSDK框架的核心入口类。
 * <br>
 * 本类主要提供一些全局参数的读取和设置。
 * 
 */
public class ClientCoreSDK
{
	private final static String TAG = ClientCoreSDK.class.getSimpleName();
	
	/** true表示开启MobileIMSDK Debug信息在Logcat下的输出，否则关闭。默认为true. */
	public static boolean DEBUG = true;
	
	/** 
	 * 是否在登陆成功后掉线时自动重新登陆线程中实质性发起登陆请求，true表示将在线程
	 * 运行周期中正常发起，否则不发起（即关闭实质性的重新登陆请求）。
	 * <p>
	 * 什么样的场景下，需要设置本参数为false？比如：上层应用可以在自已的节电逻辑中控
	 * 制当网络长时断开时就不需要实质性发起登陆请求了，因为 网络请求是非常耗电的。
	 * <p>
	 * <b>本参数的设置将实时生效。</b> 
	 */
	public static boolean autoReLogin = true;
	
	private static ClientCoreSDK instance = null;
	
	/** 
	 * 当调用 {@link #init(Context)}方法后本字段将被置为true，调用{@link #release()}
	 * 时将被重新置为false.
	 * <br>
	 * <b>本参数由框架自动设置。</b>
	 */
	private boolean _init = false;
	/** 
	 * 网络是否可用, true表示可用，否则表示不可用.
	 * <p>
	 * 本字段在将在网络事件通知处理中被设置.
	 * <p>
	 * 注意：本类中的网络状态变更事件，尤其在网络由断变好之后，受Android系统
	 * 广播机制的影响事件收到延迟在1~2秒，目前没有找到其它更优的替代方案，但
	 * 从算法逻辑上讲不影响本核心类库的工作（仅影响核心库算法的构造难易度而已）！
	 */
	private boolean localDeviceNetworkOk = true;
	
	/**
	 * 是否已成功连接到服务器（当然，前提是已成功发起过登陆请求后）.
	 * <p>
	 * 此“成功”意味着可以正常与服务端通信（可以近似理解为Socket正常建立）
	 * ，“不成功”意味着不能与服务端通信.
	 * <br>
	 * 不成功的因素有很多：比如网络不可用、网络状况很差导致的掉线、心跳超时等.
	 * <p>
	 * <b>本参数是整个MobileIMSDK框架中唯一可作为判断与MobileIMSDK服务器的通信是否正常的准确依据。</b>
	 * <p>
	 * 本参数将在收到服务端的登陆请求反馈后被设置为true，在与服务端的通信无法正常完成时被设置为false。
	 * <br>
	 * <u>那么MobileIMSDK如何判断与服务端的通信是否正常呢？</u> 判断方法如下：
	 * <ul>
	 * <li>登陆请求被正常反馈即意味着通信正常（包括首次登陆时和断掉后的自动重新时）；</li>
	 * <li>首次登陆或断线后自动重连时登陆请求被发出后，没有收到服务端反馈时即意味着不正常；</li>
	 * <li>与服务端通信正常后，在规定的超时时间内没有收到心跳包的反馈后即意味着与服务端的通信又中断了（即所谓的掉线）。</li>
	 * </ul>
	 * 
	 * <br>
	 * <b>本参数由框架自动设置。</b>
	 */
	private boolean connectedToServer = true;
	
	/** 
	 * 当且仅当用户从登陆界面成功登陆后设置本字段为true，系统退出
	 * （登陆）时设置为false。
	 * <br>
	 * <b>本参数由框架自动设置。</b>
	 */
	private boolean loginHasInit = false;
	
	/** 
	 * 本字段存放的是用户成功登陆后，服务端分配的id号。
	 * <br>
	 * 本字段只在 {@link #connectedToServer}==true 的情况下才有意义哦.
	 * <br>
	 * <b>本参数由框架自动设置。</b>
	 */
	private int currentUserId = -1;
	
	/**
	 * 本字段在登陆信息成功发出后就会被设置，将在掉线后自动重连时使用。
	 * <br>
	 * 因不保证服务端正确收到和处理了该用户的登陆信息，所以本字段因只在
	 * {@link #connectedToServer}==true 时才有意义.
	 * <br>
	 * <b>本参数由框架自动设置。</b>
	 */
	private String currentLoginName = null;
	/**
	 * 本字段在登陆信息成功发出后就会被设置，将在掉线后自动重连时使用。
	 * <br>
	 * 因不保证服务端正确收到和处理了该用户的登陆信息，所以本字段应只在
	 * {@link #connectedToServer}==true 时才有意义.
	 * <br>
	 * <b>本参数由框架自动设置。</b>
	 */
	private String currentLoginPsw = null;
	/**
	 * 本字段在登陆信息成功发出后就会被设置，将在掉线后自动重连时使用。
	 * <br>
	 * 因不保证服务端正确收到和处理了该用户的登陆信息，所以本字段应只在
	 * {@link #connectedToServer}==true 时才有意义.
	 * <br>
	 * <b>本参数由框架自动设置。</b>
	 */
	private String currentLoginExtra = null;
	
	/** 框架基础通信消息的回调事件（如：登陆成功事件通知、掉线事件通知） */
	private ChatBaseEvent chatBaseEvent = null;
	/** 通用数据通信消息的回调事件（如：收到聊天数据事件通知、服务端返回的错误信息事件通知） */
	private ChatTransDataEvent chatTransDataEvent = null;
	/** QoS质量保证机制的回调事件（如：消息未成功发送的通知、消息已被对方成功收到的通知） */
	private MessageQoSEvent messageQoSEvent = null;
	
	//
	private Context context = null;
	
	/**
	 * 取得本类实例的唯一公开方法。
	 * <p>
	 * 依据作者对MobileIMSDK API的设计理念，本类目前在整个框架运行中
	 * 是以单例的形式存活。
	 * 
	 * @return
	 */
	public static ClientCoreSDK getInstance()
	{
		if(instance == null)
			instance = new ClientCoreSDK();
		return instance;
	}
	
	private ClientCoreSDK()
	{
//		init();
	}
	
	/**
	 * 初始化核心库.
	 * <br>
	 * <b>注意：</b>不同于MobileIMSDK的iOS和Java客户端，本方法需要
	 * 由开发者调用，以确保MobileIMSDK核心已被初始化完成。
	 * <p>
	 * 本方法被调用后， {@link #isInitialed()}将返回true，否则返回false。
	 * <p>
	 * <b><font color="red">注意：</font></b>因Andriod系统在处理网络变动广播
	 * 事件的特殊性，本方法应在MobileIMSDK的登陆信息被发出前已被开发者调用完成。
	 * 且越早被调用越好（如放在Application的onCreate()方法中或者登陆Activity的
	 * onCreate()方法中）。具体原因详见：LocalUDPDataSender.SendLoginDataAsync
	 * 中由Jack Jiang编写的技术要点备忘录。
	 * 
	 * @see {@link com.cngeeker.MobileIMSDK.java.core.LocalUDPDataSender.SendLoginDataAsync}
	 */
	public void init(Context _context)
	{
		if(!_init)
		{
			if(_context == null)
				throw new IllegalArgumentException("context can't be null!");
			
			// 将全局Application作为context上下文句柄：
			//   由于Android程序的特殊性，整个APP的生命周中除了Application外，其它包括Activity在内
			//   都可能是短命且不可靠的（随时可能会因虚拟机资源不足而被回收），所以MobileIMSDK作为跟
			//   整个APP的生命周期保持一致的全局资源，它的上下文用Application是最为恰当的。
			if(_context instanceof Application)
				this.context = _context;
			else
			{
				//### Bug FIX: 2015-11-07 by Jack Jiang
				// ** 此处一定要取到此APP的Application作为MobileIMSDK的上下文引用
				// 之前不恰当的作法如：开发者在登陆界面首次调用登陆接口（此接口将自动调用本类的init方法从而为
				// 整个MobileIMSDK初始化），使用的上下文是该登陆Activity的引用，则将会错误地导致init方法中的
				// "CONNECTIVITY_ACTION"网络状态广播注册在此登陆Activity上。而接着登陆成功后此登陆Activity
				// 随之被finish掉了，导致了MobileIMSDK所需要的"CONNECTIVITY_ACTION"网络状态广播无法
				// 收听和处理，从而导致当手机的网络断开后，再次恢复时MobileIMSDK会因错过此广播而失去重置网络
				// Socket的能力，从而导致的结局是永远无法实现自动重连的实现！
				this.context = _context.getApplicationContext();
			}
		
			// Register for broadcasts when network status changed
			IntentFilter intentFilter = new IntentFilter(); 
			intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
			this.context.registerReceiver(networkConnectionStatusBroadcastReceiver, intentFilter);
		
			//** 启动不应放在这儿哦
//			// 启动QoS机制之发送列表重视机制
//			ProtocalQoS4SendProvider.getInstance(context).startup(true);
//			// 启动QoS机制之接收列表防重复机制
//			ProtocalQoS4ReciveProvider.getInstance(context).startup(true);
				
			//
			_init = true;
		}
	}
	
	/**
	 * 释放MobileIMSDK框架资源统一方法。
	 * <p>
	 * 本方法建议在退出登陆（或退出APP时）时调用。调用时将尝试关闭所有
	 * MobileIMSDK框架的后台守护线程并同设置核心框架init=false、
	 * {@link #loginHasInit}=false、{@link #connectedToServer}=false。
	 * 
	 * @see AutoReLoginDaemon#stop()
	 * @see QoS4SendDaemon#stop()
	 * @see KeepAliveDaemon#stop()
	 * @see LocalUDPDataReciever#stop()
	 * @see QoS4ReciveDaemon#stop()
	 * @see LocalUDPSocketProvider#closeLocalUDPSocket()
	 */
	public void release()
	{
		// 尝试停掉掉线重连线程（如果线程正在运行的话）
	    AutoReLoginDaemon.getInstance(context).stop(); // 2014-11-08 add by Jack Jiang
		// 尝试停掉QoS质量保证（发送）心跳线程
		QoS4SendDaemon.getInstance(context).stop();
		// 尝试停掉Keep Alive心跳线程
		KeepAliveDaemon.getInstance(context).stop();
		// 尝试停掉消息接收者
		LocalUDPDataReciever.getInstance(context).stop();
		// 尝试停掉QoS质量保证（接收防重复机制）心跳线程
		QoS4ReciveDaemon.getInstance(context).stop();
		// 尝试关闭本地Socket
		LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
		
		try
		{
			// Unregister broadcast listeners
			context.unregisterReceiver(networkConnectionStatusBroadcastReceiver);
		}
		catch (Exception e)
		{
			Log.w(TAG, e.getMessage(), e);
		}
		
		//
		_init = false;
		
		//
		this.setLoginHasInit(false);
		this.setConnectedToServer(false);
	}
	
	/**
	 * 返回用户成功登陆后，服务端分配的id号。
	 * <br>
	 * 本字段只在 {@link #connectedToServer}==true 的情况下才有意义哦.
	 * 
	 * @return
	 */
	public int getCurrentUserId()
	{
		return currentUserId;
	}
	/**
	 * 设置用户成功登陆后，服务端分配的id号。
	 * <br>
	 * <b>本方法由框架自动调用，无需也不建议应用层调用。</b>
	 * 
	 * @param currentUserId 用户成功登陆后，服务端分配的id号
	 * @return
	 */
	public ClientCoreSDK setCurrentUserId(int currentUserId)
	{
		this.currentUserId = currentUserId;
		return this;
	}
	
	/**
	 * 返回登陆信息成功发出后被设置的登陆账号名。
	 * <br>
	 * 因不保证服务端正确收到和处理了该用户的登陆信息，所以本字段因只在
	 * {@link #connectedToServer}==true 时才有意义.
	 * 
	 * @return
	 */
	public String getCurrentLoginName()
	{
		return currentLoginName;
	}
	/**
	 * 登陆信息成功发出后就会设置本字段（即登陆账号名），登陆账号名也将
	 * 在掉线后自动重连时使用。
	 * <br>
	 * <b>本方法由框架自动调用，无需也不建议应用层调用。</b>
	 * 
	 * @param currentLoginName
	 * @return
	 */
	public ClientCoreSDK setCurrentLoginName(String currentLoginName)
	{
		this.currentLoginName = currentLoginName;
		return this;
	}
	
	/**
	 * 返回登陆信息成功发出后被设置的登陆密码。
	 * <br>
	 * 因不保证服务端正确收到和处理了该用户的登陆信息，所以本字段因只在
	 * {@link #connectedToServer}==true 时才有意义.
	 * 
	 * @return
	 */
	public String getCurrentLoginPsw()
	{
		return currentLoginPsw;
	}
	/**
	 * 登陆信息成功发出后就会设置本字段（即登陆密码），登陆密码也将
	 * 在掉线后自动重连时使用。
	 * <br>
	 * <b>本方法由框架自动调用，无需也不建议应用层调用。</b>
	 * 
	 * @param currentLoginName
	 * @return
	 */
	public void setCurrentLoginPsw(String currentLoginPsw)
	{
		this.currentLoginPsw = currentLoginPsw;
	}
	
	/**
	 * 返回登陆信息成功发出后被设置的登陆额外信息（其是由调用者自行设置，不设置则为null）。
	 * <br>
	 * 因不保证服务端正确收到和处理了该用户的登陆信息，所以本字段因只在
	 * {@link #connectedToServer}==true 时才有意义.
	 * 
	 * @return
	 * @since 2.1.6
	 */
	public String getCurrentLoginExtra()
	{
		return currentLoginExtra;
	}
	/**
	 * 登陆信息成功发出后就会设置本字段（即登陆额外信息，其是由调用者自行设置，不设置则为null），登陆额外信息也将
	 * 在掉线后自动重连时使用。
	 * <br>
	 * <b>本方法由框架自动调用，无需也不建议应用层调用。</b>
	 * 
	 * @param currentLoginExtra
	 * @return
	 * @since 2.1.6
	 */
	public ClientCoreSDK setCurrentLoginExtra(String currentLoginExtra)
	{
		this.currentLoginExtra = currentLoginExtra;
		return this;
	}

	/**
	 * 当且仅当用户从登陆界面成功登陆后设置本字段为true，
	 * 服务端反馈会话被注销或系统退出（登陆）时自动被设置为false。
	 * 
	 * @return
	 */
	public boolean isLoginHasInit()
	{
		return loginHasInit;
	}
	/**
	 * 当且仅当用户从登陆界面成功登陆后设置本字段为true，
	 * 服务端反馈会话被注销或系统退出（登陆）时自动被设置为false。
	 * <br>
	 * <b>本方法由框架自动调用，无需也不建议应用层调用。</b>
	 * 
	 * @param loginHasInit
	 * @return
	 */
	public ClientCoreSDK setLoginHasInit(boolean loginHasInit)
	{
		this.loginHasInit = loginHasInit;
//		if(!logined)
//		{
//			currentLoginName = null;
//			currentLoginPsw = null;
//		}
		return this;
	}
	
	/**
	 * 是否已成功连接到服务器（当然，前提是已成功发起过登陆请求后）.
	 * <p>
	 * 此“成功”意味着可以正常与服务端通信（可以近似理解为Socket正常建立）
	 * ，“不成功”意味着不能与服务端通信.
	 * <br>
	 * 不成功的因素有很多：比如网络不可用、网络状况很差导致的掉线、心跳超时等.
	 * <p>
	 * <b>本参数是整个MobileIMSDK框架中唯一可作为判断与MobileIMSDK服务器的通信是否正常的准确依据。</b>
	 * <p>
	 * 本参数将在收到服务端的登陆请求反馈后被设置为true，在与服务端的通信无法正常完成时被设置为false。
	 * <br>
	 * <u>那么MobileIMSDK如何判断与服务端的通信是否正常呢？</u> 判断方法如下：
	 * <ul>
	 * <li>登陆请求被正常反馈即意味着通信正常（包括首次登陆时和断掉后的自动重新时）；</li>
	 * <li>首次登陆或断线后自动重连时登陆请求被发出后，没有收到服务端反馈时即意味着不正常；</li>
	 * <li>与服务端通信正常后，在规定的超时时间内没有收到心跳包的反馈后即意味着与服务端的通信又中断了（即所谓的掉线）。</li>
	 * </ul>
	 * 
	 * @return true表示与服务端真正的通信正常（即准确指明可正常进行消息交互，而不只是物理网络连接正常，因为物理连接正常
	 * 并不意味着服务端允许你合法的进行消息交互），否由表示不正常
	 */
	public boolean isConnectedToServer()
	{
		return connectedToServer;
	}
	/**
	 * 是否已成功连接到服务器（当然，前提是已成功发起过登陆请求后）.
	 * <br>
	 * <b>本方法由框架自动调用，无需也不建议应用层调用。</b>
	 * 
	 * @param connectedToServer
	 */
	public void setConnectedToServer(boolean connectedToServer)
	{
		this.connectedToServer = connectedToServer;
	}

	/**
	 * RaonbowCore的核心框架是否已经初始化.
	 * <br>
	 * 当调用 {@link #init()}方法后本字段将被置为true，调用{@link #release()}
	 * 时将被重新置为false.
	 * <br>
	 * <b>本参数由框架自动设置。</b>
	 */
	public boolean isInitialed()
	{
		return this._init;
	}
	
	public boolean isLocalDeviceNetworkOk()
	{
		return localDeviceNetworkOk;
	}

	/**
	 * 设置框架基础通信消息的回调事件通知实现对象（可能的通知
	 * 有：登陆成功事件通知、掉线事件通知）
	 * 
	 * @param chatBaseEvent 框架基础通信消息的回调事件通知实现对象引用
	 */
	public void setChatBaseEvent(ChatBaseEvent chatBaseEvent)
	{
		this.chatBaseEvent = chatBaseEvent;
	}
	/**
	 * 返回框架基础通信消息的回调事件通知实现对象（可能的通知
	 * 有：登陆成功事件通知、掉线事件通知）
	 * 
	 * @return
	 */
	public ChatBaseEvent getChatBaseEvent()
	{
		return chatBaseEvent;
	}
	
	/**
	 * 设置通用数据通信消息的回调事件通知实现对象（可能的通知
	 * 有：收到聊天数据事件通知、服务端返回的错误信息事件通知等）。
	 * 
	 * @param chatTransDataEvent 通用数据通信消息的回调事件通知实现对象引用
	 */
	public void setChatTransDataEvent(ChatTransDataEvent chatTransDataEvent)
	{
		this.chatTransDataEvent = chatTransDataEvent;
	}
	/**
	 * 返回通用数据通信消息的回调事件通知实现对象引用（可能的通知
	 * 有：收到聊天数据事件通知、服务端返回的错误信息事件通知等）。
	 * 
	 * @return
	 */
	public ChatTransDataEvent getChatTransDataEvent()
	{
		return chatTransDataEvent;
	}
	
	/**
	 * 设置QoS质量保证机制的回调事件通知实现对象（可能的通知
	 * 有：消息未成功发送的通知、消息已被对方成功收到的通知等）。
	 * 
	 * @param messageQoSEvent 通用数据通信消息的回调事件通知实现对象引用
	 */
	public void setMessageQoSEvent(MessageQoSEvent messageQoSEvent)
	{
		this.messageQoSEvent = messageQoSEvent;
	}
	/**
	 * 返回QoS质量保证机制的回调事件通知实现对象（可能的通知
	 * 有：消息未成功发送的通知、消息已被对方成功收到的通知等）。
	 * 
	 * @return
	 */
	public MessageQoSEvent getMessageQoSEvent()
	{
		return messageQoSEvent;
	}

	//--------------------------------------------------------------------------------------- inner class
	/**
	 * 本地网络状态变更消息接收对象.
	 * <p>
	 * 接收本地网络状态变更的目的在于解决当正常的连接因本地网络改变（比如：网络断开了后，又连上了）
	 * 而无法再次正常发送数据的问题（即使网络恢复后），解决的方法是：当检测到本地网络断开后就立即关停
	 * 本地UDP Socket，这样当下次重新登陆或尝试发送数据时就会重新建立Socket从而达到重置Socket的目的，
	 * Socket重置后也就解决了这个问题。
	 */
	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished
	private final BroadcastReceiver networkConnectionStatusBroadcastReceiver = new BroadcastReceiver() 
	{ 
		@Override
		public void onReceive(Context context, Intent intent)
		{
			ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE); 
			NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
			NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
			if (!(mobNetInfo != null && mobNetInfo.isConnected())
					&& !(wifiNetInfo != null && wifiNetInfo.isConnected()))
			{ 
//				if(ClientCoreSDK.DEBUG)
				Log.e(TAG, "【本地网络通知】检测本地网络连接断开了!");
				
				//
				localDeviceNetworkOk = false;
				
				// 尝试关闭本地Socket（以便等网络恢复时能重新建立Socket，也就间接达到了重置网络的能力）
				LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
			}
			else 
			{ 
				if(ClientCoreSDK.DEBUG)
					// connect network 
					Log.e(TAG, "【本地网络通知】检测本地网络已连接上了!");
				
				//
				localDeviceNetworkOk = true;
				
				// ** 尝试关闭本地Socket（以便等网络恢复时能重新建立Socket，也就间接达到了重置网络的能力）
				// 【此处可以解决以下场景问题：】当用户成功登陆后，本地网络断开了，则此时自动重连机制已启动，
				// 而本地侦听的开启是在登陆信息成功发出时（本地网络连好后，信息是可以发出的）启动的，而
				// 收到网络连接好的消息可能要滞后于真正的网络连接好（那此时间间隔内登陆数据可以成功发出）
				// ，那么此时如果启动侦听则必须导致侦听不可能成功。以下代码保证在收到网络连接消息时，先无条件关闭
				// 网络连接，当下一个自动登陆循环到来时自然就重新建立Socket了，那么此时重新登陆消息发出后再
				// 启动UDP侦听就ok了。--> 说到底，关闭网络连接就是为了在下次使用网络时无条件重置Socket，从而保证
				// Socket被建立时是处于正常的网络状况下。
				LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
			} 
		}
	};
}
