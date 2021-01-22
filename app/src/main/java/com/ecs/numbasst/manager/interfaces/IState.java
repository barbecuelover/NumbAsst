package com.ecs.numbasst.manager.interfaces;

/**
 * @author zw
 * @time 2020/12/31
 * @description
 */
public interface IState {
    void getDeviceState(int unit,int type);

    /**
     * 获取对应 unit 的版本信息
     * @param unitType
     */
    void getDeviceVersion(int unitType);
}
