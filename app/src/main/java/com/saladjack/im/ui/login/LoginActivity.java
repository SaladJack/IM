package com.saladjack.im.ui.login;

import com.saladjack.im.IMClientManager;
import com.saladjack.im.ui.base.BaseActivity;
import com.saladjack.im.ui.message.MessageActivity;
import com.saladjack.im.utils.AppUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager.BadTokenException;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.saladjack.im.R;

import scut.saladjack.core.bean.UserBean;
import scut.saladjack.core.db.dao.UserDao;

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
	/** 登录进度提示 */
	private OnLoginProgress onLoginProgress = null;

	private LoginIPresenter presenter;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//
		this.setContentView(R.layout.demo_login_activity_layout);
		presenter = new LoginPresenter(this);
		// 界面UI基本设置
		initViews();


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
		btnLogin.setOnClickListener(v -> doLogin());
	}

	/**
	 * 登录处理。
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

			// 发送登录数据包
			if(!TextUtils.isEmpty(account)) {
				onLoginProgress.showProgressing(true);
				IMClientManager.getInstance(this).getBaseEventListener().setLoginView(this);
				presenter.login(this,account,password,serverIP,port);
			}
		}
		else {
			showToast(R.string.confirm_ip_port_not_null);
			return;
		}
	}


	@Override public void onSendMsgSuccess() {
		showToast(R.string.send_msg_success);
	}

	@Override public void onSendMsgFail(int code) {
		onLoginProgress.showProgressing(false);
		showToast(getString(R.string.send_msg_fail) + code);
	}

	@Override public void onLoginSuccess(int userId,String userName) {
		onLoginProgress.showProgressing(false);
		// 登录成功

		UserBean userBean = new UserBean(userId,userName,accountEt.getText().toString().trim(),pwdEt.getText().toString().trim());
		UserDao userDao = new UserDao();
		userDao.updateUser(userBean);
		userDao.close();

		MessageActivity.open(this);
		finish();

	}

	@Override public void onLoginFail(int errorCode) {
		showToast(R.string.login_fail);
	}

	//-------------------------------------------------------------------------- inner classes
	/**
	 * 登录进度提示和超时检测封装实现类.
	 */
	private class OnLoginProgress {
		/** 登录的超时时间定义 */
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
			progressDialogForPairing.setTitle(getString(R.string.logining));
			progressDialogForPairing.setMessage(getString(R.string.logining_please_wait));
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
		 * 登录超时后要调用的方法。
		 */
		private void onTimeout() {
			// 本观察者中由用户选择是否重试登录或者取消登录重试
			new AlertDialog.Builder(LoginActivity.this)
					.setTitle("超时了")
					.setMessage("登录超时，可能是网络故障或服务器无法连接，是否重试？")
					.setPositiveButton("重试！", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,int which) {
							// 确认要重试时（再次尝试登录）
							doLogin();
						}
					})
					.setNegativeButton("取消" , new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,int which) {
							// 不需要重试则要停止“登录中”的进度提示哦
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
		 * @param show true表示显示gui内容，否则表示结束gui内容显示
		 */
		private void showLoginProgressGUI(boolean show) {
			// 显示登录提示信息
			if(show) {
				try{
					if(parentActivity != null && !parentActivity.isFinishing())
						progressDialogForPairing.show();
				}
				catch (BadTokenException e){
					Log.e(TAG, e.getMessage(), e);
				}
			}
			// 关闭登录提示信息
			else {
				// 此if语句是为了保证延迟线程里不会因Activity已被关闭而此处却要非法地执行show的情况（此判断可趁为安全的show方法！）
				if(parentActivity != null && !parentActivity.isFinishing())
					progressDialogForPairing.dismiss();
			}
		}
	}

	/**
	 * 捕获back键
	 */
	@Override public void onBackPressed()
	{
		super.onBackPressed();

		// ** 注意：Android程序要么就别处理，要处理就一定
		//			要退干净，否则会有意想不到的问题哦！
		finish();
		System.exit(0);
	}

}
