package com.vctv.launcher.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class GlobalStyleParser implements IConfigParser<Configuration.GlobalStyle> {
    private static final String TAG = "GlobalStyleParser";
    Configuration.GlobalStyle globalStyle = new Configuration.GlobalStyle();
    private final Map<String, Consumer<XmlPullParser>> handlers;

    @Override // com.vctv.launcher.config.IConfigParser
    public String getTagName() {
        return "global_style";
    }

    public GlobalStyleParser() {
        HashMap map = new HashMap();
        this.handlers = map;
        map.put("text", new Consumer() { // from class: com.vctv.launcher.config.GlobalStyleParser$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                GlobalStyleParser.this.handleText((XmlPullParser) obj);
            }
        });
        map.put("background", new Consumer() { // from class: com.vctv.launcher.config.GlobalStyleParser$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                GlobalStyleParser.this.parseBackground((XmlPullParser) obj);
            }
        });
        map.put("item_style", new Consumer() { // from class: com.vctv.launcher.config.GlobalStyleParser$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                GlobalStyleParser.this.parseItemStyle((XmlPullParser) obj);
            }
        });
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.vctv.launcher.config.IConfigParser
    public Configuration.GlobalStyle parse(XmlPullParser xmlPullParser) throws Exception {
        Consumer<XmlPullParser> consumer;
        int eventType = xmlPullParser.getEventType();
        while (true) {
            if (eventType != 3 || !getTagName().equals(xmlPullParser.getName())) {
                if (eventType == 2 && (consumer = this.handlers.get(xmlPullParser.getName())) != null) {
                    consumer.accept(xmlPullParser);
                }
                eventType = xmlPullParser.next();
            } else {
                return this.globalStyle;
            }
        }
    }

    @Override // com.vctv.launcher.config.IConfigParser
    public Class<Configuration.GlobalStyle> getConfigType() {
        return Configuration.GlobalStyle.class;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseItemStyle(XmlPullParser xmlPullParser) {
        this.globalStyle.itemSize = Configuration.GlobalStyle.ItemSize.fromString(xmlPullParser.getAttributeValue(null, "size"));
        this.globalStyle.itemCornerRadius = Integer.parseInt(xmlPullParser.getAttributeValue(null, "corner_radius").replaceAll("[^0-9]", ""));
        this.globalStyle.itemBorderWidth = Integer.parseInt(xmlPullParser.getAttributeValue(null, "border_width").replaceAll("[^0-9]", ""));
        this.globalStyle.itemSelectionMode = Configuration.GlobalStyle.SelectionMode.fromString(xmlPullParser.getAttributeValue(null, "selection_mode"));
        this.globalStyle.itemZoomScale = Float.parseFloat(xmlPullParser.getAttributeValue(null, "zoom_scale"));
        this.globalStyle.itemZoomSpeed = Integer.parseInt(xmlPullParser.getAttributeValue(null, "zoom_speed").replaceAll("[^0-9]", ""));
        this.globalStyle.itemSelectedBorderColor = xmlPullParser.getAttributeValue(null, "selected_border_color");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void parseBackground(XmlPullParser xmlPullParser) {
        this.globalStyle.backgroundColor = xmlPullParser.getAttributeValue(null, "color");
        this.globalStyle.dynamicEffect = Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "dynamic_effect"));
        String strReplaceAll = xmlPullParser.getAttributeValue(null, "transition_speed").replaceAll("[^0-9]", "");
        this.globalStyle.transitionSpeed = Integer.parseInt(strReplaceAll);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleText(XmlPullParser xmlPullParser) {
        this.globalStyle.textFont = xmlPullParser.getAttributeValue(null, "font");
        this.globalStyle.textDefaultColor = xmlPullParser.getAttributeValue(null, "default_color");
        this.globalStyle.textSelectedColor = xmlPullParser.getAttributeValue(null, "selected_color");
        this.globalStyle.textFocusedColor = xmlPullParser.getAttributeValue(null, "focused_color");
    }
}
