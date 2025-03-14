package com.server.paynotice.common;

public class AppConst {
	public static final String ROOT_DIR = System.getProperty("user.dir");
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String SOURCE_DIR = ROOT_DIR + FILE_SEPARATOR + "conf" + FILE_SEPARATOR;

	public static final String SETTINGS = "configuration.properties";
	public static final String MYBATIS = "mybatis.cfg.xml";// "mybatis.xml";
	public static final String LOG = "log.properties";
	public static final String PAY_NOTICE = "payNotice.xml";

	/** 谷歌支付 */
	public static final String PAY_CHANNEL_GOOGLE = "google";
	/** 谷歌支付 */
	public static final String PAY_CHANNEL_APPLE = "apple";

	/** 亚马逊支付 */
	public static final String PAY_CHANNEL_AWS = "AWS";

	/** 谷歌支付订单校验配置参数 */
	public static final String CLIENT_ID = "1088571333954-6b34u2qdijmmbtkgo416jc870pljgcrc.apps.googleusercontent.com";
	public static final String CLIENT_SECRET = "Mfp3uVGhLxeM_BT_wsAdK8RU";
	public static final String REFRESH_TOKEN = "1/3MHr2xGGLPcG-3WOv3hjmWiG39bZ4KnfzKTq1YyntV0";
	public static final String OAUTH_URL = "https://accounts.google.com/o/oauth2/token";
	public static final String GRANT_TYPE= "refresh_token";
	public static final String CHK_ORDERURL= "https://www.googleapis.com/androidpublisher/v2/applications/";
	
	
	

	/** 幽谷支付通知 */
	public static final String SDK_YOUGU = "yougu";
	/** 龙啸支付通知 */
	public static final String SDK_LONGXIAO = "longxiao";
	/** 虎拓支付游戏通知 */
	public static final String SDK_HUTUO = "huTuo";

}
