package com.vctv.launcher.tab.notifications;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
public class NotificationObserver extends LiveData<Integer> {
    public static final String AUTHORITY = "com.android.tv.notifications.NotificationContentProvider";
    private static final String COLUMN_COUNT = "count";
    public static final String PATH_NOTIFICATIONS = "notifications";
    public static final String PATH_NOTIFICATIONS_COUNT = "notifications/count";
    private static final String TAG = "NotificationObserver";
    public static final Uri contentUri = Uri.parse("content://com.android.tv.notifications.NotificationContentProvider/notifications/count");
    private final ContentObserver contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            NotificationObserver.this.updateNotificationCounts();
        }
    };
    private final Context context;
    private ExecutorService executorService;

    public NotificationObserver(Context context) {
        this.context = context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotificationCounts() {
        if (this.executorService == null || this.executorService.isShutdown()) {
            return;
        }
        this.executorService.execute(new Runnable() {
            @Override // java.lang.Runnable
            public final void run() {
                NotificationObserver.this.lambda$updateNotificationCounts();
            }
        });
    }

    /* synthetic */ void lambda$updateNotificationCounts() {
        try {
            Cursor cursorQuery = this.context.getContentResolver().query(contentUri, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        int index = cursorQuery.getColumnIndex(COLUMN_COUNT);
                        if (index != -1) {
                            postValue(Integer.valueOf(cursorQuery.getInt(index)));
                        }
                    }
                } finally {
                    cursorQuery.close();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Notification content provider not available: " + e.getMessage());
        }
    }

    @Override // androidx.lifecycle.LiveData
    protected void onActive() {
        super.onActive();
        this.executorService = Executors.newSingleThreadExecutor();
        updateNotificationCounts();
        try {
            this.context.getContentResolver().registerContentObserver(contentUri, true, this.contentObserver);
        } catch (Exception e) {
            Log.d(TAG, "Failed to register notification content observer: " + e.getMessage());
        }
    }

    @Override // androidx.lifecycle.LiveData
    protected void onInactive() {
        super.onInactive();
        try {
            this.context.getContentResolver().unregisterContentObserver(this.contentObserver);
        } catch (Exception e) {
            Log.d(TAG, "Failed to unregister notification content observer: " + e.getMessage());
        }
        if (this.executorService != null && !this.executorService.isShutdown()) {
            this.executorService.shutdown();
        }
    }
}
