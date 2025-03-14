package com.guaji.game.battle;

import java.util.LinkedList;
import java.util.List;

import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Battle.BattleInfo;

/**
 * 战斗buff
 */
public class BattleBuff {
	/**
	 * buff的id
	 */
	private int buffId;
	/**
	 * buff类型
	 */
	private int buffType;
	/**
	 * 剩余回合数
	 */
	private int buffRound;
	/**
	 * buff数值参数
	 */
	private List<Integer> buffArgs;
	/**
	 * 叠加层数
	 */
	private int overlap;
	/**
	 * 是否可见
	 */
	private boolean visible;

	private int effectTimes = 0;

	public BattleBuff() {
		this.buffId = 0;
		this.buffType = 0;
		this.buffRound = 0;
		this.buffArgs = new LinkedList<>();
		this.overlap = 1;
		this.visible = false;
	}

	public BattleBuff(int buffId, int buffType, int buffRound) {
		this.buffId = buffId;
		this.buffType = buffType;
		this.buffRound = buffRound;
		this.buffArgs = new LinkedList<>();
		this.overlap = 1;
	}

	public int getBuffId() {
		return buffId;
	}

	public void setBuffId(int buffId) {
		this.buffId = buffId;
	}

	public int getBuffRound() {
		return buffRound;
	}

	public void setBuffRound(int buffRound) {
		this.buffRound = buffRound;
	}

	public boolean isValid() {
		return buffId > 0 && buffRound > 0;
	}

	public boolean decreaseRound() {
		if (buffRound > 0) {
			buffRound--;
			
			if (this.buffId == Const.Buff.SHADOWARMOR2_VALUE || this.buffId == Const.Buff.SHADOWARMOR_VALUE) {
				if (this.getBuffArgs().length == 2) {
					this.setBuffArg(0, this.getBuffArgs(1));
				}
			}
			return true;
		}
		return false;
	}

	public int getBuffType() {
		return buffType;
	}

	public void setBuffType(int buffType) {
		this.buffType = buffType;
	}

	public int getOverlap() {
		return overlap;
	}

	public void setOverlap(int overlap) {
		this.overlap = overlap;
	}

	public int getBuffArgs(int index) {
		if (buffArgs.size() > index) {
			return buffArgs.get(index);
		}
		return 0;
	}

	public void addBuffArg(int... args) {
		for (int arg : args) {
			this.buffArgs.add(arg);
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public BattleInfo.BuffInfo.Builder genBuilder() {
		BattleInfo.BuffInfo.Builder builder = BattleInfo.BuffInfo.newBuilder();
		builder.setBuffId(buffId);
		builder.setBuffRound(buffRound);
		return builder;
	}

	public int getEffectTimes() {
		return effectTimes;
	}

	public void setEffectTimes(int effectTimes) {
		this.effectTimes = effectTimes;
	}

	public void setBuffArg(int index, int value) {
		if (this.buffArgs.size() > index) {
			this.buffArgs.set(index, value);
		}
	}

	public int[] getBuffArgs() {
		int[] args = new int[buffArgs.size()];
		for (int i = 0; i < buffArgs.size(); i++) {
			args[i] = buffArgs.get(i);
		}
		return args;
	}

}
