package com.vctv.launcher.home;

import android.os.Parcel;
import android.os.Parcelable;
import com.vctv.launcher.data.ChannelData;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AppChannelGroup implements Parcelable {
    public static final Parcelable.Creator<AppChannelGroup> CREATOR = new Parcelable.Creator<AppChannelGroup>() { // from class: com.vctv.launcher.home.AppChannelGroup.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AppChannelGroup createFromParcel(Parcel parcel) {
            return new AppChannelGroup(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AppChannelGroup[] newArray(int i) {
            return new AppChannelGroup[i];
        }
    };
    private List<ChannelData> channelDataList;
    private String packageName;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public AppChannelGroup(String str, List<ChannelData> list) {
        this.packageName = str;
        this.channelDataList = list;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public List<ChannelData> getChannelDataList() {
        return this.channelDataList;
    }

    public String toString() {
        return "AppChannelGroup{packageName='" + this.packageName + "', channelDataList=" + this.channelDataList + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.packageName);
        parcel.writeTypedList(this.channelDataList);
    }

    protected AppChannelGroup(Parcel parcel) {
        this.packageName = parcel.readString();
        this.channelDataList = parcel.createTypedArrayList(ChannelData.CREATOR);
    }
}
