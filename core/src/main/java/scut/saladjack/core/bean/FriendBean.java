package scut.saladjack.core.bean;

import java.io.Serializable;

/**
 * Created by saladjack on 17/1/27.
 */

public class FriendBean implements Serializable {
    private int id;
    private String name;
    private String latestContent;

    public FriendBean() {
    }

    public FriendBean(int id, String name, String latestContent) {
        this.id = id;
        this.name = name;
        this.latestContent = latestContent;
    }

    public FriendBean(UserBean userBean) {
        id = userBean.getUserId();
        name = userBean.getUserName();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatestContent() {
        return latestContent;
    }

    public void setLatestContent(String latestContent) {
        this.latestContent = latestContent;
    }

    @Override
    public String toString() {
        return "FriendBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", latestContent='" + latestContent + '\'' +
                '}';
    }
}
