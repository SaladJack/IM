package com.saladjack.im;

import com.saladjack.im.core.AutoReSigninDaemon;
import com.saladjack.im.core.KeepAliveDaemon;
import com.saladjack.im.core.LocalUDPDataReciever;
import com.saladjack.im.core.LocalUDPSocketProvider;
import com.saladjack.im.core.QoS4ReciveDaemon;
import com.saladjack.im.core.QoS4SendDaemon;
import com.saladjack.im.event.ChatBaseEvent;
import com.saladjack.im.event.ChatTransDataEvent;
import com.saladjack.im.event.MessageQoSEvent;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class IMCore {
    private final static String TAG = IMCore.class.getSimpleName();

    public static boolean DEBUG = true;

    public static boolean autoReSignIn = true;

    private static IMCore instance = null;

    private boolean init = false;

    private boolean localDeviceNetworkOk = true;

    private boolean connectedToServer = true;

    private boolean signInHasInit = false;

    private int currentUserId = -1;

    private String currentAccount = null;

    private String currentsigninPsw = null;

    private String currentsigninExtra = null;

    private ChatBaseEvent chatBaseEvent = null;

    private ChatTransDataEvent chatTransDataEvent = null;

    private MessageQoSEvent messageQoSEvent = null;

    private Context context = null;


    public static IMCore getInstance() {
        if(instance == null)
            instance = new IMCore();
        return instance;
    }

    private IMCore() {
    }


    public void init(Context context) {
        if(!init) {
            if(context == null) throw new IllegalArgumentException("context can't be null!");
            if(context instanceof Application)
                this.context = context;
            else
                this.context = context.getApplicationContext();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            this.context.registerReceiver(networkConnectionStatusBroadcastReceiver, intentFilter);
            init = true;
        }
    }

    public void release() {
        AutoReSigninDaemon.getInstance(context).stop(); // 2014-11-08 add by Jack Jiang
        QoS4SendDaemon.getInstance(context).stop();
        KeepAliveDaemon.getInstance(context).stop();
        LocalUDPDataReciever.getInstance(context).stop();
        QoS4ReciveDaemon.getInstance(context).stop();
        LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();

        try {
            context.unregisterReceiver(networkConnectionStatusBroadcastReceiver);
        }
        catch (Exception e) {
            Log.w(TAG, e.getMessage(), e);
        }

        init = false;

        this.setSignInHasInit(false);
        this.setConnectedToServer(false);
    }


    public int getCurrentUserId()
    {
        return currentUserId;
    }

    public IMCore setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
        return this;
    }


    public String getCurrentAccount() {
        return currentAccount;
    }

    public IMCore setCurrentAccount(String currentAccount) {
        this.currentAccount = currentAccount;
        return this;
    }


    public String getCurrentsigninPsw()
    {
        return currentsigninPsw;
    }

    public void setCurrentsigninPsw(String currentsigninPsw) {
        this.currentsigninPsw = currentsigninPsw;
    }

    public String getCurrentsigninExtra()
    {
        return currentsigninExtra;
    }

    public IMCore setCurrentsigninExtra(String currentsigninExtra) {
        this.currentsigninExtra = currentsigninExtra;
        return this;
    }


    public boolean isSignInHasInit()
    {
        return signInHasInit;
    }

    public IMCore setSignInHasInit(boolean signinHasInit) {
        this.signInHasInit = signinHasInit;

        return this;
    }


    public boolean isConnectedToServer()
    {
        return connectedToServer;
    }

    public void setConnectedToServer(boolean connectedToServer) {
        this.connectedToServer = connectedToServer;
    }


    public boolean isInitialed()
    {
        return this.init;
    }

    public boolean isLocalDeviceNetworkOk()
    {
        return localDeviceNetworkOk;
    }

    public void setChatBaseEvent(ChatBaseEvent chatBaseEvent) {
        this.chatBaseEvent = chatBaseEvent;
    }

    public ChatBaseEvent getChatBaseEvent()
    {
        return chatBaseEvent;
    }

    public void setChatTransDataEvent(ChatTransDataEvent chatTransDataEvent) {
        this.chatTransDataEvent = chatTransDataEvent;
    }

    public ChatTransDataEvent getChatTransDataEvent()
    {
        return chatTransDataEvent;
    }

    public void setMessageQoSEvent(MessageQoSEvent messageQoSEvent) {
        this.messageQoSEvent = messageQoSEvent;
    }

    public MessageQoSEvent getMessageQoSEvent()
    {
        return messageQoSEvent;
    }

    private final BroadcastReceiver networkConnectionStatusBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!(mobNetInfo != null && mobNetInfo.isConnected())
                    && !(wifiNetInfo != null && wifiNetInfo.isConnected())) {
                LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
            }
            else {
                if(IMCore.DEBUG)
                    Log.e(TAG, "【本地网络通知】检测本地网络已连接上了!");
                localDeviceNetworkOk = true;
                LocalUDPSocketProvider.getInstance().closeLocalUDPSocket();
            }
        }
    };
}
