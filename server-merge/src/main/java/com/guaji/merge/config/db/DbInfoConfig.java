package com.guaji.merge.config.db;

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

public class DbInfoConfig {

	private static Log logger = App.logger;
	/** 配置文件 */
	private static final String dbInfoXml = "conf/DbInfo.xml";

	private DbInfo dbInfo = new DbInfo();

	private static DbInfoConfig dbconfig = new DbInfoConfig();

	public static DbInfoConfig getDbConfig() {
		return dbconfig;
	}

	/**
	 * 读取XMl
	 * 
	 * @param fileName
	 *            文件名
	 * @throws Exception
	 */
	public void readXML() throws Exception {
		parse(new File(dbInfoXml));
	}

	private void parse(File file) throws Exception {
		logger.info("读取dbInfo配置文件");
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);

			Node dbNode = doc.getElementsByTagName("dbInfo").item(0);
			Node dbAttribute = dbNode.getAttributes().item(1);
			if (dbAttribute.getNodeName().equalsIgnoreCase("everyCount")) {
				dbInfo.setEveryCount(Integer.valueOf(dbAttribute.getNodeValue()));
			} else {
				throw new Exception("不存在" + dbAttribute.getNodeName() + "此属性");
			}
			 dbAttribute = dbNode.getAttributes().item(0);
			if (dbAttribute.getNodeName().equalsIgnoreCase("deleteCount")) {
				dbInfo.setDeleteCount(Integer.valueOf(dbAttribute.getNodeValue()));
			} else {
				throw new Exception("不存在" + dbAttribute.getNodeName() + "此属性");
			}


			NodeList typeNodes = doc.getElementsByTagName("table");
			if (typeNodes == null || typeNodes.getLength() == 0) {
				throw new Exception("不存在table标签");
			}

			for (int i = 0; i < typeNodes.getLength(); i++) {
				Table table = new Table();
				Node tableNode = typeNodes.item(i);
				if (tableNode instanceof Element) {
					NamedNodeMap attr = tableNode.getAttributes();
					Node attribute = null;
					String attName = null;
					for (int j = 0; j < attr.getLength(); j++) {
						attribute = attr.item(j);
						attName = attribute.getNodeName();
						if (attName.equalsIgnoreCase("name")) {
							table.setName(attribute.getNodeValue());
						} else if (attName.equalsIgnoreCase("isMerge")) {
							table.setIsMerge(Integer.valueOf(attribute
									.getNodeValue()));
						} else if(attName.equalsIgnoreCase("isDelete")){
							table.setIsDelete(Integer.valueOf(attribute
									.getNodeValue()));
						} else if (attName.equalsIgnoreCase("primaryKey")) {
							// set primary key
							table.setPrimaryKey(attribute.getNodeValue());
						}
					}

					NodeList fieldNodes = tableNode.getChildNodes();
					for (int k = 0; k < fieldNodes.getLength(); k++) {
						Node fieldNode = fieldNodes.item(k);
						if (fieldNode != null && fieldNode instanceof Element) {
							Field field = new Field();
							field.setSelfTable(table.getName());
							attr = fieldNode.getAttributes();
							for (int j = 0; j < attr.getLength(); j++) {
								attribute = attr.item(j);
								attName = attribute.getNodeName();
								if (attName.equalsIgnoreCase("name")) {
									field.setName(attribute.getNodeValue());
								} else if (attName.equalsIgnoreCase("type")) {
									field.setType(Integer.valueOf(attribute
											.getNodeValue()));
								} else if (attName
										.equalsIgnoreCase("tablename")) {
									field.setTablename(attribute.getNodeValue());
								} else if (attName.equalsIgnoreCase("column")) {
									field.setColumn(attribute.getNodeValue());
								} else if (attName
										.equalsIgnoreCase("separator")) {
									field.setSeparator(attribute.getNodeValue());
								} else if (attName.equalsIgnoreCase("leftChar")) {
									field.setLeftChar(attribute.getNodeValue());
								}else if (attName
										.equalsIgnoreCase("autoStartId")) {
									field.setTempValue(Integer.valueOf(attribute
											.getNodeValue()));
								} 
								else if (attName
										.equalsIgnoreCase("rightChar")) {
									field.setRightChar(attribute.getNodeValue());
								} else {
									throw new Exception("不存在" + attName + "此属性");
								}
							}
							table.addField(field);
						}
					}
					dbInfo.addTable(table);
				}
			}

			dbInfo.addStruct();
			logger.info("table数量【" + dbInfo.getTableList().size() + "】");
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	public DbInfo getDbInfo() {
		return dbInfo;
	}

}
