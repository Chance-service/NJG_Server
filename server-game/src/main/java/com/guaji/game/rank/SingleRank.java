package com.guaji.game.rank;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.guaji.game.protocol.Const.RankType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleRank {
	/**
	 * 排名类型;
	 * 
	 * @return
	 */
	RankType type();
	
	int maxRankNum();
}
