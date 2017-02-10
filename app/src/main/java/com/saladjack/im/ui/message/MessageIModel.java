package com.saladjack.im.ui.message;

/**
 * Created by saladjack on 17/1/27.
 */
public interface MessageIModel {

    void insertMessageToDbAndQueryFriend(int friendId, String content);

    void queryFriendWithLatestContent();
}
