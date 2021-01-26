package com.ecs.numbasst.manager.msg;

import java.util.Date;

/**
 * @author zw
 * @time 2021/1/8
 * @description
 */
public class DownloadMsg {

    public final static int DOWNLOAD_ALL_FILES =0xA1;
    public final static int DOWNLOAD_FILE_INFO = 0xA2;
    public final static int DOWNLOAD_PROGRESS = 0xA3;
    public final static int DOWNLOAD_FILE_NULL = 0xA4;
    public final static int DOWNLOAD_STOP = 0xA5;


    public final static int DOWNLOAD_FILE_COMPLETED = 0xD3;




    private int msgType;
    private Date date;
    private long totalSize;
    private long current;

    public DownloadMsg(int msgType, Date date) {
        this.msgType = msgType;
        this.date = date;
    }

    public DownloadMsg(int msgType) {
        this.msgType = msgType;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
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
