package com.wbk.framework.utils;

/**
 * 时间转换类
 */
public class TimeUtil {

    /**
     * 转换毫秒格式 HH:mm:ss
     */
    public static String formatDuring(long ms) {
        long hours = ((ms + + 28800000) % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (ms % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (ms % (60 * 1000)) / 1000;
        String h = hours + "";
        if (hours < 10) {
            h = "0" + hours;
        }
        String m = minutes + "";
        if (minutes < 10) {
            m = "0" + minutes;
        }
        String s = seconds + "";
        if (seconds < 10) {
            s = "0" + seconds;
        }
        return h + ":" + m + ":" + s;
    }
}
