package com.guaji.game.battle;

import java.util.List;

import org.guaji.config.ConfigManager;

import com.guaji.game.config.BuffCfg;

public class NewBuff {
	/**
	 * buff的id
	 */
	private int Id;
//	/**
//	 * buff类型
//	 */
//	private int Type;
//	/**
//	 * buff数值参数
//	 */
//	private List<Integer> Args;
	/**
	 * 叠加层数
	 */
	private int overlap;
	/**
	 * 是否有效 0.無效 1.有效
	 */
	private int Valid ;
	
	private int expireTimes = 0;
	
	private int NowMarkTime = 0;
	
	private BuffCfg buffCfg;
		
	public NewBuff(int buffId,int NowMarkTime,int expireTimes) {
		this.Id = buffId;
		this.overlap = 1;
		this.expireTimes = expireTimes;
		UpdateMarkTime(NowMarkTime);
		UpdateBuffCfg();
	}
	
	private void UpdateBuffCfg() {
		buffCfg = ConfigManager.getInstance().getConfigByKey(BuffCfg.class, this.Id);
	}
	
	public int getBuffId() {
		return Id;
	}

	public void setBuffId(int buffId) {
		this.Id = buffId;
		UpdateBuffCfg();
	}
	
	public int getBuffType() {
		return buffCfg.getType();
	}
	
	public List<Double> getArgs() {
		return buffCfg.getParams();
	}
	
	public int getGroup() {
		return buffCfg.getGroup();
	}
	
	public int getDispel() {

		return buffCfg.getDispel();
	}
	/**
	 * 取Buff最高堆疊數
	 * @return
	 */
	public int getStack() {
		return buffCfg.getStack();
	}
	
	public int getPriorty() {
		return buffCfg.getPriorty();
	}

	public int getOverlap() {
		return overlap;
	}

	public void setOverlap(int overlap) {
		this.overlap = overlap;
	}
	
	public int getExpireTimes() {
		return expireTimes;
	}

	public void setExpireTimes(int expireTimes) {
		this.expireTimes = expireTimes;
	}
	
	public boolean isValid() {
		return (Valid == 1);
	}
	
	public int UpdateMarkTime(int MarkTime) {
		this.NowMarkTime = MarkTime;
		int ret = 0;
		if (this.expireTimes > 0) { // 有時效的
			int tempvalid = this.Valid;
			this.Valid = (this.NowMarkTime < this.expireTimes) ? 1: 0;
			if ((tempvalid == 1) && (this.Valid == 0)) { // buff 時效到要做的事
				ret = getBuffId();
			}
		} else { // 沒有作用時間
			this.Valid = 1; // 永久有效
		}
		return ret;
	}
	
	public int getNowMarkTime() {
		return this.NowMarkTime;
	}
	
	public boolean canStack() {
		if (getStack() < overlap) {
			return true;
		}
		return false;
	}
	
	public boolean IsGain() {
		return buffCfg.isGain();
	}
	
	public boolean IsDispel() {

		return (buffCfg.getDispel() == 1);
	}

}
