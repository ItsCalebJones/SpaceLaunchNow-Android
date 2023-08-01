package me.calebjones.spacelaunchnow.ui.spacecraft;

import static me.calebjones.spacelaunchnow.common.utils.LinkHandler.openCustomTab;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import android.icu.text.SimpleDateFormat;
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

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftConfig;

public class SpacecraftConfigDetailAdapter extends RecyclerView.Adapter<SpacecraftConfigDetailAdapter.ViewHolder> {

    public int position;
    private Context context;
    private Activity activity;
    private List<SpacecraftConfig> items;
    private RequestOptions requestOptions;
    private int backgroundColor = 0;
    private SimpleDateFormat sdf;

    public SpacecraftConfigDetailAdapter(Context context, Activity activity) {
        items = new ArrayList<>();
        requestOptions = new RequestOptions()
                .placeholder(R.drawable.placeholder)
                .centerCrop();
        this.context = context;
        this.activity = activity;
        sdf = Utils.getSimpleDateFormatForUI("MMMM yyyy");
        sdf.toLocalizedPattern();
    }

    public void addItems(List<SpacecraftConfig> items) {
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
        SpacecraftConfig spacecraftConfig = items.get(holder.getAdapterPosition());

        //Set up vehicle card Information
        holder.orbiterTitle.setText(spacecraftConfig.getName());
        holder.orbiterSubtitle.setText(spacecraftConfig.getCapability());

        holder.orbiterDescription.setText(spacecraftConfig.getDetails());
        holder.orbiterHistoryDescription.setText(spacecraftConfig.getHistory());

        if (backgroundColor != 0) {
            holder.orbiterTitle.setBackgroundColor(backgroundColor);
            holder.orbiterSubtitle.setBackgroundColor(backgroundColor);
        }

        if (spacecraftConfig.getDiameter() != null) {
            holder.diameter.setText(String.format(context.getString(R.string.diameter_full), spacecraftConfig.getDiameter()));
        }
        if (spacecraftConfig.getHeight() != null) {
            holder.height.setText(String.format(context.getString(R.string.height_full), spacecraftConfig.getHeight()));
        }
        if (spacecraftConfig.getPayloadCapacity() != null) {
            holder.payload.setText(String.format(context.getString(R.string.payload), spacecraftConfig.getPayloadCapacity()));
        }

        if (spacecraftConfig.getFlightLife() != null) {
            holder.flightLife.setVisibility(View.VISIBLE);
            holder.flightLife.setText(String.format(context.getString(me.calebjones.spacelaunchnow.R.string.flight_life), spacecraftConfig.getFlightLife()));
        } else {
            holder.flightLife.setVisibility(View.GONE);
        }

        if (spacecraftConfig.getInUse()){
            GlideApp.with(context)
                    .load(R.drawable.ic_checkmark)
                    .into(holder.activeIcon);
        } else {
            GlideApp.with(context)
                    .load(R.drawable.ic_failed)
                    .into(holder.activeIcon);
        }

        if (spacecraftConfig.getHumanRated() == null ){
            GlideApp.with(context)
                    .load(R.drawable.ic_question_mark)
                    .into(holder.crewIcon);
            holder.crewCapacity.setVisibility(View.GONE);
        } else if (spacecraftConfig.getHumanRated()){
            GlideApp.with(context)
                    .load(R.drawable.ic_checkmark)
                    .into(holder.crewIcon);
            holder.crewCapacity.setVisibility(View.VISIBLE);
            holder.crewCapacity.setText(String.format(context.getString(R.string.crew_capacity), spacecraftConfig.getCrewCapacity()));
        } else {
            holder.crewCapacity.setVisibility(View.GONE);
            GlideApp.with(context)
                    .load(R.drawable.ic_failed)
                    .into(holder.crewIcon);
        }

        if (spacecraftConfig.getMaidenFlight() != null) {
            holder.firstFlight.setText(sdf.format(spacecraftConfig.getMaidenFlight()));
        } else {
            holder.firstFlight.setText(R.string.unknown);
        }

        GlideApp.with(context)
                .load(spacecraftConfig.getImageUrl())
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


        if (spacecraftConfig.getWikiLink() != null && spacecraftConfig.getWikiLink().length() > 0) {
            holder.wikiButton.setVisibility(View.VISIBLE);
            holder.wikiButton.setOnClickListener(v -> openCustomTab(activity, context, spacecraftConfig.getWikiLink()));
        } else {
            holder.wikiButton.setVisibility(View.GONE);
        }

        if (spacecraftConfig.getInfoLink() != null && spacecraftConfig.getInfoLink().length() > 0) {
            holder.infoButton.setVisibility(View.VISIBLE);
            holder.infoButton.setOnClickListener(v -> openCustomTab(activity, context, spacecraftConfig.getInfoLink()));
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

        @BindView(R2.id.orbiter_image)
        ImageView orbiterImage;
        @BindView(R2.id.in_use_icon)
        ImageView activeIcon;
        @BindView(R2.id.human_rated_icon)
        ImageView crewIcon;
        @BindView(R2.id.orbiter_title)
        TextView orbiterTitle;
        @BindView(R2.id.orbiter_subtitle)
        TextView orbiterSubtitle;
        @BindView(R2.id.orbiter_name)
        TextView orbiterName;
        @BindView(R2.id.orbiter_description)
        TextView orbiterDescription;
        @BindView(R2.id.orbiter_history)
        TextView orbiterHistory;
        @BindView(R2.id.orbiter_history_description)
        TextView orbiterHistoryDescription;
        @BindView(R2.id.wikiButton)
        AppCompatButton wikiButton;
        @BindView(R2.id.infoButton)
        AppCompatButton infoButton;
        @BindView(R2.id.diameter)
        TextView diameter;
        @BindView(R2.id.height)
        TextView height;
        @BindView(R2.id.payload)
        TextView payload;
        @BindView(R2.id.crew_capacity)
        TextView crewCapacity;
        @BindView(R2.id.flight_life)
        TextView flightLife;
        @BindView(R2.id.first_flight_text)
        TextView firstFlight;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
