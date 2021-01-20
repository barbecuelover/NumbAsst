package com.ecs.numbasst.manager.msg;

/**
 * @author zw
 * @time 2021/1/8
 * @description
 */
public class DeviceIDMsg {

    public final static int SET_DEVICE_ID_SUCCEED = 0x1300;
    public final static int SET_DEVICE_ID_FAILED = 0x1301;

    public final static int GET_DEVICE_ID = 0x1400;

    private int state;

    private String deviceID="";

    public DeviceIDMsg() {
    }

    public DeviceIDMsg(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

}
