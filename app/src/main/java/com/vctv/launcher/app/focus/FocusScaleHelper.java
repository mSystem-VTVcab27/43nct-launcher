package com.vctv.launcher.app.focus;

import android.animation.TimeAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/* JADX INFO: loaded from: classes.dex */
public class FocusScaleHelper {

    public static class ItemFocusScale implements FocusHighlightHandler {
        private static final int DEFAULT_DURATION = 200;
        private static final float DEFAULT_ZOOM_SCALE = 1.2f;
        private final int duration;
        private final float zoomScale;

        public ItemFocusScale(float f, int i) {
            this.zoomScale = ((double) f) == 0.0d ? DEFAULT_ZOOM_SCALE : f;
            this.duration = i == 0 ? 200 : i;
        }

        @Override // com.vctv.launcher.app.focus.FocusHighlightHandler
        public void onItemFocused(View view, boolean z) {
            getOrCreateAnimator(view).animateFocus(z, false);
        }

        private FocusAnimator getOrCreateAnimator(View view) {
            FocusAnimator focusAnimator = (FocusAnimator) view.getTag();
            if (focusAnimator != null) {
                return focusAnimator;
            }
            FocusAnimator focusAnimator2 = new FocusAnimator(view, this.zoomScale, this.duration);
            view.setTag(focusAnimator2);
            return focusAnimator2;
        }
    }

    private static class FocusAnimator implements TimeAnimator.TimeListener {
        private final TimeAnimator mAnimator;
        private final int mDuration;
        private float mFocusLevel = 0.0f;
        private float mFocusLevelDelta;
        private float mFocusLevelStart;
        private final Interpolator mInterpolator;
        private final float mScaleDiff;
        private final View mView;

        void animateFocus(boolean z, boolean z2) {
            endAnimation();
            float f = z ? 1.0f : 0.0f;
            if (z2) {
                setFocusLevel(f);
                return;
            }
            float f2 = this.mFocusLevel;
            if (f2 != f) {
                this.mFocusLevelStart = f2;
                this.mFocusLevelDelta = f - f2;
                this.mAnimator.start();
            }
        }

        FocusAnimator(View view, float f, int i) {
            TimeAnimator timeAnimator = new TimeAnimator();
            this.mAnimator = timeAnimator;
            this.mInterpolator = new AccelerateDecelerateInterpolator();
            this.mView = view;
            this.mDuration = i;
            this.mScaleDiff = f - 1.0f;
            timeAnimator.setTimeListener(this);
        }

        void setFocusLevel(float f) {
            this.mFocusLevel = f;
            float f2 = (this.mScaleDiff * f) + 1.0f;
            this.mView.setScaleX(f2);
            this.mView.setScaleY(f2);
            this.mView.setElevation(f * 12.0f);
            this.mView.setZ(f * 12.0f);
        }

        void endAnimation() {
            this.mAnimator.end();
        }

        @Override // android.animation.TimeAnimator.TimeListener
        public void onTimeUpdate(TimeAnimator timeAnimator, long j, long j2) {
            float f;
            int i = this.mDuration;
            if (j >= i) {
                this.mAnimator.end();
                f = 1.0f;
            } else {
                f = (float) (j / ((double) i));
            }
            setFocusLevel(this.mFocusLevelStart + (this.mInterpolator.getInterpolation(f) * this.mFocusLevelDelta));
        }
    }
}
