package scut.saladjack.core.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by saladjack on 17/1/28.
 */

public class UserBean implements Parcelable {
    private int userId;
    private String userName;
    private String account;
    private String password;

    public UserBean() {
    }

    public UserBean(FindFriendsResult findFriendsResult){
        userId = findFriendsResult.getUserId();
        userName = findFriendsResult.getUserName();
    }

    public UserBean(FriendBean friendBean){
        userId = friendBean.getId();
        userName = friendBean.getName();
    }
    public UserBean(int userId, String userName, String account, String password) {
        this.userId = userId;
        this.userName = userName;
        this.account = account;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.account);
        dest.writeString(this.password);
    }

    protected UserBean(Parcel in) {
        this.userId = in.readInt();
        this.userName = in.readString();
        this.account = in.readString();
        this.password = in.readString();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
