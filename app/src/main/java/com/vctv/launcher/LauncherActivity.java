package com.vctv.launcher;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.palette.graphics.Palette;
import com.vctv.launcher.app.AppManager;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.home.ProgramItemFocusEvent;
import com.vctv.launcher.tab.TabContainerFragment;
import com.vctv.launcher.tab.TabController;
import com.vctv.launcher.tab.TabVisibilityListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/* JADX INFO: loaded from: classes.dex */
public class LauncherActivity extends AppCompatActivity implements TabVisibilityListener {
    private static final String TAG = "LauncherActivity";
    private ValueAnimator colorAnimation;
    private GradientDrawable intermediateGradientDrawable;
    private Intent mAppManagerIntent;
    private View rootFrameLayout;
    private TabController tabController;
    private int transitionSpeed;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_launcher);
        View viewFindViewById = findViewById(R.id.main);
        this.rootFrameLayout = viewFindViewById;
        initBackground(viewFindViewById);
        this.transitionSpeed = Config.get().globalStyle.transitionSpeed;
        if (Config.get().globalStyle.dynamicEffect) {
            EventBus.getDefault().register(this);
            GradientDrawable gradientDrawable = new GradientDrawable();
            this.intermediateGradientDrawable = gradientDrawable;
            gradientDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            this.intermediateGradientDrawable.setGradientType(0);
            this.intermediateGradientDrawable.setDither(true);
            this.intermediateGradientDrawable.setCornerRadius(0.0f);
        }
        TabContainerFragment tabContainerFragmentNewInstance = TabContainerFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.main, tabContainerFragmentNewInstance).commit();
        this.tabController = new TabController(tabContainerFragmentNewInstance);
        Intent intent = new Intent(this, (Class<?>) AppManager.class);
        this.mAppManagerIntent = intent;
        startService(intent);
        checkTvPermissions();
    }

    private void checkTvPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String[] perms = new String[]{
                "android.permission.READ_TV_LISTINGS",
                "com.android.providers.tv.permission.READ_EPG_DATA",
                "com.android.providers.tv.permission.WRITE_EPG_DATA"
            };
            java.util.List<String> needed = new java.util.ArrayList<>();
            for (String perm : perms) {
                if (checkSelfPermission(perm) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    needed.add(perm);
                }
            }
            if (!needed.isEmpty()) {
                requestPermissions(needed.toArray(new String[0]), 1001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            boolean granted = false;
            for (int result : grantResults) {
                if (result == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                    break;
                }
            }
            if (granted) {
                com.vctv.launcher.data.ChannelDateManager.getInstance(this).reloadChannels();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission("android.permission.READ_TV_LISTINGS") == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                com.vctv.launcher.data.ChannelDateManager cdm = com.vctv.launcher.data.ChannelDateManager.getInstance(this);
                if (cdm.getDisplayChannelDataListNeedSort().isEmpty()) {
                    cdm.reloadChannels();
                }
            }
        }
    }

    private void initBackground(View view) {
        Configuration configuration = Config.get();
        if (view != null) {
            view.setBackgroundColor(configuration.globalStyle.getBackgroundColor(this));
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ("android.intent.action.ALL_APPS".equals(intent.getAction())) {
            this.tabController.handleAllApps();
        } else if ("android.intent.action.MAIN".equals(intent.getAction())) {
            this.tabController.backToFirstScreen();
        }
    }

    @Override
    public void onBackPressed() {
        if (this.tabController != null) {
            this.tabController.backToFirstScreen();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (Config.get().globalStyle.dynamicEffect) {
            EventBus.getDefault().unregister(this);
            ValueAnimator valueAnimator = this.colorAnimation;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                this.colorAnimation.cancel();
            }
        }
        this.tabController.release();
        stopService(this.mAppManagerIntent);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (this.colorAnimation != null && this.colorAnimation.isRunning()) {
            this.colorAnimation.cancel();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFocusEvent(ProgramItemFocusEvent programItemFocusEvent) {
        android.app.ActivityManager am = (android.app.ActivityManager) getSystemService(android.content.Context.ACTIVITY_SERVICE);
        if (am != null && am.isLowRamDevice()) {
            return;
        }
        Bitmap bitmap;
        if (programItemFocusEvent.hasFocus() && (programItemFocusEvent.getView() instanceof ImageView)) {
            Drawable drawable = ((ImageView) programItemFocusEvent.getView()).getDrawable();
            if (!(drawable instanceof BitmapDrawable) || (bitmap = ((BitmapDrawable) drawable).getBitmap()) == null || bitmap.isRecycled()) {
                return;
            }
            if (this.colorAnimation != null && this.colorAnimation.isRunning()) {
                this.colorAnimation.cancel();
            }
            Palette.from(bitmap).maximumColorCount(8).resizeBitmapArea(48 * 48).generate(new Palette.PaletteAsyncListener() { // from class: com.vctv.launcher.LauncherActivity$$ExternalSyntheticLambda1
                @Override // androidx.palette.graphics.Palette.PaletteAsyncListener
                public final void onGenerated(Palette palette) {
                    LauncherActivity.this.m161lambda$onFocusEvent$1$commwlauncherLauncherActivity(palette);
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$onFocusEvent$1$com-mw-launcher-LauncherActivity, reason: not valid java name */
    /* synthetic */ void m161lambda$onFocusEvent$1$commwlauncherLauncherActivity(Palette palette) {
        int[] colors;
        if (palette == null || isFinishing() || isDestroyed()) {
            return;
        }
        int iBlendColor = blendColor(palette.getDominantColor(ViewCompat.MEASURED_STATE_MASK), -1, 0.05f);
        int i = 0;
        if ((this.rootFrameLayout.getBackground() instanceof GradientDrawable) && (colors = ((GradientDrawable) this.rootFrameLayout.getBackground()).getColors()) != null && colors.length > 0) {
            i = colors[0];
        }
        ValueAnimator valueAnimatorOfObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(i), Integer.valueOf(iBlendColor));
        this.colorAnimation = valueAnimatorOfObject;
        valueAnimatorOfObject.setDuration(this.transitionSpeed);
        this.colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.vctv.launcher.LauncherActivity$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                LauncherActivity.this.m160lambda$onFocusEvent$0$commwlauncherLauncherActivity(valueAnimator);
            }
        });
        this.colorAnimation.start();
    }

    /* JADX INFO: renamed from: lambda$onFocusEvent$0$com-mw-launcher-LauncherActivity, reason: not valid java name */
    /* synthetic */ void m160lambda$onFocusEvent$0$commwlauncherLauncherActivity(ValueAnimator valueAnimator) {
        int iIntValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.intermediateGradientDrawable.setColors(new int[]{iIntValue, blendColor(iIntValue, -1, 0.2f), blendColor(iIntValue, -1, 0.5f)});
        this.rootFrameLayout.setBackground(this.intermediateGradientDrawable);
    }

    private int blendColor(int i, int i2, float f) {
        float f2 = 1.0f - f;
        return Color.rgb((int) ((Color.red(i) * f2) + (Color.red(i2) * f)), (int) ((Color.green(i) * f2) + (Color.green(i2) * f)), (int) ((Color.blue(i) * f2) + (Color.blue(i2) * f)));
    }

    @Override // com.vctv.launcher.tab.TabVisibilityListener
    public void onFocusInFirstRow() {
        this.tabController.debounceTabAction(true);
    }

    @Override // com.vctv.launcher.tab.TabVisibilityListener
    public void onFocusOutOfFirstRow() {
        this.tabController.debounceTabAction(false);
    }
}
