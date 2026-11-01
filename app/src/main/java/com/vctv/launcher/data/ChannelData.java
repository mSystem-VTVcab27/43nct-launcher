package com.vctv.launcher.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ChannelData implements Parcelable {
    public static final Parcelable.Creator<ChannelData> CREATOR = new Parcelable.Creator<ChannelData>() { // from class: com.vctv.launcher.data.ChannelData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ChannelData createFromParcel(Parcel parcel) {
            return new ChannelData(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ChannelData[] newArray(int i) {
            return new ChannelData[i];
        }
    };
    private String appLinkIntentUri;
    private long channelId;
    private Uri channelUri;
    private String channelVersion;
    private String description;
    private String displayName;
    private String displayNumber;
    private String inputId;
    private boolean isPreset;
    private boolean isShow;
    private boolean movable;
    private String packageName;
    private int position;
    Set<PreviewProgramData> previewPrograms;
    private String type;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

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

    public String getInputId() {
        return this.inputId;
    }

    public void setInputId(String str) {
        this.inputId = str;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String getDisplayNumber() {
        return this.displayNumber;
    }

    public void setDisplayNumber(String str) {
        this.displayNumber = str;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String str) {
        this.displayName = str;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public String getAppLinkIntentUri() {
        return this.appLinkIntentUri;
    }

    public void setAppLinkIntentUri(String str) {
        this.appLinkIntentUri = str;
    }

    public Uri getChannelUri() {
        return this.channelUri;
    }

    public void setChannelUri(Uri uri) {
        this.channelUri = uri;
    }

    public Set<PreviewProgramData> getPreviewProgram() {
        return this.previewPrograms;
    }

    public void setPreviewProgram(Set<PreviewProgramData> set) {
        this.previewPrograms = set;
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

    public boolean isMovable() {
        return this.movable;
    }

    public void setMovable(boolean z) {
        this.movable = z;
    }

    public String toString() {
        return "ChannelData{channelId=" + this.channelId + ", channelUri=" + this.channelUri + ", packageName='" + this.packageName + "', displayName='" + this.displayName + "', position=" + this.position + ", isShow=" + this.isShow + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.channelId);
        parcel.writeParcelable(this.channelUri, i);
        parcel.writeString(this.packageName);
        parcel.writeString(this.inputId);
        parcel.writeString(this.type);
        parcel.writeString(this.displayNumber);
        parcel.writeString(this.displayName);
        parcel.writeString(this.description);
        parcel.writeString(this.appLinkIntentUri);
        if (this.previewPrograms == null) {
            parcel.writeTypedList(new ArrayList());
        } else {
            parcel.writeTypedList(new ArrayList(this.previewPrograms));
        }
        parcel.writeInt(this.position);
        parcel.writeByte(this.isShow ? (byte) 1 : (byte) 0);
        parcel.writeByte(this.isPreset ? (byte) 1 : (byte) 0);
        parcel.writeString(this.channelVersion);
        parcel.writeByte(this.movable ? (byte) 1 : (byte) 0);
    }

    public ChannelData() {
        this.position = -1;
        this.isShow = false;
        this.isPreset = false;
        this.movable = false;
    }

    protected ChannelData(Parcel parcel) {
        this.position = -1;
        this.isShow = false;
        this.isPreset = false;
        this.movable = false;
        this.channelId = parcel.readLong();
        this.channelUri = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
        this.packageName = parcel.readString();
        this.inputId = parcel.readString();
        this.type = parcel.readString();
        this.displayNumber = parcel.readString();
        this.displayName = parcel.readString();
        this.description = parcel.readString();
        this.appLinkIntentUri = parcel.readString();
        ArrayList arrayListCreateTypedArrayList = parcel.createTypedArrayList(PreviewProgramData.CREATOR);
        this.previewPrograms = arrayListCreateTypedArrayList == null ? new HashSet() : new HashSet(arrayListCreateTypedArrayList);
        this.position = parcel.readInt();
        this.isShow = parcel.readByte() != 0;
        this.isPreset = parcel.readByte() != 0;
        this.channelVersion = parcel.readString();
        this.movable = parcel.readByte() != 0;
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

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ChannelData channelData = (ChannelData) obj;
        return this.channelId == channelData.channelId && Objects.equals(this.channelUri, channelData.channelUri) && Objects.equals(this.packageName, channelData.packageName) && Objects.equals(this.inputId, channelData.inputId) && Objects.equals(this.type, channelData.type) && Objects.equals(this.displayNumber, channelData.displayNumber) && Objects.equals(this.displayName, channelData.displayName) && Objects.equals(this.description, channelData.description) && Objects.equals(this.appLinkIntentUri, channelData.appLinkIntentUri) && Objects.equals(this.previewPrograms, channelData.previewPrograms);
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.channelId), this.channelUri, this.packageName, this.inputId, this.type, this.displayNumber, this.displayName, this.description, this.appLinkIntentUri, this.previewPrograms);
    }
}
