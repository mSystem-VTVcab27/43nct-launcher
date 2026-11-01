package com.vctv.launcher.home;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class ProgramItemFocusEvent {
    private boolean hasFocus;
    private View view;

    public ProgramItemFocusEvent(View view, boolean z) {
        this.view = view;
        this.hasFocus = z;
    }

    public View getView() {
        return this.view;
    }

    public boolean hasFocus() {
        return this.hasFocus;
    }
}
