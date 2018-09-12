package me.calebjones.spacelaunchnow.ui.orbiter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.main.Orbiter;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;

public class OrbiterDetailAdapter extends RecyclerView.Adapter<OrbiterDetailAdapter.ViewHolder> {

    public int position;
    private Context context;
    private Activity activity;
    private List<Orbiter> items;
    private RequestOptions requestOptions;
    private int backgroundColor = 0;
    private SimpleDateFormat sdf;

    public OrbiterDetailAdapter(Context context, Activity activity) {
        items = new ArrayList<>();
        requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .centerCrop();
        this.context = context;
        this.activity = activity;
        sdf = Utils.getSimpleDateFormatForUI("MMMM yyyy");
        sdf.toLocalizedPattern();
    }

    public void addItems(List<Orbiter> items) {
        if (this.items != null) {
            this.items.addAll(items);
        } else {
            this.items = new RealmList<>();
            this.items.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orbiter_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        Orbiter orbiter = items.get(holder.getAdapterPosition());

        //Set up vehicle card Information
        holder.orbiterTitle.setText(String.format(context.getString(R.string.spacecraft_format), orbiter.getName()));
        holder.orbiterSubtitle.setText(orbiter.getAgency());

        holder.orbiterName.setText(String.format(context.getString(R.string.spacecraft_details), orbiter.getName()));
        holder.orbiterDescription.setText(orbiter.getDetails());

        holder.orbiterHistory.setText(String.format(context.getString(R.string.spacecraft_history), orbiter.getName()));
        holder.orbiterHistoryDescription.setText(orbiter.getHistory());

        if (backgroundColor != 0) {
            holder.orbiterTitle.setBackgroundColor(backgroundColor);
            holder.orbiterSubtitle.setBackgroundColor(backgroundColor);
        }

        if (orbiter.getDiameter() != null) {
            holder.diameter.setText(String.format(context.getString(R.string.diameter_full), orbiter.getDiameter()));
        }
        if (orbiter.getHeight() != null) {
            holder.height.setText(String.format(context.getString(R.string.height_full), orbiter.getHeight()));
        }
        if (orbiter.getPayloadCapacity() != null) {
            holder.payload.setText(String.format(context.getString(R.string.payload), orbiter.getPayloadCapacity()));
        }

        if (orbiter.getFlightLife() != null) {
            holder.flightLife.setVisibility(View.VISIBLE);
            holder.flightLife.setText(orbiter.getFlightLife());
        } else {
            holder.flightLife.setVisibility(View.GONE);
        }

        if (orbiter.getInUse()){
            GlideApp.with(context)
                    .load(R.drawable.ic_checkmark)
                    .into(holder.activeIcon);
        } else {
            GlideApp.with(context)
                    .load(R.drawable.ic_failed)
                    .into(holder.activeIcon);
        }

        if (orbiter.getHumanRated() == null ){
            GlideApp.with(context)
                    .load(R.drawable.ic_question_mark)
                    .into(holder.crewIcon);
            holder.crewCapacity.setVisibility(View.GONE);
        } else if (orbiter.getHumanRated()){
            GlideApp.with(context)
                    .load(R.drawable.ic_checkmark)
                    .into(holder.crewIcon);
            holder.crewCapacity.setVisibility(View.VISIBLE);
            holder.crewCapacity.setText(String.format(context.getString(R.string.crew_capacity), orbiter.getCrewCapacity()));
        } else {
            holder.crewCapacity.setVisibility(View.GONE);
            GlideApp.with(context)
                    .load(R.drawable.ic_failed)
                    .into(holder.crewIcon);
        }

        if (orbiter.getMaidenFlight() != null) {
            holder.firstFlight.setText(sdf.format(orbiter.getMaidenFlight()));
        } else {
            holder.firstFlight.setText(R.string.unknown);
        }

        GlideApp.with(context)
                .load(orbiter.getImageURL())
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.orbiterImage.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.orbiterImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.orbiterImage);


        if (orbiter.getWikiLink() != null && orbiter.getWikiLink().length() > 0) {
            holder.wikiButton.setVisibility(View.VISIBLE);
            holder.wikiButton.setOnClickListener(v -> Utils.openCustomTab(activity, context, orbiter.getWikiLink()));
        } else {
            holder.wikiButton.setVisibility(View.GONE);
        }

        if (orbiter.getInfoLink() != null && orbiter.getInfoLink().length() > 0) {
            holder.infoButton.setVisibility(View.VISIBLE);
            holder.infoButton.setOnClickListener(v -> Utils.openCustomTab(activity, context, orbiter.getInfoLink()));
        } else {
            holder.infoButton.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateColor(int color) {

        backgroundColor = color;

        backgroundColor = color;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.orbiter_image)
        ImageView orbiterImage;
        @BindView(R.id.in_use_icon)
        ImageView activeIcon;
        @BindView(R.id.human_rated_icon)
        ImageView crewIcon;
        @BindView(R.id.orbiter_title)
        TextView orbiterTitle;
        @BindView(R.id.orbiter_subtitle)
        TextView orbiterSubtitle;
        @BindView(R.id.orbiter_name)
        TextView orbiterName;
        @BindView(R.id.orbiter_description_expand)
        View orbiterDescriptionExpand;
        @BindView(R.id.orbiter_description)
        ExpandableTextView orbiterDescription;
        @BindView(R.id.orbiter_history)
        TextView orbiterHistory;
        @BindView(R.id.orbiter_history_description)
        ExpandableTextView orbiterHistoryDescription;
        @BindView(R.id.orbiter_history_expand)
        View orbiterHistoryExpand;
        @BindView(R.id.wikiButton)
        AppCompatButton wikiButton;
        @BindView(R.id.infoButton)
        AppCompatButton infoButton;
        @BindView(R.id.diameter)
        TextView diameter;
        @BindView(R.id.height)
        TextView height;
        @BindView(R.id.payload)
        TextView payload;
        @BindView(R.id.crew_capacity)
        TextView crewCapacity;
        @BindView(R.id.flight_life)
        TextView flightLife;
        @BindView(R.id.first_flight_text)
        TextView firstFlight;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.orbiter_history_expand)
        public void onHistoryViewClicked() {
            orbiterHistoryDescription.toggle();
            if (orbiterHistoryDescription.isExpanded()) {
                orbiterHistoryExpand.animate().rotation(0).start();
            } else {
                orbiterHistoryExpand.animate().rotation(180).start();
            }
        }

        @OnClick(R.id.orbiter_description_expand)
        public void onDescriptionViewClicked() {
            orbiterDescription.toggle();
            if (orbiterDescription.isExpanded()) {
                orbiterDescriptionExpand.animate().rotation(0).start();
            } else {
                orbiterDescriptionExpand.animate().rotation(180).start();
            }
        }
    }
}
