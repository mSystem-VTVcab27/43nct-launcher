package com.vctv.launcher.tab.view;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.leanback.tab.LeanbackTabLayout;
import androidx.viewpager.widget.ViewPager;

/* JADX INFO: loaded from: classes.dex */
public class LeanbackTab extends LeanbackTabLayout {
    private int defaultColor;
    private int focusedColor;
    private ViewPager mViewPager;
    private int selectedColor;
    private Typeface tabItemTypeFace;

    public LeanbackTab(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // androidx.leanback.tab.LeanbackTabLayout, com.google.android.material.tabs.TabLayout
    public void setupWithViewPager(ViewPager viewPager) {
        super.setupWithViewPager(viewPager);
        this.mViewPager = viewPager;
    }

    @Override // androidx.leanback.tab.LeanbackTabLayout, android.widget.HorizontalScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updatePageTabs();
    }

    public void setTabItemTypeFace(Typeface typeface) {
        this.tabItemTypeFace = typeface;
    }

    void updatePageTabs() {
        LinearLayout linearLayout = (LinearLayout) getChildAt(0);
        if (linearLayout == null) {
            return;
        }
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = linearLayout.getChildAt(i);
            childAt.setFocusable(true);
            childAt.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.tab.view.LeanbackTab$$ExternalSyntheticLambda0
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view, boolean z) {
                    LeanbackTab.this.onTabItemFocusChange(view, z);
                }
            });
            setTabTypeFace(childAt);
        }
    }

    void setTabTypeFace(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt instanceof TextView) {
                    ((TextView) childAt).setTypeface(this.tabItemTypeFace);
                }
            }
        }
    }

    public void onTabItemFocusChange(View view, boolean z) {
        ViewPager viewPager;
        if (z) {
            LinearLayout linearLayout = (LinearLayout) getChildAt(0);
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                if (view == linearLayout.getChildAt(i) && (viewPager = this.mViewPager) != null) {
                    viewPager.setCurrentItem(i, true);
                }
                updateTabAppearance(this.defaultColor, this.selectedColor);
            }
            return;
        }
        updateTabAppearance(this.defaultColor, this.focusedColor);
    }

    public void setTabItemColor(int i, int i2, int i3) {
        this.selectedColor = i3;
        this.defaultColor = i;
        this.focusedColor = i2;
    }

    private void updateTabAppearance(int i, int i2) {
        setTabTextColors(i, i2);
        Drawable tabSelectedIndicator = getTabSelectedIndicator();
        tabSelectedIndicator.setColorFilter(new BlendModeColorFilter(i2, BlendMode.SRC));
        setSelectedTabIndicator(tabSelectedIndicator);
    }

    public void onFocusChange(View view, boolean z) {
        ViewGroup viewGroup;
        if (!z || (viewGroup = (ViewGroup) getChildAt(0)) == null) {
            return;
        }
        viewGroup.requestFocus();
    }

    @Override // com.google.android.material.tabs.TabLayout, android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.tab.view.LeanbackTab$$ExternalSyntheticLambda1
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                LeanbackTab.this.onFocusChange(view, z);
            }
        });
    }

    @Override // com.google.android.material.tabs.TabLayout, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setOnFocusChangeListener(null);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public View focusSearch(View view, int i) {
        View viewFindFocus;
        return (i == 17 && (viewFindFocus = findFocus()) != null && isFirstOrLastChild(viewFindFocus, i)) ? viewFindFocus : super.focusSearch(view, i);
    }

    private boolean isFirstOrLastChild(View view, int i) {
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        if (viewGroup == null || viewGroup.getChildCount() == 0) {
            return false;
        }
        if (i == 17) {
            return view == viewGroup.getChildAt(0);
        }
        return i == 66 && view == viewGroup.getChildAt(viewGroup.getChildCount() - 1);
    }
}
