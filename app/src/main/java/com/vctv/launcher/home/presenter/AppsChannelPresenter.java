package com.vctv.launcher.home.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.leanback.widget.Presenter;
import com.vctv.launcher.R;
import com.vctv.launcher.app.AppHelper;
import com.vctv.launcher.util.AppConfigUtil;

/* JADX INFO: loaded from: classes.dex */
public class AppsChannelPresenter extends Presenter {
    private final AppHelper appHelper;

    @Override // androidx.leanback.widget.Presenter
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object obj) {
    }

    @Override // androidx.leanback.widget.Presenter
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    public AppsChannelPresenter(AppHelper appHelper) {
        this.appHelper = appHelper;
    }

    @Override // androidx.leanback.widget.Presenter
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View viewInflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_apps_channel, viewGroup, false);
        TextView textView = (TextView) viewInflate.findViewById(R.id.favorites_apps);
        if (textView != null) {
            textView.setTextColor(AppConfigUtil.getDefaultTextColor(viewInflate.getContext()));
        }
        if (viewInflate.getTag(R.id.tag_apps_channel_inited) == null) {
            this.appHelper.initAppView(viewInflate);
            viewInflate.setTag(R.id.tag_apps_channel_inited, Boolean.TRUE);
        }
        return new Presenter.ViewHolder(viewInflate);
    }
}
