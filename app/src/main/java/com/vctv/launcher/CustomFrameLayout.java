package com.vctv.launcher;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import com.vctv.launcher.util.AppConfigUtil;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class CustomFrameLayout extends FrameLayout {
    public CustomFrameLayout(Context context) {
        super(context);
    }

    public CustomFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public View focusSearch(View view, int i) {
        int id = view.getId();
        ArrayList arrayList = new ArrayList();
        addFocusables(arrayList, i);
        View viewFocusSearch = super.focusSearch(view, i);
        TextView tabMatchTextView = getTabMatchTextView(arrayList);
        boolean zIsTalkBackEnabled = AppConfigUtil.isTalkBackEnabled(getContext());
        if (i != 17) {
            if (i != 33) {
                if (i != 66) {
                    if (i == 130) {
                        if (isChildOfSpecificView(view, (ViewGroup) findViewById(R.id.container_layout))) {
                            return zIsTalkBackEnabled ? tabMatchTextView : getNextView(R.id.layout_app, arrayList, viewFocusSearch);
                        }
                        if (id == R.id.favorites_apps || id == R.id.install_apps) {
                            return getNextView(R.id.layout_app, arrayList, viewFocusSearch);
                        }
                    }
                } else {
                    if (id == R.id.favorites_apps || id == R.id.install_apps) {
                        return view;
                    }
                    if (id == R.id.layout_app) {
                        View viewFindViewById = findViewById(R.id.clock);
                        if (viewFindViewById != null) {
                            viewFindViewById.setImportantForAccessibility(2);
                        }
                        return view;
                    }
                    if (id == R.id.settings) {
                        View viewFindViewById2 = findViewById(R.id.clock);
                        if (viewFindViewById2 != null) {
                            viewFindViewById2.setImportantForAccessibility(1);
                        }
                        return viewFindViewById2;
                    }
                }
            } else if (id == R.id.layout_app) {
                return zIsTalkBackEnabled ? tabMatchTextView : getNextView(-1, arrayList, viewFocusSearch);
            }
        } else if (id == R.id.favorites_apps || id == R.id.install_apps) {
            return view;
        }
        return viewFocusSearch;
    }

    private boolean isChildOfSpecificView(View view, ViewGroup viewGroup) {
        if (viewGroup == null) {
            return false;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt == view) {
                return true;
            }
            if ((childAt instanceof ViewGroup) && isChildOfSpecificView(view, (ViewGroup) childAt)) {
                return true;
            }
        }
        return false;
    }

    private View getNextView(int i, ArrayList<View> arrayList, View view) {
        for (View view2 : arrayList) {
            if (view2 != null && view2.getId() == i) {
                return view2;
            }
        }
        return view;
    }

    private TextView getTabMatchTextView(ArrayList<View> arrayList) {
        View next;
        TextView textView;
        Iterator<View> it = arrayList.iterator();
        do {
            if (!it.hasNext()) {
                next = null;
                break;
            }
            next = it.next();
        } while (next.getId() != -1);
        if (next instanceof TabLayout.TabView) {
            CharSequence text = ((TabLayout.TabView) next).getTab().getText();
            if (TextUtils.equals(text, getContext().getString(R.string.home))) {
                TextView textView2 = (TextView) findViewById(R.id.favorites_apps);
                if (textView2 != null && textView2.getVisibility() == 0) {
                    return textView2;
                }
            } else if (TextUtils.equals(text, getContext().getString(R.string.apps)) && (textView = (TextView) findViewById(R.id.install_apps)) != null && textView.getVisibility() == 0) {
                return textView;
            }
        }
        return null;
    }
}
