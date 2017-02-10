package com.saladjack.im.ui.chat;

import android.content.Context;

import java.util.List;

import scut.saladjack.core.bean.FriendMessageBean;
import scut.saladjack.core.db.dao.FriendMessageDao;

/**
 * Created by saladjack on 17/1/27.
 */

public class ChatPresenter implements ChatIPresenter {
    private final ChatView view;
    private final ChatIModel chatModel;

    public ChatPresenter(ChatView view) {
        this.view = view;
        chatModel = new ChatModel(this);
    }
    @Override public void sendMessage(Context context, String message, int userId, boolean qos) {
        chatModel.sendMessage(context,message,userId,qos);
    }

    @Override public void onSendMessageSuccess() {
        view.onSendMessageSuccess();
    }

    @Override public void onSendMessageFail(Integer code) {
        view.onSendMessageFail(code);
    }

    @Override public void insertMessageToDb(FriendMessageDao friendMessageDao, int friendId, int contentType, String content, long timeStamp) {
        chatModel.insertMessageToDb(friendMessageDao,friendId,contentType,content, timeStamp);
    }

    @Override public void onInsertMessageToDbFinish(FriendMessageBean friendMessageBean) {
        view.onInsertMessageToDbFinish(friendMessageBean);
    }

    @Override public void queryMessageFromDb(FriendMessageDao friendMessageDao,int friendId) {
        chatModel.queryMessageFromDb(friendMessageDao,friendId);
    }

    @Override public void onQueryMessageFromDbFinish(List<FriendMessageBean> friendMessageBeen) {
        view.onQueryMessageFromDbFinish(friendMessageBeen);
    }

    @Override public void queryUserNameFromDb(FriendMessageDao friendMessageDao, int userId) {
        chatModel.queryMessageFromDb(friendMessageDao,userId);
    }

    @Override public void onQueryUserNameFromDbFinish(String userName) {
        view.onQueryUserNameFromDbFinish(userName);
    }
}
