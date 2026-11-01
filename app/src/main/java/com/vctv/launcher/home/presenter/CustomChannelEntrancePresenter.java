package com.vctv.launcher.home.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.leanback.widget.Presenter;
import com.vctv.launcher.R;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.home.CustomChannelPanelActivity;
import com.vctv.launcher.util.CustomTextView;
import com.vctv.launcher.util.ViewUtils;

/* JADX INFO: loaded from: classes.dex */
public class CustomChannelEntrancePresenter extends Presenter {
    private final float itemZoomScale = Config.get().globalStyle.itemZoomScale;
    private final int itemZoomSpeed = Config.get().globalStyle.itemZoomSpeed;
    private int textDefaultColor;
    private int textSelectedColor;

    @Override // androidx.leanback.widget.Presenter
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override // androidx.leanback.widget.Presenter
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View viewInflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_channel_entrance_layout, viewGroup, false);
        this.textSelectedColor = ViewUtils.getTextSelectedColor(viewInflate.getContext());
        this.textDefaultColor = ViewUtils.getTextDefaultColor(viewInflate.getContext());
        return new ViewHolder(viewInflate);
    }

    @Override // androidx.leanback.widget.Presenter
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object obj) {
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        viewHolder2.displayTitle.setTextColor(this.textDefaultColor);
        viewHolder2.displayDescription.setTextColor(this.textDefaultColor);
        viewHolder2.sortTitle.setTextColor(this.textDefaultColor);
        viewHolder2.sortDescription.setTextColor(this.textDefaultColor);
        viewHolder2.displayLayout.setOnFocusChangeListener(new ChannelEntranceFocusChangeListener(viewHolder2.displayEntranceBorderLayout, viewHolder2.borderDrawable, viewHolder2.displayTitle, viewHolder2.displayDescription));
        viewHolder2.sortLayout.setOnFocusChangeListener(new ChannelEntranceFocusChangeListener(viewHolder2.sortEntranceBorderLayout, viewHolder2.borderDrawable, viewHolder2.sortTitle, viewHolder2.sortDescription));
        viewHolder2.displayLayout.setOnClickListener(new ChannelEntranceOnClickListener(CustomChannelPanelActivity.FOR_DISPLAY_CHANNEL));
        viewHolder2.sortLayout.setOnClickListener(new ChannelEntranceOnClickListener(CustomChannelPanelActivity.FOR_SORT_CHANNEL));
    }

    public static class ViewHolder extends Presenter.ViewHolder {
        GradientDrawable borderDrawable;
        CustomTextView displayDescription;
        LinearLayout displayEntranceBorderLayout;
        RelativeLayout displayLayout;
        CustomTextView displayTitle;
        CustomTextView sortDescription;
        LinearLayout sortEntranceBorderLayout;
        RelativeLayout sortLayout;
        CustomTextView sortTitle;

        public ViewHolder(View view) {
            super(view);
            this.displayEntranceBorderLayout = (LinearLayout) view.findViewById(R.id.display_entrance_border_layout);
            this.sortEntranceBorderLayout = (LinearLayout) view.findViewById(R.id.sort_entrance_border_layout);
            this.displayLayout = (RelativeLayout) view.findViewById(R.id.display_channel_entrance_layout);
            this.displayTitle = (CustomTextView) view.findViewById(R.id.display_channel_entrance_title);
            this.displayDescription = (CustomTextView) view.findViewById(R.id.display_channel_entrance_description);
            this.sortLayout = (RelativeLayout) view.findViewById(R.id.sort_channel_entrance_layout);
            this.sortTitle = (CustomTextView) view.findViewById(R.id.sort_channel_entrance_title);
            this.sortDescription = (CustomTextView) view.findViewById(R.id.sort_channel_entrance_description);
            if (Configuration.GlobalStyle.SelectionMode.BORDER.equals(Config.get().globalStyle.itemSelectionMode)) {
                int iDpToPx = ViewUtils.dpToPx(view.getContext(), Config.get().globalStyle.itemBorderWidth);
                this.displayEntranceBorderLayout.setPadding(iDpToPx, iDpToPx, iDpToPx, iDpToPx);
                this.sortEntranceBorderLayout.setPadding(iDpToPx, iDpToPx, iDpToPx, iDpToPx);
                this.borderDrawable = ViewUtils.createDrawable(view.getContext(), 0, true, true);
            }
        }
    }

    public class ChannelEntranceOnClickListener implements View.OnClickListener {
        int startIntent;

        public ChannelEntranceOnClickListener(int i) {
            this.startIntent = i;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, (Class<?>) CustomChannelPanelActivity.class);
            intent.putExtra(CustomChannelPanelActivity.START_INTENT, this.startIntent);
            context.startActivity(intent);
        }
    }

    public class ChannelEntranceFocusChangeListener implements View.OnFocusChangeListener {
        GradientDrawable borderDrawable;
        LinearLayout borderLinerLayout;
        CustomTextView description;
        CustomTextView title;

        public ChannelEntranceFocusChangeListener(LinearLayout linearLayout, GradientDrawable gradientDrawable, CustomTextView customTextView, CustomTextView customTextView2) {
            this.title = customTextView;
            this.description = customTextView2;
            if (Configuration.GlobalStyle.SelectionMode.BORDER.equals(Config.get().globalStyle.itemSelectionMode)) {
                this.borderLinerLayout = linearLayout;
                this.borderDrawable = gradientDrawable;
            }
        }

        @Override // android.view.View.OnFocusChangeListener
        public void onFocusChange(View view, boolean z) {
            if (Configuration.GlobalStyle.SelectionMode.BORDER.equals(Config.get().globalStyle.itemSelectionMode)) {
                if (z) {
                    this.borderLinerLayout.setBackground(this.borderDrawable);
                    return;
                } else {
                    this.borderLinerLayout.setBackground(null);
                    return;
                }
            }
            if (z) {
                this.title.setTextColor(CustomChannelEntrancePresenter.this.textSelectedColor);
                this.description.setTextColor(CustomChannelEntrancePresenter.this.textSelectedColor);
                view.animate().scaleX(CustomChannelEntrancePresenter.this.itemZoomScale).scaleY(CustomChannelEntrancePresenter.this.itemZoomScale).setDuration(CustomChannelEntrancePresenter.this.itemZoomSpeed).start();
            } else {
                this.title.setTextColor(CustomChannelEntrancePresenter.this.textDefaultColor);
                this.description.setTextColor(CustomChannelEntrancePresenter.this.textDefaultColor);
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(0L).start();
            }
        }
    }
}
