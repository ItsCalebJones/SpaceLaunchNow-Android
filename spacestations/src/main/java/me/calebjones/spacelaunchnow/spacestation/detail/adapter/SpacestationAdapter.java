package me.calebjones.spacelaunchnow.spacestation.detail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import me.calebjones.spacelaunchnow.spacestation.R;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class SpacestationAdapter extends RecyclerView.Adapter<SpacestationAdapter.ViewHolder> {

    public int position;
    private List<ListItem> items;

    public SpacestationAdapter() {
        items = new ArrayList<>();
    }

    public void addItems(List<ListItem> items) {

        if (this.items != null) {
            this.items.addAll(items);
        } else {
            this.items = new ArrayList<>();
            this.items.addAll(items);
        }
        this.notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getListItemType();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = null;
        switch (type) {
            case ListItem.TYPE_DOCKED_VEHICLE:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_docked_vehicle, viewGroup, false);
                return new ViewHolderDockedVehicle(view);
            case ListItem.TYPE_DOCKING_EVENT:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_docking_event, viewGroup, false);
                return new ViewHolderDockingEvent(view);
            case ListItem.TYPE_ACTIVE_EXPEDITION:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_active_expedition, viewGroup, false);
                return new ViewHolderActiveExpedition(view);
            case ListItem.TYPE_PAST_EXPEDITION:
                view = LayoutInflater
                        .from(viewGroup.getContext())
                        .inflate(R.layout.item_past_expedition, viewGroup, false);
                return new ViewHolderPastExpedition(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int pos) {
        ListItem item = items.get(pos);
        viewHolder.bindType(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(ListItem item);
    }

    public class ViewHolderDockedVehicle extends ViewHolder {
        private final TextView mTextView;

        public ViewHolderDockedVehicle(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(R.id.txtView);
        }

        public void bindType(ListItem item) {
            mTextView.setText(((DockedVehicleItem) item).getDockedVehicle().getId());
        }
    }

    public class ViewHolderDockingEvent extends ViewHolder {
        private final TextView mTextView;

        public ViewHolderDockingEvent(View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.txtView);
        }

        public void bindType(ListItem item) {
            mTextView.setText(((DockingEventItem) item).getDockingEvent().getId());
        }
    }

    public class ViewHolderActiveExpedition extends ViewHolder {
        private final TextView mTextView;

        public ViewHolderActiveExpedition(View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.txtView);
        }

        public void bindType(ListItem item) {
            mTextView.setText(((ActiveExpeditionItem) item).getExpedition().getId());
        }
    }

    public class ViewHolderPastExpedition extends ViewHolder {
        private final TextView mTextView;

        public ViewHolderPastExpedition(View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.txtView);
        }

        public void bindType(ListItem item) {
            mTextView.setText(((ActiveExpeditionItem) item).getExpedition().getId());
        }
    }
}

