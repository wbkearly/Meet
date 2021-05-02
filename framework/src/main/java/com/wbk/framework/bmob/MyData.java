package com.wbk.framework.bmob;

import cn.bmob.v3.BmobObject;

/**
 * 测试云函数
 */
public class MyData extends BmobObject {

    // 姓名
    private String name;

    // 性别 0男 1女
    private int gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

}
