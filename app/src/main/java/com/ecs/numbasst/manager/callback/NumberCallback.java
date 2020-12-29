package com.ecs.numbasst.manager.callback;

public interface NumberCallback extends Callback{
    void onNumberGot(String number);
    void onNumberSet(int state);
    void onUnsubscribed(int state);
}
