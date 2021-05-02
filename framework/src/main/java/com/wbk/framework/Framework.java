package com.wbk.framework;

import android.content.Context;

import com.wbk.framework.bmob.BmobManager;
import com.wbk.framework.helper.UploadHelper;
import com.wbk.framework.utils.LogUtil;
import com.wbk.framework.utils.SpUtil;

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
        LogUtil.i("init framework");
        SpUtil.getInstance().initSp(context);
        BmobManager.getInstance().initBmob(context);
    }
}
