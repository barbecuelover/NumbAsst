package com.ecs.numbasst.manager.interfaces;

import java.util.Date;

/**
 * @author zw
 * @time 2020/12/31
 * @description
 */
public interface ICarNumber {
    void setCarNumber(String number);
    void getCarNumber();
    void logoutCarNumber();

    void setTime(Date date);
    void getTime();
}
