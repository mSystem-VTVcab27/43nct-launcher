package com.vctv.launcher.util;

import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class LogUtil {
    private static final String TAG = "MW_Launcher";

    public static void i(String str) {
        Log.i(TAG, str);
    }

    public static void i(String str, String str2) {
        Log.i("MW_Launcher " + str, str2);
    }

    public static void d(String str) {
        Log.d(TAG, str);
    }

    public static void d(String str, String str2) {
        Log.d("MW_Launcher " + str, str2);
    }

    public static void e(String str) {
        Log.e(TAG, str);
    }

    public static void e(String str, String str2) {
        Log.e("MW_Launcher " + str, str2);
    }
}
