package com.ecs.numbasst.manager.interfaces;

import com.ecs.numbasst.manager.callback.AdjustCallback;

/**
 * @author zw
 * @time 2020/12/31
 * @description
 */
public interface IAdjustSensor {
    void adjustSensor(int type, int pressure);
    void setAdjustCallback(AdjustCallback callBack);
}
