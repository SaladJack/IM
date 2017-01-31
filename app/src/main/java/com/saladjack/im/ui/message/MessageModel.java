package com.saladjack.im.ui.message;

/**
 * Created by saladjack on 17/1/27.
 */

public class MessageModel implements MessageIModel {

    MessageIPrensenter prensenter;

    public MessageModel(MessageIPrensenter prensenter) {
        this.prensenter = prensenter;
    }


}
