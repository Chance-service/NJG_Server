package com.guaji.game.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.guaji.game.battle.SkillHandler;
import com.guaji.game.battle.skill.*;

/**
 * 技能管理器
 */
public class SkillHandlerManager {
	/**
	 * 技能handler表
	 */
	private Map<Integer, SkillHandler> skillHandlers;

	/**
	 * 技能全局管理器对象
	 */
	private static SkillHandlerManager instance = null;

	/**
	 * 获取技能全局管理器对象
	 * 
	 * @return
	 */
	public static SkillHandlerManager getInstance() {
		if (instance == null) {
			instance = new SkillHandlerManager();
		}
		return instance;
	}

	private SkillHandlerManager() {
		skillHandlers = new ConcurrentHashMap<Integer, SkillHandler>();
	}

	public boolean init() {
		skillHandlers.put(SkillType.Eye_ID_100001, new EyesHandler_100001(SkillType.Eye_ID_100001));
		skillHandlers.put(SkillType.Eye_ID_100003, new EyesHandler_100003(SkillType.Eye_ID_100003));
		skillHandlers.put(SkillType.Eye_ID_100004, new EyesHandler_100004(SkillType.Eye_ID_100004));
		skillHandlers.put(SkillType.Eye_ID_100005, new EyesHandler_100005(SkillType.Eye_ID_100005));
		skillHandlers.put(SkillType.Eye_ID_100006, new EyesHandler_100006(SkillType.Eye_ID_100006));
		skillHandlers.put(SkillType.Eye_ID_100009, new EyesHandler_100009(SkillType.Eye_ID_100009));
		skillHandlers.put(SkillType.Eye_ID_100018, new EyesHandler_100018(SkillType.Eye_ID_100018));
		skillHandlers.put(SkillType.Eye_ID_100019, new EyesHandler_100019(SkillType.Eye_ID_100019));
		
		skillHandlers.put(SkillType.Passive_ID_90001, new PassiveHandler_90001(SkillType.Passive_ID_90001));
		skillHandlers.put(SkillType.Passive_ID_90002, new PassiveHandler_90002(SkillType.Passive_ID_90002));
		skillHandlers.put(SkillType.Passive_ID_90003, new PassiveHandler_90003(SkillType.Passive_ID_90003));
		skillHandlers.put(SkillType.Passive_ID_90004, new PassiveHandler_90004(SkillType.Passive_ID_90004));
		skillHandlers.put(SkillType.Passive_ID_90005, new PassiveHandler_90005(SkillType.Passive_ID_90005));
		skillHandlers.put(SkillType.Passive_ID_90006, new PassiveHandler_90006(SkillType.Passive_ID_90006));
		//skillHandlers.put(SkillType.Passive_ID_90007, new PassiveHandler_90007(SkillType.Passive_ID_90007));
		skillHandlers.put(SkillType.Passive_ID_90008, new PassiveHandler_90008(SkillType.Passive_ID_90008));
		skillHandlers.put(SkillType.Passive_ID_90009, new PassiveHandler_90009(SkillType.Passive_ID_90009));
		skillHandlers.put(SkillType.Passive_ID_90010, new PassiveHandler_90010(SkillType.Passive_ID_90010));
		skillHandlers.put(SkillType.Passive_ID_90011, new PassiveHandler_90011(SkillType.Passive_ID_90011));
		skillHandlers.put(SkillType.Passive_ID_90012, new PassiveHandler_90012(SkillType.Passive_ID_90012));

		skillHandlers.put(SkillType.SKILL_ID_1, new SkillHandler_1(SkillType.SKILL_ID_1));
		skillHandlers.put(SkillType.SKILL_ID_1001, new SkillHandler_1001(SkillType.SKILL_ID_1001));
		skillHandlers.put(SkillType.SKILL_ID_1002, new SkillHandler_1002(SkillType.SKILL_ID_1002));
		skillHandlers.put(SkillType.SKILL_ID_1101, new SkillHandler_1101(SkillType.SKILL_ID_1101));
		skillHandlers.put(SkillType.SKILL_ID_1102, new SkillHandler_1102(SkillType.SKILL_ID_1102));
		skillHandlers.put(SkillType.SKILL_ID_2, new SkillHandler_2(SkillType.SKILL_ID_2));
		skillHandlers.put(SkillType.SKILL_ID_2001, new SkillHandler_2001(SkillType.SKILL_ID_2001));
		skillHandlers.put(SkillType.SKILL_ID_2002, new SkillHandler_2002(SkillType.SKILL_ID_2002));
		skillHandlers.put(SkillType.SKILL_ID_2101, new SkillHandler_2101(SkillType.SKILL_ID_2101));
		skillHandlers.put(SkillType.SKILL_ID_2102, new SkillHandler_2102(SkillType.SKILL_ID_2102));
		skillHandlers.put(SkillType.SKILL_ID_21, new SkillHandler_21(SkillType.SKILL_ID_21));
		skillHandlers.put(SkillType.SKILL_ID_22, new SkillHandler_22(SkillType.SKILL_ID_22));
		skillHandlers.put(SkillType.SKILL_ID_3001, new SkillHandler_3001(SkillType.SKILL_ID_3001));
		skillHandlers.put(SkillType.SKILL_ID_3002, new SkillHandler_3002(SkillType.SKILL_ID_3002));
		skillHandlers.put(SkillType.SKILL_ID_3101, new SkillHandler_3101(SkillType.SKILL_ID_3101));
		skillHandlers.put(SkillType.SKILL_ID_3102, new SkillHandler_3102(SkillType.SKILL_ID_3102));
		skillHandlers.put(SkillType.SKILL_ID_4001, new SkillHandler_4001(SkillType.SKILL_ID_4001));
		skillHandlers.put(SkillType.SKILL_ID_4002, new SkillHandler_4002(SkillType.SKILL_ID_4002));
		skillHandlers.put(SkillType.SKILL_ID_41, new SkillHandler_41(SkillType.SKILL_ID_41));
		skillHandlers.put(SkillType.SKILL_ID_4101, new SkillHandler_4101(SkillType.SKILL_ID_4101));
		skillHandlers.put(SkillType.SKILL_ID_4102, new SkillHandler_4102(SkillType.SKILL_ID_4102));
		skillHandlers.put(SkillType.SKILL_ID_42, new SkillHandler_42(SkillType.SKILL_ID_42));
		skillHandlers.put(SkillType.SKILL_ID_500001, new SkillHandler_500001(SkillType.SKILL_ID_500001));
		skillHandlers.put(SkillType.SKILL_ID_5001, new SkillHandler_5001(SkillType.SKILL_ID_5001));
		skillHandlers.put(SkillType.SKILL_ID_500101, new SkillHandler_500001(SkillType.SKILL_ID_500101));
		skillHandlers.put(SkillType.SKILL_ID_5002, new SkillHandler_5002(SkillType.SKILL_ID_5002));
		skillHandlers.put(SkillType.SKILL_ID_500201, new SkillHandler_500201(SkillType.SKILL_ID_500201));
		skillHandlers.put(SkillType.SKILL_ID_500301, new SkillHandler_500301(SkillType.SKILL_ID_500301));
		skillHandlers.put(SkillType.SKILL_ID_500401, new SkillHandler_500401(SkillType.SKILL_ID_500401));
		skillHandlers.put(SkillType.SKILL_ID_500501, new SkillHandler_500501(SkillType.SKILL_ID_500501));
		skillHandlers.put(SkillType.SKILL_ID_500601, new SkillHandler_500601(SkillType.SKILL_ID_500601));
		skillHandlers.put(SkillType.SKILL_ID_500701, new SkillHandler_500701(SkillType.SKILL_ID_500701));
		skillHandlers.put(SkillType.SKILL_ID_500801, new SkillHandler_500801(SkillType.SKILL_ID_500801));
		skillHandlers.put(SkillType.SKILL_ID_500901, new SkillHandler_500901(SkillType.SKILL_ID_500901));
		skillHandlers.put(SkillType.SKILL_ID_501001, new SkillHandler_501001(SkillType.SKILL_ID_501001));
		skillHandlers.put(SkillType.SKILL_ID_501601, new SkillHandler_501601(SkillType.SKILL_ID_501601));
		skillHandlers.put(SkillType.SKILL_ID_502101, new SkillHandler_502101(SkillType.SKILL_ID_502101));
		skillHandlers.put(SkillType.SKILL_ID_600101, new SkillHandler_600101(SkillType.SKILL_ID_600101));
		skillHandlers.put(SkillType.SKILL_ID_600102, new SkillHandler_600102(SkillType.SKILL_ID_600102));
		skillHandlers.put(SkillType.SKILL_ID_600103, new SkillHandler_600103(SkillType.SKILL_ID_600103));
		skillHandlers.put(SkillType.SKILL_ID_5101, new SkillHandler_5101(SkillType.SKILL_ID_5101));
		skillHandlers.put(SkillType.SKILL_ID_5102, new SkillHandler_5102(SkillType.SKILL_ID_5102));
		skillHandlers.put(SkillType.SKILL_ID_9901, new SkillHandler_9901(SkillType.SKILL_ID_9901));
		skillHandlers.put(SkillType.SKILL_ID_9902, new SkillHandler_9902(SkillType.SKILL_ID_9902));

		return true;
	}

	public SkillHandler getSkillHandler(int scriptId) {
		if (skillHandlers.containsKey(scriptId)) {
			return skillHandlers.get(scriptId);
		}
		return null;
	}
}
