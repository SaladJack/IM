package com.saladjack.im.ui.message;

import scut.saladjack.core.bean.FriendBean;

/**
 * Created by saladjack on 17/1/27.
 */
public interface MessageIPrensenter {

    void queryFriend(int friendId, String content);

    void onQueryFriendSuccess(FriendBean friendBean);

    void onQueryFriendFail();
}
