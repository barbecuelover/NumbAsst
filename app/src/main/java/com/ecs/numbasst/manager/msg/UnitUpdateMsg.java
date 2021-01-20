package com.ecs.numbasst.manager.msg;

/**
 * @author zw
 * @time 2021/1/8
 * @description
 */
public class UnitUpdateMsg {

    public  final static  long REQUEST_SUCCEED = 0x2100;
    public  final static  long REQUEST_FAILED = 0x2101;
    public  final static  long TRANSFER_PROGRESS_CHANGED = 0x2200;
    public  final static  long UPDATE_COMPLETED = 0x2300;
    public  final static  long UPDATE_FAILED = 0x2301;

    private long state;
    private int progress;
    private int unitType;


    public UnitUpdateMsg() {
    }

    public UnitUpdateMsg(long state) {
        this.state = state;
    }

    /**
     * 状态 和 固件类型
     * @param state
     * @param unitType
     */
    public UnitUpdateMsg(int state,byte unitType) {
        this.state = state;
        this.unitType = unitType;
    }

    /**
     * 传输文件过程
     * @param progress
     */
    public UnitUpdateMsg(int progress) {
        this.state = TRANSFER_PROGRESS_CHANGED;
        this.progress = progress;
    }


    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getUnitType() {
        return unitType;
    }

    public void setUnitType(int unitType) {
        this.unitType = unitType;
    }
}
