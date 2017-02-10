package com.saladjack.im.ui.message;

import java.util.List;

import scut.saladjack.core.bean.FriendBean;

/**
 * Created by saladjack on 17/1/27.
 */

public class MessagePresenter implements MessageIPrensenter {

    private final MessageIModel model;
    private MessageView view;

    public MessagePresenter(MessageView view) {
        this.view = view;
        model = new MessageModel(this);
    }

    @Override public void insertMessageToDbAndQueryFriend(int friendId, String content) {
        model.insertMessageToDbAndQueryFriend(friendId,content);
    }

    @Override public void onInsertMessageToDbAndQueryFriendSuccess(FriendBean friendBean) {
        view.onQueryFriendSuccess(friendBean);
    }

    @Override public void onInsertMessageToDbAndQueryFriendFail() {
        view.onQueryFriendFail();
    }


    @Override
    public void queryFriendWithLatestContent() {
        model.queryFriendWithLatestContent();
    }

    @Override
    public void onQueryFriendWithLatestContentFinish(List<FriendBean> friendBeen) {
        view.onQueryFriendWithLatestContentFinish(friendBeen);
    }

}
