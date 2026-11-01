package com.vctv.launcher.app.event;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class CancelMoveEvent {
    private final View focusView;

    public CancelMoveEvent(View view) {
        this.focusView = view;
    }

    public View getFocusView() {
        return this.focusView;
    }
}
