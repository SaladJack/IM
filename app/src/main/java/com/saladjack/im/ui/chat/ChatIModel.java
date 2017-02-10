package com.saladjack.im.ui.chat;

import android.content.Context;

import scut.saladjack.core.db.dao.FriendMessageDao;

/**
 * Created by saladjack on 17/1/27.
 */
public interface ChatIModel {

    void sendMessage(Context context, String message, int userId, boolean qos);

    void insertMessageToDb(FriendMessageDao friendMessageDao, int friendId, int contentType, String content, long timeStamp);

    void queryMessageFromDb(FriendMessageDao friendMessageDao,int friendId);

    void queryUserNameFromDb(FriendMessageDao friendMessageDao, int userId);
}
