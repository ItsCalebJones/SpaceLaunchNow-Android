package me.calebjones.spacelaunchnow.events.list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.events.R;
import me.calebjones.spacelaunchnow.events.R2;
import me.calebjones.spacelaunchnow.events.detail.EventDetailsActivity;


public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {


    private List<Event> events;
    private Context context;

    public EventRecyclerViewAdapter(Context context) {
        events = new ArrayList<>();
        this.context = context;
    }

    public void addItems(List<Event> events) {
        this.events = events;
        this.notifyDataSetChanged();
    }

    public void clear() {
        events = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = events.get(position);
        holder.eventTitle.setText(holder.mItem.getName());
        holder.eventDate.setText(DateFormat.getDateInstance(DateFormat.LONG).format(holder.mItem.getDate()));
        holder.eventDescription.setText(holder.mItem.getDescription());
        holder.eventType.setText(holder.mItem.getType().getName());

        if (holder.mItem.getVideoUrl() != null){
            holder.watchLive.setVisibility(View.VISIBLE);
        } else {
            holder.watchLive.setVisibility(View.GONE);
        }

        GlideApp.with(context)
                .load(holder.mItem.getFeatureImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.eventImage);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.event_image)
        ImageView eventImage;
        @BindView(R2.id.event_description)
        TextView eventDescription;
        @BindView(R2.id.event_title)
        TextView eventTitle;
        @BindView(R2.id.event_date)
        TextView eventDate;
        @BindView(R2.id.event_type)
        TextView eventType;
        @BindView(R2.id.details)
        AppCompatButton details;
        @BindView(R2.id.watchButton)
        AppCompatImageButton watchLive;
        public Event mItem;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R2.id.details)
        void onClick(View v){
            Event event = events.get(getAdapterPosition());
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("eventId", event.getId());
            context.startActivity(intent);
        }

        @OnClick(R2.id.watchButton)
        void onWawtchClick(View v){
            Event event = events.get(getAdapterPosition());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(event.getVideoUrl()));
            context.startActivity(i);
        }
    }
}
