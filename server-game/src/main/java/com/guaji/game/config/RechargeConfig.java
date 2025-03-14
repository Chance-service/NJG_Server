package com.guaji.game.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.guaji.os.MyException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.guaji.game.GsApp;

/**
 * 充值配置文件
 * 
 * @author xulinqs
 * 
 */
public class RechargeConfig {
	/**
	 * 充值币种
	 */
	private String currency = "RMB";
	/**
	 * 金钱钻石比
	 */
	private int moneyGoldRatio = 10;
	
	/**
	 * 默认充值配置
	 */
	private static RechargeConfig defaultRechargeConfig = null;

	/**
	 * 平台充值配置
	 */
	private static Map<String, RechargeConfig> rechargeMap = new ConcurrentHashMap<String, RechargeConfig>();

	/**
	 * 充值项
	 */
	public static class RechargeItem {
		private int id;
		private String name;
		private int type;
		private int amount;
		private int addNum;
		private float costMoney;
		private boolean valid;
		private String productId;
		private String productName;
		
		private String currency;
		private int moneyGoldRatio;
		private int showAddNum;
		private int roleId;
		private int minLevel;
		private int costAmount;
		/**描述*/
		private String desc;
		
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
		
		
		public String getName() {
			return name;
		}
		
		public int getRoleId()
		{
			return roleId;
		}
		
		public int getMinLevel()
		{
			return minLevel;
		}

		public void setName(String string) {
			this.name = string;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
		
		public int getAmount() {
			return amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}

		public int getAddNum() {
			return addNum;
		}

		public void setAddNum(int num) {
			this.addNum = num;
		}

		public float getCostMoney() {
			return costMoney;
		}
		
		public int getCostAmount()
		{
			return costAmount;
		}
		
		public void setCostAmount(int count)
		{
			this.costAmount = count;
		}

		public void setCostMoney(float money) {
			this.costMoney = money;
		}

		public boolean isValid() {
			return valid;
		}
		
		public void setRoleId(int id)
		{
			this.roleId = id;
		}
		
		public void setMinLevel(int level)
		{
			this.minLevel = level;
		}

		public void setValid(boolean valid) {
			this.valid = valid;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public int getMoneyGoldRatio() {
			return moneyGoldRatio;
		}

		public void setMoneyGoldRatio(int moneyGoldRatio) {
			this.moneyGoldRatio = moneyGoldRatio;
		}

        
		public String getProductId() {
			return productId;
		}

		public String getProductName() {
			return productName;
		}

		public int getShowAddNum() {
			return showAddNum;
		}

		public void setShowAddNum(int showAddNum) {
			this.showAddNum = showAddNum;
		}

		public void setProductId(String productId) {
			this.productId = productId;
		}
		
		public void setProductName(String productName) {
			this.productName = productName;
		}
		
		public void setDesc(String desc) {
			this.desc = desc;
		}
		
		public String getDesc() {
			return desc;
		}
	}

	/**
	 * 所有充值列表
	 */
	private Map<Integer, RechargeItem> rechargeCfgMap;

	public RechargeConfig() {
		rechargeCfgMap = new TreeMap<Integer, RechargeItem>();
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getMoneyGoldRatio() {
		return moneyGoldRatio;
	}
	
	public void setMoneyGoldRatio(int moneyGoldRatio) {
		this.moneyGoldRatio = moneyGoldRatio;
	}
	
	public void add(RechargeItem recharge) {
		rechargeCfgMap.put(recharge.getId(), recharge);
	}

	public RechargeItem get(int id) {
		return rechargeCfgMap.get(id);
	}
	
	public Collection<RechargeItem> getAllRechargeItems() {
		return rechargeCfgMap.values();
	}
	
    public Map<Integer,RechargeItem> getAllrechargeCfg() {
    	return rechargeCfgMap ;
    }

	/**
	 * 获取充值配置
	 * 
	 * @param platform
	 * @return
	 */
	public static RechargeConfig getRechargeConfig(String platform) {
		try {
			if (platform == null || "".equals(platform)) {
				if (defaultRechargeConfig == null) {
					String filePath = GsApp.getInstance().getWorkPath() + "xml/rechargeConfig/rechargeConfig.xml";
					defaultRechargeConfig = loadRechargeConfig(new File(filePath));
					return defaultRechargeConfig;
				}
			}

			RechargeConfig rechargeConfig = rechargeMap.get(platform);
			if (rechargeConfig != null) {
				return rechargeConfig;
			}
			
			String filePath = GsApp.getInstance().getWorkPath() + "xml/rechargeConfig/rechargeConfig_" + platform + ".xml";
			File file = new File(filePath);
			if (file.exists()) {
				rechargeConfig = loadRechargeConfig(file);
				if (rechargeConfig != null) {
					rechargeMap.put(platform, rechargeConfig);
					return rechargeConfig;
				}
			}
			
			return defaultRechargeConfig;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}

	/**
	 * 加载充值配置文件
	 * 
	 * @param file
	 * @return
	 */
	private static RechargeConfig loadRechargeConfig(File file) {
		try {
			RechargeConfig rechargeConfig = new RechargeConfig();
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dombuilder = domFactory.newDocumentBuilder();
			
			Element root = dombuilder.parse(new FileInputStream(file)).getDocumentElement();
			NodeList rechargeItems = root.getElementsByTagName("Recharge");
			String currency = root.getAttribute("currency");
			if (currency != null && currency.length() > 0) {
				rechargeConfig.setCurrency(currency);
			}
			
			if (root.hasAttribute("moneyGoldRatio")) {
				rechargeConfig.setMoneyGoldRatio(Integer.valueOf(root.getAttribute("moneyGoldRatio")));
			}
			
			for (int i = 0; i < rechargeItems.getLength(); i++) {
				Node node = rechargeItems.item(i);
				NamedNodeMap attrMap = node.getAttributes();
				RechargeItem rechargeItem = new RechargeItem();
				rechargeItem.setId(Integer.valueOf(attrMap.getNamedItem("id").getNodeValue()));
				rechargeItem.setName(String.valueOf(attrMap.getNamedItem("name").getNodeValue()));
				rechargeItem.setType(Integer.valueOf(attrMap.getNamedItem("type").getNodeValue()));
				rechargeItem.setCostMoney(Float.valueOf(attrMap.getNamedItem("costMoney").getNodeValue()));
				rechargeItem.setAmount(Integer.valueOf(attrMap.getNamedItem("amount").getNodeValue()));
				rechargeItem.setAddNum(Integer.valueOf(attrMap.getNamedItem("addNum").getNodeValue()));
				rechargeItem.setShowAddNum(Integer.valueOf(attrMap.getNamedItem("showAddNum")==null?"0":attrMap.getNamedItem("showAddNum").getNodeValue()));
				rechargeItem.setValid(Boolean.valueOf(attrMap.getNamedItem("valid").getNodeValue()));
				if (attrMap.getNamedItem("productId") != null) {
					rechargeItem.setProductId(attrMap.getNamedItem("productId").getNodeValue());
				}
				
				if (attrMap.getNamedItem("productName") != null) {
					rechargeItem.setProductName(attrMap.getNamedItem("productName").getNodeValue());
				}
				rechargeItem.setCurrency(rechargeConfig.getCurrency());
				rechargeItem.setMoneyGoldRatio(rechargeConfig.getMoneyGoldRatio());
				rechargeItem.setRoleId(Integer.valueOf(attrMap.getNamedItem("roleId").getNodeValue()));
				rechargeItem.setMinLevel(Integer.valueOf(attrMap.getNamedItem("minLevel").getNodeValue()));
				rechargeItem.setCostAmount(Integer.valueOf(attrMap.getNamedItem("costAmount").getNodeValue()));
				rechargeItem.setDesc(attrMap.getNamedItem("desc").getNodeValue());
				
				rechargeConfig.add(rechargeItem);
			}
			return rechargeConfig;
		} catch (Exception e) {
			MyException.catchException(e);
		}
		return null;
	}
	
	/**
	 * 清理相关静态数据
	 */
	protected void clearStaticData() {
	}
}
