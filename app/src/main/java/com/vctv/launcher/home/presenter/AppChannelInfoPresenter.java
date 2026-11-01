package com.vctv.launcher.home.presenter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.leanback.widget.Presenter;
import com.vctv.launcher.R;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.home.AppChannelGroup;
import com.vctv.launcher.util.CustomTextView;
import com.vctv.launcher.util.ViewUtils;

/* JADX INFO: loaded from: classes.dex */
public class AppChannelInfoPresenter extends Presenter {
    public static String TAG = "AppChannelInfoPresenter";
    AppChannelInfoItemClickListener appChannelInfoItemClickListener;
    private PackageManager pm;

    public interface AppChannelInfoItemClickListener {
        void onClick(AppChannelGroup appChannelGroup);
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) viewHolder;
            if (vh.appBanner != null) {
                vh.appBanner.setImageDrawable(null);
            }
        }
    }

    public AppChannelInfoPresenter(PackageManager packageManager) {
        this.pm = packageManager;
    }

    @Override // androidx.leanback.widget.Presenter
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_channel_info_item, viewGroup, false));
    }

    @Override // androidx.leanback.widget.Presenter
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object obj) {
        if (obj instanceof AppChannelGroup) {
            final AppChannelGroup appChannelGroup = (AppChannelGroup) obj;
            final ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            viewHolder2.view.setOnClickListener(new View.OnClickListener() { // from class: com.vctv.launcher.home.presenter.AppChannelInfoPresenter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (AppChannelInfoPresenter.this.appChannelInfoItemClickListener != null) {
                        AppChannelInfoPresenter.this.appChannelInfoItemClickListener.onClick(appChannelGroup);
                    }
                }
            });
            String string = "";
            Context context = viewHolder2.view.getContext();
            try {
                ApplicationInfo applicationInfo = this.pm.getApplicationInfo(appChannelGroup.getPackageName(), 0);
                Drawable applicationBanner = this.pm.getApplicationBanner(applicationInfo);
                Drawable applicationIcon = this.pm.getApplicationIcon(applicationInfo);
                string = this.pm.getApplicationLabel(applicationInfo).toString();
                if (applicationBanner != null) {
                    RoundedBitmapDrawable roundedBitmapDrawableCreate = RoundedBitmapDrawableFactory.create(context.getResources(), getBitmapFromDrawable(applicationBanner));
                    roundedBitmapDrawableCreate.setCornerRadius(ViewUtils.dpToPx(context, Config.get().globalStyle.itemCornerRadius));
                    viewHolder2.appBanner.setImageDrawable(roundedBitmapDrawableCreate);
                } else {
                    viewHolder2.appBanner.setImageDrawable(applicationIcon);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "Package not found: " + appChannelGroup.getPackageName() + ", using fallback name");
                if ("vn.vtv.vtvgo".equals(appChannelGroup.getPackageName())) {
                    string = "VTVgo TV";
                } else if ("vn.mytv.livetv".equals(appChannelGroup.getPackageName())) {
                    string = "MyTV For Smart TV";
                } else if (appChannelGroup.getChannelDataList() != null && !appChannelGroup.getChannelDataList().isEmpty()) {
                    string = appChannelGroup.getChannelDataList().get(0).getDisplayName();
                } else {
                    string = appChannelGroup.getPackageName();
                }
                viewHolder2.appBanner.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_app));
            }
            viewHolder2.appName.setText(string);
            viewHolder2.appChannelCount.setText(context.getString(R.string.channel_count, String.valueOf(appChannelGroup.getChannelDataList().size())));
            viewHolder2.view.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.home.presenter.AppChannelInfoPresenter.2
                @Override // android.view.View.OnFocusChangeListener
                public void onFocusChange(View view, boolean z) {
                    if (z) {
                        viewHolder2.appName.setTextColor(ViewUtils.getTextSelectedColor(view.getContext()));
                        viewHolder2.appChannelCount.setTextColor(ViewUtils.getTextSelectedColor(view.getContext()));
                    } else {
                        viewHolder2.appName.setTextColor(ViewUtils.getTextDefaultColor(view.getContext()));
                        viewHolder2.appChannelCount.setTextColor(ViewUtils.getTextDefaultColor(view.getContext()));
                    }
                }
            });
        }
    }

    public static class ViewHolder extends Presenter.ViewHolder {
        ImageView appBanner;
        CustomTextView appChannelCount;
        CustomTextView appName;

        public ViewHolder(View view) {
            super(view);
            this.appBanner = (ImageView) view.findViewById(R.id.app_banner);
            this.appName = (CustomTextView) view.findViewById(R.id.app_name);
            this.appChannelCount = (CustomTextView) view.findViewById(R.id.app_channel_count);
            this.appName.setTextColor(ViewUtils.getTextDefaultColor(view.getContext()));
            this.appChannelCount.setTextColor(ViewUtils.getTextDefaultColor(view.getContext()));
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }

    public void setAppChannelInfoItemClickListener(AppChannelInfoItemClickListener appChannelInfoItemClickListener) {
        this.appChannelInfoItemClickListener = appChannelInfoItemClickListener;
    }
}
