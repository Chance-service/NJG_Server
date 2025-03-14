package com.guaji.game.platform.user;

import java.util.List;

public class ResponseData {
    private String stamp;
    private String youaiId;
    private String youaiName;
    private String password;
    private String email;
    private List<ResponseUserData> users;
    private int isCreate;
    private int userType;
    private int userTypeCode;
    private String userTypeMsg;

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public String getYouaiId() {
        return youaiId;
    }

    public void setYouaiId(String youaiId) {
        this.youaiId = youaiId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIsCreate() {
        return isCreate;
    }

    public void setIsCreate(int isCreate) {
        this.isCreate = isCreate;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getYouaiName() {
        return youaiName;
    }

    public void setYouaiName(String youaiName) {
        this.youaiName = youaiName;
    }

    public List<ResponseUserData> getUsers() {
        return users;
    }

    public void setUsers(List<ResponseUserData> users) {
        this.users = users;
    }

    public int getUserTypeCode() {
        return userTypeCode;
    }

    public void setUserTypeCode(int userTypeCode) {
        this.userTypeCode = userTypeCode;
    }

    public String getUserTypeMsg() {
        return userTypeMsg;
    }

    public void setUserTypeMsg(String userTypeMsg) {
        this.userTypeMsg = userTypeMsg;
    }
}
