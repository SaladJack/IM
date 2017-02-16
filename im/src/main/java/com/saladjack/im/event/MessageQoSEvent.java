
package com.saladjack.im.event;

import com.saladjack.im.protocal.Protocal;

import java.util.ArrayList;


/**
 * Created by SaladJack on 2017/1/16.
 */
public interface MessageQoSEvent {
	void messagesLost(ArrayList<Protocal> lostMessages);
	void messagesBeReceived(String theFingerPrint);
}
