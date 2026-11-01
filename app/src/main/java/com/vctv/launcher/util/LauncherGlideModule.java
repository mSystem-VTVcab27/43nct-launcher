package com.vctv.launcher.util;

import android.app.ActivityManager;
import android.content.Context;
import androidx.annotation.NonNull;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

@GlideModule
public final class LauncherGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean isLowRam = am != null && am.isLowRamDevice();

        // Optimize memory: Prefer RGB_565 to cut bitmap memory consumption by ~50%
        builder.setDefaultRequestOptions(
            new RequestOptions().format(DecodeFormat.PREFER_RGB_565)
        );

        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(isLowRam ? 1.0f : 1.5f)
                .setBitmapPoolScreens(isLowRam ? 1.0f : 1.5f)
                .build();

        builder.setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));
        builder.setBitmapPool(new LruBitmapPool(calculator.getBitmapPoolSize()));

        // Limit disk cache size on TV flash memory to avoid high I/O overhead
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, 40 * 1024 * 1024));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}

