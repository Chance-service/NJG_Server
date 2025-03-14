package com.guaji.game.manager;

import java.util.List;

import org.guaji.annotation.MessageHandlerAnno;
import org.guaji.app.AppObj;
import org.guaji.db.DBManager;
import org.guaji.msg.Msg;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.config.RankServerCfg;
import com.guaji.game.entity.FirstEntity;
import com.guaji.game.util.GsConst;

public class RecordFirstManager extends AppObj {
	
	private static FirstEntity firstEntity = null;
	
	public RecordFirstManager(GuaJiXID xid) {
		super(xid);
		if (instance == null) {
			instance = this;
		}
	}
	/**
	 * 全局对象, 便于访问
	 */
	private static RecordFirstManager instance = null;
	/**
	 * 获取全局实例对象
	 */
	public static RecordFirstManager getInstance() {
		return instance;
	}
	
	/**
	 * 初始化
	 */
	public void init() {
		loadFirstEntity();
	}
	
	/**
	 * 加载好友列表
	 *
	 * @return
	 */
	public FirstEntity loadFirstEntity() {
		if (firstEntity == null) {
			
			List<FirstEntity> FirstEntityList = DBManager.getInstance()
					.query("from FirstEntity where invalid = 0");
			
			if (FirstEntityList != null && FirstEntityList.size() != 0) {
				firstEntity = FirstEntityList.get(0);
			} else {
				firstEntity = new FirstEntity();
				DBManager.getInstance().create(firstEntity);
			}
			
			firstEntity.convert();
		}
		return firstEntity;
	}
	
	public FirstEntity getFirstEntity() {
		return firstEntity;
	}
	
	@MessageHandlerAnno(code = GsConst.MsgType.ON_SERVER_MISSION_CHANGE)
	private void onFirstChange(Msg msg) {
		int type = msg.getParam(0);
		int playerId = msg.getParam(1);
		int value = msg.getParam(2);
		
		if (firstEntity == null) {
			return;
		}
		
		if (RankServerCfg.isTypeAllDone(type,firstEntity.getAllCfgId())){
			return;
		}

		List<Integer> cList = RankServerCfg.getCompleteCfgId(type, value);

		if (cList.size() > 0) {
			boolean save = false;
			for(Integer cfgid : cList) {
				if (!firstEntity.isAleadyDone(cfgid)){
					firstEntity.setCfgId(cfgid, playerId);
					save =true;
				}
			}
			if (save) {
				firstEntity.notifyUpdate(true);
			}
		}
		
	}

}
