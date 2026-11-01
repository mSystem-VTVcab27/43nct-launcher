package com.vctv.launcher.app;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.DiffCallback;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.Presenter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.vctv.launcher.R;
import com.vctv.launcher.app.event.CancelMoveEvent;
import com.vctv.launcher.app.event.OnMoveEvent;
import com.vctv.launcher.app.popmenu.PopMenuDirector;
import com.vctv.launcher.app.popmenu.PopMenuHelper;
import com.vctv.launcher.app.presenter.AppPresenter;
import com.vctv.launcher.app.view.AppHorizonGridView;
import com.vctv.launcher.app.view.AppVerticalGridView;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.data.AppItem;
import com.vctv.launcher.data.AppStrategy;
import com.vctv.launcher.data.AppViewModel;
import com.vctv.launcher.util.AppConfigUtil;
import com.vctv.launcher.util.CommonUtil;
import com.vctv.launcher.util.CustomTextView;
import com.vctv.launcher.util.LogUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/* JADX INFO: loaded from: classes.dex */
public class AppHelper {
    private static final int INTERVAL_TIME = 500;
    private static final int MSG_UPDATE_DATA = 1;
    private static boolean mIsRefreshFavAppList = false;
    private final Configuration.App mAppConfig;
    private AppManager mAppManager;
    private AppViewModel mAppModel;
    private AppArrayObjectAdapter mArrayAdapter;
    private final Context mContext;
    private final Configuration.FavApp mFavAppConfig;
    private final Fragment mFragment;
    private AppHorizonGridView mHorizonView;
    private ActivityResultLauncher<Intent> mLauncher;
    private final PopMenuHelper mPopMenuHelper;
    private AppPresenter mPresenter;
    private final AppResource mResource;
    private AppStrategy.State mState;
    private final AppStrategy mStrategy;
    private AppVerticalGridView mVerticalView;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private Integer mPendingSelectedPosition = null;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.vctv.launcher.app.AppHelper.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 1) {
                AppHelper.this.mHandler.removeMessages(1);
                if (AppHelper.this.mState.equals(AppStrategy.State.DEFAULT)) {
                    AppHelper.this.mExecutor.execute(AppHelper.this.loadDataTask);
                } else {
                    AppHelper.this.mHandler.sendEmptyMessageDelayed(1, 500L);
                }
            }
        }
    };
    private final ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.vctv.launcher.app.AppHelper.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AppHelper.this.mAppManager = ((AppManager.LocalBinder) iBinder).getAppManager();
            AppHelper.this.mExecutor.execute(AppHelper.this.loadDataTask);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            AppHelper.this.mAppManager = null;
        }
    };
    private final Runnable loadDataTask = new AnonymousClass3();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.vctv.launcher.app.AppHelper.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            AppHelper.this.mHandler.sendEmptyMessage(1);
        }
    };

    /* JADX INFO: renamed from: com.vctv.launcher.app.AppHelper$3, reason: invalid class name */
    class AnonymousClass3 implements Runnable {
        AnonymousClass3() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (AppHelper.this.mAppManager != null) {
                Set<AppItem> appItems = AppHelper.this.mStrategy.equals(AppStrategy.DEFAULT) ? AppHelper.this.mAppManager.getAppItems() : AppHelper.this.mAppManager.getFavAppItems();
                if (appItems != null) {
                    List<AppItem> arrayList = new ArrayList<>(appItems);
                    arrayList.sort(new Comparator() { // from class: com.vctv.launcher.app.AppHelper$3$$ExternalSyntheticLambda0
                        @Override // java.util.Comparator
                        public final int compare(Object obj, Object obj2) {
                            return AnonymousClass3.this.m163lambda$run$0$commwlauncherappAppHelper$3((AppItem) obj, (AppItem) obj2);
                        }
                    });
                    if (AppHelper.this.mStrategy.equals(AppStrategy.DEFAULT)) {
                        AppHelper.this.mAppModel.getAppItems().postValue(arrayList);
                        return;
                    }
                    if (Config.get().netflix.isIntegrated) {
                        arrayList = AppHelper.this.checkNetflixOrder(arrayList);
                    }
                    AppHelper.this.mAppModel.getFavItems().postValue(arrayList);
                }
            }
        }

        /* JADX INFO: renamed from: lambda$run$0$com-mw-launcher-app-AppHelper$3, reason: not valid java name */
        /* synthetic */ int m163lambda$run$0$commwlauncherappAppHelper$3(AppItem appItem, AppItem appItem2) {
            return AppHelper.this.mStrategy.equals(AppStrategy.DEFAULT) ? appItem.compareTo(appItem2) : appItem2.compareTo(appItem);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<AppItem> checkNetflixOrder(List<AppItem> list) {
        AppItem appItem;
        if (list != null) {
            int i = 2;
            if (list.size() > 2) {
                if (isNetflixApp(list.get(0)) || isNetflixApp(list.get(1))) {
                    LogUtil.d("-checkNetflixOrder-ok-");
                } else {
                    int size = list.size();
                    while (true) {
                        if (i >= size) {
                            i = -1;
                            appItem = null;
                            break;
                        }
                        if (isNetflixApp(list.get(i))) {
                            appItem = list.get(i);
                            LogUtil.d("-netflixApp-index->" + i);
                            break;
                        }
                        i++;
                    }
                    if (appItem != null) {
                        LogUtil.d("-netflixApp-change-order-to-1");
                        list.remove(i);
                        list.add(1, appItem);
                    }
                }
            }
        }
        return list;
    }

    private boolean isNetflixApp(AppItem appItem) {
        return appItem != null && "com.netflix.ninja".equals(appItem.getIntent().getComponent().getPackageName());
    }

    public AppHelper(Fragment fragment, AppStrategy appStrategy) {
        Context contextRequireContext = fragment.requireContext();
        this.mContext = contextRequireContext;
        this.mFragment = fragment;
        this.mStrategy = appStrategy;
        this.mState = AppStrategy.State.DEFAULT;
        this.mResource = new AppResource(contextRequireContext);
        this.mPopMenuHelper = new PopMenuHelper(contextRequireContext, this, appStrategy);
        this.mAppConfig = Config.get().pagination.appConfig;
        this.mFavAppConfig = Config.get().pagination.favApp;
        mIsRefreshFavAppList = false;
    }

    public void initAppView(View view) {
        this.mPresenter = new AppPresenter(this.mStrategy);
        this.mArrayAdapter = new AppArrayObjectAdapter(this.mPresenter);
        AppItemBridgeAdapterImpl appItemBridgeAdapterImpl = new AppItemBridgeAdapterImpl(this.mArrayAdapter);
        this.mVerticalView = (AppVerticalGridView) view.findViewById(R.id.app_vertical_view);
        this.mHorizonView = (AppHorizonGridView) view.findViewById(R.id.app_horizon_view);
        int i = AnonymousClass6.$SwitchMap$com$mw$launcher$config$Configuration$Direction[AppConfigUtil.getDirection(this.mStrategy).ordinal()];
        if (i == 1) {
            this.mVerticalView.setVisibility(8);
            this.mHorizonView.setRowHeight(AppConfigUtil.getHorizonHeight(this.mContext));
            this.mHorizonView.setItemSpacing((int) this.mContext.getResources().getDimension(R.dimen.app_horizon_view_space));
            this.mHorizonView.setAdapter(appItemBridgeAdapterImpl);
        } else if (i == 2) {
            this.mHorizonView.setVisibility(8);
            this.mVerticalView.setColumnNumber(AppConfigUtil.getAppSize());
            this.mVerticalView.setVerticalSpacing((int) this.mContext.getResources().getDimension(R.dimen.app_vertical_view_space));
            this.mVerticalView.setAdapter(appItemBridgeAdapterImpl);
            AppVerticalGridView appVerticalGridView = this.mVerticalView;
            appVerticalGridView.setPadding(appVerticalGridView.getPaddingLeft(), this.mVerticalView.getPaddingTop(), this.mVerticalView.getPaddingRight(), AppConfigUtil.getHorizonHeight(this.mContext) / 5);
        }
        Integer num = this.mPendingSelectedPosition;
        if (num != null) {
            setSelectedPosition(num.intValue());
            this.mPendingSelectedPosition = null;
        }
    }

    public boolean isFav(AppItem appItem) {
        AppManager appManager = this.mAppManager;
        return appManager != null && appManager.isFav(appItem);
    }

    public boolean isRemovable(AppItem appItem) {
        AppManager appManager = this.mAppManager;
        return appManager != null && appManager.isRemovable(appItem);
    }

    public class AppItemBridgeAdapterImpl extends AppItemBridgeAdapter {
        public AppItemBridgeAdapterImpl(ObjectAdapter objectAdapter) {
            super(objectAdapter);
        }

        @Override // com.vctv.launcher.app.AppItemBridgeAdapter
        public AppItemBridgeAdapter.OnItemViewClickedListener getOnItemViewClickedListener() {
            return new AppItemBridgeAdapter.OnItemViewClickedListener() { // from class: com.vctv.launcher.app.AppHelper.AppItemBridgeAdapterImpl.1
                @Override // com.vctv.launcher.app.AppItemBridgeAdapter.OnItemViewClickedListener
                public void onItemClicked(View view, Presenter.ViewHolder viewHolder, Object obj) {
                    if (view.hasFocus()) {
                        int i = AnonymousClass6.$SwitchMap$com$mw$launcher$data$AppStrategy$State[AppHelper.this.mState.ordinal()];
                        if (i != 1) {
                            if (i != 2) {
                                return;
                            }
                            AppHelper.this.cancelMove(new CancelMoveEvent(view));
                        } else {
                            AppItem appItem = (AppItem) obj;
                            if (appItem.getLabel().equals(AppHelper.this.mResource.addFavPlus)) {
                                AppHelper.this.startPanelActivity();
                            } else {
                                AppHelper.this.onOpenClicked(appItem);
                            }
                        }
                    }
                }
            };
        }

        @Override // com.vctv.launcher.app.AppItemBridgeAdapter
        public AppItemBridgeAdapter.OnItemViewLongClickedListener getOnItemViewLongClickedListener() {
            return new AppItemBridgeAdapter.OnItemViewLongClickedListener() { // from class: com.vctv.launcher.app.AppHelper.AppItemBridgeAdapterImpl.2
                @Override // com.vctv.launcher.app.AppItemBridgeAdapter.OnItemViewLongClickedListener
                public boolean onItemLongClicked(View view, Presenter.ViewHolder viewHolder, Object obj) {
                    if (view.hasFocus() && !AppStrategy.State.MOVE.equals(AppHelper.this.mState)) {
                        AppItem appItem = (AppItem) obj;
                        if (!appItem.getLabel().equals(AppHelper.this.mResource.addFavPlus)) {
                            AppHelper.this.mPopMenuHelper.showPopMenu(view, appItem);
                        } else {
                            AppHelper.this.startPanelActivity();
                        }
                    }
                    return true;
                }
            };
        }
    }

    /* JADX INFO: renamed from: com.vctv.launcher.app.AppHelper$6, reason: invalid class name */
    static /* synthetic */ class AnonymousClass6 {
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$config$Configuration$Direction;
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$data$AppStrategy$State;

        static {
            int[] iArr = new int[AppStrategy.State.values().length];
            $SwitchMap$com$mw$launcher$data$AppStrategy$State = iArr;
            try {
                iArr[AppStrategy.State.DEFAULT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$mw$launcher$data$AppStrategy$State[AppStrategy.State.MOVE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            int[] iArr2 = new int[Configuration.Direction.values().length];
            $SwitchMap$com$mw$launcher$config$Configuration$Direction = iArr2;
            try {
                iArr2[Configuration.Direction.HORIZONTAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$mw$launcher$config$Configuration$Direction[Configuration.Direction.VERTICAL.ordinal()] = 2;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public void registerActivityResultLauncher() {
        if (this.mLauncher == null) {
            this.mLauncher = this.mFragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() { // from class: com.vctv.launcher.app.AppHelper$$ExternalSyntheticLambda0
                @Override // androidx.activity.result.ActivityResultCallback
                public final void onActivityResult(Object obj) {
                    AppHelper.this.m162x856cc7e1((ActivityResult) obj);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$registerActivityResultLauncher$0$com-mw-launcher-app-AppHelper, reason: not valid java name */
    /* synthetic */ void m162x856cc7e1(ActivityResult activityResult) {
        Intent data;
        int intExtra;
        AppManager appManager;
        if (activityResult.getResultCode() == -1 && (data = activityResult.getData()) != null && (intExtra = data.getIntExtra("key", -1)) >= 0 && (appManager = this.mAppManager) != null) {
            AppItem appItem = appManager.getPanelItems().get(intExtra);
            this.mArrayAdapter.onInsert(appItem);
            this.mAppManager.notifyFavAppAdded(appItem);
        }
    }

    public void unregisterActivityResultLauncher() {
        this.mLauncher = null;
    }

    public void releaseOnDestroy() {
        this.mContext.unbindService(this.mServiceConnection);
        this.mHandler.removeCallbacksAndMessages(null);
        LocalBroadcastManager.getInstance(this.mContext).unregisterReceiver(this.mReceiver);
        this.mExecutor.shutdown();
    }

    public void initOnCreate() {
        this.mAppModel = (AppViewModel) new ViewModelProvider(this.mFragment.requireActivity()).get(AppViewModel.class);
        Observer<? super List<AppItem>> observer = new Observer() { // from class: com.vctv.launcher.app.AppHelper$$ExternalSyntheticLambda1
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                AppHelper.this.updateList((List) obj);
            }
        };
        if (this.mStrategy.equals(AppStrategy.DEFAULT)) {
            this.mAppModel.getAppItems().observe(this.mFragment, observer);
        } else {
            this.mAppModel.getFavItems().observe(this.mFragment, observer);
        }
        this.mContext.bindService(new Intent(this.mContext, (Class<?>) AppManager.class), this.mServiceConnection, 1);
        LocalBroadcastManager.getInstance(this.mContext).registerReceiver(this.mReceiver, new IntentFilter(AppManager.ACTION_APP_LIST_INVALIDATED));
    }

    public void doOnResume() {
        if (this.mStrategy.equals(AppStrategy.FAV) && mIsRefreshFavAppList) {
            this.mExecutor.execute(this.loadDataTask);
        }
        EventBus.getDefault().register(this);
    }

    public void doOnPause() {
        EventBus.getDefault().unregister(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPanelActivity() {
        if (this.mArrayAdapter.getmAppItemsList().size() <= this.mFavAppConfig.maxCount) {
            this.mLauncher.launch(new Intent(this.mContext, (Class<?>) FavAppPanelActivity.class));
        } else {
            showCustomToast();
        }
    }

    private void showCustomToast() {
        View viewInflate = LayoutInflater.from(this.mContext).inflate(R.layout.custom_toast, (ViewGroup) null);
        ((CustomTextView) viewInflate.findViewById(R.id.msg)).setText(this.mContext.getString(R.string.fav_app_count_reach_max));
        Toast toast = new Toast(this.mContext);
        toast.setDuration(0);
        toast.setView(viewInflate);
        toast.setGravity(80, 0, (int) this.mContext.getResources().getDimension(R.dimen.toast_y_offset));
        toast.show();
    }

    public void onOpenClicked(AppItem appItem) {
        try {
            if (appItem.getLabel().equals(CommonUtil.NETFLIX_NAME) && Config.get().netflix.isIntegrated()) {
                startNetflixWithIID();
            } else {
                this.mContext.startActivity(appItem.getIntent());
            }
            AppManager appManager = this.mAppManager;
            if (appManager != null) {
                appManager.notifyAppLaunched(appItem);
            }
        } catch (ActivityNotFoundException e) {
            LogUtil.d("Activity Intent NotFound : " + e.getMessage());
            try {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + appItem.getIntent().getComponent().getPackageName()));
                intent.addFlags(268435456);
                intent.setPackage("com.android.vending");
                this.mContext.startActivity(intent);
            } catch (ActivityNotFoundException e2) {
                LogUtil.d("play store NotFound : " + e2.getMessage());
            }
        }
    }

    public void startNetflixWithIID() {
        String str = this.mStrategy == AppStrategy.DEFAULT ? Config.get().netflix.iidApps : Config.get().netflix.iidFavApp;
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.netflix.com/browse?iid=" + str));
        intent.addFlags(32);
        intent.setPackage("com.netflix.ninja");
        LogUtil.d("startNetflixWithIID: " + str);
        this.mContext.startActivity(intent);
    }

    public void onAddFavClicked(AppItem appItem) {
        AppManager appManager = this.mAppManager;
        if (appManager != null) {
            if (appManager.getFavAppCount() < this.mFavAppConfig.maxCount) {
                this.mAppManager.notifyFavAppAdded(appItem);
                mIsRefreshFavAppList = true;
            } else {
                showCustomToast();
            }
        }
    }

    public void onRemFavClicked(AppItem appItem) {
        AppManager appManager = this.mAppManager;
        if (appManager != null) {
            appManager.notifyFavAppRemoved(appItem);
        }
        if (AppStrategy.FAV.equals(this.mStrategy)) {
            this.mArrayAdapter.getmAppItemsList().remove(appItem);
            this.mArrayAdapter.remove(appItem);
        } else {
            mIsRefreshFavAppList = true;
        }
    }

    public void onInfoClicked(AppItem appItem) {
        try {
            this.mContext.startActivity(appItem.getInfoIntent());
        } catch (ActivityNotFoundException e) {
            LogUtil.d("Activity Info Intent NotFound : " + e.getMessage());
        }
    }

    public void onUninstallClicked(AppItem appItem) {
        try {
            this.mContext.startActivity(appItem.getUninstallIntent());
        } catch (ActivityNotFoundException e) {
            LogUtil.d("Activity Uninstall Intent NotFound : " + e.getMessage());
        }
    }

    @Subscribe
    public void cancelMove(CancelMoveEvent cancelMoveEvent) {
        if (AppConfigUtil.getDirection(this.mStrategy).equals(Configuration.Direction.VERTICAL)) {
            this.mVerticalView.cancelMove();
        } else {
            this.mHorizonView.cancelMove();
        }
        this.mPresenter.cancelMove(cancelMoveEvent.getFocusView());
        if (this.mAppManager != null) {
            if (this.mStrategy.equals(AppStrategy.FAV)) {
                this.mAppManager.notifyFavAppMoved(this.mArrayAdapter.getmAppItemsList());
            } else if (this.mAppConfig.orderBy.equals(Configuration.App.OrderBy.USER)) {
                this.mAppManager.notifyAppMoved(this.mArrayAdapter.getmAppItemsList());
            }
        }
        this.mState = AppStrategy.State.DEFAULT;
    }

    @Subscribe
    public void onMove(OnMoveEvent onMoveEvent) {
        int curPos = onMoveEvent.getCurPos();
        int targetPos = onMoveEvent.getTargetPos();
        if (this.mArrayAdapter == null || curPos == targetPos) {
            return;
        }
        int unMovableAppCount = (this.mAppManager == null || !this.mStrategy.equals(AppStrategy.FAV)) ? 0 : this.mAppManager.getUnMovableAppCount();
        int size = AppStrategy.DEFAULT.equals(this.mStrategy) ? this.mArrayAdapter.size() - 1 : this.mArrayAdapter.size() - 2;
        if (AppConfigUtil.inRange(curPos, unMovableAppCount, size) && AppConfigUtil.inRange(targetPos, unMovableAppCount, size)) {
            this.mArrayAdapter.onMove(curPos, targetPos);
        }
    }

    public void onMoveClicked(View view) {
        this.mState = AppStrategy.State.MOVE;
        if (!AppConfigUtil.isTalkBackEnabled(this.mContext)) {
            if (AppConfigUtil.getDirection(this.mStrategy).equals(Configuration.Direction.VERTICAL)) {
                this.mVerticalView.triggerMove();
            } else {
                this.mHorizonView.triggerMove();
            }
        } else {
            this.mPopMenuHelper.showTalkBackPopMenu(new PopMenuDirector(this.mStrategy, this.mArrayAdapter.size(), AppConfigUtil.getDirection(this.mStrategy).equals(Configuration.Direction.HORIZONTAL) ? this.mHorizonView.getChildAdapterPosition(view) : this.mVerticalView.getChildAdapterPosition(view), (this.mAppManager == null || !this.mStrategy.equals(AppStrategy.FAV)) ? 0 : this.mAppManager.getUnMovableAppCount()), view);
        }
        this.mPresenter.triggerMove(view);
    }

    public void onDoneClicked() {
        if (this.mState.equals(AppStrategy.State.MOVE)) {
            this.mPresenter.cancelMove(AppConfigUtil.getDirection(this.mStrategy).equals(Configuration.Direction.VERTICAL) ? this.mVerticalView.getFocusedChild() : this.mHorizonView.getFocusedChild());
            if (this.mAppManager != null) {
                if (this.mStrategy.equals(AppStrategy.DEFAULT)) {
                    this.mAppManager.notifyAppMoved(this.mArrayAdapter.getmAppItemsList());
                } else {
                    this.mAppManager.notifyFavAppMoved(this.mArrayAdapter.getmAppItemsList());
                }
            }
            this.mState = AppStrategy.State.DEFAULT;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateList(List<AppItem> list) {
        AppArrayObjectAdapter appArrayObjectAdapter;
        if (!this.mFragment.isAdded() || (appArrayObjectAdapter = this.mArrayAdapter) == null || list == null) {
            return;
        }
        List<AppItem> list2 = appArrayObjectAdapter.getmAppItemsList();
        list2.clear();
        list2.addAll(list);
        if (AppStrategy.FAV.equals(this.mStrategy)) {
            AppResource appResource = this.mResource;
            list2.add(new AppItem(appResource.getDrawable(appResource.addFavPlus), this.mResource.addFavPlus));
        }
        this.mArrayAdapter.setItems(list2, new DiffCallback<AppItem>() { // from class: com.vctv.launcher.app.AppHelper.5
            @Override // androidx.leanback.widget.DiffCallback
            public boolean areContentsTheSame(AppItem appItem, AppItem appItem2) {
                return appItem.areContentsTheSame(appItem2);
            }

            @Override // androidx.leanback.widget.DiffCallback
            public boolean areItemsTheSame(AppItem appItem, AppItem appItem2) {
                return Objects.equals(appItem, appItem2);
            }
        });
    }

    public void setSelectedPosition(int i) {
        if (AppConfigUtil.getDirection(this.mStrategy).equals(Configuration.Direction.HORIZONTAL)) {
            AppHorizonGridView appHorizonGridView = this.mHorizonView;
            if (appHorizonGridView != null) {
                appHorizonGridView.setSelectedPosition(i);
                return;
            } else {
                this.mPendingSelectedPosition = Integer.valueOf(i);
                return;
            }
        }
        AppVerticalGridView appVerticalGridView = this.mVerticalView;
        if (appVerticalGridView != null) {
            appVerticalGridView.setSelectedPosition(i);
        } else {
            this.mPendingSelectedPosition = Integer.valueOf(i);
        }
    }
}
