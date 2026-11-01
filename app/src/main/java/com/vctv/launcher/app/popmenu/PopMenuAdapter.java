package com.vctv.launcher.app.popmenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.vctv.launcher.R;
import com.vctv.launcher.data.PopMenuItem;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PopMenuAdapter extends RecyclerView.Adapter<PopMenuAdapter.ItemMenuViewHolder> {
    private final OnItemClickListener listener;
    private final List<PopMenuItem> popMenuItemList;

    public interface OnItemClickListener {
        void onItemClicked(PopMenuItem popMenuItem);
    }

    public PopMenuAdapter(List<PopMenuItem> list, OnItemClickListener onItemClickListener) {
        this.popMenuItemList = list;
        this.listener = onItemClickListener;
    }

    public static class ItemMenuViewHolder extends RecyclerView.ViewHolder {
        TextView desc;
        ImageView icon;
        LinearLayout layout;

        public ItemMenuViewHolder(View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.icon);
            this.desc = (TextView) view.findViewById(R.id.desc);
            this.layout = (LinearLayout) view.findViewById(R.id.menu_item);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ItemMenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemMenuViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pop_menu, viewGroup, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(final ItemMenuViewHolder itemMenuViewHolder, int i) {
        PopMenuItem popMenuItem = this.popMenuItemList.get(i);
        itemMenuViewHolder.icon.setImageDrawable(popMenuItem.getIcon());
        itemMenuViewHolder.desc.setText(popMenuItem.getDesc());
        final int color = ContextCompat.getColor(itemMenuViewHolder.itemView.getContext(), R.color.pop_menu_item_default);
        final int color2 = ContextCompat.getColor(itemMenuViewHolder.itemView.getContext(), R.color.pop_menu_item_selected);
        ContextCompat.getColor(itemMenuViewHolder.itemView.getContext(), R.color.pop_menu_item_disable);
        TextView textView = itemMenuViewHolder.desc;
        boolean enable = popMenuItem.getEnable();
        int i2 = ViewCompat.MEASURED_STATE_MASK;
        textView.setTextColor(enable ? -16777216 : color2);
        ImageView imageView = itemMenuViewHolder.icon;
        if (!popMenuItem.getEnable()) {
            i2 = color2;
        }
        imageView.setColorFilter(i2);
        itemMenuViewHolder.itemView.setEnabled(popMenuItem.getEnable());
        itemMenuViewHolder.layout.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.app.popmenu.PopMenuAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                PopMenuAdapter.lambda$onBindViewHolder$0(color2, color, view, z);
            }
        });
        itemMenuViewHolder.layout.setOnClickListener(new View.OnClickListener() { // from class: com.vctv.launcher.app.popmenu.PopMenuAdapter$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PopMenuAdapter.this.m171xb8be59cb(itemMenuViewHolder, view);
            }
        });
    }

    static /* synthetic */ void lambda$onBindViewHolder$0(int i, int i2, View view, boolean z) {
        if (!z) {
            i = i2;
        }
        view.setBackgroundColor(i);
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$1$com-mw-launcher-app-popmenu-PopMenuAdapter, reason: not valid java name */
    /* synthetic */ void m171xb8be59cb(ItemMenuViewHolder itemMenuViewHolder, View view) {
        OnItemClickListener onItemClickListener;
        if (!itemMenuViewHolder.itemView.isEnabled() || (onItemClickListener = this.listener) == null) {
            return;
        }
        onItemClickListener.onItemClicked(this.popMenuItemList.get(itemMenuViewHolder.getAdapterPosition()));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.popMenuItemList.size();
    }
}
