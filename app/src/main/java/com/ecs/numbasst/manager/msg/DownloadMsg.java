package com.ecs.numbasst.manager.msg;

import java.util.Date;

/**
 * @author zw
 * @time 2021/1/8
 * @description
 */
public class DownloadMsg {

    private byte msgType;
    private Date date;
    private long totalSize;

    public DownloadMsg(byte msgType, Date date) {
        this.msgType = msgType;
        this.date = date;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}
