package com.ecs.numbasst.manager.callback;

public interface DownloadCallback {
    void onConfirmed(long size);
    void onTransferred(byte[] data);
}
