package com.vctv.launcher.home.presenter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.Presenter;
import com.vctv.launcher.R;
import com.vctv.launcher.data.ChannelData;
import com.vctv.launcher.util.CustomTextView;
import com.vctv.launcher.util.ViewUtils;

/* JADX INFO: loaded from: classes.dex */
public class SortChannelItemPresenter extends Presenter {
    public static int defaultColor;
    public static int selectColor;
    ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onClick(View view, ChannelData channelData);
    }

    @Override // androidx.leanback.widget.Presenter
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override // androidx.leanback.widget.Presenter
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        View viewInflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sort_chanel_item, viewGroup, false);
        defaultColor = ViewUtils.getTextDefaultColor(viewInflate.getContext());
        selectColor = ViewUtils.getTextSelectedColor(viewInflate.getContext());
        return new ViewHolder(viewInflate);
    }

    @Override // androidx.leanback.widget.Presenter
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object obj) {
        String string;
        final ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        if (obj instanceof ChannelData) {
            final ChannelData channelData = (ChannelData) obj;
            Context context = viewHolder2.view.getContext();
            PackageManager packageManager = context.getPackageManager();
            try {
                string = packageManager.getApplicationLabel(packageManager.getApplicationInfo(channelData.getPackageName(), 0)).toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                string = "";
            }
            String displayName = channelData.getDisplayName();
            if (!TextUtils.isEmpty(string) && !TextUtils.isEmpty(displayName) && !displayName.contains(string)) {
                displayName = string + ":" + displayName;
            }
            if (channelData.isPreset() && !channelData.isMovable()) {
                viewHolder2.view.setEnabled(false);
                viewHolder2.sortImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_do_disturb));
                viewHolder2.presetChannelDescription.setVisibility(0);
            } else {
                viewHolder2.view.setOnClickListener(new View.OnClickListener() { // from class: com.vctv.launcher.home.presenter.SortChannelItemPresenter.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (SortChannelItemPresenter.this.itemClickListener != null) {
                            SortChannelItemPresenter.this.itemClickListener.onClick(view, channelData);
                        }
                    }
                });
                viewHolder2.view.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.home.presenter.SortChannelItemPresenter.2
                    @Override // android.view.View.OnFocusChangeListener
                    public void onFocusChange(View view, boolean z) {
                        if (z) {
                            viewHolder2.channelName.setTextColor(SortChannelItemPresenter.selectColor);
                        } else {
                            viewHolder2.channelName.setTextColor(SortChannelItemPresenter.defaultColor);
                        }
                    }
                });
            }
            viewHolder2.channelName.setText(displayName);
        }
    }

    public static class ViewHolder extends Presenter.ViewHolder {
        CustomTextView channelName;
        CustomTextView presetChannelDescription;
        ImageView sortImage;

        public ViewHolder(View view) {
            super(view);
            this.channelName = (CustomTextView) view.findViewById(R.id.channel_name);
            this.presetChannelDescription = (CustomTextView) view.findViewById(R.id.preset_channel_description);
            this.channelName.setTextColor(SortChannelItemPresenter.defaultColor);
            this.presetChannelDescription.setTextColor(SortChannelItemPresenter.defaultColor);
            this.presetChannelDescription.setTextColor(SortChannelItemPresenter.defaultColor);
            this.presetChannelDescription.setVisibility(8);
            this.sortImage = (ImageView) view.findViewById(R.id.sort_image);
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
