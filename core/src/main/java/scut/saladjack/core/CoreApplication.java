package scut.saladjack.core;

import android.app.Application;

/**
 * Created by saladjack on 17/1/28.
 */

public class CoreApplication extends Application {
    private static CoreApplication sInstance;
    public static CoreApplication getInstance(){
        return sInstance;
    }

    @Override public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

}
