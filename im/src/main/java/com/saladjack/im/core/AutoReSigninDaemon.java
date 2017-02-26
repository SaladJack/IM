
package com.saladjack.im.core;

import com.saladjack.im.IMCore;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.saladjack.im.utils.BetterAsyncTask;

/**
 * Created by SaladJack on 2017/1/15.
 */
public class AutoReSigninDaemon {
	private final static String TAG = AutoReSigninDaemon.class.getSimpleName();
	

	public static int AUTO_RE$SIGNIN_INTERVAL = 2000;

	private Handler handler = null;
	private Runnable runnable = null;
	

	private boolean autoResigninRunning = false;
	
	private boolean executing = false;
	
	private static AutoReSigninDaemon instance = null;
	
	private Context context = null;
	
	public static AutoReSigninDaemon getInstance(Context context) {
		if(instance == null)
			instance = new AutoReSigninDaemon(context);
		return instance;
	}
	
	private AutoReSigninDaemon(Context context) {
		this.context = context;
		init();
	}
	
	private void init() {
		handler = new Handler();
		runnable = new Runnable(){
			@Override public void run() {
				if(!executing) {
					// Handler的机制是在主线程中执行的，所以此处在放在另一个线程里，否则会报错哦
					new BetterAsyncTask<Object, Integer, Integer>(){
						@Override protected Integer doInBackground(Object... params) {
							executing = true;
							if(IMCore.DEBUG)
								Log.d(TAG, "自动重新登录线程执行中, autoReSignIn?"+ IMCore.autoReSignIn +"...");
							int code = -1;
							if(IMCore.autoReSignIn) {
								code = LocalUDPDataSender.getInstance(context).sendSignin(
										IMCore.getInstance().getCurrentAccount()
										, IMCore.getInstance().getCurrentsigninPsw()
										, IMCore.getInstance().getCurrentsigninExtra());
							}
							return code;
						}

						@Override protected void onPostExecute(Integer result) {
							if(result == 0) {
								LocalUDPDataReciever.getInstance(context).startup();
							}
							executing = false;
							handler.postDelayed(runnable, AUTO_RE$SIGNIN_INTERVAL);
						}
					}.executeParallel();
				}
			}
		};
	}

	public void stop() {
		handler.removeCallbacks(runnable);
		autoResigninRunning = false;
	}

	public void start(boolean immediately) {
		stop();
		handler.postDelayed(runnable, immediately ? 0 : AUTO_RE$SIGNIN_INTERVAL);
		autoResigninRunning = true;
	}

	public boolean isAutoReSignInRunning()
	{
		return autoResigninRunning;
	}
}
