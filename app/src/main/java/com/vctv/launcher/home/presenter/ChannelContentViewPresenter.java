package com.vctv.launcher.home.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.leanback.widget.Presenter;
import com.vctv.launcher.R;
import com.vctv.launcher.data.ChannelData;
import com.vctv.launcher.home.ChannelContentView;

/* JADX INFO: loaded from: classes.dex */
public class ChannelContentViewPresenter extends Presenter {
    @Override // androidx.leanback.widget.Presenter
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override // androidx.leanback.widget.Presenter
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new ChannelContextViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.channel_content_layout, viewGroup, false));
    }

    @Override // androidx.leanback.widget.Presenter
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object obj) {
        if (obj instanceof ChannelData) {
            ((ChannelContentView) viewHolder.view).setChannelData((ChannelData) obj);
        }
    }

    public static class ChannelContextViewHolder extends Presenter.ViewHolder {
        ChannelContentView channelContentView;

        public ChannelContextViewHolder(View view) {
            super(view);
            this.channelContentView = (ChannelContentView) view;
        }
    }
}
