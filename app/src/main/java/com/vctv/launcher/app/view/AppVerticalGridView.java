package com.vctv.launcher.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;
import com.vctv.launcher.app.event.CancelMoveEvent;
import com.vctv.launcher.app.event.OnMoveEvent;
import com.vctv.launcher.data.AppStrategy;
import com.vctv.launcher.util.AppConfigUtil;
import java.util.Objects;
import org.greenrobot.eventbus.EventBus;

/* JADX INFO: loaded from: classes.dex */
public class AppVerticalGridView extends VerticalGridView {
    private int columnNumber;
    private AppStrategy.State mState;

    public AppVerticalGridView(Context context) {
        this(context, null, 0);
    }

    public AppVerticalGridView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AppVerticalGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.columnNumber = 1;
        this.mState = AppStrategy.State.DEFAULT;
        setHasFixedSize(true);
        setItemViewCacheSize(4);
    }

    public void setColumnNumber(int i) {
        setNumColumns(i);
        this.columnNumber = i;
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
        View viewFindNextFocusedView = findNextFocusedView(view, i);
        if (!this.mState.equals(AppStrategy.State.MOVE)) {
            return viewFindNextFocusedView;
        }
        int childAdapterPosition = getChildAdapterPosition(view);
        int childAdapterPosition2 = getChildAdapterPosition(viewFindNextFocusedView);
        if (!((RecyclerView.ItemAnimator) Objects.requireNonNull(getItemAnimator())).isRunning()) {
            EventBus.getDefault().post(new OnMoveEvent(childAdapterPosition, childAdapterPosition2));
        }
        return view;
    }

    private View findNextFocusedView(View view, int i) {
        int childAdapterPosition = getChildAdapterPosition(view);
        int itemCount = ((RecyclerView.Adapter) Objects.requireNonNull(getAdapter())).getItemCount();
        int i2 = this.columnNumber;
        int i3 = itemCount % i2 == 0 ? itemCount / i2 : (itemCount / i2) + 1;
        int i4 = childAdapterPosition + 1;
        int i5 = i4 % i2 == 0 ? i4 / i2 : (i4 / i2) + 1;
        if (i != 17) {
            if (i != 33) {
                if (i != 66) {
                    if (i == 130 && i5 == i3 - 1 && i4 + i2 > itemCount) {
                        return getChildAt(itemCount - 1);
                    }
                } else if ((i4 % i2 == 0 || i4 == itemCount) && !AppConfigUtil.isTalkBackEnabled(view.getContext())) {
                    return view;
                }
            } else if (childAdapterPosition < i2 && this.mState.equals(AppStrategy.State.MOVE)) {
                return view;
            }
        } else if (childAdapterPosition % i2 == 0) {
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
