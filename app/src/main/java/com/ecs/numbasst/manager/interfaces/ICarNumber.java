package com.ecs.numbasst.manager.interfaces;

import java.util.Date;

/**
 * @author zw
 * @time 2020/12/31
 * @description
 */
public interface ICarNumber {
    void setCarNumber(String number, Date date);
    void getCarNumber();
    void logoutCarNumber();
}
