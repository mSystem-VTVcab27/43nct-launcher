package com.vctv.launcher.util;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import com.vctv.launcher.R;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.data.AppStrategy;

/* JADX INFO: loaded from: classes.dex */
public class AppConfigUtil {
    public static boolean inRange(int i, int i2, int i3) {
        return i >= i2 && i <= i3;
    }

    public static int getBorderWidth(Context context) {
        return FontDisplayUtil.dip2px(context, Config.get().globalStyle.itemBorderWidth);
    }

    public static int getCornerRadius(Context context) {
        return FontDisplayUtil.dip2px(context, Config.get().globalStyle.itemCornerRadius);
    }

    public static int getDefaultTextColor(Context context) {
        return Config.get().globalStyle.getTextDefaultColor(context);
    }

    public static int getFocusedTextColor(Context context) {
        return Config.get().globalStyle.getTextFocusedColor(context);
    }

    public static int getSelectedTextColor(Context context) {
        return Config.get().globalStyle.getTextSelectedColor(context);
    }

    public static int getItemSelectedBorderColor(Context context) {
        return Config.get().globalStyle.geItemSelectedBorderColor(context);
    }

    public static String getFontPath() {
        return Config.get().globalStyle.textFont;
    }

    public static Configuration.Direction getDirection(AppStrategy appStrategy) {
        if (AnonymousClass1.$SwitchMap$com$mw$launcher$data$AppStrategy[appStrategy.ordinal()] != 2) {
            return Config.get().pagination.appConfig.scrollDirection;
        }
        return Config.get().pagination.favApp.scrollDirection;
    }

    /* JADX INFO: renamed from: com.vctv.launcher.util.AppConfigUtil$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize;
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$data$AppStrategy;

        static {
            int[] iArr = new int[Configuration.GlobalStyle.ItemSize.values().length];
            $SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize = iArr;
            try {
                iArr[Configuration.GlobalStyle.ItemSize.SMALL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Configuration.GlobalStyle.ItemSize.MEDIUM.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Configuration.GlobalStyle.ItemSize.LARGE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            int[] iArr2 = new int[AppStrategy.values().length];
            $SwitchMap$com$mw$launcher$data$AppStrategy = iArr2;
            try {
                iArr2[AppStrategy.DEFAULT.ordinal()] = 1;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$mw$launcher$data$AppStrategy[AppStrategy.FAV.ordinal()] = 2;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public static int getAppSize() {
        int i = AnonymousClass1.$SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Config.get().globalStyle.itemSize.ordinal()];
        if (i != 1) {
            return i != 3 ? 6 : 5;
        }
        return 7;
    }

    public static int getHorizonHeight(Context context) {
        float dimension;
        Configuration.GlobalStyle globalStyle = Config.get().globalStyle;
        float f = Configuration.GlobalStyle.SelectionMode.ZOOM.equals(globalStyle.itemSelectionMode) ? globalStyle.itemZoomScale : 1.0f;
        int i = AnonymousClass1.$SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[globalStyle.itemSize.ordinal()];
        int i2 = 0;
        if (i == 1) {
            dimension = context.getResources().getDimension(R.dimen.app_banner_height_s);
        } else if (i == 2) {
            dimension = context.getResources().getDimension(R.dimen.app_banner_height_m);
        } else {
            if (i == 3) {
                dimension = context.getResources().getDimension(R.dimen.app_banner_height_l);
            }
            return Math.round((i2 + ((int) context.getResources().getDimension(R.dimen.app_label_height))) * f);
        }
        i2 = 0 + ((int) dimension);
        return Math.round((i2 + ((int) context.getResources().getDimension(R.dimen.app_label_height))) * f);
    }

    public static boolean isTalkBackEnabled(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        return accessibilityManager != null && accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled();
    }
}
