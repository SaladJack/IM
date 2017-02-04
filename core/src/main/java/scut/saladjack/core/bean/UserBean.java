package scut.saladjack.core.bean;

import java.io.Serializable;

/**
 * Created by saladjack on 17/1/28.
 */

public class UserBean implements Serializable {
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



}
