package com.guaji.game.recharge;

public class SyncSubResponse {

    private String payMoney;
    private String puId;
    private String platform;
    private String orderSerial;
    private Integer type;
    private Long expireTime;

    public String getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(String payMoney) {
        this.payMoney = payMoney;
    }

    public String getPuId() {
        return puId;
    }

    public void setPuId(String puId) {
        this.puId = puId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOrderSerial() {
        return orderSerial;
    }

    public void setOrderSerial(String orderSerial) {
        this.orderSerial = orderSerial;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "SyncSubResponse{" +
                "payMoney='" + payMoney + '\'' +
                ", puId='" + puId + '\'' +
                ", platform='" + platform + '\'' +
                ", orderSerial='" + orderSerial + '\'' +
                ", type=" + type +
                ", expireTime=" + expireTime +
                '}';
    }
}
