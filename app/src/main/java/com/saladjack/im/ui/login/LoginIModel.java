package com.saladjack.im.ui.login;

import android.content.Context;

/**
 * Created by saladjack on 17/1/27.
 */
public interface LoginIModel {
    void login(Context context, String account, String password, String serverIP, int serverPort);
}
