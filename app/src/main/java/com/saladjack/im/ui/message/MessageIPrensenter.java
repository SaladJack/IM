package com.saladjack.im.ui.message;

import java.util.List;

import scut.saladjack.core.bean.FriendBean;

/**
 * Created by saladjack on 17/1/27.
 */
public interface MessageIPrensenter {

    void insertMessageToDbAndQueryFriend(int friendId, String content);

    void onInsertMessageToDbAndQueryFriendSuccess(FriendBean friendBean);

    void onInsertMessageToDbAndQueryFriendFail();

    void queryFriendWithLatestContent();

    void onQueryFriendWithLatestContentFinish(List<FriendBean> friendBeen);
}
