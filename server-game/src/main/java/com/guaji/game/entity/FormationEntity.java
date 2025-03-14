package com.guaji.game.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.guaji.db.DBEntity;
import org.guaji.os.GuaJiTime;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.reflect.TypeToken;
import com.guaji.game.util.GsConst;
import com.guaji.game.util.GsonUtil;

@Entity
@Table(name = "formation")
@SuppressWarnings("serial")
public class FormationEntity extends DBEntity {

    @Id
    @GenericGenerator(name = "AUTO_INCREMENT", strategy = "native")
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @Column(name = "id", unique = true)
    private int id = 0;

    @Column(name = "playerId")
    private int playerId;

    @Column(name = "type")
    private int type;

    @Column(name = "fightingArray")
    private String fightingArray;

    @Transient
    private List<Integer> fightingArrayList = null;

    @Column(name = "createTime")
    private Date createTime;

    @Column(name = "updateTime")
    private Date updateTime;

    @Column(name = "invalid")
    private boolean invalid;

//    @Column(name = "assistanceArray")
//    private String assistanceArray;

    @Column(name = "name")
    private String name;

//    @Transient
//    private List<Integer> assistanceArrayList = null;

    public FormationEntity() {
        createTime = GuaJiTime.getCalendar().getTime();
        this.fightingArrayList = new ArrayList<Integer>(GsConst.FormationType.FormationMember);//初始队列元素为5

        for (int i = 0; i < GsConst.FormationType.FormationMember; i++) {
            fightingArrayList.add(i, 0);
        }

        this.fightingArray = GsonUtil.getJsonInstance().toJson(this.fightingArrayList);

//        this.assistanceArrayList = new ArrayList<>();
//        this.assistanceArray = GsonUtil.getJsonInstance().toJson(this.assistanceArrayList);
    }

    /**
     * 生成单一阵型对象
     *
     * @param playerId
     * @return
     */
    public static FormationEntity valueOf(int playerId, int type) {
        FormationEntity formationEntity = new FormationEntity();
        formationEntity.playerId = playerId;
        formationEntity.type = type;

        return formationEntity;
    }

    public void convertData() {
        if (this.fightingArray != null && !"".equals(this.fightingArray)) {
            this.fightingArrayList = GsonUtil.getJsonInstance().fromJson(this.fightingArray, new TypeToken<List<Integer>>() {
            }.getType());
        }
    }

    /**
     * 增加应援副将
     *
     * @param number
     * @return
     */
//    public int addMercenaryAssistance(int number) {
//        if (number <= 0) {
//            return -1;
//        }
//        if (this.assistanceArrayList.contains(number)) {
//            return -1;
//        }
//        int popNumber = 0;
//
//        for (int i = 0; i < this.assistanceArrayList.size(); i++) {
//            if (this.assistanceArrayList.get(i) == 0) {
//                this.assistanceArrayList.set(i, number);
//
//                this.assistanceArray = GsonUtil.getJsonInstance().toJson(this.assistanceArrayList);
//
//                return popNumber;
//            }
//        }
//
//        popNumber = this.assistanceArrayList.get(0);
//        this.assistanceArrayList.remove(0);
//        this.assistanceArrayList.add(number);
//
//        this.assistanceArray = GsonUtil.getJsonInstance().toJson(this.assistanceArrayList);
//
//        return popNumber;
//
//    }
    /**
     * 取代編隊
     *
     * @param
     */
    public boolean replaceMercenaryTeamList(List<Integer> Teamlist) {
    	if (Teamlist.size() != GsConst.FormationType.FormationMember)
    	{
    		return false;
    	}
    	this.fightingArrayList.clear();
    	this.fightingArrayList.addAll(Teamlist);
    	this.fightingArray = GsonUtil.getJsonInstance().toJson(this.fightingArrayList);
    	return true;
    }

    /**
     * 添加一个出战佣兵,如果满了弹出佣兵number
     *
     * @param number
     */
    public int addMercenaryFighting(int number) {
        if (number == 0) {
            return -1;
        }
        if (this.fightingArrayList.contains(number)) {
            return -1;
        }

        int popNumber = 0;

        for (int i = 0; i < this.fightingArrayList.size(); i++) {
            if (this.fightingArrayList.get(i) == 0) {
                this.fightingArrayList.set(i, number);

                this.fightingArray = GsonUtil.getJsonInstance().toJson(this.fightingArrayList);

                return popNumber;
            }
        }

        popNumber = this.fightingArrayList.get(0);
        this.fightingArrayList.remove(0);
        this.fightingArrayList.add(number);

        this.fightingArray = GsonUtil.getJsonInstance().toJson(this.fightingArrayList);

        return popNumber;
    }

    /**
     * 替换一个出战佣兵
     *
     * @param fromItemId
     * @param toItemId
     */
    public boolean replaceMercenaryFighting(int fromItemId, int toItemId) {
        if (fromItemId <= 0 || toItemId <= 0) {
            return false;
        }
        if (!fightingArrayList.contains(fromItemId)) {
            return false;
        }
        fightingArrayList.set(fightingArrayList.indexOf(fromItemId), toItemId);
        fightingArray = GsonUtil.getJsonInstance().toJson(this.fightingArrayList);
        return true;
    }

    /**
     * 替换一个应援佣兵
     *
     * @param fromItemId
     * @param toItemId
     */
//    public boolean replaceMercenaryAssistance(int fromItemId, int toItemId) {
//        if (fromItemId <= 0 || toItemId <= 0) {
//            return false;
//        }
//        if (!assistanceArrayList.contains(fromItemId)) {
//            return false;
//        }
//        assistanceArrayList.set(assistanceArrayList.indexOf(fromItemId), toItemId);
//        assistanceArray = GsonUtil.getJsonInstance().toJson(this.assistanceArrayList);
//        return true;
//    }

    /**
     * 查看佣兵是否已经满了
     */

    public Boolean checkMercenaryInFightingArrayFull() {
        if (this.fightingArrayList.get(this.fightingArrayList.size() - 1) != 0) {
            return true;
        }

        return false;
    }
    /**
     * 判断佣兵是否在应援队形中
     */
//    public Boolean checkMercenaryInAssistanceArray(int number) {
//        if (this.assistanceArrayList.contains(number)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
    /**
     * 查看应援是否已经满了
     *
     * @return
     */
//    public Boolean checkAssistanceFull() {
//        if (this.assistanceArrayList.get(this.assistanceArrayList.size() - 1) != 0) {
//            return true;
//        }
//
//        return false;
//    }


    /**
     * 判断佣兵是否在战斗队形中
     */
    public Boolean checkMercenaryInFinghtingArray(int number) {
        if (this.fightingArrayList.contains(number)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 位置返回佣兵
     */
    public int getMercenaryIdInFightingArrayByPos(int pos) {
        return this.fightingArrayList.get(pos);
    }

    /**
     * 获取阵型列表
     */
    public List<Integer> getFightingArray() {
        return this.fightingArrayList;
    }

    /**
     * 获取应援列表
     *
     * @return
     */
//    public List<Integer> getAssistanceArrayList() {
//        return this.assistanceArrayList;
//    }
//
//    public String getAssistanceArray() {
//        return this.assistanceArray;
//    }


    /**
     * 返回佣兵出战位置
     */
    public int getPosMercenaryInFightingArray(int number) {
        if (this.fightingArrayList.contains(number)) {
            return this.fightingArrayList.indexOf(number);
        } else {
            return -1;
        }
    }

    /**
     * 一个佣兵取消出战
     *
     * @param number
     */
    public Boolean removeMercenaryFighting(int number) {
        if (number == 0) {
            return false;
        }
        if (this.fightingArrayList.contains(number)) {
            this.fightingArrayList.remove((Integer) number);
            this.fightingArrayList.add(0);
            this.fightingArray = GsonUtil.getJsonInstance().toJson(this.fightingArrayList);

            return true;
        } else {
            return false;
        }

    }

    /**
     * 应援休息
     *
     * @param number
     * @return
     */
//    public Boolean removeAssistance(int number) {
//        if (number == 0) {
//            return false;
//        }
//        if (this.assistanceArrayList.contains(number)) {
//            this.assistanceArrayList.remove((Integer) number);
//            this.assistanceArrayList.add(0);
//            this.assistanceArray = GsonUtil.getJsonInstance().toJson(this.assistanceArrayList);
//
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * 扩容佣兵出战位置
     *
     * @param
     */
    public void addFinghtingArrayBox(int count) {
        for (int i = 0; i < count; i++) {
            this.fightingArrayList.add(0);
        }
        this.fightingArray = GsonUtil.getJsonInstance().toJson(this.fightingArrayList);

        return;
    }

    /**
     * 扩容应援数量
     *
     * @param count
     */
//    public void addAssistanceArrayBox(int count) {
//        for (int i = 0; i < count; i++) {
//            this.assistanceArrayList.add(0);
//        }
//        this.assistanceArray = GsonUtil.getJsonInstance().toJson(this.assistanceArrayList);
//
//        return;
//    }

    /**
     * 获取现在出战容量
     *
     * @param
     */
    public int getFightingArrayBoxCount() {
        return this.fightingArrayList.size();
    }

    /**
     * 应援容量
     *
     * @return
     */
//    public int getAssistanceArrayBoxCount() {
//        return this.assistanceArrayList.size();
//    }

    /**
     * 获取应援角色数量
     *
     * @return
     */
//    public int getAssistanceRoleSize() {
//        int count = 0;
//        for (Integer i : this.assistanceArrayList) {
//            if (i > 0) {
//                count++;
//            }
//        }
//        return count;
//    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
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

    public void setFightingArray(String fightingArray) {
        this.fightingArray = fightingArray;
        this.fightingArrayList = GsonUtil.getJsonInstance().fromJson(this.fightingArray, new TypeToken<List<Integer>>() {
        }.getType());
    }

    public void setFightingArrayList(List<Integer> fightingArrayList) {
        this.fightingArrayList = fightingArrayList;
        this.fightingArray = GsonUtil.getJsonInstance().toJson(this.fightingArrayList);
    }

//    public void setAssistanceArray(String assistanceArray) {
//        this.assistanceArray = assistanceArray;
//        this.assistanceArrayList = GsonUtil.getJsonInstance().fromJson(this.assistanceArray, new TypeToken<List<Integer>>() {
//        }.getType());
//    }

//    public void setAssistanceArrayList(List<Integer> assistanceArrayList) {
//        this.assistanceArrayList = assistanceArrayList;
//        this.assistanceArray = GsonUtil.getJsonInstance().toJson(this.assistanceArrayList);
//    }

    public String getName() {
        if (name == null){
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
