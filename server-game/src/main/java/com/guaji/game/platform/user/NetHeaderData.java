package com.guaji.game.platform.user;

import com.google.gson.annotations.Expose;

public class NetHeaderData {

    @Expose
    private String gameId;
    @Expose
    private String gameVersion;
    @Expose
    private long timestamp;
    @Expose
    private String deviceMacId;
    @Expose
    private String deviceName;
    @Expose
    private String platform;
    @Expose
    private String osVersion;
    @Expose
    private String idfa;
    @Expose
    private String remoteAddr;


    public String getGameId() {
        return gameId;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getIdfa() {
        return idfa;
    }

    public String getDeviceMacId() {
        return deviceMacId;
    }

    public void setDeviceMacId(String deviceMacId) {
        this.deviceMacId = deviceMacId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String toString() {
        return "\ngameId:" + gameId + " gameVersion:" + gameVersion +
                "\ntimestamp:" + timestamp + " remoteAddr:" + remoteAddr + " deviceMacId:" + deviceMacId +
                "\n platform: " + platform;
    }


}
