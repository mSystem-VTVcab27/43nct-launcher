package com.vctv.launcher.data;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class AppItem implements Comparable<AppItem> {
    private static final String TAG = "AppItem";
    private final Intent infoIntent;
    private Drawable mBanner;
    private Drawable mCachedBannerDrawable;

    public Drawable getCachedBannerDrawable() {
        return this.mCachedBannerDrawable;
    }

    public void setCachedBannerDrawable(Drawable drawable) {
        this.mCachedBannerDrawable = drawable;
    }
    private Drawable mIcon;
    protected final Intent mIntent;
    private CharSequence mLabel;
    private final long mPriority;
    private final boolean movable;
    private final boolean removable;
    private final Intent uninstallIntent;
    private boolean uninstallable;

    public AppItem(Context context, ResolveInfo resolveInfo, long j) {
        this(context, resolveInfo, j, true, true);
    }

    public AppItem(Drawable drawable, CharSequence charSequence) {
        this.infoIntent = null;
        this.uninstallIntent = null;
        this.mBanner = null;
        this.mIcon = drawable;
        this.mLabel = charSequence;
        this.mPriority = 0L;
        this.movable = true;
        this.removable = true;
        this.uninstallable = true;
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.example.myapp", "com.example.myapp.MyActivity"));
        this.mIntent = intent;
    }

    public AppItem(Context context, ResolveInfo resolveInfo, long j, boolean z, boolean z2) {
        this(context, resolveInfo, "", "", j, z, z2);
    }

    public AppItem(Context context, ResolveInfo resolveInfo, String str, String str2, long j, boolean z, boolean z2) {
        Bitmap bitmapDecodeFile;
        Bitmap bitmapDecodeFile2;
        StringBuilder sb;
        Bitmap bitmapDecodeFile3;
        Intent intentMakeMainActivity = Intent.makeMainActivity(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name));
        this.mIntent = intentMakeMainActivity;
        intentMakeMainActivity.addFlags(268435456);
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        this.infoIntent = intent;
        intent.setData(Uri.parse("package:" + resolveInfo.activityInfo.packageName));
        Intent intent2 = new Intent("android.intent.action.DELETE");
        this.uninstallIntent = intent2;
        intent2.setData(Uri.parse("package:" + resolveInfo.activityInfo.packageName));
        this.mPriority = j;
        PackageManager packageManager = context.getPackageManager();
        CharSequence charSequenceLoadLabel = null;
        try {
            try {
                Drawable drawableLoadBanner = resolveInfo.activityInfo.loadBanner(packageManager);
                if (drawableLoadBanner == null && !TextUtils.isEmpty(str2) && (bitmapDecodeFile3 = BitmapFactory.decodeFile(str2)) != null) {
                    this.mBanner = new BitmapDrawable(context.getResources(), bitmapDecodeFile3);
                    sb = new StringBuilder();
                    Log.d(TAG, sb.append(resolveInfo.activityInfo.packageName).append(" use preset banner").toString());
                } else {
                    this.mBanner = drawableLoadBanner;
                }
            } catch (Exception unused) {
                Log.d(TAG, resolveInfo.activityInfo.packageName + " default banner not exist");
                if (!TextUtils.isEmpty(str2) && (bitmapDecodeFile2 = BitmapFactory.decodeFile(str2)) != null) {
                    this.mBanner = new BitmapDrawable(context.getResources(), bitmapDecodeFile2);
                    sb = new StringBuilder();
                } else {
                    this.mBanner = null;
                }
            }
            try {
                try {
                    this.mIcon = resolveInfo.loadIcon(packageManager);
                } catch (Exception unused2) {
                    Log.d(TAG, resolveInfo.activityInfo.packageName + " default icon not exist");
                    this.mIcon = null;
                }
                try {
                    charSequenceLoadLabel = resolveInfo.loadLabel(packageManager);
                } catch (Exception unused3) {
                    Log.d(TAG, resolveInfo.activityInfo.packageName + " default label not exist");
                }
                if (charSequenceLoadLabel == null) {
                    str = TextUtils.isEmpty(str) ? resolveInfo.activityInfo.packageName : str;
                    this.mLabel = str;
                    Log.d(TAG, resolveInfo.activityInfo.packageName + " label use " + ((Object) str));
                } else {
                    this.mLabel = charSequenceLoadLabel;
                }
                if (resolveInfo.activityInfo.applicationInfo == null) {
                    this.uninstallable = false;
                } else {
                    this.uninstallable = (resolveInfo.activityInfo.applicationInfo.flags & 129) == 0;
                }
                this.movable = z;
                this.removable = z2;
            } catch (Throwable th) {
                this.mIcon = null;
                throw th;
            }
        } catch (Throwable th2) {
            if (!TextUtils.isEmpty(str2) && (bitmapDecodeFile = BitmapFactory.decodeFile(str2)) != null) {
                this.mBanner = new BitmapDrawable(context.getResources(), bitmapDecodeFile);
                Log.d(TAG, resolveInfo.activityInfo.packageName + " use preset banner");
            } else {
                this.mBanner = null;
            }
            throw th2;
        }
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public Intent getInfoIntent() {
        return this.infoIntent;
    }

    public Intent getUninstallIntent() {
        return this.uninstallIntent;
    }

    public Drawable getBanner() {
        return this.mBanner;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public boolean isMovable() {
        return this.movable;
    }

    public boolean isRemovable() {
        return this.removable;
    }

    public long getmPriority() {
        return this.mPriority;
    }

    public boolean isUninstallable() {
        return this.uninstallable;
    }

    public boolean areContentsTheSame(AppItem appItem) {
        return Objects.equals(appItem.getBanner(), getBanner()) && Objects.equals(appItem.getIcon(), getIcon()) && Objects.equals(appItem.getLabel(), getLabel());
    }

    @Override // java.lang.Comparable
    public int compareTo(AppItem appItem) {
        long j = appItem.mPriority - this.mPriority;
        if (j != 0) {
            return j < 0 ? -1 : 1;
        }
        return getLabel().toString().compareTo(appItem.getLabel().toString());
    }

    public boolean equals(Object obj) {
        return (obj instanceof AppItem) && this.mIntent.filterEquals(((AppItem) obj).getIntent());
    }

    public int hashCode() {
        return this.mIntent.hashCode();
    }

    public String toString() {
        return ((Object) this.mLabel) + " -- " + this.mIntent.getComponent().toString();
    }

    public String toDebugString() {
        return "Label: " + ((Object) this.mLabel) + " Intent: " + this.mIntent + " Banner: " + this.mBanner + " Icon: " + this.mIcon + " Priority: " + Long.toString(this.mPriority);
    }
}
