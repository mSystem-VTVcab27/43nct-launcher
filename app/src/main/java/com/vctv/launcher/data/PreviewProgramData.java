package com.vctv.launcher.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class PreviewProgramData implements Parcelable {
    public static final Parcelable.Creator<PreviewProgramData> CREATOR = new Parcelable.Creator<PreviewProgramData>() { // from class: com.vctv.launcher.data.PreviewProgramData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PreviewProgramData createFromParcel(Parcel parcel) {
            return new PreviewProgramData(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PreviewProgramData[] newArray(int i) {
            return new PreviewProgramData[i];
        }
    };
    long durationMillis;
    String intentUri;
    long interactionCount;
    String posterArtUri;
    int posterAspectRatio;
    String shortDescription;
    String title;
    long type;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String getShortDescription() {
        return this.shortDescription;
    }

    public void setShortDescription(String str) {
        this.shortDescription = str;
    }

    public String getPosterArtUri() {
        return this.posterArtUri;
    }

    public void setPosterArtUri(String str) {
        this.posterArtUri = str;
    }

    public long getDurationMillis() {
        return this.durationMillis;
    }

    public void setDurationMillis(long j) {
        this.durationMillis = j;
    }

    public String getIntentUri() {
        return this.intentUri;
    }

    public void setIntentUri(String str) {
        this.intentUri = str;
    }

    public long getType() {
        return this.type;
    }

    public void setType(long j) {
        this.type = j;
    }

    public long getInteractionCount() {
        return this.interactionCount;
    }

    public void setInteractionCount(long j) {
        this.interactionCount = j;
    }

    public int getPosterAspectRatio() {
        return this.posterAspectRatio;
    }

    public void setPosterAspectRatio(int i) {
        this.posterAspectRatio = i;
    }

    public String toString() {
        return "PreviewProgramData{title='" + this.title + "', shortDescription='" + this.shortDescription + "', posterArtUri='" + this.posterArtUri + "', durationMillis=" + this.durationMillis + ", intentUri='" + this.intentUri + "', type=" + this.type + ", interactionCount=" + this.interactionCount + ", posterAspectRatio=" + this.posterAspectRatio + '}';
    }

    public PreviewProgramData() {
    }

    protected PreviewProgramData(Parcel parcel) {
        this.title = parcel.readString();
        this.shortDescription = parcel.readString();
        this.posterArtUri = parcel.readString();
        this.durationMillis = parcel.readLong();
        this.intentUri = parcel.readString();
        this.type = parcel.readLong();
        this.interactionCount = parcel.readLong();
        this.posterAspectRatio = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.shortDescription);
        parcel.writeString(this.posterArtUri);
        parcel.writeLong(this.durationMillis);
        parcel.writeString(this.intentUri);
        parcel.writeLong(this.type);
        parcel.writeLong(this.interactionCount);
        parcel.writeInt(this.posterAspectRatio);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PreviewProgramData previewProgramData = (PreviewProgramData) obj;
        return this.durationMillis == previewProgramData.durationMillis && this.type == previewProgramData.type && this.interactionCount == previewProgramData.interactionCount && this.posterAspectRatio == previewProgramData.posterAspectRatio && Objects.equals(this.title, previewProgramData.title) && Objects.equals(this.shortDescription, previewProgramData.shortDescription) && Objects.equals(this.posterArtUri, previewProgramData.posterArtUri) && Objects.equals(this.intentUri, previewProgramData.intentUri);
    }

    public int hashCode() {
        return Objects.hash(this.title, this.shortDescription, this.posterArtUri, Long.valueOf(this.durationMillis), this.intentUri, Long.valueOf(this.type), Long.valueOf(this.interactionCount), Integer.valueOf(this.posterAspectRatio));
    }
}
