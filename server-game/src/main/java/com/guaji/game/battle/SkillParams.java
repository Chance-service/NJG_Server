package com.guaji.game.battle;

public class SkillParams {
	
	private double onlyparams;
	
	private int addHurt;
	
	private boolean decHurt;
	
	private boolean divSATK;

	public SkillParams() {
		init();
		// TODO Auto-generated constructor stub
	}
	
	public void init() {
		onlyparams = 0.0;
		addHurt = 0;
		decHurt = true;
		divSATK = true;
	}
	
	public double getOnlyparams() {
		return this.onlyparams;
	}
	
	public int getAddHurt() {
		return this.addHurt;
	}
	
	public boolean getDecHurt() {
		return this.decHurt;
	}
	
	public boolean getDivSATK() {
		return this.divSATK;
	}
}
