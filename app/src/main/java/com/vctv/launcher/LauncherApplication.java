package com.vctv.launcher;

import android.app.Application;
import android.content.ComponentCallbacks2;
import com.bumptech.glide.Glide;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.data.ChannelDateManager;

public class LauncherApplication extends Application {
    private static LauncherApplication sInstance;
    private ChannelDateManager channelDateManager;
    Configuration configuration;

    public static LauncherApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        this.configuration = Config.get();
        this.channelDateManager = ChannelDateManager.getInstance(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (this.channelDateManager != null) {
            this.channelDateManager.destroy();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        try {
            Glide.get(this).trimMemory(level);
            if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
                Glide.get(this).clearMemory();
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            Glide.get(this).clearMemory();
        } catch (Exception ignored) {
        }
    }
}
