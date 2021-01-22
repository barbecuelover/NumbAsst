package com.ecs.numbasst.manager.msg;

/**
 * @author zw
 * @time 2021/1/20
 * @description
 */
public class DebuggingMsg {

    byte[] data;
    public DebuggingMsg(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
