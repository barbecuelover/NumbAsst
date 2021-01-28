package com.ecs.numbasst.manager.interfaces;

/**
 * @author zw
 * @time 2021/1/28
 * @description
 */
public interface IWifi {
    void openWifi();
    void connectWifi(String name);
    void closeWifi();
    void getWifiName();

}
