package com.vctv.launcher.app.presenter;

import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.leanback.widget.Presenter;
import com.vctv.launcher.R;
import com.vctv.launcher.app.focus.FocusScaleHelper;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.data.AppItem;
import com.vctv.launcher.data.AppStrategy;
import com.vctv.launcher.util.AppConfigUtil;

/* JADX INFO: loaded from: classes.dex */
public class AppPresenter extends Presenter {
    private static final int ADJUST_VALUE = 2;
    private static final String TAG = "AppPresenter";
    private ValueAnimator colorAnimator;
    private FocusScaleHelper.ItemFocusScale focusScaleOperator;
    private Context mContext;
    private AppStrategy.State mState = AppStrategy.State.DEFAULT;
    private final AppStrategy strategy;

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) viewHolder;
            vh.banner.setImageDrawable(null);
            vh.banner.setBackground(null);
            vh.icon.setImageDrawable(null);
            vh.icon.setBackground(null);
        }
    }

    public AppPresenter(AppStrategy appStrategy) {
        this.strategy = appStrategy;
    }

    @Override // androidx.leanback.widget.Presenter
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        if (this.mContext == null) {
            this.mContext = viewGroup.getContext();
        }
        View viewInflate = LayoutInflater.from(this.mContext).inflate(R.layout.item_app, viewGroup, false);
        viewInflate.setSelected(true);
        viewInflate.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.app.presenter.AppPresenter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                AppPresenter.this.m176xe6098a3d(view, z);
            }
        });
        return new ViewHolder(viewInflate);
    }

    /* JADX INFO: renamed from: lambda$onCreateViewHolder$0$com-mw-launcher-app-presenter-AppPresenter, reason: not valid java name */
    /* synthetic */ void m176xe6098a3d(View view, boolean z) {
        View viewFindViewById = view.findViewById(R.id.banner_layout);
        ImageView imageView = (ImageView) view.findViewById(R.id.banner);
        TextView textView = (TextView) view.findViewById(R.id.label);
        imageView.setSelected(z);
        textView.setSelected(z);
        if (Configuration.GlobalStyle.SelectionMode.ZOOM.equals(getSelectMode())) {
            if (this.focusScaleOperator == null) {
                this.focusScaleOperator = new FocusScaleHelper.ItemFocusScale(Config.get().globalStyle.itemZoomScale, Config.get().globalStyle.itemZoomSpeed);
            }
            this.focusScaleOperator.onItemFocused(view, z);
        }
        if (isShowLabel()) {
            Context context = this.mContext;
            textView.setTextColor(z ? AppConfigUtil.getSelectedTextColor(context) : AppConfigUtil.getDefaultTextColor(context));
            textView.setVisibility(z ? 0 : 4);
        }
        if (Configuration.GlobalStyle.SelectionMode.BORDER.equals(getSelectMode()) && this.mState.equals(AppStrategy.State.DEFAULT)) {
            viewFindViewById.setBackground(z ? drawBorder() : null);
        }
        if (this.mState.equals(AppStrategy.State.MOVE)) {
            if (z) {
                triggerMoveEffect(view);
            } else {
                resetNormalEffect(view);
            }
        }
    }

    private void resetNormalEffect(View view) {
        view.findViewById(R.id.banner_layout).setBackground(null);
        ValueAnimator valueAnimator = this.colorAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.colorAnimator.removeAllUpdateListeners();
            this.colorAnimator = null;
        }
    }

    public void cancelMove(View view) {
        this.mState = AppStrategy.State.DEFAULT;
        View viewFindViewById = view.findViewById(R.id.banner_layout);
        viewFindViewById.setBackground(null);
        ValueAnimator valueAnimator = this.colorAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.colorAnimator.removeAllUpdateListeners();
            this.colorAnimator = null;
        }
        if (Configuration.GlobalStyle.SelectionMode.BORDER.equals(getSelectMode())) {
            viewFindViewById.setBackground(drawBorder());
        }
    }

    public void triggerMove(View view) {
        this.mState = AppStrategy.State.MOVE;
        triggerMoveEffect(view);
    }

    private void triggerMoveEffect(View view) {
        View viewFindViewById = view.findViewById(R.id.banner_layout);
        final GradientDrawable gradientDrawableDrawBorder = drawBorder();
        viewFindViewById.setBackground(gradientDrawableDrawBorder);
        ValueAnimator valueAnimatorOfArgb = ValueAnimator.ofArgb(getBorderColor(), 0, getBorderColor());
        this.colorAnimator = valueAnimatorOfArgb;
        valueAnimatorOfArgb.setDuration(1000L);
        this.colorAnimator.setRepeatMode(2);
        this.colorAnimator.setRepeatCount(-1);
        this.colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.vctv.launcher.app.presenter.AppPresenter$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                gradientDrawableDrawBorder.setStroke(5, ((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        });
        this.colorAnimator.start();
    }

    private GradientDrawable drawBorder() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setStroke(AppConfigUtil.getBorderWidth(this.mContext) + 2, getBorderColor());
        gradientDrawable.setColor(0);
        gradientDrawable.setCornerRadius(AppConfigUtil.getCornerRadius(this.mContext));
        return gradientDrawable;
    }

    private GradientDrawable drawDefaultBg() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setColor(ContextCompat.getColor(this.mContext, R.color.toast_bg));
        gradientDrawable.setCornerRadius(AppConfigUtil.getCornerRadius(this.mContext));
        return gradientDrawable;
    }

    private GradientDrawable drawNetflixBg() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setColor(-1);
        gradientDrawable.setCornerRadius(AppConfigUtil.getCornerRadius(this.mContext));
        return gradientDrawable;
    }

    @Override // androidx.leanback.widget.Presenter
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object obj) {
        ComponentName component;
        if (obj instanceof AppItem) {
            AppItem appItem = (AppItem) obj;
            ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            viewHolder2.banner.setImageDrawable(null);
            viewHolder2.banner.setBackground(null);
            viewHolder2.icon.setImageDrawable(null);
            viewHolder2.icon.setBackground(null);
            if (appItem.getBanner() != null) {
                Drawable cachedDrawable = appItem.getCachedBannerDrawable();
                if (cachedDrawable == null) {
                    RoundedBitmapDrawable roundedBitmapDrawableCreate = RoundedBitmapDrawableFactory.create(this.mContext.getResources(), getBitmapFromDrawable(appItem.getBanner()));
                    roundedBitmapDrawableCreate.setCornerRadius(AppConfigUtil.getCornerRadius(this.mContext));
                    cachedDrawable = roundedBitmapDrawableCreate;
                    appItem.setCachedBannerDrawable(cachedDrawable);
                }
                viewHolder2.banner.setBackground(cachedDrawable);
                if (Config.get().netflix.isIntegrated && (component = appItem.getIntent().getComponent()) != null && component.getPackageName().equals("com.netflix.ninja")) {
                    viewHolder2.banner.setImageDrawable(ContextCompat.getDrawable(this.mContext, R.mipmap.icon_netflix));
                    viewHolder2.banner.setBackground(drawNetflixBg());
                }
            } else if (appItem.getIcon() != null) {
                viewHolder2.banner.setBackground(drawDefaultBg());
                viewHolder2.icon.setImageDrawable(appItem.getIcon());
            }
            if (TextUtils.isEmpty(appItem.getLabel())) {
                return;
            }
            viewHolder2.label.setText(appItem.getLabel());
            viewHolder2.label.setTextColor(AppConfigUtil.getDefaultTextColor(this.mContext));
            viewHolder2.view.setContentDescription(appItem.getLabel());
            if (appItem.getLabel().equals(this.mContext.getString(R.string.add_fav_plus))) {
                setAccessibilityDelegateForAddFavAppItem(viewHolder2.view);
            }
        }
    }

    private void setAccessibilityDelegateForAddFavAppItem(View view) {
        ViewCompat.setAccessibilityDelegate(view, new AccessibilityDelegateCompat() { // from class: com.vctv.launcher.app.presenter.AppPresenter.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfoCompat);
                AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(16, AppPresenter.this.mContext.getString(R.string.start_select_app_activity));
                AccessibilityNodeInfoCompat.AccessibilityActionCompat accessibilityActionCompat2 = new AccessibilityNodeInfoCompat.AccessibilityActionCompat(32, AppPresenter.this.mContext.getString(R.string.start_select_app_activity));
                accessibilityNodeInfoCompat.addAction(accessibilityActionCompat);
                accessibilityNodeInfoCompat.addAction(accessibilityActionCompat2);
            }
        });
    }

    public static class ViewHolder extends Presenter.ViewHolder {
        private final ImageView banner;
        private final ImageView icon;
        private final TextView label;

        public ViewHolder(View view) {
            super(view);
            ImageView imageView = (ImageView) view.findViewById(R.id.banner);
            this.banner = imageView;
            ImageView imageView2 = (ImageView) view.findViewById(R.id.icon);
            this.icon = imageView2;
            TextView textView = (TextView) view.findViewById(R.id.label);
            this.label = textView;
            View viewFindViewById = view.findViewById(R.id.banner_layout);
            int borderWidth = AppConfigUtil.getBorderWidth(view.getContext());
            viewFindViewById.setPadding(borderWidth, borderWidth, borderWidth, borderWidth);
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            Pair bannerSize = AppPresenter.getBannerSize(view.getContext());
            layoutParams.width = Math.round(((Float) bannerSize.first).floatValue());
            layoutParams.height = Math.round(((Float) bannerSize.second).floatValue());
            imageView.setLayoutParams(layoutParams);
            ViewGroup.LayoutParams layoutParams2 = imageView2.getLayoutParams();
            layoutParams2.width = Math.round(AppPresenter.getIconSize(view.getContext()));
            layoutParams2.height = Math.round(AppPresenter.getIconSize(view.getContext()));
            imageView2.setLayoutParams(layoutParams2);
            textView.setVisibility(4);
        }
    }

    private boolean isShowLabel() {
        if (AnonymousClass2.$SwitchMap$com$mw$launcher$data$AppStrategy[this.strategy.ordinal()] == 1) {
            return Config.get().pagination.favApp.showLabel;
        }
        return Config.get().pagination.appConfig.showLabel;
    }

    private static Configuration.GlobalStyle.SelectionMode getSelectMode() {
        return Config.get().globalStyle.itemSelectionMode;
    }

    private int getBorderColor() {
        if (AnonymousClass2.$SwitchMap$com$mw$launcher$data$AppStrategy$State[this.mState.ordinal()] != 2) {
            return AppConfigUtil.getItemSelectedBorderColor(this.mContext);
        }
        return ContextCompat.getColor(this.mContext, R.color.move_border_color);
    }

    /* JADX INFO: renamed from: com.vctv.launcher.app.presenter.AppPresenter$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize;
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$data$AppStrategy;
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$data$AppStrategy$State;

        static {
            int[] iArr = new int[Configuration.GlobalStyle.ItemSize.values().length];
            $SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize = iArr;
            try {
                iArr[Configuration.GlobalStyle.ItemSize.SMALL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Configuration.GlobalStyle.ItemSize.MEDIUM.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Configuration.GlobalStyle.ItemSize.LARGE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            int[] iArr2 = new int[AppStrategy.State.values().length];
            $SwitchMap$com$mw$launcher$data$AppStrategy$State = iArr2;
            try {
                iArr2[AppStrategy.State.DEFAULT.ordinal()] = 1;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$mw$launcher$data$AppStrategy$State[AppStrategy.State.MOVE.ordinal()] = 2;
            } catch (NoSuchFieldError unused5) {
            }
            int[] iArr3 = new int[AppStrategy.values().length];
            $SwitchMap$com$mw$launcher$data$AppStrategy = iArr3;
            try {
                iArr3[AppStrategy.FAV.ordinal()] = 1;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$mw$launcher$data$AppStrategy[AppStrategy.DEFAULT.ordinal()] = 2;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Pair<Float, Float> getBannerSize(Context context) {
        Resources resources = context.getResources();
        int i = AnonymousClass2.$SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Config.get().globalStyle.itemSize.ordinal()];
        if (i == 1) {
            return new Pair<>(Float.valueOf(resources.getDimension(R.dimen.app_banner_width_s)), Float.valueOf(resources.getDimension(R.dimen.app_banner_height_s)));
        }
        if (i == 2) {
            return new Pair<>(Float.valueOf(resources.getDimension(R.dimen.app_banner_width_m)), Float.valueOf(resources.getDimension(R.dimen.app_banner_height_m)));
        }
        return new Pair<>(Float.valueOf(resources.getDimension(R.dimen.app_banner_width_l)), Float.valueOf(resources.getDimension(R.dimen.app_banner_height_l)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static float getIconSize(Context context) {
        Resources resources = context.getResources();
        int i = AnonymousClass2.$SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Config.get().globalStyle.itemSize.ordinal()];
        if (i == 1) {
            return resources.getDimension(R.dimen.app_icon_size_s);
        }
        if (i == 2) {
            return resources.getDimension(R.dimen.app_icon_size_m);
        }
        return resources.getDimension(R.dimen.app_icon_size_l);
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof android.graphics.drawable.BitmapDrawable) {
            Bitmap b = ((android.graphics.drawable.BitmapDrawable) drawable).getBitmap();
            if (b != null) return b;
        }
        int w = Math.max(1, drawable.getIntrinsicWidth());
        int h = Math.max(1, drawable.getIntrinsicHeight());
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }
}
