package com.guaji.cs.common;

import java.util.Comparator;

import com.guaji.cs.db.RankData;

/**
 * 排行榜比较器
 * 
 * @author Nannan.Gao
 * @date 2017-8-14 14:46:48
 */
public class RankComparator implements Comparator<RankData> {
	
	private static final RankComparator instance = new RankComparator();
	
	private RankComparator() {
		
	}
	
	public static RankComparator getInstance() {
		return instance;
	}

	 @Override
     public int compare(RankData rank_1, RankData rank_2) {
		 if(rank_1 == null || rank_2 == null) {
			return 0;
		}
		// 积分比较
		if (rank_1.getScore() > rank_2.getScore()) {
			return -1;
		}
		if (rank_1.getScore() < rank_2.getScore()) {
			return 1;
		}
		return 0;
     }

}
