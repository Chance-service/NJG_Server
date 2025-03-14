package com.guaji.game.manager.gvg;

import java.util.Comparator;

import com.guaji.game.bean.GvgBattleResultBean;

/**
* @author 作者 E-mail:Jeremy@163.com
* @version 创建时间：Apr 9, 2019 2:42:40 PM
* 类说明
*/
public class HoldeCityComparator implements Comparator<GvgBattleResultBean>{

	@Override
	public int compare(GvgBattleResultBean o1, GvgBattleResultBean o2) {
		if(o1 == null || o2 == null) {
			return 0;
		}
		//总积分比较
		if(o1.getnTotalScore()>o2.getnTotalScore())
		{
			return -1;
		}
		
		if(o1.getnTotalScore()<o2.getnTotalScore())
		{
			return 1;
		}

		
		if(o1.getAllianceId()>o2.getAllianceId())
		{
			return -1;
		}
		
		
		if(o1.getAllianceId()<o2.getAllianceId())
		{
			return 1;
		}
		
		
	
		
		return 0;
	}
}
