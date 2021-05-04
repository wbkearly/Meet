package com.wbk.framework.event;

import org.greenrobot.eventbus.EventBus;

public class EventManager {

    public static final int FLAG_TEST = 1000;

    public static void register(Object subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    public static void unregister(Object subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    public static void post(int flag) {
        EventBus.getDefault().post(new MessageEvent(FLAG_TEST));
    }
}
