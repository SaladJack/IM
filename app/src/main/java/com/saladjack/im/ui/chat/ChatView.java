package com.saladjack.im.ui.chat;

import java.util.List;

import scut.saladjack.core.bean.FriendMessageBean;

/**
 * Created by saladjack on 17/1/27.
 */

public interface ChatView {
    void onSendMessageSuccess();
    void onSendMessageFail(Integer code);
    void onInsertMessageToDbFinish(FriendMessageBean friendMessageBean);
    void onQueryMessageFromDbFinish(List<FriendMessageBean> friendMessageBeen);
    void onQueryUserNameFromDbFinish(String userName);
}
