package com.guaji.game.module.activity.activity125;

public enum Activity125TypeEnum {

    //初级场
    LOW_TYPE(1),
    //中级场
    MEDIUM_TYPE(2),
    //高级场
    HIGH_TYPE(3);

    private int typeId;

    Activity125TypeEnum(int typeId){
       this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }
}
