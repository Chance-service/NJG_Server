package com.guaji.game.entity;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "eighteenprinces_help_history")
public class EighteenPrincesHelpHistoryEntity extends DBEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4132282814991338597L;

	@Id
    @GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @Column(name = "id", unique = true)
    private long id;

    @Column(name = "playerId")
    private int playerId;

    @Column(name = "friendPlayerId")
    private int friendPlayerId;


    @Column(name = "reward")
    private boolean reward;

    /**
     * 第几次协战
     */
    @Column(name = "helpCount")
    private int helpCount;

    @Column(name = "createTime", nullable = false)
    protected Date createTime;

    @Column(name = "updateTime")
    protected Date updateTime;

    @Column(name = "invalid")
    protected boolean invalid;

    public EighteenPrincesHelpHistoryEntity(){
        this.createTime = GuaJiTime.getCalendar().getTime();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getFriendPlayerId() {
        return friendPlayerId;
    }

    public void setFriendPlayerId(int friendPlayerId) {
        this.friendPlayerId = friendPlayerId;
    }


    public boolean isReward() {
        return reward;
    }

    public void setReward(boolean reward) {
        this.reward = reward;
    }

    public int getHelpCount() {
        return helpCount;
    }

    public void setHelpCount(int helpCount) {
        this.helpCount = helpCount;
    }

    @Override
    public Date getCreateTime() {
        return this.createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getUpdateTime() {
        return this.updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean isInvalid() {
        return this.invalid;
    }

    @Override
    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }
}
