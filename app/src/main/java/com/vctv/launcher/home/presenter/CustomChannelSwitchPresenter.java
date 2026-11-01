package com.vctv.launcher.home.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.leanback.widget.Presenter;
import com.vctv.launcher.R;
import com.vctv.launcher.data.ChannelData;
import com.vctv.launcher.data.ChannelDateManager;
import com.vctv.launcher.home.ChannelContentView;
import com.vctv.launcher.util.CustomTextView;
import com.vctv.launcher.util.ViewUtils;
import org.greenrobot.eventbus.EventBus;

/* JADX INFO: loaded from: classes.dex */
public class CustomChannelSwitchPresenter extends Presenter {
    public static int defaultColor;
    public static int selectColor;
    private final String TAG = "CustomChannelSwitchPresenter";
    private final ChannelDateManager channelDateManager;
    Context context;
    private ItemOnClickListener itemOnClickListener;

    public interface ItemOnClickListener {
        void onClick();
    }

    @Override // androidx.leanback.widget.Presenter
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    public CustomChannelSwitchPresenter(Context context) {
        this.channelDateManager = ChannelDateManager.getInstance(context);
        defaultColor = ViewUtils.getTextDefaultColor(context);
        selectColor = ViewUtils.getTextSelectedColor(context);
    }

    @Override // androidx.leanback.widget.Presenter
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        Context context = viewGroup.getContext();
        this.context = context;
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_channel_switch_item, viewGroup, false));
    }

    @Override // androidx.leanback.widget.Presenter
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object obj) {
        if (obj instanceof ChannelData) {
            final ChannelData channelData = (ChannelData) obj;
            final ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            viewHolder2.view.getContext();
            if (channelData.isPreset() && !channelData.isMovable()) {
                viewHolder2.view.setFocusable(false);
                viewHolder2.channelSwitch.setEnabled(false);
                viewHolder2.presetChannelDescription.setVisibility(0);
                viewHolder2.view.setOnClickListener(new View.OnClickListener() { // from class: com.vctv.launcher.home.presenter.CustomChannelSwitchPresenter.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        viewHolder2.channelSwitch.setChecked(true);
                    }
                });
            } else {
                viewHolder2.view.setOnClickListener(new View.OnClickListener() { // from class: com.vctv.launcher.home.presenter.CustomChannelSwitchPresenter.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        viewHolder2.channelSwitch.setChecked(!viewHolder2.channelSwitch.isChecked());
                        CustomChannelSwitchPresenter.this.channelDateManager.handleDisplayChannel(channelData, viewHolder2.channelSwitch.isChecked());
                        CustomChannelSwitchPresenter.this.itemOnClickListener.onClick();
                    }
                });
            }
            viewHolder2.channelName.setText(channelData.getDisplayName());
            viewHolder2.channelSwitch.setChecked(channelData.isShow());
            viewHolder2.channelName.setText(channelData.getDisplayName());
            viewHolder2.channelSwitch.setChecked(channelData.isShow());
            viewHolder2.view.setOnClickListener(new View.OnClickListener() { // from class: com.vctv.launcher.home.presenter.CustomChannelSwitchPresenter.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    viewHolder2.channelSwitch.setChecked(!viewHolder2.channelSwitch.isChecked());
                    CustomChannelSwitchPresenter.this.channelDateManager.handleDisplayChannel(channelData, viewHolder2.channelSwitch.isChecked());
                    CustomChannelSwitchPresenter.this.itemOnClickListener.onClick();
                    EventBus.getDefault().post(Integer.valueOf(ChannelContentView.PROGRAM_INFO_VISIBLE_EVENT));
                }
            });
        }
    }

    public static class ViewHolder extends Presenter.ViewHolder {
        CustomTextView channelName;
        Switch channelSwitch;
        CustomTextView presetChannelDescription;

        public ViewHolder(View view) {
            super(view);
            CustomTextView customTextView = (CustomTextView) view.findViewById(R.id.channel_name);
            this.channelName = customTextView;
            customTextView.setTextColor(CustomChannelSwitchPresenter.defaultColor);
            CustomTextView customTextView2 = (CustomTextView) view.findViewById(R.id.preset_channel_description);
            this.presetChannelDescription = customTextView2;
            customTextView2.setVisibility(8);
            this.presetChannelDescription.setTextColor(CustomChannelSwitchPresenter.defaultColor);
            view.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.home.presenter.CustomChannelSwitchPresenter.ViewHolder.1
                @Override // android.view.View.OnFocusChangeListener
                public void onFocusChange(View view2, boolean z) {
                    if (z) {
                        ViewHolder.this.channelName.setTextColor(CustomChannelSwitchPresenter.selectColor);
                    } else {
                        ViewHolder.this.channelName.setTextColor(CustomChannelSwitchPresenter.defaultColor);
                    }
                }
            });
            this.channelSwitch = (Switch) view.findViewById(R.id.channel_switch);
        }
    }

    public void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
        this.itemOnClickListener = itemOnClickListener;
    }
}
