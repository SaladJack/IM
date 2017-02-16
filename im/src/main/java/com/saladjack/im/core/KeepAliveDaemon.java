
package com.saladjack.im.core;

import java.util.Observer;

import com.saladjack.im.IMCore;
import com.saladjack.im.utils.BetterAsyncTask;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
/**
 * Created by SaladJack on 2017/1/16.
 */
public class KeepAliveDaemon {
	private final static String TAG = KeepAliveDaemon.class.getSimpleName();

	public static int NETWORK_CONNECTION_TIME_OUT = 10 * 1000;


	public static int KEEP_ALIVE_INTERVAL = 3000;//1000;

	private Handler handler = null;
	private Runnable runnable = null;

	private boolean keepAliveRunning = false;
	private long lastGetKeepAliveResponseFromServerTimstamp = 0;
	
	private static KeepAliveDaemon instance = null;
	
	/** 网络断开事件观察者 */
	private Observer networkConnectionLostObserver = null;
	
	private boolean _excuting = false;
	
	private Context context = null;
	
	public static KeepAliveDaemon getInstance(Context context)
	{
		if(instance == null)
			instance = new KeepAliveDaemon(context);
		return instance;
	}
	
	private KeepAliveDaemon(Context context)
	{
		this.context = context;
		init();
	}
	
	private void init()
	{
		handler = new Handler();
		runnable = new Runnable(){
			@Override
			public void run() {
				// 极端情况下本次循环内可能执行时间超过了时间间隔，此处是防止在前一
				// 次还没有运行完的情况下又重复过执行，从而出现无法预知的错误
				if(!_excuting) {
					// Handler的机制是在主线程中执行的，所以此处在放在另一个线程里，否则会报错
					new BetterAsyncTask<Object, Integer, Integer>()
					{
						private boolean willStop = false;
						
						@Override
						protected Integer doInBackground(Object... params) {
							_excuting = true;
							if(IMCore.DEBUG)
								Log.d(TAG, "心跳线程执行中...");
							int code = LocalUDPDataSender.getInstance(context).sendKeepAlive();
							return code;
						}

						@Override
						protected void onPostExecute(Integer code) {
							// 首先执行Keep Alive心跳包时，把此时的时间作为第1次收到服务响应的时间（初始化）
							boolean isInitialedForKeepAlive = (lastGetKeepAliveResponseFromServerTimstamp == 0);
							if(code == 0 && lastGetKeepAliveResponseFromServerTimstamp == 0)
								lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();

							if(!isInitialedForKeepAlive) {
								long now = System.currentTimeMillis();
								if(now - lastGetKeepAliveResponseFromServerTimstamp >= NETWORK_CONNECTION_TIME_OUT) {
									stop();
									if(networkConnectionLostObserver != null)
										networkConnectionLostObserver.update(null, null);

									willStop = true;
								}
							}
							
							//
							_excuting = false;
							if(!willStop)
								handler.postDelayed(runnable, KEEP_ALIVE_INTERVAL);
						}
					}.executeParallel();
				}
			}
		};
	}
	

	public void stop() {
		handler.removeCallbacks(runnable);
		keepAliveRunning = false;
		lastGetKeepAliveResponseFromServerTimstamp = 0;
	}
	

	public void start(boolean immediately) {
		stop();
		handler.postDelayed(runnable, immediately ? 0 : KEEP_ALIVE_INTERVAL);
		keepAliveRunning = true;
	}
	

	public boolean isKeepAliveRunning()
	{
		return keepAliveRunning;
	}
	

	public void updateGetKeepAliveResponseFromServerTimstamp() {
		lastGetKeepAliveResponseFromServerTimstamp = System.currentTimeMillis();
	}


	public void setNetworkConnectionLostObserver(Observer networkConnectionLostObserver) {
		this.networkConnectionLostObserver = networkConnectionLostObserver;
	}
}
