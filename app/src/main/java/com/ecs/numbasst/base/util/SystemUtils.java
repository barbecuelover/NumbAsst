package com.ecs.numbasst.base.util;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.List;

public class SystemUtils {

    public static  boolean isCurrentAppProcess(Context mContext){
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appInfo : processes) {
            if (appInfo.pid == pid){
                String processName = appInfo.processName;
                if (!TextUtils.isEmpty(processName) && processName.equals(mContext.getPackageName())){
                    return true;
                }
            }
        }
        return  false;
    }



}
