package com.vctv.launcher.tab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.leanback.tab.LeanbackViewPager;
import androidx.lifecycle.Observer;
import com.bumptech.glide.Glide;
import com.vctv.launcher.R;
import com.vctv.launcher.app.AppFragment;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.discover.DiscoverFragment;
import com.vctv.launcher.home.HomeFragment;
import com.vctv.launcher.tab.notifications.NotificationObserver;
import com.vctv.launcher.tab.view.IconNavButton;
import com.vctv.launcher.tab.view.LeanbackTab;
import com.vctv.launcher.tab.view.NotificationItem;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

/* JADX INFO: loaded from: classes.dex */
public class TabContainerFragment extends Fragment {
    private static final int ANIM_DURATION = 600;
    public static final String NOTIFICATION_PANEL = "com.android.tv.NOTIFICATIONS_PANEL";
    private static final String TAG = "TabFragment";
    public static final String TV_RECOMMENDATIONS_PACKAGE_NAME = "com.google.android.tvrecommendations";
    private NotificationItem btnNotification;
    private IconNavButton btnSettings;
    private int defaultTextColor;
    private int focusedColor;
    private Typeface fontStyle;
    private boolean isShowLogo;
    private LeanbackTab leanbackTabLayout;
    private String logoPath;
    private PageAdapter pageAdapter;
    private int selectedColor;
    private ConstraintLayout tabContainerLayout;
    private TextClock textClock;
    private TextView tvHintText;
    private LeanbackViewPager viewPage;
    private boolean isTabAnimating = false;
    private boolean pendingShow = false;
    private boolean pendingHide = false;
    int[] viewLocation = new int[2];

    private void initConfiguration() {
        Configuration configuration = Config.get();
        this.isShowLogo = configuration.logo.enabled;
        this.logoPath = configuration.logo.path;
        Context context = getContext();
        if (context != null) {
            this.defaultTextColor = configuration.globalStyle.getTextDefaultColor(context);
            this.selectedColor = configuration.globalStyle.getTextSelectedColor(context);
            this.focusedColor = configuration.globalStyle.getTextFocusedColor(context);
            this.fontStyle = Typeface.create(configuration.globalStyle.textFont, 0);
        }
    }

    public static TabContainerFragment newInstance() {
        return new TabContainerFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.fragment_tab_container, viewGroup, false);
        initConfiguration();
        initViews(viewInflate);
        setupLogo(viewInflate);
        setupTabLayout();
        setupLaunchItemButtons();
        setupTextClock();
        return viewInflate;
    }

    private void setupTextClock() {
        this.textClock.setTextColor(this.defaultTextColor);
        this.textClock.setTypeface(this.fontStyle);
    }

    private void initViews(View view) {
        this.leanbackTabLayout = (LeanbackTab) view.findViewById(R.id.tab_layout);
        this.viewPage = (LeanbackViewPager) view.findViewById(R.id.content_view_page);
        this.btnNotification = (NotificationItem) view.findViewById(R.id.notification);
        this.btnNotification.setVisibility(View.GONE);
        this.btnSettings = (IconNavButton) view.findViewById(R.id.settings);
        this.tvHintText = (TextView) view.findViewById(R.id.tv_hint_info);
        this.textClock = (TextClock) view.findViewById(R.id.clock);
        this.tabContainerLayout = (ConstraintLayout) view.findViewById(R.id.container_layout);
    }

    private void setupLogo(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.logo);
        if (this.isShowLogo) {
            if (android.text.TextUtils.isEmpty(this.logoPath)) {
                imageView.setImageResource(R.drawable.default_logo);
            } else {
                Glide.with(requireContext()).load(this.logoPath).error(R.drawable.default_logo).skipMemoryCache(true).into(imageView);
            }
            imageView.setVisibility(View.VISIBLE);
            imageView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            return;
        }
        imageView.setVisibility(View.GONE);
    }

    public void hideTab() {
        if (this.isTabAnimating) {
            this.pendingHide = true;
            this.pendingShow = false;
        } else {
            if (this.tabContainerLayout.getVisibility() != 0) {
                return;
            }
            this.isTabAnimating = true;
            int marginTop = getMarginTop(this.tabContainerLayout);
            int height = this.tabContainerLayout.getHeight();
            animateTabHide(height, marginTop);
            animateViewPagerHeight(this.viewPage.getHeight(), this.viewPage.getHeight() + height + marginTop, true);
        }
    }

    public void showTab() {
        if (this.isTabAnimating) {
            this.pendingShow = true;
            this.pendingHide = false;
        } else {
            if (this.tabContainerLayout.getVisibility() != 8) {
                return;
            }
            this.isTabAnimating = true;
            this.tabContainerLayout.post(new Runnable() { // from class: com.vctv.launcher.tab.TabContainerFragment$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TabContainerFragment.this.m195lambda$showTab$0$commwlaunchertabTabContainerFragment();
                }
            });
        }
    }

    /* JADX INFO: renamed from: lambda$showTab$0$com-mw-launcher-tab-TabContainerFragment, reason: not valid java name */
    /* synthetic */ void m195lambda$showTab$0$commwlaunchertabTabContainerFragment() {
        this.tabContainerLayout.setVisibility(0);
        int marginTop = getMarginTop(this.tabContainerLayout);
        int height = this.tabContainerLayout.getHeight();
        this.tabContainerLayout.setTranslationY(-(height + marginTop));
        this.tabContainerLayout.setAlpha(0.0f);
        animateTabShow(marginTop);
        animateViewPagerHeight(this.viewPage.getHeight(), (this.viewPage.getHeight() - height) - marginTop, false);
    }

    public void setCurrentItem(int i) {
        if (this.viewPage.getCurrentItem() != i) {
            this.viewPage.setCurrentItem(i);
        }
    }

    public int getPositionById(int i) {
        PageAdapter pageAdapter = this.pageAdapter;
        if (pageAdapter != null) {
            return pageAdapter.getPositionByResId(i);
        }
        return -1;
    }

    private void animateTabHide(int i, int i2) {
        this.tabContainerLayout.animate().translationY(-(i + i2)).alpha(0.0f).setDuration(600L).setInterpolator(new FastOutSlowInInterpolator()).withEndAction(new Runnable() { // from class: com.vctv.launcher.tab.TabContainerFragment$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                TabContainerFragment.this.m190lambda$animateTabHide$1$commwlaunchertabTabContainerFragment();
            }
        }).start();
    }

    /* JADX INFO: renamed from: lambda$animateTabHide$1$com-mw-launcher-tab-TabContainerFragment, reason: not valid java name */
    /* synthetic */ void m190lambda$animateTabHide$1$commwlaunchertabTabContainerFragment() {
        this.tabContainerLayout.setVisibility(8);
        this.isTabAnimating = false;
        this.pendingHide = false;
        if (this.pendingShow) {
            showTab();
        }
    }

    private void animateTabShow(int i) {
        this.tabContainerLayout.animate().translationY(0.0f).alpha(1.0f).setDuration(600L).setInterpolator(new FastOutSlowInInterpolator()).withEndAction(new Runnable() { // from class: com.vctv.launcher.tab.TabContainerFragment$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                TabContainerFragment.this.m191lambda$animateTabShow$2$commwlaunchertabTabContainerFragment();
            }
        }).start();
    }

    /* JADX INFO: renamed from: lambda$animateTabShow$2$com-mw-launcher-tab-TabContainerFragment, reason: not valid java name */
    /* synthetic */ void m191lambda$animateTabShow$2$commwlaunchertabTabContainerFragment() {
        this.isTabAnimating = false;
        this.pendingShow = false;
        if (this.pendingHide) {
            hideTab();
        }
    }

    private void animateViewPagerHeight(int i, int i2, final boolean z) {
        final float translationY = this.viewPage.getTranslationY();
        final float f = i2 - i;
        ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(i, i2);
        valueAnimatorOfInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.vctv.launcher.tab.TabContainerFragment$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                TabContainerFragment.this.m192xa3a0fbe(z, f, translationY, valueAnimator);
            }
        });
        valueAnimatorOfInt.addListener(new AnimatorListenerAdapter() { // from class: com.vctv.launcher.tab.TabContainerFragment.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                TabContainerFragment.this.viewPage.setTranslationY(translationY);
                TabContainerFragment.this.viewPage.requestLayout();
            }
        });
        valueAnimatorOfInt.setInterpolator(new FastOutSlowInInterpolator());
        valueAnimatorOfInt.setDuration(600L);
        valueAnimatorOfInt.start();
    }

    /* JADX INFO: renamed from: lambda$animateViewPagerHeight$3$com-mw-launcher-tab-TabContainerFragment, reason: not valid java name */
    /* synthetic */ void m192xa3a0fbe(boolean z, float f, float f2, ValueAnimator valueAnimator) {
        float f3;
        ViewGroup.LayoutParams layoutParams = this.viewPage.getLayoutParams();
        layoutParams.height = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.viewPage.setLayoutParams(layoutParams);
        float animatedFraction = valueAnimator.getAnimatedFraction();
        if (z) {
            f3 = -(f * 0.5f * animatedFraction);
        } else {
            f3 = (1.0f - animatedFraction) * f * 0.5f;
        }
        this.viewPage.setTranslationY(f2 + f3);
    }

    private int getMarginTop(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) layoutParams).topMargin;
        }
        return 0;
    }

    private void setupTabLayout() {
        this.leanbackTabLayout.setTabTextColors(this.defaultTextColor, this.selectedColor);
        PageAdapter pageAdapter = new PageAdapter(getParentFragmentManager(), getContext());
        this.pageAdapter = pageAdapter;
        this.viewPage.setAdapter(pageAdapter);
        this.leanbackTabLayout.setupWithViewPager(this.viewPage);
        Drawable drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.tab_indicator_2dp_light);
        if (drawable != null) {
            drawable.setColorFilter(new BlendModeColorFilter(this.selectedColor, BlendMode.SRC));
            this.leanbackTabLayout.setSelectedTabIndicator(drawable);
        }
        this.leanbackTabLayout.setTabItemTypeFace(this.fontStyle);
        this.leanbackTabLayout.setTabItemColor(this.defaultTextColor, this.focusedColor, this.selectedColor);
    }

    private void setupLaunchItemButtons() {
        this.btnSettings.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.tab.TabContainerFragment$$ExternalSyntheticLambda4
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                TabContainerFragment.this.onLaunchItemFocusChange(view, z);
            }
        });
        this.btnSettings.setOnClickListener(new View.OnClickListener() { // from class: com.vctv.launcher.tab.TabContainerFragment$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TabContainerFragment.this.onLaunchItemClick(view);
            }
        });
        this.btnSettings.setFocusColor(this.selectedColor);
        this.btnSettings.setIconColor(this.defaultTextColor);
        this.tvHintText.setTextColor(this.defaultTextColor);
        this.btnNotification.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.tab.TabContainerFragment$$ExternalSyntheticLambda4
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                TabContainerFragment.this.onLaunchItemFocusChange(view, z);
            }
        });
        this.btnNotification.setOnClickListener(new View.OnClickListener() { // from class: com.vctv.launcher.tab.TabContainerFragment$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TabContainerFragment.this.onLaunchItemClick(view);
            }
        });
        this.btnNotification.setItemFocusedColor(this.selectedColor);
        this.btnNotification.setTextColor(this.defaultTextColor);
        new NotificationObserver(getContext()).observe(getViewLifecycleOwner(), new Observer() { // from class: com.vctv.launcher.tab.TabContainerFragment$$ExternalSyntheticLambda6
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                TabContainerFragment.this.m194x80d39023((Integer) obj);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setupLaunchItemButtons$4$com-mw-launcher-tab-TabContainerFragment, reason: not valid java name */
    /* synthetic */ void m194x80d39023(Integer num) {
        if (num == null || num.intValue() <= 0) {
            this.btnNotification.setVisibility(View.GONE);
            return;
        }
        if (this.btnNotification.getCount() < num.intValue()) {
            this.btnNotification.setStatus(0);
        }
        this.btnNotification.setCount(num.intValue());
        this.btnNotification.setContentDescription(getResources().getQuantityString(R.plurals.notifications_desc_content, num.intValue(), num));
        this.btnNotification.setVisibility(View.VISIBLE);
    }

    void onLaunchItemClick(View view) {
        Intent intent;
        if (view.getId() == this.btnSettings.getId()) {
            intent = new Intent("android.settings.SETTINGS");
        } else if (view.getId() == this.btnNotification.getId()) {
            intent = new Intent(NOTIFICATION_PANEL).setPackage(TV_RECOMMENDATIONS_PACKAGE_NAME);
            this.btnNotification.setStatus(1);
        } else {
            intent = null;
        }
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected exception on startActivity", e);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    public void onLaunchItemFocusChange(final View view, boolean z) {
        final ViewGroup viewGroup = (ViewGroup) this.tvHintText.getParent();
        if (z) {
            view.post(new Runnable() { // from class: com.vctv.launcher.tab.TabContainerFragment$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TabContainerFragment.this.m193xfb5b5137(view, viewGroup);
                }
            });
        } else {
            viewGroup.setVisibility(4);
        }
    }

    /* JADX INFO: renamed from: lambda$onLaunchItemFocusChange$5$com-mw-launcher-tab-TabContainerFragment, reason: not valid java name */
    /* synthetic */ void m193xfb5b5137(View view, ViewGroup viewGroup) {
        view.getLocationOnScreen(this.viewLocation);
        int width = (this.viewLocation[0] + (view.getWidth() / 2)) - (this.tvHintText.getWidth() / 2);
        this.tvHintText.setText(getHintText(view));
        viewGroup.setPadding(width, viewGroup.getPaddingTop(), viewGroup.getPaddingRight(), viewGroup.getPaddingBottom());
        viewGroup.setVisibility(0);
    }

    private static String getHintText(View view) {
        if (view instanceof NotificationItem) {
            return ((NotificationItem) view).getHintText();
        }
        return view.getContentDescription().toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class PageAdapter extends FragmentStatePagerAdapter {
        WeakReference<Context> context;
        private final List<PageFragmentAdapter> enabledFragments;
        private final Map<String, PageFragmentAdapter> pagesMap;

        public PageAdapter(FragmentManager fragmentManager, Context context) {
            super(fragmentManager, 1);
            ArrayList arrayList = new ArrayList();
            this.enabledFragments = arrayList;
            Map<String, PageFragmentAdapter> mapOf = Map.of("home", HomeFragment.newInstance(), "discover", DiscoverFragment.newInstance(), "apps", AppFragment.newInstance());
            this.pagesMap = mapOf;
            List<Configuration.Page> list = Config.get().pagination.pages;
            if (list == null || list.isEmpty()) {
                arrayList.add(mapOf.get("home"));
                arrayList.add(mapOf.get("apps"));
            } else {
                list.sort(Comparator.comparingInt(new ToIntFunction() { // from class: com.vctv.launcher.tab.TabContainerFragment$PageAdapter$$ExternalSyntheticLambda0
                    @Override // java.util.function.ToIntFunction
                    public final int applyAsInt(Object obj) {
                        return ((Configuration.Page) obj).order;
                    }
                }));
                for (Configuration.Page page : list) {
                    if (page.enabled) {
                        this.enabledFragments.add(this.pagesMap.get(page.id));
                    }
                }
            }
            this.context = new WeakReference<>(context);
        }

        @Override // androidx.fragment.app.FragmentStatePagerAdapter
        public Fragment getItem(int i) {
            return (Fragment) this.enabledFragments.get(i);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return this.enabledFragments.size();
        }

        public int getPositionByResId(int i) {
            for (int i2 = 0; i2 < this.enabledFragments.size(); i2++) {
                if (this.enabledFragments.get(i2).getPageTitle() == i) {
                    return i2;
                }
            }
            return -1;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public CharSequence getPageTitle(int i) {
            return this.context.get() != null ? this.context.get().getString(this.enabledFragments.get(i).getPageTitle()) : "";
        }
    }
}
