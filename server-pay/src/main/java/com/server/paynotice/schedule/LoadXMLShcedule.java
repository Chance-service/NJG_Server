package com.server.paynotice.schedule;

import java.io.File;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.server.paynotice.xmlparser.PayNoticeUrlManager;

public class LoadXMLShcedule implements Job {
	private static Logger logger = Logger.getLogger(LoadXMLShcedule.class);
	//@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		String location = PayNoticeUrlManager.getXmlPath();
		File file = new File(location);
		if(!file.exists()){
			return;
		}
		
		long modifyTime = file.lastModified();
		long lastmodifyTime = PayNoticeUrlManager.getLastModifyTime();
		if(modifyTime==lastmodifyTime){
			return;
		}
		PayNoticeUrlManager.loadPayNoticeXML();
		logger.info("reload paynoticeXml success!");
	}

}
