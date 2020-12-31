package com.ecs.numbasst.manager.interfaces;

import com.ecs.numbasst.manager.callback.DebugCallback;

/**
 * @author zw
 * @time 2020/12/31
 * @description
 */
public interface IDebugging {
    void sendDebuggingData(String data);
    void enableDebugging(boolean enable);
    void setDebugCallBack(DebugCallback callBack);
}
