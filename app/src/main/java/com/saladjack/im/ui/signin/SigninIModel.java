package com.saladjack.im.ui.signin;

import android.content.Context;

/**
 * Created by saladjack on 17/1/27.
 */
public interface SigninIModel {
    void signin(Context context, String account, String password, String serverIP, int serverPort);
}
