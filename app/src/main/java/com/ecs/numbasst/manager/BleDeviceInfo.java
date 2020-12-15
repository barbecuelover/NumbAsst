package com.ecs.numbasst.manager;

public class BleDeviceInfo {

    private final String mac;
    private final String name;
    private int status = -1;


    public BleDeviceInfo(String mac, String name) {
        this.mac = mac;
        this.name = name;
    }

    public String getAddress() {
        return mac;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void resetStatus(){
        status = -1;
    }

}
