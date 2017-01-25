/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * LocalUDPDataSender.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package net.openmob.mobileimsdk.android.core;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.conf.ConfigEntity;
import net.openmob.mobileimsdk.android.utils.UDPUtils;
import net.openmob.mobileimsdk.server.protocal.CharsetHelper;
import net.openmob.mobileimsdk.server.protocal.ErrorCode;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 数据发送处理实用类。
 * <p>
 * 本类是MobileIMSDK框架的唯一提供数据发送的公开实用类。
 * 
 * @author Jack Jiang, 2013-10-10
 * @version 1.0
 * @since 1.0
 */
public class LocalUDPDataSender
{
	private final static String TAG = LocalUDPDataSender.class.getSimpleName();
	private static LocalUDPDataSender instance = null;
	
	private Context context = null;
	
	public static LocalUDPDataSender getInstance(Context context)
	{
		if(instance == null)
			instance = new LocalUDPDataSender(context);
		return instance;
	}
	
	private LocalUDPDataSender(Context context)
	{
		this.context = context;
	}
	
	/**
	 * 发送登陆信息.
	 * <p>
	 * <b>注意：</b>本库的启动入口就是登陆过程触发的，因而要使本库能正常工作，
	 * 请确保首先进行登陆操作。
	 * <p>
	 * 实现登陆操作时推荐使用本类默认的 {@link SendLoginDataAsync}类或者类似实现。
	 * 否则，在调用本方法前请确保核心库的初始化方法{@link ClientCoreSDK#init(Context)}
	 * 已被调用（从而保证核心类库的初始化），且本方法调用后登陆被成功发出后还需调用
	 * {@link LocalUDPDataReciever#startup()}以便启动本地端口监听，否则将收到任何消息 。
	 * 
	 * @param loginName 登陆时提交的用户名：此用户名对框架来说可以随意，具体意义由上层逻辑决即可
	 * @param loginPsw 登陆时提交的密码：此密码对框架来说可以随意，具体意义由上层逻辑决即可
	 * @param extra 额外信息字符串，可为null。本字段目前为保留字段，供上层应用自行放置需要的内容
	 * @return 0表示数据发出成功，否则返回的是错误码
	 * @see #send(byte[], int)
	 */
	// 不推荐直接调用本方法实现“登陆”流程，请使用SendLoginAsync（此异步线程中包含发送登陆包之外的处理和逻辑）
	int sendLogin(String loginName, String loginPsw, String extra)
	{
		byte[] b = ProtocalFactory.createPLoginInfo(loginName, loginPsw, extra).toBytes();
		int code = send(b, b.length);
		// 登陆信息成功发出时就把登陆名存下来
		if(code == 0)
		{
			ClientCoreSDK.getInstance().setCurrentLoginName(loginName);
			ClientCoreSDK.getInstance().setCurrentLoginPsw(loginPsw);
			ClientCoreSDK.getInstance().setCurrentLoginExtra(extra);
		}
		
		return code;
	}
	
	/**
	 * 发送注销登陆信息.
	 * <p>
	 * <b>注意：</b>此方法的调用将被本库理解为退出库的使用，本方法将会额外调
	 * 用资源释放方法{@link ClientCoreSDK#release()}，以保证资源释放。
	 * <p>
	 * 本方法调用后，除非再次进行登陆过程，否则核心库将处于初始未初始化状态。
	 * 
	 * @return 0表示数据发出成功，否则返回的是错误码
	 * @see #send(byte[], int)
	 */
	public int sendLoginout()
	{
		int code = ErrorCode.COMMON_CODE_OK;
		if(ClientCoreSDK.getInstance().isLoginHasInit())
		{
			byte[] b = ProtocalFactory.createPLoginoutInfo(ClientCoreSDK.getInstance().getCurrentUserId()
					, ClientCoreSDK.getInstance().getCurrentLoginName()).toBytes();
			code = send(b, b.length);
			// 登出信息成功发出时
			if(code == 0)
			{
	//			// 发出退出登陆的消息同时也关闭心跳线程
	//			KeepAliveDaemon.getInstance(context).stop();
	//			// 重置登陆标识
	//			ClientCoreSDK.getInstance().setLoginHasInit(false);
			}
		}
		
		// 释放SDK资源
		ClientCoreSDK.getInstance().release();
		
		return code;
	}
	
	/**
	 * 发送Keep Alive心跳包.
	 * 
	 * @return 0表示数据发出成功，否则返回的是错误码
	 * @see #send(byte[], int)
	 */
	int sendKeepAlive()
	{
		byte[] b = ProtocalFactory.createPKeepAlive(ClientCoreSDK.getInstance().getCurrentUserId()).toBytes();
		return send(b, b.length);
	}
	
	/**
	 * 通用数据发送方法（默认不需要Qos支持）。
	 * 
	 * @param dataContent byte数组组织的数据内容
	 * @param dataLen byte数组长度
	 * @param to_user_id 要发送到的目标用户id
	 * @return 0表示数据发出成功，否则返回的是错误码
	 * @see #sendCommonData(String, int, boolean, String)
	 */
	public int sendCommonData(byte[] dataContent, int dataLen, int to_user_id)
	{
		return sendCommonData(CharsetHelper.getString(dataContent, dataLen), to_user_id, false, null);
	}
	/**
	 * 通用数据发送方法。
	 * 
	 * @param dataContent byte数组组织的数据内容
	 * @param dataLen byte数组长度
	 * @param to_user_id 要发送到的目标用户id
	 * @param QoS true表示需QoS机制支持，不则不需要
	 * @param fingerPrint QoS机制中要用到的指纹码（即消息包唯一id），可设为null，生成方法见 {@link Protocal#genFingerPrint()}
	 * @return 0表示数据发出成功，否则返回的是错误码
	 * @see #sendCommonData(String, int, boolean, String)
	 */
	public int sendCommonData(byte[] dataContent, int dataLen, int to_user_id, boolean QoS, String fingerPrint)
	{
		return sendCommonData(CharsetHelper.getString(dataContent, dataLen), to_user_id, QoS, fingerPrint);
	}
	
	/**
	 * 通用数据发送方法（默认不需要Qos支持）。
	 * 
	 * @param dataContentWidthStr 要发送的数据内容（字符串方式组织）
	 * @param to_user_id 要发送到的目标用户id
	 * @return 0表示数据发出成功，否则返回的是错误码
	 * @see #sendCommonData(Protocal)
	 * @see DataFactoryC#createCommonData(String, int, int)
	 */
	public int sendCommonData(String dataContentWidthStr, int to_user_id)
	{
		return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr
				, ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id));
	}
	/**
	 * 通用数据发送方法。
	 * 
	 * @param dataContentWidthStr 要发送的数据内容（字符串方式组织）
	 * @param to_user_id 要发送到的目标用户id
	 * @param QoS true表示需QoS机制支持，不则不需要
	 * @param fingerPrint QoS机制中要用到的指纹码（即消息包唯一id），可设为null，生成方法见 {@link Protocal#genFingerPrint()}
	 * @return 0表示数据发出成功，否则返回的是错误码
	 * @see #sendCommonData(Protocal)
	 * @see DataFactoryC#createCommonData(String, int, int, boolean, String)
	 */
	public int sendCommonData(String dataContentWidthStr, int to_user_id, boolean QoS, String fingerPrint)
	{
		return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr
				, ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id, QoS, fingerPrint));
	}
	
	/**
	 * 通用数据发送的根方法。
	 * 
	 * @param p 要发送的内容（MobileIMSDK框架的“协议”DTO对象组织形式）
	 * @return 0表示数据发出成功，否则返回的是错误码
	 * @see #send(byte[], int)
	 */
	public int sendCommonData(Protocal p)
	{
		if(p != null)
		{
			byte[] b = p.toBytes();
			int code = send(b, b.length);
			if(code == 0)
			{
				// 【【C2C或C2S模式下的QoS机制1/4步：将包加入到发送QoS队列中】】
				// 如果需要进行QoS质量保证，则把它放入质量保证队列中供处理(已在存在于列
				// 表中就不用再加了，已经存在则意味当前发送的这个是重传包哦)
				if(p.isQoS() && !QoS4SendDaemon.getInstance(context).exist(p.getFp()))
					QoS4SendDaemon.getInstance(context).put(p);
			}
			return code;
		}
		else
			return ErrorCode.COMMON_INVALID_PROTOCAL;
	}
	/**
	 * 发送数据到服务端.
	 * <p>
	 * 注意：直接调用此方法将无法支持QoS质量保证哦！
	 * 
	 * @param fullProtocalBytes Protocal对象转成JSON后再编码成byte数组后的结果
	 * @param dataLen Protocal对象转成JSON后再编码成byte数组长度
	 * @return 0表示数据发出成功，否则返回的是错误码
	 * @see ErrorCode
	 * @see ErrorCode.ForC
	 */
	private int send(byte[] fullProtocalBytes, int dataLen)
	{
		if(!ClientCoreSDK.getInstance().isInitialed())
			return ErrorCode.ForC.CLIENT_SDK_NO_INITIALED;
		
		if(!ClientCoreSDK.getInstance().isLocalDeviceNetworkOk())
		{
			Log.e(TAG, "本地网络不能工作，send数据没有继续!");
			return ErrorCode.ForC.LOCAL_NETWORK_NOT_WORKING;
		}
//		if(!ClientCoreSDK.getInstance().isLogined())
//			return ErrorCode.COMMON_NO_LOGIN;
		
//		System.out.println("\n---------------------------------------------------------[1]");
		// 获得UDPSocket实例
		DatagramSocket ds = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();
		// 如果Socket没有连接上服务端
		if(ds != null && !ds.isConnected())
		{
			try
			{
				// 此判断是为了防存放于static的全局变量因Exception而导致Android把它们置null从而导致程
				// 序未捕获异常而崩溃的风险！（加了这个判断的目的在于让程序直接报错或提示算了！）
				if(ConfigEntity.serverIP == null)
				{
					Log.w(TAG, "send数据没有继续，原因是ConfigEntity.server_ip==null!");
					return ErrorCode.ForC.TO_SERVER_NET_INFO_NOT_SETUP;
				}
				
				// 即刻连接上服务端（如果不connect，即使在DataProgram中设置了远程id和地址则服务端MINA也收不到，跟普通的服
				// 务端UDP貌似不太一样，普通UDP时客户端无需先connect可以直接send设置好远程ip和端口的DataPragramPackage）
//				ds.connect(InetAddress.getByName(_Utils.REMOTE_SERVER_LISTENING_IP), _Utils.REMOTE_SERVER_LISTENING_PORT);

//				ds.connect(InetAddress.getByName(ConfigEntity.serverIP), ConfigEntity.serverUDPPort);
				ds.connect(new InetSocketAddress(ConfigEntity.serverIP,ConfigEntity.serverUDPPort));
//				FIXME: 因为connect是异步的，为了在尽可能保证在send前就已connect，所以最好在socketProvider里Bind后就connect!
			}
			catch (Exception e)
			{
				Log.w(TAG, "send时出错，原因是："+e.getMessage(), e);
				return ErrorCode.ForC.BAD_CONNECT_TO_SERVER;
			}
		}
		return UDPUtils.send(ds, fullProtocalBytes, dataLen) ? ErrorCode.COMMON_CODE_OK : ErrorCode.COMMON_DATA_SEND_FAILD;
	}
	
	//------------------------------------------------------------------------------------------ utilities class
//	public static abstract class SendDataAsync extends AsyncTask<Object, Integer, Integer>
//	{
//		protected Context context = null;
//
//		public SendDataAsync(Context context)
//		{
//			this.context = context;
//		}
//
//		@Override
//		protected abstract Integer doInBackground(Object... params);
//
//		@Override
//		protected abstract void onPostExecute(Integer code);
//	}
	
	/**
	 * 通用数据发送异步线程实现抽象类。
	 * <br>
	 * 子类需自行实现 {@link #onPostExecute(Integer)}方法。
	 * <p>
	 * 为保持API使用的一致性，此类为本库的推荐实现类，但并非必须要使用，使用者也
	 * 可自行设计异步数据发送过程（如使用{@link AsyncTask}）。
	 * 
	 * @author Jack Jiang
	 * @see ProtocalFactory#createCommonData(String, int, int, boolean, String)
	 * @see ProtocalFactory#createCommonData(String, int, int)
	 * @see LocalUDPDataSender#sendCommonData(Protocal)
	 */
	public static abstract class SendCommonDataAsync extends AsyncTask<Object, Integer, Integer>
	{
		protected Context context = null;
		protected Protocal p = null;
		
		public SendCommonDataAsync(Context context, byte[] dataContent, int dataLen, int to_user_id)
		{
			this(context, CharsetHelper.getString(dataContent, dataLen), to_user_id);
		}
		public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id
				, boolean QoS)
		{
			this(context, dataContentWidthStr, to_user_id, QoS, null);
		}
		public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id
				, boolean QoS, String fingerPrint)
		{
			this(context, ProtocalFactory.createCommonData(dataContentWidthStr
					, ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id, QoS, fingerPrint));
		}
		public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id)
		{
			this(context, ProtocalFactory.createCommonData(dataContentWidthStr
					, ClientCoreSDK.getInstance().getCurrentUserId(), to_user_id));
		}
		public SendCommonDataAsync(Context context, Protocal p)
		{
			if(p == null)
			{
				Log.w(TAG, "无效的参数p==null!");
				return;
			}
			this.context = context;
			this.p = p;
		}

		@Override
		protected Integer doInBackground(Object... params)
		{
			if(p != null)
				return LocalUDPDataSender.getInstance(context).sendCommonData(p);//dataContentWidthStr, to_user_id);
			return -1;
		}

		@Override
		protected abstract void onPostExecute(Integer code);
	}
	
	// 【* 关于不能在SendLoginDataAsync中进行ClientCoreSDK的init初始化的备忘】
	//   请确保首先进行核心库的初始化（这是不同于iOS和Java平台的地方，差异的原因源自Android系统里
	//   的网络连接/断开广播事件：当APP中首次注册广播事件监听时，系统会无条件向此监听者广播一条事件出来（
	//   而此时手机根本就不存在断开/连接事件的发生，其实连接本来就没有变动），且此广播是异步的。也就是说如果
	//   不能首先处理好这个”首次“广播，很可能会因异步产生的时间差而打乱MobileIMSDK的端口绑定情况。
	// 【* 非要这么干会怎么样？】
	//   如果像Java或ios平台一样，为了简化开发者的api使用，也把此行代码放到
	//   登陆接口里去默认调用，则产生的问题会是：当此行代码被调用时，网络状态广播
	//   事件会立即被注册，而android系统接着会异步地推一条连接变动广播出来（注意：
	//   此时的连接并没有变动，这只是android系统故意推出来的），那么可能导致登陆包
	//   发出时本地刚开启的本地端口监听会因刚才的广播因异步时间差，恰在本监听做好后
	//   被MobileIMSDK捕获到，而根据MobileIMSDK捕获到此事件的处理方法——立即重置Socket
	//   （正常情况下，此广播的发生肯定是因为手机网络有变动发生，那么此重置显然是合理
	//   且是必须的），那么势必会导致服务端反馈回来的登陆结果包无法被本地收到（因为刚才
	//   因那恶心的Android系统无厘头的广播已将本已启动好的本地Socket端口监听给重置了呢）。
	//   所以：鉴于Android系统对网络变动事件在首次被注册时的无厘头表现，加上MobileIMSDK
	//   对此事件的处理逻辑，最佳解决方案就是，无条件将本init方法放到APP在使用任何实际性
	//   数据发送前就作为MobileIMSDK的必备条件尽可能优先被调用：如放在Application的
	//	onCreate方法中、或者任意其它首次发送登陆包前，而不因等发送登陆包时（因为那个广播
	//   时间差的存在，很可能就导致了此广播在本地包发出后且服务端登陆结果被反馈回来后才被
	//   APP捕获到）。
	/**
	 * 登陆异步线程实现类。
	 * <br>
	 * 子类需自行实现 {@link #fireAfterSendLogin(int)}方法，以便实现登陆发出后的UI处理。
	 * 
	 * <p>
	 * 此类为本库的默认实现类，非必须要使用，使用者也可自行设计异步登陆过程（如使用{@link AsyncTask}）。
	 * <p>
	 * <b><font color="red">注意：</font></b>因Andriod系统在处理网络变动广播事件的特殊性，本类中没有
	 * 像Java或iOS平台那样默认调用MobileIMSDK的核心库的初始化方法{@link ClientCoreSDK#init(Context)}，
	 * 所以在发送登陆发前，请确保{@link ClientCoreSDK#init(Context)}已经被调用过，且越早被调用越好（如
	 * 放在Application的onCreate()方法中或者登陆Activity的onCreate()方法中）。
	 * 
	 * @see AutoReLoginDaemon
	 * @see LocalUDPDataSender#sendLogin(String, String)
	 * @see LocalUDPDataReciever#startup()
	 */
	public static abstract class SendLoginDataAsync extends AsyncTask<Object, Integer, Integer>
	{
		protected Context context = null;
		protected String loginName = null;
		protected String loginPsw = null;
		protected String extra = null;

		/**
		 * 构造方法(默认extra字段为null)。
		 * 
		 * @param context
		 * @param loginName 登陆时提交的用户名：此用户名对框架来说可以随意，具体意义由上层逻辑决即可
		 * @param loginPsw 登陆时提交的密码：此密码对框架来说可以随意，具体意义由上层逻辑决即可
		 */
		public SendLoginDataAsync(Context context
				, String loginName, String loginPsw)
		{
			this(context, loginName, loginPsw, null);
		}
		/**
		 * 构造方法。
		 * 
		 * @param context
		 * @param loginName 登陆时提交的用户名：此用户名对框架来说可以随意，具体意义由上层逻辑决即可
		 * @param loginPsw 登陆时提交的密码：此密码对框架来说可以随意，具体意义由上层逻辑决即可
		 * @param extra 额外信息字符串，可为null。本字段目前为保留字段，供上层应用自行放置需要的内容
		 */
		public SendLoginDataAsync(Context context
				, String loginName, String loginPsw, String extra)
		{
			this.context = context;
			this.loginName = loginName;
			this.loginPsw = loginPsw;
			this.extra = extra;
			
			//### Bug Fix 2015-11-07 by Jack Jiang
//			// 确保首先进行核心库的初始化（此方法多次调用是无害的，但必须要
//			// 保证在使用IM核心库的任何实质方法前调用（初始化）1次））
//			ClientCoreSDK.getInstance().init(context);
		}

		@Override
		protected Integer doInBackground(Object... params)
		{
			int code = LocalUDPDataSender.getInstance(context).sendLogin(loginName, loginPsw, this.extra);
			return code;
		}

		@Override
		protected void onPostExecute(Integer code)
		{
			// *********************** 同样的代码也存在于AutoReLoginDaemon中的代码
			if(code == 0)
			{
				// 登陆消息成功发出后就启动本地消息侦听线程：
				// 第1）种情况：首次使用程序时，登陆信息发出时才启动本地监听线程是合理的；
				// 第2）种情况：因网络原因（比如服务器关闭或重启）而导致本地监听线程中断的问题：
				//      当首次登陆后，因服务端或其它网络原因导致本地监听出错，将导致中断本地监听线程，
				//	          所以在此处在自动登陆重连或用户自已手机尝试再次登陆时重启监听线程就可以恢复本地
				//	          监听线程的运行。
				LocalUDPDataReciever.getInstance(context).startup();
			}
			else
			{
				Log.d(TAG, "数据发送失败, 错误码是："+code+"！");
			}
			
			//
			fireAfterSendLogin(code);
		}
		
		/**
		 * 登陆请求包发出后的处理。
		 * 
		 * @param code 0表示数据发出成功，否则返回的是错误码
		 */
		protected void fireAfterSendLogin(int code)
		{
			// default do nothing
		}
	}
}
