package com.saladjack.im.core;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import com.saladjack.im.IMCore;
import com.saladjack.im.conf.ConfigEntity;
import com.saladjack.im.protocal.CharsetHelper;
import com.saladjack.im.protocal.ErrorCode;
import com.saladjack.im.protocal.Protocal;
import com.saladjack.im.protocal.ProtocalFactory;
import com.saladjack.im.utils.BetterAsyncTask;
import com.saladjack.im.utils.UDPUtils;

import android.content.Context;
import android.util.Log;

/**
 * Created by SaladJack on 2017/1/15.
 */
public class LocalUDPDataSender {
	private final static String TAG = LocalUDPDataSender.class.getSimpleName();
	private static LocalUDPDataSender instance = null;
	
	private Context context = null;
	
	public static LocalUDPDataSender getInstance(Context context) {
		if(instance == null)
			instance = new LocalUDPDataSender(context);
		return instance;
	}

	public static LocalUDPDataSender getInstance(){
		return instance;
	}
	
	private LocalUDPDataSender(Context context)
	{
		this.context = context;
	}
	

	public int sendSignin(String signinName, String signinPsw, String extra) {
		byte[] b = ProtocalFactory.createPLoginInfo(signinName, signinPsw, extra).toBytes();
		int code = send(b, b.length);
		// 登录信息成功发出时就把登录名存下来
		if(code == 0) {
			IMCore.getInstance().setCurrentAccount(signinName);
			IMCore.getInstance().setCurrentsigninPsw(signinPsw);
			IMCore.getInstance().setCurrentsigninExtra(extra);
		}
		return code;
	}
	

	public int sendSignout() {
		int code = ErrorCode.COMMON_CODE_OK;
		if(IMCore.getInstance().isSignInHasInit()) {
			byte[] b = ProtocalFactory.createPLoginoutInfo(IMCore.getInstance().getCurrentUserId()
					, IMCore.getInstance().getCurrentAccount()).toBytes();
			code = send(b, b.length);
			if(code == 0) {
				KeepAliveDaemon.getInstance(context).stop();
				IMCore.getInstance().setSignInHasInit(false);
			}
		}
		return code;
	}
	

	int sendKeepAlive() {
		byte[] b = ProtocalFactory.createPKeepAlive(IMCore.getInstance().getCurrentUserId()).toBytes();
		return send(b, b.length);
	}
	

	public int sendCommonData(byte[] dataContent, int dataLen, int to_user_id) {
		return sendCommonData(CharsetHelper.getString(dataContent, dataLen), to_user_id, false, null);
	}

	public int sendCommonData(byte[] dataContent, int dataLen, int to_user_id, boolean QoS, String fingerPrint) {
		return sendCommonData(CharsetHelper.getString(dataContent, dataLen), to_user_id, QoS, fingerPrint);
	}
	

	public int sendCommonData(String dataContentWidthStr, int to_user_id) {
		return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr
				, IMCore.getInstance().getCurrentUserId(), to_user_id));
	}

	public int sendCommonData(String dataContentWidthStr, int to_user_id, boolean QoS, String fingerPrint) {
		return sendCommonData(ProtocalFactory.createCommonData(dataContentWidthStr
				, IMCore.getInstance().getCurrentUserId(), to_user_id, QoS, fingerPrint));
	}
	

	public int sendCommonData(Protocal p) {
		if(p != null) {
			byte[] b = p.toBytes();
			int code = send(b, b.length);
			if(code == 0) {
				if(p.isQoS() && !QoS4SendDaemon.getInstance(context).exist(p.getFp()))
					QoS4SendDaemon.getInstance(context).put(p);
			}
			return code;
		}
		else
			return ErrorCode.COMMON_INVALID_PROTOCAL;
	}

	private int send(byte[] fullProtocalBytes, int dataLen) {
		if(!IMCore.getInstance().isInitialed())
			return ErrorCode.C.IM_CORE_NO_INITIALED;
		if(!IMCore.getInstance().isLocalDeviceNetworkOk()) {
			Log.e(TAG, "本地网络不能工作，send数据没有继续!");
			return ErrorCode.C.LOCAL_NETWORK_NOT_WORKING;
		}

		DatagramSocket ds = LocalUDPSocketProvider.getInstance().getLocalUDPSocket();
		if(ds != null && !ds.isConnected()) {
			try {
				if(ConfigEntity.serverIP == null) {
					Log.w(TAG, "send数据没有继续，原因是ConfigEntity.server_ip==null!");
					return ErrorCode.C.TO_SERVER_NET_INFO_NOT_SETUP;
				}
				ds.connect(new InetSocketAddress(ConfigEntity.serverIP,ConfigEntity.serverUDPPort));

			}
			catch (Exception e) {
				Log.w(TAG, "send时出错，原因是："+e.getMessage(), e);
				return ErrorCode.C.BAD_CONNECT_TO_SERVER;
			}
		}
		return UDPUtils.send(ds, fullProtocalBytes, dataLen) ? ErrorCode.COMMON_CODE_OK : ErrorCode.COMMON_DATA_SEND_FAILD;
	}
	

	public static abstract class SendCommonDataAsync extends BetterAsyncTask<Object, Integer, Integer> {
		protected Context context = null;
		protected Protocal p = null;
		
		public SendCommonDataAsync(Context context, byte[] dataContent, int dataLen, int to_user_id) {
			this(context, CharsetHelper.getString(dataContent, dataLen), to_user_id);
		}
		public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id
				, boolean QoS) {
			this(context, dataContentWidthStr, to_user_id, QoS, null);
		}
		public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id
				, boolean QoS, String fingerPrint) {
			this(context, ProtocalFactory.createCommonData(dataContentWidthStr
					, IMCore.getInstance().getCurrentUserId(), to_user_id, QoS, fingerPrint));
		}
		public SendCommonDataAsync(Context context, String dataContentWidthStr, int to_user_id)
		{
			this(context, ProtocalFactory.createCommonData(dataContentWidthStr
					, IMCore.getInstance().getCurrentUserId(), to_user_id));
		}
		public SendCommonDataAsync(Context context, Protocal p) {
			if(p == null) {
				Log.w(TAG, "无效的参数p==null!");
				return;
			}
			this.context = context;
			this.p = p;
		}

		@Override
		protected Integer doInBackground(Object... params) {
			if(p != null)
				return LocalUDPDataSender.getInstance(context).sendCommonData(p);//dataContentWidthStr, to_user_id);
			return -1;
		}

		@Override
		protected abstract void onPostExecute(Integer code);
	}
	

	public static abstract class SendSigninDataAsync extends BetterAsyncTask<Object, Integer, Integer> {
		protected Context context = null;
		protected String signinName = null;
		protected String signinPsw = null;
		protected String extra = null;


		public SendSigninDataAsync(Context context, String signinName, String signinPsw) {
			this(context, signinName, signinPsw, null);
		}

		public SendSigninDataAsync(Context context
				, String signinName, String signinPsw, String extra) {
			this.context = context;
			this.signinName = signinName;
			this.signinPsw = signinPsw;
			this.extra = extra;
		}

		@Override protected Integer doInBackground(Object... params) {
			int code = LocalUDPDataSender.getInstance(context).sendSignin(signinName, signinPsw, this.extra);
			return code;
		}

		@Override protected void onPostExecute(Integer code) {
			if(code == 0) {
				LocalUDPDataReciever.getInstance(context).startup();
			}
			else {
				Log.d(TAG, "数据发送失败, 错误码是："+code+"！");
			}
			fireAfterSendSignIn(code);
		}

		protected void fireAfterSendSignIn(int code) {

		}
	}
}
