package com.guaji.game.module.activity.activity144;

import java.util.ArrayList;
import java.util.List;
import org.guaji.config.ConfigManager;
import org.guaji.net.protocol.Protocol;
import org.guaji.os.MyException;

import com.guaji.game.protocol.Activity3.Act144BossInfo;
import com.guaji.game.protocol.Activity3.Act144BuffInfo;
import com.guaji.game.protocol.Activity3.Act144TeamInfo;
import com.guaji.game.protocol.Activity3.Activity144Little_testRep;
import com.guaji.game.util.ActivityUtil;
import com.guaji.game.config.ActivityTimeCfg;
import com.guaji.game.config.LittleTestQuestionCfg;
import com.guaji.game.config.LittleTestRewardCfg;
import com.guaji.game.config.LittleTestRoleCfg;
import com.guaji.game.config.LittleTestSkillCfg;
import com.guaji.game.item.AwardItems;
import com.guaji.game.item.ItemInfo;
import com.guaji.game.log.BehaviorLogger.Action;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;

/**
 * 海盜活动管理类
 */
public class Activity144LittleTestManager {

	private static final int OPERATE_SYNC = 0;		// 同步
	private static final int OPERATE_ANSWERS = 1;  	// 回答
	private static final int OPERATE_ACCOUNT = 2;	// 結算
	private static final int OPERATE_Get_REWARD = 3;// 領獎
	private static final int OPERATE_CreateGame = 4;// 開局初始
	private static final int OPERATE_ClearGame = 5; // 測試清除用
	public static final int Action_0 = 0;
	public static final int Action_1 = 1;
	public static final int Action_2 = 2;
	public static final int Action_3 = 3;
	
	/**
	 * 檢查題目清除
	 * 
	 */
	public static void checkquestion_144() {
		int activityId = Const.ActivityId.ACTIVITY144_LITTLE_TEST_VALUE;
		ActivityTimeCfg timeCfg = ActivityUtil.getCurActivityTimeCfg(activityId);
		if (timeCfg == null) {
			if (LittleTestQuestionCfg.getQuestionList().size() > 0)
				LittleTestQuestionCfg.getQuestionList().clear();
		}
	}
	/**
	 * 同步
	 */
	static void sync(Player player, ActivityTimeCfg timeCfg, Activity144Status status) {
		Activity144Little_testRep.Builder response = getBuilder(player, timeCfg, status);
		if (null != response) {
			response.setType(OPERATE_SYNC);
			if (status.getlevel()<= 10) //闖關結束才秀答對題數
				response.setBinbom(0);
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY144_S_VALUE, response));
		}
	}
	/**
	 * 回答
	 */
	static void crategame(Player player, ActivityTimeCfg timeCfg, Activity144Status status, List<Integer>RoleAry) {
		// 如果活动进入展示期，只能同步和兑换
		if (timeCfg.isEnd()) {
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return;
		}
		if (status.getlevel() != 0) {
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return;
		}
		
		if (LittleTestQuestionCfg.getQuestionList().size() != LittleTestQuestionCfg.MAX_QUESTION_COUNT){
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}
		
		if ((RoleAry == null) ||(RoleAry.size() == 0)) {
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}
		
		// 設定隊伍資料與Boss資料
		// Team
		String Rolestr="";
		int hp = 0;
		for(Integer aid : RoleAry) {
			if (Rolestr.equals("")) {
				Rolestr = String.format("%d",aid);
			} else {
				Rolestr = Rolestr + String.format(Rolestr+"_%d",aid);
			}
			LittleTestRoleCfg RoleCfg = ConfigManager.getInstance().getConfigByKey(LittleTestRoleCfg.class,aid);
			if (RoleCfg != null)
			{
				hp = hp + RoleCfg.getHP();
			}
		}
		
		if (hp == 0) {
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;			
		}
		
		LittleTestRoleCfg BossCfg = LittleTestRoleCfg.RandBoss();
		
		if (BossCfg == null){
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;		
		}
		
		status.setlevel(1);
		int question = LittleTestQuestionCfg.getQuestionList().get(0);
		status.setqueid(question);
		int nextid = LittleTestQuestionCfg.getQuestionList().get(1);
		status.setnextid(nextid);
		status.setbinbom(0);
		status.setscore(0);
		status.setrewards("");
		

		status.setRoles(Rolestr);
		status.setTeamHP(hp);
		status.setTeamMaxHP(hp);
		status.setTeamBuff("");
		status.setTeamRoundBuff("");
		// Boss
			
		status.setBossid(BossCfg.getRole());
		status.setBossHP(BossCfg.getHP());
		status.setBossMaxHP(BossCfg.getHP());
		status.setKillValue(0);
		status.setBossBuff("");
		status.setBossRoundBuff("");
		
		
		player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY144_LITTLE_TEST_VALUE, timeCfg.getStageId());
		
		Activity144Little_testRep.Builder response = getBuilder(player, timeCfg, status);
		if (null != response) {
			response.setType(OPERATE_CreateGame);
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY144_S_VALUE, response));
		}
	}
	/**
	 * 回答並攻擊
	 * @throws MyException 
	 */
	static void answers(Player player, ActivityTimeCfg timeCfg, Activity144Status status, int ans){

		// 如果活动进入展示期，只能同步和兑换
		if (timeCfg.isEnd()) {
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return;
		}
		
		if (LittleTestQuestionCfg.getQuestionList().size() != LittleTestQuestionCfg.MAX_QUESTION_COUNT){
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}
		
		if ((ans < 1) || (ans > LittleTestQuestionCfg.MAX_ANSWERS_COUNT)){
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}
		
		int nowlevel = status.getlevel();
		int queid = status.getqueid();
		int nextid = status.getnextid();
		
		if ((nowlevel < 1) || (nowlevel > LittleTestQuestionCfg.MAX_QUESTION_COUNT)){
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}
				
		if (queid == 0) {  // 沒有題目
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}
		
		if ((nowlevel < LittleTestQuestionCfg.MAX_QUESTION_COUNT)&&(nextid == 0)) { // 不是最後一題,必須有下一題
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}
		
		if ((status.getTeamHP() == 0)){
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}
		
		if ((status.getRoles().equals("")) || (status.getBossid() == 0)) {
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
			return;
		}
		
		if (status.getBossHP() == 0) {
			
			LittleTestRoleCfg BossCfg = LittleTestRoleCfg.RandBoss();
			
			if (BossCfg == null){
				player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
				return;		
			}
			//重新產生Boss
			status.setBossid(BossCfg.getRole());
			status.setBossHP(BossCfg.getHP());
			status.setBossMaxHP(BossCfg.getHP());
			status.setBossBuff("");
			status.setBossRoundBuff("");
			
			// 刪除TeamBuff
			status.setTeamBuff("");
			status.setTeamRoundBuff("");
		}
		
		
		LittleTestQuestionCfg Cfg = ConfigManager.getInstance().getConfigMap(LittleTestQuestionCfg.class).get(queid);
		
		boolean right = Cfg.matchanswer(ans);
		
		if (right) { //答對
			int nowbinbom = status.getbinbom();
			status.setbinbom(nowbinbom+1);
			int nowscore = status.getscore();
			int takescore = Cfg.getScore();
			status.setscore(nowscore+takescore);
		}
		
		status.setlevel(nowlevel+1);
		status.setqueid(nextid);
		if (status.getlevel() < LittleTestQuestionCfg.MAX_QUESTION_COUNT) {
			status.setnextid(LittleTestQuestionCfg.getQuestionList().get(status.getlevel()-1+1));
		}else {
			status.setnextid(0);
		}
		
		status.setrewards("");
		
		fighting(right,status,Cfg);
		
		// 更新活动实体
		player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY144_LITTLE_TEST_VALUE, timeCfg.getStageId());
		// 返回消息包
		Activity144Little_testRep.Builder response = getBuilder(player, timeCfg, status);
		if (null != response) {
			response.setType(OPERATE_ANSWERS);
			
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY144_S_VALUE, response));
		}
	}
	/**
	 * 結算遊戲
	 */
	static void AccountGame(Player player, ActivityTimeCfg timeCfg, Activity144Status status) {
		// 如果活动进入展示期，只能同步和兑换
		if (timeCfg.isEnd()) {
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.ACTIVITY_CLOSE);
			return;
		}
		
		if (status.getTeamHP() != 0) {
			if (status.getlevel() <= LittleTestQuestionCfg.MAX_QUESTION_COUNT) {
				player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.CONFIG_ERROR_VALUE);
				return;
			}
		}
		
		if (!status.getrewards().isEmpty()) { // 已經結換過
			return;
		}
		String awardstr = "";
		int addaward = 0;
		if (status.getbinbom() != 0) {
			int score = (int)(status.getscore()*(1+ status.getKillValue()/100));
			Activity144TeamInfo TeamData = new Activity144TeamInfo(status);
			addaward = TeamData.getaddaward();
			List<String> str_awardlist = LittleTestRewardCfg.GetPlayerAward(status.getbinbom(),score,addaward);
			AwardItems nowrewardItems = new AwardItems();
			for (String astr :str_awardlist) {
				ItemInfo nowitemInfos = ItemInfo.valueOf(astr);
				nowrewardItems.addItem(nowitemInfos);
			}
			awardstr = nowrewardItems.toString();
		}
		status.setrewards(awardstr);
		// 更新活动实体
		player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY144_LITTLE_TEST_VALUE, timeCfg.getStageId());
		// 返回消息包
		Activity144Little_testRep.Builder response = getBuilder(player, timeCfg, status);
		if (null != response) {
			response.setType(OPERATE_ACCOUNT);
			
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY144_S_VALUE, response));
		}
	}
	/**
	 * 获取返回结构体
	 */
	static Activity144Little_testRep.Builder getBuilder(Player player, ActivityTimeCfg timeCfg, Activity144Status status) {
	
		// 返回包
		Activity144Little_testRep.Builder response = Activity144Little_testRep.newBuilder();
		response.setLevel(status.getlevel());
		response.setQueid(status.getqueid());
		response.setNextid(status.getnextid());
		response.setBinbom(status.getbinbom());
		response.setScore(status.getscore());
		response.addReward(status.getrewards());
		//Team
		Act144TeamInfo.Builder Team = Act144TeamInfo.newBuilder();
		String info = status.getRoles() ;
		if	(info != null && info.length() > 0){
			String[] Roles = info.split("_");
			for(String idstr : Roles) {
				Team.addRoleid(Integer.parseInt(idstr));
			}
		}
		Team.setNowHp(status.getTeamHP());
		Team.setMaxHp(status.getTeamMaxHP());
		Team.setTeamSpeed(status.getTeamSpeed());
		
		List<Act144BuffInfo> BuffList = getBuffBuilder(status.getTeamBuff());
		List<Act144BuffInfo> BuffRoundList = getBuffBuilder(status.getTeamRoundBuff());
		
		Team.addAllBuff(BuffList);
		Team.addAllNowBuff(BuffRoundList);
		
		response.setTeamInfo(Team);
		
		//Boss
		Act144BossInfo.Builder Boss = Act144BossInfo.newBuilder();
		Boss.setBossid(status.getBossid());
		Boss.setBossNowHp((status.getBossHP()));
		Boss.setBossMaxHp((status.getBossMaxHP()));
		Boss.setBossSpeed((status.getBossSpeed()));
		
		List<Act144BuffInfo> BossBuffList = getBuffBuilder(status.getBossBuff());
		List<Act144BuffInfo> BossRoundBuffList = getBuffBuilder(status.getBossRoundBuff());
		
		Boss.addAllBuff(BossBuffList);
		Boss.addAllNowBuff(BossRoundBuffList);
						
		response.setBossInfo(Boss);
		

		return response;
	}
	/**
	 * 領取獎勵
	 */
	static void takegift(Player player, ActivityTimeCfg timeCfg, Activity144Status status) {
		// 如果活动进入展示期，只能同步和兑换
		// 有獎品才能兌換
		if (status.getrewards().isEmpty()) {
			player.sendError(HP.code.ACTIVITY144_C_VALUE, Status.error.ACTIVITY_AWARDS_HAS_GOT);
			return ;
		}
		AwardItems ownawardItems = new AwardItems();
		List<ItemInfo> ownitemInfos = ItemInfo.valueListOf(status.getrewards()); 
		ownawardItems.addItemInfos(ownitemInfos); // 獲得的物品
		ownawardItems.rewardTakeAffectAndPush(player, Action.ACTIVITY144_LITTLE_TEST, 0);
		
		//已領取歸零
		//status.setlevel(0);不歸零,玩過遊戲的紀錄
		status.setqueid(0);
		status.setnextid(0);
		status.setrewards("");
		
		player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY144_LITTLE_TEST_VALUE, timeCfg.getStageId());
		// 返回消息包
		Activity144Little_testRep.Builder response = getBuilder(player, timeCfg, status);
		if (null != response) {
			response.setType(OPERATE_Get_REWARD);
			
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY144_S_VALUE, response));
		}
	}
	/**
	 * 測試用清空
	 */
	static void cleargame(Player player, ActivityTimeCfg timeCfg, Activity144Status status) {
		status.setlevel(0);
		status.setqueid(0);
		status.setnextid(0);
		status.setbinbom(0);
		status.setscore(0);
		status.setrewards("");
		
		player.getPlayerData().updateActivity(Const.ActivityId.ACTIVITY144_LITTLE_TEST_VALUE, timeCfg.getStageId());
		// 返回消息包
		Activity144Little_testRep.Builder response = getBuilder(player, timeCfg, status);
		if (null != response) {
			response.setType(OPERATE_ClearGame);
			
			player.sendProtocol(Protocol.valueOf(HP.code.ACTIVITY144_S_VALUE, response));
		}
	}
	
	static List<Act144BuffInfo> getBuffBuilder(String BuffStr){
		List<Act144BuffInfo> BuffList = new ArrayList<>();	
		if	(BuffStr != null && BuffStr.length() > 0){
			String[] BuffAry = BuffStr.split(";");
			for (String aBuff :BuffAry) {
				String[] ValAry = aBuff.split("_");
				if (ValAry.length < 2) {
					continue;
				}
				Act144BuffInfo.Builder Buff = Act144BuffInfo.newBuilder();
				Buff.setUserid(Integer.parseInt(ValAry[0]));
				Buff.setBuffid(Integer.parseInt(ValAry[1]));
				Buff.setValue(Integer.parseInt(ValAry[2]));
				Buff.setTimes(Integer.parseInt(ValAry[3]));
				BuffList.add(Buff.build());
			}
		}
		return BuffList;
	}
	
	static String getBufftoStr (List<Act144BuffInfo> Bufflist) {
		String BuffStr = "";
		if ((Bufflist != null) && (Bufflist.size() > 0)) {
			for (Act144BuffInfo buff : Bufflist) {
				if (BuffStr.equals("")) {
					BuffStr = String.format("%d_%d_%d_%d",buff.getUserid(),buff.getBuffid(),buff.getValue(),buff.getTimes());
				}
				else {
					BuffStr = BuffStr+String.format("%d_%d_%d_%d",buff.getUserid(),buff.getBuffid(),buff.getValue(),buff.getTimes());
				}
			}
		}
		return BuffStr;
	}
	
	static void fighting(boolean right , Activity144Status status,LittleTestQuestionCfg QuestCfg) {
		Activity144TeamInfo TeamData = new Activity144TeamInfo(status);
		Activity144BossInfo BossData = new Activity144BossInfo(status);
		
		TeamData.AddBuff(Action_0,BossData);
		BossData.AddBuff(Action_0,TeamData);
		boolean isDead = false;
		if (TeamData.getSpeed() >= BossData.getSpeed()) {
			
			TeamData.AddBuff(Action_1,BossData);
			isDead = TeamData.AttackEnemy(BossData,right,QuestCfg);
			if (!isDead) {
				TeamData.AddBuff(Action_2,BossData);
				TeamData.RecoveryHP();
			
				BossData.AddBuff(Action_1,TeamData);
				isDead = BossData.AttackEnemy(TeamData,right,QuestCfg);
				if (!isDead) {
					BossData.AddBuff(Action_2,TeamData);
					BossData.RecoveryHP();
				}
			}
			
		}else {
			BossData.AddBuff(Action_1,TeamData);
			isDead = BossData.AttackEnemy(TeamData,right,QuestCfg);
			if (!isDead) { 
				BossData.AddBuff(Action_2,TeamData);
				BossData.RecoveryHP();
				
				TeamData.AddBuff(Action_1,BossData);
				isDead = TeamData.AttackEnemy(BossData,right,QuestCfg);
				if (!isDead) {
					TeamData.AddBuff(Action_2,BossData);
					TeamData.RecoveryHP();
				}
			}
		}
		if (!isDead) {
			TeamData.AddBuff(Action_3,BossData);
			BossData.AddBuff(Action_3,TeamData);
			
			TeamData.finishBuff();
			BossData.finishBuff();
		}
		TeamData.setStatus(status);
		BossData.setStatus(status);
		
		if (BossData.getNowHP() == 0) {
			status.setKillValue(status.getKillValue()+BossData.getIntimacy());
		}
	}

}
