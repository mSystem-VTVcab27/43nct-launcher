package com.vctv.launcher.app.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.leanback.widget.Presenter;
import com.vctv.launcher.R;
import com.vctv.launcher.data.AppItem;
import com.vctv.launcher.util.AppConfigUtil;

/* JADX INFO: loaded from: classes.dex */
public class AppPanelPresenter extends Presenter {
    private static final String TAG = "AppPanelPresenter";
    private Context mContext;

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) viewHolder;
            vh.banner.setImageDrawable(null);
            vh.banner.setBackground(null);
            vh.icon.setImageDrawable(null);
            vh.icon.setBackground(null);
        }
    }

    @Override // androidx.leanback.widget.Presenter
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        if (this.mContext == null) {
            this.mContext = viewGroup.getContext();
        }
        View viewInflate = LayoutInflater.from(this.mContext).inflate(R.layout.item_app_panel, viewGroup, false);
        viewInflate.setSelected(true);
        viewInflate.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.app.presenter.AppPanelPresenter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                AppPanelPresenter.this.m175x2c308a8f(view, z);
            }
        });
        return new ViewHolder(viewInflate);
    }

    /* JADX INFO: renamed from: lambda$onCreateViewHolder$0$com-mw-launcher-app-presenter-AppPanelPresenter, reason: not valid java name */
    /* synthetic */ void m175x2c308a8f(View view, boolean z) {
        TextView textView = (TextView) view.findViewById(R.id.label);
        Context context = this.mContext;
        textView.setTextColor(z ? AppConfigUtil.getSelectedTextColor(context) : AppConfigUtil.getDefaultTextColor(context));
    }

    @Override // androidx.leanback.widget.Presenter
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object obj) {
        if (obj instanceof AppItem) {
            AppItem appItem = (AppItem) obj;
            ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            viewHolder2.banner.setImageDrawable(null);
            viewHolder2.icon.setImageDrawable(null);
            if (appItem.getBanner() != null) {
                RoundedBitmapDrawable roundedBitmapDrawableCreate = RoundedBitmapDrawableFactory.create(this.mContext.getResources(), getBitmapFromDrawable(appItem.getBanner()));
                roundedBitmapDrawableCreate.setCornerRadius(AppConfigUtil.getCornerRadius(this.mContext));
                viewHolder2.banner.setImageDrawable(roundedBitmapDrawableCreate);
            } else if (appItem.getIcon() != null) {
                viewHolder2.banner.setImageDrawable(drawDefaultBg());
                viewHolder2.icon.setImageDrawable(appItem.getIcon());
            }
            if (TextUtils.isEmpty(appItem.getLabel())) {
                return;
            }
            viewHolder2.label.setText(appItem.getLabel());
            viewHolder2.label.setTextColor(AppConfigUtil.getDefaultTextColor(this.mContext));
            viewHolder2.view.setContentDescription(appItem.getLabel());
        }
    }

    private GradientDrawable drawDefaultBg() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setColor(ContextCompat.getColor(this.mContext, R.color.app_bg));
        gradientDrawable.setCornerRadius(AppConfigUtil.getCornerRadius(this.mContext));
        return gradientDrawable;
    }

    public static class ViewHolder extends Presenter.ViewHolder {
        private final ImageView banner;
        private final ImageView icon;
        private final TextView label;

        public ViewHolder(View view) {
            super(view);
            this.banner = (ImageView) view.findViewById(R.id.banner);
            this.icon = (ImageView) view.findViewById(R.id.icon);
            this.label = (TextView) view.findViewById(R.id.label);
        }
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }
}
