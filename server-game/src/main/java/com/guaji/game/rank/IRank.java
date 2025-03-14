package com.guaji.game.rank;

import java.util.List;

import com.guaji.game.protocol.Const.RankType;

/**
 * 排行接口;
 * 
 * @author qianhang
 *
 */
public interface IRank {
	/**
	 * 加载排行, 实现不同的加载策略;
	 */
	public void loadRank();

	/**
	 * 获取前几名排名的排行对象;
	 * 
	 * @param limitNum
	 * @return
	 */
	public List<IRankingObj> getTopLimitRank(int limitNum);

	/**
	 * 更新排行;
	 * 
	 * @param rankObj
	 */
	public void updateRank(IRankingObj rankObj);

	/**
	 * 排行是否开启;
	 * 
	 * @return
	 */
	public boolean isRankOpen();

	/**
	 * 关闭排行;
	 */
	public void closeRank();

	/**
	 * 开启排行;
	 */
	public void openRank();

	/**
	 * 根据id获取该排行信息;
	 * 
	 * @param id
	 * @return
	 */
	public IRankingObj getRankingObjById(int id);

	/**
	 * 组装List排行;
	 * 
	 * @param list
	 */
	public void buildRankObjs(List<?> list);

	/**
	 * 设置排行最大数量;
	 * 
	 * @param maxRankNum
	 */
	public void setMaxRankNum(int maxRankNum);
	

	/**
	 * 设置排序类型
	 * @param rankType
	 */
	public void setRankType(RankType rankType);

	/**
	 * 解析postMsg传入的参数;
	 * 
	 * @param str
	 */
	public void parseStr(String str);

	/**
	 * 给玩家发送排行信息;
	 * 
	 * @param str
	 */
	public void sendRank(String str);
}
