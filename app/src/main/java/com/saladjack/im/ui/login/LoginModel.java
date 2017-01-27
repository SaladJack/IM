package com.saladjack.im.ui.login;

import android.content.Context;
import android.widget.Toast;

import com.saladjack.im.core.LocalUDPDataSender;
import com.saladjack.im.conf.ConfigEntity;

/**
 * Created by saladjack on 17/1/27.
 */

public class LoginModel implements LoginIModel {

    LoginIPresenter presenter;

    public LoginModel(LoginIPresenter presenter) {
        this.presenter = presenter;
    }

    @Override public void login(Context context, String account, String password, String serverIP, int serverPort) {

        serverIP = ConfigEntity.serverIP;
        serverPort = ConfigEntity.serverUDPPort;

        // 异步提交登陆名和密码
        new LocalUDPDataSender.SendLoginDataAsync(context, account, password) {
            /**
             * 登陆信息发送完成后将调用本方法（注意：此处仅是登陆信息发送完成
             * ，真正的登陆结果要在异步回调中处理）。
             *
             * @param code 数据发送返回码，0 表示数据成功发出，否则是错误码
             */
            @Override
            protected void fireAfterSendLogin(int code) {
                if(code == 0) {
                    Toast.makeText(context, "数据发送成功！", Toast.LENGTH_SHORT).show();
                    //presenter.loginSuccess();
                }
                else {
                    Toast.makeText(context, "数据发送失败。错误码是："+code+"！", Toast.LENGTH_SHORT).show();
                    presenter.loginFail();

                }
            }
        }.execute();
    }
}
