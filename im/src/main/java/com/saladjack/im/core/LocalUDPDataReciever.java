/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * LocalUDPDataReciever.java at 2016-2-20 10:59:12, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.saladjack.im.core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Observable;
import java.util.Observer;

import com.saladjack.im.ClientCoreSDK;
import com.saladjack.im.conf.ConfigEntity;
import net.openmob.mobileimsdk.server.protocal.ErrorCode;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import net.openmob.mobileimsdk.server.protocal.ProtocalFactory;
import net.openmob.mobileimsdk.server.protocal.ProtocalType;
import net.openmob.mobileimsdk.server.protocal.s.PErrorResponse;
import net.openmob.mobileimsdk.server.protocal.s.PLoginInfoResponse;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 数据接收处理独立线程。
 * <br>
 * 主要工作是将收到的数据进行解析并按MobileIMSDK框架的协议进行调度和处理。
 * 本类是MobileIMSDK框架数据接收处理的唯一实现类，也是整个框架算法最为关
 * 键的部分。
 * <p>
 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
 * 
 */
public class LocalUDPDataReciever
{
	private final static String TAG = LocalUDPDataReciever.class.getSimpleName();
	
	private Thread thread = null;
	
	private static LocalUDPDataReciever instance = null;
	
	private static MessageHandler messageHandler = null;
	
	private Context context = null;
	
	public static LocalUDPDataReciever getInstance(Context context)
	{
		if(instance == null)
		{
			instance = new LocalUDPDataReciever(context);
			messageHandler = new MessageHandler(context);
		}
		return instance;
	}
	
	private LocalUDPDataReciever(Context context)
	{
		this.context = context;
	}
	
	/**
	 * 无条件中断本线程的运行。
	 * <p>
	 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
	 */
	public void stop()
	{
		if(thread != null)
		{
			thread.interrupt();
			thread = null;
		}
	}
	
	/**
	 * 启动线程。
	 * <p>
	 * 无论本方法调用前线程是否已经在运行中，都会尝试首先调用 {@link #stop()}方法，
	 * 以便确保线程被启动前是真正处于停止状态，这也意味着可无害调用本方法。
	 * <p>
	 * <b>本线程的启停，目前属于MobileIMSDK算法的一部分，暂时无需也不建议由应用层自行调用。</b>
	 */
	public void startup()
	{
		//
		stop();
		
		try {
			thread = new Thread(new Runnable()
			{
				public void run()
				{
					try {
						if(ClientCoreSDK.DEBUG)
							Log.d(TAG, "本地UDP端口侦听中，端口="+ ConfigEntity.localUDPPort+"...");
						//开始侦听
						p2pListeningImpl();
					}
					catch (Exception eee) {
						Log.w(TAG, "本地UDP监听停止了(socket被关闭了?),"+eee.getMessage(), eee);
					}
				}
			});
			//启动线程,开始侦听
			thread.start();
		}
		catch (Exception e) {
			Log.w(TAG, "本地UDPSocket监听开启时发生异常,"+e.getMessage(), e);
		}
	}

	/**
	 * 实施开启本地UDP侦听.
	 * 
	 * @throws Exception
	 */
	private void p2pListeningImpl() throws Exception {
		while (true) {
			// 缓冲区
			byte[] data = new byte[1024];
			// 接收数据报的包
			DatagramPacket packet = new DatagramPacket(data, data.length);
			
			//
			DatagramSocket localUDPSocket = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();
			if (localUDPSocket != null && !localUDPSocket.isClosed())
			{
				//接收数据
				localUDPSocket.receive(packet);
				
				// 通知处理者
				Message m = Message.obtain();
				m.obj = packet;
				messageHandler.sendMessage(m);
			}
		}
	}
	
	private static class MessageHandler extends Handler
	{
		private Context context = null;
		
		public MessageHandler(Context context)
		{
			this.context = context;
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			DatagramPacket packet = (DatagramPacket)msg.obj;
			if(packet == null)
				return;
//			Log.d(TAG, "\n--------------------------------------------------------[2]");
			try
			{
//				Log.d(TAG, "收到UDP原始消息：长度="+packet.getLength()+", json="
////						+CharsetHelper.getString(packet.getData(), 4, packet.getLength() - 5)
//						+CharsetHelper.getString(packet.getData(), packet.getLength())
//						);
				final Protocal pFromServer = 
//						Protocal.fromBytes_JSON(packet.getData(), packet.getLength());
						ProtocalFactory.parse(packet.getData(), packet.getLength());
//				Log.d(TAG, "JSON解决完成，协议类型是："+pFromServer.getType());
				
				// ## 如果该消息是需要QoS支持的包
				if(pFromServer.isQoS())
				{
					// 且已经存在于接收列表中（即意味着可能是之前发给对方的应
					// 答包因网络或其它情况丢了，对方又因QoS机制重新发过来了）
					if(QoS4ReciveDaemon.getInstance(context).hasRecieved(pFromServer.getFp()))
					{
						if(ClientCoreSDK.DEBUG)
							Log.d(TAG, "【QoS机制】"+pFromServer.getFp()+"已经存在于发送列表中，这是重复包，通知应用层收到该包咯！");
						
						//------------------------------------------------------------------------------ [1]代码与[2]处相同的哦 S
						// 【【C2C、C2S、S2C模式下的QoS机制2/4步：将收到的包存入QoS接收方暂存队列中（用于防重复）】】
						QoS4ReciveDaemon.getInstance(context).addRecieved(pFromServer);
						// 【【C2C、C2S、S2C模式下的QoS机制3/4步：回应答包】】
						// 给发送者回一个“收到”应答包
						sendRecievedBack(pFromServer);
						//------------------------------------------------------------------------------ [1]代码与[2]处相同的哦 E
						
						// 此包重复，不需要通知应用层收到该包了，直接返回
						return;
					}
					
					//------------------------------------------------------------------------------ [2]代码与[1]处相同的哦 S
					// 【【C2C、C2S、S2C模式下的QoS机制2/4步：将收到的包存入QoS接收方暂存队列中（用于防重复）】】
					QoS4ReciveDaemon.getInstance(context).addRecieved(pFromServer);
					// 【【C2C、C2S、S2C模式下的QoS机制3/4步：回应答包】】
					// 给发送者回一个“收到”应答包
					sendRecievedBack(pFromServer);
					//------------------------------------------------------------------------------ [2]代码与[1]处相同的哦 E
				}
				
				//
				switch(pFromServer.getType())
				{
					// ** 收到通用数据
					case ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA:
					{
//						Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>收到"+pFromServer.getFrom()+"发过来的消息："
//								+pFromServer.getDataContent()+".["+pFromServer.getTo()+"]");
						// 收到通用数据的回调
						if(ClientCoreSDK.getInstance().getChatTransDataEvent() != null)
						{
							ClientCoreSDK.getInstance().getChatTransDataEvent().onTransBuffer(
									pFromServer.getFp(), pFromServer.getFrom(), pFromServer.getDataContent());
						}
						break;
					}
					//** 收到服务反馈过来的心跳包
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE:
					{
						if(ClientCoreSDK.DEBUG)
							Log.d(TAG, "收到服务端回过来的Keep Alive心跳响应包.");
						// 更新服务端的最新响应时间（该时间将作为计算网络是否断开的依据）
						KeepAliveDaemon.getInstance(context).updateGetKeepAliveResponseFromServerTimstamp();
						break;
					}
					//** 收到好友发过来的QoS应答包
					case ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED:
					{
						// 应答包的消息内容即为之前收到包的指纹id
						String theFingerPrint = pFromServer.getDataContent();
						if(ClientCoreSDK.DEBUG)
							Log.d(TAG, "【QoS】收到"+pFromServer.getFrom()+"发过来的指纹为"+theFingerPrint+"的应答包.");
						
						// 将收到的应答事件通知事件处理者
						if(ClientCoreSDK.getInstance().getMessageQoSEvent() != null)
							ClientCoreSDK.getInstance().getMessageQoSEvent().messagesBeReceived(theFingerPrint);
						
						
						// 【【C2C或C2S模式下的QoS机制4/4步：收到应答包时将包从发送QoS队列中删除】】
						QoS4SendDaemon.getInstance(context).remove(theFingerPrint);
						
						break;
					}
					// ** 收到服务端反馈过来的登录完成信息
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN:
					{
						// 解析服务端反馈过来的登录消息
						PLoginInfoResponse signinInfoRes = ProtocalFactory.parsePLoginInfoResponse(pFromServer.getDataContent());
						// 登录成功了！
						if(signinInfoRes.getCode() == 0)
						{
							// 记录用户登录信息（因为此处不太好记录用户登录名和密
							// 码，所以登录名和密码现在是在登录消息发出时就记录了）
							ClientCoreSDK.getInstance()
								.setsigninHasInit(true)
								.setCurrentUserId(signinInfoRes.getUser_id());
							
							// 尝试关闭自动重新登录线程（如果该线程正在运行的话）
							AutoReSigninDaemon.getInstance(context).stop();
							
							// 立即开启Keepalive心跳线程
							KeepAliveDaemon.getInstance(context).setNetworkConnectionLostObserver(new Observer(){
								// 与服务端的网络断开后会由观察者调用本方法
								@Override
								public void update(Observable observable, Object data) {
									//【掉线后关掉QoS机制，为手机省电】
									// 掉线时关闭QoS机制之发送列表重视机制（因为掉线了开启也没有意义）
									// ** 关闭但不清空可能存在重传列表是合理的，防止在网络状况不好的情况下，登
									// ** 陆能很快恢复时也能通过重传尝试重传，即使重传不成功至少也可以提示一下
									QoS4SendDaemon.getInstance(context).stop();
									// 掉线时关闭QoS机制之接收列表防重复机制（因为掉线了开启也没有意义）
									// ** 关闭但不清空可能存在缓存列表是合理的，防止在网络状况不好的情况下，登
									// ** 陆能很快恢复时对方可能存在的重传，此时也能一定程序避免消息重复的可能
									QoS4ReciveDaemon.getInstance(context).stop();
									
									// 设置中否正常连接（登录）到服务器的标识（注意：在要event事件通知前设置哦，因为应用中是在event中处理状态的）
									ClientCoreSDK.getInstance().setConnectedToServer(false);
									ClientCoreSDK.getInstance().setCurrentUserId(-1);
									// 通知回调实现类
									ClientCoreSDK.getInstance().getChatBaseEvent().onLinkCloseMessage(-1);
									// 网络断开后即刻开启自动重新登录线程从而尝试重新登录（以便网络恢复时能即时自动登录）
									AutoReSigninDaemon.getInstance(context).start(true);
								}
							});
							// ** 2015-02-10 by Jack Jiang：收到登录成功反馈后，无需立即就发起心跳，因为刚刚才与服务端
							// ** 成功通信了呢（刚收到服务器的登录成功反馈），节省1次心跳，降低服务重启后的“雪崩”可能性
//							KeepAliveDaemon.getInstance(context).start(true);
							KeepAliveDaemon.getInstance(context).start(false);
							
							//【登录成功后开启QoS机制】
							// 启动QoS机制之发送列表重视机制
							QoS4SendDaemon.getInstance(context).startup(true);
							// 启动QoS机制之接收列表防重复机制
							QoS4ReciveDaemon.getInstance(context).startup(true);
							// 设置中否正常连接（登录）到服务器的标识（注意：在要event事件通知前设置哦，因为应用中是在event中处理状态的）
							ClientCoreSDK.getInstance().setConnectedToServer(true);
						}
						else {
//							Log.d(TAG, "登录验证失败，错误码="+signinInfoRes.getCode()+"！");
							// 设置中否正常连接（登录）到服务器的标识（注意：在要event事件通知前设置哦，因为应用中是在event中处理状态的）
							ClientCoreSDK.getInstance().setConnectedToServer(false);
							ClientCoreSDK.getInstance().setCurrentUserId(-1);
						}
						
						// 用户登录认证情况通知回调
						if(ClientCoreSDK.getInstance().getChatBaseEvent() != null)
						{
							ClientCoreSDK.getInstance().getChatBaseEvent().onSignInMessage(
								signinInfoRes.getUser_id(), signinInfoRes.getCode(),/*username*/"user-name");
						}
						
						break;
					}
					// ** 服务端返回来过的错误消息
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR:
					{
						// 解析服务端反馈过来的消息
						PErrorResponse errorRes = ProtocalFactory.parsePErrorResponse(pFromServer.getDataContent());
						
						// 收到的如果是“尚未登录”的错误消息，则意味着该用户的会话可能已经超时了，
						// 此时当然要停止心跳线程了，否则心跳有何意义！
						// ** 【性能隐患】：因为与服务端是UDP无连接的，所以服务端回过来的此消息可能会网络状况
						// ** 差等情况而导致延迟收到该包（收到时很可能与服务端的会话已经正常了），但目前来
						// ** 没有办法保证完全的正确性，但起码保证用户与服务端的正常会话才是首位的，所以
						// ** 有时候错误地终止了正常会话而启动重新登录从而损失的一些性能因是可以下载解和合理的
						if(errorRes.getErrorCode() == ErrorCode.ForS.RESPONSE_FOR_UNLOGIN)
						{
							// 
							ClientCoreSDK.getInstance().setsigninHasInit(false);
							
							Log.e(TAG, "收到服务端的“尚未登录”的错误消息，心跳线程将停止，请应用层重新登录.");
							// 停止心跳
							KeepAliveDaemon.getInstance(context).stop();
							
							// 此时尝试延迟开启自动登录线程哦（注意不需要立即开启）
							// ** 【说明】：为何此时要开启自动登录呢？逻辑上讲，心跳时即是连接正常时，
							// ** 上面的停止心跳即是登录身份丢失时，那么此时再开启自动登录线程
							// ** 则也是合理的。
							// ** 其实此处开启自动登录线程是更多是为了防止这种情况：当客户端并没有
							// ** 触发网络断开（也就不会触发自动登录）时，而此时却可能因延迟等错误或
							// ** 时机不对的情况下却收到了“未登录”回复时，就会关闭心跳，但自动重新登录
							// ** 却再也没有机会启动起来，那么这个客户端将会因此而永远（直到登录程序后再登录）
							// ** 无法重新登录而一直处于离线状态，这就不对了。以下的启动重新登录时机在此正
							// ** 可解决此种情况，而使得重新登录机制更强壮哦！
							AutoReSigninDaemon.getInstance(context).start(false);
						}
						
						// 收到错误响应消息的回调
						if(ClientCoreSDK.getInstance().getChatTransDataEvent() != null)
						{
							ClientCoreSDK.getInstance().getChatTransDataEvent().onErrorResponse(
									errorRes.getErrorCode(), errorRes.getErrorMsg());
						}
						break;
					}
					
					default:
						Log.w(TAG, "收到的服务端消息类型："+pFromServer.getType()+"，但目前该类型客户端不支持解析和处理！");
						break;
				}
			}
			catch (Exception e)
			{
				Log.w(TAG, "处理消息的过程中发生了错误.", e);
			}
		}
		
		private void sendRecievedBack(final Protocal pFromServer)
		{
			if(pFromServer.getFp() != null)
			{
				new LocalUDPDataSender.SendCommonDataAsync(
						context
						, ProtocalFactory.createRecivedBack(
								pFromServer.getTo()
								, pFromServer.getFrom()
								, pFromServer.getFp())){
					@Override
					protected void onPostExecute(Integer code)
					{
						if(ClientCoreSDK.DEBUG)
							Log.d(TAG, "【QoS】向"+pFromServer.getFrom()+"发送"+pFromServer.getFp()+"包的应答包成功,from="+pFromServer.getTo()+"！");
					}
				}.execute();
			}
			else
			{
				Log.w(TAG, "【QoS】收到"+pFromServer.getFrom()+"发过来需要QoS的包，但它的指纹码却为null！无法发应答包！");
			}
		}
	}
}
