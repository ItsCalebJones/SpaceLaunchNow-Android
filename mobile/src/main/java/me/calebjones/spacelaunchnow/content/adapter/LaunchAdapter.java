package me.calebjones.spacelaunchnow.content.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to the UpcomingLaunchesFragment
 */
public class LaunchAdapter extends RecyclerView.Adapter<LaunchAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {
    public int position;
    private List<Launch> launchList;
    private List<Integer> mSectionPositions;
    private List<String> mSections;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private static ListPreferences sharedPreference;

    public LaunchAdapter(Context context) {
        rightNow = Calendar.getInstance();
        launchList = new ArrayList();
        mSectionPositions = new ArrayList<>();
        mSections = new ArrayList<>();
        sharedPreference = ListPreferences.getInstance(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
    }

    public void addItems(List<Launch> launchList) {
        if (this.launchList == null) {
            this.launchList = launchList;
        } else {
            this.launchList.addAll(launchList);
        }
    }

    public void clear() {
        launchList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        int m_theme;

        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreference = ListPreferences.getInstance(mContext);

        if (sharedPreference.getNightMode()) {
            night = true;
            m_theme = R.layout.dark_historical_list_item;
        } else {
            night = false;
            m_theme = R.layout.light_historical_list_item;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(m_theme, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Launch launchItem = launchList.get(i);


        String missionType;
        String title;
        String location;
        String launchDate;

        position = i;

        //Retrieve missionType
        if (launchItem.getMissions().size() != 0) {
            setCategoryIcon(holder, launchItem.getMissions().get(0).getTypeName());
        }

        if (launchItem.getStatus() == 2) {
            //Get launch date
            if (sharedPref.getBoolean("local_time", true)) {
                SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy.");
                df.toLocalizedPattern();
                Date date = new Date(launchItem.getWindowstart());
                launchDate = df.format(date);
            } else {
                launchDate = launchItem.getWindowstart();
            }

            holder.launch_date.setText(String.format("To be determined... %s", launchDate));
        } else {
            //Get launch date
            if (sharedPref.getBoolean("local_time", true)) {
                SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd yyyy hh:mm a zzz");
                df.toLocalizedPattern();
                Date date = new Date(launchItem.getWindowstart());
                launchDate = df.format(date);
            } else {
                launchDate = launchItem.getWindowstart();
            }
            holder.launch_date.setText(launchDate);
        }


        //If pad and agency exist add it to location, otherwise get whats always available
        if (launchItem.getLocation() != null){
            holder.location.setText(launchItem.getLocation().getName());
        }

        if (launchItem.getRocket().getAgencies().size() > 0) {
            title = launchItem.getRocket().getAgencies().get(0).getName() + " | " + (launchItem.getRocket().getName());
        } else {
            title = launchItem.getRocket().getName();
        }

        holder.mission.setText(launchItem.getName());
        holder.title.setText(title);
    }

    public String parseDateToMMyyyy(String time) {
        String inputPattern = "EEEE, MMM dd yyyy hh:mm a zzz";
        String outputPattern = "MMM yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    public int getItemCount() {
        return launchList.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd yyyy hh:mm a zzz");
        df.toLocalizedPattern();

        Date date = new Date(launchList.get(position).getWindowstart());

        return parseDateToMMyyyy(df.format(date));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, content, location, launch_date, mission;
        public ImageView categoryIcon;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);

            categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);
            title = (TextView) view.findViewById(R.id.launch_rocket);
            location = (TextView) view.findViewById(R.id.location);
            launch_date = (TextView) view.findViewById(R.id.launch_date);
            mission = (TextView) view.findViewById(R.id.mission);

            title.setOnClickListener(this);
            location.setOnClickListener(this);
            launch_date.setOnClickListener(this);
            mission.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            Launch launch = launchList.get(position);
            Intent intent = new Intent(mContext, LaunchDetailActivity.class);
            intent.putExtra("TYPE", "Launch");
            intent.putExtra("launch", launch);
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

    private void setCategoryIcon(ViewHolder holder, String type) {
        if (type != null) {
            switch (type) {
                case "Earth Science":
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_earth_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_earth));
                    }
                    break;
                case "Planetary Science":
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_planetary_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_planetary));
                    }
                    break;
                case "Astrophysics":
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_astrophysics_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_astrophysics));
                    }
                    break;
                case "Heliophysics":
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_heliophysics_alt_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_heliophysics_alt));
                    }
                    break;
                case "Human Exploration":
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_human_explore_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_human_explore));
                    }
                    break;
                case "Robotic Exploration":
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_robotic_explore_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_robotic_explore));
                    }
                    break;
                case "Government/Top Secret":
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_top_secret_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_top_secret));
                    }
                    break;
                case "Tourism":
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_tourism_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_tourism));
                    }
                    break;
                case "Unknown":
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_unknown_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_unknown));
                    }
                    break;
                case "Communications":
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_satellite_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_satellite));
                    }
                    break;
                case "Resupply":
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_resupply_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_resupply));
                    }
                    break;
                default:
                    if (night) {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_unknown_white));
                    } else {
                        holder.categoryIcon.setImageDrawable(
                                ContextCompat.getDrawable(mContext, R.drawable.ic_unknown));
                    }
                    break;
            }
        }
    }
}
