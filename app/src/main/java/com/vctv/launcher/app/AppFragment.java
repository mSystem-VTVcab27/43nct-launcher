package com.vctv.launcher.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.OnChildSelectedListener;
import com.vctv.launcher.R;
import com.vctv.launcher.app.view.AppVerticalGridView;
import com.vctv.launcher.data.AppStrategy;
import com.vctv.launcher.tab.PageFragmentAdapter;
import com.vctv.launcher.tab.TabVisibilityListener;
import com.vctv.launcher.util.AppConfigUtil;

/* JADX INFO: loaded from: classes.dex */
public class AppFragment extends Fragment implements PageFragmentAdapter {
    private Context context;
    private AppHelper mAppHelper;
    private AppVerticalGridView mAppListView;

    public static AppFragment newInstance() {
        return new AppFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        AppHelper appHelper = new AppHelper(this, AppStrategy.DEFAULT);
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
        return layoutInflater.inflate(R.layout.fragment_app, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        ((TextView) view.findViewById(R.id.install_apps)).setTextColor(AppConfigUtil.getDefaultTextColor(view.getContext()));
        AppVerticalGridView appVerticalGridView = (AppVerticalGridView) view.findViewById(R.id.app_vertical_view);
        this.mAppListView = appVerticalGridView;
        appVerticalGridView.setOnChildSelectedListener(new OnChildSelectedListener() { // from class: com.vctv.launcher.app.AppFragment$$ExternalSyntheticLambda0
            @Override // androidx.leanback.widget.OnChildSelectedListener
            public final void onChildSelected(ViewGroup viewGroup, View view2, int i, long j) {
                AppFragment.this.onChildSelected(viewGroup, view2, i, j);
            }
        });
        this.mAppHelper.initAppView(view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onChildSelected(ViewGroup viewGroup, View view, int i, long j) {
        int appSize = AppConfigUtil.getAppSize();
        if (appSize <= 0) {
            appSize = 1;
        }
        int i2 = i / appSize;
        Object obj = this.context;
        if (obj instanceof TabVisibilityListener) {
            if (i2 == 0) {
                ((TabVisibilityListener) obj).onFocusInFirstRow();
            } else if (i2 == 1) {
                ((TabVisibilityListener) obj).onFocusOutOfFirstRow();
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAppHelper.doOnResume();
        handleAppListSelection();
    }

    private void handleAppListSelection() {
        if (this.mAppListView.getSelectedPosition() == 0) {
            Object obj = this.context;
            if (obj instanceof TabVisibilityListener) {
                ((TabVisibilityListener) obj).onFocusInFirstRow();
                return;
            }
            return;
        }
        this.mAppListView.setSelectedPositionSmooth(0);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mAppHelper.doOnPause();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mAppHelper.releaseOnDestroy();
        this.mAppHelper = null;
        super.onDestroy();
    }

    @Override // com.vctv.launcher.tab.PageFragmentAdapter
    public int getPageTitle() {
        return R.string.apps;
    }
}
