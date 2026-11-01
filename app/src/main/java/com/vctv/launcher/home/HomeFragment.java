package com.vctv.launcher.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.OnChildSelectedListener;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;
import com.vctv.launcher.R;
import com.vctv.launcher.app.AppHelper;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.data.AppStrategy;
import com.vctv.launcher.data.AppsChannel;
import com.vctv.launcher.data.ChannelData;
import com.vctv.launcher.data.ChannelDateManager;
import com.vctv.launcher.selector.ChannelPresenterSelector;
import com.vctv.launcher.tab.PageFragmentAdapter;
import com.vctv.launcher.tab.TabVisibilityListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class HomeFragment extends Fragment implements PageFragmentAdapter {
    private VerticalGridView channelContentGridView;
    private ChannelDateManager channelDateManager;
    private Context context;
    private AppHelper mAppHelper;
    private ArrayObjectAdapter objectAdapter;
    private final String TAG = "HomeFragment";
    private final List<Runnable> pendingUiUpdates = new ArrayList();
    ChannelDateManager.DisplayChannelListener displayChannelListener = new AnonymousClass2();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ChannelDateManager channelDateManager = ChannelDateManager.getInstance(getContext());
        this.channelDateManager = channelDateManager;
        channelDateManager.setDisplayChannelListener(this.displayChannelListener);
        AppHelper appHelper = new AppHelper(this, AppStrategy.FAV);
        this.mAppHelper = appHelper;
        appHelper.initOnCreate();
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.fragment_home, viewGroup, false);
        this.channelContentGridView = (VerticalGridView) viewInflate.findViewById(R.id.channels);
        this.channelContentGridView.setItemViewCacheSize(2);
        this.channelContentGridView.setHasFixedSize(true);
        this.channelContentGridView.setVerticalSpacing(com.vctv.launcher.util.ViewUtils.dpToPx(requireContext(), 6.0f));
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new ChannelPresenterSelector(this.mAppHelper));
        this.objectAdapter = arrayObjectAdapter;
        arrayObjectAdapter.add(AppsChannel.INSTANCE);
        if (Config.get().pagination.channels.enabled) {
            this.objectAdapter.add(new Object());
        }
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter();
        itemBridgeAdapter.setAdapter(this.objectAdapter);
        this.channelContentGridView.setAdapter(itemBridgeAdapter);
        if (this.channelDateManager != null && this.channelDateManager.isInitialLoadFinished()) {
            this.displayChannelListener.completeChannelDataLoad();
        }
        this.channelContentGridView.setOnChildSelectedListener(new OnChildSelectedListener() { // from class: com.vctv.launcher.home.HomeFragment$$ExternalSyntheticLambda0
            @Override // androidx.leanback.widget.OnChildSelectedListener
            public final void onChildSelected(ViewGroup viewGroup2, View view, int i, long j) {
                HomeFragment.this.m187lambda$onCreateView$0$commwlauncherhomeHomeFragment(viewGroup2, view, i, j);
            }
        });
        this.channelContentGridView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.vctv.launcher.home.HomeFragment.1
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 0) {
                    RecyclerView.ViewHolder viewHolderFindViewHolderForAdapterPosition = HomeFragment.this.channelContentGridView.findViewHolderForAdapterPosition(HomeFragment.this.channelContentGridView.getSelectedPosition());
                    if (viewHolderFindViewHolderForAdapterPosition == null || !(viewHolderFindViewHolderForAdapterPosition.itemView instanceof ChannelContentView)) {
                        return;
                    }
                    ((ChannelContentView) viewHolderFindViewHolderForAdapterPosition.itemView).expandViewForFocus();
                }
            }
        });
        return viewInflate;
    }

    /* JADX INFO: renamed from: lambda$onCreateView$0$com-mw-launcher-home-HomeFragment, reason: not valid java name */
    /* synthetic */ void m187lambda$onCreateView$0$commwlauncherhomeHomeFragment(ViewGroup viewGroup, View view, int i, long j) {
        setTabLayoutHide(i != 0);
    }

    private void setTabLayoutHide(boolean z) {
        Object obj = this.context;
        if (obj instanceof TabVisibilityListener) {
            if (z) {
                ((TabVisibilityListener) obj).onFocusOutOfFirstRow();
            } else {
                ((TabVisibilityListener) obj).onFocusInFirstRow();
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mAppHelper.registerActivityResultLauncher();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAppHelper.doOnResume();
        if (!this.pendingUiUpdates.isEmpty()) {
            Iterator<Runnable> it = this.pendingUiUpdates.iterator();
            while (it.hasNext()) {
                it.next().run();
            }
            this.pendingUiUpdates.clear();
        }
        this.channelContentGridView.setSelectedPosition(0);
        this.mAppHelper.setSelectedPosition(0);
        handleChannelContentSelection();
    }

    private void handleChannelContentSelection() {
        if (this.channelContentGridView.getSelectedPosition() == 0) {
            setTabLayoutHide(false);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mAppHelper.doOnPause();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroyView() {
        this.mAppHelper.unregisterActivityResultLauncher();
        super.onDestroyView();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mAppHelper.releaseOnDestroy();
        this.mAppHelper = null;
        super.onDestroy();
    }

    /* JADX INFO: renamed from: com.vctv.launcher.home.HomeFragment$2, reason: invalid class name */
    class AnonymousClass2 implements ChannelDateManager.DisplayChannelListener {
        AnonymousClass2() {
        }

        @Override // com.vctv.launcher.data.ChannelDateManager.DisplayChannelListener
        public void notifyDisplayChannelChange(final boolean z, final ChannelData channelData, boolean z2) {
            Runnable runnable = new Runnable() { // from class: com.vctv.launcher.home.HomeFragment$2$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AnonymousClass2.this.m188xb89782b4(z, channelData);
                }
            };
            if (HomeFragment.this.isResumed() || z2) {
                runnable.run();
            } else {
                Log.i("HomeFragment", "The state of HomeFragment is not Resumed.");
                HomeFragment.this.pendingUiUpdates.add(runnable);
            }
        }

        /* JADX INFO: renamed from: lambda$notifyDisplayChannelChange$0$com-mw-launcher-home-HomeFragment$2, reason: not valid java name */
        /* synthetic */ void m188xb89782b4(boolean z, ChannelData channelData) {
            if (!z) {
                HomeFragment.this.objectAdapter.remove(channelData);
            } else if (channelData.isPreset()) {
                addDataIfNeed(1, channelData);
            } else {
                addDataIfNeed(HomeFragment.this.objectAdapter.size() - 1, channelData);
            }
        }

        private void addDataIfNeed(int i, ChannelData channelData) {
            if (HomeFragment.this.objectAdapter == null) {
                return;
            }
            int size = HomeFragment.this.objectAdapter.size();
            int iMax = Math.max(0, Math.min(i, size));
            if (iMax == 0 && size > 0 && (HomeFragment.this.objectAdapter.get(0) instanceof AppsChannel)) {
                iMax = 1;
            }
            if (HomeFragment.this.objectAdapter.indexOf(channelData) < 0) {
                HomeFragment.this.objectAdapter.add(iMax, channelData);
            }
        }

        @Override // com.vctv.launcher.data.ChannelDateManager.DisplayChannelListener
        public void notifyMoveChannel(int i, int i2) {
            HomeFragment.this.objectAdapter.move(i, i2);
        }

        @Override // com.vctv.launcher.data.ChannelDateManager.DisplayChannelListener
        public void notifyUpdateChannel(final ChannelData channelData) {
            Runnable runnable = new Runnable() { // from class: com.vctv.launcher.home.HomeFragment$2$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AnonymousClass2.this.m189lambda$notifyUpdateChannel$1$commwlauncherhomeHomeFragment$2(channelData);
                }
            };
            if (HomeFragment.this.isResumed()) {
                runnable.run();
            } else {
                Log.i("HomeFragment", "The state of HomeFragment is not Resumed.");
                HomeFragment.this.pendingUiUpdates.add(runnable);
            }
        }

        /* JADX INFO: renamed from: lambda$notifyUpdateChannel$1$com-mw-launcher-home-HomeFragment$2, reason: not valid java name */
        /* synthetic */ void m189lambda$notifyUpdateChannel$1$commwlauncherhomeHomeFragment$2(ChannelData channelData) {
            HomeFragment.this.objectAdapter.notifyArrayItemRangeChanged(HomeFragment.this.objectAdapter.indexOf(channelData), 1);
        }

        @Override // com.vctv.launcher.data.ChannelDateManager.DisplayChannelListener
        public void completeChannelDataLoad() {
            if (HomeFragment.this.objectAdapter == null) {
                return;
            }
            List<ChannelData> displayChannelDataListNeedSort = HomeFragment.this.channelDateManager.getDisplayChannelDataListNeedSort();
            for (int i = 0; i < displayChannelDataListNeedSort.size(); i++) {
                ChannelData cd = displayChannelDataListNeedSort.get(i);
                if (cd.getPreviewProgram() != null && !cd.getPreviewProgram().isEmpty()) {
                    if (HomeFragment.this.objectAdapter.indexOf(cd) == -1) {
                        int insertIndex = HomeFragment.this.objectAdapter.size();
                        if (insertIndex > 1 && !(HomeFragment.this.objectAdapter.get(insertIndex - 1) instanceof ChannelData) && !(HomeFragment.this.objectAdapter.get(insertIndex - 1) instanceof AppsChannel)) {
                            insertIndex--;
                        }
                        HomeFragment.this.objectAdapter.add(insertIndex, cd);
                    }
                }
            }
        }
    }

    @Override // com.vctv.launcher.tab.PageFragmentAdapter
    public int getPageTitle() {
        return R.string.home;
    }
}
