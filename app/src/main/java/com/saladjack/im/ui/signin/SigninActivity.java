package com.saladjack.im.ui.signin;

import com.saladjack.im.app.Constant;
import com.saladjack.im.ui.base.BaseActivity;
import com.saladjack.im.ui.home.HomeActivity;
import com.saladjack.im.ui.signup.SignUpActivity;
import com.saladjack.im.utils.AppUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager.BadTokenException;

import com.saladjack.im.R;
import com.saladjack.im.utils.DrawableUtils;

import scut.saladjack.core.bean.UserBean;
import scut.saladjack.core.db.dao.UserDao;

/**
 * Created by saladjack on 17/1/27.
 */

public class SigninActivity extends BaseActivity implements SigninView {


	public static void open(Context context) {
		Intent intent = new Intent(context,SigninActivity.class);
		context.startActivity(intent);
	}

	private static final String TAG = "SigninActivity";



	private TextInputEditText accountEditText;
	private TextInputEditText pwdEditText;
	/** 登录进度提示 */
	private OnSigninProgress onSigninProgress = null;

	private SigninIPresenter presenter;
	private BroadcastReceiver mSignInSuccessReceiver;
	private BroadcastReceiver mSignInFailReceiver;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.signin_activity);
		presenter = new SigninPresenter(this);
		// 界面UI基本设置
		initViews();
		mSignInSuccessReceiver = new BroadcastReceiver() {
			@Override public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getBundleExtra("bundle");
				int userId = bundle.getInt("userId",-1);
				String userName = bundle.getString("userName");
				onSigninSuccess(userId,userName);
			}
		};
		mSignInFailReceiver = new BroadcastReceiver() {
			@Override public void onReceive(Context context, Intent intent) {
				Bundle bundle = intent.getBundleExtra("bundle");
				int errorCode = bundle.getInt("errorCode",-1);
				onSigninFail(errorCode);
			}
		};
	}

	private void initViews() {

		accountEditText = (TextInputEditText) findViewById(R.id.account_editText);
		pwdEditText = (TextInputEditText) findViewById(R.id.psw_editText);
		//user
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Drawable[] uds = accountEditText.getCompoundDrawablesRelative();
			Drawable warpDrawable = DrawableUtils.tintDrawable(uds[0], getResources().getColorStateList(R.color.login_icon_colors));
			accountEditText.setCompoundDrawablesRelative(warpDrawable, uds[1], uds[2], uds[3]);
		} else {
			Drawable[] uds = accountEditText.getCompoundDrawables();
			Drawable warpDrawable = DrawableUtils.tintDrawable(uds[0], getResources().getColorStateList(R.color.login_icon_colors));
			accountEditText.setCompoundDrawables(warpDrawable, uds[1], uds[2], uds[3]);
		}
		//pwd
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Drawable[] pds = pwdEditText.getCompoundDrawablesRelative();
			Drawable warp = DrawableUtils.tintDrawable(pds[0], getResources().getColorStateList(R.color.login_icon_colors));
			pwdEditText.setCompoundDrawablesRelative(warp, pds[1], pds[2], pds[3]);
		} else {
			Drawable[] pds = pwdEditText.getCompoundDrawables();
			Drawable warp = DrawableUtils.tintDrawable(pds[0], getResources().getColorStateList(R.color.login_icon_colors));
			pwdEditText.setCompoundDrawables(warp, pds[1], pds[2], pds[3]);
		}

		onSigninProgress = new OnSigninProgress(this);
		findViewById(R.id.signin_btn).setOnClickListener(v -> doSignin());
		findViewById(R.id.signup_btn).setOnClickListener(v-> SignUpActivity.open(this));
	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter filterSignInSucess = new IntentFilter("onSignInSuccess");
		IntentFilter filterSignInFail = new IntentFilter("onSignInFail");
		registerReceiver(mSignInSuccessReceiver,filterSignInSucess);
		registerReceiver(mSignInFailReceiver,filterSignInFail);
	}

	@Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mSignInSuccessReceiver);
		unregisterReceiver(mSignInFailReceiver);
	}

	/**
	 * 登录处理。
	 */
	private void doSignin() {
		if(!AppUtils.checkNetworkState(this)){
			showToast(R.string.network_not_work);
			return;
		}
		// 设置服务器地址和端口号
		String serverIP = "121.42.199.202";
		String serverPort = "7901";
//		String account = accountEditText.getText().toString().trim();
//		String password = pwdEditText.getText().toString().trim();
		String account = "123456";
		String password = "123456";
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
				onSigninProgress.showProgressing(true);
				presenter.signin(this,account,password,serverIP,port);
				Constant.USER_ACCOUNT = account;
				Constant.USER_PASSWORD = password;
			}
		}
		else {
			showToast(R.string.confirm_ip_port_not_null);
		}
	}


	@Override public void onSendMsgSuccess() {
		showToast(R.string.send_msg_success);
	}

	@Override public void onSendMsgFail(int code) {
		onSigninProgress.showProgressing(false);
		showToast(getString(R.string.send_msg_fail) + code);
	}

	@Override public void onSigninSuccess(int userId, String userName) {
		onSigninProgress.showProgressing(false);
		// 登录成功
		Constant.USER_ID = userId;
		Constant.USER_NAME = userName;
		UserBean userBean = new UserBean(userId,userName, accountEditText.getText().toString().trim(), pwdEditText.getText().toString().trim());
		UserDao userDao = new UserDao();
		userDao.updateUser(userBean);
		userDao.close();

		HomeActivity.open(this);
		finish();

	}

	public void onSigninFail(int errorCode) {
		showToast(R.string.signin_fail);
	}



	//-------------------------------------------------------------------------- inner classes
	/**
	 * 登录进度提示和超时检测封装实现类.
	 */
	private class OnSigninProgress {
		/** 登录的超时时间定义 */
		private final static int RETRY_DELAY = 6000;

		private Handler handler = null;
		private Runnable runnable = null;


		private ProgressDialog progressDialogForPairing = null;
		private Activity parentActivity = null;

		public OnSigninProgress(Activity parentActivity) {
			this.parentActivity = parentActivity;
			init();
		}

		private void init() {
			progressDialogForPairing = new ProgressDialog(parentActivity);
			progressDialogForPairing.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialogForPairing.setTitle(getString(R.string.signining));
			progressDialogForPairing.setMessage(getString(R.string.signining_please_wait));
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
			new AlertDialog.Builder(SigninActivity.this)
					.setTitle("超时了")
					.setMessage("登录超时，可能是网络故障或服务器无法连接，是否重试？")
					.setPositiveButton("重试！", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,int which) {
							// 确认要重试时（再次尝试登录）
							doSignin();
						}
					})
					.setNegativeButton("取消" , new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,int which) {
							// 不需要重试则要停止“登录中”的进度提示哦
							OnSigninProgress.this.showProgressing(false);
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
				showSigninProgressGUI(true);

				// 先无论如何保证利重试检测线程在启动前肯定是处于停止状态
				handler.removeCallbacks(runnable);
				// 启动
				handler.postDelayed(runnable, RETRY_DELAY);
			}
			// 关闭进度提示
			else {
				// 无条件停掉延迟重试任务
				handler.removeCallbacks(runnable);
				showSigninProgressGUI(false);
			}
		}

		/**
		 * 进度提示时要显示或取消显示的GUI内容。
		 *
		 * @param show true表示显示gui内容，否则表示结束gui内容显示
		 */
		private void showSigninProgressGUI(boolean show) {
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
