package com.ecs.numbasst.manager.callback;

public interface ConnectionCallback extends Callback{
    void onSucceed(String msg);
    void onFailed(String reason);
}
