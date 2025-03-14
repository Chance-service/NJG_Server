package com.guaji.game.battle;

import com.guaji.game.util.GsConst;

public class BuffValue {
	/**
	 * Buff數值
	 */
	private double buffValue;
	/**
	 * 光環
	 */
	private double auraValue;
	/**
	 * 標記
	 */
	private double markValue;

	public BuffValue(boolean isOne) {
		if (isOne) {
			setAllone();
		} else {
			buffValue = 0.0;
			auraValue = 0.0;
			markValue = 0.0;
		}
	}
	
	public void setAllone() {
		buffValue = 1.0;
		auraValue = 1.0;
		markValue = 1.0;
	}
	
	public void setAllValue(double buffVal, double auraVal , double markval) {
		buffValue = buffVal;
		auraValue = auraVal;
		markValue = markval;
	}
	
	public void CloneValue(BuffValue buffval) {
		buffValue = buffval.getBuffValue();
		auraValue = buffval.getAuraValue();
		markValue = buffval.getMarkValue();
	}
	
	public double getAllMul() {
		return BattleUtil.mulmul(buffValue,auraValue,markValue);
	}
	
	public double getAlladd() {
		return BattleUtil.addadd(buffValue,auraValue,markValue);
	}
	
	public double getBuffValue() {
		return buffValue;
	}
	
	public void addBuffValue(double value) {
		this.buffValue = BattleUtil.add(this.buffValue, value);
	}
	
	public void subBuffValue(double value) {
		this.buffValue = BattleUtil.sub(this.buffValue, value);
	}

	public double getAuraValue() {
		return auraValue;
	}
	
	public void addAuraValue(double value) {
		this.auraValue = BattleUtil.add(this.auraValue, value);
	}
	
	public void subAuraValue(double value) {
		this.auraValue = BattleUtil.sub(this.auraValue, value);
	}
	
	public double getMarkValue() {
		return markValue;
	}
	
	public void addMarkValue(double value) {
		this.markValue = BattleUtil.add(this.markValue, value);
	}
	
	public void subMarkValue(double value) {
		this.markValue = BattleUtil.sub(this.markValue, value);
	}
	
	public void addValue(int type ,double value) {
		if (type == GsConst.BUFF_TYPE.NORMAL_BUFF) {
			addBuffValue(value);
		} else if (type == GsConst.BUFF_TYPE.AURA_BUFF) {
			addAuraValue(value);
		} else if  (type == GsConst.BUFF_TYPE.MARK){
			addMarkValue(value);
		}
	}
	/**
	 * 兩個物件數值相加
	 * @param abuffVal
	 */
	public void setAddObj(BuffValue abuffVal) {
		addBuffValue(abuffVal.getBuffValue());
		addAuraValue(abuffVal.getAuraValue());
		addMarkValue(abuffVal.getMarkValue());
	}
	/**
	 * 數值最小為零
	 */
	public void MaxZero() {
		this.buffValue = Math.max(this.buffValue,0);
		this.auraValue = Math.max(this.auraValue,0);
		this.markValue = Math.max(this.markValue,0);
	}
}
