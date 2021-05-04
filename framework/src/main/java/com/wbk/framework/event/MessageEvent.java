package com.wbk.framework.event;

public class MessageEvent {

    private int type;

    public int getType() {
        return type;
    }

    public MessageEvent(int type) {
        this.type = type;
    }
}
