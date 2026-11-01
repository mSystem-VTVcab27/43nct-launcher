package com.vctv.launcher.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import androidx.core.content.IntentCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.vctv.launcher.R;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.data.AppItem;
import com.vctv.launcher.util.LogUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
public class AppManager extends Service implements ComponentCallbacks2 {
    public static final String ACTION_APP_LIST_INVALIDATED = "APP_LIST_INVALIDATED";
    public static final String NETFLIX_PKG = "com.netflix.ninja";
    private static final long NETFLIX_PRIORITY = 1;
    private static final String SP_PRESET_APP_LIST = "preset_app_list";
    private static final String TAG = "AppManager";
    private volatile boolean considerOobPriority;
    private boolean isNetflixIntegrated;
    private volatile Map<ComponentName, Date> mAppLastOpenMap;
    private volatile boolean mAppListValid;
    private volatile Map<ComponentName, Long> mAppPriorityMap;
    private AppDbHelper mDbHelper;
    private volatile Map<ComponentName, Long> mFavAppPriorityMap;
    private volatile Map<String, Long> mOobPriority;
    private Configuration.App.OrderBy mOrderBy;
    private final Object mDbHelperLock = new Object();
    private Set<AppItem> mAppItems = new ArraySet();
    private Set<AppItem> mFavAppItems = new ArraySet();
    private List<AppItem> mPanelItems = new ArrayList();
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private List<Configuration.PresetItem> mPresetItems = new ArrayList();
    private final PackageListener mPackageListener = new PackageListener();
    private final IBinder mLocalBinder = new LocalBinder();

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onLowMemory() {
    }

    @Override // android.app.Service, android.content.ComponentCallbacks2
    public void onTrimMemory(int i) {
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        registerReceiver(this.mPackageListener, intentFilter);
        registerComponentCallbacks(this);
        initConfig();
        invalidateAppList();
        LogUtil.d(TAG, "onCreate");
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mPackageListener);
        unregisterComponentCallbacks(this);
        if (this.mExecutor.isShutdown()) {
            return;
        }
        this.mExecutor.shutdown();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mLocalBinder;
    }

    private void initConfig() {
        this.mOrderBy = Config.get().pagination.appConfig.orderBy;
        this.mPresetItems = Config.get().pagination.favApp.favAppList;
    }

    private void ensureDatabase() {
        synchronized (this.mDbHelperLock) {
            if (this.mDbHelper == null) {
                AppDbHelper appDbHelper = new AppDbHelper(getApplicationContext());
                this.mDbHelper = appDbHelper;
                this.mAppLastOpenMap = appDbHelper.readLastOpens();
                this.mAppPriorityMap = this.mDbHelper.readOrderPriorities();
                this.mFavAppPriorityMap = this.mDbHelper.readFavAppList();
                android.content.SharedPreferences spFav = getSharedPreferences("launcher_fav_prefs", android.content.Context.MODE_PRIVATE);
                if (!spFav.getBoolean("fav_cleared_for_user_selection_v3", false)) {
                    if (this.mFavAppPriorityMap != null) {
                        for (android.content.ComponentName cn : new java.util.ArrayList<>(this.mFavAppPriorityMap.keySet())) {
                            this.mDbHelper.deleteFavAppItem(cn);
                        }
                        this.mFavAppPriorityMap.clear();
                    }
                    spFav.edit().putBoolean("fav_cleared_for_user_selection_v3", true).apply();
                }
                String[] stringArray = getResources().getStringArray(R.array.oob_order);
                this.mOobPriority = new ArrayMap(stringArray.length);
                boolean z = false;
                for (int i = 0; i < stringArray.length; i++) {
                    this.mOobPriority.put(stringArray[i], Long.valueOf((((long) stringArray.length) - ((long) i)) + 1));
                }
                if (this.isNetflixIntegrated) {
                    this.mOobPriority.put("com.netflix.ninja", 1L);
                }
                if (this.mAppPriorityMap.isEmpty() && this.mAppLastOpenMap.isEmpty()) {
                    z = true;
                }
                this.considerOobPriority = z;
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:14:0x0073 A[PHI: r1
  0x0073: PHI (r1v12 java.lang.Long) = (r1v7 java.lang.Long), (r1v15 java.lang.Long) binds: [B:18:0x008f, B:12:0x0052] A[DONT_GENERATE, DONT_INLINE]] */
    private long getAppPriority(ResolveInfo resolveInfo) {
        Long lValueOf;
        Long orDefault;
        ensureDatabase();
        ComponentName componentName = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        int i = AnonymousClass1.$SwitchMap$com$mw$launcher$config$Configuration$App$OrderBy[this.mOrderBy.ordinal()];
        if (i == 1) {
            lValueOf = this.mAppPriorityMap.get(componentName);
            Date date = this.mAppLastOpenMap.get(componentName);
            if (date != null) {
                lValueOf = Long.valueOf(date.getTime());
            }
            if (lValueOf == null) {
                orDefault = this.mOobPriority.getOrDefault(resolveInfo.activityInfo.packageName, 0L);
                this.mDbHelper.writeOrderPriority(componentName, orDefault.longValue());
                this.mAppPriorityMap.put(componentName, orDefault);
            } else {
                orDefault = lValueOf;
            }
        } else if (i == 2) {
            lValueOf = this.mAppPriorityMap.get(componentName);
            if (lValueOf == null) {
                orDefault = this.mOobPriority.getOrDefault(resolveInfo.activityInfo.packageName, 0L);
                this.mDbHelper.writeOrderPriority(componentName, orDefault.longValue());
                this.mAppPriorityMap.put(componentName, orDefault);
            } else {
                orDefault = lValueOf;
            }
        } else {
            orDefault = this.considerOobPriority ? this.mOobPriority.getOrDefault(resolveInfo.activityInfo.packageName, 0L) : 0L;
            this.mDbHelper.writeOrderPriority(componentName, orDefault.longValue());
            this.mAppPriorityMap.put(componentName, orDefault);
        }
        return orDefault.longValue();
    }

    /* JADX INFO: renamed from: com.vctv.launcher.app.AppManager$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$config$Configuration$App$OrderBy;

        static {
            int[] iArr = new int[Configuration.App.OrderBy.values().length];
            $SwitchMap$com$mw$launcher$config$Configuration$App$OrderBy = iArr;
            try {
                iArr[Configuration.App.OrderBy.RECENT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$mw$launcher$config$Configuration$App$OrderBy[Configuration.App.OrderBy.USER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$mw$launcher$config$Configuration$App$OrderBy[Configuration.App.OrderBy.ALPHABET.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    private long getFavAppPriority(ResolveInfo resolveInfo) {
        ComponentName key;
        ensureDatabase();
        ComponentName componentName = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        Long lValueOf = this.mFavAppPriorityMap.get(componentName);
        if (lValueOf == null) {
            Iterator<Map.Entry<ComponentName, Long>> it = this.mFavAppPriorityMap.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    lValueOf = null;
                    key = null;
                    break;
                }
                Map.Entry<ComponentName, Long> next = it.next();
                key = next.getKey();
                if (key.getPackageName().equals(resolveInfo.activityInfo.packageName) && !key.equals(componentName) && !key.getClassName().equals(key.getPackageName())) {
                    lValueOf = next.getValue();
                    Log.d(TAG, "-getFavAppPriority- Found old specific activity priority: " + key.flattenToShortString());
                    break;
                }
            }
            if (lValueOf != null) {
                updateFavAppPriority(componentName, key, lValueOf);
                Log.d(TAG, "-getFavAppPriority- Migrated priority from " + key.flattenToShortString() + " to " + componentName.flattenToShortString());
            } else {
                ComponentName componentName2 = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.packageName);
                Long l = this.mFavAppPriorityMap.get(componentName2);
                if (l != null) {
                    updateFavAppPriority(componentName, componentName2, l);
                    Log.d(TAG, "-getFavAppPriority- Migrated emptyPresetPriority from " + componentName2.flattenToShortString() + " to " + componentName.flattenToShortString());
                    lValueOf = l;
                } else {
                    Log.d(TAG, "-getFavAppPriority- No existing priority found for " + componentName.flattenToShortString() + ". Creating new.");
                    lValueOf = Long.valueOf(new Date().getTime());
                    updateFavAppPriority(componentName, null, lValueOf);
                }
            }
        }
        return lValueOf.longValue();
    }

    private void updateFavAppPriority(ComponentName componentName, ComponentName componentName2, Long l) {
        if (componentName != null) {
            this.mFavAppPriorityMap.put(componentName, l);
            this.mDbHelper.writeFavAppOrderPriority(componentName, l.longValue());
            if (componentName2 != null) {
                this.mFavAppPriorityMap.remove(componentName2);
                this.mDbHelper.deleteFavAppItem(componentName2);
            }
        }
    }

    private List<ResolveInfo> queryAllLauncherActivities() {
        Intent leanbackIntent = new Intent("android.intent.action.MAIN").addCategory(IntentCompat.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(leanbackIntent, 0);

        Intent standardIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> standardList = getPackageManager().queryIntentActivities(standardIntent, 0);

        Set<String> packageSet = new HashSet<>();
        List<ResolveInfo> combinedList = new ArrayList<>();
        if (list != null) {
            for (ResolveInfo info : list) {
                if (info.activityInfo != null && !getPackageName().equals(info.activityInfo.packageName)) {
                    packageSet.add(info.activityInfo.packageName);
                    combinedList.add(info);
                }
            }
        }
        if (standardList != null) {
            for (ResolveInfo info : standardList) {
                if (info.activityInfo != null && !getPackageName().equals(info.activityInfo.packageName)) {
                    if (!packageSet.contains(info.activityInfo.packageName)) {
                        packageSet.add(info.activityInfo.packageName);
                        combinedList.add(info);
                    }
                }
            }
        }
        return combinedList;
    }

    private void updateAppList() {
        List<ResolveInfo> listQueryIntentActivities = queryAllLauncherActivities();
        ArraySet arraySet = new ArraySet(listQueryIntentActivities.size());
        for (ResolveInfo resolveInfo : listQueryIntentActivities) {
            if (!resolveInfo.activityInfo.packageName.equals("com.google.android.videos")) {
                arraySet.add(new AppItem(this, resolveInfo, getAppPriority(resolveInfo)));
            }
        }
        this.mAppItems = arraySet;
        updateAppDB();
        if (!this.mOrderBy.equals(Configuration.App.OrderBy.ALPHABET)) {
            updateAppPriority();
        }
        this.mAppListValid = true;
    }

    private void updateAppPriority() {
        ArrayList arrayList = new ArrayList(this.mAppItems);
        arrayList.sort(Comparator.reverseOrder());
        int i = 0;
        while (i < arrayList.size()) {
            ComponentName component = ((AppItem) arrayList.get(i)).getIntent().getComponent();
            i++;
            long j = i;
            this.mDbHelper.writeOrderPriority(component, j);
            this.mAppPriorityMap.put(component, Long.valueOf(j));
        }
    }

    private void updateAppDB() {
        ArrayList arrayList = new ArrayList();
        Iterator<AppItem> it = this.mAppItems.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().getIntent().getComponent());
        }
        Iterator<Map.Entry<ComponentName, Long>> it2 = this.mAppPriorityMap.entrySet().iterator();
        while (it2.hasNext()) {
            ComponentName key = it2.next().getKey();
            if (!arrayList.contains(key)) {
                it2.remove();
                notifyAppRemoved(key);
            }
        }
        Iterator<Map.Entry<ComponentName, Date>> it3 = this.mAppLastOpenMap.entrySet().iterator();
        while (it3.hasNext()) {
            ComponentName key2 = it3.next().getKey();
            if (!arrayList.contains(key2)) {
                it3.remove();
                notifyAppRemoved(key2);
            }
        }
    }

    private void updateFavAppDB() {
        ArrayList arrayList = new ArrayList();
        Iterator<AppItem> it = this.mFavAppItems.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().getIntent().getComponent());
        }
        Iterator<Map.Entry<ComponentName, Long>> it2 = this.mFavAppPriorityMap.entrySet().iterator();
        while (it2.hasNext()) {
            ComponentName key = it2.next().getKey();
            if (!arrayList.contains(key)) {
                it2.remove();
                notifyFavAppRemoved(key);
            }
        }
    }

    public Set<AppItem> updateFavAppList() {
        AppItem appItemCreateAppItem;
        ensureDatabase();
        ArraySet arraySet = new ArraySet();
        HashSet hashSet = new HashSet();
        List<ResolveInfo> listQueryIntentActivities = queryAllLauncherActivities();
        HashMap map = new HashMap();
        HashMap map2 = new HashMap();
        for (ResolveInfo resolveInfo : listQueryIntentActivities) {
            map.put(resolveInfo.activityInfo.packageName, resolveInfo);
            map2.put(new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name), resolveInfo);
        }
        HashMap map3 = new HashMap();
        for (Configuration.PresetItem presetItem : this.mPresetItems) {
            map3.put(presetItem.pkg, presetItem);
        }
        if (!isFirstBoot()) {
            synchronized (this.mFavAppPriorityMap) {
                Iterator<Map.Entry<ComponentName, Long>> it = this.mFavAppPriorityMap.entrySet().iterator();
                while (it.hasNext()) {
                    ComponentName key = it.next().getKey();
                    String packageName = key.getPackageName();
                    if (!hashSet.contains(packageName)) {
                        ResolveInfo resolveInfo2 = (ResolveInfo) map2.get(key);
                        if (resolveInfo2 == null) {
                            resolveInfo2 = (ResolveInfo) map.get(packageName);
                        }
                        ResolveInfo resolveInfo3 = resolveInfo2;
                        Configuration.PresetItem presetItem2 = (Configuration.PresetItem) map3.get(packageName);
                        if (presetItem2 == null) {
                            presetItem2 = new Configuration.PresetItem(packageName, "", "", true, true);
                        }
                        AppItem appItemCreateAppItem2 = createAppItem(resolveInfo3, presetItem2, key.getClassName());
                        if (appItemCreateAppItem2 != null) {
                            arraySet.add(appItemCreateAppItem2);
                            hashSet.add(packageName);
                        }
                        map3 = map3;
                    }
                }
            }
        }
        for (Configuration.PresetItem presetItem3 : this.mPresetItems) {
            if (!hashSet.contains(presetItem3.pkg) && (appItemCreateAppItem = createAppItem((ResolveInfo) map.get(presetItem3.pkg), presetItem3, "")) != null) {
                arraySet.add(appItemCreateAppItem);
                hashSet.add(presetItem3.pkg);
            }
        }

        this.mFavAppItems = arraySet;
        updateFavAppDB();
        return this.mFavAppItems;
    }

    private AppItem createAppItem(ResolveInfo resolveInfo, Configuration.PresetItem presetItem, String str) {
        if (presetItem == null) {
            return null;
        }
        if (resolveInfo == null) {
            Log.d(TAG, "-createAppItem-" + presetItem.pkg + "-not-installed");
            resolveInfo = new ResolveInfo();
            resolveInfo.activityInfo = new ActivityInfo();
            resolveInfo.activityInfo.packageName = presetItem.pkg;
            if (!TextUtils.isEmpty(str)) {
                resolveInfo.activityInfo.name = str;
            } else {
                resolveInfo.activityInfo.name = presetItem.pkg;
            }
        }
        ResolveInfo resolveInfo2 = resolveInfo;
        return new AppItem(this, resolveInfo2, presetItem.name, presetItem.bannerPath, getFavAppPriority(resolveInfo2), presetItem.movable, presetItem.removable);
    }

    public void setPanelItems(List<AppItem> list) {
        if (list != null) {
            this.mPanelItems = list;
        }
    }

    public List<AppItem> getPanelItems() {
        return this.mPanelItems;
    }

    public boolean isFav(AppItem appItem) {
        return this.mFavAppPriorityMap.get(appItem.getIntent().getComponent()) != null;
    }

    public boolean isRemovable(AppItem appItem) {
        ComponentName component = appItem.getIntent().getComponent();
        for (Configuration.PresetItem presetItem : this.mPresetItems) {
            if (presetItem.pkg.equals(component.getPackageName())) {
                return presetItem.removable;
            }
        }
        return true;
    }

    public int getFavAppCount() {
        return this.mFavAppPriorityMap.size();
    }

    private boolean isFirstBoot() {
        SharedPreferences sharedPreferences = getSharedPreferences(SP_PRESET_APP_LIST, 0);
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        boolean z = sharedPreferences.getBoolean("isFirstBoot", true);
        LogUtil.d("isFirstBoot : " + z);
        if (z) {
            editorEdit.putBoolean("isFirstBoot", false);
            editorEdit.apply();
        }
        return z;
    }

    public int getUnMovableAppCount() {
        Iterator<AppItem> it = this.mFavAppItems.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (!it.next().isMovable()) {
                i++;
            }
        }
        return i;
    }

    public void invalidateAppList() {
        this.mAppListValid = false;
        this.considerOobPriority = false;
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_APP_LIST_INVALIDATED));
    }

    public synchronized Set<AppItem> getAppItems() {
        if (!this.mAppListValid) {
            updateAppList();
        }
        return this.mAppItems;
    }

    public synchronized Set<AppItem> getFavAppItems() {
        return updateFavAppList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateListsForPackage(String str) {
        boolean z;
        Iterator<AppItem> it = this.mAppItems.iterator();
        while (true) {
            if (!it.hasNext()) {
                z = false;
                break;
            } else if (TextUtils.equals(((ComponentName) Objects.requireNonNull(it.next().getIntent().getComponent())).getPackageName(), str)) {
                z = true;
                break;
            }
        }
        if (getPackageManager().queryIntentActivities(new Intent("android.intent.action.MAIN").setPackage(str), 0).isEmpty() ? z : true) {
            invalidateAppList();
        }
    }

    public void notifyAppLaunched(AppItem appItem) {
        final Date date = new Date();
        final ComponentName component = appItem.getIntent().getComponent();
        this.mAppLastOpenMap.put(component, date);
        this.mExecutor.execute(new Runnable() { // from class: com.vctv.launcher.app.AppManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AppManager.this.m164lambda$notifyAppLaunched$0$commwlauncherappAppManager(component, date);
            }
        });
        if (Configuration.App.OrderBy.RECENT.equals(this.mOrderBy)) {
            invalidateAppList();
        }
    }

    /* JADX INFO: renamed from: lambda$notifyAppLaunched$0$com-mw-launcher-app-AppManager, reason: not valid java name */
    /* synthetic */ void m164lambda$notifyAppLaunched$0$commwlauncherappAppManager(ComponentName componentName, Date date) {
        ensureDatabase();
        if (componentName != null) {
            this.mDbHelper.writeLastOpen(componentName, date);
        }
    }

    public void notifyFavAppAdded(AppItem appItem) {
        final long time = new Date().getTime();
        final ComponentName component = appItem.getIntent().getComponent();
        this.mFavAppPriorityMap.put(component, Long.valueOf(time));
        this.mExecutor.execute(new Runnable() { // from class: com.vctv.launcher.app.AppManager$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                AppManager.this.m167lambda$notifyFavAppAdded$1$commwlauncherappAppManager(component, time);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$notifyFavAppAdded$1$com-mw-launcher-app-AppManager, reason: not valid java name */
    /* synthetic */ void m167lambda$notifyFavAppAdded$1$commwlauncherappAppManager(ComponentName componentName, long j) {
        ensureDatabase();
        if (componentName != null) {
            this.mDbHelper.writeFavAppOrderPriority(componentName, j);
        }
    }

    public void notifyFavAppRemoved(AppItem appItem) {
        notifyFavAppRemoved(appItem.getIntent().getComponent());
    }

    public void notifyFavAppRemoved(final ComponentName componentName) {
        if (componentName == null || this.mFavAppPriorityMap.get(componentName) == null) {
            this.mFavAppPriorityMap.remove(componentName);
            this.mExecutor.execute(new Runnable() { // from class: com.vctv.launcher.app.AppManager$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    AppManager.this.m169lambda$notifyFavAppRemoved$2$commwlauncherappAppManager(componentName);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$notifyFavAppRemoved$2$com-mw-launcher-app-AppManager, reason: not valid java name */
    /* synthetic */ void m169lambda$notifyFavAppRemoved$2$commwlauncherappAppManager(ComponentName componentName) {
        ensureDatabase();
        if (componentName != null) {
            this.mDbHelper.deleteFavAppItem(componentName);
        }
    }

    public void notifyAppRemoved(final ComponentName componentName) {
        this.mAppPriorityMap.remove(componentName);
        this.mAppLastOpenMap.remove(componentName);
        this.mExecutor.execute(new Runnable() { // from class: com.vctv.launcher.app.AppManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AppManager.this.m166lambda$notifyAppRemoved$3$commwlauncherappAppManager(componentName);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$notifyAppRemoved$3$com-mw-launcher-app-AppManager, reason: not valid java name */
    /* synthetic */ void m166lambda$notifyAppRemoved$3$commwlauncherappAppManager(ComponentName componentName) {
        ensureDatabase();
        if (componentName != null) {
            this.mDbHelper.deleteAppItem(componentName);
        }
    }

    public void notifyFavAppMoved(final List<AppItem> list) {
        if (list.size() < 2) {
            return;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            this.mFavAppPriorityMap.put(list.get(i).getIntent().getComponent(), Long.valueOf(i));
        }
        this.mExecutor.execute(new Runnable() { // from class: com.vctv.launcher.app.AppManager$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AppManager.this.m168lambda$notifyFavAppMoved$4$commwlauncherappAppManager(list);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$notifyFavAppMoved$4$com-mw-launcher-app-AppManager, reason: not valid java name */
    /* synthetic */ void m168lambda$notifyFavAppMoved$4$commwlauncherappAppManager(List list) {
        ensureDatabase();
        for (int i = 0; i < list.size() - 1; i++) {
            ComponentName component = ((AppItem) list.get(i)).getIntent().getComponent();
            long jLongValue = this.mFavAppPriorityMap.get(component).longValue();
            if (component != null) {
                this.mDbHelper.writeFavAppOrderPriority(component, jLongValue);
            }
        }
    }

    public void notifyAppMoved(final List<AppItem> list) {
        if (list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            this.mAppPriorityMap.put(list.get(i).getIntent().getComponent(), Long.valueOf(list.size() - i));
        }
        this.mExecutor.execute(new Runnable() { // from class: com.vctv.launcher.app.AppManager$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                AppManager.this.m165lambda$notifyAppMoved$5$commwlauncherappAppManager(list);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$notifyAppMoved$5$com-mw-launcher-app-AppManager, reason: not valid java name */
    /* synthetic */ void m165lambda$notifyAppMoved$5$commwlauncherappAppManager(List list) {
        ensureDatabase();
        for (int i = 0; i < list.size(); i++) {
            ComponentName component = ((AppItem) list.get(i)).getIntent().getComponent();
            long jLongValue = this.mAppPriorityMap.get(component).longValue();
            if (component != null) {
                this.mDbHelper.writeOrderPriority(component, jLongValue);
            }
        }
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onConfigurationChanged(android.content.res.Configuration configuration) {
        invalidateAppList();
    }

    @Override // android.app.Service
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("App Items");
        Set<AppItem> set = this.mAppItems;
        if (set != null) {
            for (AppItem appItem : (AppItem[]) set.toArray(new AppItem[set.size()])) {
                printWriter.println(appItem.toDebugString());
            }
            return;
        }
        printWriter.println("Null");
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public AppManager getAppManager() {
            return AppManager.this;
        }
    }

    public class PackageListener extends BroadcastReceiver {
        public PackageListener() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            AppManager.this.invalidateListsForPackage(Uri.parse(intent.getDataString()).getSchemeSpecificPart());
        }
    }
}
