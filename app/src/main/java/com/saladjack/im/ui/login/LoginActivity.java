package com.saladjack.im.ui.login;

import java.util.Observable;
import java.util.Observer;

import com.saladjack.im.IMClientManager;
import com.saladjack.im.ui.BaseActivity;
import com.saladjack.im.ui.friend.FriendActivity;
import com.saladjack.im.utils.AppUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.openmob.mobilesdk.android.R;

/**
 * Created by saladjack on 17/1/27.
 */

public class LoginActivity extends BaseActivity implements LoginView
{
	private static final String TAG = "LoginActivity";

	private EditText editServerIp = null;
	private EditText editServerPort = null;
	
	private EditText accountEt = null;
	private EditText pwdEt = null;
	private Button btnLogin = null;
	private TextView viewVersion = null;
	/** 登陆进度提示 */
	private OnLoginProgress onLoginProgress = null;
	/** 收到服务端的登陆完成反馈时要通知的观察者（因登陆是异步实现，本观察者将由
	 *  ChatBaseEvent 事件的处理者在收到服务端的登陆反馈后通知之） */
	private Observer onLoginSucessObserver = null;
	private LoginIPresenter presenter;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//
		this.setContentView(R.layout.demo_login_activity_layout);
		presenter = new LoginPresenter(this);
		// 界面UI基本设置
		initViews();
		initListeners();
		
		// 确保MobileIMSDK被初始化哦（整个APP生生命周期中只需调用一次哦）
		IMClientManager.getInstance(this).initMobileIMSDK();
		
		// 登陆有关的初始化工作
		initForLogin();
	}
	
	private void initViews() {
		editServerIp = (EditText)this.findViewById(R.id.serverIP_editText);
		editServerPort = (EditText)this.findViewById(R.id.serverPort_editText);
		btnLogin = (Button)this.findViewById(R.id.login_btn);
		accountEt = (EditText)this.findViewById(R.id.loginName_editText);
		pwdEt = (EditText)this.findViewById(R.id.loginPsw_editText);
		viewVersion = (TextView)this.findViewById(R.id.demo_version);
		// Demo程序的版本号
		viewVersion.setText(AppUtils.getProgrammVersion(this));

		this.setTitle("登录");
	}

	/**
	 * 捕获back键，实现调用 {@link #doExit()}方法.
	 */
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();

		// ** 注意：Android程序要么就别处理，要处理就一定
		//			要退干净，否则会有意想不到的问题哦！
		finish();
		System.exit(0);
	}

	private void initListeners()
	{
		btnLogin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				doLogin();
			}
		});
	}

	private void initForLogin()
	{
		// 实例化登陆进度提示封装类
		onLoginProgress = new OnLoginProgress(this);
		// 准备好异步登陆结果回调观察者（将在登陆方法中使用）
		onLoginSucessObserver = new Observer(){
			@Override public void update(Observable observable, Object data) {
				// * 已收到服务端登陆反馈则当然应立即取消显示登陆进度条
				onLoginProgress.showProgressing(false);
				// 服务端返回的登陆结果值
				int code = (Integer)data;
				// 登陆成功
				if(code == 0) {
					//** 提示：登陆MobileIMSDK服务器成功后的事情在此实现即可
					// 进入主界面
					FriendActivity.open(LoginActivity.this);
					// 同时关闭登陆界面
					finish();
				}
				// 登陆失败
				else {
					new AlertDialog.Builder(LoginActivity.this)
						.setTitle("友情提示")  
						.setMessage("Sorry，登陆失败，错误码="+code)
						.setPositiveButton("知道了", null) 
				.show(); 
				}
			}
		};
	}
	
	/**
	 * 登陆处理。
	 */
	private void doLogin()
	{
		if(!AppUtils.checkNetworkState(this)){
			showToast(R.string.network_not_work);
			return;
		}
		// 设置服务器地址和端口号
		String serverIP = editServerIp.getText().toString();
		String serverPort = editServerPort.getText().toString();
		String account = accountEt.getText().toString().trim();
		String password = pwdEt.getText().toString().trim();
		int port = -1;
		if(!TextUtils.isEmpty(serverIP.trim()) && !TextUtils.isEmpty(serverPort.trim())){
			serverIP = serverIP.trim();
			try {
				port = Integer.parseInt(serverPort.trim());
			}
			catch (Exception e) {
				showToast(R.string.confirm_port_legal);
				return;
			}

			// 发送登陆数据包
			if(!TextUtils.isEmpty(account)) {
				onLoginProgress.showProgressing(true);
				// * 设置好服务端反馈的登陆结果观察者（当客户端收到服务端反馈过来的登陆消息时将被通知）
				IMClientManager.getInstance(this).getBaseEventListener().setLoginOkForLaunchObserver(onLoginSucessObserver);
				presenter.login(this,account,password,serverIP,port);
			}
		}
		else {
			showToast(R.string.confirm_ip_port_not_null);
			return;
		}
	}


	@Override public void loginSuccess() {

	}


	@Override public void loginFail() {
		onLoginProgress.showProgressing(false);
	}

	//-------------------------------------------------------------------------- inner classes
	/**
	 * 登陆进度提示和超时检测封装实现类.
	 */
	private class OnLoginProgress {
		/** 登陆的超时时间定义 */
		private final static int RETRY_DELAY = 6000;
		
		private Handler handler = null;
		private Runnable runnable = null;

		
		private ProgressDialog progressDialogForPairing = null;
		private Activity parentActivity = null;

		public OnLoginProgress(Activity parentActivity) {
			this.parentActivity = parentActivity;
			init();
		}

		private void init() {
			progressDialogForPairing = new ProgressDialog(parentActivity);
			progressDialogForPairing.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialogForPairing.setTitle("登陆中"); 
			progressDialogForPairing.setMessage("正在登陆中，请稍候。。。"); 
			progressDialogForPairing.setCanceledOnTouchOutside(false);
			
			handler = new Handler();
			runnable = new Runnable(){
				@Override
				public void run() {
					onTimeout();
				}
			};
		}

		/**
		 * 登陆超时后要调用的方法。
		 */
		private void onTimeout() {
			// 本观察者中由用户选择是否重试登陆或者取消登陆重试
			new AlertDialog.Builder(LoginActivity.this)
				.setTitle("超时了")  
				.setMessage("登陆超时，可能是网络故障或服务器无法连接，是否重试？")
				.setPositiveButton("重试！", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog,int which) {
						// 确认要重试时（再次尝试登陆）
						doLogin();
					}
				}) 
				.setNegativeButton("取消" , new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog,int which) {
						// 不需要重试则要停止“登陆中”的进度提示哦
						OnLoginProgress.this.showProgressing(false);
					}
				})
			.show(); 
		}

		/**
		 * 显示进度提示.
		 * 
		 * @param show
		 */
		public void showProgressing(boolean show) {
			// 显示进度提示的同时即启动超时提醒线程
			if(show) {
				showLoginProgressGUI(true);

				// 先无论如何保证利重试检测线程在启动前肯定是处于停止状态
				handler.removeCallbacks(runnable);
				// 启动
				handler.postDelayed(runnable, RETRY_DELAY);
			}
			// 关闭进度提示
			else {
				// 无条件停掉延迟重试任务
				handler.removeCallbacks(runnable);

				showLoginProgressGUI(false);
			}
		}

		/**
		 * 进度提示时要显示或取消显示的GUI内容。
		 * 
		 * @param show true表示显示gui内容，否则表示结速gui内容显示
		 */
		private void showLoginProgressGUI(boolean show) {
			// 显示登陆提示信息
			if(show) {
				try{
					if(parentActivity != null && !parentActivity.isFinishing())
						progressDialogForPairing.show();
				}
				catch (BadTokenException e){
					Log.e(TAG, e.getMessage(), e);
				}
			}
			// 关闭登陆提示信息
			else {
				// 此if语句是为了保证延迟线程里不会因Activity已被关闭而此处却要非法地执行show的情况（此判断可趁为安全的show方法哦！）
				if(parentActivity != null && !parentActivity.isFinishing())
					progressDialogForPairing.dismiss();
			}
		}
	}
}
