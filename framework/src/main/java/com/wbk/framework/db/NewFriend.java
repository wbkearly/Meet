package com.wbk.framework.db;

import org.litepal.crud.LitePalSupport;

public class NewFriend extends LitePalSupport {

    // 留言
    private String msg;

    // 对方id
    private String userId;

    private long saveTime;

    // 状态 -1：待确认 0：同意 1：拒绝
    private int response = -1;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }
}
