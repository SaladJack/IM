package com.saladjack.im.ui.mine;

import scut.saladjack.core.bean.UserBean;

/**
 * Created by saladjack on 17/1/28.
 */

public interface MineView {
    void updateUserInfo(UserBean userBean);

    void onSignOutSuccess();

    void onSignOutFail(int code);
    void onFetchUserInfoFail();
}
