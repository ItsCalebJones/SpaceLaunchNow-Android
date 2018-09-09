package me.calebjones.spacelaunchnow.ui.main.launches;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.data.LaunchStatus;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    public int position;
    private RealmList<Launch> launchList;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private static ListPreferences sharedPreference;
    private SimpleDateFormat sdf;
    private SimpleDateFormat df;

    public ListAdapter(Context context) {
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

        night = sharedPreference.isNightModeActive(mContext);
    }

    public void addItems(List<Launch> launchList) {

        if (this.launchList != null) {
            this.launchList.addAll(launchList);
        } else {
            this.launchList = new RealmList<>();
            this.launchList.addAll(launchList);
        }
        this.notifyDataSetChanged();
    }

    public void clear() {
        launchList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Timber.v("onCreate ViewHolder.");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.launch_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Launch launchItem = launchList.get(i);

        String[] title;
        String launchDate;

        position = i;

        //Retrieve missionType
        if (launchItem.getMission() != null) {
            Utils.setCategoryIcon(holder.categoryIcon, launchItem.getMission().getTypeName(), night);
        } else {
            if (night) {
                holder.categoryIcon.setImageResource(R.drawable.ic_unknown_white);
            } else {
                holder.categoryIcon.setImageResource(R.drawable.ic_unknown);
            }
        }

        if (launchItem.getStatus() != null && launchItem.getStatus().getId() == 2) {
            //Get launch date
            launchDate = sdf.format(launchItem.getNet());

            holder.launch_date.setText(String.format("To be determined... %s", launchDate));
        } else {
            launchDate = sdf.format(launchItem.getNet());
            holder.launch_date.setText(launchDate);
        }

        //If pad and agency exist add it to location, otherwise get whats always available
        if (launchItem.getLocation() != null) {
            holder.location.setText(launchItem.getLocation().getName());
        } else {
            holder.location.setText("Click for more information.");
        }

        if (launchItem.getName() != null) {
            title = launchItem.getName().split("\\|");
            try {
                if (title.length > 0) {
                    holder.title.setText(title[1].trim());
                    holder.mission.setText(title[0].trim());
                } else {
                    holder.title.setText(launchItem.getName());
                    if (launchItem.getMission() != null) {
                        holder.title.setText(launchItem.getMission().getName());
                    }
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                holder.title.setText(launchItem.getName());
                if (launchItem.getMission() != null) {
                    holder.title.setText(launchItem.getMission().getName());
                }

            }
        }

        holder.status.setText(launchItem.getStatus().getName());
        holder.statusCard.setCardBackgroundColor(LaunchStatus.getLaunchStatusColor(mContext, launchItem.getStatus().getId()));

        if (launchItem.getLanding() != null && launchItem.getLanding().getLandingLocation() != null) {
            holder.landingCard.setVisibility(View.VISIBLE);
            holder.landingLocation.setText(launchItem.getLanding().getLandingLocation().getAbbrev());
            holder.landingCard.setCardBackgroundColor(LaunchStatus.getLandingStatusColor(mContext, launchItem.getLanding()));
        } else {
            holder.landingCard.setVisibility(View.GONE);
        }
    }

    public String parseDateToMMyyyy(Date date) {
        return new SimpleDateFormat("MMM yyyy").format(date);
    }

    @Override
    public int getItemCount() {
        return launchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.status_pill_mini)
        CardView statusCard;
        @BindView(R.id.landing)
        TextView landingLocation;
        @BindView(R.id.landing_pill_mini)
        CardView landingCard;
        @BindView(R.id.launch_rocket)
        TextView title;
        @BindView(R.id.location)
        TextView location;
        @BindView(R.id.launch_date)
        TextView launch_date;
        @BindView(R.id.mission)
        TextView mission;
        @BindView(R.id.categoryIcon)
        ImageView categoryIcon;


        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            title.setOnClickListener(this);
            location.setOnClickListener(this);
            launch_date.setOnClickListener(this);
            mission.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final Launch launch = launchList.get(getAdapterPosition());

            Intent intent = new Intent(mContext, LaunchDetailActivity.class);
            intent.putExtra("TYPE", "launch");
            intent.putExtra("launchID", launch.getId());
            mContext.startActivity(intent);
        }
    }

    public void animateTo(List<Launch> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Launch> newModels) {
        for (int i = launchList.size() - 1; i >= 0; i--) {
            if (!newModels.contains(launchList.get(i))) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Launch> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Launch model = newModels.get(i);
            if (!launchList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Launch> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Launch model = newModels.get(toPosition);
            final int fromPosition = launchList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Launch removeItem(int position) {
        Launch model = launchList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Launch model) {
        launchList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        launchList.add(toPosition, launchList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }
}
