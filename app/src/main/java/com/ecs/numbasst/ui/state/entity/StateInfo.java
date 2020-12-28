package com.ecs.numbasst.ui.state.entity;

/**
 * @author zw
 * @time 2020/12/28
 * @description
 */
public abstract class StateInfo {
    public byte stateType;
    public StateInfo(byte[] data) {
        stateType = data[0];
    }
}
