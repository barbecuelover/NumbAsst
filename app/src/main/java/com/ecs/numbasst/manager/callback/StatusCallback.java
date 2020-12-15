package com.ecs.numbasst.manager.callback;

public interface StatusCallback {
    void onSucceed(String msg);
    void onFailed(String reason);
}
