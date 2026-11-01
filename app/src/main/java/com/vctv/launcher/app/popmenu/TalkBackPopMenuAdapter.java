package com.vctv.launcher.app.popmenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.vctv.launcher.R;
import com.vctv.launcher.data.PopMenuItem;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class TalkBackPopMenuAdapter extends RecyclerView.Adapter<TalkBackPopMenuAdapter.ItemMenuViewHolder> {
    private final OnItemClickListener listener;
    private final List<PopMenuItem> popMenuItemList;

    public interface OnItemClickListener {
        void onItemClicked(PopMenuItem popMenuItem);
    }

    public TalkBackPopMenuAdapter(List<PopMenuItem> list, OnItemClickListener onItemClickListener) {
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
        int color = ContextCompat.getColor(itemMenuViewHolder.itemView.getContext(), R.color.pop_menu_item_default);
        int color2 = ContextCompat.getColor(itemMenuViewHolder.itemView.getContext(), R.color.pop_menu_item_disable);
        itemMenuViewHolder.itemView.setEnabled(popMenuItem.getEnable());
        LinearLayout linearLayout = itemMenuViewHolder.layout;
        if (!popMenuItem.getEnable()) {
            color = color2;
        }
        linearLayout.setBackgroundColor(color);
        itemMenuViewHolder.layout.setOnClickListener(new View.OnClickListener() { // from class: com.vctv.launcher.app.popmenu.TalkBackPopMenuAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TalkBackPopMenuAdapter.this.m174x169261ff(itemMenuViewHolder, view);
            }
        });
        itemMenuViewHolder.layout.setLayoutParams(new LinearLayout.LayoutParams((int) itemMenuViewHolder.itemView.getContext().getResources().getDimension(R.dimen.pop_talk_back_menu_item_width), (int) itemMenuViewHolder.itemView.getContext().getResources().getDimension(R.dimen.pop_menu_height)));
    }

    /* JADX INFO: renamed from: lambda$onBindViewHolder$0$com-mw-launcher-app-popmenu-TalkBackPopMenuAdapter, reason: not valid java name */
    /* synthetic */ void m174x169261ff(ItemMenuViewHolder itemMenuViewHolder, View view) {
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
