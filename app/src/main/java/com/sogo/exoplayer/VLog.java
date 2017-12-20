package com.sogo.exoplayer;

import android.util.Log;

/**
 * Created by shaopengxiang on 2017/12/20.
 */

public class VLog {
    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void d(String tag, String msg, Throwable th) {
        Log.d(tag, msg, th);
    }

    public static void e(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void e(String tag, String msg, Throwable th) {
        Log.d(tag, msg, th);
    }
}
