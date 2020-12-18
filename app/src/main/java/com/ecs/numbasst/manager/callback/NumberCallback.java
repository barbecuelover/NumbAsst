package com.ecs.numbasst.manager.callback;

public interface NumberCallback extends Callback{
    void onNumberGot(String number);
    void onSetSucceed();
    void onFailed(String reason);
}
