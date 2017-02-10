package com.saladjack.im.ui.profile;

import android.support.annotation.StringRes;

/**
 * Created by SaladJack on 2017/2/4.
 */
public interface ProfileView {
    void onAddFriendSuccess();
    void onAddFriendFail(@StringRes int errorResId);
}
