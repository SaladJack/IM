package com.saladjack.im.ui.message;

import scut.saladjack.core.bean.FriendBean;

/**
 * Created by SaladJack on 2017/2/6.
 */

public interface MessageView {
    void onQueryFriendSuccess(FriendBean friendBean);

    void onQueryFriendFail();
}
