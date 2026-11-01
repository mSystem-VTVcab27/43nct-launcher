package com.vctv.launcher.util;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import androidx.core.content.ContextCompat;
import androidx.core.internal.view.SupportMenu;
import com.vctv.launcher.R;
import com.vctv.launcher.config.Config;

/* JADX INFO: loaded from: classes.dex */
public class ViewUtils {
    private static final int DEFAULT_COLOR = -16777216;

    public static int dpToPx(Context context, float f) {
        return Math.round(TypedValue.applyDimension(1, f, context.getResources().getDisplayMetrics()));
    }

    public static GradientDrawable createProgramItemDrawable(Context context) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(ContextCompat.getColor(context, R.color.app_bg));
        gradientDrawable.setStroke(dpToPx(context, 2.0f), -16777216);
        gradientDrawable.setCornerRadius(dpToPx(context, Config.get().globalStyle.itemCornerRadius));
        return gradientDrawable;
    }

    public static int getTextDefaultColor(Context context) {
        try {
            return Config.get().globalStyle.getTextDefaultColor(context);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return -16777216;
        }
    }

    public static int getTextSelectedColor(Context context) {
        try {
            return Config.get().globalStyle.getTextSelectedColor(context);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return -16777216;
        }
    }

    public static int getItemSelectedBorderColor(Context context) {
        return Config.get().globalStyle.geItemSelectedBorderColor(context);
    }

    public static GradientDrawable createDrawable(Context context, int i, boolean z, boolean z2) {
        int iDpToPx;
        int itemSelectedBorderColor;
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(i);
        if (z) {
            int iDpToPx2 = dpToPx(context, Config.get().globalStyle.itemBorderWidth + 3);
            try {
                itemSelectedBorderColor = getItemSelectedBorderColor(context);
            } catch (IllegalArgumentException unused) {
                Log.e("TAG", "Invalid border color: " + getItemSelectedBorderColor(context));
                itemSelectedBorderColor = SupportMenu.CATEGORY_MASK;
            }
            gradientDrawable.setStroke(iDpToPx2, itemSelectedBorderColor);
        }
        if (z2) {
            if (z) {
                iDpToPx = Config.get().globalStyle.itemCornerRadius > 0 ? dpToPx(context, Config.get().globalStyle.itemCornerRadius - 1) : 0;
            } else {
                iDpToPx = dpToPx(context, Config.get().globalStyle.itemCornerRadius);
            }
            gradientDrawable.setCornerRadius(iDpToPx);
        }
        return gradientDrawable;
    }
}
