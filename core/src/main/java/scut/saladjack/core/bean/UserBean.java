package scut.saladjack.core.bean;

/**
 * Created by saladjack on 17/1/28.
 */

public class UserBean {
    private int uid;
    private String userName;
    private String account;
    private String password;

    public UserBean(){

    }
    public UserBean(int uid, String userName, String account, String password) {
        this.uid = uid;
        this.userName = userName;
        this.account = account;
        this.password = password;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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
