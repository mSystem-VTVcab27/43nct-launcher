package com.vctv.launcher.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LauncherDatabaseHelper extends SQLiteOpenHelper {
    private static final String COLUMN_CHANNEL_ID = "channel_id";
    private static final String COLUMN_CHANNEL_VERSION = "channel_version";
    private static final String COLUMN_DISPLAY_NAME = "display_name";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IS_PRESET = "is_preset";
    private static final String COLUMN_IS_SHOW = "is_show";
    private static final String COLUMN_MOVABLE = "movable";
    private static final String COLUMN_PACKAGE = "package_name";
    private static final String COLUMN_POSITION = "position";
    private static final String CREATE_TABLE = "CREATE TABLE display_channels_info(id INTEGER PRIMARY KEY AUTOINCREMENT,channel_id INTEGER NOT NULL UNIQUE,package_name TEXT NOT NULL,display_name TEXT,position INTEGER DEFAULT -1,is_show INTEGER DEFAULT 0,is_preset INTEGER DEFAULT 0,movable INTEGER DEFAULT 0,channel_version TEXT);";
    private static final String DATABASE_NAME = "launcher.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "display_channels_info";
    private static String TAG = "LauncherDatabaseHelper";
    private static LauncherDatabaseHelper instance;

    private LauncherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, DATABASE_VERSION);
    }

    public static synchronized LauncherDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LauncherDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS display_channels_info");
        onCreate(sQLiteDatabase);
    }

    public long insertChannel(DbChannelData dbChannelData) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CHANNEL_ID, Long.valueOf(dbChannelData.getChannelId()));
        contentValues.put(COLUMN_PACKAGE, dbChannelData.getPackageName());
        contentValues.put(COLUMN_DISPLAY_NAME, dbChannelData.getDisplayName());
        contentValues.put(COLUMN_POSITION, Integer.valueOf(dbChannelData.getPosition()));
        contentValues.put(COLUMN_IS_SHOW, Integer.valueOf(dbChannelData.isShow() ? 1 : 0));
        contentValues.put(COLUMN_IS_PRESET, Integer.valueOf(dbChannelData.isPreset() ? 1 : 0));
        contentValues.put(COLUMN_MOVABLE, Integer.valueOf(dbChannelData.isMovable() ? 1 : 0));
        contentValues.put(COLUMN_CHANNEL_VERSION, dbChannelData.getChannelVersion());
        try {
            return writableDatabase.insert(TABLE_NAME, null, contentValues);
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "Duplicate channel ID: " + dbChannelData.getChannelId(), e);
            return -1L;
        }
    }

    public List<DbChannelData> getAllChannels() {
        ArrayList arrayList = new ArrayList();
        Cursor cursorRawQuery = null;
        try {
            cursorRawQuery = getReadableDatabase().rawQuery("SELECT * FROM display_channels_info", null);
            while (cursorRawQuery.moveToNext()) {
                DbChannelData dbChannelData = new DbChannelData();
                boolean z = false;
                dbChannelData.setId(cursorRawQuery.getLong(0));
                dbChannelData.setChannelId(cursorRawQuery.getLong(1));
                dbChannelData.setPackageName(cursorRawQuery.getString(2));
                dbChannelData.setDisplayName(cursorRawQuery.getString(3));
                dbChannelData.setPosition(cursorRawQuery.getInt(4));
                dbChannelData.setShow(cursorRawQuery.getInt(5) == 1);
                dbChannelData.setPreset(cursorRawQuery.getInt(6) == 1);
                if (cursorRawQuery.getInt(7) == 1) {
                    z = true;
                }
                dbChannelData.setMovable(z);
                dbChannelData.setChannelVersion(cursorRawQuery.getString(8));
                arrayList.add(dbChannelData);
            }
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
            return arrayList;
        } catch (Throwable th) {
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
            throw th;
        }
    }

    public int updateChannel(ChannelData channelData) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CHANNEL_ID, Long.valueOf(channelData.getChannelId()));
        contentValues.put(COLUMN_PACKAGE, channelData.getPackageName());
        contentValues.put(COLUMN_DISPLAY_NAME, channelData.getDisplayName());
        contentValues.put(COLUMN_POSITION, Integer.valueOf(channelData.getPosition()));
        contentValues.put(COLUMN_IS_SHOW, Integer.valueOf(channelData.isShow() ? 1 : 0));
        contentValues.put(COLUMN_IS_PRESET, Integer.valueOf(channelData.isPreset() ? 1 : 0));
        contentValues.put(COLUMN_MOVABLE, Integer.valueOf(channelData.isMovable() ? 1 : 0));
        contentValues.put(COLUMN_CHANNEL_VERSION, channelData.getChannelVersion());
        int iUpdate = writableDatabase.update(TABLE_NAME, contentValues, "channel_id = ?", new String[]{String.valueOf(channelData.getChannelId())});
        Log.d(TAG, "Updated " + iUpdate + " rows for channel ID: " + channelData.getChannelId());
        return iUpdate;
    }

    public void deleteChannel(long j) {
        getWritableDatabase().delete(TABLE_NAME, "channel_id = ?", new String[]{String.valueOf(j)});
    }

    public void deleteAllChannels() {
        getWritableDatabase().delete(TABLE_NAME, null, null);
    }

    public int getChannelCount() {
        int i = 0;
        Cursor cursorRawQuery = null;
        try {
            try {
                cursorRawQuery = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM display_channels_info", null);
                if (cursorRawQuery != null && cursorRawQuery.moveToFirst()) {
                    i = cursorRawQuery.getInt(0);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
            }
            return i;
        } finally {
            if (cursorRawQuery != null) {
                cursorRawQuery.close();
            }
        }
    }
}
