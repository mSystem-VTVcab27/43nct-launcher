package com.vctv.launcher.util;

import android.graphics.Color;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class ColorUtil {
    private static final String TAG = "Colors";

    public static int parseColorWithDefault(String str, int i) {
        if (str == null || str.trim().isEmpty()) {
            Log.w(TAG, "Invalid color");
            return i;
        }
        try {
            return Color.parseColor(str);
        } catch (Exception e) {
            Log.w(TAG, "Unknown color" + str, e);
            return i;
        }
    }
}
