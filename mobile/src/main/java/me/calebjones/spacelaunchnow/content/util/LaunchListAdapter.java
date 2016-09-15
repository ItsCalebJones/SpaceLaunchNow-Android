package me.calebjones.spacelaunchnow.content.util;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;

import java.util.ArrayList;
import java.util.List;

import me.calebjones.spacelaunchnow.R;

public class LaunchListAdapter extends RecyclerView.Adapter<LaunchListAdapter.SimpleListVH> implements MDAdapter {

    public interface Callback {
        void onListItemSelected(int index, MaterialSimpleListItem item, boolean longClick);
    }

    private MaterialDialog dialog;
    private List<MaterialSimpleListItem> mItems;
    private Callback mCallback;

    public LaunchListAdapter(Callback callback) {
        mItems = new ArrayList<>(4);
        mCallback = callback;
    }

    public void add(MaterialSimpleListItem item) {
        mItems.add(item);
        notifyItemInserted(mItems.size() - 1);
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public MaterialSimpleListItem getItem(int index) {
        return mItems.get(index);
    }

    @Override
    public void setDialog(MaterialDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public SimpleListVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.md_simplelist_item, parent, false);
        return new SimpleListVH(view, this);
    }

    @Override
    public void onBindViewHolder(SimpleListVH holder, int position) {
        if (dialog != null) {
            final MaterialSimpleListItem item = mItems.get(position);
            if (item.getIcon() != null) {
                holder.icon.setImageDrawable(item.getIcon());
                holder.icon.setPadding(item.getIconPadding(), item.getIconPadding(),
                        item.getIconPadding(), item.getIconPadding());
                holder.icon.getBackground().setColorFilter(item.getBackgroundColor(),
                        PorterDuff.Mode.SRC_ATOP);
            } else {
                holder.icon.setVisibility(View.GONE);
            }
            holder.title.setTextColor(dialog.getBuilder().getItemColor());
            holder.title.setText(item.getContent());
            dialog.setTypeface(holder.title, dialog.getBuilder().getRegularFont());
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class SimpleListVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        final ImageView icon;
        final TextView title;
        final LaunchListAdapter adapter;

        public SimpleListVH(View itemView, LaunchListAdapter adapter) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(android.R.id.icon);
            title = (TextView) itemView.findViewById(android.R.id.title);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (adapter.mCallback != null)
                adapter.mCallback.onListItemSelected(getAdapterPosition(), adapter.getItem(getAdapterPosition()), false);
        }

        @Override
        public boolean onLongClick(View view) {
            if (adapter.mCallback != null)
                adapter.mCallback.onListItemSelected(getAdapterPosition(), adapter.getItem(getAdapterPosition()), true);
            return true;
        }
    }
}