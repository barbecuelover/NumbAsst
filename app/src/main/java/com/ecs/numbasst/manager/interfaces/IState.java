package com.ecs.numbasst.manager.interfaces;

import com.ecs.numbasst.manager.callback.DownloadCallback;
import com.ecs.numbasst.manager.callback.QueryStateCallback;

import java.util.Date;

/**
 * @author zw
 * @time 2020/12/31
 * @description
 */
public interface IState {
    void getDeviceState(int type);
    void setQueryStateCallback(QueryStateCallback callBack);
}
