package com.vctv.launcher.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import com.vctv.launcher.config.Config;

/* JADX INFO: loaded from: classes.dex */
public class CustomTextView extends AppCompatTextView {
    public CustomTextView(Context context) {
        super(context);
        applyCustomFont();
    }

    public CustomTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        applyCustomFont();
    }

    public CustomTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        applyCustomFont();
    }

    private void applyCustomFont() {
        setTypeface(Typeface.create(Config.get().globalStyle.textFont, 0));
    }
}
