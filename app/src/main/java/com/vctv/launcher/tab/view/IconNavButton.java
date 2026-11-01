package com.vctv.launcher.tab.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.view.ViewCompat;
import com.vctv.launcher.R;

/* JADX INFO: loaded from: classes.dex */
public class IconNavButton extends FrameLayout {
    private int bgColorAnimationValue;
    private int focusColor;
    private int iconColor;
    private ImageView mIcon;
    private float maxRadius;
    private float minRadius;
    private final Paint paint;
    private float radiusAnimationValue;

    public IconNavButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
        initAttrs(context, attributeSet);
        setClickable(true);
        Paint paint = new Paint();
        this.paint = paint;
        paint.setColor(-1);
    }

    public void setFocusColor(int i) {
        this.focusColor = i;
    }

    private void initView(Context context) {
        this.mIcon = (ImageView) LayoutInflater.from(context).inflate(R.layout.icon_nav_button_layout, this).findViewById(R.id.iv_icon);
    }

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.IconNavButton);
        try {
            this.mIcon.setImageResource(typedArrayObtainStyledAttributes.getResourceId(R.styleable.IconNavButton_src, 0));
            int color = typedArrayObtainStyledAttributes.getColor(R.styleable.IconNavButton_iconBackground, getResources().getColor(R.color.gray_default, getContext().getTheme()));
            this.iconColor = color;
            this.mIcon.setColorFilter(color);
            this.focusColor = typedArrayObtainStyledAttributes.getColor(R.styleable.IconNavButton_focusedColor, -1);
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

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        this.paint.setColor(this.bgColorAnimationValue);
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, this.radiusAnimationValue, this.paint);
        super.dispatchDraw(canvas);
    }

    private void animateFocusEffect(final boolean z) {
        float f = z ? this.minRadius : this.maxRadius;
        final float f2 = z ? this.maxRadius : this.minRadius;
        int i = z ? this.iconColor : this.focusColor;
        int i2 = z ? this.focusColor : this.iconColor;
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(f, f2);
        valueAnimatorOfFloat.setDuration(200L);
        valueAnimatorOfFloat.setInterpolator(new DecelerateInterpolator());
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.vctv.launcher.tab.view.IconNavButton$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                IconNavButton.this.m198xca3f8ff5(z, f2, valueAnimator);
            }
        });
        ValueAnimator valueAnimatorOfObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(i), Integer.valueOf(i2));
        valueAnimatorOfObject.setDuration(200L);
        valueAnimatorOfObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.vctv.launcher.tab.view.IconNavButton$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                IconNavButton.this.m199x215d80d4(valueAnimator);
            }
        });
        valueAnimatorOfObject.start();
        valueAnimatorOfFloat.start();
    }

    /* JADX INFO: renamed from: lambda$animateFocusEffect$0$com-mw-launcher-tab-view-IconNavButton, reason: not valid java name */
    /* synthetic */ void m198xca3f8ff5(boolean z, float f, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.radiusAnimationValue = fFloatValue;
        if (!z && fFloatValue == f) {
            this.radiusAnimationValue = 0.0f;
        }
        invalidate();
    }

    /* JADX INFO: renamed from: lambda$animateFocusEffect$1$com-mw-launcher-tab-view-IconNavButton, reason: not valid java name */
    /* synthetic */ void m199x215d80d4(ValueAnimator valueAnimator) {
        this.bgColorAnimationValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        invalidate();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        float fMax = Math.max(i, i2) / 2.0f;
        this.maxRadius = fMax;
        this.minRadius = fMax * 0.5f;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(new Runnable() { // from class: com.vctv.launcher.tab.view.IconNavButton$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                IconNavButton.this.m200x75f97b4d();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$onAttachedToWindow$2$com-mw-launcher-tab-view-IconNavButton, reason: not valid java name */
    /* synthetic */ void m200x75f97b4d() {
        float fMax = Math.max(getWidth(), getHeight()) / 2.0f;
        this.maxRadius = fMax;
        this.minRadius = fMax * 0.5f;
        if (!hasFocus() || this.radiusAnimationValue == this.maxRadius) {
            return;
        }
        animateFocusEffect(true);
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        animateFocusEffect(z);
        if (z) {
            this.mIcon.setColorFilter(ViewCompat.MEASURED_STATE_MASK);
        } else {
            this.mIcon.setColorFilter(this.iconColor);
        }
        super.onFocusChanged(z, i, rect);
    }

    public void setIconColor(int i) {
        this.iconColor = i;
        this.mIcon.setColorFilter(i);
    }
}
