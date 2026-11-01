package com.vctv.launcher.tab;

import android.os.Handler;
import android.os.Looper;
import com.vctv.launcher.R;
import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes.dex */
public class TabController {
    private static final int DEBOUNCE_DELAY_MS = 200;
    private TabContainerFragment tabContainerFragment;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final AtomicReference<TabAction> pendingAction = new AtomicReference<>(null);
    private final Runnable debounceRunnable = new Runnable() { // from class: com.vctv.launcher.tab.TabController$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            TabController.this.m196lambda$new$0$commwlaunchertabTabController();
        }
    };

    private enum TabAction {
        SHOW,
        HIDE
    }

    public TabController(TabContainerFragment tabContainerFragment) {
        this.tabContainerFragment = tabContainerFragment;
    }

    public void backToFirstScreen() {
        this.tabContainerFragment.setCurrentItem(0);
    }

    public void handleAllApps() {
        int positionById = this.tabContainerFragment.getPositionById(R.string.apps);
        if (positionById != -1) {
            this.tabContainerFragment.setCurrentItem(positionById);
        }
    }

    public void debounceTabAction(boolean z) {
        this.pendingAction.set(z ? TabAction.SHOW : TabAction.HIDE);
        this.handler.removeCallbacks(this.debounceRunnable);
        this.handler.postDelayed(this.debounceRunnable, 200L);
    }

    /* JADX INFO: renamed from: lambda$new$0$com-mw-launcher-tab-TabController, reason: not valid java name */
    /* synthetic */ void m196lambda$new$0$commwlaunchertabTabController() {
        TabAction andSet = this.pendingAction.getAndSet(null);
        if (this.tabContainerFragment == null) {
            return;
        }
        int i = AnonymousClass1.$SwitchMap$com$mw$launcher$tab$TabController$TabAction[andSet.ordinal()];
        if (i == 1) {
            this.tabContainerFragment.showTab();
        } else {
            if (i != 2) {
                return;
            }
            this.tabContainerFragment.hideTab();
        }
    }

    /* JADX INFO: renamed from: com.vctv.launcher.tab.TabController$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$tab$TabController$TabAction;

        static {
            int[] iArr = new int[TabAction.values().length];
            $SwitchMap$com$mw$launcher$tab$TabController$TabAction = iArr;
            try {
                iArr[TabAction.SHOW.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$mw$launcher$tab$TabController$TabAction[TabAction.HIDE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public void release() {
        this.handler.removeCallbacks(this.debounceRunnable);
        this.tabContainerFragment = null;
    }
}
