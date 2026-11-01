package com.vctv.launcher.home;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.vctv.launcher.R;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.data.ChannelData;
import com.vctv.launcher.data.ChannelDateManager;
import com.vctv.launcher.data.PreviewProgramData;
import com.vctv.launcher.home.presenter.ProgramItemPresenter;
import com.vctv.launcher.util.CustomTextView;
import com.vctv.launcher.util.ViewUtils;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/* JADX INFO: loaded from: classes.dex */
public class ChannelContentView extends LinearLayout {
    private static final RecyclerView.RecycledViewPool sSharedProgramPool = new RecyclerView.RecycledViewPool();
    static {
        sSharedProgramPool.setMaxRecycledViews(0, 20);
    }
    private static final Map<Configuration.GlobalStyle.ItemSize, Map<Float, Float>> HEIGHT_MAP;
    private static final Map<Configuration.GlobalStyle.ItemSize, Map<Float, Float>> PADDING_TOP_MAP;
    public static int PROGRAM_INFO_VISIBLE_EVENT;
    ChannelData channelData;
    CustomTextView channelTitle;
    Context context;
    private List<ChannelData> displayChannelList;
    private ArrayObjectAdapter programArrayObjectAdapter;
    LinearLayout programDescriptionLayout;
    HorizontalGridView programGridView;
    CustomTextView programItemFirstDescription;
    ProgramItemPresenter.ItemFocusListener programItemFocusListener;
    CustomTextView programItemSecondDescription;
    CustomTextView programItemTitle;

    static {
        Configuration.GlobalStyle.ItemSize itemSize = Configuration.GlobalStyle.ItemSize.SMALL;
        Float fValueOf = Float.valueOf(1.1f);
        Float fValueOf2 = Float.valueOf(119.0f);
        Float fValueOf3 = Float.valueOf(1.2f);
        Float fValueOf4 = Float.valueOf(128.0f);
        Float fValueOf5 = Float.valueOf(1.3f);
        HEIGHT_MAP = Map.of(itemSize, Map.of(fValueOf, fValueOf2, fValueOf3, fValueOf4, fValueOf5, Float.valueOf(137.0f)), Configuration.GlobalStyle.ItemSize.MEDIUM, Map.of(fValueOf, Float.valueOf(148.7f), fValueOf3, Float.valueOf(160.4f), fValueOf5, Float.valueOf(172.1f)), Configuration.GlobalStyle.ItemSize.LARGE, Map.of(fValueOf, Float.valueOf(178.4f), fValueOf3, Float.valueOf(192.8f), fValueOf5, Float.valueOf(207.2f)));
        PADDING_TOP_MAP = Map.of(Configuration.GlobalStyle.ItemSize.SMALL, Map.of(fValueOf, Float.valueOf(14.5f), fValueOf3, Float.valueOf(19.0f), fValueOf5, Float.valueOf(23.5f)), Configuration.GlobalStyle.ItemSize.MEDIUM, Map.of(fValueOf, Float.valueOf(15.85f), fValueOf3, Float.valueOf(21.7f), fValueOf5, Float.valueOf(27.55f)), Configuration.GlobalStyle.ItemSize.LARGE, Map.of(fValueOf, Float.valueOf(17.2f), fValueOf3, Float.valueOf(24.4f), fValueOf5, Float.valueOf(31.6f)));
    }

    public ChannelContentView(Context context) {
        this(context, null);
    }

    public ChannelContentView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ChannelContentView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.programItemFocusListener = new ProgramItemPresenter.ItemFocusListener() { // from class: com.vctv.launcher.home.ChannelContentView.1
            @Override // com.vctv.launcher.home.presenter.ProgramItemPresenter.ItemFocusListener
            public void focusStatus(boolean z, Object obj) {
                String shortDescription;
                String title;
                long interactionCount;
                ChannelContentView.this.setProgramInfoViewVisibility(z);
                if (obj instanceof PreviewProgramData) {
                    PreviewProgramData previewProgramData = (PreviewProgramData) obj;
                    title = previewProgramData.getTitle();
                    interactionCount = previewProgramData.getInteractionCount();
                    shortDescription = previewProgramData.getShortDescription();
                } else {
                    shortDescription = "";
                    title = shortDescription;
                    interactionCount = 0;
                }
                ChannelContentView.this.adjustProgramGridViewLayout(z, ChannelContentView.this.programArrayObjectAdapter.indexOf(obj));
                if (z) {
                    if (!TextUtils.isEmpty(title)) {
                        ChannelContentView.this.programItemTitle.setText(title);
                    }
                    if (0 != interactionCount) {
                        ChannelContentView.this.programItemFirstDescription.setText(ChannelContentView.this.programItemFirstDescription.getContext().getString(R.string.views_format, NumberFormat.getNumberInstance(Locale.US).format(interactionCount)));
                    }
                    if (TextUtils.isEmpty(shortDescription)) {
                        return;
                    }
                    ChannelContentView.this.programItemSecondDescription.setText(shortDescription);
                    return;
                }
                ChannelContentView.this.programItemTitle.setText("");
                ChannelContentView.this.programItemFirstDescription.setText("");
                ChannelContentView.this.programItemSecondDescription.setText("");
            }
        };
        init(context);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMyCustomEvent(Integer num) {
        if (num.equals(Integer.valueOf(PROGRAM_INFO_VISIBLE_EVENT))) {
            this.programItemTitle.setVisibility(8);
            this.programDescriptionLayout.setVisibility(8);
        }
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.channel_content_view, this);
        this.channelTitle = (CustomTextView) findViewById(R.id.channel_title);
        this.programGridView = (HorizontalGridView) findViewById(R.id.program_horizontal_grid_view);
        this.programItemTitle = (CustomTextView) findViewById(R.id.program_item_title);
        this.programItemFirstDescription = (CustomTextView) findViewById(R.id.program_item_first_description);
        this.programItemSecondDescription = (CustomTextView) findViewById(R.id.program_item_second_description);
        this.channelTitle.setTextColor(ViewUtils.getTextDefaultColor(context));
        this.programItemTitle.setTextColor(ViewUtils.getTextSelectedColor(context));
        this.programItemFirstDescription.setTextColor(ViewUtils.getTextDefaultColor(context));
        this.programItemSecondDescription.setTextColor(ViewUtils.getTextDefaultColor(context));
        this.programDescriptionLayout = (LinearLayout) findViewById(R.id.program_description_layout);
        this.displayChannelList = ChannelDateManager.getInstance(context.getApplicationContext()).getDisplayChannelDataListNoSort();
        setDefaultProgramGridViewLayout();
    }

    /* JADX INFO: renamed from: com.vctv.launcher.home.ChannelContentView$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize;

        static {
            int[] iArr = new int[Configuration.GlobalStyle.ItemSize.values().length];
            $SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize = iArr;
            try {
                iArr[Configuration.GlobalStyle.ItemSize.SMALL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Configuration.GlobalStyle.ItemSize.LARGE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private void setDefaultProgramGridViewLayout() {
        float f;
        int i = AnonymousClass2.$SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Config.get().globalStyle.itemSize.ordinal()];
        if (i != 1) {
            f = i != 2 ? 157.0f : 184.0f;
        } else {
            f = 130.0f;
        }
        ViewGroup.LayoutParams layoutParams = this.programGridView.getLayoutParams();
        layoutParams.height = ViewUtils.dpToPx(this.programGridView.getContext(), f);
        HorizontalGridView horizontalGridView = this.programGridView;
        horizontalGridView.setPadding(0, ViewUtils.dpToPx(horizontalGridView.getContext(), 10.0f), 0, 0);
        this.programGridView.setLayoutParams(layoutParams);
        post(new Runnable() { // from class: com.vctv.launcher.home.ChannelContentView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ChannelContentView.this.m185xeee08e18();
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setDefaultProgramGridViewLayout$0$com-mw-launcher-home-ChannelContentView, reason: not valid java name */
    /* synthetic */ void m185xeee08e18() {
        requestLayout();
        invalidate();
    }

    public ChannelData getChannelData() {
        return this.channelData;
    }

    public void setChannelData(ChannelData channelData) {
        this.channelData = channelData;
        setChannelTitle(channelData.getDisplayName());
        this.programItemTitle.setVisibility(8);
        this.programDescriptionLayout.setVisibility(8);
        loadProgramsGridView();
    }

    private void setChannelTitle(String str) {
        CustomTextView customTextView = this.channelTitle;
        if (customTextView != null) {
            customTextView.setText(str);
            this.channelTitle.setContentDescription(str);
            this.channelTitle.setAccessibilityHeading(true);
        }
    }

    private void loadProgramsGridView() {
        this.programGridView.setWindowAlignment(1);
        HorizontalGridView horizontalGridView = this.programGridView;
        horizontalGridView.setWindowAlignmentOffset(ViewUtils.dpToPx(horizontalGridView.getContext(), 12.0f));
        this.programGridView.setWindowAlignmentOffsetPercent(0.0f);
        this.programGridView.setItemAlignmentOffset(0);
        this.programGridView.setItemAlignmentOffsetPercent(0.0f);
        this.programGridView.setHorizontalSpacing(15);
        this.programGridView.setRecycledViewPool(sSharedProgramPool);
        this.programGridView.setItemViewCacheSize(2);
        this.programGridView.setHasFixedSize(true);
        if ("TYPE_PREVIEW".equals(this.channelData.getType())) {
            ProgramItemPresenter programItemPresenter = new ProgramItemPresenter();
            programItemPresenter.setItemFocusListener(this.programItemFocusListener);
            programItemPresenter.setPreviewProgramList(new ArrayList(this.channelData.getPreviewProgram()));
            programItemPresenter.setIsNetflix(this.channelData.getPackageName().equals("com.netflix.ninja"));
            ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(programItemPresenter);
            this.programArrayObjectAdapter = arrayObjectAdapter;
            arrayObjectAdapter.addAll(0, this.channelData.getPreviewProgram());
            ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter();
            itemBridgeAdapter.setAdapter(this.programArrayObjectAdapter);
            this.programGridView.setAdapter(itemBridgeAdapter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setProgramInfoViewVisibility(boolean z) {
        if (z) {
            this.programItemTitle.setVisibility(0);
            this.programDescriptionLayout.setVisibility(0);
        } else {
            this.programItemTitle.setVisibility(8);
            this.programDescriptionLayout.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustProgramGridViewLayout(boolean z, int i) {
        int iDpToPx;
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.programGridView.getLayoutParams();
        if (z) {
            float marginStart = marginLayoutParams.getMarginStart();
            if (i != -1) {
                if (i == 0) {
                    iDpToPx = ViewUtils.dpToPx(this.programGridView.getContext(), 50.0f);
                } else {
                    iDpToPx = ViewUtils.dpToPx(this.programGridView.getContext(), 40.0f);
                }
                marginStart = iDpToPx;
            }
            marginLayoutParams.setMarginStart(Math.round(marginStart));
            if (!Configuration.GlobalStyle.SelectionMode.BORDER.equals(Config.get().globalStyle.itemSelectionMode)) {
                Map<Configuration.GlobalStyle.ItemSize, Map<Float, Float>> map = HEIGHT_MAP;
                float fFloatValue = map.getOrDefault(Config.get().globalStyle.itemSize, map.get(Configuration.GlobalStyle.ItemSize.MEDIUM)).getOrDefault(Float.valueOf(Config.get().globalStyle.itemZoomScale), Float.valueOf(160.4f)).floatValue();
                Map<Configuration.GlobalStyle.ItemSize, Map<Float, Float>> map2 = PADDING_TOP_MAP;
                float fFloatValue2 = map2.getOrDefault(Config.get().globalStyle.itemSize, map2.get(Configuration.GlobalStyle.ItemSize.MEDIUM)).getOrDefault(Float.valueOf(Config.get().globalStyle.itemZoomScale), Float.valueOf(21.7f)).floatValue();
                marginLayoutParams.height = ViewUtils.dpToPx(this.programGridView.getContext(), fFloatValue);
                HorizontalGridView horizontalGridView = this.programGridView;
                horizontalGridView.setPadding(0, ViewUtils.dpToPx(horizontalGridView.getContext(), fFloatValue2), 0, 0);
            }
            this.programGridView.setLayoutParams(marginLayoutParams);
            return;
        }
        setDefaultProgramGridViewLayout();
    }

    public void expandViewForFocus() {
        adjustProgramGridViewLayout(true, this.programGridView.getSelectedPosition());
        forceParentRelayout();
    }

    private void forceParentRelayout() {
        requestLayout();
        invalidate();
        ViewParent parent = getParent();
        while (parent != null && !(parent instanceof RecyclerView)) {
            parent = parent.getParent();
        }
        if (parent instanceof RecyclerView) {
            final RecyclerView recyclerView = (RecyclerView) parent;
            recyclerView.post(new Runnable() { // from class: com.vctv.launcher.home.ChannelContentView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChannelContentView.lambda$forceParentRelayout$1(recyclerView);
                }
            });
        }
    }

    static /* synthetic */ void lambda$forceParentRelayout$1(RecyclerView recyclerView) {
        recyclerView.requestLayout();
        recyclerView.invalidateItemDecorations();
    }
}
