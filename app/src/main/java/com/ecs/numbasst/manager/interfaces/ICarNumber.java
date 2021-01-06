package com.ecs.numbasst.manager.interfaces;

import com.ecs.numbasst.manager.callback.NumberCallback;

/**
 * @author zw
 * @time 2020/12/31
 * @description
 */
public interface ICarNumber {
    void setCarNumber(String number);
    void getCarNumber();
    void logoutCarNumber();
    void setNumberCallback(NumberCallback callBack);
}
