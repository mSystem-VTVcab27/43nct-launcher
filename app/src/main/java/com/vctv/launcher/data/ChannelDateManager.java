package com.vctv.launcher.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import com.vctv.launcher.config.Config;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class ChannelDateManager {
    private static final long DEBOUNCE_DELAY_MS = 3000;
    private static ChannelDateManager instance;
    private final Handler backgroundHandler;
    private final HandlerThread backgroundThread;
    ContentResolver contentResolver;
    private List<DbChannelData> dbChannelDataList;
    private DisplayChannelListener displayChannelListener;
    LauncherDatabaseHelper launcherDatabaseHelper;
    private TIFDataObserver tifDataObserver;
    TifDatabaseReader tifDatabaseReader;
    private final String TAG = "ChannelDateManager";
    private final Object lock = new Object();
    private volatile boolean initialLoadFinished = false;

    
    public void reloadChannels() {
        if (this.backgroundHandler != null) {
            this.backgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    ChannelDateManager.this.tifAllChannelDataList.clear();
                    ChannelDateManager.this.tifAllChannelDataList.addAll(ChannelDateManager.this.tifDatabaseReader.readChannels());
                    ChannelDateManager.this.dbChannelDataList = ChannelDateManager.this.launcherDatabaseHelper.getAllChannels();
                    ChannelDateManager.this.loadDisplayChannel();
                }
            });
        }
    }

    public boolean isInitialLoadFinished() {
        return this.initialLoadFinished;
    }

    private final Runnable debouncedReadRunnable = new AnonymousClass1();
    List<ChannelData> tifAllChannelDataList = new ArrayList();
    List<ChannelData> displayChannelDataList = new ArrayList();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface DisplayChannelListener {
        void completeChannelDataLoad();

        void notifyDisplayChannelChange(boolean z, ChannelData channelData, boolean z2);

        void notifyMoveChannel(int i, int i2);

        void notifyUpdateChannel(ChannelData channelData);
    }

    /* JADX INFO: renamed from: com.vctv.launcher.data.ChannelDateManager$1, reason: invalid class name */
    class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override // java.lang.Runnable
        public void run() {
            List<ChannelData> channels = ChannelDateManager.this.tifDatabaseReader.readChannels();
            HashMap<Uri, ChannelData> map = new HashMap<>();
            for (ChannelData channelData : channels) {
                map.put(channelData.getChannelUri(), channelData);
            }
            final ArrayList arrayList = new ArrayList();
            final ArrayList arrayList2 = new ArrayList();
            final ArrayList arrayList3 = new ArrayList();
            synchronized (ChannelDateManager.this.lock) {
                for (ChannelData channelData2 : ChannelDateManager.this.tifAllChannelDataList) {
                    Uri channelUri = channelData2.getChannelUri();
                    if (map.containsKey(channelUri)) {
                        ChannelData channelData3 = (ChannelData) map.get(channelUri);
                        if (!channelData2.equals(channelData3)) {
                            channelData3.setPosition(channelData2.getPosition());
                            channelData3.setShow(channelData2.isShow());
                            channelData3.setChannelVersion(channelData2.getChannelVersion());
                            ChannelDateManager.this.launcherDatabaseHelper.updateChannel(channelData3);
                            arrayList.add(channelData3);
                        }
                    } else {
                        ChannelDateManager.this.launcherDatabaseHelper.deleteChannel(channelData2.getChannelId());
                        arrayList2.add(channelData2);
                    }
                    map.remove(channelUri);
                }
                for (ChannelData channelData4 : map.values()) {
                    channelData4.setPosition(-1);
                    channelData4.setShow(false);
                    ChannelDateManager.this.insertChannelToDB(channelData4);
                }
            }
            if (arrayList.isEmpty() && arrayList2.isEmpty() && arrayList3.isEmpty()) {
                return;
            }
            ChannelDateManager.this.mainHandler.post(new Runnable() { // from class: com.vctv.launcher.data.ChannelDateManager$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AnonymousClass1.this.m184lambda$run$0$commwlauncherdataChannelDateManager$1(arrayList, arrayList2, arrayList3);
                }
            });
        }

        /* JADX INFO: renamed from: lambda$run$0$com-mw-launcher-data-ChannelDateManager$1, reason: not valid java name */
        /* synthetic */ void m184lambda$run$0$commwlauncherdataChannelDateManager$1(List list, List list2, List list3) {
            ChannelDateManager.this.updateUiWithChanges(list, list2, list3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUiWithChanges(List<ChannelData> list, List<ChannelData> list2, List<ChannelData> list3) {
        synchronized (this.lock) {
            for (ChannelData channelData : list2) {
                if (this.tifAllChannelDataList.remove(channelData)) {
                    Log.i("ChannelDateManager", "delete channel: " + channelData.getDisplayName());
                    this.displayChannelDataList.remove(channelData);
                    DisplayChannelListener displayChannelListener = this.displayChannelListener;
                    if (displayChannelListener != null) {
                        displayChannelListener.notifyDisplayChannelChange(false, channelData, false);
                    }
                }
            }
            for (ChannelData channelData2 : list) {
                for (ChannelData channelData3 : this.tifAllChannelDataList) {
                    if (channelData3.getChannelUri().equals(channelData2.getChannelUri())) {
                        Log.i("ChannelDateManager", "update channel: " + channelData3.getDisplayName());
                        channelData3.setDisplayName(channelData2.getDisplayName());
                        channelData3.setDisplayNumber(channelData2.getDisplayNumber());
                        channelData3.setDescription(channelData2.getDescription());
                        channelData3.setAppLinkIntentUri(channelData2.getAppLinkIntentUri());
                        channelData3.setType(channelData2.getType());
                        channelData3.setPreviewProgram(channelData2.getPreviewProgram());
                        DisplayChannelListener displayChannelListener2 = this.displayChannelListener;
                        if (displayChannelListener2 == null) {
                            break;
                        }
                        displayChannelListener2.notifyUpdateChannel(channelData3);
                        break;
                    }
                }
            }
            for (ChannelData channelData4 : list3) {
                Log.i("ChannelDateManager", "add channel: " + channelData4.getDisplayName());
                this.tifAllChannelDataList.add(channelData4);
                if (channelData4.isShow()) {
                    this.displayChannelDataList.add(channelData4);
                    DisplayChannelListener displayChannelListener3 = this.displayChannelListener;
                    if (displayChannelListener3 != null) {
                        displayChannelListener3.notifyDisplayChannelChange(true, channelData4, false);
                    }
                }
            }
        }
    }

    private ChannelDateManager(Context context) {
        this.tifDatabaseReader = TifDatabaseReader.getInstance(context);
        this.launcherDatabaseHelper = LauncherDatabaseHelper.getInstance(context);
        this.contentResolver = context.getContentResolver();
        HandlerThread handlerThread = new HandlerThread("ChannelDataManagerWorker");
        this.backgroundThread = handlerThread;
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        this.backgroundHandler = handler;
        this.tifDataObserver = new TIFDataObserver(handler);
        registerTIFDataObserver();
        handler.post(new Runnable() { // from class: com.vctv.launcher.data.ChannelDateManager$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ChannelDateManager.this.m181lambda$new$0$commwlauncherdataChannelDateManager();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$new$0$com-mw-launcher-data-ChannelDateManager, reason: not valid java name */
    /* synthetic */ void m181lambda$new$0$commwlauncherdataChannelDateManager() {
        this.tifAllChannelDataList.addAll(this.tifDatabaseReader.readChannels());
        this.dbChannelDataList = this.launcherDatabaseHelper.getAllChannels();
        loadDisplayChannel();
    }

    public static synchronized ChannelDateManager getInstance(Context context) {
        if (instance == null) {
            instance = new ChannelDateManager(context.getApplicationContext());
        }
        return instance;
    }

    public void destroy() {
        unregisterTIFDataObserver();
        this.backgroundHandler.removeCallbacksAndMessages(null);
        this.backgroundThread.quitSafely();
        instance = null;
    }

    private void loadDisplayChannel() {
        this.displayChannelDataList.clear();
        updateDbChannelAccordingTif();
        Map<Long, DbChannelData> mapConvertDbChannelDataListToMap = convertDbChannelDataListToMap();
        for (ChannelData channelData : this.tifAllChannelDataList) {
            DbChannelData dbChannelData = mapConvertDbChannelDataListToMap.get(Long.valueOf(channelData.getChannelId()));
            if (dbChannelData != null) {
                if (dbChannelData.isShow()) {
                    channelData.setShow(true);
                    channelData.setPosition(dbChannelData.getPosition());
                    this.displayChannelDataList.add(channelData);
                } else {
                    Log.i("ChannelDateManager", channelData.getDisplayName() + " not show");
                }
            } else {
                channelData.setShow(false);
                channelData.setPosition(-1);
            }
        }
        this.mainHandler.post(new Runnable() { // from class: com.vctv.launcher.data.ChannelDateManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ChannelDateManager.this.m180x8893fad6();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$loadDisplayChannel$1$com-mw-launcher-data-ChannelDateManager, reason: not valid java name */
    /* synthetic */ void m180x8893fad6() {
        this.initialLoadFinished = true;
        DisplayChannelListener displayChannelListener = this.displayChannelListener;
        if (displayChannelListener != null) {
            displayChannelListener.completeChannelDataLoad();
        }
    }

    private void updateDbChannelAccordingTif() {
        if (this.tifAllChannelDataList.isEmpty()) {
            this.launcherDatabaseHelper.deleteAllChannels();
            return;
        }
        Map<Long, DbChannelData> mapConvertDbChannelDataListToMap = convertDbChannelDataListToMap();
        for (ChannelData channelData : this.tifAllChannelDataList) {
            DbChannelData dbChannelData = mapConvertDbChannelDataListToMap.get(Long.valueOf(channelData.getChannelId()));
            if (dbChannelData != null) {
                if (!channelData.getPackageName().equals(dbChannelData.getPackageName()) || !channelData.getDisplayName().equals(dbChannelData.getDisplayName())) {
                    if (channelData.isPreset()) {
                        channelData.setPosition(0);
                        channelData.setShow(true);
                    } else {
                        channelData.setPosition(-1);
                        channelData.setShow(false);
                    }
                    this.launcherDatabaseHelper.updateChannel(channelData);
                } else if (dbChannelData.isPreset() && !channelData.isPreset()) {
                    Log.i("ChannelDateManager", channelData.getDisplayName() + " from preset to not preset");
                    channelData.setPosition(-1);
                    channelData.setShow(false);
                    this.launcherDatabaseHelper.updateChannel(channelData);
                } else if (!dbChannelData.isPreset() && channelData.isPreset()) {
                    Log.i("ChannelDateManager", channelData.getDisplayName() + " from not preset to preset");
                    channelData.setPosition(0);
                    channelData.setShow(true);
                    this.launcherDatabaseHelper.updateChannel(channelData);
                }
            } else {
                DbChannelData dbChannelData2 = new DbChannelData();
                dbChannelData2.setChannelId(channelData.getChannelId());
                dbChannelData2.setPackageName(channelData.getPackageName());
                dbChannelData2.setDisplayName(channelData.getDisplayName());
                dbChannelData2.setPosition(-1);
                dbChannelData2.setShow(false);
                dbChannelData2.setChannelVersion(Config.get().version);
                dbChannelData2.setMovable(true);
                this.launcherDatabaseHelper.insertChannel(dbChannelData2);
            }
        }
        HashSet hashSet = new HashSet();
        Iterator<ChannelData> it = this.tifAllChannelDataList.iterator();
        while (it.hasNext()) {
            hashSet.add(Long.valueOf(it.next().getChannelId()));
        }
        for (DbChannelData dbChannelData3 : this.dbChannelDataList) {
            if (!hashSet.contains(Long.valueOf(dbChannelData3.getChannelId()))) {
                this.launcherDatabaseHelper.deleteChannel(dbChannelData3.getChannelId());
            }
        }
        this.dbChannelDataList = this.launcherDatabaseHelper.getAllChannels();
    }

    private Map<Long, DbChannelData> convertDbChannelDataListToMap() {
        HashMap<Long, DbChannelData> map = new HashMap<>();
        for (DbChannelData dbChannelData : this.dbChannelDataList) {
            map.put(Long.valueOf(dbChannelData.getChannelId()), dbChannelData);
        }
        return map;
    }

    public void sortDisplayChannel() {
        Collections.sort(this.displayChannelDataList, new Comparator<ChannelData>() { // from class: com.vctv.launcher.data.ChannelDateManager.2
            @Override // java.util.Comparator
            public int compare(ChannelData channelData, ChannelData channelData2) {
                boolean zIsMovable;
                int iCompare = Integer.compare(channelData.getPosition(), channelData2.getPosition());
                if (iCompare != 0) {
                    return iCompare;
                }
                boolean zIsPreset = channelData.isPreset();
                if (zIsPreset != channelData2.isPreset()) {
                    return zIsPreset ? -1 : 1;
                }
                if (zIsPreset && (zIsMovable = channelData.isMovable()) != channelData2.isMovable()) {
                    return zIsMovable ? 1 : -1;
                }
                return 0;
            }
        });
    }

    public void calibrateDisplayChannelPosition(int i) {
        if (i > this.displayChannelDataList.size() - 1) {
            Log.i("ChannelDateManager", "position err");
        }
        while (i < this.displayChannelDataList.size()) {
            this.displayChannelDataList.get(i).setPosition(i);
            Log.i("ChannelDateManager", "calibrate Position: " + this.displayChannelDataList.get(i));
            i++;
        }
    }

    public List<ChannelData> getDisplayChannelDataListNeedSort() {
        List<ChannelData> list;
        synchronized (this.lock) {
            if (!this.displayChannelDataList.isEmpty()) {
                sortDisplayChannel();
                calibrateDisplayChannelPosition(0);
            } else {
                Log.i("ChannelDateManager", "All channel isn't show");
            }
            list = this.displayChannelDataList;
        }
        return list;
    }

    public List<ChannelData> getDisplayChannelDataListNoSort() {
        return this.displayChannelDataList;
    }

    public List<ChannelData> getTifAllChannelDataList() {
        return this.tifAllChannelDataList;
    }

    public void handleDisplayChannel(ChannelData channelData, boolean z) {
        synchronized (this.lock) {
            channelData.setShow(z);
            if (z) {
                this.displayChannelDataList.add(channelData);
                channelData.setPosition(this.displayChannelDataList.size() - 1);
                Log.i("ChannelDateManager", "add channel " + channelData);
            } else {
                int position = channelData.getPosition();
                this.displayChannelDataList.remove(channelData);
                calibrateDisplayChannelPosition(position);
                channelData.setPosition(-1);
                Log.i("ChannelDateManager", "remove channel: " + channelData);
            }
            DisplayChannelListener displayChannelListener = this.displayChannelListener;
            if (displayChannelListener != null) {
                displayChannelListener.notifyDisplayChannelChange(z, channelData, true);
            }
        }
    }

    public void handleMoveChannel(int i, int i2) {
        synchronized (this.lock) {
            ChannelData channelData = this.displayChannelDataList.get(i);
            ChannelData channelData2 = this.displayChannelDataList.get(i2);
            channelData.setPosition(i2);
            channelData2.setPosition(i);
            Collections.swap(this.displayChannelDataList, i, i2);
            Log.i("ChannelDateManager", channelData.getDisplayName() + " from " + i + " to " + i2);
            Log.i("ChannelDateManager", channelData2.getDisplayName() + " from " + i2 + " to " + i);
            DisplayChannelListener displayChannelListener = this.displayChannelListener;
            if (displayChannelListener != null) {
                displayChannelListener.notifyMoveChannel(i + 1, i2 + 1);
            }
        }
    }

    public void setDisplayChannelListener(final DisplayChannelListener displayChannelListener) {
        this.displayChannelListener = displayChannelListener;
        if (displayChannelListener == null || !this.initialLoadFinished) {
            return;
        }
        Handler handler = this.mainHandler;
        Objects.requireNonNull(displayChannelListener);
        handler.post(new Runnable() { // from class: com.vctv.launcher.data.ChannelDateManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                displayChannelListener.completeChannelDataLoad();
            }
        });
    }

    public void updateAllDbChannelData() {
        this.backgroundHandler.post(new Runnable() { // from class: com.vctv.launcher.data.ChannelDateManager$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ChannelDateManager.this.m182x43452da7();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$updateAllDbChannelData$2$com-mw-launcher-data-ChannelDateManager, reason: not valid java name */
    /* synthetic */ void m182x43452da7() {
        synchronized (this.lock) {
            for (int i = 0; i < this.tifAllChannelDataList.size(); i++) {
                this.launcherDatabaseHelper.updateChannel(this.tifAllChannelDataList.get(i));
                Log.i("ChannelDateManager", "upDbChannelData" + this.tifAllChannelDataList.get(i));
            }
        }
    }

    public void updateDisplayDbChannelData() {
        this.backgroundHandler.post(new Runnable() { // from class: com.vctv.launcher.data.ChannelDateManager$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ChannelDateManager.this.m183x2dfaa567();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$updateDisplayDbChannelData$3$com-mw-launcher-data-ChannelDateManager, reason: not valid java name */
    /* synthetic */ void m183x2dfaa567() {
        synchronized (this.lock) {
            for (int i = 0; i < this.displayChannelDataList.size(); i++) {
                this.launcherDatabaseHelper.updateChannel(this.displayChannelDataList.get(i));
                Log.i("ChannelDateManager", "updateDisPlayDbChannelData" + this.displayChannelDataList.get(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void insertChannelToDB(ChannelData channelData) {
        DbChannelData dbChannelData = new DbChannelData();
        dbChannelData.setChannelId(channelData.getChannelId());
        dbChannelData.setPackageName(channelData.getPackageName());
        dbChannelData.setDisplayName(channelData.getDisplayName());
        dbChannelData.setPosition(-1);
        dbChannelData.setShow(false);
        dbChannelData.setChannelVersion(Config.get().version);
        dbChannelData.setMovable(channelData.isMovable());
        this.launcherDatabaseHelper.insertChannel(dbChannelData);
    }

    private void registerTIFDataObserver() {
        if (this.tifDataObserver != null) {
            try {
                this.contentResolver.registerContentObserver(TvContract.PreviewPrograms.CONTENT_URI, true, this.tifDataObserver);
                this.contentResolver.registerContentObserver(TvContract.Channels.CONTENT_URI, true, this.tifDataObserver);
                Log.d("ChannelDateManager", "Registered ContentObserver for TIF.");
            } catch (SecurityException e) {
                Log.e("ChannelDateManager", "Permission denied to register ContentObserver for TIF: " + e.getMessage());
            }
        }
    }

    public void unregisterTIFDataObserver() {
        TIFDataObserver tIFDataObserver = this.tifDataObserver;
        if (tIFDataObserver != null) {
            this.contentResolver.unregisterContentObserver(tIFDataObserver);
            this.tifDataObserver = null;
        }
    }

    private class TIFDataObserver extends ContentObserver {
        public TIFDataObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            Log.i("ChannelDateManager", "TIFDataObserver receive change");
            if (uri == null || !uri.toString().startsWith(TvContract.Channels.CONTENT_URI.toString()) || "TYPE_PREVIEW".equals(ChannelDateManager.this.tifDatabaseReader.getChannelType(uri))) {
                ChannelDateManager.this.backgroundHandler.removeCallbacks(ChannelDateManager.this.debouncedReadRunnable);
                ChannelDateManager.this.backgroundHandler.postDelayed(ChannelDateManager.this.debouncedReadRunnable, ChannelDateManager.DEBOUNCE_DELAY_MS);
            }
        }
    }
}
