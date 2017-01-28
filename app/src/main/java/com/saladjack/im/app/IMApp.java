package com.saladjack.im.app;

import android.app.Application;

import com.saladjack.im.IMClientManager;

/**
 * Created by saladjack on 17/1/28.
 */

public class IMApp extends Application {
    private static IMApp sInstance;
    public static IMApp getInstance(){
        return sInstance;
    }

    @Override public void onCreate() {
        super.onCreate();
        sInstance = this;
        IMClientManager.getInstance(this).initIM();
    }


}
