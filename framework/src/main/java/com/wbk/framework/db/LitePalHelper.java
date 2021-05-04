package com.wbk.framework.db;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

public class LitePalHelper {

    private static volatile LitePalHelper mInstance = null;

    private LitePalHelper() {
    }

    public static LitePalHelper getInstance() {
        if (mInstance == null) {
            synchronized (LitePalHelper.class) {
                if (mInstance == null) {
                    mInstance = new LitePalHelper();
                }
            }
        }
        return mInstance;
    }

    private void baseSave(LitePalSupport support) {
        support.save();
    }

    public void saveNewFriend(String msg, String id) {
        NewFriend newFriend = new NewFriend();
        newFriend.setUserId(id);
        newFriend.setMsg(msg);
        newFriend.setResponse(-1);
        newFriend.setSaveTime(System.currentTimeMillis());
        baseSave(newFriend);
    }

    private List<? extends LitePalSupport> baseQuery(Class<? extends LitePalSupport> cls) {
        return LitePal.findAll(cls);
    }

    public List<NewFriend> queryNewFriend() {
        return (List<NewFriend>) baseQuery(NewFriend.class);
    }

    public void updateNewFriend(String userId, int response) {
        NewFriend newFriend = new NewFriend();
        newFriend.setResponse(response);
        newFriend.updateAll("userId = ?", userId);
    }


}
