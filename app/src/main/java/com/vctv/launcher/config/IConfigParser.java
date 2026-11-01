package com.vctv.launcher.config;

import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public interface IConfigParser<T> {
    Class<T> getConfigType();

    String getTagName();

    T parse(XmlPullParser xmlPullParser) throws Exception;
}
