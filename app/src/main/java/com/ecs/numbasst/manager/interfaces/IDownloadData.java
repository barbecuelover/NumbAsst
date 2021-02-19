package com.ecs.numbasst.manager.interfaces;

import java.util.Date;

/**
 * @author zw
 * @time 2020/12/31
 * @description
 */
public interface IDownloadData {
    void downloadDataRequest(Date startTime, Date endTime);
    void downloadOneDayData(int index,Date date);
    void stopDownload();
}
