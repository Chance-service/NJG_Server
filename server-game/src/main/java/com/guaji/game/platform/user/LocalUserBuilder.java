package com.guaji.game.platform.user;

public class LocalUserBuilder {
    private String youaiId;
    private String password;
    private String gameId;
    private String youaiName;
    private String thirdId;
    private String platform;
    private int channel;
    private int isCreate;
    private int userType;
    private String email;

    public LocalUserBuilder withPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public LocalUserBuilder withYouaiId(String userId) {
        this.youaiId = userId;
        return this;
    }

    public LocalUserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public LocalUserBuilder withChannel(int channel) {
        this.channel = channel;
        return this;
    }

    public LocalUserBuilder withGameId(String gameId) {
        this.gameId = gameId;
        return this;
    }

    public LocalUserBuilder withYouaiName(String username) {
        this.youaiName = username;
        return this;
    }

    public LocalUserBuilder withIsCreate(int isCreate) {
        this.isCreate = isCreate;
        return this;
    }

    public LocalUserBuilder withUserType(int userType) {
        this.userType = userType;
        return this;
    }

    public LocalUserBuilder withThirdId(String thirdid) {
        this.thirdId = thirdid;
        return this;
    }

    public LocalUser build() {
        LocalUser user = new LocalUser();
        user.setGameId(this.gameId);
        user.setChannel(channel);
        user.setPassword(password);
        user.setYouaiId(youaiId);
        user.setYouaiName(youaiName);
        user.setIsCreate(isCreate);
        user.setUserType(userType);
        user.setPlatform(platform);
        user.setThirdId(thirdId);
        user.setEmail(email);
        return user;
    }


    public LocalUserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
}
