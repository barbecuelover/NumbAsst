package com.ecs.numbasst.manager.interfaces;

import com.ecs.numbasst.manager.callback.DownloadCallback;
import com.ecs.numbasst.manager.callback.UpdateCallback;

import java.io.File;
import java.util.Date;

/**
 * @author zw
 * @time 2020/12/31
 * @description
 */
public interface IDownloadData {
    void downloadDataRequest(Date startTime, Date endTime);
    void replyDownloadConfirm(boolean download);
    void setDownloadCallback(DownloadCallback callBack);
}
