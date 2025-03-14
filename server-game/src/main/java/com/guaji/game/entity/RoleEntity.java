package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.config.ConfigManager;
import org.guaji.db.DBEntity;
import org.guaji.log.Log;
import org.guaji.msg.Msg;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.GsApp;
import com.guaji.game.attribute.Attribute;
import com.guaji.game.config.BadgeGachaListCfg;
import com.guaji.game.config.EquipCfg;
import com.guaji.game.config.HeroAwakeCfg;
import com.guaji.game.config.HeroStarCfg;
import com.guaji.game.config.Hero_NGListCfg;
import com.guaji.game.config.RoleEquipCfg;
import com.guaji.game.config.SysBasicCfg;
import com.guaji.game.manager.RankManager;
import com.guaji.game.manager.RecordFirstManager;
import com.guaji.game.player.Player;
import com.guaji.game.protocol.Const;
import com.guaji.game.protocol.Const.RankType;
import com.guaji.game.util.GameUtil;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;
import com.guaji.game.util.PlayerUtil;

/**
 * 角色基础数据
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "role")
public class RoleEntity extends DBEntity {
    @Id
    @GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @Column(name = "id", unique = true)
    private int id = 0;

    @Column(name = "playerId")
    private int playerId = 0;
    
 // type 1.leader為職業別  2.英雄為Herolist index
    @Column(name = "type")
    private int type = 0;
    
    @Column(name = "itemId")
    private int itemId = 0;
    
    @Column(name = "name", nullable = false)
    private String name = null;
    
    @Column(name = "fightvalue")
    private int fightvalue = 0;
    
    @Column(name = "attr")
    private int attr = 0;

    @Column(name = "rebirthStage")
    private int rebirthStage;

    @Column(name = "level")
    private int level = 0;

    @Column(name = "exp")
    private long exp = 0;

    /**
    *	  激活状态
     */
    @Column(name = "roleState")
    private int roleState = 0;

    // 魂魄数量
    @Column(name = "soulCount")
    private int soulCount = 0;

    @Column(name = "equip1")
    private long equip1 = 0;

    @Column(name = "equip2")
    private long equip2 = 0;

    @Column(name = "equip3")
    private long equip3 = 0;

    @Column(name = "equip4")
    private long equip4 = 0;

    @Column(name = "equip5")
    private long equip5 = 0;

    @Column(name = "equip6")
    private long equip6 = 0;

    @Column(name = "equip7")
    private long equip7 = 0;

    @Column(name = "equip8")
    private long equip8 = 0;

    @Column(name = "equip9")
    private long equip9 = 0;

    @Column(name = "equip10")
    private long equip10 = 0;

    @Column(name = "skill1")
    private int skill1 = 0;

    @Column(name = "skill2")
    private int skill2 = 0;

    @Column(name = "skill3")
    private int skill3 = 0;

    @Column(name = "skill4")
    private int skill4 = 0;

    @Column(name = "skill5")
    private int skill5 = 0;

    @Column(name = "attrInfo")
    private String attrInfo;
    /**
     * 遊戲行為狀態
     */
    @Column(name = "status")
    private int status = 0;

    /**
     * skill2 list
     */
    @Column(name = "skill2idListStr")
    private String skill2idListStr;

    /**
     * skill3 list
     */
    @Column(name = "skill3idListStr")
    private String skill3idListStr;

    /**
     * 星，经验
     */
    @Column(name = "starExp")
    private int starExp = 0;

    /**
     * 突破星(標記)
     */
    @Column(name = "starLevel")
    private int starLevel = 0;

    /**
     * 阶
     */
    @Column(name = "stageLevel")
    private int stageLevel = 0;
    
    /**
       * 覺醒等級
     */
    @Column(name = "stageLevel2")
    private int stageLevel2 = 0;
    /**
     * 已解锁光环
     */
    @Column(name = "ringStr")
    private String ringStr;

    /**
     * 光环信息
     */
    @Transient
    private List<Integer> ringList;

    @Column(name = "elements")
    private String elementInfo = "";

    @Transient
    private List<Long> elementIds;

    @Column(name = "power", nullable = false)
    protected int power;

    @Column(name = "refreshTime", nullable = false)
    protected long refreshTime;

    /**
     * 是否隐藏（皮肤佣兵默认true，普通佣兵默认false）
     */
//    @Column(name = "hide", nullable = false)
//    protected boolean hide;

    @Column(name = "roleBaptizeAttrStr")
    private String roleBaptizeAttrStr = "";
    
    @Column(name = "skinId")
    private int skinId = 0;

    /**
     * 是否在抽到SSR/UR时发过广播
     */
    @Column(name = "broadcasted", nullable = false)
    private boolean broadcasted = false;

    @Column(name = "createTime", nullable = false)
    protected Date createTime = null;

    @Column(name = "updateTime")
    protected Date updateTime;

    @Column(name = "invalid")
    protected boolean invalid;

    @Transient
    protected Hero_NGListCfg roleCfg;
    @Transient
    protected HeroStarCfg StarCfg;
    @Transient
    protected HeroAwakeCfg AwakeCfg;
    
//    @Transient
//    protected NewHeroClassCfg classCfg;
    /**
     * 佣兵育成属性
     */
    @Transient
    private Attribute roleBaptizeAttr;

    /**
     * 	競技場屬性
     */
    @Transient
    protected Attribute ArenaAttr;

    /**
     * 躶体属性
     */
    @Transient
    protected Attribute trainAttr;
    /**
     * 装备属性集合
     */
    @Transient
    protected Attribute attribute;
    /**
     * 裸體属性集合
     */
    @Transient
    protected Attribute Nakedattribute;

    @Transient
    private List<Integer> skill2idList;

    @Transient
    private List<Integer> skill3idList;

    @Column(name = "badge1", columnDefinition = "int default 0")
    private long badge1 = 0;

    @Column(name = "badge2", columnDefinition = "int default 0")
    private long badge2 = 0;

    @Column(name = "badge3", columnDefinition = "int default 0")
    private long badge3 = 0;

    @Column(name = "badge4", columnDefinition = "int default 0")
    private long badge4 = 0;

    @Column(name = "badge5", columnDefinition = "int default 0")
    private long badge5 = 0;

    @Column(name = "badge6", columnDefinition = "int default 0")
    private long badge6 = 0;
    
    @Column(name = "nakedFight", columnDefinition = "int default 0")
    private int nakedFight = 0;

    @Transient
    private Map<Integer, Long> badgeMap = new HashMap<>(6);

    public RoleEntity() {
        this.setExp(0);
        this.setLevel(1);
        this.status = 0;
        this.attrInfo = "";
        this.createTime = GuaJiTime.getCalendar().getTime();
        this.trainAttr = new Attribute();
        this.ArenaAttr = new Attribute();
        this.Nakedattribute = new Attribute();
        this.ringList = new ArrayList<>();
        this.skill2idList = new LinkedList<>();
        this.skill3idList = new LinkedList<>();
        this.skill2idListStr = GsonUtil.getJsonInstance().toJson(this.skill2idList);
        this.skill3idListStr = GsonUtil.getJsonInstance().toJson(this.skill3idList);
        this.ringStr = GsonUtil.getJsonInstance().toJson(this.ringList);
        this.elementIds = new LinkedList<>();
        // 初始化九个元素格子
        for (int i = 0; i < SysBasicCfg.getInstance().getMaxElementSize(); i++) {
            if (this.elementIds.size() <= i) {
                this.elementIds.add(0l);
            }
        }
        //this.hide = false;
        this.broadcasted = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getType() {
        return type;
    }
    /**
     * type 1.leader為職業別  2.英雄為Herolist index 4.隨從(免費英雄)
     * @return
     */
    public void setType(int type) {
        this.type = type;
    }
    
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getFightValue() {
    	return this.fightvalue;
    }
    
    public void setFightValue(int value) {
    	int oldvalue = getFightValue();
    	this.fightvalue = value;
    	if ((oldvalue != this.fightvalue)&&(getAttr()!=0)) {
    		Player player = getOnlinePlayer();
    		RankType ranktype = getRankType();
    		RoleEntity hightHero = null;
    		if (player != null) { // 更新最強該屬性英雄上排行榜
    			hightHero = player.getPlayerData().getBestHeroByAttr(getAttr());
				if (hightHero != null) { 
					postRankChangeMsg(ranktype,hightHero.getItemId(),hightHero.getLevel(),
							hightHero.getStarLevel(),hightHero.getFightValue(),hightHero.getSkinId());
					postSvrChangeMsg(hightHero.getPlayerId(),hightHero.getFightValue());
				}    				
    		}
    	}
    }
    
    
    public int getNakedFight() {
		return nakedFight;
	}

	public void setNakedFight(int nakedFight) {
		this.nakedFight = nakedFight;
	}

	public int getAttr() {
    	return this.attr;
    }
    
    public void setAttr(int attr) {
    	this.attr = attr;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getEquip1() {
        return equip1;
    }

    public void setEquip1(long equip1) {
        this.equip1 = equip1;
    }

    public long getEquip2() {
        return equip2;
    }

    public void setEquip2(long equip2) {
        this.equip2 = equip2;
    }

    public long getEquip3() {
        return equip3;
    }

    public void setEquip3(long equip3) {
        this.equip3 = equip3;
    }

    public long getEquip4() {
        return equip4;
    }

    public void setEquip4(long equip4) {
        this.equip4 = equip4;
    }

    public long getEquip5() {
        return equip5;
    }

    public void setEquip5(long equip5) {
        this.equip5 = equip5;
    }

    public long getEquip6() {
        return equip6;
    }

    public void setEquip6(long equip6) {
        this.equip6 = equip6;
    }

    public long getEquip7() {
        return equip7;
    }

    public void setEquip7(long equip7) {
        this.equip7 = equip7;
    }

    public long getEquip8() {
        return equip8;
    }

    public void setEquip8(long equip8) {
        this.equip8 = equip8;
    }

    public long getEquip9() {
        return equip9;
    }

    public void setEquip9(long equip9) {
        this.equip9 = equip9;
    }

    public long getEquip10() {
        return equip10;
    }

    public void setEquip10(long equip10) {
        this.equip10 = equip10;
    }

    public int getSkill1() {
        return skill1;
    }

    public void setSkill1(int skill1) {
        this.skill1 = skill1;
    }

    public int getSkill2() {
        return skill2;
    }

    public void setSkill2(int skill2) {
        this.skill2 = skill2;
    }

    public int getSkill3() {
        return skill3;
    }

    public void setSkill3(int skill3) {
        this.skill3 = skill3;
    }

    public int getSkill4() {
        return skill4;
    }

    public void setSkill4(int skill4) {
        this.skill4 = skill4;
    }

    public int getSkill5() {
        return skill5;
    }

    public void setSkill5(int skill5) {
        this.skill5 = skill5;
    }

    public int getStatus() {
        return status;
    }

//    public void setStatus(int status) {
//        this.status = status;
//    }
    /**
     * 增加狀態
     * @param status
     */
    public void incStatus(int status) {
    	if (this.status != status) {
    		this.status = Math.min(this.status+status, Const.RoleStatus.MIXTASK_VALUE);
    	}
    }
    
    /**
       * 移除狀態
     * 
     * @param status
     */
    public void decStatus(int status) {
    	this.status = Math.max(this.status - status,Const.RoleStatus.RESTTING_VALUE);
    }

    public int getStageLevel() {
        return stageLevel;
    }

    public void setStageLevel(int stageLevel) {
        this.stageLevel = stageLevel;
    }
    
    public int getStageLevel2() {
        return stageLevel2;
    }

    public void setStageLevel2(int stageLevel2) {
        this.stageLevel2 = stageLevel2;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public long getBadge1() {
        return badge1;
    }

    public void setBadge1(long badge1) {
        this.badge1 = badge1;
    }

    public long getBadge2() {
        return badge2;
    }

    public void setBadge2(long badge2) {
        this.badge2 = badge2;
    }

    public long getBadge3() {
        return badge3;
    }

    public void setBadge3(long badge3) {
        this.badge3 = badge3;
    }

    public long getBadge4() {
        return badge4;
    }

    public void setBadge4(long badge4) {
        this.badge4 = badge4;
    }

    public long getBadge5() {
        return badge5;
    }

    public void setBadge5(long badge5) {
        this.badge5 = badge5;
    }

    public long getBadge6() {
        return badge6;
    }

    public void setBadge6(long badge6) {
        this.badge6 = badge6;
    }

    public boolean isEquipment() {
    	for(long id :getEquipIds()) {
    		if (id > 0) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public int getTier() {
//    	if (getClassCfg() != null) { 
//    		return getClassCfg().getTier();
//    	}
    	return 0;
    }
    
//    public int getSurmountLv() {
//		int surmountLv = GsConst.LIMIT_SURMOUNT_LV_T1;
//		
//		if (getTier() == 1) {
//			surmountLv = GsConst.LIMIT_SURMOUNT_LV_T1;
//		} else if (getTier() == 2) {
//			surmountLv = GsConst.LIMIT_SURMOUNT_LV_T2;
//		}
//		
//		return surmountLv;
//    }    
    /**
        * 回傳角色星數對等級限制
     */
    public int getLimitLevel() {
    	if (this.isArmy()) {
    		return this.getStarCfg().getLevel();
    	}
    	return 0;
    }
    
    /**
	   * 元素屬性 leader為 無屬性0
	 * @return
	 */
	public int getElement() {
		if (this.isHero()) {
			return this.getRoleCfg().getattr();
		}
		return 0;
	}
     
     public int getATKSpeed() {
      	return  getRoleCfg().getATKSpeed();
      }
     
 	public int getNATK() {
		return getRoleCfg().getNATK();
	}
	
	public int getATKMP() {
		return getRoleCfg().getATKMP();
	}

	public int getDEFMP() {
		return getRoleCfg().getDEFMP();
	}
	
	public String getSKMP() {
		return getRoleCfg().getSKMP();
	}
	
	public float getClassCorrection() {
		return getRoleCfg().getClassCorrection();
	}
	
    /**
     * 获取角色配置文件
     *
     * @return
     */
    public Hero_NGListCfg getRoleCfg() {
        if ((roleCfg == null)) {
            roleCfg = ConfigManager.getInstance().getConfigByKey(Hero_NGListCfg.class, getItemId());
        }
        return roleCfg;
    }
    
    /**
     * 	获取角色星星配置表
     *
     * @return
     */
    public HeroStarCfg getStarCfg() {
        if ((StarCfg == null)) {
        	StarCfg = HeroStarCfg.getHeroStarCfg(getItemId(),getStarLevel());
        }
        return StarCfg;
    }
    
    /**
     *	獲取英雄覺醒配置表
     */
    public HeroAwakeCfg getAwakeCfg() {
    	if (AwakeCfg == null) {
    		AwakeCfg = ConfigManager.getInstance().getConfigByKey(HeroAwakeCfg.class,getStageLevel2());
    	}
    	return AwakeCfg;
    }
    
        
    /**
        * 獲取英雄角色眼睛技能
     */
//     public List<Integer> getEyeSkill(){
//    	 List<Integer> aList = new ArrayList<>();
//    	 if (this.isHero()) {
//    		 if (this.getRoleCfg().geteye_L() == this.getRoleCfg().geteye_R()) {
//    			 aList.add(this.getRoleCfg().geteye_L()+ SkillType.Eye_MARK);
//    		 } else {
//    			 aList.add(this.getRoleCfg().geteye_L()+ SkillType.Eye_MARK);
//    			 aList.add(this.getRoleCfg().geteye_R()+ SkillType.Eye_MARK);
//    		 }
//    	 }
//    	 return aList;
//     }
    
    /**
     * 获取職業配置文件
     *
     * @return
     */
//    public NewHeroClassCfg getClassCfg() {
//        if (classCfg == null) {
//        	classCfg = ConfigManager.getInstance().getConfigByKey(NewHeroClassCfg.class, this.getProfession());
//        }
//        return classCfg;
//    }
    
    /**
     * 获取角色職業
     *
     * @return
     */
    public int getProfession() {
    	return this.getRoleCfg().getJob();
    }
    /**
       *  是否為英雄
     */
    public boolean isHero() {
    	if (GameUtil.isHero(this.getType())) {
    		return true;
    	}
    	return false;
    }
    
    /**
     *  是否為精靈
   */
  public boolean isSprite() {
  	if (GameUtil.isSprite(this.getType())) {
  		return true;
  	}
  	return false;
  }
  /**
   * 是否部隊 Hero+Sprite
   * @return
   */
  public boolean isArmy() {
	  	if (GameUtil.isHero(this.getType())||GameUtil.isSprite(this.getType())) {
	  		return true;
	  	}
	  	return false;
  }    
    /**
     * 是否為魔法職業
     *
     * @return
     */
    public boolean getIsMagic() {
        return this.getRoleCfg().IsMagic();
    }

    /**
     * 获取角色属性
     *
     * @return
     */
    public Attribute getAttribute() {
        if (attribute == null) {
            attribute = new Attribute();
        }
        return attribute;
    }
    
    /**
     * 获取裸體角色属性
     *
     * @return
     */
    public Attribute getNakedattribute() {
        if (Nakedattribute == null) {
        	Nakedattribute = new Attribute();
        }
        return Nakedattribute;
    }

    public void setTrainAttr(Attribute trainAttr) {
        this.trainAttr = trainAttr;
        if (trainAttr == null) {
            this.attrInfo = "";
        } else {
            this.attrInfo = trainAttr.toString();
        }
    }


    public boolean isBroadcasted() {
        return broadcasted;
    }

    public void setBroadcasted(boolean broadcasted) {
        this.broadcasted = broadcasted;
    }

    public Attribute getTrainAttr() {
        return this.trainAttr;
    }

    /**
     * 检测装备是否穿戴在本角色身上
     *
     * @param equipId
     * @return
     */
    public boolean checkEquipInDress(long equipId) {
        if (equipId > 0) {
            return equipId == equip1 || equipId == equip2 || equipId == equip3 || equipId == equip4 || equipId == equip5 || equipId == equip6
                    || equipId == equip7 || equipId == equip8 || equipId == equip9 || equipId == equip10;
        }
        return false;
    }

    /**
     * 检测元素是否穿戴在本角色身上
     *
     * @param equipId
     * @return
     */
    public boolean checkElementInDress(long elementId) {
        if (this.elementIds.contains(elementId)) {
            return true;
        }
        return false;
    }

    /**
     * 获得角色指定部位的装备id
     *
     * @param part
     * @return
     */
    public long getPartEquipId(int part) {
        switch (part) {
            case Const.equipPart.HELMET_VALUE:
                return equip1;

            case Const.equipPart.RING_VALUE:
                return equip2;

            case Const.equipPart.BELT_VALUE:
                return equip3;

            case Const.equipPart.CUIRASS_VALUE:
                return equip4;

            case Const.equipPart.WEAPON1_VALUE:
                return equip5;

            case Const.equipPart.WEAPON2_VALUE:
                return equip6;

            case Const.equipPart.LEGGUARD_VALUE:
                return equip7;

            case Const.equipPart.SHOES_VALUE:
                return equip8;

            case Const.equipPart.GLOVE_VALUE:
                return equip9;

            case Const.equipPart.NECKLACE_VALUE:
                return equip10;

            default:
                break;
        }
        return 0;
    }

    /**
     * 设置角色指定部位的装备id
     *
     * @param part
     * @return
     */
    public void setPartEquipId(int part, long equipId) {
        switch (part) {
            case Const.equipPart.HELMET_VALUE:
                equip1 = equipId;
                break;

            case Const.equipPart.RING_VALUE:
                equip2 = equipId;
                break;

            case Const.equipPart.BELT_VALUE:
                equip3 = equipId;
                break;

            case Const.equipPart.CUIRASS_VALUE:
                equip4 = equipId;
                break;

            case Const.equipPart.WEAPON1_VALUE:
                equip5 = equipId;
                break;

            case Const.equipPart.WEAPON2_VALUE:
                equip6 = equipId;
                break;

            case Const.equipPart.LEGGUARD_VALUE:
                equip7 = equipId;
                break;

            case Const.equipPart.SHOES_VALUE:
                equip8 = equipId;
                break;

            case Const.equipPart.GLOVE_VALUE:
                equip9 = equipId;
                break;

            case Const.equipPart.NECKLACE_VALUE:
                equip10 = equipId;
                break;

            default:
                break;
        }
    }

    public int getBadgePartById(long id) {
        Map.Entry<Integer, Long> result = badgeMap.entrySet().stream().filter(item -> item.getValue() == id).findFirst().orElse(null);
        if (result != null) {
            return result.getKey();
        }
        return 0;
    }

    public void setBadgePart(long id, int part) {
        if (part == 1) {
            badge1 = id;
        }
        if (part == 2) {
            badge2 = id;
        }
        if (part == 3) {
            badge3 = id;
        }
        if (part == 4) {
            badge4 = id;
        }
        if (part == 5) {
            badge5 = id;
        }
        if (part == 6) {
            badge6 = id;
        }
        badgeMap.put(part, id);
    }

    public Map<Integer, Long> getBadgeMap() {
        if (badgeMap.size() == 0) {
            initBadgeMap();
        }
        return badgeMap;
    }

    public Map<Integer, Long> initBadgeMap() {
        badgeMap.clear();
        badgeMap.put(1, badge1);
        badgeMap.put(2, badge2);
        badgeMap.put(3, badge3);
        badgeMap.put(4, badge4);
        badgeMap.put(5, badge5);
        badgeMap.put(6, badge6);
        return badgeMap;
    }

    public void setBadgeList(int part, long id) {
        badgeMap.put(part - 1, id);
    }

    /**
     * 获取技能id
     *
     * @param index
     * @return
     */
    public int getSkillId(int index) {
    	
    	if (index < 0) {
    		return 0;
    	}
    	
    	List<Integer> Skills = getStarCfg().getSkillList();
    	
    	if ((Skills.size() <= 0) || (index > Skills.size()-1)) {
    		return 0;
    	}
    	
        return Skills.get(index);
    }

    /**
     * 设置技能id
     *
     * @param index
     * @param skillId
     */
//    public void setSkillId(int index, int skillId) {
//        switch (index) {
//            case 0:
//                skill1 = skillId;
//                break;
//
//            case 1:
//                skill2 = skillId;
//                break;
//
//            case 2:
//                skill3 = skillId;
//                break;
//
//            case 3:
//                skill4 = skillId;
//                break;
//
//            case 4:
//                skill5 = skillId;
//                break;
//
//            default:
//                break;
//        }
//    }

    /**
     * 获取装备技能Ids
     *
     * @return
     */
//    public int[] getSkillIds() {
//        return new int[]{skill1, skill2, skill3, skill4, skill5};
//    }

    /**
     * 获取装备ids
     *
     * @return
     */
    public long[] getEquipIds() {
        return new long[]{equip1, equip2, equip3, equip4, equip5, equip6, equip7, equip8, equip9, equip10};
    }

    /**
     * 序列化光环Str
     */
    public void convertRing() {
        if (ringStr != null && ringStr.length() > 0) {
            ringList.clear();
            this.ringList = GsonUtil.getJsonInstance().fromJson(this.ringStr, new TypeToken<LinkedList<Integer>>() {
            }.getType());
        }
    }

    /**
     * 转换培育属性
     */
    public void convert() {
        if (attrInfo == null || attrInfo.equals("")) {
            this.trainAttr = new Attribute();
            this.attrInfo = "";
        } else {
            this.trainAttr = Attribute.valueOf(attrInfo);
        }

        if (skill2idListStr != null && skill2idListStr.length() > 0) {
            skill2idList = GsonUtil.getJsonInstance().fromJson(skill2idListStr, new TypeToken<LinkedList<Integer>>() {
            }.getType());
        } else {
            skill2idList = new LinkedList<Integer>();
        }
        if (skill3idListStr != null && skill3idListStr.length() > 0) {
            skill3idList = GsonUtil.getJsonInstance().fromJson(skill3idListStr, new TypeToken<LinkedList<Integer>>() {
            }.getType());
        } else {
            skill3idList = new LinkedList<Integer>();
        }

        if (this.elementInfo != null && this.elementInfo.length() > 0) {
            String[] ss = this.elementInfo.split(",");
            for (int i = 0; i < ss.length; i++) {
                if (!ss[i].trim().isEmpty()) {
                    this.elementIds.set(i, Long.valueOf(ss[i].trim()));
                }
            }
        }
        this.roleBaptizeAttr = Attribute.valueOf(roleBaptizeAttrStr);
    }

    public Attribute getBaseAttr() {
        Attribute attribute = new Attribute();
        
        Hero_NGListCfg ClassCfg = getRoleCfg();
        HeroStarCfg StarCfg = getStarCfg();
        HeroAwakeCfg AwakeCfg = getAwakeCfg();
        float StarRatio = StarCfg == null ? 1.0f : StarCfg.getRatio();
        float AwakeRatio = AwakeCfg == null ? 1.0f : AwakeCfg.getStates();
        
        if (StarCfg == null) {
        	Log.errPrintln(String.format("StarCfg == null , playerId:%d itemId :%d , StarLevel:%d ",getPlayerId(),getItemId(),getStarLevel()));
        }

        if (ClassCfg != null) {
        	Attribute baseattr = ClassCfg.getattribute();
        	int rolelv = this.getLevel();
        	if (rolelv >= 1) {
        		int str = Math.round(((baseattr.getValue(Const.attr.STRENGHT) + ((rolelv-1)*ClassCfg.getStrenghtRate()))*StarRatio)*AwakeRatio);
        		attribute.add(Const.attr.STRENGHT, str);
        		int agi = Math.round(((baseattr.getValue(Const.attr.AGILITY) + ((rolelv-1)*ClassCfg.getAgilityRate()))*StarRatio)*AwakeRatio);
        		attribute.add(Const.attr.AGILITY, agi);
        		int intell = Math.round(((baseattr.getValue(Const.attr.INTELLECT) + ((rolelv-1)*ClassCfg.getIntellectRate()))*StarRatio)*AwakeRatio);
        		attribute.add(Const.attr.INTELLECT, intell);
        		int sta = Math.round(((baseattr.getValue(Const.attr.STAMINA) + ((rolelv-1)*ClassCfg.getStaminaRate()))*StarRatio)*AwakeRatio);
        		attribute.add(Const.attr.STAMINA, sta);
        		
        		for (Map.Entry<Const.attr, Integer> entry : baseattr.getAttrMap().entrySet()) {
	    			if ((entry.getKey() == Const.attr.STRENGHT)||(entry.getKey() == Const.attr.INTELLECT)||
	    					(entry.getKey() == Const.attr.AGILITY)|| (entry.getKey() == Const.attr.STAMINA)) {
	    				continue;
	    			}
	    			if (entry.getValue() == 0) {
	    				continue;
	    			}
	    			attribute.add(entry.getKey(),entry.getValue());
        		}        		
        	}else{

        		attribute = baseattr;
        	}
        }
        return attribute;
    }

    public List<Integer> getSkill2idList() {
        return skill2idList;
    }

    public void setSkill2idList(List<Integer> skill2idList) {
        this.skill2idList = skill2idList;
        this.skill2idListStr = GsonUtil.getJsonInstance().toJson(skill2idList);
    }

    public String getSkill3idListStr() {
        return skill3idListStr;
    }

    public void setSkill3idListStr(String skill3idListStr) {
        this.skill3idListStr = skill3idListStr;
    }

    public List<Integer> getSkill3idList() {
        return skill3idList;
    }

    public void setSkill3idList(List<Integer> skill3idList) {
        this.skill3idList = skill3idList;
        this.skill3idListStr = GsonUtil.getJsonInstance().toJson(skill3idList);
    }

    public int getStarExp() {
        return starExp;
    }

    public void setStarExp(int starExp) {
        this.starExp = starExp;
    }

    public int getStarLevel() {
        return starLevel;
    }
    /**
     * 檢查激活狀態
     * @return
     */
    public int getRoleState() {
        return roleState;
    }

    public void setRoleState(int roleState) {
        this.roleState = roleState;
    }

    public int getSoulCount() {
        return soulCount;
    }

    public void setSoulCount(int soulCount) {
        this.soulCount = soulCount;
    }

    public void setStarLevel(int starLevel) {
        this.starLevel = starLevel;
        StarCfg = HeroStarCfg.getHeroStarCfg(getItemId(),getStarLevel()); // 更新配置表
    }

    public Long getElementByIndex(int index) {
        if (this.elementIds.size() > index) {
            return this.elementIds.get(index);
        }
        return 0L;
    }

    public void setElementId(int index, Long i) {
        if (this.elementIds.size() > index) {
            this.elementIds.set(index, i);
        }
        this.elementInfo = GameUtil.join(this.elementIds, ",");
    }

    public int getElementIndexById(long eleId) {
        return this.elementIds.indexOf(eleId);
    }

    public int getRebirthStage() {
        return rebirthStage;
    }

    public void setRebirthStage(int rebirthStage) {
        this.rebirthStage = rebirthStage;
    }

    /**
     * 增加解锁技能
     *
     * @param ringId
     */
    public void addRing(int ringId) {
        ringList.add(ringId);
        ringStr = GsonUtil.getJsonInstance().toJson(ringList);
    }

    public String getRingStr() {
        return ringStr;
    }

    public void setRingStr(String ringStr) {
        this.ringStr = ringStr;
    }

    public List<Integer> getRingList() {
        return ringList;
    }

    public void setRingList(List<Integer> ringList) {
        this.ringList = ringList;
    }

    public Attribute getArenaAttr() {
        return ArenaAttr;
    }

    public void setArenaAttr(Attribute ArenaAttr) {
        this.ArenaAttr = ArenaAttr;
    }

    /**
     * 获取已学技能
     *
     * @return
     */
    public List<Integer> getSkillAll() {
        List<Integer> skillList = new ArrayList<>();
        if (isArmy()) {
	        int skillSlotNum = GameUtil.getSkillSlotNumByLevel(this);
	        HeroStarCfg StarCfg = getStarCfg();
	        if (StarCfg != null) {
		        List<Integer> Skills = StarCfg.getSkillList();
		    	for (int i = 0; i < skillSlotNum; i++) {
		    		skillList.add(Skills.get(i));
		    	}
	        }
	        Player player = getOnlinePlayer();
	        //Map <Integer,Integer> skillMap = new HashMap<>();
	        if (player != null) {
	        	
		        // 專武技能
		        long equipId = getPartEquipId(Const.equipPart.NECKLACE_VALUE);
		        EquipEntity equipEntity = player.getPlayerData().getEquipById(equipId);
		        if (equipEntity != null) {
		        	EquipCfg equipCfg = ConfigManager.getInstance().getConfigByKey(EquipCfg.class, equipEntity.getEquipId());
		        	if (equipCfg != null) {
		        		int equipExclusiveId = equipCfg.getRoleAttrId();
		        		RoleEquipCfg roleEquipCfg = ConfigManager.getInstance().getConfigByKey(RoleEquipCfg.class, equipExclusiveId);
		        		if ((roleEquipCfg != null)&&(roleEquipCfg.getRoleIdList().contains(getItemId()))) {
		        			if (!equipCfg.getSkill().isEmpty()){
			        			String [] skillAry = equipCfg.getSkill().split(",");
			        			for (String skillstr :skillAry) {
			        				skillList.add(Integer.valueOf(skillstr.trim()));
			        			}
		        			}
		        		}
		        	}
		        }
		        
				//獲取英雄角色(徽章)符文技能
				for (Long badge :getBadgeMap().values()) {
					if (badge == 0) {
						continue;
					}
					BadgeEntity badgeEntity = player.getPlayerData().getBadgeById(badge);
					if (badgeEntity != null) {
						if (badgeEntity.getSkillList().size() > 0) {
							for (int reId : badgeEntity.getSkillList())
							{
								int cfgId = (reId > GsConst.BADGE_LOCK_MASK)? reId % GsConst.BADGE_LOCK_MASK : reId;
								
								BadgeGachaListCfg scfg = ConfigManager.getInstance().getConfigByKey(BadgeGachaListCfg.class, cfgId);
								if (scfg != null) {
									skillList.add(scfg.getSkill());
								}
							}
						}
					}
				}
	        }

        }
        return skillList;
    }
    
    /*
     * 檢查是否沒有裝備Skill
     */
//    public boolean isNoSkill(){
//    	int[] arrayS = getSkillIds();
//    	
//    	for (int i : arrayS) {
//    		if (i > 0) {
//    			return false;
//    		}
//    	}
//    	return true;
//    }

    /**
     * 解锁技能
     *
     * @param skillId
     */
    public void addRoleSkill(int skillId) {
        if (getSkill1() != 0) {
            setSkill2(skillId);
        } else {
            setSkill1(skillId);
        }
    }

    public int getSkinId () {
        return skinId;
    }

    public void setSkinId(int id) {
        this.skinId = id;
    }

    public Attribute getRoleBaptizeAttr() {
        return roleBaptizeAttr;
    }

    public void setRoleBaptizeAttr(Attribute roleBaptizeAttr) {
        this.roleBaptizeAttr = roleBaptizeAttr;
    }

    public String getRoleBaptizeAttrStr() {
        return roleBaptizeAttrStr;
    }
    
//    public String getDataString() {
//    	HeroMeta dataCfg = getRoleCfg();
//    	if (dataCfg != null) {
//    		//return String.format("%s_%d_%d",dataCfg.getDataString(),gettokenId(),getId());
//    		return String.format("%s_%d_%d",dataCfg.getDataString(),0,getId());
//    	}
//    	return "0";
//    }
    
//    public String getIconString() {
//    	HeroMeta dataCfg = getRoleCfg();
//    	if (dataCfg != null) {
//    		return dataCfg.getIconString();
//    	}
//    	return "0";
//    }
     /**
         * 取職名稱
     * @return
     */
//    public String getClassName() {
//    	return getClassCfg().getName();
//    }
    

    @Override
    public void notifyUpdate() {
        this.roleBaptizeAttrStr = this.roleBaptizeAttr.toString();
        super.notifyUpdate();
    }
    
	/**
	 * 是否為該角色的Skin
	 * @param skinId
	 * @return
	 */
	public boolean isMySkin(int skinId) {
		return getRoleCfg().isRoleSkin(skinId);
	}
	
	private void postRankChangeMsg(RankType type,int itemId,int level,int starLevel,int Score,int skinId) {
		//playerId,itemId,fightvalue,level,starLevel,skinId
		Msg questMsg = Msg.valueOf(GsConst.MsgType.ON_RANK_CHANGE);
		questMsg.pushParam(type);
		questMsg.pushParam(String.format("%d,%d,%d,%d,%d,%d", this.getPlayerId(),itemId, Score , level, starLevel,skinId));
		GsApp.getInstance().postMsg(RankManager.getInstance().getXid(), questMsg);
	}
	
	private Player getOnlinePlayer() {
		Player player = PlayerUtil.queryPlayer(playerId);
		return player;
	}
	
	private RankType getRankType() {
		if (getAttr() == GsConst.HeroAttrType.Fire) {
			return RankType.HERO_FIRE_RANK;
		} else if (getAttr() == GsConst.HeroAttrType.Water) {
			return RankType.HERO_WATER_RANK;
		}else if (getAttr() == GsConst.HeroAttrType.Wind) {
			return RankType.HERO_WIND_RANK;
		}else if (getAttr() == GsConst.HeroAttrType.Light) {
			return RankType.HERO_LIGHT_RANK;
		}else if (getAttr() == GsConst.HeroAttrType.Dark) {
			return RankType.HERO_DARK_RANK;
		}
		return RankType.HERO_FIRE_RANK;
	}
	
	/**
	 * 屬性英雄戰力總值改變回報伺服器成就管理
	 * @param type
	 * @param playerId
	 * @param playerScore
	 */
	private void postSvrChangeMsg(int playerId, int playerScore) {
		Msg questMsg = Msg.valueOf(GsConst.MsgType.ON_SERVER_MISSION_CHANGE);
		int type = getMissionTypeByAttr();
		questMsg.pushParam(type);
		questMsg.pushParam(playerId);
		questMsg.pushParam(playerScore);
		GsApp.getInstance().postMsg(RecordFirstManager.getInstance().getXid(), questMsg);
	}
	

	
	private int getMissionTypeByAttr() {
		if (getAttr() == GsConst.HeroAttrType.Fire) {
			return GsConst.SvrMissionType.Hero_Attr_Fire;
		} else if (getAttr() == GsConst.HeroAttrType.Water) {
			return GsConst.SvrMissionType.Hero_Attr_Water;
		}else if (getAttr() == GsConst.HeroAttrType.Wind) {
			return GsConst.SvrMissionType.Hero_Attr_Wind;
		}else if (getAttr() == GsConst.HeroAttrType.Light) {
			return GsConst.SvrMissionType.Hero_Attr_Light;
		}else if (getAttr() == GsConst.HeroAttrType.Dark) {
			return GsConst.SvrMissionType.Hero_Attr_dark;
		}
		return 0;
	}
        
}
