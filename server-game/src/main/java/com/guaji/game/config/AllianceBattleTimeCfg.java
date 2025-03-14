package com.guaji.game.config;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.guaji.config.ConfigBase;
import org.guaji.config.ConfigManager;
import org.guaji.os.GuaJiTime;

import com.guaji.game.protocol.AllianceBattle.AllianceBattleState;
import com.guaji.game.protocol.AllianceBattle.FightGroup;

@ConfigManager.XmlResource(file = "xml/allianceBattleTime.xml", struct = "list")
public class AllianceBattleTimeCfg extends ConfigBase {
	
	private int curAllianceBattleStageStartId = 0;
	
	private int curAllianceBattleStageEndId = 0;
	
	private final int stageId ;
	
	private final int weekStartDay ;
	
	private final String startTime ;
	
	private final int weekEndDay ;
	
	private final String endTime ;
	
	public AllianceBattleTimeCfg(){
		this.stageId = 0;
		this.weekStartDay = 0;
		this.startTime = null;
		this.weekEndDay = 0;
		this.endTime = null;
	}

	public int getStageId() {
		return stageId;
	}

	public int getWeekStartDay() {
		return weekStartDay;
	}

	public String getStartTime() {
		return startTime;
	}

	public int getWeekEndDay() {
		return weekEndDay;
	}

	public String getEndTime() {
		return endTime;
	}
	
	private Date startDate = null;
	
	private Date endDate = null;
	
	public Date getStartSpecifiedDate(int stageId) {
		if (curAllianceBattleStageStartId != stageId || this.startDate == null) {
			String dateStr = String.valueOf(stageId) + startTime;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(GuaJiTime.DATE_FORMATOR_AF(dateStr));
			calendar.add(Calendar.DAY_OF_WEEK, weekStartDay - 1);
			this.startDate = calendar.getTime();

			curAllianceBattleStageStartId = stageId;
		}

		return this.startDate;
	}
	
	public Date getEndSpecifiedDate(int stageId) {
		if (curAllianceBattleStageEndId != stageId || this.endDate == null) {
			String dateStr = String.valueOf(stageId) + endTime;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(GuaJiTime.DATE_FORMATOR_AF(dateStr));
			calendar.add(Calendar.DAY_OF_WEEK, weekEndDay - 1);
			this.endDate = calendar.getTime();

			curAllianceBattleStageEndId = stageId;
		}

		return this.endDate;
	}
	
	/**
	 * 根据当前工会战期数获得当前
	 * @param curStageId
	 * @return
	 */
	public static int getCurBattleStageId(int curStageId) {
		long curTime = GuaJiTime.getMillisecond();
		List<AllianceBattleTimeCfg> battleTimeCfgs = ConfigManager.getInstance().getConfigList(AllianceBattleTimeCfg.class);
		for(AllianceBattleTimeCfg battleTimeCfg : battleTimeCfgs){
			if( curTime >= battleTimeCfg.getStartSpecifiedDate(curStageId).getTime() && curTime < battleTimeCfg.getEndSpecifiedDate(curStageId).getTime()){
				return battleTimeCfg.getStageId();
			}
		}
		return -1;
	}
	
	public static AllianceBattleTimeCfg getCfg(int stageId){
		List<AllianceBattleTimeCfg> battleTimeCfgs = ConfigManager.getInstance().getConfigList(AllianceBattleTimeCfg.class);
		for(AllianceBattleTimeCfg battleTimeCfg : battleTimeCfgs){
			if( battleTimeCfg.getStageId() == stageId){
				return battleTimeCfg;
			}
		}
		return null;
	}
	
	/**
	 * 获得该阶段的下一个阶段
	 * @return
	 */
	public static AllianceBattleState getNextBattleState(AllianceBattleState state) {
		switch (state) {
			case PREPARE:
				return AllianceBattleState.Draw_Lots_WAIT;
			case Draw_Lots_WAIT:
				return AllianceBattleState.Publicity_WAIT;
			case Publicity_WAIT:
				return AllianceBattleState.FS32_16_FIGHTING;
			case FS32_16_FIGHTING:
				return AllianceBattleState.FS16_8_WAIT;
			case FS16_8_WAIT:
				return AllianceBattleState.FS16_8_FIGHTING;
			case FS16_8_FIGHTING:
				return AllianceBattleState.FS8_4_WAIT;
			case FS8_4_WAIT:
				return AllianceBattleState.FS8_4_FIGHTING;
			case FS8_4_FIGHTING:
				return AllianceBattleState.FS4_2_WAIT;
			case FS4_2_WAIT:
				return AllianceBattleState.FS4_2_FIGHTING;
			case FS4_2_FIGHTING:
				return AllianceBattleState.FS2_1_WAIT;
			case FS2_1_WAIT:
				return AllianceBattleState.FS2_1_FIGHTING;
			case FS2_1_FIGHTING:
				return AllianceBattleState.SHOW_TIME;
			default:
				break;
		}
		return null;
	}
	
	public static FightGroup getBeforeGroup(FightGroup group) {
		switch (group) {
			case GROUP_16:
				return FightGroup.GROUP_32;
				
			case GROUP_8:
				return FightGroup.GROUP_16;
				
			case GROUP_4:
				return FightGroup.GROUP_8;
				
			case GROUP_2:
				return FightGroup.GROUP_4;
				
			default:
				break;
		}
		return null;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
