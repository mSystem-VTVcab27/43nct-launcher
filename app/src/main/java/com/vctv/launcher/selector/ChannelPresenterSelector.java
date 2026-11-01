package com.vctv.launcher.selector;

import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import com.vctv.launcher.app.AppHelper;
import com.vctv.launcher.data.AppsChannel;
import com.vctv.launcher.data.ChannelData;
import com.vctv.launcher.home.presenter.AppsChannelPresenter;
import com.vctv.launcher.home.presenter.ChannelContentViewPresenter;
import com.vctv.launcher.home.presenter.CustomChannelEntrancePresenter;

/* JADX INFO: loaded from: classes.dex */
public class ChannelPresenterSelector extends PresenterSelector {
    private final AppsChannelPresenter appsChannelPresenter;
    private final ChannelContentViewPresenter channelContentViewPresenter = new ChannelContentViewPresenter();
    private final CustomChannelEntrancePresenter customChannelEntrancePresenter = new CustomChannelEntrancePresenter();

    public ChannelPresenterSelector(AppHelper appHelper) {
        this.appsChannelPresenter = new AppsChannelPresenter(appHelper);
    }

    @Override // androidx.leanback.widget.PresenterSelector
    public Presenter getPresenter(Object obj) {
        if (obj instanceof AppsChannel) {
            return this.appsChannelPresenter;
        }
        if (obj instanceof ChannelData) {
            return this.channelContentViewPresenter;
        }
        return this.customChannelEntrancePresenter;
    }
}
