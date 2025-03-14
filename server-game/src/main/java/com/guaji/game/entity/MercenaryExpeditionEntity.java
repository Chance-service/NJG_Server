package com.guaji.game.entity;

import java.util.Date;
import java.util.LinkedList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;

@SuppressWarnings("serial")
@Entity
@Table(name = "mercenaryexpedition")
public class MercenaryExpeditionEntity extends DBEntity {

	@Id
	@GenericGenerator(name = "ASSIGNED", strategy = "assigned")
	@GeneratedValue(generator = "ASSIGNED")
	@Column(name = "playerId", nullable = false, unique = true)
	private int playerId;

	@Column(name = "createTime")
	private Date createTime;

	@Column(name = "updateTime")
	private Date updateTime;

	@Column(name = "invalid")
	private boolean invalid;

	@Column(name = "refreshCount")
	private int refreshCount;

	@Column(name = "dispatchCount")
	private int dispatchCount;

	@Column(name = "expeditionTaskStr", nullable = false)
	private String expeditionTaskStr;

	@Transient
	private LinkedList<ExpeditionTask> expeditionTaskList;

	@Column(name = "lucky")
	private int lucky;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public int getDispatchCount() {
		return this.dispatchCount;
	}

	public void increaseDispatchCount() {
		this.dispatchCount++;
		return;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	/**
	 * 构造初始化
	 */
	public MercenaryExpeditionEntity() {
		this.createTime = GuaJiTime.getCalendar().getTime();
		this.expeditionTaskList = new LinkedList<ExpeditionTask>();
		for (int i = 0; i < GsConst.MercenaryExpedition.EXPEDITION_TASK_SIZE; i++) {
			expeditionTaskList.add(i, null);
		}
	}

	/**
	 * 字符串存储转换为内存数据
	 */
	public void convert() {
		if (this.getExpeditionTaskListStr() != null) {
			this.expeditionTaskList = GsonUtil.getJsonInstance().fromJson(
					this.getExpeditionTaskListStr(),
					new TypeToken<LinkedList<ExpeditionTask>>() {
					}.getType());
		}
	}

	/**
	 * 将内存数据转化为json字符串
	 */
	public void reConvert() {
		this.setExpeditionTaskListStr(GsonUtil.getJsonInstance().toJson(
				this.expeditionTaskList));
		return;
	}

	/**
	 * 获取任务数据
	 * 
	 * @return
	 */
	public LinkedList<ExpeditionTask> getExpeditionTaskList() {
		return this.expeditionTaskList;
	}

	/**
	 * 获取任务存储字符串
	 * 
	 * @return
	 */
	private String getExpeditionTaskListStr() {
		return expeditionTaskStr;
	}

	/**
	 * 设置存储字符串
	 * 
	 * @param expeditionTaskStr
	 */
	private void setExpeditionTaskListStr(String expeditionTaskStr) {
		this.expeditionTaskStr = expeditionTaskStr;
	}

	/**
	 * 获取该玩家的佣兵远征数据存储对象
	 * 
	 * @param playerId
	 * @return
	 */
	public static MercenaryExpeditionEntity valueOf(int playerId) {
		MercenaryExpeditionEntity mercenaryExpeditionEntity = new MercenaryExpeditionEntity();
		mercenaryExpeditionEntity.playerId = playerId;
		mercenaryExpeditionEntity.reConvert();
		return mercenaryExpeditionEntity;
	}

	public int getNextRefreshCost() {
		return SysBasicCfg.getInstance().getExpeditionTaskRefreshCost(
				refreshCount);
	}

	/**
	 * 获取某个位置的任务对象
	 * 
	 * @param id
	 * @return
	 */
	public ExpeditionTask getExpeditionTask(int pos) {
		return this.expeditionTaskList.get(pos);
	}

	/**
	 * 获取某个任务id的对象
	 */
	public ExpeditionTask getExpeditionTaskFromId(int id) {
		for (ExpeditionTask task : expeditionTaskList) {
			if (task != null) {
				if (task.getId() == id) {
					return task;
				}
			}
		}

		return null;

	}

	/**
	 * 获取任务的位置
	 */
	public int getExpeditionTaskPos(int id) {
		for (int i = 0; i < GsConst.MercenaryExpedition.EXPEDITION_TASK_SIZE; i++) {
			if (expeditionTaskList.get(i) != null) {
				if (expeditionTaskList.get(i).getId() == id) {
					return i;
				}
			}

		}
		return -1;

	}

	/**
	 * 获取幸运值
	 * 
	 * @return
	 */
	public int getLucky() {
		return lucky;
	}

	/**
	 * 设置幸运值
	 * 
	 * @param lucky
	 */
	public void setLucky(int lucky) {
		this.lucky = lucky;
	}

	/**
	 * 重置次数累加
	 */
	public void increaseRefreshCount() {
		this.refreshCount += 1;
	}
	
	/**
	 * 获取刷新次数
	 */
	public int getRefreshCount()
	{
		return this.refreshCount;
	}

	/**
	 * 移出未开的任务
	 */
	private void removeExpeditionTask() {
		int i = 0;
		for (ExpeditionTask task : expeditionTaskList) {
			if (task != null) {
				if (task.getStatus() == 0) 
				{
					expeditionTaskList.set(i, null);
				}
			}
			i++;
		}
		return;
	}
	
	/**
	 * 定时刷新任务
	 */
	public void fixedReset()
	{
		removeExpeditionTask();
		return;
	}

	/**
	 * 重置刷新
	 */
	public void reset() {
		// 移出未开始的
//		removeExpeditionTask();
		this.lucky = 0;
		this.refreshCount = 0;
		this.dispatchCount = 0;
	}

//	public boolean changeExpeditionMercenary(int fromRoleId, int toRoleId){
//		if(fromRoleId <= 0 || toRoleId <= 0){
//			return false;
//		}
//		for (ExpeditionTask task : expeditionTaskList) {
//			if (task != null && task.getDoingRoleId() == fromRoleId) {
//				task.setDoingRoleId(toRoleId);
//				return true;
//			}
//		}
//		return false;
//	}
}
