package com.ecs.numbasst.manager.callback;

public interface ConnectionCallback {
    void onSucceed(String msg);
    void onFailed(String reason);
}
