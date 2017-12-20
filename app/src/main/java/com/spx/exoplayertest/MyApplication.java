package com.spx.exoplayertest;

import android.app.Application;

/**
 * Created by shaopengxiang on 2017/12/20.
 */

public class MyApplication extends Application {
    private static MyApplication sApplication = null;
    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }

    public static MyApplication getApp(){
        return sApplication;
    }
}
