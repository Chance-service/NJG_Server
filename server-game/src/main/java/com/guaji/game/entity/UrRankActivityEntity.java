package com.guaji.game.entity;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "ur_rank_activity")
@SuppressWarnings("serial")
public class UrRankActivityEntity extends DBEntity {

	@Id
	@GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
	@GeneratedValue(generator = "AUTO_INCREMENT")
	@Column(name = "id", unique = true)
    private long id = 0;

    @Column(name = "refreshTime", nullable = false)
    private long  refreshTime;


    @Column(name = "createTime", nullable = false)
    protected Date createTime =GuaJiTime.getCalendar().getTime();

    @Column(name = "updateTime")
    protected Date updateTime;

    @Column(name = "invalid")
    protected boolean invalid;

    public UrRankActivityEntity() {
        this.createTime = GuaJiTime.getCalendar().getTime();
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean isInvalid() {
        return invalid;
    }

    @Override
    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }



	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


}
