package scut.saladjack.core;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

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
        String currentProcessName = getCurrentProcessName();
        if (currentProcessName.endsWith(":imservice")) {
            return;
        }
        sInstance = this;
    }

    protected String getCurrentProcessName() {
        String currentProcName = "";
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                currentProcName = processInfo.processName;
                break;
            }
        }
        return currentProcName;
    }


}
