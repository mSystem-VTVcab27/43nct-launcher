package com.vctv.launcher.tab.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import androidx.core.view.ViewCompat;
import com.vctv.launcher.R;

/* JADX INFO: loaded from: classes.dex */
public class NotificationItem extends View {
    private static final int ANIMATION_DURATION = 200;
    private static final int BORDER_STROKE_WIDTH = 4;
    public static final int READ = 1;
    public static final int UNREAD = 0;
    private final Paint backgroundPaint;
    private int bgColorAnimationValue;
    private final Paint borderPaint;
    private float centerX;
    private float centerY;
    private final Paint clearPaint;
    private boolean focused;
    private String hintText;
    private int itemFocusedColor;
    private final Paint paint;
    private float radius;
    private float radiusAnimationValue;
    private int status;
    private String text;
    private final Rect textBounds;
    private int textColor;
    private final Paint textPaint;
    private float textSize;

    public NotificationItem(Context context) {
        super(context);
        this.text = "0";
        this.backgroundPaint = new Paint(1);
        this.textPaint = new Paint(1);
        this.clearPaint = new Paint(1);
        this.borderPaint = new Paint(1);
        this.paint = new Paint(1);
        this.bgColorAnimationValue = 0;
        this.radiusAnimationValue = 0.0f;
        this.status = 1;
        this.textBounds = new Rect();
        init();
    }

    public NotificationItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.text = "0";
        this.backgroundPaint = new Paint(1);
        this.textPaint = new Paint(1);
        this.clearPaint = new Paint(1);
        this.borderPaint = new Paint(1);
        this.paint = new Paint(1);
        this.bgColorAnimationValue = 0;
        this.radiusAnimationValue = 0.0f;
        this.status = 1;
        this.textBounds = new Rect();
        initAttrs(context, attributeSet);
        init();
    }

    public NotificationItem(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.text = "0";
        this.backgroundPaint = new Paint(1);
        this.textPaint = new Paint(1);
        this.clearPaint = new Paint(1);
        this.borderPaint = new Paint(1);
        this.paint = new Paint(1);
        this.bgColorAnimationValue = 0;
        this.radiusAnimationValue = 0.0f;
        this.status = 1;
        this.textBounds = new Rect();
        initAttrs(context, attributeSet);
        init();
    }

    public void setTextColor(int i) {
        this.textColor = i;
    }

    public void setItemFocusedColor(int i) {
        this.itemFocusedColor = i;
    }

    private void init() {
        this.radius = (Math.min(getWidth(), getHeight()) / 2.0f) - 20.0f;
        this.centerX = getWidth() / 2.0f;
        this.centerY = getHeight() / 2.0f;
        this.backgroundPaint.setColor(this.textColor);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint.setColor(this.textColor);
        this.textPaint.setTextSize(this.textSize * getResources().getDisplayMetrics().density);
        this.clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.clearPaint.setTextAlign(Paint.Align.CENTER);
        this.clearPaint.setTextSize(this.textPaint.getTextSize());
        this.borderPaint.setColor(this.textColor);
        this.borderPaint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setStrokeWidth(4.0f);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.radius = (Math.min(i, i2) / 2.0f) - 20.0f;
        this.centerX = i / 2.0f;
        this.centerY = i2 / 2.0f;
    }

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.NotificationItem);
        try {
            this.textSize = typedArrayObtainStyledAttributes.getDimension(R.styleable.NotificationItem_android_textSize, 14.0f);
            this.textColor = typedArrayObtainStyledAttributes.getColor(R.styleable.NotificationItem_android_textColor, -7829368);
            this.hintText = typedArrayObtainStyledAttributes.getString(R.styleable.NotificationItem_android_hint);
            this.itemFocusedColor = typedArrayObtainStyledAttributes.getColor(R.styleable.NotificationItem_ItemFocusedColor, -1);
            if (typedArrayObtainStyledAttributes != null) {
                typedArrayObtainStyledAttributes.recycle();
            }
        } catch (Throwable th) {
            if (typedArrayObtainStyledAttributes != null) {
                try {
                    typedArrayObtainStyledAttributes.recycle();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public String getHintText() {
        return this.hintText;
    }

    public void setCount(int i) {
        this.text = String.valueOf(i);
        invalidate();
    }

    public int getCount() {
        return Integer.parseInt(this.text);
    }

    public void setStatus(int i) {
        this.status = i;
        updatePainter(this.focused);
        invalidate();
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        updatePainter(z);
        animateFocusEffect(z);
        this.focused = z;
    }

    private void updatePainter(boolean z) {
        if (z) {
            if (this.status == 0) {
                this.backgroundPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
                this.clearPaint.setXfermode(null);
                this.clearPaint.setColor(-1);
                return;
            } else {
                this.borderPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
                this.textPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
                return;
            }
        }
        init();
    }

    private void animateFocusEffect(final boolean z) {
        float fMinInitial = Math.min(getWidth(), getHeight()) / 2.0f;
        float f = z ? fMinInitial - 10.0f : fMinInitial;
        final float fMin = z ? fMinInitial : fMinInitial - 10.0f;
        int i = z ? this.textColor : this.itemFocusedColor;
        int i2 = z ? this.itemFocusedColor : this.textColor;
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(f, fMin);
        valueAnimatorOfFloat.setDuration(200L);
        valueAnimatorOfFloat.setInterpolator(new DecelerateInterpolator());
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.vctv.launcher.tab.view.NotificationItem$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NotificationItem.this.m201xb5f195c5(z, fMin, valueAnimator);
            }
        });
        ValueAnimator valueAnimatorOfObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(i), Integer.valueOf(i2));
        valueAnimatorOfObject.setDuration(200L);
        valueAnimatorOfObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.vctv.launcher.tab.view.NotificationItem$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                NotificationItem.this.m202x9b330486(valueAnimator);
            }
        });
        valueAnimatorOfObject.start();
        valueAnimatorOfFloat.start();
    }

    /* JADX INFO: renamed from: lambda$animateFocusEffect$0$com-mw-launcher-tab-view-NotificationItem, reason: not valid java name */
    /* synthetic */ void m201xb5f195c5(boolean z, float f, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.radiusAnimationValue = fFloatValue;
        if (!z && fFloatValue == f) {
            this.radiusAnimationValue = 0.0f;
        }
        invalidate();
    }

    /* JADX INFO: renamed from: lambda$animateFocusEffect$1$com-mw-launcher-tab-view-NotificationItem, reason: not valid java name */
    /* synthetic */ void m202x9b330486(ValueAnimator valueAnimator) {
        this.bgColorAnimationValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int iSaveLayer = canvas.saveLayer(null, null);
        this.paint.setColor(this.bgColorAnimationValue);
        canvas.drawCircle(this.centerX, this.centerY, this.radiusAnimationValue, this.paint);
        Paint paint = this.textPaint;
        String str = this.text;
        paint.getTextBounds(str, 0, str.length(), this.textBounds);
        float fExactCenterY = this.centerY - this.textBounds.exactCenterY();
        if (this.status == 0) {
            canvas.drawCircle(this.centerX, this.centerY, this.radius, this.backgroundPaint);
            canvas.drawText(this.text, this.centerX, fExactCenterY, this.clearPaint);
        } else {
            canvas.drawCircle(this.centerX, this.centerY, this.radius, this.borderPaint);
            canvas.drawText(this.text, this.centerX, fExactCenterY, this.textPaint);
        }
        canvas.restoreToCount(iSaveLayer);
    }
}
