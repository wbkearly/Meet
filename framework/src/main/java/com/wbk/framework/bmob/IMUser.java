package com.wbk.framework.bmob;

import cn.bmob.v3.BmobUser;

/**
 * 用户类
 */
public class IMUser extends BmobUser {

    // 获取头像和昵称的方式
    private String tokenPortrait;
    private String tokenNickname;

    // 基本属性
    private String nickname;
    private String portrait;

    // true-- 男 false -- 女
    private boolean gender = true;

    // 简介
    private String description;

    private int age = 0;

    private String birthday;

    // 星座
    private String constellation;

    private String hobby;

    // 单身状态
    private String status;

    public String getTokenPortrait() {
        return tokenPortrait;
    }

    public void setTokenPortrait(String tokenPortrait) {
        this.tokenPortrait = tokenPortrait;
    }

    public String getTokenNickname() {
        return tokenNickname;
    }

    public void setTokenNickname(String tokenNickname) {
        this.tokenNickname = tokenNickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "IMUser{" +
                "tokenPortrait='" + tokenPortrait + '\'' +
                ", tokenNickname='" + tokenNickname + '\'' +
                ", nickname='" + nickname + '\'' +
                ", portrait='" + portrait + '\'' +
                ", gender=" + gender +
                ", description='" + description + '\'' +
                ", age=" + age +
                ", birthday='" + birthday + '\'' +
                ", constellation='" + constellation + '\'' +
                ", hobby='" + hobby + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
