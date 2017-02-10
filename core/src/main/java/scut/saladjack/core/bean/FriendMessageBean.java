package scut.saladjack.core.bean;

/**
 * Created by SaladJack on 2017/2/9.
 */

public class FriendMessageBean {
    private int friendId;
    private int messageType;
    private String message;
    private long timeStamp;

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "FriendMessageBean{" +
                "friendId=" + friendId +
                ", messageType=" + messageType +
                ", message='" + message + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
