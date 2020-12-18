package com.ecs.numbasst.manager.callback;

public interface DownloadCallback extends Callback{
    void onConfirmed(long size);
    void onTransferred(byte[] data);
}
