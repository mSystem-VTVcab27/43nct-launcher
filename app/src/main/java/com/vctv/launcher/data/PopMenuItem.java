package com.vctv.launcher.data;

import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class PopMenuItem {
    private final String desc;
    private boolean enable;
    private final Drawable icon;

    public PopMenuItem(Drawable drawable, String str) {
        this(drawable, str, true);
    }

    public PopMenuItem(Drawable drawable, String str, boolean z) {
        this.icon = drawable;
        this.desc = str;
        this.enable = z;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public String getDesc() {
        return this.desc;
    }

    public boolean getEnable() {
        return this.enable;
    }

    public void setEnable(boolean z) {
        this.enable = z;
    }
}
