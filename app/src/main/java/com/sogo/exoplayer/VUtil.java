package com.sogo.exoplayer;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.spx.exoplayertest.MyApplication;


/**
 * Created by shaopengxiang on 2017/12/20.
 */

public class VUtil {

    /**
     * 判断wifi是否连接
     *
     * @param context
     * @return true:wifi连接状态
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static Application getApplication(){
        return MyApplication.getApp();
    }
}
