package com.vctv.launcher.app;

import android.view.View;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.Presenter;

/* JADX INFO: loaded from: classes.dex */
public abstract class AppItemBridgeAdapter extends ItemBridgeAdapter {

    public interface OnItemFocusChangedListener {
        void onItemFocusChanged(View view, Presenter.ViewHolder viewHolder, Object obj, boolean z, int i);
    }

    public interface OnItemViewClickedListener {
        void onItemClicked(View view, Presenter.ViewHolder viewHolder, Object obj);
    }

    public interface OnItemViewLongClickedListener {
        boolean onItemLongClicked(View view, Presenter.ViewHolder viewHolder, Object obj);
    }

    public OnItemFocusChangedListener getOnItemFocusChangedListener() {
        return null;
    }

    public abstract OnItemViewClickedListener getOnItemViewClickedListener();

    public OnItemViewLongClickedListener getOnItemViewLongClickedListener() {
        return null;
    }

    protected AppItemBridgeAdapter(ObjectAdapter objectAdapter) {
        super(objectAdapter, null);
    }

    @Override // androidx.leanback.widget.ItemBridgeAdapter
    protected void onBind(final ItemBridgeAdapter.ViewHolder viewHolder) {
        if (getOnItemViewClickedListener() != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.vctv.launcher.app.AppItemBridgeAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    AppItemBridgeAdapter.this.getOnItemViewClickedListener().onItemClicked(view, viewHolder.getViewHolder(), viewHolder.getItem());
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.vctv.launcher.app.AppItemBridgeAdapter.2
                @Override // android.view.View.OnLongClickListener
                public boolean onLongClick(View view) {
                    if (AppItemBridgeAdapter.this.getOnItemViewLongClickedListener() != null) {
                        return AppItemBridgeAdapter.this.getOnItemViewLongClickedListener().onItemLongClicked(view, viewHolder.getViewHolder(), viewHolder.getItem());
                    }
                    return true;
                }
            });
        }
        if (getOnItemFocusChangedListener() != null) {
            viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.vctv.launcher.app.AppItemBridgeAdapter.3
                @Override // android.view.View.OnFocusChangeListener
                public void onFocusChange(View view, boolean z) {
                    AppItemBridgeAdapter.this.getOnItemFocusChangedListener().onItemFocusChanged(view, viewHolder.getViewHolder(), viewHolder.getItem(), z, viewHolder.getAdapterPosition());
                }
            });
        }
        super.onBind(viewHolder);
    }

    @Override // androidx.leanback.widget.ItemBridgeAdapter
    protected void onUnbind(ItemBridgeAdapter.ViewHolder viewHolder) {
        super.onUnbind(viewHolder);
        viewHolder.itemView.setOnClickListener(null);
        if (getOnItemFocusChangedListener() != null) {
            viewHolder.itemView.setOnFocusChangeListener(null);
        }
    }
}
