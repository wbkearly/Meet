package com.wbk.framework.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.wbk.framework.BuildConfig;

public class SpUtil {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private volatile static SpUtil mInstance = null;

    private SpUtil() {

    }

    public static SpUtil getInstance() {
        if (mInstance == null) {
            synchronized (SpUtil.class) {
                if (mInstance == null) {
                    mInstance = new SpUtil();
                }
            }
        }
        return mInstance;
    }

    public void initSp(Context mContext) {
        sp = mContext.getSharedPreferences(BuildConfig.SP_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public Boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public void deleteKey(String key) {
        editor.remove(key);
        editor.commit();
    }
}
