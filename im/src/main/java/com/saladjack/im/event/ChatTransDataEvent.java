package com.saladjack.im.event;

/**
 * Created by SaladJack on 2017/1/16.
 */
public interface ChatTransDataEvent {
	void onTransBuffer(String fingerPrintOfProtocal, int dwUserid, String dataContent);
	void onErrorResponse(int errorCode, String errorMsg);
}