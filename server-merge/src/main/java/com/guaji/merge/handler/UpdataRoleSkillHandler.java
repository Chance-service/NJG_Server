package com.guaji.merge.handler;

import com.guaji.merge.config.Master;
import com.guaji.merge.config.Slave;
import com.guaji.merge.config.db.DbInfo;
import com.guaji.merge.db.C3P0Impl;

/**
 * 删除角色身上装配的技能
 * 
 * @author tianzhiyuan
 *
 */
public class UpdataRoleSkillHandler extends AbsDataHandler {

	@Override
	public void execute(Master master, DbInfo dbInfo) throws Exception {
		for (Slave slave : master.getSlaveList()) {
			updateRoleKill(slave.getImpl());
		}
	}

	/**
	 * 删除玩家装配技能信息
	 * 
	 * @param impl
	 * @param serverId
	 */
	private void updateRoleKill(C3P0Impl impl) {
		impl.executeUpdate("update role set skill1=0,skill2=0,skill3=0,skill4=0,skill5=0,skill2idListStr=\"[]\",skill3idListStr=\"[]\"");
	}
}
