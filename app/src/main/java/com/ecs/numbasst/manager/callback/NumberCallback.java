package com.ecs.numbasst.manager.callback;

public interface NumberCallback {
    void onNumberGot(String number);
    void onSetSucceed();
    void onFailed(String reason);
}
