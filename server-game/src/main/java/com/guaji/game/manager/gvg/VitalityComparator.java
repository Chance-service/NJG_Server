package com.guaji.game.manager.gvg;

import java.util.Comparator;

import com.guaji.game.entity.GvgAllianceEntity;

/**
 * 元气比较器
 */
public class VitalityComparator implements Comparator<GvgAllianceEntity> {

	@Override
	public int compare(GvgAllianceEntity entity0, GvgAllianceEntity entity1) {
		if(entity0 == null || entity1 == null) {
			return 0;
		}
		if (entity0.equals(entity1)) {
			return 0;
		}
		// 积分比较
		if (entity0.getAddCount() > entity1.getAddCount()) {
			return -1;
		}
		if (entity0.getAddCount() < entity1.getAddCount()) {
			return 1;
		}
		// 公会ID比较
		if(entity0.getAllianceLevel() > entity1.getAllianceLevel()) {
			return -1;
		}
		if(entity0.getAllianceLevel() < entity1.getAllianceLevel()) {
			return 1;
		}
		
		if(entity0.getAllianceId() > entity1.getAllianceId()) {
			return -1;
		}
		if(entity0.getAllianceId() < entity1.getAllianceId()) {
			return 1;
		}
		
		return 0;
	}

}
