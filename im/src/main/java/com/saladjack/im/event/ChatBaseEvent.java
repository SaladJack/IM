package com.saladjack.im.event;

/**
 * Created by SaladJack on 2017/1/15.
 */
public interface ChatBaseEvent {
	void onSignInMessage(int dwUserId, int dwErrorCode, String username);
	void onLinkCloseMessage(int dwErrorCode);
}
