package me.calebjones.spacelaunchnow.starship.ui.upcoming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.google.android.material.tabs.TabLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.content.LaunchStatusUtil;
import me.calebjones.spacelaunchnow.common.prefs.ListPreferences;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;
import me.calebjones.spacelaunchnow.data.models.main.starship.Notice;
import me.calebjones.spacelaunchnow.events.detail.EventDetailsActivity;
import me.spacelaunchnow.starship.R;
import me.spacelaunchnow.starship.R2;

public class CombinedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> combinedList;
    private Context context;
    private final static int TYPE_EVENT=1, TYPE_LAUNCHLIST=2;
    public int position;
    private RealmList<LaunchList> launchList;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private static ListPreferences sharedPreference;
    private final ListPreloader.PreloadSizeProvider sizeProvider = new ViewPreloadSizeProvider();
    private SimpleDateFormat sdf;
    private SimpleDateFormat df;
    private int color;

    public CombinedAdapter(Context context, boolean night) {
        combinedList = new ArrayList<>();
        this.context = context;
        rightNow = Calendar.getInstance();
        launchList = new RealmList<>();
        sharedPreference = ListPreferences.getInstance(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
        if (sharedPref.getBoolean("local_time", true)) {
            sdf = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy");
        } else {
            sdf = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy zzz");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        if (sharedPref.getBoolean("24_hour_mode", false)) {
            df = Utils.getSimpleDateFormatForUI("EEEE, MMMM dd, yyyy - HH:mm");
        } else {
            df = Utils.getSimpleDateFormatForUI("EEEE, MMMM dd, yyyy - hh:mm a zzz");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        this.night = night;
        if (night){
            color = ContextCompat.getColor(mContext, me.calebjones.spacelaunchnow.common.R.color.material_color_white);
        } else {
            color = ContextCompat.getColor(mContext, me.calebjones.spacelaunchnow.common.R.color.material_color_black);
        }
    }

    public void addItems(ArrayList<Object> events) {
        this.combinedList = events;
        this.notifyDataSetChanged();
    }

    public void clear() {
        combinedList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public int getItemViewType(int position) {
        if (combinedList.get(position) instanceof Event) {
            return TYPE_EVENT;
        } else if (combinedList.get(position) instanceof LaunchList) {
            return TYPE_LAUNCHLIST;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = 0;
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case TYPE_EVENT:
                layout=R.layout.event_list_item;
                View eventView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
                viewHolder = new EventListViewHolder(eventView);
                break;
            case TYPE_LAUNCHLIST:
                layout=R.layout.launch_list_item;
                View launchView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
                viewHolder = new LaunchListViewHolder(launchView);
                break;
            default:
                viewHolder=null;
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType=holder.getItemViewType();
        switch (viewType){
            case TYPE_EVENT:
                Event event = (Event) combinedList.get(position);
                ((EventListViewHolder)holder).showDetails(event);
                break;
            case TYPE_LAUNCHLIST:
                LaunchList launchList = (LaunchList) combinedList.get(position);
                ((LaunchListViewHolder)holder).showDetails(launchList);
                break;
        }
    }

    public class EventListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView location;
        private TextView eventDate;
        private TextView eventTitle;
        private ImageView eventIcon;
        private View rootview;

        public EventListViewHolder(View view) {
            super(view);
            // Initiate view
            location = view.findViewById(R.id.event_location);
            eventDate = view.findViewById(R.id.event_date);
            eventTitle = view.findViewById(R.id.event_title);
            eventIcon = view.findViewById(R.id.event_icon);
            rootview = view.findViewById(R.id.rootview);
        }
        public void showDetails(Event event){
            // Attach values for each item
            if (event.getFeatureImage() != null) {
                GlideApp.with(mContext)
                        .load(event.getFeatureImage())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .into(eventIcon);
            }
            String date = DateFormat.getDateInstance(DateFormat.LONG).format(event.getDate());

            eventTitle.setText(event.getName());
            eventDate.setText(date);
            location.setText(event.getLocation());
            rootview.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Event event = (Event) combinedList.get(getAdapterPosition());
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("eventId", event.getId());
            context.startActivity(intent);
        }
    }

    public class LaunchListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView status;
        private CardView statusCard;
        private TextView landingLocation;
        private CardView landingCard;
        private TextView orbitName;
        private CardView orbitCard;
        private TextView title;
        private TextView location;
        private TextView launch_date;
        private TextView mission;
        private ImageView categoryIcon;

        public LaunchListViewHolder(View view) {
            super(view);
            status = view.findViewById(R.id.status);
            statusCard = view.findViewById(R.id.status_pill_mini);
            landingLocation = view.findViewById(R.id.landing);
            landingCard = view.findViewById(R.id.landing_pill_mini);
            orbitName = view.findViewById(R.id.launcher_name);
            orbitCard = view.findViewById(R.id.launcher_pill_mini);
            title = view.findViewById(R.id.launch_rocket);
            location = view.findViewById(R.id.location);
            launch_date = view.findViewById(R.id.launch_date);
            mission = view.findViewById(R.id.mission);
            categoryIcon = view.findViewById(R.id.categoryIcon);

            categoryIcon.setOnClickListener(this);
            title.setOnClickListener(this);
            location.setOnClickListener(this);
            launch_date.setOnClickListener(this);
            mission.setOnClickListener(this);
            // Initiate view
        }
        public void showDetails(LaunchList launchItem){
            // Attach values for each item
            String[] title_text;
            String launchDate;

            //Retrieve missionType
            if (launchItem.getImage() != null) {
                GlideApp.with(mContext)
                        .load(launchItem.getImage())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .into(categoryIcon);
            } else {
                if (launchItem.getMission() != null) {

                    GlideApp.with(mContext)
                            .load(Utils.getCategoryIcon(launchItem.getMissionType()))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .circleCrop()
                            .transform(new ColorFilterTransformation(color))
                            .into(categoryIcon);

                } else {
                    GlideApp.with(context)
                            .load(me.calebjones.spacelaunchnow.common.R.drawable.ic_unknown)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .circleCrop()
                            .transform(new ColorFilterTransformation(color))
                            .into(categoryIcon);
                }
            }

            if (launchItem.getStatus() != null && launchItem.getStatus().getId() == 2) {
                //Get launch date
                launchDate = sdf.format(launchItem.getNet());

                launch_date.setText(launchDate);
            } else {
                launchDate = sdf.format(launchItem.getNet());
                launch_date.setText(launchDate);
            }

            //If pad and agency exist add it to location, otherwise get whats always available
            if (launchItem.getLocation() != null) {
                location.setText(launchItem.getLocation());
            } else {
                location.setText(context.getString(me.calebjones.spacelaunchnow.common.R.string.click_for_info));
            }

            if (launchItem.getName() != null) {
                title_text = launchItem.getName().split("\\|");
                try {
                    if (title_text.length > 0) {
                        title.setText(title_text[1].trim());
                        mission.setText(title_text[0].trim());
                    } else {
                        title.setText(launchItem.getName());
                        if (launchItem.getMission() != null) {
                            title.setText(launchItem.getMission());
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException exception) {
                    title.setText(launchItem.getName());
                    if (launchItem.getMission() != null) {
                        title.setText(launchItem.getMission());
                    }

                }
            }

            if (launchItem.getLanding() != null) {
                landingCard.setVisibility(View.VISIBLE);
                landingLocation.setText(launchItem.getLanding());
                landingCard.setCardBackgroundColor(LaunchStatusUtil.getLandingStatusColor(context, launchItem.getLandingSuccess()));
            } else {
                landingCard.setVisibility(View.GONE);
            }

            if (launchItem.getOrbit() != null) {
                orbitCard.setVisibility(View.VISIBLE);
                orbitName.setText(launchItem.getOrbit());
            } else {
                orbitCard.setVisibility(View.GONE);
            }

            status.setText(launchItem.getStatus().getName());
            statusCard.setCardBackgroundColor(LaunchStatusUtil.getLaunchStatusColor(context, launchItem.getStatus().getId()));
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final LaunchList launch = (LaunchList) combinedList.get(getAdapterPosition());

            Intent intent = new Intent(context, LaunchDetailActivity.class);
            intent.putExtra("TYPE", "launch");
            intent.putExtra("launchID", launch.getId());
            context.startActivity(intent);
        }
    }


    @Override
    public int getItemCount() {
        return combinedList.size();
    }
}
