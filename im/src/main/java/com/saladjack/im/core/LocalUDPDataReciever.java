
package com.saladjack.im.core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Observable;
import java.util.Observer;

import com.saladjack.im.IMCore;
import com.saladjack.im.conf.ConfigEntity;
import com.saladjack.im.protocal.ErrorCode;
import com.saladjack.im.protocal.Protocal;
import com.saladjack.im.protocal.ProtocalFactory;
import com.saladjack.im.protocal.ProtocalType;
import com.saladjack.im.protocal.s.PErrorResponse;
import com.saladjack.im.protocal.s.PLoginInfoResponse;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by SaladJack on 2017/1/15.
 */
public class LocalUDPDataReciever {
	private final static String TAG = LocalUDPDataReciever.class.getSimpleName();
	
	private Thread thread = null;
	
	private static LocalUDPDataReciever instance = null;
	
	private static MessageHandler messageHandler = null;
	
	private Context context = null;
	
	public static LocalUDPDataReciever getInstance(Context context) {
		if(instance == null) {
			instance = new LocalUDPDataReciever(context);
			messageHandler = new MessageHandler(context);
		}
		return instance;
	}
	
	private LocalUDPDataReciever(Context context)
	{
		this.context = context;
	}

	public void stop() {
		if(thread != null) {
			thread.interrupt();
			thread = null;
		}
	}
	

	public void startup() {
		stop();
		try {
			thread = new Thread(new Runnable()
			{
				public void run()
				{
					try {
						if(IMCore.DEBUG)
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

	private void p2pListeningImpl() throws Exception {
		while (true) {
			// 缓冲区
			byte[] data = new byte[1024];
			// 接收数据报的包
			DatagramPacket packet = new DatagramPacket(data, data.length);

			DatagramSocket localUDPSocket = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();
			if (localUDPSocket != null && !localUDPSocket.isClosed()) {
				//这里因为并发socket可能会close
				localUDPSocket.receive(packet);
				
				Message m = Message.obtain();
				m.obj = packet;
				messageHandler.sendMessage(m);
			}
		}
	}
	
	private static class MessageHandler extends Handler {
		private Context context = null;
		
		public MessageHandler(Context context)
		{
			this.context = context;
		}
		
		@Override
		public void handleMessage(Message msg) {
			DatagramPacket packet = (DatagramPacket)msg.obj;
			if(packet == null)
				return;
			try {
				final Protocal pFromServer = ProtocalFactory.parse(packet.getData(), packet.getLength());
				if(pFromServer.isQoS()) {
					if(QoS4ReciveDaemon.getInstance(context).hasRecieved(pFromServer.getFp())) {
						if(IMCore.DEBUG)
							Log.d(TAG, "【QoS机制】"+pFromServer.getFp()+"已经存在于发送列表中，这是重复包，通知应用层收到该包咯！");
						
						QoS4ReciveDaemon.getInstance(context).addRecieved(pFromServer);
						sendRecievedBack(pFromServer);
						return;
					}
					
					QoS4ReciveDaemon.getInstance(context).addRecieved(pFromServer);
					sendRecievedBack(pFromServer);
				}
				
				//
				switch(pFromServer.getType()) {
					case ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA: {
						if(IMCore.getInstance().getChatTransDataEvent() != null) {
							IMCore.getInstance().getChatTransDataEvent().onTransBuffer(
									pFromServer.getFp(), pFromServer.getFrom(), pFromServer.getDataContent());
						}
						break;
					}
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$KEEP$ALIVE: {
						if(IMCore.DEBUG)
							Log.d(TAG, "收到服务端回过来的Keep Alive心跳响应包.");
						KeepAliveDaemon.getInstance(context).updateGetKeepAliveResponseFromServerTimstamp();
						break;
					}
					case ProtocalType.C.FROM_CLIENT_TYPE_OF_RECIVED: {
						String theFingerPrint = pFromServer.getDataContent();
						if(IMCore.DEBUG)
							Log.d(TAG, "【QoS】收到"+pFromServer.getFrom()+"发过来的指纹为"+theFingerPrint+"的应答包.");
						
						if(IMCore.getInstance().getMessageQoSEvent() != null)
							IMCore.getInstance().getMessageQoSEvent().messagesBeReceived(theFingerPrint);
						
						
						QoS4SendDaemon.getInstance(context).remove(theFingerPrint);
						
						break;
					}
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$LOGIN: {
						PLoginInfoResponse signinInfoRes = ProtocalFactory.parsePLoginInfoResponse(pFromServer.getDataContent());
						if(signinInfoRes.getCode() == 0) {

							IMCore.getInstance()
								.setSignInHasInit(true)
								.setCurrentUserId(signinInfoRes.getUser_id());
							

							AutoReSigninDaemon.getInstance(context).stop();
							
							KeepAliveDaemon.getInstance(context).setNetworkConnectionLostObserver(new Observer(){
								// 与服务端的网络断开后会由观察者调用本方法
								@Override
								public void update(Observable observable, Object data) {
									QoS4SendDaemon.getInstance(context).stop();
									QoS4ReciveDaemon.getInstance(context).stop();
									IMCore.getInstance().setConnectedToServer(false);
									IMCore.getInstance().setCurrentUserId(-1);
									IMCore.getInstance().getChatBaseEvent().onLinkCloseMessage(-1);
									AutoReSigninDaemon.getInstance(context).start(true);
								}
							});

							KeepAliveDaemon.getInstance(context).start(false);
							
							QoS4SendDaemon.getInstance(context).startup(true);
							QoS4ReciveDaemon.getInstance(context).startup(true);
							IMCore.getInstance().setConnectedToServer(true);
						}
						else {
							IMCore.getInstance().setConnectedToServer(false);
							IMCore.getInstance().setCurrentUserId(-1);
						}
						
						if(IMCore.getInstance().getChatBaseEvent() != null) {
							IMCore.getInstance().getChatBaseEvent().onSignInMessage(
								signinInfoRes.getUser_id(), signinInfoRes.getCode(),/*username*/"user-name");
						}
						
						break;
					}
					case ProtocalType.S.FROM_SERVER_TYPE_OF_RESPONSE$FOR$ERROR: {
						PErrorResponse errorRes = ProtocalFactory.parsePErrorResponse(pFromServer.getDataContent());
						if(errorRes.getErrorCode() == ErrorCode.S.RESPONSE_FOR_UNLOGIN) {
							IMCore.getInstance().setSignInHasInit(false);
							
							Log.e(TAG, "收到服务端的“尚未登录”的错误消息，心跳线程将停止，请应用层重新登录.");
							KeepAliveDaemon.getInstance(context).stop();

							AutoReSigninDaemon.getInstance(context).start(false);
						}
						

						if(IMCore.getInstance().getChatTransDataEvent() != null) {
							IMCore.getInstance().getChatTransDataEvent().onErrorResponse(
									errorRes.getErrorCode(), errorRes.getErrorMsg());
						}
						break;
					}
					
					default:
						Log.w(TAG, "收到的服务端消息类型："+pFromServer.getType()+"，但目前该类型客户端不支持解析和处理！");
						break;
				}
			}
			catch (Exception e) {
				Log.w(TAG, "处理消息的过程中发生了错误.", e);
			}
		}
		
		private void sendRecievedBack(final Protocal pFromServer) {
			if(pFromServer.getFp() != null) {
				new LocalUDPDataSender.SendCommonDataAsync(
						context,ProtocalFactory.createRecivedBack(
								pFromServer.getTo()
								, pFromServer.getFrom()
								, pFromServer.getFp())){
					@Override
					protected void onPostExecute(Integer code) {
						if(IMCore.DEBUG)
							Log.d(TAG, "【QoS】向"+pFromServer.getFrom()+"发送"+pFromServer.getFp()+"包的应答包成功,from="+pFromServer.getTo()+"！");
					}
				}.execute();
			}
			else {
				Log.w(TAG, "【QoS】收到"+pFromServer.getFrom()+"发过来需要QoS的包，但它的指纹码却为null！无法发应答包！");
			}
		}
	}
}
