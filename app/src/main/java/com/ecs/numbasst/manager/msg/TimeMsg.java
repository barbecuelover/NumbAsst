package com.ecs.numbasst.manager.msg;

/**
 * @author zw
 * @time 2021/1/20
 * @description
 */
public class TimeMsg {

    private long timeT;
    private int state;
    public final static int TIME_SET_SUCCEED = 0x1800;
    public final static int TIME_SET_FAILED = 0x1801;
    public final static int TIME_GET = 0x1900;


    public TimeMsg(int state) {
        this.state = state;
    }

    public TimeMsg() {

    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTimeT() {
        return timeT;
    }

    public void setTimeT(long timeT) {
        this.timeT = timeT;
    }
}
