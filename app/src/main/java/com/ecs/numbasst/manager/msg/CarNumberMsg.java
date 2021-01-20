package com.ecs.numbasst.manager.msg;

/**
 * @author zw
 * @time 2021/1/8
 * @description
 */
public class CarNumberMsg {

    public final static int UNSUBSCRIBE_NUMBER_SUCCEED = 0x1000;
    public final static int UNSUBSCRIBE_NUMBER_FAILED = 0x1001;

    public final static int SET_NUMBER_SUCCEED = 0x1100;
    public final static int SET_NUMBER_FAILED = 0x1101;

    public final static int GET_NUMBER = 0x1200;

    private int state;
    private String carNumber="";

    public CarNumberMsg() {
    }

    public CarNumberMsg(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }
}
