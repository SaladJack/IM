package com.saladjack.im.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;

/**
 * Created by saladjack on 17/1/27.
 */

public class AppUtils {
    /**
     * 获取APP版本信息.
     */
    public static String getProgramVersion(Context context)
    {
        PackageInfo info;
        try
        {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return "N/A";
        }
    }


    /**
     * 查看网络是否已连接
     * @return
     */
    public static boolean checkNetworkState(Context context)
    {
        boolean flag = false;
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if(manager.getActiveNetworkInfo() != null)
        {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }

        return flag;
    }

    public static String getPackageName(){
        return "com.saladjack.im";
    }

    public static String getCurrentProcessName(Context context){
        String currentProcessName = "";
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                currentProcessName = processInfo.processName;
                break;
            }
        }
        return currentProcessName;
    }

    public static void sendBroadCast(Context context, Bundle bundle,String action){
        Intent intent = new Intent(action);
        intent.putExtra("bundle",bundle);
        context.sendBroadcast(intent);
    }

}
