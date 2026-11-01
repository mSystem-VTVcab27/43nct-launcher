package com.vctv.launcher.config;

import android.util.Log;
import com.vctv.launcher.LauncherApplication;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;

/* JADX INFO: loaded from: classes.dex */
public class Config {
    private static final String OEM_CONFIG_PATH = "/oem/etc/launcher_config.xml";
    private static final String TAG = "Config";
    private static final String VENDOR_CONFIG_PATH = "/vendor/etc/launcher_config.xml";
    private Configuration configuration;

    private static final class InstanceHolder {
        private static final Config INSTANCE = new Config();

        private InstanceHolder() {
        }
    }

    public static Configuration get() {
        return InstanceHolder.INSTANCE.configuration;
    }

    private Config() {
        InputStream inputStream = null;
        try {
            File vendorFile = new File(VENDOR_CONFIG_PATH);
            File oemFile = new File(OEM_CONFIG_PATH);
            if (vendorFile.exists()) {
                inputStream = Files.newInputStream(vendorFile.toPath(), new OpenOption[0]);
            } else if (oemFile.exists()) {
                inputStream = Files.newInputStream(oemFile.toPath(), new OpenOption[0]);
            } else if (LauncherApplication.getInstance() != null) {
                try {
                    inputStream = LauncherApplication.getInstance().getAssets().open("launcher_config.xml");
                } catch (Exception ignored) {
                }
            }

            if (inputStream != null) {
                ConfigurationParser configurationParser = new ConfigurationParser();
                this.configuration = configurationParser.parse(inputStream);
            } else {
                this.configuration = new Configuration();
            }
        } catch (Exception e) {
            Log.e(TAG, "obtain configuration failed !!! " + e);
            this.configuration = new Configuration();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception ignored) {
                }
            }
        }
        Log.d(TAG, "Config: " + this.configuration);
    }
}
