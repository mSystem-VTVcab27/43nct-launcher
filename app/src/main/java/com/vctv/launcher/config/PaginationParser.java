package com.vctv.launcher.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class PaginationParser implements IConfigParser<Configuration.Pagination> {
    private static final String TAG = "PaginationParser";
    private final Map<String, Consumer<XmlPullParser>> handlers;
    Configuration.Pagination pagination = new Configuration.Pagination();

    @Override // com.vctv.launcher.config.IConfigParser
    public String getTagName() {
        return "pagination_config";
    }

    public PaginationParser() {
        HashMap map = new HashMap();
        this.handlers = map;
        map.put("page", new Consumer() { // from class: com.vctv.launcher.config.PaginationParser$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PaginationParser.this.handlePage((XmlPullParser) obj);
            }
        });
        map.put("fav_apps", new Consumer() { // from class: com.vctv.launcher.config.PaginationParser$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PaginationParser.this.handleFavApps((XmlPullParser) obj);
            }
        });
        map.put("preset_fav", new Consumer() { // from class: com.vctv.launcher.config.PaginationParser$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PaginationParser.this.handlePresetFav((XmlPullParser) obj);
            }
        });
        map.put("channels", new Consumer() { // from class: com.vctv.launcher.config.PaginationParser$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PaginationParser.this.handleChannels((XmlPullParser) obj);
            }
        });
        map.put("preset_channel", new Consumer() { // from class: com.vctv.launcher.config.PaginationParser$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PaginationParser.this.handlePresetChannel((XmlPullParser) obj);
            }
        });
        map.put("item_config", new Consumer() { // from class: com.vctv.launcher.config.PaginationParser$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PaginationParser.this.handleItemConfig((XmlPullParser) obj);
            }
        });
        map.put("recommend", new Consumer() { // from class: com.vctv.launcher.config.PaginationParser$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PaginationParser.this.handleRecommend((XmlPullParser) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRecommend(XmlPullParser xmlPullParser) {
        this.pagination.recommendEnable = Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "enabled"));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.vctv.launcher.config.IConfigParser
    public Configuration.Pagination parse(XmlPullParser xmlPullParser) throws Exception {
        int eventType = xmlPullParser.getEventType();
        while (true) {
            if (eventType != 3 || !getTagName().equals(xmlPullParser.getName())) {
                if (eventType == 2) {
                    Consumer<XmlPullParser> consumer = this.handlers.get(xmlPullParser.getName());
                    if (consumer != null) {
                        consumer.accept(xmlPullParser);
                    }
                }
                eventType = xmlPullParser.next();
            } else {
                return this.pagination;
            }
        }
    }

    @Override // com.vctv.launcher.config.IConfigParser
    public Class<Configuration.Pagination> getConfigType() {
        return Configuration.Pagination.class;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleChannels(XmlPullParser xmlPullParser) {
        this.pagination.channels.enabled = Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "enabled"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePresetFav(XmlPullParser xmlPullParser) {
        this.pagination.favApp.favAppList.add(getPresetItem(xmlPullParser));
    }

    private static Configuration.PresetItem getPresetItem(XmlPullParser xmlPullParser) {
        return new Configuration.PresetItem(xmlPullParser.getAttributeValue(null, "package"), xmlPullParser.getAttributeValue(null, "name"), xmlPullParser.getAttributeValue(null, "bannerPath"), Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "movable")), Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "removable")));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFavApps(XmlPullParser xmlPullParser) {
        this.pagination.favApp.maxCount = Integer.parseInt(xmlPullParser.getAttributeValue(null, "max_count"));
        this.pagination.favApp.scrollDirection = Configuration.Direction.fromString(xmlPullParser.getAttributeValue(null, "scroll_direction"));
        this.pagination.favApp.showLabel = Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "show_label"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePresetChannel(XmlPullParser xmlPullParser) {
        this.pagination.channels.list.add(getPresetItem(xmlPullParser));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleItemConfig(XmlPullParser xmlPullParser) {
        this.pagination.appConfig.scrollDirection = Configuration.Direction.fromString(xmlPullParser.getAttributeValue(null, "scroll_direction"));
        this.pagination.appConfig.showLabel = Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "show_label"));
        this.pagination.appConfig.orderBy = Configuration.App.OrderBy.fromString(xmlPullParser.getAttributeValue(null, "order"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePage(XmlPullParser xmlPullParser) {
        this.pagination.pages.add(new Configuration.Page(xmlPullParser.getAttributeValue(null, "id"), Boolean.parseBoolean(xmlPullParser.getAttributeValue(null, "enabled")), Integer.parseInt(xmlPullParser.getAttributeValue(null, "order"))));
    }
}
