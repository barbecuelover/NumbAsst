package com.ecs.numbasst;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ecs.numbasst.base.util.DataKeeper;
import com.ecs.numbasst.base.util.SystemUtils;

public class NumberAsstApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //init data
        if (SystemUtils.isCurrentAppProcess(this)){
            DataKeeper.init(getApplicationContext());
        }
    }




}
