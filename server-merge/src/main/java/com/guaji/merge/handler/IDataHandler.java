package com.guaji.merge.handler;

import com.guaji.merge.config.Master;
import com.guaji.merge.config.db.DbInfo;

/**
 * otherHandler接口
 * 
 * @author tianzhiyuan
 *
 */
public interface IDataHandler {
	/**
	 * 执行方法
	 * 
	 * @param master
	 */
	void execute(Master master, DbInfo dbInfo) throws Exception;
}
