package com.saladjack.im.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.saladjack.im.IMAidl;
import com.saladjack.im.IMClientManager;
import com.saladjack.im.conf.ConfigEntity;
import com.saladjack.im.core.AutoReSigninDaemon;
import com.saladjack.im.core.KeepAliveDaemon;
import com.saladjack.im.core.LocalUDPDataSender;
import com.saladjack.im.utils.AppUtils;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by SaladJack on 2017/2/6.
 */

public class IMService extends Service {
    private static final String TAG = "IMService";
    private final static int GRAY_SERVICE_ID = 1001;
    private int userId = -1;
    @Nullable @Override public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override public void onCreate() {
        super.onCreate();
        IMClientManager.getInstance(this).initIM();
    }


    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, new Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, new Notification());
        }

        return START_STICKY;
    }


    @Override public void onDestroy() {
        super.onDestroy();
        IMClientManager.getInstance(this).release();
    }

    private IMAidl.Stub mBinder = new IMAidl.Stub() {
        @Override public void signIn(String account, String password, String serverIP, int serverPort) throws RemoteException {
            ConfigEntity.serverIP = serverIP;
            ConfigEntity.serverUDPPort = serverPort;

            new LocalUDPDataSender.SendSigninDataAsync(IMService.this, account, password) {
                @Override
                protected void fireAfterSendSignIn(int code) {
                    if(code == 0) {
                    }
                    else System.out.println("SignInFail");
                }
            }.execute();
        }

        @Override public void signOut() throws RemoteException {
            Observable.create((Observable.OnSubscribe<Integer>) subscriber -> subscriber.onNext(LocalUDPDataSender.getInstance(IMService.this).sendSignout()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(code -> {
                            Bundle bundle = new Bundle();
                            bundle.putInt("code",code);
                            AppUtils.sendBroadCast(IMService.this,bundle,"signout");
                    });
        }

        @Override public void sendMessage(String message, int friendId, boolean qos) throws RemoteException {
            new LocalUDPDataSender.SendCommonDataAsync(IMService.this, message, friendId, true) {
                @Override protected void onPostExecute(Integer code) {
//                    if(code == 0)
////                        onSendMessageSuccess();
//                    else
////                    presenter.onSendMessageFail(code);
                }
            }.execute();
        }

        @Override
        public int getUserId() throws RemoteException {
//            return KeepAliveDaemon.getInstance(IMService.this).isKeepAliveRunning() || AutoReSigninDaemon.getInstance(IMService.this).isAutoReSignInRunning();
            return userId;
        }
    };

    public static final class GrayInnerService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
    }

    public void setUserId(int userId){
        this.userId = userId;
    }
}
