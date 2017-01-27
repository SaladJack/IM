package com.saladjack.im.entity;

import java.io.Serializable;

/**
 * Created by saladjack on 17/1/27.
 */

public class Friend implements Serializable {
    private int id;
    private String name;
    private String latestContent;

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
}
