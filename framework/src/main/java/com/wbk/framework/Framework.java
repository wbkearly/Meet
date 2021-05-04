package com.wbk.framework;

import android.content.Context;

import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.cloud.CloudManager;
import com.wbk.framework.utils.LogUtils;
import com.wbk.framework.utils.SpUtils;

/**
 * Framework的入口
 */
public class Framework {

    private volatile static Framework mFramework;

    private Framework() {}

    public static Framework getFramework() {
        if (mFramework == null) {
            synchronized (Framework.class) {
                if (mFramework == null) {
                    mFramework = new Framework();
                }
            }
        }
        return mFramework;
    }

    public void initFramework(Context context) {
        LogUtils.i("init framework");
        SpUtils.getInstance().initSp(context);
        BmobManager.getInstance().initBmob(context);
        CloudManager.getInstance().initCloud(context);
    }
}
