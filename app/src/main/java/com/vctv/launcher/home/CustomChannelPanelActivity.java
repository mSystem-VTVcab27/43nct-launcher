package com.vctv.launcher.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.vctv.launcher.R;
import com.vctv.launcher.data.ChannelDateManager;

/* JADX INFO: loaded from: classes.dex */
public class CustomChannelPanelActivity extends AppCompatActivity {
    public static int FOR_DISPLAY_CHANNEL = 0;
    public static int FOR_SORT_CHANNEL = 1;
    public static String START_INTENT = "startIntent";
    ChannelDateManager channelDateManager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private LinearLayout linearLayout;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_custom_channel_panel);
        this.linearLayout = (LinearLayout) findViewById(R.id.notifications_panel_view);
        this.channelDateManager = ChannelDateManager.getInstance(this);
        if (getIntent().getIntExtra(START_INTENT, FOR_DISPLAY_CHANNEL) == FOR_SORT_CHANNEL) {
            replaceFragment(new SortChannelFragment());
        } else {
            replaceFragment(new AllAppChannelSetFragment());
        }
    }

    private void replaceFragment(final Fragment fragment) {
        final FragmentManager supportFragmentManager = getSupportFragmentManager();
        this.handler.postDelayed(new Runnable() { // from class: com.vctv.launcher.home.CustomChannelPanelActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CustomChannelPanelActivity.lambda$replaceFragment$0(supportFragmentManager, fragment);
            }
        }, 100L);
        this.handler.postDelayed(new Runnable() { // from class: com.vctv.launcher.home.CustomChannelPanelActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                CustomChannelPanelActivity.this.m186xd852a489();
            }
        }, 600L);
    }

    static /* synthetic */ void lambda$replaceFragment$0(FragmentManager fragmentManager, Fragment fragment) {
        if (fragmentManager.isDestroyed()) {
            return;
        }
        FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
        fragmentTransactionBeginTransaction.setCustomAnimations(R.anim.slide_in_right, 0);
        fragmentTransactionBeginTransaction.replace(R.id.side_fragment_container, fragment);
        fragmentTransactionBeginTransaction.commit();
    }

    /* JADX INFO: renamed from: lambda$replaceFragment$1$com-mw-launcher-home-CustomChannelPanelActivity, reason: not valid java name */
    /* synthetic */ void m186xd852a489() {
        this.linearLayout.setBackgroundResource(R.drawable.side_fragment_bg);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            View viewFindViewById = findViewById(R.id.notifications_panel_view);
            viewFindViewById.animate().translationX(viewFindViewById.getWidth()).alpha(0.0f).setDuration(300L).withEndAction(new Runnable() { // from class: com.vctv.launcher.home.CustomChannelPanelActivity$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CustomChannelPanelActivity.this.finish();
                }
            }).start();
        } else {
            super.onBackPressed();
        }
    }
}
