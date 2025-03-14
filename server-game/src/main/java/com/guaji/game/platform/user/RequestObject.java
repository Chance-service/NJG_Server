package com.guaji.game.platform.user;

public class RequestObject {
    private RequestData data;
    private NetHeaderData header;

    public RequestObject() {}

    public RequestObject(String deviceId, String deviceName, String platform, String gameId) {
        header = new NetHeaderData();
        header.setDeviceMacId(deviceId);
        header.setDeviceName(deviceName);
        header.setPlatform(platform);
        header.setGameId(gameId);
    }

    public RequestData getData() {
        return data;
    }

    public void setData(RequestData data) {
        this.data = data;
    }

    public NetHeaderData getHeader() {
        return header;
    }

    public void setHeader(NetHeaderData header) {
        this.header = header;
    }
}
