package com.guaji.game.module.activity.activity124;

import org.guaji.os.GuaJiTime;

import java.time.LocalDate;

public class Activity124Status {


    private Boolean gotTicket;

    /**
     * 彩票获取时间
     */
    private String ticketGetDate;

    /**
     * 抽奖次数
     */
    private int count;

    /**
     * 彩票类型
     */
    private Integer ticketId;

    /**
     * 是否已经使用
     */
    private Boolean isUsed;


    public Activity124Status() {
        this.ticketGetDate = GuaJiTime.getDateString();
        this.count = 0;
        this.ticketId = 0;
        this.isUsed = false;
        this.gotTicket = false;
    }


    public Boolean getGotTicket() {
        if (GuaJiTime.getDateString().equals(this.ticketGetDate) && ticketId > 0) {
            this.gotTicket = true;
        } else {
            this.gotTicket = false;
        }
        return this.gotTicket;
    }

    public void setGotTicket(Boolean gotTicket) {
        this.gotTicket = gotTicket;
    }

    public int getCount() {
        if (!GuaJiTime.getDateString().equals(this.ticketGetDate)) {
            this.count = 0;
        }
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTicketGetDate() {
        return ticketGetDate;
    }

    public void setTicketGetDate(String ticketGetDate) {
        this.ticketGetDate = ticketGetDate;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public Boolean getUsed() {
        if (!GuaJiTime.getDateString().equals(this.ticketGetDate)) {
            this.isUsed = false;
        }
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

}
