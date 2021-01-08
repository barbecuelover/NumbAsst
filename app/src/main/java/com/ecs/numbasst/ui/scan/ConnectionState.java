package com.ecs.numbasst.ui.scan;

/**
 * @author zw
 * @time 2021/1/8
 * @description
 */
public class ConnectionState {
    public  final static  int CONNECTED = 0x100;
    public  final static  int DISCONNECTED = 0x101;

    private int type;
    private String mac;
    private String name;

    public ConnectionState(int type, String mac, String name) {
        this.type = type;
        this.mac = mac;
        this.name = name;
    }

    public ConnectionState() {
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }
}
