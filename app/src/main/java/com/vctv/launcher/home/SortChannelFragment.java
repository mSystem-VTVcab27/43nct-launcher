package com.vctv.launcher.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ItemBridgeAdapter;
import com.vctv.launcher.R;
import com.vctv.launcher.data.ChannelData;
import com.vctv.launcher.data.ChannelDateManager;
import com.vctv.launcher.home.presenter.SortChannelItemPresenter;
import com.vctv.launcher.util.CustomTextView;
import com.vctv.launcher.util.ViewUtils;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SortChannelFragment extends Fragment {
    private ChannelDateManager channelDateManager;
    boolean isMoveChannel = false;
    SortChannelItemPresenter.ItemClickListener itemClickListener = new SortChannelItemPresenter.ItemClickListener() { // from class: com.vctv.launcher.home.SortChannelFragment.1
        @Override // com.vctv.launcher.home.presenter.SortChannelItemPresenter.ItemClickListener
        public void onClick(View view, ChannelData channelData) {
            SortChannelFragment.this.sortChannelVerticalGridView.switchMoveStatus(view, channelData);
        }
    };
    SortChannelVerticalGridView.MoveChannelListener moveChannelListener = new SortChannelVerticalGridView.MoveChannelListener() { // from class: com.vctv.launcher.home.SortChannelFragment.2
        @Override // com.vctv.launcher.home.SortChannelVerticalGridView.MoveChannelListener
        public void moveChannel() {
            SortChannelFragment.this.isMoveChannel = true;
        }
    };
    SortChannelVerticalGridView sortChannelVerticalGridView;

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.fragment_sort_channel, viewGroup, false);
        this.channelDateManager = ChannelDateManager.getInstance(requireContext().getApplicationContext());
        ((CustomTextView) viewInflate.findViewById(R.id.sort_channels_fragment_title)).setTextColor(ViewUtils.getTextDefaultColor(viewInflate.getContext()));
        ((CustomTextView) viewInflate.findViewById(R.id.sort_channel_description)).setTextColor(ViewUtils.getTextDefaultColor(viewInflate.getContext()));
        this.sortChannelVerticalGridView = (SortChannelVerticalGridView) viewInflate.findViewById(R.id.sort_channel_list);
        SortChannelItemPresenter sortChannelItemPresenter = new SortChannelItemPresenter();
        sortChannelItemPresenter.setItemClickListener(this.itemClickListener);
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(sortChannelItemPresenter);
        List<ChannelData> displayChannelDataListNeedSort = this.channelDateManager.getDisplayChannelDataListNeedSort();
        arrayObjectAdapter.addAll(0, displayChannelDataListNeedSort);
        ItemBridgeAdapter itemBridgeAdapter = new ItemBridgeAdapter();
        itemBridgeAdapter.setAdapter(arrayObjectAdapter);
        this.sortChannelVerticalGridView.setArrayObjectAdapter(arrayObjectAdapter);
        this.sortChannelVerticalGridView.setDisplayChannel(displayChannelDataListNeedSort);
        this.sortChannelVerticalGridView.setChannelDateManager(this.channelDateManager);
        this.sortChannelVerticalGridView.setMoveChannelListener(this.moveChannelListener);
        this.sortChannelVerticalGridView.setAdapter(itemBridgeAdapter);
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        if (this.isMoveChannel) {
            this.channelDateManager.updateDisplayDbChannelData();
        }
    }
}
