package com.wbk.framework.bmob;

import android.content.Context;

import com.wbk.framework.helper.UploadHelper;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class BmobManager {

    private static final String BMOB_SDK_ID = "a0d2ea5bff839224dfbfeb6680ec8c69";
    private static final String BMOB_NEW_DOMAIN = "http://sdk.wbkearly.tk/8/";
    private volatile static BmobManager mInstance = null;

    private BmobManager() {
    }

    public static BmobManager getInstance() {
        if (mInstance == null) {
            synchronized (BmobManager.class) {
                if (mInstance == null) {
                    mInstance = new BmobManager();
                }
            }
        }
        return mInstance;
    }

    public boolean isLogin() {
        return BmobUser.isLogin();
    }

    /**
     * 初始化Bmob
     */
    public void initBmob(Context context) {
        Bmob.resetDomain(BMOB_NEW_DOMAIN);
        Bmob.initialize(context, BMOB_SDK_ID);
    }

    /**
     * 获取本地对象
     */
    public IMUser getUser() {
        return BmobUser.getCurrentUser(IMUser.class);
    }
    /**
     * 发送短信验证码
     */
    public void requestSMS(String phone, QueryListener<Integer> listener) {
        BmobSMS.requestSMSCode(phone, "", listener);
    }

    public void signOrLoginByMobilePhone(String phone, String code, LogInListener<IMUser> listener) {
        BmobUser.signOrLoginByMobilePhone(phone, code, listener);
    }

    public void updateUserInfo(String nickname, String portraitUrl, OnUploadListener listener) {
        // 更新用户信息
        IMUser imUser = getUser();
        imUser.setNickname(nickname);
        imUser.setPortrait(portraitUrl);
        imUser.setTokenNickname(nickname);
        imUser.setTokenPortrait(portraitUrl);
        imUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    listener.onUploadSuccess();
                } else {
                    listener.onUpdateFail(e);
                }
            }
        });
    }

    public interface OnUploadListener {
        void onUploadSuccess();

        void onUpdateFail(BmobException e);
    }

    public void queryUserByPhone(String phone, FindListener<IMUser> listener) {
        baseQuery("mobilePhoneNumber", phone, listener);
    }

    public void queryAllUsers(FindListener<IMUser> listener) {
        BmobQuery<IMUser> query = new BmobQuery<>();
        query.findObjects(listener);
    }

    public void baseQuery(String key, String value, FindListener<IMUser> listener) {
        BmobQuery<IMUser> query = new BmobQuery<>();
        query.addWhereEqualTo(key, value);
        query.findObjects(listener);
    }
}
