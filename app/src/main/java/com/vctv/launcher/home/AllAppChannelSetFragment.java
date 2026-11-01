package com.vctv.launcher.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.VerticalGridView;
import com.vctv.launcher.R;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.data.ChannelData;
import com.vctv.launcher.data.ChannelDateManager;
import com.vctv.launcher.home.presenter.AppChannelInfoPresenter;
import com.vctv.launcher.util.CustomTextView;
import com.vctv.launcher.util.ViewUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class AllAppChannelSetFragment extends Fragment {
    VerticalGridView appChannelGridView;
    List<AppChannelGroup> appChannelGroupList;
    AppChannelInfoPresenter.AppChannelInfoItemClickListener appChannelInfoItemClickListener = new AppChannelInfoPresenter.AppChannelInfoItemClickListener() { // from class: com.vctv.launcher.home.AllAppChannelSetFragment.1
        @Override // com.vctv.launcher.home.presenter.AppChannelInfoPresenter.AppChannelInfoItemClickListener
        public void onClick(AppChannelGroup appChannelGroup) {
            AllAppChannelSetFragment.this.replaceFragment(SingleAppChannelInfoFragment.newInstance(appChannelGroup));
        }
    };

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.fragment_all_app_channel_set, viewGroup, false);
        ((CustomTextView) viewInflate.findViewById(R.id.channel_set_fragment_title)).setTextColor(ViewUtils.getTextDefaultColor(viewInflate.getContext()));
        this.appChannelGridView = (VerticalGridView) viewInflate.findViewById(R.id.app_channel_list);
        this.appChannelGroupList = classifyChannelByPackageName(ChannelDateManager.getInstance(requireContext()).getTifAllChannelDataList());
        AppChannelInfoPresenter appChannelInfoPresenter = new AppChannelInfoPresenter(requireContext().getPackageManager());
        appChannelInfoPresenter.setAppChannelInfoItemClickListener(this.appChannelInfoItemClickListener);
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(appChannelInfoPresenter);
        arrayObjectAdapter.addAll(0, this.appChannelGroupList);
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter();
        itemBridgeAdapter.setAdapter(arrayObjectAdapter);
        this.appChannelGridView.setAdapter(itemBridgeAdapter);
        return viewInflate;
    }

    public static List<AppChannelGroup> classifyChannelByPackageName(List<ChannelData> list) {
        List<Configuration.PresetItem> list2 = null;
        boolean z = false;
        HashMap<String, List<ChannelData>> map = new HashMap<>();
        try {
            list2 = Config.get().pagination.channels.list;
        } catch (Exception unused) {
        }
        if (list2 != null) {
            for (Configuration.PresetItem item : list2) {
                if ("com.netflix.ninja".equals(item.pkg)) {
                    z = true;
                    break;
                }
            }
        }
        for (ChannelData channelData : list) {
            String packageName = channelData.getPackageName();
            if (!map.containsKey(packageName)) {
                if (!packageName.equals("com.netflix.ninja") || z) {
                    map.put(packageName, new ArrayList<>());
                }
            }
            List<ChannelData> channelList = map.get(packageName);
            if (channelList != null) {
                channelList.add(channelData);
            }
        }
        ArrayList<AppChannelGroup> arrayList = new ArrayList<>();
        for (Map.Entry<String, List<ChannelData>> entry : map.entrySet()) {
            arrayList.add(new AppChannelGroup(entry.getKey(), entry.getValue()));
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replaceFragment(Fragment fragment) {
        FragmentManager parentFragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = parentFragmentManager.beginTransaction();
        fragmentTransactionBeginTransaction.setCustomAnimations(R.animator.side_panel_fragment_enter, R.animator.side_panel_fragment_exit, R.animator.side_panel_fragment_pop_enter, R.animator.side_panel_fragment_pop_exit);
        fragmentTransactionBeginTransaction.replace(R.id.side_fragment_container, fragment);
        fragmentTransactionBeginTransaction.addToBackStack(null);
        fragmentTransactionBeginTransaction.commit();
        parentFragmentManager.executePendingTransactions();
    }
}
