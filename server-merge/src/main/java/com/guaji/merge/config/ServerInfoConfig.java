package com.guaji.merge.config;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.guaji.merge.App;

public class ServerInfoConfig {

	private static Log logger = App.logger;
	/** 配置文件 */
	private static final String serverInfo = "conf/ServerInfo.xml";

	private Master master = new Master();

	private static ServerInfoConfig serverConfig = new ServerInfoConfig();

	private ServerInfoConfig() {
		readXML(serverInfo);
	}

	public static ServerInfoConfig getServerInfoConfig() {
		return serverConfig;
	}

	/**
	 * 读取XMl
	 * 
	 * @param fileName
	 *            文件名
	 */
	private void readXML(String fileName) {
		try {
			parse(new File(fileName));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void parse(File file) throws Exception {
		logger.info("读取服务器配置文件--->分别是:");
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);

			NodeList typeNodes = doc.getElementsByTagName("master");
			if (typeNodes == null || typeNodes.getLength() == 0) {
				throw new Exception("不存在master标签");
			}

			Node masterNode = typeNodes.item(0);
			master.setDbInfo(getDbInfo(masterNode.getAttributes()));
			logger.info("主服务器信息:" + master.getDbInfo().toString());

			NodeList slaveNodes = masterNode.getChildNodes();
			if (slaveNodes == null || slaveNodes.getLength() == 0) {
				throw new Exception("不存在slave标签");
			}

			for (int i = 0; i < slaveNodes.getLength(); i++) {
				Node slaveNode = slaveNodes.item(i);
				if (slaveNode instanceof Element) {
					Slave slave = new Slave();
					slave.setDbInfo(getDbInfo(slaveNode.getAttributes()));
					master.addSlave(slave);

					logger.info("从服务器信息:" + slave.getDbInfo().toString());
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * 获取数据库配置信息
	 * 
	 * @param attr
	 *            节点属性
	 * @return
	 * @throws Exception
	 */
	private DbConfigInfo getDbInfo(NamedNodeMap attr) throws Exception {
		DbConfigInfo dbInfo = new DbConfigInfo();
		for (int k = 0; k < attr.getLength(); k++) {
			Node attribute = attr.item(k);
			String attName = attribute.getNodeName();
			if (attName.equalsIgnoreCase("ip")) {
				dbInfo.setIp(attribute.getNodeValue());
			} else if (attName.equalsIgnoreCase("port")) {
				dbInfo.setPort(Integer.valueOf(attribute.getNodeValue()));
			} else if (attName.equalsIgnoreCase("db")) {
				dbInfo.setDb(attribute.getNodeValue());
			} else if (attName.equalsIgnoreCase("username")) {
				dbInfo.setUsername(attribute.getNodeValue());
			} else if (attName.equalsIgnoreCase("password")) {
				dbInfo.setPassword(attribute.getNodeValue());
			} else if (attName.equalsIgnoreCase("addServerId")) {
				dbInfo.setAddServerId(Integer.valueOf(attribute.getNodeValue()));
			} else if (attName.equalsIgnoreCase("serverId")) {
				dbInfo.setServerId(Integer.valueOf(attribute.getNodeValue()));
			} else {
				throw new Exception("不存在" + attName + "此属性");
			}
		}
		dbInfo.checkEmpty();
		return dbInfo;
	}

	public Master getMaster() {
		return master;
	}

}
