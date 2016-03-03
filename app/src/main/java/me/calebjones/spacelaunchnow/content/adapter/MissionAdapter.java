package me.calebjones.spacelaunchnow.content.adapter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Mission;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.customtab.CustomTabActivityHelper;
import me.calebjones.spacelaunchnow.utils.customtab.CustomTabHelper;
import me.calebjones.spacelaunchnow.utils.customtab.WebViewFallback;
import timber.log.Timber;

/**
 * This adapter takes data from SharedPreference/LoaderService and applies it to the UpcomingLaunchesFragment
 */
public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {
    public int position;
    private List<Mission> missionList;
    private List<Integer> mSectionPositions;
    private List<String> mSections;
    private Context mContext;
    private Context aContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private static SharedPreference sharedPreference;

    public MissionAdapter(Context context, Context aContext) {
        rightNow = Calendar.getInstance();
        missionList = new ArrayList();
        mSectionPositions = new ArrayList<>();
        mSections = new ArrayList<>();
        sharedPreference = SharedPreference.getInstance(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
        this.aContext = aContext;
    }

    public void addItems(List<Mission> missionList) {
        if (this.missionList == null) {
            this.missionList = missionList;
        } else {
            this.missionList.addAll(missionList);
        }
    }

    public void clear() {
        missionList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        int m_theme;

        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreference = SharedPreference.getInstance(mContext);

        if (sharedPreference.getNightMode()) {
            night = true;
            m_theme = R.layout.dark_mission_list_item;
        } else {
            night = false;
            m_theme = R.layout.light_mission_list_item;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(m_theme, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Mission mission = missionList.get(i);

        //Retrieve missionType

        setCategoryIcon(holder, mission.getTypeName());

        holder.mission_name.setText(mission.getName());
        holder.mission_summary.setText(mission.getDescription());

        if (mission.getLaunch() != null && mission.getLaunch().getId() != null) {

            if (mission.getLaunch().getVidURL() != null) {
                ((MainActivity) aContext).mayLaunchUrl(Uri.parse(mission.getLaunch().getVidURL()));
            }
            //If there's no info on the launch hide the button and no need to check for launch date.
            if (mission.getLaunch().getId() == 0) {
                holder.launchButton.setVisibility(View.GONE);
                holder.mission_vehicle.setVisibility(View.GONE);
                holder.mission_date.setVisibility(View.GONE);
            } else {
                if (mission.getLaunch().getId() != null && mission.getLaunch().getId() != 0) {

                    //If we can find the name of the launch add it to the card.
                    if (mission.getLaunch() != null) {
                        if (mission.getLaunch().getRocket() != null) {
                            if (mission.getLaunch().getRocket().getName() != null) {
                                holder.mission_vehicle.setText(mission.getLaunch().getRocket().getName());
                                holder.mission_vehicle.setVisibility(View.VISIBLE);
                            } else {
                                holder.mission_vehicle.setText(mission.getLaunch().getName());
                                holder.mission_vehicle.setVisibility(View.VISIBLE);
                            }
                        }  else {
                            holder.mission_vehicle.setText(mission.getLaunch().getName());
                            holder.mission_vehicle.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.mission_vehicle.setVisibility(View.GONE);
                    }

                    //If we can find the date of the launch add it to the card.
                    if (mission.getLaunch().getNet() != null) {
                        SimpleDateFormat informat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss zzz");
                        SimpleDateFormat outformat = new SimpleDateFormat("MMM dd, yyyy");
                        outformat.toLocalizedPattern();

                        Date date;
                        String str = "";

                        try {
                            date = informat.parse(mission.getLaunch().getNet());
                            str = outformat.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            holder.mission_date.setVisibility(View.GONE);
                        }
                        holder.mission_date.setText(str);
                        holder.mission_date.setVisibility(View.VISIBLE);
                    } else {
                        holder.mission_date.setVisibility(View.GONE);
                    }

                    holder.launchButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.mission_vehicle.setVisibility(View.GONE);
            holder.mission_date.setVisibility(View.GONE);
            holder.launchButton.setVisibility(View.GONE);
        }
        if (mission.getInfoURL().length() == 0) {
            holder.infoButton.setVisibility(View.INVISIBLE);
        } else {
            ((MainActivity) aContext).mayLaunchUrl(Uri.parse(mission.getInfoURL()));
            holder.infoButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
            String section = missionList.get(position).getName().substring(0, 1);
            if (section.matches("\\d+(?:\\.\\d+)?")) {
                section = "#";
            }
        return section;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mission_name, mission_summary, launchButton, infoButton, mission_vehicle,
                mission_date;
        public ImageView categoryIcon;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);

            categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);
            mission_name = (TextView) view.findViewById(R.id.mission_name);
            mission_summary = (TextView) view.findViewById(R.id.mission_summary);
            launchButton = (TextView) view.findViewById(R.id.launchButton);
            infoButton = (TextView) view.findViewById(R.id.infoButton);
            mission_vehicle = (TextView) view.findViewById(R.id.mission_vehicle);
            mission_date = (TextView) view.findViewById(R.id.mission_date);

            launchButton.setOnClickListener(this);
            infoButton.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            Timber.d("%s clicked.", v);
            switch (v.getId()) {
                case R.id.launchButton:
                    Timber.v("Launch: %s", missionList.get(position).getLaunch());
                    Intent exploreIntent = new Intent(aContext, LaunchDetailActivity.class);
                    exploreIntent.putExtra("TYPE", "LaunchID");
                    int id = missionList.get(position).getLaunch().getId();
                    exploreIntent.putExtra("id", id);
                    aContext.startActivity(exploreIntent);
                    break;
                case R.id.infoButton:
                    Timber.v("Info : %s", missionList.get(position).getInfoURL());
                    Activity activity = (Activity) aContext;
                    Utils.openCustomTab(activity, aContext, missionList.get(position).getInfoURL());
                    break;
            }
        }
    }

    public void animateTo(List<Mission> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Mission> newModels) {
        for (int i = missionList.size() - 1; i >= 0; i--) {
            if (!newModels.contains(missionList.get(i))) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Mission> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Mission model = newModels.get(i);
            if (!missionList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Mission> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Mission model = newModels.get(toPosition);
            final int fromPosition = missionList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Mission removeItem(int position) {
        Mission model = missionList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Mission model) {
        missionList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        missionList.add(toPosition, missionList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    private void setCategoryIcon(ViewHolder holder, String type) {
        switch (type){
            case "Earth Science":
                if(night){
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_earth_white));
                } else {
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_earth));
                }
                break;
            case "Planetary Science":
                if(night){
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_planetary_white));
                } else {
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_planetary));
                }
                break;
            case "Astrophysics":
                if(night){
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_astrophysics_white));
                } else {
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_astrophysics));
                }
                break;
            case "Heliophysics":
                if(night){
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_heliophysics_alt_white));
                } else {
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_heliophysics_alt));
                }
                break;
            case "Human Exploration":
                if(night){
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_human_explore_white));
                } else {
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_human_explore));
                }
                break;
            case "Robotic Exploration":
                if(night){
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_robotic_explore_white));
                } else {
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_robotic_explore));
                }
                break;
            case "Government/Top Secret":
                if(night){
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_top_secret_white));
                } else {
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_top_secret));
                }
                break;
            case "Tourism":
                if(night){
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_tourism_white));
                } else {
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_tourism));
                }
                break;
            case "Unknown":
                if(night){
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_unknown_white));
                } else {
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_unknown));
                }
                break;
            case "Communications":
                if(night){
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_satellite_white));
                } else {
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_satellite));
                }
                break;
            case "Resupply":
                if(night){
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_resupply_white));
                } else {
                    holder.categoryIcon.setImageDrawable(
                            ContextCompat.getDrawable(mContext, R.drawable.ic_resupply));
                }
                break;
            default:
                if(night){
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
