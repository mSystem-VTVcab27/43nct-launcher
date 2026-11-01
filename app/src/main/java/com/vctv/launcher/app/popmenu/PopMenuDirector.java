package com.vctv.launcher.app.popmenu;

import com.vctv.launcher.app.event.OnMoveEvent;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.data.AppStrategy;
import com.vctv.launcher.util.AppConfigUtil;
import org.greenrobot.eventbus.EventBus;

/* JADX INFO: loaded from: classes.dex */
public class PopMenuDirector {
    public static final int DOWN = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int UP = 0;
    private final int mCount;
    private int mCurPos;
    private final int mMaxPos;
    private final int mMinPos;
    private final AppStrategy mStrategy;

    public PopMenuDirector(AppStrategy appStrategy, int i, int i2, int i3) {
        this.mStrategy = appStrategy;
        this.mCurPos = i2;
        this.mCount = i;
        this.mMinPos = appStrategy.equals(AppStrategy.DEFAULT) ? 0 : i3;
        this.mMaxPos = appStrategy.equals(AppStrategy.DEFAULT) ? i - 1 : i - 2;
    }

    private int getNextPos(int i) {
        int appSize = AppConfigUtil.getAppSize();
        int i2 = this.mCount;
        int i3 = i2 / appSize;
        if (i2 % appSize != 0) {
            i3++;
        }
        int i4 = this.mCurPos;
        int i5 = (i4 + 1) / appSize;
        if ((i4 + 1) % appSize != 0) {
            i5++;
        }
        if (i == 0) {
            return i4 - appSize;
        }
        if (i == 1) {
            return (i5 != i3 - 1 || (i4 + 1) + appSize <= i2) ? i4 + appSize : i2 - 1;
        }
        if (i == 2) {
            int i6 = AnonymousClass1.$SwitchMap$com$mw$launcher$config$Configuration$Direction[AppConfigUtil.getDirection(this.mStrategy).ordinal()];
            if (i6 == 1) {
                int i7 = this.mCurPos;
                return i7 % appSize != 0 ? i7 - 1 : i7;
            }
            if (i6 == 2) {
                return this.mCurPos - 1;
            }
        } else {
            if (i == 3) {
            }
            return this.mCurPos;
        }
        int i8 = AnonymousClass1.$SwitchMap$com$mw$launcher$config$Configuration$Direction[AppConfigUtil.getDirection(this.mStrategy).ordinal()];
        if (i8 == 1) {
            int i9 = this.mCurPos;
            return ((i9 + 1) % appSize == 0 && i9 != 0) ? i9 : i9 + 1;
        }
        if (i8 == 2) {
            return this.mCurPos + 1;
        }
        return this.mCurPos;
    }

    /* JADX INFO: renamed from: com.vctv.launcher.app.popmenu.PopMenuDirector$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$config$Configuration$Direction;

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
        }
    }

    public void onMoveNext(int i) {
        int nextPos = getNextPos(i);
        EventBus.getDefault().post(new OnMoveEvent(this.mCurPos, nextPos));
        this.mCurPos = nextPos;
    }

    public boolean canMoveNext(int i) {
        int nextPos = getNextPos(i);
        int i2 = this.mCurPos;
        return i2 != nextPos && AppConfigUtil.inRange(i2, this.mMinPos, this.mMaxPos) && AppConfigUtil.inRange(nextPos, this.mMinPos, this.mMaxPos);
    }
}
