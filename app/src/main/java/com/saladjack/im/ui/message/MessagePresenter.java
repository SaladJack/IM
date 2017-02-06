package com.saladjack.im.ui.message;

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

    @Override public void queryFriend(int friendId, String content) {
        model.queryFriend(friendId,content);
    }

    @Override public void onQueryFriendSuccess(FriendBean friendBean) {
        view.onQueryFriendSuccess(friendBean);
    }

    @Override public void onQueryFriendFail() {
        view.onQueryFriendFail();

    }
}
