package com.guaji.game.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.app.AppObj;
import org.guaji.config.ClassScaner;
import org.guaji.msg.Msg;
import org.guaji.os.MyException;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.rank.IRank;
import com.guaji.game.rank.SingleRank;
import com.guaji.game.util.GsConst;

/**
 * 排行管理器,通用的排行接口;
 */
public class RankManager extends AppObj {
	private static final String RANK_SCANER_PACKAGE = "com.guaji.game.rank";
	private static RankManager instance = null;

	private Map<RankType, IRank> ranks;

	public RankManager(GuaJiXID xid) {
		super(xid);
		if (instance == null) {
			instance = this;
		}
	}

	public static RankManager getInstance() {
		return instance;
	}

	/**
	 * 初始化排名管理器;
	 */
	public void init() {
		ranks = new HashMap<RankType, IRank>();
		List<Class<?>> scanClassesFilter = ClassScaner.scanClassesFilter(RANK_SCANER_PACKAGE, SingleRank.class);
		for (Class<?> eachClass : scanClassesFilter) {
			SingleRank rankMark = eachClass.getAnnotation(SingleRank.class);
			if (rankMark != null) {
				try {
					IRank rank = (IRank) eachClass.newInstance();
					rank.setMaxRankNum(rankMark.maxRankNum());
					rank.loadRank();
					ranks.put(rankMark.type(), rank);
				} catch (InstantiationException e) {
					MyException.catchException(e);
				} catch (IllegalAccessException e) {
					MyException.catchException(e);
				}
			}
		}

	}

	@MessageHandlerAnno(code = GsConst.MsgType.ON_RANK_CHANGE)
	private boolean onRankChange(Msg msg) {
		RankType type = msg.getParam(0);
		String str = msg.getParam(1);
		if (isRankTypeOpen(type)) {
			IRank iRank = ranks.get(type);
			if (iRank != null) {
				iRank.parseStr(str);
			}
		}
		return false;
	}

	@MessageHandlerAnno(code = GsConst.MsgType.ON_RANK_GET)
	private boolean onRankGet(Msg msg) {
		RankType type = msg.getParam(0);
		String str = msg.getParam(1);
		if (ranks.containsKey(type)) {
			IRank iRank = ranks.get(type);
			if (iRank != null) {
				iRank.sendRank(str);
			}
		}
		return false;
	}

	/**
	 * 排行是否开启;
	 * 
	 * @param ranktype
	 * @return
	 */
	public boolean isRankTypeOpen(RankType ranktype) {
		IRank iRank = ranks.get(ranktype);
		if (iRank != null) {
			return iRank.isRankOpen();
		}
		return false;
	}

	/**
	 * 根据排行类型获取排行;
	 * 
	 * @param ranktype
	 * @param list
	 */
	public void getRankByType(RankType ranktype, List<?> list) {
		if (ranks.containsKey(ranktype)) {
			IRank iRank = ranks.get(ranktype);
			iRank.buildRankObjs(list);
		}
	}
}
