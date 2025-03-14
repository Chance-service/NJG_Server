package com.guaji.game.platform.user;

public class ResponseUserData {
    private String youaiId;
    private String name;
    private int userType;
    private String gameId;


    public String getYouaiId() {
        return youaiId;
    }

    public void setYouaiId(String youaiId) {
        this.youaiId = youaiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
