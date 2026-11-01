package com.vctv.launcher.config;

import android.util.Log;
import com.bumptech.glide.load.Key;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/* JADX INFO: loaded from: classes.dex */
public class ConfigurationParser {
    private static final String TAG = "ConfigurationParser";
    private final Map<String, IConfigParser<?>> parserMap = new HashMap();
    private final Map<Class<?>, Object> configMap = new HashMap();

    public ConfigurationParser() {
        registerParser(new NetflixParser());
        registerParser(new GlobalStyleParser());
        registerParser(new LogoParser());
        registerParser(new PaginationParser());
    }

    public void registerParser(IConfigParser<?> iConfigParser) {
        this.parserMap.put(iConfigParser.getTagName(), iConfigParser);
    }

    public <T> T getConfig(Class<T> cls) throws IllegalAccessException, InstantiationException {
        Object obj = this.configMap.get(cls);
        if (obj == null) {
            Log.w(TAG, "Not found destination type " + cls);
            return cls.newInstance();
        }
        return cls.cast(obj);
    }

    public Configuration parse(InputStream inputStream) throws Exception {
        XmlPullParser xmlPullParserNewPullParser = XmlPullParserFactory.newInstance().newPullParser();
        xmlPullParserNewPullParser.setInput(inputStream, Key.STRING_CHARSET_NAME);
        String str = "";
        for (int eventType = xmlPullParserNewPullParser.getEventType(); eventType != 1; eventType = xmlPullParserNewPullParser.next()) {
            if (eventType == 2) {
                if ("launcher_config".equals(xmlPullParserNewPullParser.getName())) {
                    String attributeValue = xmlPullParserNewPullParser.getAttributeValue(null, "version");
                    if (attributeValue == null) {
                        Log.w(TAG, "Missing version attribute, using default '0'");
                        attributeValue = "0";
                    }
                    str = attributeValue;
                } else {
                    IConfigParser<?> iConfigParser = this.parserMap.get(xmlPullParserNewPullParser.getName());
                    if (iConfigParser != null) {
                        try {
                            this.configMap.put(iConfigParser.getConfigType(), iConfigParser.parse(xmlPullParserNewPullParser));
                        } catch (Exception e) {
                            Log.e(TAG, "parse failed ", e);
                        }
                    }
                }
            }
        }
        return new Configuration(str, (Configuration.Netflix) getConfig(Configuration.Netflix.class), (Configuration.GlobalStyle) getConfig(Configuration.GlobalStyle.class), (Configuration.Logo) getConfig(Configuration.Logo.class), (Configuration.Pagination) getConfig(Configuration.Pagination.class));
    }
}
