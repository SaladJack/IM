package scut.saladjack.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SaladJack on 2017/2/24.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static final boolean DUBUG = true;

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashTest/log/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".trace";

    private static CrashHandler sInstance;
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    private CrashHandler(){}

    public static CrashHandler getInstance(){
        if(sInstance == null)
            sInstance = new CrashHandler();
        return sInstance;
    }

    public void init(Context context){
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();

    }

    @Override public void uncaughtException(Thread thread, Throwable ex) {
        try {
            dumpExecptionToSDCard(ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ex.printStackTrace();
        if(mDefaultCrashHandler != null)
            mDefaultCrashHandler.uncaughtException(thread,ex);
        else
            Process.killProcess(Process.myPid());
    }

    private void dumpExecptionToSDCard(Throwable ex) throws IOException {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return;
        File dir = new File(PATH);
        if(!dir.exists())
            dir.mkdirs();
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace();
            pw.close();
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);
        pw.println("App Version: " + pi.versionName + "_" + pi.versionCode);

        //Android 版本号
        pw.println("OS Version: " + Build.VERSION.RELEASE + "_" + Build.VERSION.SDK_INT);

        //手机厂商
        pw.println("Vendor: " + Build.MANUFACTURER);

        //手机型号
        pw.println("Model: " + Build.MODEL);

        //CPU 架构
        pw.println("CPU ABI: " + Build.CPU_ABI);


    }
}
