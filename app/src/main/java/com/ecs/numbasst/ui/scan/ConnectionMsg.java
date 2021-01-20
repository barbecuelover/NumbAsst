package com.ecs.numbasst.ui.scan;

/**
 * @author zw
 * @time 2021/1/8
 * @description
 */
public class ConnectionMsg {
    public  final static  int CONNECTED = 0x100;
    public  final static  int DISCONNECTED = 0x101;
    public  final static  int CONNECT_FAILED = 0x2001;

    private int type;
    private String mac ="";
    private String name ="";



    private String reason ="";

    public ConnectionMsg(int type, String mac, String name) {
        this.type = type;
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
        this.type = CONNECT_FAILED;
        this.reason = reason;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
