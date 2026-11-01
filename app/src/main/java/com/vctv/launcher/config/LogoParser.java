package com.vctv.launcher.config;

import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class LogoParser implements IConfigParser<Configuration.Logo> {
    private static final String TAG = "LogoParser";

    @Override // com.vctv.launcher.config.IConfigParser
    public String getTagName() {
        return "logo_config";
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.vctv.launcher.config.IConfigParser
    public Configuration.Logo parse(XmlPullParser xmlPullParser) throws Exception {
        Configuration.Logo logo = new Configuration.Logo();
        int eventType = xmlPullParser.getEventType();
        while (true) {
            if (eventType == 3 && getTagName().equals(xmlPullParser.getName())) {
                return logo;
            }
            if (eventType == 2) {
                logo.enabled = Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "enabled"));
                logo.path = xmlPullParser.getAttributeValue(null, "path");
            }
            eventType = xmlPullParser.next();
        }
    }

    @Override // com.vctv.launcher.config.IConfigParser
    public Class<Configuration.Logo> getConfigType() {
        return Configuration.Logo.class;
    }
}
