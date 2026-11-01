package com.vctv.launcher.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import androidx.leanback.widget.HorizontalGridView;
import androidx.recyclerview.widget.RecyclerView;
import com.vctv.launcher.app.event.CancelMoveEvent;
import com.vctv.launcher.app.event.OnMoveEvent;
import com.vctv.launcher.data.AppStrategy;
import java.util.Objects;
import org.greenrobot.eventbus.EventBus;

/* JADX INFO: loaded from: classes.dex */
public class AppHorizonGridView extends HorizontalGridView {
    private AppStrategy.State mState;

    public AppHorizonGridView(Context context) {
        this(context, null, 0);
    }

    public AppHorizonGridView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppHorizonGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mState = AppStrategy.State.DEFAULT;
        setHasFixedSize(true);
        setItemViewCacheSize(4);
    }

    public void triggerMove() {
        this.mState = AppStrategy.State.MOVE;
    }

    public void cancelMove() {
        this.mState = AppStrategy.State.DEFAULT;
        setHasFixedSize(true);
        setItemViewCacheSize(4);
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.ViewParent
    public View focusSearch(View view, int i) {
        int i2;
        int childAdapterPosition = getChildAdapterPosition(view);
        if (this.mState.equals(AppStrategy.State.MOVE)) {
            if (!((RecyclerView.ItemAnimator) Objects.requireNonNull(getItemAnimator())).isRunning()) {
                if (i == 17) {
                    i2 = childAdapterPosition - 1;
                } else {
                    i2 = i == 66 ? childAdapterPosition + 1 : childAdapterPosition;
                }
                EventBus.getDefault().post(new OnMoveEvent(childAdapterPosition, i2));
            }
            return view;
        }
        return super.focusSearch(view, i);
    }

    @Override // androidx.leanback.widget.BaseGridView, android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 4) {
            if (this.mState.equals(AppStrategy.State.DEFAULT)) {
                if (getSelectedPosition() != 0) {
                    setSelectedPosition(0);
                    return true;
                }
                clearFocus();
                return true;
            }
            EventBus.getDefault().post(new CancelMoveEvent(getFocusedChild()));
            return true;
        }
        return super.dispatchKeyEvent(keyEvent);
    }
}
