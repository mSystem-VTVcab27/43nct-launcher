package com.vctv.launcher.app;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.ArrayMap;
import java.util.Date;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class AppDbHelper extends SQLiteOpenHelper {
    private static final String CREATE_APP_TABLE = "CREATE TABLE IF NOT EXISTS app_items(component TEXT NOT NULL PRIMARY KEY , order_priority INTEGER NOT NULL DEFAULT 0 , last_open INTEGER  )";
    private static final String CREATE_FAV_APP_TABLE = "CREATE TABLE IF NOT EXISTS fav_app_items(component TEXT NOT NULL PRIMARY KEY , order_priority INTEGER NOT NULL DEFAULT 0  )";
    private static final String DATABASE_NAME = "launch_items";
    private static final int DATABASE_VERSION = 1;

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public AppDbHelper(Context context) {
        super(context, DATABASE_NAME, new SQLiteDatabase.CursorFactory() { // from class: com.vctv.launcher.app.AppDbHelper.1
            @Override // android.database.sqlite.SQLiteDatabase.CursorFactory
            public Cursor newCursor(SQLiteDatabase sQLiteDatabase, SQLiteCursorDriver sQLiteCursorDriver, String str, SQLiteQuery sQLiteQuery) {
                return new SQLiteCursor(sQLiteCursorDriver, str, sQLiteQuery);
            }
        }, 1);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(CREATE_APP_TABLE);
        sQLiteDatabase.execSQL(CREATE_FAV_APP_TABLE);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onConfigure(SQLiteDatabase sQLiteDatabase) {
        super.onConfigure(sQLiteDatabase);
        setWriteAheadLoggingEnabled(true);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    public Map<ComponentName, Date> readLastOpens() {
        Cursor cursorQuery = getReadableDatabase().query(AppDbItem.TABLE_NAME, new String[]{AppDbItem.COLUMN_COMPONENT, AppDbItem.COLUMN_LAST_OPEN}, null, null, null, null, null, null);
        try {
            int columnIndex = cursorQuery.getColumnIndex(AppDbItem.COLUMN_COMPONENT);
            int columnIndex2 = cursorQuery.getColumnIndex(AppDbItem.COLUMN_LAST_OPEN);
            ArrayMap arrayMap = new ArrayMap(cursorQuery.getCount());
            while (cursorQuery.moveToNext()) {
                ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(cursorQuery.getString(columnIndex));
                if (cursorQuery.isNull(columnIndex2)) {
                    arrayMap.put(componentNameUnflattenFromString, null);
                } else {
                    arrayMap.put(componentNameUnflattenFromString, new Date(cursorQuery.getLong(columnIndex2)));
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return arrayMap;
        } catch (Throwable th) {
            if (cursorQuery != null) {
                try {
                    cursorQuery.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public Map<ComponentName, Long> readFavAppList() {
        Cursor cursorQuery = getReadableDatabase().query(AppDbItem.FAV_APP_TABLE_NAME, new String[]{AppDbItem.COLUMN_COMPONENT, AppDbItem.COLUMN_ORDER_PRIORITY}, null, null, null, null, null, null);
        try {
            int columnIndex = cursorQuery.getColumnIndex(AppDbItem.COLUMN_COMPONENT);
            int columnIndex2 = cursorQuery.getColumnIndex(AppDbItem.COLUMN_ORDER_PRIORITY);
            ArrayMap arrayMap = new ArrayMap(cursorQuery.getCount());
            while (cursorQuery.moveToNext()) {
                arrayMap.put(ComponentName.unflattenFromString(cursorQuery.getString(columnIndex)), Long.valueOf(cursorQuery.getLong(columnIndex2)));
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return arrayMap;
        } catch (Throwable th) {
            if (cursorQuery != null) {
                try {
                    cursorQuery.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public void writeLastOpen(ComponentName componentName, Date date) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransactionNonExclusive();
        try {
            ContentValues contentValues = new ContentValues(2);
            contentValues.put(AppDbItem.COLUMN_LAST_OPEN, Long.valueOf(date.getTime()));
            if (writableDatabase.update(AppDbItem.TABLE_NAME, contentValues, "component=?", new String[]{componentName.flattenToString()}) < 1) {
                contentValues.put(AppDbItem.COLUMN_COMPONENT, componentName.flattenToString());
                writableDatabase.insert(AppDbItem.TABLE_NAME, AppDbItem.COLUMN_COMPONENT, contentValues);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public Map<ComponentName, Long> readOrderPriorities() {
        Cursor cursorQuery = getReadableDatabase().query(AppDbItem.TABLE_NAME, new String[]{AppDbItem.COLUMN_COMPONENT, AppDbItem.COLUMN_ORDER_PRIORITY}, null, null, null, null, null, null);
        try {
            int columnIndex = cursorQuery.getColumnIndex(AppDbItem.COLUMN_COMPONENT);
            int columnIndex2 = cursorQuery.getColumnIndex(AppDbItem.COLUMN_ORDER_PRIORITY);
            ArrayMap arrayMap = new ArrayMap(cursorQuery.getCount());
            while (cursorQuery.moveToNext()) {
                arrayMap.put(ComponentName.unflattenFromString(cursorQuery.getString(columnIndex)), Long.valueOf(cursorQuery.getLong(columnIndex2)));
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return arrayMap;
        } catch (Throwable th) {
            if (cursorQuery != null) {
                try {
                    cursorQuery.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public void writeOrderPriority(ComponentName componentName, long j) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransactionNonExclusive();
        try {
            ContentValues contentValues = new ContentValues(2);
            contentValues.put(AppDbItem.COLUMN_ORDER_PRIORITY, Long.valueOf(j));
            if (writableDatabase.update(AppDbItem.TABLE_NAME, contentValues, "component=?", new String[]{componentName.flattenToString()}) < 1) {
                contentValues.put(AppDbItem.COLUMN_COMPONENT, componentName.flattenToString());
                writableDatabase.insert(AppDbItem.TABLE_NAME, AppDbItem.COLUMN_COMPONENT, contentValues);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void writeFavAppOrderPriority(ComponentName componentName, long j) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransactionNonExclusive();
        try {
            ContentValues contentValues = new ContentValues(2);
            contentValues.put(AppDbItem.COLUMN_ORDER_PRIORITY, Long.valueOf(j));
            if (writableDatabase.update(AppDbItem.FAV_APP_TABLE_NAME, contentValues, "component=?", new String[]{componentName.flattenToString()}) < 1) {
                contentValues.put(AppDbItem.COLUMN_COMPONENT, componentName.flattenToString());
                writableDatabase.insert(AppDbItem.FAV_APP_TABLE_NAME, AppDbItem.COLUMN_COMPONENT, contentValues);
            }
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void deleteFavAppItem(ComponentName componentName) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransactionNonExclusive();
        try {
            writableDatabase.delete(AppDbItem.FAV_APP_TABLE_NAME, "component=?", new String[]{componentName.flattenToString()});
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void deleteAppItem(ComponentName componentName) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransactionNonExclusive();
        try {
            writableDatabase.delete(AppDbItem.TABLE_NAME, "component=?", new String[]{componentName.flattenToString()});
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void clearAppData() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransactionNonExclusive();
        try {
            writableDatabase.delete(AppDbItem.TABLE_NAME, null, null);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void writeOrderPriorityAndShift(ComponentName componentName, int i) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransactionNonExclusive();
        if (i > 0) {
            try {
                writableDatabase.execSQL("UPDATE app_items SET order_priority=order_priority+1 WHERE order_priority>=?", new String[]{Integer.toString(i)});
            } finally {
                writableDatabase.endTransaction();
            }
        }
        writeOrderPriority(componentName, i);
        writableDatabase.setTransactionSuccessful();
    }

    private static class AppDbItem {
        public static final String COLUMN_COMPONENT = "component";
        public static final String COLUMN_LAST_OPEN = "last_open";
        public static final String COLUMN_ORDER_PRIORITY = "order_priority";
        public static final String FAV_APP_TABLE_NAME = "fav_app_items";
        public static final String TABLE_NAME = "app_items";

        private AppDbItem() {
        }
    }
}
