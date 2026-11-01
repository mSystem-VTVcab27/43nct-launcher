package com.vctv.launcher.home;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import androidx.core.content.ContextCompat;
import androidx.core.internal.view.SupportMenu;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.VerticalGridView;
import com.vctv.launcher.R;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.data.ChannelData;
import com.vctv.launcher.data.ChannelDateManager;
import com.vctv.launcher.util.ViewUtils;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/* JADX INFO: loaded from: classes.dex */
public class SortChannelVerticalGridView extends VerticalGridView {
    public static final int MOVE_DONE = 27;
    public static String TAG = "SortChannelVerticalGridView";
    ArrayObjectAdapter arrayObjectAdapter;
    ChannelData channelDataOfMoveView;
    private ChannelDateManager channelDateManager;
    private List<ChannelData> displayChannel;
    private boolean isMoveStatus;
    MoveChannelListener moveChannelListener;
    private LinearLayout moveDownButton;
    private LinearLayout moveUpButton;
    ValueAnimator moveViewAnimator;
    View needMoveView;
    private PopupWindow popupWindow;

    public interface MoveChannelListener {
        void moveChannel();
    }

    private class MoveButtonOnClickListener implements View.OnClickListener {
        int direction;

        public MoveButtonOnClickListener(int i) {
            this.direction = i;
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (SortChannelVerticalGridView.this.needMoveView != null && SortChannelVerticalGridView.this.needMoveView.isSelected() && SortChannelVerticalGridView.this.isMoveStatus) {
                int i = this.direction;
                if (i == 27) {
                    SortChannelVerticalGridView.this.popupWindow.dismiss();
                } else if (i == 33) {
                    SortChannelVerticalGridView.this.swapItemsIfNeeded(33);
                } else if (i == 130) {
                    SortChannelVerticalGridView.this.swapItemsIfNeeded(130);
                }
                SortChannelVerticalGridView.this.updateMoveButtonState();
            }
        }
    }

    public SortChannelVerticalGridView(Context context) {
        super(context);
        this.isMoveStatus = false;
    }

    public SortChannelVerticalGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isMoveStatus = false;
    }

    public SortChannelVerticalGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.isMoveStatus = false;
    }

    public void switchMoveStatus(View view, ChannelData channelData) {
        if (!this.isMoveStatus) {
            Context context = view.getContext();
            this.isMoveStatus = true;
            this.needMoveView = view;
            view.setSelected(true);
            setMoveStatusViewUI(view);
            this.channelDataOfMoveView = channelData;
            if (isTalkBackEnabled(context)) {
                showMoveButtonInTalkBack(context, view);
                return;
            }
            return;
        }
        this.isMoveStatus = false;
        this.moveViewAnimator.cancel();
        this.moveViewAnimator = null;
        this.channelDataOfMoveView = null;
        removeMoveView();
    }

    private void setMoveStatusViewUI(View view) {
        int itemSelectedBorderColor;
        Context context = view.getContext();
        Drawable background = view.getBackground();
        final GradientDrawable gradientDrawableCreateDrawable = ViewUtils.createDrawable(view.getContext(), 0, true, false);
        view.setBackground(new LayerDrawable(new Drawable[]{background, gradientDrawableCreateDrawable}));
        final int iDpToPx = ViewUtils.dpToPx(context, Config.get().globalStyle.itemBorderWidth + 1);
        try {
            itemSelectedBorderColor = ViewUtils.getItemSelectedBorderColor(context);
        } catch (IllegalArgumentException unused) {
            Log.e("TAG", "Invalid border color: " + ViewUtils.getItemSelectedBorderColor(context));
            itemSelectedBorderColor = SupportMenu.CATEGORY_MASK;
        }
        ValueAnimator valueAnimatorOfArgb = ValueAnimator.ofArgb(itemSelectedBorderColor, 0, itemSelectedBorderColor);
        this.moveViewAnimator = valueAnimatorOfArgb;
        valueAnimatorOfArgb.setDuration(1000L);
        this.moveViewAnimator.setRepeatMode(2);
        this.moveViewAnimator.setRepeatCount(-1);
        this.moveViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.vctv.launcher.home.SortChannelVerticalGridView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                gradientDrawableCreateDrawable.setStroke(iDpToPx, ((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        });
        this.moveViewAnimator.start();
    }

    public boolean isMoveStatus() {
        return this.isMoveStatus;
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.ViewParent
    public View focusSearch(View view, int i) {
        if (view.isSelected() && this.isMoveStatus) {
            swapItemsIfNeeded(i);
        }
        return super.focusSearch(view, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void swapItemsIfNeeded(int i) {
        int positionOfMoveView = getPositionOfMoveView();
        if (positionOfMoveView == -1) {
            return;
        }
        if (i == 33) {
            if (positionOfMoveView <= 0 || positionOfMoveView >= this.displayChannel.size()) {
                return;
            }
            int i2 = positionOfMoveView - 1;
            ChannelData channelData = this.displayChannel.get(i2);
            if (!channelData.isPreset() || channelData.isMovable()) {
                swapItem(positionOfMoveView, i2);
                return;
            }
            return;
        }
        if (i == 130 && positionOfMoveView >= 0 && positionOfMoveView < this.displayChannel.size() - 1) {
            swapItem(positionOfMoveView, positionOfMoveView + 1);
        }
    }

    private void swapItem(int i, int i2) {
        this.channelDateManager.handleMoveChannel(i, i2);
        this.arrayObjectAdapter.move(i, i2);
        MoveChannelListener moveChannelListener = this.moveChannelListener;
        if (moveChannelListener != null) {
            moveChannelListener.moveChannel();
        }
        EventBus.getDefault().post(Integer.valueOf(ChannelContentView.PROGRAM_INFO_VISIBLE_EVENT));
    }

    public void setDisplayChannel(List<ChannelData> list) {
        this.displayChannel = list;
    }

    public void setArrayObjectAdapter(ArrayObjectAdapter arrayObjectAdapter) {
        this.arrayObjectAdapter = arrayObjectAdapter;
    }

    @Override // androidx.leanback.widget.BaseGridView, android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        View view;
        if (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 4 && (view = this.needMoveView) != null && view.isSelected() && this.isMoveStatus) {
            if (isTalkBackEnabled(this.needMoveView.getContext())) {
                this.popupWindow.dismiss();
            }
            removeMoveView();
            this.isMoveStatus = false;
            return true;
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public void removeMoveView() {
        View view = this.needMoveView;
        if (view == null) {
            return;
        }
        view.setSelected(false);
        this.needMoveView.setBackgroundResource(R.drawable.custom_channel_focus_background);
        this.needMoveView = null;
    }

    public void setChannelDateManager(ChannelDateManager channelDateManager) {
        this.channelDateManager = channelDateManager;
    }

    public void setMoveChannelListener(MoveChannelListener moveChannelListener) {
        this.moveChannelListener = moveChannelListener;
    }

    private boolean isTalkBackEnabled(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        return accessibilityManager != null && accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled();
    }

    private void showMoveButtonInTalkBack(Context context, View view) {
        View viewInflate = LayoutInflater.from(context).inflate(R.layout.move_channel_button, (ViewGroup) null);
        PopupWindow popupWindow = new PopupWindow(viewInflate, -2, -2, true);
        this.popupWindow = popupWindow;
        popupWindow.showAtLocation(view, 81, 0, 0);
        this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: com.vctv.launcher.home.SortChannelVerticalGridView.1
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {
                SortChannelVerticalGridView.this.removeMoveView();
                SortChannelVerticalGridView.this.isMoveStatus = false;
            }
        });
        this.moveUpButton = (LinearLayout) viewInflate.findViewById(R.id.move_up_layout);
        this.moveDownButton = (LinearLayout) viewInflate.findViewById(R.id.move_down_layout);
        LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.move_done_layout);
        this.moveUpButton.setOnClickListener(new MoveButtonOnClickListener(33));
        this.moveDownButton.setOnClickListener(new MoveButtonOnClickListener(130));
        linearLayout.setOnClickListener(new MoveButtonOnClickListener(27));
        updateMoveButtonState();
        this.popupWindow.showAsDropDown(view);
    }

    private int getPositionOfMoveView() {
        ChannelData channelData = this.channelDataOfMoveView;
        if (channelData == null) {
            Log.i(TAG, "channelDataOfMoveView is null!!!");
            return -1;
        }
        return channelData.getPosition();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMoveButtonState() {
        int positionOfMoveView = getPositionOfMoveView();
        int size = this.displayChannel.size();
        setButtonState(this.moveUpButton, positionOfMoveView == 0);
        setButtonState(this.moveDownButton, positionOfMoveView == size - 1);
    }

    private void setButtonState(LinearLayout linearLayout, boolean z) {
        boolean zIsEnabled = linearLayout.isEnabled();
        boolean z2 = z && zIsEnabled;
        boolean z3 = (z || zIsEnabled) ? false : true;
        if (z2) {
            linearLayout.setEnabled(false);
            linearLayout.setBackgroundColor(ContextCompat.getColor(linearLayout.getContext(), R.color.move_button_disable_color));
            setButtonContentDescription(linearLayout, true);
        } else if (z3) {
            linearLayout.setEnabled(true);
            linearLayout.setBackgroundColor(ContextCompat.getColor(linearLayout.getContext(), R.color.move_button_enable_color));
            setButtonContentDescription(linearLayout, false);
        }
        linearLayout.setImportantForAccessibility(1);
    }

    private void setButtonContentDescription(LinearLayout linearLayout, boolean z) {
        int i;
        if (linearLayout.getId() == R.id.move_up_layout) {
            i = z ? R.string.move_up_button__disable_Description : R.string.move_up_button;
        } else {
            i = z ? R.string.move_down_button__disable_Description : R.string.move_down_button;
        }
        linearLayout.setContentDescription(ContextCompat.getString(linearLayout.getContext(), i));
    }
}
