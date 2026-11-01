package com.vctv.launcher.data;

/* JADX INFO: loaded from: classes.dex */
public class DbChannelData {
    private long channelId;
    private String channelVersion;
    private String displayName;
    private long id;
    private String packageName;
    private int position = -1;
    private boolean isShow = false;
    private boolean isPreset = false;
    private boolean movable = false;

    public long getChannelId() {
        return this.channelId;
    }

    public void setChannelId(long j) {
        this.channelId = j;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String str) {
        this.displayName = str;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int i) {
        this.position = i;
    }

    public boolean isShow() {
        return this.isShow;
    }

    public void setShow(boolean z) {
        this.isShow = z;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public boolean isPreset() {
        return this.isPreset;
    }

    public void setPreset(boolean z) {
        this.isPreset = z;
    }

    public String getChannelVersion() {
        return this.channelVersion;
    }

    public void setChannelVersion(String str) {
        this.channelVersion = str;
    }

    public boolean isMovable() {
        return this.movable;
    }

    public void setMovable(boolean z) {
        this.movable = z;
    }

    public String toString() {
        return "DbChannelData{packageName='" + this.packageName + "', displayName='" + this.displayName + "', position=" + this.position + ", isShow=" + this.isShow + ", isPreset=" + this.isPreset + '}';
    }
}
