package com.ecs.numbasst.manager.callback;

public interface UpdateCallback {
    void onRequestSucceed();
    void onUpdateCompleted(int unitType,int status);
    void onFailed(String reason);
}
