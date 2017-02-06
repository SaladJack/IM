package com.saladjack.im.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.saladjack.im.IMAidl;
import com.saladjack.im.IMClientManager;
import com.saladjack.im.conf.ConfigEntity;
import com.saladjack.im.core.LocalUDPDataSender;



/**
 * Created by SaladJack on 2017/2/6.
 */

public class IMService extends Service {
    private static final String TAG = "IMService";



    @Nullable @Override public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override public void onCreate() {
        super.onCreate();
        IMClientManager.getInstance(this).initIM();
        System.out.println("onCreate");
    }


    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        IMClientManager.getInstance(this).release();
    }


    protected String getCurrentProcessName() {
        String currentProcName = "";
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                currentProcName = processInfo.processName;
                break;
            }
        }
        return currentProcName;
    }



    private IMAidl.Stub mBinder = new IMAidl.Stub() {
        @Override public void signIn(String account, String password, String serverIP, int serverPort) throws RemoteException {
            ConfigEntity.serverIP = serverIP;
            ConfigEntity.serverUDPPort = serverPort;

            // 异步提交登录名和密码
            new LocalUDPDataSender.SendSigninDataAsync(IMService.this, account, password) {
                /**
                 * 登录信息发送完成后将调用本方法（注意：此处仅是登录信息发送完成
                 * ，真正的登录结果要在异步回调中处理）。
                 *
                 * @param code 数据发送返回码，0 表示数据成功发出，否则是错误码
                 */
                @Override
                protected void fireAfterSendsignin(int code) {
                    if(code == 0) {
                    }
                    else System.out.println("SignInFail");
                }
            }.execute();
        }

        @Override public void sendMessage(String message, int friendId, boolean qos) throws RemoteException {
            new LocalUDPDataSender.SendCommonDataAsync(IMService.this, message, friendId, true) {
                @Override
                protected void onPostExecute(Integer code) {
//                    if(code == 0)
////                        onSendMessageSuccess();
//                    else
////                    presenter.onSendMessageFail(code);
                }
            }.execute();
        }
    };
}
