package com.vctv.launcher.config;

import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class NetflixParser implements IConfigParser<Configuration.Netflix> {
    private static final String TAG = "NetflixParser";

    @Override // com.vctv.launcher.config.IConfigParser
    public String getTagName() {
        return "netflix";
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.vctv.launcher.config.IConfigParser
    public Configuration.Netflix parse(XmlPullParser xmlPullParser) throws Exception {
        Configuration.Netflix netflix = new Configuration.Netflix();
        int eventType = xmlPullParser.getEventType();
        while (true) {
            if (eventType == 3 && getTagName().equals(xmlPullParser.getName())) {
                return netflix;
            }
            if (eventType == 2) {
                netflix.isIntegrated = Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "isIntegrated"));
                netflix.iidFavApp = xmlPullParser.getAttributeValue(null, "iid_favapp");
                netflix.iidApps = xmlPullParser.getAttributeValue(null, "iid_app");
                netflix.iidChannel = xmlPullParser.getAttributeValue(null, "iid_channel");
            }
            eventType = xmlPullParser.next();
        }
    }

    @Override // com.vctv.launcher.config.IConfigParser
    public Class<Configuration.Netflix> getConfigType() {
        return Configuration.Netflix.class;
    }
}
