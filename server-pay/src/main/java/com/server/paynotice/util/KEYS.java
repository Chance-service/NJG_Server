package com.server.paynotice.util;

public class KEYS {

	//苹果支付需要传递字段
    public final static String REQ_APPLE_CHANNEL_ORDER = "transactionid";
    public final static String REQ_APPLE_SKU = "productid";
    public final static String REQ_APPLE_RECEIPT = "receipt";
    public final static String REQ_APPLE_CUSTOMER_ID = "uid";
    public final static String REQ_APPLE_SERVER_ID = "serverId";
    public final static String REQ_APPLE_SKU_PRICE = "price";
    public final static String REQ_APPLE_CURRENCY = "currencyCode";
    public final static String REQ_APPLE_EXTRA = "cparam";

    //谷歌支付需要传递字段
    public final static String REQ_GOOGLE_JSON = "googleOriginalJson";
    public final static String REQ_GOOGLE_SIGNATURE = "googleSignature";
    public final static String REQ_GOOGLE_CUSTOMER_ID = "uid";
    public final static String REQ_GOOGLE_GAME_NAME = "gamename";
    public final static String REQ_GOOGLE_SERVER_ID = "sid";
    public final static String REQ_GOOGLE_SKU_PRICE = "price";
    public final static String REQ_GOOGLE_CURRENCY = "currencyCode";
    public final static String REQ_GOOGLE_EXTRA = "cparam";
    
    //亚马逊支付需要传递字段
    public final static String REQ_AWS_CHANNEL_ORDER = "transactionid";
    public final static String REQ_AWS_SKU = "productid";
    public final static String REQ_AWS_RECEIPT = "receipt";
    public final static String REQ_AWS_CUSTOMER_ID = "uid";
    public final static String REQ_AWS_SERVER_ID = "serverId";
    public final static String REQ_AWS_SKU_PRICE = "price";
    public final static String REQ_AWS_CURRENCY = "currencyCode";
    public final static String REQ_AWS_EXTRA = "cparam";
    public final static String REQ_AWS_PLATFORMUID = "puid";

    
    public final static String REQ_PLATFORM = "platform";
    
    public final static int HTTP_STATUS_BAD_REQUEST =400;
    
    
}
