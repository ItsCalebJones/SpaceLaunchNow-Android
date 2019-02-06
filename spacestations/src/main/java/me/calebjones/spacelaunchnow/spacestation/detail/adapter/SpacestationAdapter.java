package me.calebjones.spacelaunchnow.spacestation.detail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.DockingEvent;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Expedition;
import me.calebjones.spacelaunchnow.spacestation.R;
import me.calebjones.spacelaunchnow.spacestation.detail.fragments.expeditions.CrewAdapter;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class SpacestationAdapter extends RecyclerView.Adapter<SpacestationAdapter.ViewHolder> {

    public int position;
    private Context context;
    private List<ListItem> items;

    public SpacestationAdapter(Context context) {
        this.context = context;
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
        private ImageView image;
        private TextView title;
        private TextView subtitle;
        private ImageView humanRated;
        private TextView dockedLocation;

        public ViewHolderDockedVehicle(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.orbiter_image);
            title = itemView.findViewById(R.id.spacestation_docked_title);
            subtitle = itemView.findViewById(R.id.spacestation_docked_subtitle);
            humanRated = itemView.findViewById(R.id.human_rated_icon);
            dockedLocation = itemView.findViewById(R.id.docked_text);
        }

        public void bindType(ListItem item) {
            DockedVehicleItem dockedVehicleItem = (DockedVehicleItem) item;
            SpacecraftStage spacecraft = dockedVehicleItem.getDockedVehicle();
            List<DockingEvent> dockingEvents = spacecraft.getDockingEvents();
            for (DockingEvent dockingEvent: dockingEvents){
                if (dockingEvent.getDeparture() == null){
                    dockedLocation.setText(dockingEvent.getDockingLocation());
                }
            }
            GlideApp.with(context)
                    .load(spacecraft.getSpacecraft().getConfiguration().getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(image);

            title.setText(spacecraft.getSpacecraft().getName());
            subtitle.setText(spacecraft.getSpacecraft().getSerialNumber());
            if (spacecraft.getSpacecraft().getConfiguration().getHumanRated() == null ){
                GlideApp.with(context)
                        .load(R.drawable.ic_question_mark)
                        .into(humanRated);
            } else if (spacecraft.getSpacecraft().getConfiguration().getHumanRated()){
                GlideApp.with(context)
                        .load(R.drawable.ic_checkmark)
                        .into(humanRated);
                } else {
                GlideApp.with(context)
                        .load(R.drawable.ic_failed)
                        .into(humanRated);
            }
        }
    }

    public class ViewHolderDockingEvent extends ViewHolder {
        private final TextView mTextView;

        public ViewHolderDockingEvent(View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.txtView);
        }

        public void bindType(ListItem item) {
            DockingEventItem dockingEventItem = (DockingEventItem) item;
            mTextView.setText(((DockingEventItem) item).getDockingEvent().getDockingLocation());
        }
    }

    public class ViewHolderActiveExpedition extends ViewHolder {
        private final TextView mTextView;
        private RecyclerView crewRecyler;
        private TextView title;
        private TextView subtitle;
        private TextView start;
        private TextView end;

        public ViewHolderActiveExpedition(View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.txtView);
            crewRecyler = itemView.findViewById(R.id.crew_recycler_view);
            title = itemView.findViewById(R.id.spacestation_active_title);
            subtitle = itemView.findViewById(R.id.spacestaion_active_subtitle);
            start = itemView.findViewById(R.id.start_date);
            end = itemView.findViewById(R.id.end_date);
        }

        public void bindType(ListItem item) {
            Expedition expedition = ((ActiveExpeditionItem) item).getExpedition();
            title.setText(expedition.getName());
            if (expedition.getStart() != null) {
                start.setVisibility(View.VISIBLE);
                start.setText(String.format(context.getString(R.string.start_fill), DateFormat.getDateInstance(DateFormat.LONG).format(expedition.getStart())));
            } else {
                start.setVisibility(View.GONE);
            }

            if (expedition.getEnd() != null) {
                end.setVisibility(View.VISIBLE);
                end.setText(String.format(context.getString(R.string.end_fill), DateFormat.getDateInstance(DateFormat.LONG).format(expedition.getEnd())));
            } else {
                end.setVisibility(View.GONE);
            }

            crewRecyler.setLayoutManager(new LinearLayoutManager(context));
            crewRecyler.setAdapter(new CrewAdapter(context, expedition.getCrew()));
        }
    }

    public class ViewHolderPastExpedition extends ViewHolder {
        private final TextView mTextView;

        public ViewHolderPastExpedition(View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.txtView);
        }

        public void bindType(ListItem item) {
            mTextView.setText(((ActiveExpeditionItem) item).getExpedition().getName());
        }
    }
}

