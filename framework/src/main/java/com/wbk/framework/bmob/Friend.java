package com.wbk.framework.bmob;

import cn.bmob.v3.BmobObject;

/**
 * 好友关系表
 */
public class Friend extends BmobObject {

    private IMUser me;

    private IMUser friend;

    public IMUser getMe() {
        return me;
    }

    public void setMe(IMUser me) {
        this.me = me;
    }

    public IMUser getFriend() {
        return friend;
    }

    public void setFriend(IMUser friend) {
        this.friend = friend;
    }
}
