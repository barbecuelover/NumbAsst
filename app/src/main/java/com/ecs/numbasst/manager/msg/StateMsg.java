package com.ecs.numbasst.manager.msg;

import com.ecs.numbasst.ui.state.entity.StateInfo;

/**
 * @author zw
 * @time 2021/1/8
 * @description
 */
public class StateMsg {

    public final static int GET_NUMBER = 0x0100;
    private StateInfo stateInfo;

    public StateMsg(StateInfo stateInfo) {
        this.stateInfo = stateInfo;
    }

    public StateInfo getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(StateInfo stateInfo) {
        this.stateInfo = stateInfo;
    }
}
