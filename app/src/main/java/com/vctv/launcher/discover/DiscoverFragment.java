package com.vctv.launcher.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.vctv.launcher.R;
import com.vctv.launcher.tab.PageFragmentAdapter;

/* JADX INFO: loaded from: classes.dex */
public class DiscoverFragment extends Fragment implements PageFragmentAdapter {
    public static DiscoverFragment newInstance() {
        return new DiscoverFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_discover, viewGroup, false);
    }

    @Override // com.vctv.launcher.tab.PageFragmentAdapter
    public int getPageTitle() {
        return R.string.discover;
    }
}
