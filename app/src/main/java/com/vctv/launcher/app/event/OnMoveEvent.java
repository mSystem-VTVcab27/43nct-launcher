package com.vctv.launcher.app.event;

/* JADX INFO: loaded from: classes.dex */
public class OnMoveEvent {
    private final int curPos;
    private final int targetPos;

    public OnMoveEvent(int i, int i2) {
        this.curPos = i;
        this.targetPos = i2;
    }

    public int getCurPos() {
        return this.curPos;
    }

    public int getTargetPos() {
        return this.targetPos;
    }
}
