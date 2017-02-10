package com.saladjack.im.ui.chat;

import android.content.Context;

import java.util.List;

import scut.saladjack.core.bean.FriendMessageBean;
import scut.saladjack.core.db.dao.FriendMessageDao;

/**
 * Created by saladjack on 17/1/27.
 */
public interface ChatIPresenter {
    void sendMessage(Context context, String message, int userId, boolean qos);

    void onSendMessageSuccess();

    void onSendMessageFail(Integer code);

    void insertMessageToDb(FriendMessageDao friendMessageDao, int friendId, int contentType, String content,long timeStamp);

    void onInsertMessageToDbFinish(FriendMessageBean friendMessageBean);

    void queryMessageFromDb(FriendMessageDao friendMessageDao,int friendId);

    void onQueryMessageFromDbFinish(List<FriendMessageBean> friendMessageBeen);

    void queryUserNameFromDb(FriendMessageDao friendMessageDao, int userId);

    void onQueryUserNameFromDbFinish(String userName);
}
