package com.vctv.launcher.config;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.vctv.launcher.R;
import com.vctv.launcher.util.ColorUtil;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class Configuration {
    private static final String TAG = "Configuration";
    public GlobalStyle globalStyle;
    public Logo logo;
    public Netflix netflix;
    public Pagination pagination;
    public String version;

    public Configuration() {
        this.globalStyle = new GlobalStyle();
        this.netflix = new Netflix();
        this.logo = new Logo();
        this.pagination = new Pagination();
    }

    public Configuration(String str, Netflix netflix, GlobalStyle globalStyle, Logo logo, Pagination pagination) {
        this.version = str;
        this.netflix = netflix != null ? netflix : new Netflix();
        this.globalStyle = globalStyle != null ? globalStyle : new GlobalStyle();
        this.logo = logo != null ? logo : new Logo();
        this.pagination = pagination != null ? pagination : new Pagination();
    }

    public String toString() {
        return "Configuration{version='" + this.version + "'netflix='" + this.netflix + "', globalStyle=" + this.globalStyle + ", logo=" + this.logo + ", pagination=" + this.pagination + '}';
    }

    public static class Netflix {
        public String iidApps;
        public String iidChannel;
        public String iidFavApp;
        public boolean isIntegrated;

        public Netflix() {
            this.isIntegrated = false;
            this.iidFavApp = "";
            this.iidApps = "";
            this.iidChannel = "";
        }

        public Netflix(boolean z, String str) {
            this.isIntegrated = z;
            this.iidFavApp = str;
            this.iidApps = str;
            this.iidChannel = str;
        }

        public boolean isIntegrated() {
            return this.isIntegrated;
        }

        public void setIntegrated(boolean z) {
            this.isIntegrated = z;
        }

        public String getIIDFavApp() {
            return this.iidFavApp;
        }

        public void setIIDFavApp(String str) {
            this.iidFavApp = str;
        }

        public String getIIDApps() {
            return this.iidApps;
        }

        public void setIIDApps(String str) {
            this.iidApps = str;
        }

        public String getIIDChannel() {
            return this.iidChannel;
        }

        public void setIIDChannel(String str) {
            this.iidChannel = str;
        }

        public String toString() {
            return "Netflix{isIntegrated=" + this.isIntegrated + ", iidFavApp='" + this.iidFavApp + "', iidApps='" + this.iidApps + "', iidChannel='" + this.iidChannel + "'}";
        }
    }

    public static class GlobalStyle {
        public String backgroundColor;
        public boolean dynamicEffect = true;
        public int itemBorderWidth = 3;
        public int itemCornerRadius = 12;
        public String itemSelectedBorderColor;
        public SelectionMode itemSelectionMode;
        public ItemSize itemSize;
        public float itemZoomScale = 1.18f;
        public int itemZoomSpeed = 150;
        public String textDefaultColor;
        public String textFocusedColor;
        public String textFont;
        public String textSelectedColor;
        public int transitionSpeed = 300;

        public GlobalStyle() {
            this.itemSize = ItemSize.MEDIUM;
            this.itemSelectionMode = SelectionMode.ZOOM;
        }

        public GlobalStyle(String str, String str2, String str3, String str4, String str5, boolean z, int i, ItemSize itemSize, int i2, int i3, SelectionMode selectionMode, float f, int i4, String str6) {
            this.textFont = str;
            this.textDefaultColor = str2;
            this.textSelectedColor = str3;
            this.textFocusedColor = str4;
            this.backgroundColor = str5;
            this.dynamicEffect = z;
            this.transitionSpeed = i;
            this.itemSize = itemSize != null ? itemSize : ItemSize.MEDIUM;
            this.itemCornerRadius = i2;
            this.itemBorderWidth = i3;
            this.itemSelectionMode = selectionMode != null ? selectionMode : SelectionMode.ZOOM;
            this.itemZoomScale = f;
            this.itemZoomSpeed = i4;
            this.itemSelectedBorderColor = str6;
        }

        public int getTextDefaultColor(Context context) {
            return ColorUtil.parseColorWithDefault(this.textDefaultColor, ContextCompat.getColor(context, R.color.default_text_color));
        }

        public int getTextSelectedColor(Context context) {
            return ColorUtil.parseColorWithDefault(this.textSelectedColor, ContextCompat.getColor(context, R.color.default_text_selected_color));
        }

        public int getTextFocusedColor(Context context) {
            return ColorUtil.parseColorWithDefault(this.textFocusedColor, ContextCompat.getColor(context, R.color.default_text_focus_color));
        }

        public int getBackgroundColor(Context context) {
            return ColorUtil.parseColorWithDefault(this.backgroundColor, ContextCompat.getColor(context, R.color.default_background));
        }

        public int geItemSelectedBorderColor(Context context) {
            return ColorUtil.parseColorWithDefault(this.itemSelectedBorderColor, ContextCompat.getColor(context, R.color.default_item_selected_border_color));
        }

        public enum ItemSize {
            SMALL,
            MEDIUM,
            LARGE;

            public static ItemSize fromString(String str) {
                if (str == null) return MEDIUM;
                for (ItemSize itemSize : values()) {
                    if (itemSize.name().equalsIgnoreCase(str)) {
                        return itemSize;
                    }
                }
                return MEDIUM;
            }
        }

        public enum SelectionMode {
            ZOOM,
            BORDER;

            public static SelectionMode fromString(String str) {
                if (str == null) return ZOOM;
                for (SelectionMode selectionMode : values()) {
                    if (selectionMode.name().equalsIgnoreCase(str)) {
                        return selectionMode;
                    }
                }
                return ZOOM;
            }
        }

        public String toString() {
            return "GlobalStyles{textFont='" + this.textFont + "', textDefaultColor='" + this.textDefaultColor + "', textSelectedColor='" + this.textSelectedColor + "', textFocusedColor='" + this.textFocusedColor + "', backgroundColor='" + this.backgroundColor + "', dynamicEffect=" + this.dynamicEffect + ", transitionSpeed=" + this.transitionSpeed + ", itemSize=" + this.itemSize + ", itemCornerRadius='" + this.itemCornerRadius + "', itemBorderWidth='" + this.itemBorderWidth + "', itemSelectionMode=" + this.itemSelectionMode + ", itemZoomScale=" + this.itemZoomScale + ", itemZoomSpeed='" + this.itemZoomSpeed + "', itemSelectedBorderColor='" + this.itemSelectedBorderColor + "'}";
        }
    }

    public static class Logo {
        public boolean enabled;
        public String path;

        public Logo() {
        }

        public Logo(boolean z, String str) {
            this.enabled = z;
            this.path = str;
        }

        public String toString() {
            return "Logo{enabled=" + this.enabled + ", path='" + this.path + "'}";
        }
    }

    public static class Page {
        public final boolean enabled;
        public final String id;
        public final int order;

        public Page(String str, boolean z, int i) {
            this.id = str;
            this.enabled = z;
            this.order = i;
        }

        public String toString() {
            return "Page{id='" + this.id + "', enabled=" + this.enabled + ", order=" + this.order + '}';
        }
    }

    public static class FavApp {
        public List<PresetItem> favAppList;
        public int maxCount;
        public Direction scrollDirection;
        public boolean showLabel;

        public FavApp() {
            this.favAppList = new ArrayList<>();
            this.maxCount = 20;
            this.scrollDirection = Direction.HORIZONTAL;
            this.showLabel = true;
        }

        public FavApp(int i, Direction direction, boolean z, List<PresetItem> list) {
            this.maxCount = i > 0 ? i : 20;
            this.scrollDirection = direction != null ? direction : Direction.HORIZONTAL;
            this.showLabel = z;
            this.favAppList = list != null ? list : new ArrayList<>();
        }

        public String toString() {
            return "FavApp{maxCount=" + this.maxCount + ", scrollDirection=" + this.scrollDirection + ", showLabel=" + this.showLabel + ", favAppList=" + this.favAppList + '}';
        }
    }

    public enum Direction {
        HORIZONTAL,
        VERTICAL;

        public static Direction fromString(String str) {
            if (str == null) return HORIZONTAL;
            for (Direction direction : values()) {
                if (direction.name().equalsIgnoreCase(str)) {
                    return direction;
                }
            }
            return HORIZONTAL;
        }
    }

    public static class Channels {
        public boolean enabled;
        public List<PresetItem> list;

        public Channels() {
            this.enabled = true;
            this.list = new ArrayList<>();
        }

        public Channels(boolean z, List<PresetItem> list) {
            this.enabled = z;
            this.list = list != null ? list : new ArrayList<>();
        }

        public String toString() {
            return "Channels{enabled=" + this.enabled + ", list=" + this.list + '}';
        }
    }

    public static class PresetItem {
        public final String bannerPath;
        public final boolean movable;
        public final String name;
        public final String pkg;
        public final boolean removable;

        public PresetItem(String str, String str2, String str3, boolean z, boolean z2) {
            this.pkg = str;
            this.name = str2;
            this.bannerPath = str3;
            this.movable = z;
            this.removable = z2;
        }

        public String toString() {
            return "PresetItem{pkg='" + this.pkg + "', name='" + this.name + "', bannerPath='" + this.bannerPath + ", movable=" + this.movable + ", removable=" + this.removable + '}';
        }
    }

    public static class App {
        public OrderBy orderBy;
        public Direction scrollDirection;
        public boolean showLabel;

        public App() {
            this.scrollDirection = Direction.VERTICAL;
            this.orderBy = OrderBy.RECENT;
            this.showLabel = true;
        }

        public App(Direction direction, boolean z, OrderBy orderBy) {
            this.scrollDirection = direction != null ? direction : Direction.VERTICAL;
            this.showLabel = z;
            this.orderBy = orderBy != null ? orderBy : OrderBy.RECENT;
        }

        public String toString() {
            return "App{scrollDirection=" + this.scrollDirection + ", showLabel=" + this.showLabel + ", orderBy=" + this.orderBy + '}';
        }

        public enum OrderBy {
            RECENT,
            ALPHABET,
            USER;

            public static OrderBy fromString(String str) {
                if (str == null) return RECENT;
                for (OrderBy orderBy : values()) {
                    if (orderBy.name().equalsIgnoreCase(str)) {
                        return orderBy;
                    }
                }
                return RECENT;
            }
        }
    }

    public static class Pagination {
        public App appConfig;
        public Channels channels;
        public FavApp favApp;
        public List<Page> pages;
        public boolean recommendEnable;

        public Pagination() {
            this.pages = new ArrayList<>();
            this.favApp = new FavApp();
            this.channels = new Channels();
            this.appConfig = new App();
            this.recommendEnable = true;
        }

        public Pagination(List<Page> list, FavApp favApp, Channels channels, App app, boolean z) {
            this.pages = list != null ? list : new ArrayList<>();
            this.favApp = favApp != null ? favApp : new FavApp();
            this.channels = channels != null ? channels : new Channels();
            this.appConfig = app != null ? app : new App();
            this.recommendEnable = z;
        }

        public String toString() {
            return "Pagination{pages=" + this.pages + ", favApp=" + this.favApp + ", channels=" + this.channels + ", appConfig=" + this.appConfig + ", recommendEnable=" + this.recommendEnable + '}';
        }
    }
}
