package com.ecs.numbasst.manager.msg;

/**
 * @author zw
 * @time 2021/1/20
 * @description
 */
public class WifiMsg {

    private int state;

    private String name = "zemt_liewei";
    public final static int WIFI_NAME = 0x3400;
    public final static int WIFI_OPEN_SUCCEED = 0x3410;
    public final static int WIFI_OPEN_FAILED = 0x3411;
    public final static int WIFI_CLOSE_SUCCEED = 0x3420;
    public final static int WIFI_CLOSE_FAILED = 0x3421;


    public WifiMsg(int state) {
        this.state = state;
    }

    public WifiMsg() {
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
