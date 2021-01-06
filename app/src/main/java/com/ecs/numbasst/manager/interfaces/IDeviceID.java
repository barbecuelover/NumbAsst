package com.ecs.numbasst.manager.interfaces;

import com.ecs.numbasst.manager.callback.DeviceIDCallback;

/**
 * @author zw
 * @time 2020/12/31
 * @description
 */
public interface IDeviceID {
    void setDeviceID(String id);
    void getDeviceID();
    void setDeviceIDCallback(DeviceIDCallback callBack);
}
