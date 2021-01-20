package com.ecs.numbasst.manager.msg;

/**
 * @author zw
 * @time 2021/1/8
 * @description
 */
public class ConnectionMsg {
    public  final static  int CONNECTED = 0x100;
    public  final static  int DISCONNECTED = 0x101;
    public  final static  int CONNECT_FAILED = 0x2001;

    private int state;
    private String mac ="";
    private String name ="";



    private String reason ="";

    public ConnectionMsg(int type, String mac, String name) {
        this.state = type;
        this.mac = mac;
        this.name = name;
    }

    public ConnectionMsg() {
    }

    /**
     * 连接失败
     * @param reason
     */
    public ConnectionMsg(String reason) {
        this.state = CONNECT_FAILED;
        this.reason = reason;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public String getMac() {
        return mac;
    }

    public String getName() {
        return name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
