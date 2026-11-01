package com.vctv.launcher.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.vctv.launcher.R;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class AppResource {
    public String addFav;
    public String addFavPlus;
    public int defaultColor;
    public int disableColor;
    public String done;
    public String info;
    private final Map<String, Drawable> mDrawableValueMap = new ArrayMap();
    public String move;
    public String moveDown;
    public String moveLeft;
    public String moveRight;
    public String moveUp;
    public String open;
    public String remFav;
    public String uninstall;

    public AppResource(Context context) {
        loadResValue(context);
        configDrawableValueMap(context);
    }

    private void loadResValue(Context context) {
        this.open = context.getString(R.string.item_open);
        this.addFav = context.getString(R.string.item_add_fav);
        this.remFav = context.getString(R.string.item_remove_fav);
        this.info = context.getString(R.string.item_info);
        this.uninstall = context.getString(R.string.item_uninstall);
        this.move = context.getString(R.string.item_move);
        this.addFavPlus = context.getString(R.string.add_fav_plus);
        this.moveUp = context.getString(R.string.move_up);
        this.moveDown = context.getString(R.string.move_down);
        this.moveLeft = context.getString(R.string.move_left);
        this.moveRight = context.getString(R.string.move_right);
        this.done = context.getString(R.string.done);
        this.defaultColor = ContextCompat.getColor(context, R.color.pop_menu_item_default);
        this.disableColor = ContextCompat.getColor(context, R.color.pop_menu_item_disable);
    }

    private void configDrawableValueMap(Context context) {
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_exit, null);
        Drawable drawable2 = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_add_fav, null);
        Drawable drawable3 = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_remove, null);
        Drawable drawable4 = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_info, null);
        Drawable drawable5 = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_delete, null);
        Drawable drawable6 = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_move, null);
        Drawable drawable7 = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_add, null);
        Drawable drawable8 = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_up, null);
        Drawable drawable9 = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_down, null);
        Drawable drawable10 = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_left, null);
        Drawable drawable11 = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_right, null);
        Drawable drawable12 = ResourcesCompat.getDrawable(context.getResources(), R.drawable.icon_done, null);
        this.mDrawableValueMap.put(context.getString(R.string.item_open), drawable);
        this.mDrawableValueMap.put(context.getString(R.string.item_add_fav), drawable2);
        this.mDrawableValueMap.put(context.getString(R.string.item_remove_fav), drawable3);
        this.mDrawableValueMap.put(context.getString(R.string.item_info), drawable4);
        this.mDrawableValueMap.put(context.getString(R.string.item_uninstall), drawable5);
        this.mDrawableValueMap.put(context.getString(R.string.item_move), drawable6);
        this.mDrawableValueMap.put(context.getString(R.string.add_fav_plus), drawable7);
        this.mDrawableValueMap.put(this.moveUp, drawable8);
        this.mDrawableValueMap.put(this.moveDown, drawable9);
        this.mDrawableValueMap.put(context.getString(R.string.move_left), drawable10);
        this.mDrawableValueMap.put(context.getString(R.string.move_right), drawable11);
        this.mDrawableValueMap.put(context.getString(R.string.done), drawable12);
    }

    public Drawable getDrawable(String str) {
        return this.mDrawableValueMap.get(str);
    }
}
