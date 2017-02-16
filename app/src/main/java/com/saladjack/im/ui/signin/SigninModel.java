package com.saladjack.im.ui.signin;

import android.content.Context;
import android.os.RemoteException;

import com.saladjack.im.app.IMApp;


/**
 * Created by saladjack on 17/1/27.
 */

public class SigninModel implements SigninIModel {

    SigninIPresenter presenter;

    public SigninModel(SigninIPresenter presenter) {
        this.presenter = presenter;
    }

    @Override public void signin(Context context, String account, String password, String serverIP, int serverPort) {

        try {
            IMApp.getInstance().getBinder().signIn(account,password,serverIP,serverPort);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        ConfigEntity.serverIP = serverIP;
//        ConfigEntity.serverUDPPort = serverPort;
//
//        // 异步提交登录名和密码
//        new LocalUDPDataSender.SendSigninDataAsync(context, account, password) {
//            /**
//             * 登录信息发送完成后将调用本方法（注意：此处仅是登录信息发送完成
//             * ，真正的登录结果要在异步回调中处理）。
//             *
//             * @param code 数据发送返回码，0 表示数据成功发出，否则是错误码
//             */
//            @Override
//            protected void fireAfterSendSignIn(int code) {
//                if(code == 0) presenter.onSendMsgSuccess();
//                else          presenter.onSendMsgFail(code);
//            }
//        }.execute();

//        Observable.create((Observable.OnSubscribe<Integer>) subscriber ->subscriber.onNext(LocalUDPDataSender.getInstance().sendSignin(account,password,null)))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe();
    }
}
