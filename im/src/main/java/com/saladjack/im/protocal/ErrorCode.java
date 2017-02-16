package com.saladjack.im.protocal;

/**
 * 错误码常量表.
 *
 *
 * Created by saladjack on 17/2/16.
 */
public class ErrorCode {
	/** 一切正常 */
	public final static int COMMON_CODE_OK = 0;
	/** 客户端尚未登陆 */
	public final static int COMMON_NO_LOGIN = 1;
	/** 未知错误 */
	public final static int COMMON_UNKNOW_ERROR = 2;

	/** 数据发送失败 */
	public final static int COMMON_DATA_SEND_FAILD = 3;

	/** 无效的Protocal对象 */
	public final static int COMMON_INVALID_PROTOCAL = 4;

	/** 由客户端产生的错误码 */
	public class C {
		/** 与服务端的连接已断开 */
		public final static int BREOKEN_CONNECT_TO_SERVER = 201;

		/** 与服务端的网络连接失败 */
		public final static int BAD_CONNECT_TO_SERVER = 202;

		/** imCore尚未初始化 */
		public final static int IM_CORE_NO_INITIALED = 203;

		/** 本地网络不可用（未打开） */
		public final static int LOCAL_NETWORK_NOT_WORKING = 204;

		/** 要连接的服务端网络参数未设置 */
		public final static int TO_SERVER_NET_INFO_NOT_SETUP = 205;
	}

	/** 由服务端产生的错误码 */
	public class S {
		/** 客户端尚未登陆，请重新登陆 */
		public final static int RESPONSE_FOR_UNLOGIN = 301;
		public final static int LOGIN_FAIL = 1025;
		public final static int REGISTER_FAIL$ACCOUNT_ALREADY_EXIST = 1026;
	}

}