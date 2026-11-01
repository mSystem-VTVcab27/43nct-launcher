package com.vctv.launcher.home.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.leanback.widget.Presenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vctv.launcher.R;
import com.vctv.launcher.config.Config;
import com.vctv.launcher.config.Configuration;
import com.vctv.launcher.data.PreviewProgramData;
import com.vctv.launcher.home.ProgramItemFocusEvent;
import com.vctv.launcher.util.LogUtil;
import com.vctv.launcher.util.ViewUtils;
import java.net.URISyntaxException;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/* JADX INFO: loaded from: classes.dex */
public class ProgramItemPresenter extends Presenter {
    private static String TAG = "ProgramItemPresenter";
    private boolean isNetflix;
    private float itemDefaultHeight;
    ItemFocusListener itemFocusListener;
    private boolean lastFocusStatus;
    private View lastFocusedView;
    List<PreviewProgramData> previewProgramList;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable postFocusEventRunnable = new Runnable() { // from class: com.vctv.launcher.home.presenter.ProgramItemPresenter.1
        @Override // java.lang.Runnable
        public void run() {
            EventBus.getDefault().post(new ProgramItemFocusEvent(ProgramItemPresenter.this.lastFocusedView, ProgramItemPresenter.this.lastFocusStatus));
        }
    };
    private final boolean dynamicEffect = Config.get().globalStyle.dynamicEffect;
    private final Configuration.GlobalStyle.SelectionMode itemSelectionMode = Config.get().globalStyle.itemSelectionMode;
    private final float itemZoomScale = Config.get().globalStyle.itemZoomScale;
    private final int itemZoomSpeed = Config.get().globalStyle.itemZoomSpeed;

    public interface ItemFocusListener {
        void focusStatus(boolean z, Object obj);
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        if (viewHolder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) viewHolder;
            if (vh.mImagePic != null) {
                try {
                    com.bumptech.glide.Glide.with(vh.mImagePic.getContext()).clear(vh.mImagePic);
                } catch (Exception ignored) {
                }
                vh.mImagePic.setImageDrawable(null);
            }
        }
    }

    public ProgramItemPresenter() {
        int i = AnonymousClass4.$SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Config.get().globalStyle.itemSize.ordinal()];
        if (i == 1) {
            this.itemDefaultHeight = 90.0f;
        } else if (i == 2) {
            this.itemDefaultHeight = 144.0f;
        } else {
            this.itemDefaultHeight = 117.0f;
        }
    }

    /* JADX INFO: renamed from: com.vctv.launcher.home.presenter.ProgramItemPresenter$4, reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
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

    @Override // androidx.leanback.widget.Presenter
    public Presenter.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.program_item, viewGroup, false));
    }

    @Override // androidx.leanback.widget.Presenter
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, final Object obj) {
        String string;
        if (obj instanceof PreviewProgramData) {
            final PreviewProgramData previewProgramData = (PreviewProgramData) obj;
            final ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            Context context = viewHolder2.view.getContext();
            int iIndexOf = this.previewProgramList.indexOf(previewProgramData);
            String title = previewProgramData.getTitle();
            if (!TextUtils.isEmpty(title)) {
                string = context.getString(R.string.program_item_description, title, Integer.valueOf(iIndexOf + 1));
            } else {
                string = context.getString(R.string.program_item_description, context.getString(R.string.not_program_title), Integer.valueOf(iIndexOf + 1));
            }
            if (iIndexOf == 0) {
                string = string + context.getString(R.string.program_count_description, Integer.valueOf(this.previewProgramList.size()));
            }
            ViewGroup.LayoutParams layoutParams = viewHolder2.mImagePic.getLayoutParams();
            layoutParams.width = ViewUtils.dpToPx(viewHolder2.mImagePic.getContext(), adjustPosterWidth(previewProgramData.getPosterAspectRatio()));
            viewHolder2.mImagePic.setLayoutParams(layoutParams);
            viewHolder2.mImagePic.setContentDescription(string);
            viewHolder2.mImagePic.setOnClickListener(new View.OnClickListener() { // from class: com.vctv.launcher.home.presenter.ProgramItemPresenter.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    String intentUri = previewProgramData.getIntentUri();
                    try {
                        Intent uri = Intent.parseUri(intentUri, 1);
                        Context context2 = view.getContext();
                        if (ProgramItemPresenter.this.isNetflix && Config.get().netflix.isIntegrated()) {
                            ProgramItemPresenter.this.startNetflixWithIID(intentUri, context2);
                            return;
                        }
                        if (uri == null) {
                            Log.e(ProgramItemPresenter.TAG, "Intent is null");
                            return;
                        }
                        try {
                            if (uri.resolveActivity(context2.getPackageManager()) == null) {
                                Log.e(ProgramItemPresenter.TAG, "No Activity found for intent: " + uri);
                            } else {
                                context2.startActivity(uri);
                            }
                        } catch (SecurityException e) {
                            Log.e(ProgramItemPresenter.TAG, "SecurityException: " + e.getMessage());
                        }
                    } catch (URISyntaxException e2) {
                        Log.e(ProgramItemPresenter.TAG, "parse program intent fail: " + e2.getMessage());
                    }
                }
            });
            viewHolder2.mImagePic.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.home.presenter.ProgramItemPresenter.3
                @Override // android.view.View.OnFocusChangeListener
                public void onFocusChange(View view, boolean z) {
                    ProgramItemPresenter.this.itemFocusListener.focusStatus(z, obj);
                    if (ProgramItemPresenter.this.dynamicEffect) {
                        ProgramItemPresenter.this.handler.removeCallbacks(ProgramItemPresenter.this.postFocusEventRunnable);
                        ProgramItemPresenter.this.lastFocusedView = view;
                        ProgramItemPresenter.this.lastFocusStatus = z;
                        ProgramItemPresenter.this.handler.postDelayed(ProgramItemPresenter.this.postFocusEventRunnable, 2000L);
                    }
                    if (z) {
                        if (Configuration.GlobalStyle.SelectionMode.BORDER.equals(ProgramItemPresenter.this.itemSelectionMode)) {
                            viewHolder2.mImageLayout.setBackground(viewHolder2.borderDrawable);
                            return;
                        }
                        view.setElevation(12.0f);
                        view.setZ(12.0f);
                        viewHolder2.mImageLayout.setZ(12.0f);
                        float height = view.getHeight() / 2.0f;
                        if (height <= 0.0f) {
                            height = ViewUtils.dpToPx(view.getContext(), ProgramItemPresenter.this.itemDefaultHeight) / 2.0f;
                        }
                        view.setPivotY(height);
                        int iIndexOf = ProgramItemPresenter.this.previewProgramList != null ? ProgramItemPresenter.this.previewProgramList.indexOf(previewProgramData) : 0;
                        if (iIndexOf == 0) {
                            view.setPivotX(0.0f);
                        } else {
                            view.setPivotX(view.getWidth() / 2.0f);
                        }
                        view.animate().scaleX(ProgramItemPresenter.this.itemZoomScale).scaleY(ProgramItemPresenter.this.itemZoomScale).setDuration(ProgramItemPresenter.this.itemZoomSpeed).start();
                        return;
                    }
                    if (Configuration.GlobalStyle.SelectionMode.BORDER.equals(ProgramItemPresenter.this.itemSelectionMode)) {
                        viewHolder2.mImageLayout.setBackground(null);
                    } else {
                        view.setElevation(0.0f);
                        view.setZ(0.0f);
                        viewHolder2.mImageLayout.setZ(0.0f);
                        view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(ProgramItemPresenter.this.itemZoomSpeed).start();
                    }
                }
            });
            Glide.with(viewHolder2.mImagePic.getContext()).asBitmap().load(forceHttps(previewProgramData.getPosterArtUri())).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.bg_app).fallback(R.drawable.bg_app).error(R.drawable.bg_app).centerCrop().into(viewHolder2.mImagePic);
        }
    }

    public void startNetflixWithIID(String str, Context context) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str + "?iid=" + Config.get().netflix.iidChannel));
        intent.addFlags(32);
        intent.setPackage("com.netflix.ninja");
        LogUtil.d("startNetflixWithIID: " + Config.get().netflix.iidChannel);
        context.startActivity(intent);
    }

    /* JADX WARN: Code duplicated, block: B:22:0x003d  */
    public int adjustPosterWidth(int i) {
        float f;
        float f2;
        float f3 = 1.7777778f;
        int i2 = AnonymousClass4.$SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Config.get().globalStyle.itemSize.ordinal()];
        if (i2 != 1) {
            f = i2 != 2 ? 117.0f : 144.0f;
        } else {
            f = 90.0f;
        }
        if (i == 0) {
            f2 = f * 1.7777778f;
        } else {
            if (i == 1) {
                f3 = 1.5f;
            } else if (i == 2) {
                f3 = 1.3333334f;
            } else if (i == 3) {
                f3 = 1.0f;
            } else if (i != 4) {
                f2 = f * 1.7777778f;
            } else {
                f3 = 0.6666667f;
            }
            f2 = f * f3;
        }
        return Math.round(f2);
    }

    public static class ViewHolder extends Presenter.ViewHolder {
        GradientDrawable borderDrawable;
        public LinearLayout mImageLayout;
        public ImageView mImagePic;

        public ViewHolder(View view) {
            super(view);
            this.mImagePic = (ImageView) view.findViewById(R.id.item_image);
            this.mImageLayout = (LinearLayout) view.findViewById(R.id.item_image_layout);
            ViewGroup.LayoutParams layoutParams = this.mImagePic.getLayoutParams();
            Pair<Float, Float> itemSize = getItemSize();
            layoutParams.width = ViewUtils.dpToPx(view.getContext(), ((Float) itemSize.first).floatValue());
            layoutParams.height = ViewUtils.dpToPx(view.getContext(), ((Float) itemSize.second).floatValue());
            this.mImagePic.setLayoutParams(layoutParams);
            this.mImagePic.setBackground(ViewUtils.createProgramItemDrawable(view.getContext()));
            this.mImagePic.setClipToOutline(true);
            if (Configuration.GlobalStyle.SelectionMode.BORDER.equals(Config.get().globalStyle.itemSelectionMode)) {
                int iDpToPx = ViewUtils.dpToPx(this.mImageLayout.getContext(), Config.get().globalStyle.itemBorderWidth);
                this.mImageLayout.setPadding(iDpToPx, iDpToPx, iDpToPx, iDpToPx);
                this.borderDrawable = ViewUtils.createDrawable(view.getContext(), 0, true, true);
            }
        }

        private Pair<Float, Float> getItemSize() {
            int i = AnonymousClass4.$SwitchMap$com$mw$launcher$config$Configuration$GlobalStyle$ItemSize[Config.get().globalStyle.itemSize.ordinal()];
            if (i == 1) {
                return new Pair<>(Float.valueOf(160.0f), Float.valueOf(90.0f));
            }
            if (i == 2) {
                return new Pair<>(Float.valueOf(256.0f), Float.valueOf(144.0f));
            }
            return new Pair<>(Float.valueOf(208.0f), Float.valueOf(117.0f));
        }
    }

    public void setItemFocusListener(ItemFocusListener itemFocusListener) {
        this.itemFocusListener = itemFocusListener;
    }

    public void setPreviewProgramList(List<PreviewProgramData> list) {
        this.previewProgramList = list;
    }

    public static String forceHttps(String str) {
        if (str == null) {
            return null;
        }
        String strTrim = str.trim();
        if (strTrim.isEmpty()) {
            return null;
        }
        return strTrim.regionMatches(true, 0, "http://", 0, 7) ? "https://" + strTrim.substring(7) : strTrim;
    }

    public void setIsNetflix(boolean z) {
        this.isNetflix = z;
    }
}
