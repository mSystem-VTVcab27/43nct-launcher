package com.vctv.launcher.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AppViewModel extends ViewModel {
    private MutableLiveData<List<AppItem>> items;
    private MutableLiveData<List<AppItem>> mAppItems;
    private MutableLiveData<List<AppItem>> mFavItems;

    public MutableLiveData<List<AppItem>> getItems() {
        if (this.items == null) {
            this.items = new MutableLiveData<>();
        }
        return this.items;
    }

    public MutableLiveData<List<AppItem>> getAppItems() {
        if (this.mAppItems == null) {
            this.mAppItems = new MutableLiveData<>();
        }
        return this.mAppItems;
    }

    public MutableLiveData<List<AppItem>> getFavItems() {
        if (this.mFavItems == null) {
            this.mFavItems = new MutableLiveData<>();
        }
        return this.mFavItems;
    }
}
