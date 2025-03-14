package com.server.paynotice.common;

public interface RequestUri {
	/** 添加注销账户 */
	public String ADD_CANCELUSER = "/add_canceluser";
	/** 删除注销账户 */
	public String DEL_CACNELUSER = "/del_canceluser";
	/** 查询注销账户 */
	public String QUERY_CACNELUSER = "/query_canceluser";
	/** 客户端查询账户状态 */
	public String CLIENT_QUERY_USER = "/client_query_user";
	/** 幽谷支付通知 */
	public String YOUGU_PAYNOTICE = "/youguPayNotice";
	/** 龙啸支付通知 */
	public String LONGXIAO_PAYNOTICE = "/longxiaoPayNotice";

	/** 幽谷支付通知 */
	public String GOOGLE_PAY_HUTUO_PAYNOTICE = "/google/huTuoPayNotice";
	public String APPLE_PAY_HUTUO_PAYNOTICE = "/apple/huTuoPayNotice";
	public String AWS_PAY_HUTUO_PAYNOTICE = "/aws/huTuoPayNotice";
	/** 测试当前服务是否连通 */
	public String TEST_NET = "/testNet";
}
