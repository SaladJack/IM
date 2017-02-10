package com.saladjack.im.ui.friend;

import java.util.List;

import scut.saladjack.core.bean.UserBean;

/**
 * Created by SaladJack on 2017/2/4.
 */
public interface FriendView {

    void onLoadFriendsSuccess(List<UserBean> userBeen);

    void onLoadFriendsFail();
}
