package com.vctv.launcher.app.popmenu;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.vctv.launcher.R;
import com.vctv.launcher.app.AppHelper;
import com.vctv.launcher.app.AppResource;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.data.AppItem;
import com.vctv.launcher.data.AppStrategy;
import com.vctv.launcher.data.PopMenuItem;
import com.vctv.launcher.util.AppConfigUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class PopMenuHelper {
    private final AppHelper mAppHelper;
    private final Context mContext;
    private final AppResource mResource;
    private final AppStrategy mStrategy;

    public PopMenuHelper(Context context, AppHelper appHelper, AppStrategy appStrategy) {
        this.mContext = context;
        this.mAppHelper = appHelper;
        this.mStrategy = appStrategy;
        this.mResource = new AppResource(context);
    }

    private List<PopMenuItem> getMenuItems(AppItem appItem) {
        ArrayList arrayList = new ArrayList();
        int i = AnonymousClass2.$SwitchMap$com$mw$launcher$data$AppStrategy[this.mStrategy.ordinal()];
        boolean z = true;
        if (i == 1) {
            boolean zIsFav = this.mAppHelper.isFav(appItem);
            boolean zIsRemovable = this.mAppHelper.isRemovable(appItem);
            AppResource appResource = this.mResource;
            arrayList.add(new PopMenuItem(appResource.getDrawable(appResource.open), this.mResource.open));
            if (Config.get().pagination.appConfig.orderBy.equals(Configuration.App.OrderBy.USER)) {
                AppResource appResource2 = this.mResource;
                arrayList.add(new PopMenuItem(appResource2.getDrawable(appResource2.move), this.mResource.move));
            }
            AppResource appResource3 = this.mResource;
            Drawable drawable = appResource3.getDrawable(zIsFav ? appResource3.remFav : appResource3.addFav);
            AppResource appResource4 = this.mResource;
            String str = zIsFav ? appResource4.remFav : appResource4.addFav;
            if (zIsFav && !zIsRemovable) {
                z = false;
            }
            arrayList.add(new PopMenuItem(drawable, str, z));
            AppResource appResource5 = this.mResource;
            arrayList.add(new PopMenuItem(appResource5.getDrawable(appResource5.info), this.mResource.info));
            AppResource appResource6 = this.mResource;
            arrayList.add(new PopMenuItem(appResource6.getDrawable(appResource6.uninstall), this.mResource.uninstall, appItem.isUninstallable()));
        } else if (i == 2) {
            AppResource appResource7 = this.mResource;
            arrayList.add(new PopMenuItem(appResource7.getDrawable(appResource7.remFav), this.mResource.remFav, appItem.isRemovable()));
            AppResource appResource8 = this.mResource;
            arrayList.add(new PopMenuItem(appResource8.getDrawable(appResource8.move), this.mResource.move, appItem.isMovable()));
            AppResource appResource9 = this.mResource;
            arrayList.add(new PopMenuItem(appResource9.getDrawable(appResource9.open), this.mResource.open));
        }
        return arrayList;
    }

    public void showPopMenu(final View view, final AppItem appItem) {
        View viewInflate = LayoutInflater.from(this.mContext).inflate(R.layout.layout_pop_menu, (ViewGroup) null);
        final PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2, true);
        List<PopMenuItem> menuItems = getMenuItems(appItem);
        final RecyclerView recyclerView = (RecyclerView) viewInflate.findViewById(R.id.menu_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        recyclerView.setAdapter(new PopMenuAdapter(menuItems, new PopMenuAdapter.OnItemClickListener() { // from class: com.vctv.launcher.app.popmenu.PopMenuHelper$$ExternalSyntheticLambda3
            @Override // com.vctv.launcher.app.popmenu.PopMenuAdapter.OnItemClickListener
            public final void onItemClicked(PopMenuItem popMenuItem) {
                PopMenuHelper.this.m172lambda$showPopMenu$0$commwlauncherapppopmenuPopMenuHelper(appItem, view, popupWindow, popMenuItem);
            }
        }));
        popupWindow.showAsDropDown(view, 0, getVerticalOffset(view, ((int) this.mContext.getResources().getDimension(R.dimen.pop_menu_height)) * menuItems.size()));
        recyclerView.post(new Runnable() { // from class: com.vctv.launcher.app.popmenu.PopMenuHelper$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                PopMenuHelper.lambda$showPopMenu$1(recyclerView);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$showPopMenu$0$com-mw-launcher-app-popmenu-PopMenuHelper, reason: not valid java name */
    /* synthetic */ void m172lambda$showPopMenu$0$commwlauncherapppopmenuPopMenuHelper(AppItem appItem, View view, PopupWindow popupWindow, PopMenuItem popMenuItem) {
        String desc = popMenuItem.getDesc();
        if (this.mResource.open.equals(desc)) {
            this.mAppHelper.onOpenClicked(appItem);
        } else if (this.mResource.addFav.equals(desc)) {
            this.mAppHelper.onAddFavClicked(appItem);
        } else if (this.mResource.remFav.equals(desc)) {
            this.mAppHelper.onRemFavClicked(appItem);
        } else if (this.mResource.info.equals(desc)) {
            this.mAppHelper.onInfoClicked(appItem);
        } else if (this.mResource.uninstall.equals(desc)) {
            this.mAppHelper.onUninstallClicked(appItem);
        } else if (this.mResource.move.equals(desc)) {
            this.mAppHelper.onMoveClicked(view);
        }
        popupWindow.dismiss();
    }

    static /* synthetic */ void lambda$showPopMenu$1(RecyclerView recyclerView) {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View childAt = recyclerView.getChildAt(i);
            if (childAt.isEnabled() && childAt.requestFocus()) {
                return;
            }
        }
    }

    /* JADX INFO: renamed from: com.vctv.launcher.app.popmenu.PopMenuHelper$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$config$Configuration$Direction;
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$data$AppStrategy;

        static {
            int[] iArr = new int[Configuration.Direction.values().length];
            $SwitchMap$com$mw$launcher$config$Configuration$Direction = iArr;
            try {
                iArr[Configuration.Direction.VERTICAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$mw$launcher$config$Configuration$Direction[Configuration.Direction.HORIZONTAL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            int[] iArr2 = new int[AppStrategy.values().length];
            $SwitchMap$com$mw$launcher$data$AppStrategy = iArr2;
            try {
                iArr2[AppStrategy.DEFAULT.ordinal()] = 1;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$mw$launcher$data$AppStrategy[AppStrategy.FAV.ordinal()] = 2;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    private List<PopMenuItem> getTalkBackMenuItems(PopMenuDirector popMenuDirector) {
        ArrayList arrayList = new ArrayList();
        int i = AnonymousClass2.$SwitchMap$com$mw$launcher$config$Configuration$Direction[AppConfigUtil.getDirection(this.mStrategy).ordinal()];
        if (i != 1) {
            if (i == 2) {
            }
            return arrayList;
        }
        AppResource appResource = this.mResource;
        arrayList.add(new PopMenuItem(appResource.getDrawable(appResource.moveUp), this.mResource.moveUp, popMenuDirector.canMoveNext(0)));
        AppResource appResource2 = this.mResource;
        arrayList.add(new PopMenuItem(appResource2.getDrawable(appResource2.moveDown), this.mResource.moveDown, popMenuDirector.canMoveNext(1)));
        AppResource appResource3 = this.mResource;
        arrayList.add(new PopMenuItem(appResource3.getDrawable(appResource3.moveLeft), this.mResource.moveLeft, popMenuDirector.canMoveNext(2)));
        AppResource appResource4 = this.mResource;
        arrayList.add(new PopMenuItem(appResource4.getDrawable(appResource4.moveRight), this.mResource.moveRight, popMenuDirector.canMoveNext(3)));
        AppResource appResource5 = this.mResource;
        arrayList.add(new PopMenuItem(appResource5.getDrawable(appResource5.done), this.mResource.done));
        return arrayList;
    }

    public void showTalkBackPopMenu(final PopMenuDirector popMenuDirector, View view) {
        View viewInflate = LayoutInflater.from(this.mContext).inflate(R.layout.layout_pop_menu_talk_back, (ViewGroup) null);
        final PopupWindow popupWindow = new PopupWindow(viewInflate, -1, -2, true);
        List<PopMenuItem> talkBackMenuItems = getTalkBackMenuItems(popMenuDirector);
        final RecyclerView recyclerView = (RecyclerView) viewInflate.findViewById(R.id.menu_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext, 0, false));
        recyclerView.setAdapter(new TalkBackPopMenuAdapter(talkBackMenuItems, new TalkBackPopMenuAdapter.OnItemClickListener() { // from class: com.vctv.launcher.app.popmenu.PopMenuHelper$$ExternalSyntheticLambda0
            @Override // com.vctv.launcher.app.popmenu.TalkBackPopMenuAdapter.OnItemClickListener
            public final void onItemClicked(PopMenuItem popMenuItem) {
                PopMenuHelper.this.m173x8af6e62c(popMenuDirector, popupWindow, recyclerView, popMenuItem);
            }
        }));
        recyclerView.setPadding(getSidePadding(talkBackMenuItems.size()), 0, 0, 0);
        recyclerView.setClipToPadding(false);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: com.vctv.launcher.app.popmenu.PopMenuHelper.1
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect rect, View view2, RecyclerView recyclerView2, RecyclerView.State state) {
                if (recyclerView2.getChildAdapterPosition(view2) != ((RecyclerView.Adapter) Objects.requireNonNull(recyclerView2.getAdapter())).getItemCount() - 1) {
                    rect.right = (int) PopMenuHelper.this.mContext.getResources().getDimension(R.dimen.pop_talk_back_menu_item_space);
                }
            }
        });
        final AppHelper appHelper = this.mAppHelper;
        Objects.requireNonNull(appHelper);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: com.vctv.launcher.app.popmenu.PopMenuHelper$$ExternalSyntheticLambda1
            @Override // android.widget.PopupWindow.OnDismissListener
            public final void onDismiss() {
                appHelper.onDoneClicked();
            }
        });
        popupWindow.showAtLocation(view, 80, 0, 0);
        recyclerView.post(new Runnable() { // from class: com.vctv.launcher.app.popmenu.PopMenuHelper$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                PopMenuHelper.lambda$showTalkBackPopMenu$3(recyclerView);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$showTalkBackPopMenu$2$com-mw-launcher-app-popmenu-PopMenuHelper, reason: not valid java name */
    /* synthetic */ void m173x8af6e62c(PopMenuDirector popMenuDirector, PopupWindow popupWindow, RecyclerView recyclerView, PopMenuItem popMenuItem) {
        String desc = popMenuItem.getDesc();
        if (this.mResource.moveUp.equals(desc)) {
            popMenuDirector.onMoveNext(0);
        } else if (this.mResource.moveDown.equals(desc)) {
            popMenuDirector.onMoveNext(1);
        } else if (this.mResource.moveLeft.equals(desc)) {
            popMenuDirector.onMoveNext(2);
        } else if (this.mResource.moveRight.equals(desc)) {
            popMenuDirector.onMoveNext(3);
        } else if (this.mResource.done.equals(desc)) {
            this.mAppHelper.onDoneClicked();
            popupWindow.dismiss();
        }
        refreshItemState(recyclerView, popMenuDirector);
    }

    static /* synthetic */ void lambda$showTalkBackPopMenu$3(RecyclerView recyclerView) {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View childAt = recyclerView.getChildAt(i);
            if (childAt.isEnabled() && childAt.requestFocus()) {
                return;
            }
        }
    }

    private void refreshItemState(RecyclerView recyclerView, PopMenuDirector popMenuDirector) {
        boolean zCanMoveNext = popMenuDirector.canMoveNext(0);
        boolean zCanMoveNext2 = popMenuDirector.canMoveNext(1);
        boolean zCanMoveNext3 = popMenuDirector.canMoveNext(2);
        boolean zCanMoveNext4 = popMenuDirector.canMoveNext(3);
        int i = AnonymousClass2.$SwitchMap$com$mw$launcher$config$Configuration$Direction[AppConfigUtil.getDirection(this.mStrategy).ordinal()];
        if (i != 1) {
            if (i != 2) {
                return;
            }
            recyclerView.getChildAt(0).setEnabled(zCanMoveNext3);
            View childAt = recyclerView.getChildAt(0);
            AppResource appResource = this.mResource;
            childAt.setBackgroundColor(zCanMoveNext3 ? appResource.defaultColor : appResource.disableColor);
            recyclerView.getChildAt(1).setEnabled(zCanMoveNext4);
            View childAt2 = recyclerView.getChildAt(1);
            AppResource appResource2 = this.mResource;
            childAt2.setBackgroundColor(zCanMoveNext4 ? appResource2.defaultColor : appResource2.disableColor);
            return;
        }
        recyclerView.getChildAt(0).setEnabled(zCanMoveNext);
        recyclerView.getChildAt(0).setBackgroundColor(zCanMoveNext ? this.mResource.defaultColor : this.mResource.disableColor);
        recyclerView.getChildAt(1).setEnabled(zCanMoveNext2);
        View childAt3 = recyclerView.getChildAt(1);
        AppResource appResource3 = this.mResource;
        childAt3.setBackgroundColor(zCanMoveNext2 ? appResource3.defaultColor : appResource3.disableColor);
        recyclerView.getChildAt(2).setEnabled(zCanMoveNext3);
        View childAt4 = recyclerView.getChildAt(2);
        AppResource appResource4 = this.mResource;
        childAt4.setBackgroundColor(zCanMoveNext3 ? appResource4.defaultColor : appResource4.disableColor);
        recyclerView.getChildAt(3).setEnabled(zCanMoveNext4);
        View childAt5 = recyclerView.getChildAt(3);
        AppResource appResource5 = this.mResource;
        childAt5.setBackgroundColor(zCanMoveNext4 ? appResource5.defaultColor : appResource5.disableColor);
    }

    private int getSidePadding(int i) {
        int dimension = (((int) this.mContext.getResources().getDimension(R.dimen.pop_talk_back_menu_item_width)) * i) + ((int) this.mContext.getResources().getDimension(R.dimen.pop_talk_back_menu_item_space));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Display) Objects.requireNonNull(this.mContext.getDisplay())).getRealMetrics(displayMetrics);
        int i2 = displayMetrics.widthPixels;
        if (dimension < i2) {
            return (i2 - dimension) / 2;
        }
        return 0;
    }

    private int getVerticalOffset(View view, int i) {
        int dimension = (int) this.mContext.getResources().getDimension(R.dimen.pop_menu_vertical_offset);
        if (Configuration.Direction.HORIZONTAL.equals(AppConfigUtil.getDirection(this.mStrategy))) {
            return dimension;
        }
        int[] iArr = new int[2];
        view.getLocationOnScreen(iArr);
        int height = iArr[1] + view.getHeight();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Display) Objects.requireNonNull(this.mContext.getDisplay())).getRealMetrics(displayMetrics);
        return displayMetrics.heightPixels - height < i + dimension ? -(view.getHeight() + i + dimension) : dimension;
    }
}
