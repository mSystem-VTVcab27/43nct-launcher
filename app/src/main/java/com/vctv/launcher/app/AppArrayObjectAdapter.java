package com.vctv.launcher.app;

import android.content.ComponentName;
import androidx.collection.ArrayMap;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.Presenter;
import com.vctv.launcher.data.AppItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class AppArrayObjectAdapter extends ArrayObjectAdapter {
    private final List<AppItem> mAppItemsList;
    private final Map<ComponentName, Long> mItemIdMap;
    private long mNextItemId;

    public AppArrayObjectAdapter(Presenter presenter) {
        super(presenter);
        this.mItemIdMap = new ArrayMap();
        this.mNextItemId = 0L;
        this.mAppItemsList = new ArrayList();
        setHasStableIds(true);
    }

    public void onMove(int i, int i2) {
        move(i, i2);
        if (i >= i2) {
            while (i > i2) {
                Collections.swap(this.mAppItemsList, i, i - 1);
                i--;
            }
        } else {
            while (i < i2) {
                int i3 = i + 1;
                Collections.swap(this.mAppItemsList, i, i3);
                i = i3;
            }
        }
    }

    public void onInsert(AppItem appItem) {
        List<AppItem> list = this.mAppItemsList;
        list.add(list.size() - 1, appItem);
        add(size() - 1, appItem);
        notifyItemMoved(this.mAppItemsList.size() - 2, this.mAppItemsList.size() - 1);
    }

    public List<AppItem> getmAppItemsList() {
        return this.mAppItemsList;
    }

    @Override // androidx.leanback.widget.ObjectAdapter
    public long getId(int i) {
        if (i < 0 || i >= this.mAppItemsList.size()) {
            return 0L;
        }
        ComponentName component = this.mAppItemsList.get(i).getIntent().getComponent();
        Long l = this.mItemIdMap.get(component);
        if (l != null) {
            return l.longValue();
        }
        long j = this.mNextItemId;
        this.mNextItemId = 1 + j;
        this.mItemIdMap.put(component, Long.valueOf(j));
        return j;
    }
}
