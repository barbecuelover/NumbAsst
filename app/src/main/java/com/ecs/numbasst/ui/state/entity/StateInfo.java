package com.ecs.numbasst.ui.state.entity;

/**
 * @author zw
 * @time 2020/12/28
 * @description
 */
public abstract class StateInfo {
    public byte stateType;

    public byte unitType;
    public StateInfo(byte[] data) {
        stateType = data[0];
    }

    public void setUnitType(byte unitType) {
        this.unitType = unitType;
    }

    public byte getUnitType() {
        return unitType;
    }
}
