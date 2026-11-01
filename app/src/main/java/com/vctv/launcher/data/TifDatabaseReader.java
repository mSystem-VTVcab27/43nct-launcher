package com.vctv.launcher.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.net.Uri;
import android.util.Log;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class TifDatabaseReader {
    private static final String PLAY_STORE_CHANNELS = "Apps Spotlight";
    private static String TAG = "TifDatabaseReader";
    private static volatile TifDatabaseReader instance;
    ContentResolver contentResolver;
    Context context;
    Map<String, Boolean> presetChannels;
    List<ChannelData> previewChannelDataList;

    public static TifDatabaseReader getInstance(Context context) {
        if (instance == null) {
            synchronized (TifDatabaseReader.class) {
                if (instance == null) {
                    instance = new TifDatabaseReader(context);
                }
            }
        }
        return instance;
    }

    private TifDatabaseReader(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.context = applicationContext;
        this.contentResolver = applicationContext.getContentResolver();
        this.presetChannels = getPresetChannels();
    }

    public List<ChannelData> readChannels() {
        ArrayList<ChannelData> arrayList = new ArrayList<>();
        try {
            Cursor cursorQuery = this.contentResolver.query(TvContract.Channels.CONTENT_URI, new String[]{"_id", "package_name", "input_id", "type", "display_number", "display_name", "description", "app_link_intent_uri"}, null, null, null);
            if (cursorQuery != null) {
                try {
                    while (cursorQuery.moveToNext()) {
                        long j = cursorQuery.getLong(0);
                        String string = cursorQuery.getString(1);
                        String string2 = cursorQuery.getString(2);
                        String string3 = cursorQuery.getString(3);
                        String string4 = cursorQuery.getString(4);
                        String string5 = cursorQuery.getString(5);
                        String string6 = cursorQuery.getString(6);
                        String string7 = cursorQuery.getString(7);
                        Uri uriBuildChannelUri = TvContract.buildChannelUri(j);
                        if ("TYPE_PREVIEW".equals(string3)) {
                            ChannelData channelData = new ChannelData();
                            channelData.setChannelId(j);
                            channelData.setPackageName(string);
                            channelData.setInputId(string2);
                            channelData.setType(string3);
                            channelData.setDisplayNumber(string4);
                            channelData.setDisplayName(string5);
                            channelData.setDescription(string6);
                            channelData.setAppLinkIntentUri(string7);
                            channelData.setChannelUri(uriBuildChannelUri);
                            Set<PreviewProgramData> previewProgramsForChannels = readPreviewProgramsForChannels(TvContract.buildPreviewProgramsUriForChannel(channelData.getChannelUri()));
                            channelData.setPreviewProgram(previewProgramsForChannels != null ? previewProgramsForChannels : new HashSet<>());
                            String str = string + ":" + string5;
                            Boolean bool = this.presetChannels.get(str);
                            if (bool != null) {
                                channelData.setPreset(true);
                                channelData.setMovable(bool.booleanValue());
                                Log.i(TAG, "preset channel: " + string5);
                            }
                            if (!PLAY_STORE_CHANNELS.equals(string5) && (Config.get().netflix.isIntegrated || !"com.netflix.ninja".equals(string))) {
                                arrayList.add(channelData);
                                Log.i(TAG, "add Channel: " + str + ", programs count: " + channelData.getPreviewProgram().size());
                            }
                        }
                    }
                } finally {
                    cursorQuery.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to read TV channels: " + e.getMessage());
        }

        return arrayList;
    }

    public Set<PreviewProgramData> readPreviewProgramsForChannels(Uri uri) {
        HashSet<PreviewProgramData> hashSet = new HashSet<>();
        try {
            Cursor cursorQuery = this.contentResolver.query(uri, new String[]{"title", "short_description", "poster_art_uri", "duration_millis", "intent_uri", "type", "poster_art_aspect_ratio", "interaction_count"}, null, null, null);
            if (cursorQuery != null) {
                try {
                    while (cursorQuery.moveToNext()) {
                        PreviewProgramData previewProgramData = new PreviewProgramData();
                        previewProgramData.setTitle(cursorQuery.getString(0));
                        previewProgramData.setShortDescription(cursorQuery.getString(1));
                        previewProgramData.setPosterArtUri(cursorQuery.getString(2));
                        previewProgramData.setDurationMillis(cursorQuery.getLong(3));
                        previewProgramData.setIntentUri(cursorQuery.getString(4));
                        previewProgramData.setType(cursorQuery.getLong(5));
                        previewProgramData.setPosterAspectRatio(cursorQuery.getInt(6));
                        previewProgramData.setInteractionCount(cursorQuery.getLong(7));
                        hashSet.add(previewProgramData);
                    }
                } finally {
                    cursorQuery.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to read preview programs: " + e.getMessage());
        }
        return hashSet;
    }

    private Map<String, Boolean> getPresetChannels() {
        HashMap<String, Boolean> map = new HashMap<>();
        if (Config.get().pagination != null && Config.get().pagination.channels != null && Config.get().pagination.channels.list != null) {
            for (Configuration.PresetItem presetItem : Config.get().pagination.channels.list) {
                map.put(presetItem.pkg + ":" + presetItem.name, Boolean.valueOf(presetItem.movable));
            }
        }
        return map;
    }

    public String getChannelType(Uri uri) {
        if (uri == null) {
            Log.e(TAG, "getChannelType: channelUri cannot be null.");
            return null;
        }
        try {
            Cursor cursorQuery = this.contentResolver.query(uri, new String[]{"type"}, null, null, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        int columnIndex = cursorQuery.getColumnIndex("type");
                        if (columnIndex != -1) {
                            return cursorQuery.getString(columnIndex);
                        }
                    }
                } finally {
                    cursorQuery.close();
                }
            }
            Log.w(TAG, "channel or COLUMN_TYPE not found in URI: " + uri);
        } catch (Exception e) {
            Log.e(TAG, "Error querying channel URI: " + uri + ", error: " + e.getMessage());
        }
        return null;
    }
}
