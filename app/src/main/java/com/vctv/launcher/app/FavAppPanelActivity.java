package com.vctv.launcher.app;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.leanback.widget.DiffCallback;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.VerticalGridView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.vctv.launcher.R;
import com.vctv.launcher.app.presenter.AppPanelPresenter;
import com.vctv.launcher.data.AppItem;
import com.vctv.launcher.data.AppViewModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
public class FavAppPanelActivity extends AppCompatActivity {
    private AppManager mAppManager;
    private AppViewModel mAppModel;
    private AppArrayObjectAdapter mArrayAdapter;
    private VerticalGridView mView;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ServiceConnection connection = new AnonymousClass1();
    private final DiffCallback<AppItem> diffCallback = new DiffCallback<AppItem>() { // from class: com.vctv.launcher.app.FavAppPanelActivity.3
        @Override // androidx.leanback.widget.DiffCallback
        public boolean areContentsTheSame(AppItem appItem, AppItem appItem2) {
            return appItem.areContentsTheSame(appItem2);
        }

        @Override // androidx.leanback.widget.DiffCallback
        public boolean areItemsTheSame(AppItem appItem, AppItem appItem2) {
            return Objects.equals(appItem, appItem2);
        }
    };

    /* JADX INFO: renamed from: com.vctv.launcher.app.FavAppPanelActivity$1, reason: invalid class name */
    class AnonymousClass1 implements ServiceConnection {
        AnonymousClass1() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            FavAppPanelActivity.this.mAppManager = ((AppManager.LocalBinder) iBinder).getAppManager();
            FavAppPanelActivity.this.executorService.execute(new Runnable() { // from class: com.vctv.launcher.app.FavAppPanelActivity$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AnonymousClass1.this.m170xd1e71491();
                }
            });
        }

        /* JADX INFO: renamed from: lambda$onServiceConnected$1$com-mw-launcher-app-FavAppPanelActivity$1, reason: not valid java name */
        /* synthetic */ void m170xd1e71491() {
            if (FavAppPanelActivity.this.mAppManager != null) {
                ArrayList arrayList = new ArrayList(FavAppPanelActivity.this.mAppManager.getFavAppItems());
                ArrayList arrayList2 = new ArrayList(FavAppPanelActivity.this.mAppManager.getAppItems());
                arrayList2.removeAll(arrayList);
                arrayList2.sort(new Comparator() { // from class: com.vctv.launcher.app.FavAppPanelActivity$1$$ExternalSyntheticLambda0
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        return ((AppItem) obj).getLabel().toString().compareTo(((AppItem) obj2).getLabel().toString());
                    }
                });
                FavAppPanelActivity.this.mAppManager.setPanelItems(arrayList2);
                FavAppPanelActivity.this.mAppModel.getItems().postValue(arrayList2);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            FavAppPanelActivity.this.mAppManager = null;
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_fav_app_panel);
        this.mView = (VerticalGridView) findViewById(R.id.app_list);
        this.mArrayAdapter = new AppArrayObjectAdapter(new AppPanelPresenter());
        this.mView.setAdapter(new AppItemBridgeAdapterImpl(this.mArrayAdapter));
        this.mAppModel = (AppViewModel) new ViewModelProvider(this).get(AppViewModel.class);
        this.mAppModel.getItems().observe(this, new Observer<List<AppItem>>() { // from class: com.vctv.launcher.app.FavAppPanelActivity.2
            @Override // androidx.lifecycle.Observer
            public void onChanged(List<AppItem> list) {
                if (FavAppPanelActivity.this.mArrayAdapter == null || list == null) {
                    return;
                }
                List<AppItem> list2 = FavAppPanelActivity.this.mArrayAdapter.getmAppItemsList();
                list2.clear();
                list2.addAll(list);
                FavAppPanelActivity.this.mArrayAdapter.setItems(list2, FavAppPanelActivity.this.diffCallback);
            }
        });
        bindService(new Intent(this, (Class<?>) AppManager.class), this.connection, 1);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        unbindService(this.connection);
        if (!this.executorService.isShutdown()) {
            this.executorService.shutdown();
        }
        super.onDestroy();
    }

    public class AppItemBridgeAdapterImpl extends AppItemBridgeAdapter {
        public AppItemBridgeAdapterImpl(ObjectAdapter objectAdapter) {
            super(objectAdapter);
        }

        @Override // com.vctv.launcher.app.AppItemBridgeAdapter
        public AppItemBridgeAdapter.OnItemViewClickedListener getOnItemViewClickedListener() {
            return new AppItemBridgeAdapter.OnItemViewClickedListener() { // from class: com.vctv.launcher.app.FavAppPanelActivity.AppItemBridgeAdapterImpl.1
                @Override // com.vctv.launcher.app.AppItemBridgeAdapter.OnItemViewClickedListener
                public void onItemClicked(View view, Presenter.ViewHolder viewHolder, Object obj) {
                    if (view.hasFocus()) {
                        AppItemBridgeAdapterImpl.this.onBack(view);
                    }
                }
            };
        }

        @Override // com.vctv.launcher.app.AppItemBridgeAdapter
        public AppItemBridgeAdapter.OnItemViewLongClickedListener getOnItemViewLongClickedListener() {
            return new AppItemBridgeAdapter.OnItemViewLongClickedListener() { // from class: com.vctv.launcher.app.FavAppPanelActivity.AppItemBridgeAdapterImpl.2
                @Override // com.vctv.launcher.app.AppItemBridgeAdapter.OnItemViewLongClickedListener
                public boolean onItemLongClicked(View view, Presenter.ViewHolder viewHolder, Object obj) {
                    if (!view.hasFocus()) {
                        return true;
                    }
                    AppItemBridgeAdapterImpl.this.onBack(view);
                    return true;
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void onBack(View view) {
            Intent intent = new Intent();
            intent.putExtra("key", FavAppPanelActivity.this.mView.getChildAdapterPosition(view));
            FavAppPanelActivity.this.setResult(-1, intent);
            FavAppPanelActivity.this.finish();
        }
    }
}
