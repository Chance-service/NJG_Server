package com.guaji.game.platform.user;

public class LocalUser {
    private String youaiId;
    private String password;
    private String gameId;
    private String youaiName;
    private String thirdId;
    private String platform;
    private String email;
    private int channel;
    private int isCreate;
    private int userType;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
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

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getThirdId() {
        return thirdId;
    }

    public void setThirdId(String thirdId) {
        this.thirdId = thirdId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getYouaiName() {
        return youaiName;
    }

    public void setYouaiName(String youaiName) {
        this.youaiName = youaiName;
    }

    public String getYouaiId() {
        return youaiId;
    }

    public void setYouaiId(String youaiId) {
        this.youaiId = youaiId;
    }
}
