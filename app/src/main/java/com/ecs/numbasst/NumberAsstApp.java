package com.ecs.numbasst;

import android.app.Application;

import com.ecs.numbasst.base.util.SystemUtils;

public class NumberAsstApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //init data
        if (SystemUtils.isCurrentAppProcess(this)){

        }
    }



}
