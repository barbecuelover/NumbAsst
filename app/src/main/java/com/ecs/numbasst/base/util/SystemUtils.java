package com.ecs.numbasst.base.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

    /**
     * [获取应用程序版本名称信息]
     * @param context
     * @return 当前应用的版本名称
     */
    public static  String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
