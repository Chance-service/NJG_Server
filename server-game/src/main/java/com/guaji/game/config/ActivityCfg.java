package com.guaji.game.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.guaji.os.GuaJiTime;
import org.guaji.os.MyException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.guaji.game.GsApp;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsConst.ActivityTimeType;

/**
 * 活动配置
 */
public class ActivityCfg {

	/**
	 * 
	 */
	private static List<ActivityItem> activityList = new LinkedList<ActivityItem>();

	/**
	 * 单项活动配置
	 * 
	 * @author xulinqs
	 */
	public static class ActivityItem {

		private int id;

		private GsConst.ActivityTimeType activityTimeType;

		private String name;

		private int levelLimit;

		private Map<String, Object> params = new HashMap<String, Object>();

		public void addParam(String key, Object value) {
			params.put(key, value);
		}

		@SuppressWarnings("unchecked")
		public <T> T getParam(String key) {
			if (params.containsKey(key)) {
				return (T) params.get(key);
			}
			return null;
		}

		public Map<String, Object> getParamsMap() {
			return this.params;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public GsConst.ActivityTimeType getActivityTimeType() {
			return activityTimeType;
		}

		public void setActivityTimeType(GsConst.ActivityTimeType activityTimeType) {
			this.activityTimeType = activityTimeType;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String toString() {
			return "id:" + id + ",timeType:" + activityTimeType.ordinal() + ",name:" + name + ",params:" + params;
		}

		public int getLevelLimit() {
			return levelLimit;
		}

		public void setLevelLimit(int levelLimit) {
			this.levelLimit = levelLimit;
		}
	}

	/**
	 * 加载配置
	 */
	public static void load() {
		activityList.clear();

		String filePath = GsApp.getInstance().getWorkPath() + "xml/activity.xml";
		File file = new File(filePath);
		DocumentBuilder dombuilder;
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			dombuilder = domFactory.newDocumentBuilder();
			Element root = dombuilder.parse(new FileInputStream(file)).getDocumentElement();
			NodeList dataItems = root.getElementsByTagName("data");
			for (int i = 0; i < dataItems.getLength(); i++) {
				ActivityItem activityItem = new ActivityItem();
				Node node = dataItems.item(i);
				NamedNodeMap attrMap = node.getAttributes();
				activityItem.id = Integer.valueOf(attrMap.getNamedItem("id").getNodeValue());
				int timeType = Integer.valueOf(attrMap.getNamedItem("timeType").getNodeValue());
				activityItem.activityTimeType = ActivityTimeType.valueOf(timeType);
				activityItem.name = attrMap.getNamedItem("name").getNodeValue();
				activityItem.levelLimit = Integer.valueOf(attrMap.getNamedItem("levelLimit").getNodeValue());
				for (Node propertyNode = node.getFirstChild(); propertyNode != null; propertyNode = propertyNode.getNextSibling()) {
					if (propertyNode.getNodeName().equals("property")) {
						String key = propertyNode.getAttributes().getNamedItem("key").getNodeValue();
						String type = propertyNode.getAttributes().getNamedItem("type").getNodeValue();
						String value = propertyNode.getTextContent();
						Object val = null;
						if ("int".equals(type)) {
							val = Integer.valueOf(value);
						} else if ("date".equals(type)) {
							val = GuaJiTime.DATE_FORMATOR(value);
						} else if ("float".equals(type)) {
							val = Float.valueOf(value);
						} else {
							val = value;
						}
						activityItem.addParam(key, val);
					}
				}
				activityList.add(activityItem);
			}
		} catch (Exception e) {
			MyException.catchException(e);
		}

	}

	/**
	 * 根据活动Id获得相关配置
	 * 
	 * @return
	 */
	public static ActivityItem getActivityItem(int activityId) {
		for (ActivityItem item : activityList) {
			if (item.getId() == activityId) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 活动列表
	 * 
	 * @return
	 */
	public static List<ActivityItem> getActivityItemList() {
		return activityList;
	}

	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
		activityList.clear();
	}
}
