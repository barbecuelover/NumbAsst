package com.ecs.numbasst.manager.callback;

public interface NumberCallback extends Callback{
    void onNumberGot(int type,String number);
    void onNumberSet(int type,int state);
    void onUnsubscribed(int state);
    void onSensorDemarcated(int type,int pressure);
}
