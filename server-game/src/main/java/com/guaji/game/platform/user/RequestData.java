package com.guaji.game.platform.user;

public class RequestData {
    private int channel = 1000;
    private String nickName = "";
    private String thirdId = "";
    private String playerId = "";
    private String platform = "";
    private String youaiId = "";
    private String youaiName = "";
    private String email = "";
    private String password = "";
    private String oldPassword;
    private int isGuestConversion;
    private int isInitPassword;
    private String youai_name="";


    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getThirdId() {
        return thirdId;
    }

    public void setThirdId(String thirdId) {
        this.thirdId = thirdId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getYouaiId() {
        return youaiId;
    }

    public void setYouaiId(String youaiId) {
        this.youaiId = youaiId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setIsGuestConversion(int isGuestConversion) {
        this.isGuestConversion = isGuestConversion;
    }

    public int getIsGuestConversion() {
        return isGuestConversion;
    }


    public void setIsInitPassword(int isInitPassword) {
        this.isInitPassword = isInitPassword;
    }

    public int getIsInitPassword() {
        return isInitPassword;
    }

    public String getYouaiName() {
        return youaiName;
    }

    public void setYouaiName(String youaiName) {
        this.youaiName = youaiName;
    }

    public String getYouai_name() {
        return youai_name;
    }

    public void setYouai_name(String youai_name) {
        this.youai_name = youai_name;
    }
}
