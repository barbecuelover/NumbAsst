package com.ecs.numbasst.manager.callback;

public interface UpdateCallback extends Callback{
    void onRequestSucceed();
    void onUpdateCompleted(int unitType,int status);
    void onUpdateProgressChanged(int progress);
    void onUpdateError();
    void onFailed(String reason);
}
