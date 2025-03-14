package com.guaji.game.module;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.guaji.msg.Msg;
import org.guaji.net.protocol.Protocol;
import org.guaji.xid.GuaJiXID;

import com.guaji.game.GsApp;
import com.guaji.game.config.Hero_NGListCfg;
import com.guaji.game.entity.FormationEntity;
import com.guaji.game.entity.RoleEntity;
import com.guaji.game.player.Player;
import com.guaji.game.player.PlayerModule;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Formation.FormationInfo;
import com.guaji.game.protocol.Formation.HPFormationChangeNameReq;
import com.guaji.game.protocol.Formation.HPFormationEditInfoReq;
import com.guaji.game.protocol.Formation.HPFormationEditInfoRes;
import com.guaji.game.protocol.Formation.HPFormationEditReq;
import com.guaji.game.protocol.Formation.HPFormationEditRes;
import com.guaji.game.protocol.Formation.HPFormationResponse;
import com.guaji.game.protocol.Formation.HPFormationUseReq;
import com.guaji.game.protocol.Formation.HPFormationUseRes;
import com.guaji.game.protocol.HP;
import com.guaji.game.protocol.Status;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.PlayerUtil;

//以后扩展

public class PlayerFormationModule extends PlayerModule {

	List<FormationEntity> formations = null;

	public PlayerFormationModule(Player player) {
		super(player);
		// TODO Auto-generated constructor stub
		// 监听协议
//		listenProto(HP.code.GET_FORMATION_INFO_C_VALUE);
		listenProto(HP.code.GET_FORMATION_EDIT_INFO_C);
		listenProto(HP.code.EDIT_FORMATION_C);
		//listenProto(HP.code.USE_FORMATION_C);
		listenProto(HP.code.UPDATE_FORMATION_NAME_C);

		// 监听内部消息
		listenMsg(GsConst.MsgType.FORMATION_MODIFY);

	}

	/**
	 * 更新
	 *
	 * @return
	 */
	@Override
	public boolean onTick() {
		return super.onTick();
	}

	/**
	 * 消息响应
	 *
	 * @param msg
	 * @return
	 */
	@Override
	public boolean onMessage(Msg msg) {

		if (msg.getMsg() == GsConst.MsgType.FORMATION_MODIFY) {
			onFormationUpdate(msg);
		}
		return super.onMessage(msg);
	}

	@Override
	protected boolean onPlayerLogin() {
		formations = player.getPlayerData().loadFormations();
		fixRoleCount();
		fixRoleFighting();
		return true;

	}

	@Override
	protected boolean onPlayerAssemble() {

		// 容错，版本兼容
		//SysBasicCfg sysCfg = SysBasicCfg.getInstance();
		int maxCount = GsConst.FormationType.FormationMember;

//		for (int i = player.getLevel(); i >= 0; i--) {
//			maxCount = sysCfg.getLevelOpenCount(i);
//
//			if (maxCount != 0) {
//				break;
//			}
//		}

		int extendCount = maxCount - player.getPlayerData().getFormationByType(GsConst.FormationType.FormationBegin).getFightingArrayBoxCount();

		if (extendCount > 0) {
			for (FormationEntity formationEntity : formations) {
				formationEntity.addFinghtingArrayBox(extendCount);
				formationEntity.notifyUpdate();
			}
		}

		for (FormationEntity formationEntity : formations) {
			sendFormation(formationEntity.getType());
		}
		return super.onPlayerAssemble();
	}

	/**
	 * 协议响应
	 */
	@Override
	public boolean onProtocol(Protocol protocol) {
//		if (protocol.checkType(HP.code.GET_FORMATION_INFO_C)) {
//			showFormation(protocol.parseProtocol(HPFormationRequest.getDefaultInstance()));
//			return true;
//		}
		// 获取所有编队信息
		if (protocol.checkType(HP.code.GET_FORMATION_EDIT_INFO_C)) {
			getFormations(protocol.parseProtocol(HPFormationEditInfoReq.getDefaultInstance()));
			return true;
		}
		// 编辑编队
		if (protocol.checkType(HP.code.EDIT_FORMATION_C)) {
			editFormation(protocol.parseProtocol(HPFormationEditReq.getDefaultInstance()));
			return true;
		}
		// 使用编队
//		if (protocol.checkType(HP.code.USE_FORMATION_C)) {
//			useFormation(protocol.parseProtocol(HPFormationUseReq.getDefaultInstance()));
//			return true;
//		}
		// 修改编队名称
		if (protocol.checkType(HP.code.UPDATE_FORMATION_NAME_C)) {
			updateFormationName(protocol.parseProtocol(HPFormationChangeNameReq.getDefaultInstance()));
			return true;
		}
		return super.onProtocol(protocol);
	}

	private void sendFormation(int type) {
		// check range
		if (type < GsConst.FormationType.FormationBegin || type > GsConst.FormationType.FormationEnd) {
			sendError(HP.code.GET_FORMATION_INFO_C_VALUE, Status.error.PARAMS_INVALID);

			return;
		}
		FormationEntity formationEntity = player.getPlayerData().getFormationByType(type);

		HPFormationResponse.Builder builder = HPFormationResponse.newBuilder();
		builder.setType(type);

		List<Integer> fightingArrayList = formationEntity.getFightingArray();
//		List<Integer> assistanceArrayList = formationEntity.getAssistanceArrayList();
//		if (assistanceArrayList.size() > 0) {
//			List<Integer> allArrayList = new ArrayList<>();
//			allArrayList.addAll(fightingArrayList);
//			allArrayList.addAll(assistanceArrayList);
//			builder.setPosCount(allArrayList.size());
//			builder.addAllRoleNumberList(allArrayList);
//		} else {
		int count = formationEntity.getFightingArrayBoxCount();
		builder.setPosCount(count);
		builder.addAllRoleNumberList(fightingArrayList);
//		}

		player.sendProtocol(Protocol.valueOf(HP.code.SHOW_FORMATION_INFO_S_VALUE, builder));
		return;
	}

//	public void showFormation(HPFormationRequest formationRequest) {
//		int type = formationRequest.getType();
//
//		sendFormation(type);
//
//		return;
//	}

	public void getFormations(HPFormationEditInfoReq req) {
		// 数据库的编号ID转编号ID
		int index = req.getIndex();
		
		if (ErroIndexRange(index)) {
			player.sendError(HP.code.GET_FORMATION_EDIT_INFO_C_VALUE, Status.error.PARAMS_INVALID);
			return;
		}

		FormationEntity formationEntity = player.getPlayerData().getFormationByType(index);
		if(formationEntity!=null) {
			List<Integer> lockRole = new ArrayList<Integer>();
			RoleEntity heroEntity = null;
			// 移除已做為突破素材英雄
			lockRole.clear();
			for (Integer roleId : formationEntity.getFightingArray()) {
				if (roleId != 0) {
					heroEntity = player.getPlayerData().getMercenaryById(roleId);
					if (heroEntity == null) {
						lockRole.add(roleId);
					}
				}
			}
			for (Integer heroid : lockRole ) {
				formationEntity.removeMercenaryFighting(heroid);
			}
			
			FormationInfo.Builder info = FormationInfo.newBuilder();
			// 数据库的编号ID转编号ID
			info.setIndex(formationEntity.getType());
			info.setName(formationEntity.getName());
			info.addAllRoleIds(formationEntity.getFightingArray());
			//info.addAllRoleIds(formationEntity.getAssistanceArrayList());
			HPFormationEditInfoRes.Builder builder = HPFormationEditInfoRes.newBuilder();
			builder.setFormations(info);

			player.sendProtocol(Protocol.valueOf(HP.code.GET_FORMATION_EDIT_INFO_S_VALUE, builder));
		}
		
	}

	public void editFormation(HPFormationEditReq req) {
		// 数据库的编号ID转编号ID
		int index = req.getIndex();
		
		if (ErroIndexRange(index)) {
			player.sendError(HP.code.EDIT_FORMATION_C_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		FormationEntity formationEntity = player.getPlayerData().getFormationByType(index);
		if (formationEntity == null) {
			player.sendError(HP.code.EDIT_FORMATION_C_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		List<Integer> defaultItemIds = new ArrayList<>();
		defaultItemIds.addAll(formationEntity.getFightingArray());
		
		List<Integer> teamList = req.getRoleIdsList();
		
		if (teamList.size() != GsConst.FormationType.FormationMember) {
			player.sendError(HP.code.EDIT_FORMATION_C_VALUE, Status.error.PARAMS_INVALID);
			return;			
		}
		
		// check repeat
		Set<Integer> comparaSet = new HashSet<Integer>();
		
		boolean haveHero = false;
		boolean haveNull = false;
		for (Integer indexId : teamList) {
			if (indexId != 0) {
				if (comparaSet.contains(indexId)) { // Role repeat
					player.sendError(HP.code.EDIT_FORMATION_C_VALUE, Status.error.PARAMS_INVALID);
					return;
				} else {
					comparaSet.add(indexId);
				}
				RoleEntity roleEntity = player.getPlayerData().getMercenaryById(indexId);
				if (roleEntity != null) {
					if (roleEntity.isHero()) { // 檢查編輯隊伍裡面有沒有英雄
						haveHero = true;
					}
				} else {
					// 無效Index
					haveNull = true;
				}
			}
		}
		
		if ((!haveHero)||(haveNull)) { // 隊伍不能沒有英雄 ,或無效角色索引
			player.sendError(HP.code.EDIT_FORMATION_C_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		// 取代編隊
		formationEntity.replaceMercenaryTeamList(teamList);
		// 存储编队
		formationEntity.notifyUpdate();
		
		if (index == GsConst.FormationType.FormationBegin || index == GsConst.FormationType.FormationEnd ) { // 設定出戰隊伍(競技場隊伍)額外做的事
			
			List<Integer> itemIds = new ArrayList<>();
			itemIds.addAll(formationEntity.getFightingArray());
						
			// 休息差集
			List<Integer> restList = new ArrayList<>();
			restList.addAll(defaultItemIds);
			restList.removeAll(itemIds);
		
			// 上阵差集
			List<Integer> fightList = new ArrayList<>();
			fightList.addAll(itemIds);
			fightList.removeAll(defaultItemIds);
			
			// 無變動者
			List<Integer> abiding = new ArrayList<>();
			abiding.addAll(formationEntity.getFightingArray());
			abiding.removeAll(fightList);
			
			
			for (Integer roleId : restList) {
				if (roleId > 0) {
					RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
					if (index == GsConst.FormationType.FormationBegin) {
						// 移除出戰
						roleEntity.decStatus(Const.RoleStatus.FIGHTING_VALUE);
						roleEntity.notifyUpdate(true);
					}
					PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
					player.getPlayerData().syncRoleInfo(roleEntity.getId());
				}
			}
		
			for (Integer roleId : fightList) {
				if (roleId > 0) {
					RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
					if (index == GsConst.FormationType.FormationBegin) {
						roleEntity.incStatus(Const.RoleStatus.FIGHTING_VALUE);
						roleEntity.notifyUpdate(true);
					}
					PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
					player.getPlayerData().syncRoleInfo(roleEntity.getId());
				}
			}
				
			for (Integer roleId : abiding) {
				if (roleId > 0) {
					RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
					PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), roleEntity);
					player.getPlayerData().syncRoleInfo(roleEntity.getId());
				}
				
			}
		}

		// 消息返回
		HPFormationEditRes.Builder builder = HPFormationEditRes.newBuilder();
		FormationInfo.Builder info = FormationInfo.newBuilder();
		info.setIndex(req.getIndex());
		info.setName(formationEntity.getName());
		info.addAllRoleIds(formationEntity.getFightingArray());
		builder.setFormations(info);
		player.sendProtocol(Protocol.valueOf(HP.code.EDIT_FORMATION_S_VALUE, builder));
	}

	/**
	 * 廢棄NG無使用
	 * @param req
	 */
	public void useFormation(HPFormationUseReq req) {
		// 数据库的编号ID转编号ID
		int index = req.getIndex();
		FormationEntity formationEntity = player.getPlayerData().getFormationByType(index);

		List<Integer> itemIds = new ArrayList<>();
		itemIds.addAll(formationEntity.getFightingArray());
		//itemIds.addAll(formationEntity.getAssistanceArrayList());
		// 验证编队中角色状态
		for (Integer roleId : itemIds) {
			if (roleId > 0) {
				RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
//				if (roleEntity.getStatus() == Const.RoleStatus.EXPEDITION_VALUE) {
//					player.sendError(HP.code.USE_FORMATION_C_VALUE,
//							Status.error.FORMATION_CAN_NOT_USE_EXPEDITION_VALUE);
//					return;
//				}
				// 隐藏的佣兵不能出战
//				if (roleEntity.isHide()) {
//					player.sendError(HP.code.USE_FORMATION_C_VALUE, Status.error.PARAMS_INVALID);
//					return;
//				}
			}
		}
		// 修改为原默认阵型角色休息状态
		FormationEntity defaultFormationEntity = player.getPlayerData().getFormationByType(GsConst.FormationType.FormationBegin);
		List<Integer> defaultItemIds = new ArrayList<>();
		defaultItemIds.addAll(defaultFormationEntity.getFightingArray());
		//defaultItemIds.addAll(defaultFormationEntity.getAssistanceArrayList());

		// 休息差集
		List<Integer> restList = new ArrayList<>();
		restList.addAll(defaultItemIds);
		restList.removeAll(itemIds);
//		for (Integer roleId : restList) {
//			if (roleId > 0) {
//				RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
//				//roleEntity.setStatus(Const.RoleStatus.RESTTING_VALUE);
//				roleEntity.notifyUpdate(true);
//			}
//		}

		// 上阵差集
		List<Integer> fightList = new ArrayList<>();
		fightList.addAll(itemIds);
		fightList.removeAll(defaultItemIds);
//		for (Integer roleId : fightList) {
//			if (roleId > 0) {
//				RoleEntity roleEntity = player.getPlayerData().getMercenaryById(roleId);
//				roleEntity.setStatus(Const.RoleStatus.FIGHTING_VALUE);
//				roleEntity.notifyUpdate(true);
//			}
//		}

		List<Integer> defaultFightList = new ArrayList<>();
		defaultFightList.addAll(formationEntity.getFightingArray());
//		List<Integer> defaultAssistanceList = new ArrayList<>();
//		defaultAssistanceList.addAll(formationEntity.getAssistanceArrayList());

		// 修改默认阵型
		defaultFormationEntity.setFightingArrayList(defaultFightList);
		//defaultFormationEntity.setAssistanceArrayList(defaultAssistanceList);
		defaultFormationEntity.notifyUpdate();
		/* 缘分 */
//		List<Integer> allMercenary = new ArrayList<>();
//		List<Integer> luckyMercenary = new ArrayList<>();
//		if (player.getPlayerData().getFormationByType(1) != null) {
//			player.getPlayerData().getFormationByType(1).getFightingArray().stream().filter(item -> item != 0)
//					.forEach(item -> allMercenary.add(item));
//
////			player.getPlayerData().getFormationByType(1).getAssistanceArrayList().stream().filter(item -> item != 0)
////					.forEach(item -> allMercenary.add(item));
//
//			allMercenary.forEach(item -> {
//				Optional.ofNullable(player.getPlayerData().getMercenaryByItemId(item)).ifPresent(consumer -> {
//					PlayerUtil.refreshOnlineAttribute(player.getPlayerData(), consumer);
//					if (luckByMercenaryGroup.getRoleLuckyItems().containsKey(consumer.getItemId())) {
//						Optional.ofNullable(luckByMercenaryGroup.getRoleLuckyItems().get(consumer.getItemId()))
//								.ifPresent(luckGroupList -> {
//									if (!luckyMercenary.contains(consumer.getItemId())) {
//										luckyMercenary.add(consumer.getItemId());
//									}
//								});
//					}
//				});
//			});
//		}

		// 同步默认阵型状态
		Msg hawkMsg = Msg.valueOf(GsConst.MsgType.FORMATION_MODIFY,
				GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
		hawkMsg.pushParam(1);
		GsApp.getInstance().postMsg(hawkMsg);
		/* 缘分 */
//		luckyMercenary.forEach(item -> {
//			Optional.ofNullable(player.getPlayerData().getMercenaryByItemId(item)).ifPresent(consumer -> {
//				player.getPlayerData().syncRoleInfo(consumer.getId());
//			});
//		});

		// 出战状态发生变化
		if (restList.size() > 0 || fightList.size() > 0) {
			// 同步主角状态
			player.getPlayerData().syncRoleInfo(player.getPlayerData().getMainRole().getId());
		}
		// 刷新快照
		player.getPlayerData().refreshOnlinePlayerSnapshot();

		// 消息返回
		HPFormationUseRes.Builder builder = HPFormationUseRes.newBuilder();
		builder.setIndex(req.getIndex());
		player.sendProtocol(Protocol.valueOf(HP.code.USE_FORMATION_S_VALUE, builder));
	}

	public void updateFormationName(HPFormationChangeNameReq req) {
		int index = req.getIndex();
		
		if (ErroIndexRange(index)) {
			player.sendError(HP.code.UPDATE_FORMATION_NAME_C_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		
		String name = req.getName();
		FormationEntity formationEntity = player.getPlayerData().getFormationByType(index);
		// 角色名长度不对
		if (name.length() <= 0 || name.length() > 12) {
			player.sendError(HP.code.UPDATE_FORMATION_NAME_C_VALUE, Status.error.PARAMS_INVALID);
			return;
		}
		formationEntity.setName(name);
		formationEntity.notifyUpdate();
	}

	public void onFormationUpdate(Msg msg) {
		int type = (Integer) msg.getParams().get(0);

		if (ErroIndexRange(type)) {
			return;
		}

		sendFormation(type);
		return;
	}
	/**
	 * 檢查 client index range error
	 * @param index
	 * @return
	 */
	private boolean ErroIndexRange(int index) {
		return (index < GsConst.FormationType.FormationBegin || index > GsConst.FormationType.FormationEnd);
	}
	
	/**
	 * 修正1,8編隊隊伍人數
	 */
	
	private void fixRoleCount() {
		Set<Integer> HSet = player.getPlayerData().getHeroIdList();
		Set<Integer> SPSet = player.getPlayerData().getSpriteList();
		List<Integer> defaultItemIds = new ArrayList<>();
		RoleEntity roleEntity = player.getPlayerData().getMercenaryByItemId(Hero_NGListCfg.getFreeCfgIdx().get(0));
		int freeIndex = (roleEntity != null) ? roleEntity.getId() : 0;
		int index = 0 ;
		boolean flag = false;
		int count = 0;
		Set<Integer> delindex = new HashSet<>();
		for (int i = GsConst.FormationType.FormationBegin ; i <= GsConst.FormationType.FormationEnd ; i++) {
			
			if (i == GsConst.FormationType.FormationBegin || i == GsConst.FormationType.FormationEnd) {
				FormationEntity defaultFormationEntity = player.getPlayerData().getFormationByType(i);
				defaultItemIds.clear();
				defaultItemIds.addAll(defaultFormationEntity.getFightingArray());
				index = 0 ;
				flag = false;
				count = 0;
				delindex.clear();
				if (defaultItemIds.size() > 0) {
					if (defaultItemIds.size() < GsConst.FormationType.FormationMember) {
						count = GsConst.FormationType.FormationMember - defaultItemIds.size();
						flag  = true;
					}
					for (int s = 0 ; s < count ; s++) {
						defaultItemIds.add(0);
					}
					
					for (Integer aId :defaultItemIds) {
						if (aId != 0) {
							// index 0-4
							if ((index >=0)&&(index <= GsConst.FormationType.HeroMaxMember-1)) {
								if (!HSet.contains(aId)) {
									delindex.add(index);
								}
							} else {
							// index 5~8
								if (!SPSet.contains(aId)) {
									delindex.add(index);
								}
							}
						}
						index++;
					}
					for (Integer idx :delindex) {
						defaultItemIds.set(idx,0);
						flag  = true;
					}
					
					if (i == GsConst.FormationType.FormationEnd) {
						int checkcount = 0;
						
						for (int j = 0; j < defaultItemIds.size()-1; j++) {
							if (j == GsConst.FormationType.HeroMaxMember) {
								break;
							}
							Integer aId = defaultItemIds.get(j);
							checkcount  = checkcount + aId;
						}
						// 檢查英雄隊伍是否是空的
						
						if (checkcount == 0) { // 競技隊伍沒英雄,編隊至一隻英雄
							defaultItemIds.set(0, freeIndex);
							roleEntity.incStatus(Const.RoleStatus.FIGHTING_VALUE);
							flag  = true;
						}
					}
				} else if (defaultItemIds.size() == 0) { 
					for (int j = 0 ; j < GsConst.FormationType.FormationMember; j++ ) {
						if (i == GsConst.FormationType.FormationEnd) { // 競技隊伍空的,編隊至少要一人
							if ((freeIndex != 0) && (j == 0)) {
								roleEntity.incStatus(Const.RoleStatus.FIGHTING_VALUE);
								defaultItemIds.add(freeIndex);
								continue;
							}
						}
						defaultItemIds.add(0);
					}
					flag = true;
				}
				
				if (flag) {
					defaultFormationEntity.setFightingArrayList(defaultItemIds);
					defaultFormationEntity.notifyUpdate();
					
					// 同步默认阵型状态
					Msg hawkMsg = Msg.valueOf(GsConst.MsgType.FORMATION_MODIFY,
							GuaJiXID.valueOf(GsConst.ObjType.PLAYER, player.getId()));
					hawkMsg.pushParam(i);
					GsApp.getInstance().postMsg(hawkMsg);
					if (i == GsConst.FormationType.FormationEnd) {
						// 刷新快照
						player.getPlayerData().refreshOnlinePlayerSnapshot();
					}
				}
			}
		}		
	}
	
	/**
	 * 修正出戰隊伍戰鬥狀態
	 */
	private void fixRoleFighting() {
		List<Integer> defaultItemIds = new ArrayList<>();
		FormationEntity defaultFormationEntity = player.getPlayerData().getFormationByType(GsConst.FormationType.FormationBegin);
		defaultItemIds.clear();
		defaultItemIds.addAll(defaultFormationEntity.getFightingArray());
		List<RoleEntity> herolist = player.getPlayerData().getHero();
		for (RoleEntity heroEntity:herolist) {
			if (defaultItemIds.contains(heroEntity.getId())){ //出戰編隊中
				if (heroEntity.getStatus() != Const.RoleStatus.FIGHTING_VALUE && heroEntity.getStatus() != Const.RoleStatus.MIXTASK_VALUE) {
					heroEntity.incStatus(Const.RoleStatus.FIGHTING_VALUE);
				}
			} else {
				if (heroEntity.getStatus() == Const.RoleStatus.FIGHTING_VALUE || heroEntity.getStatus() == Const.RoleStatus.MIXTASK_VALUE) {
					heroEntity.decStatus(Const.RoleStatus.FIGHTING_VALUE);
				}
			}
		}
	}
	
	public void resetElementSkill(){
		
	}

}
