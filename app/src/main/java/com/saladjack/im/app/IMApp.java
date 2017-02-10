package com.saladjack.im.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.saladjack.im.IMAidl;
import com.saladjack.im.service.IMService;

import scut.saladjack.core.CoreApplication;

/**
 * Created by saladjack on 17/1/28.
 */

public class IMApp extends CoreApplication {
    private static IMApp sInstance;
    public static IMApp getInstance(){
        return sInstance;
    }

    private IMAidl mBinder;


    @Override public void onCreate() {
        super.onCreate();
        String currentProcessName = getCurrentProcessName();
        System.out.println(currentProcessName + "IMApp Application onCreate");
        if(currentProcessName.endsWith(":imservice")) {

        }else{
            ServiceConnection connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mBinder = IMAidl.Stub.asInterface(service);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };
            Intent intent = new Intent(this, IMService.class);
            startService(intent);
            bindService(intent, connection, BIND_AUTO_CREATE);

            sInstance = this;
        }
    }

    public IMAidl getBinder(){
        return mBinder;
    }

}
