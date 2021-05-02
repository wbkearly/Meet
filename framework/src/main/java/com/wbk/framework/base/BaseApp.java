package com.wbk.framework.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.wbk.framework.Framework;

public class BaseApp extends Application {

    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (getApplicationInfo().packageName.equals(
                getCurProcessName(getApplicationContext()))) {
            Framework.getFramework().initFramework(this);
        }
    }

    private static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess:activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
