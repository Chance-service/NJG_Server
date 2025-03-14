package com.server.paynotice.log;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;

import com.server.paynotice.common.AppConst;

public class MyRollingFileAppender extends DailyRollingFileAppender {
	@Override
	public boolean isAsSevereAsThreshold(Priority priority) {
		return this.getThreshold().equals(priority);
	}
	@Override
	public void setFile(String file) {
		// TODO Auto-generated method stub
		//fileName = AppConst.ROOT_DIR+AppConst.FILE_SEPARATOR+file.trim();
		
		fileName =file.trim();
	}
	
}
