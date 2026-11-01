package com.vctv.launcher.home;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;
import com.vctv.launcher.R;
import com.vctv.launcher.data.ChannelDateManager;
import com.vctv.launcher.home.presenter.CustomChannelSwitchPresenter;
import com.vctv.launcher.util.CustomTextView;
import com.vctv.launcher.util.ViewUtils;

/* JADX INFO: loaded from: classes.dex */
public class SingleAppChannelInfoFragment extends Fragment {
    private static final String ARG_APP_CHANNEL_GROUP = "single_app_channels";
    private AppChannelGroup appChannelGroup;
    private ChannelDateManager channelDateManager;
    boolean isModifyDate = false;
    public CustomChannelSwitchPresenter.ItemOnClickListener itemOnClickListener = new CustomChannelSwitchPresenter.ItemOnClickListener() { // from class: com.vctv.launcher.home.SingleAppChannelInfoFragment.1
        @Override // com.vctv.launcher.home.presenter.CustomChannelSwitchPresenter.ItemOnClickListener
        public void onClick() {
            SingleAppChannelInfoFragment.this.isModifyDate = true;
        }
    };
    CustomTextView title;
    VerticalGridView verticalGridView;

    public static SingleAppChannelInfoFragment newInstance(AppChannelGroup appChannelGroup) {
        SingleAppChannelInfoFragment singleAppChannelInfoFragment = new SingleAppChannelInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_APP_CHANNEL_GROUP, appChannelGroup);
        singleAppChannelInfoFragment.setArguments(bundle);
        return singleAppChannelInfoFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.fragment_single_app_channel_info, viewGroup, false);
        CustomTextView customTextView = (CustomTextView) viewInflate.findViewById(R.id.single_app_channels_title);
        this.title = customTextView;
        customTextView.setTextColor(ViewUtils.getTextDefaultColor(viewInflate.getContext()));
        this.channelDateManager = ChannelDateManager.getInstance(requireContext().getApplicationContext());
        this.verticalGridView = (VerticalGridView) viewInflate.findViewById(R.id.single_app_channel_list);
        Bundle arguments = getArguments();
        if (arguments != null) {
            AppChannelGroup appChannelGroup = (AppChannelGroup) arguments.getParcelable(ARG_APP_CHANNEL_GROUP);
            this.appChannelGroup = appChannelGroup;
            if (appChannelGroup != null) {
                initUI();
            }
        }
        return viewInflate;
    }

    private void initUI() {
        String string;
        PackageManager packageManager = requireContext().getPackageManager();
        try {
            string = packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.appChannelGroup.getPackageName(), 0)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            string = "";
        }
        this.title.setText(requireContext().getString(R.string.app_channel, string));
        CustomChannelSwitchPresenter customChannelSwitchPresenter = new CustomChannelSwitchPresenter(requireContext());
        customChannelSwitchPresenter.setItemOnClickListener(this.itemOnClickListener);
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(customChannelSwitchPresenter);
        arrayObjectAdapter.addAll(0, this.appChannelGroup.getChannelDataList());
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter();
        itemBridgeAdapter.setAdapter(arrayObjectAdapter);
        this.verticalGridView.setAdapter(itemBridgeAdapter);
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.isModifyDate) {
            this.channelDateManager.updateAllDbChannelData();
        }
    }
}
