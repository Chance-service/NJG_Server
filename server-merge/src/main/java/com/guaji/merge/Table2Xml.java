package com.guaji.merge;

import org.w3c.dom.Document;

import com.guaji.merge.config.Master;
import com.guaji.merge.config.ServerInfoConfig;
import com.guaji.merge.util.XmlUtil;

/**
 * 将表格映射为xml
 * 注:这个不用每次都执行
 * 
 * @author ksfzhaohui
 * 
 */
public class Table2Xml {

	public static void main(String[] args) throws Exception {
		Master master = ServerInfoConfig.getServerInfoConfig().getMaster();
		master.connect();

		Document doc = XmlUtil.generateXml(master);
		XmlUtil.outputXml(doc, "DbInfo.xml");
	}

}
