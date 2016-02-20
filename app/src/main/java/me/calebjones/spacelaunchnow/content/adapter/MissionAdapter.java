package me.calebjones.spacelaunchnow.content.adapter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private static SharedPreference sharedPreference;

    public MissionAdapter(Context context) {
        rightNow = Calendar.getInstance();
        missionList = new ArrayList();
        mSectionPositions = new ArrayList<>();
        mSections = new ArrayList<>();
        sharedPreference = SharedPreference.getInstance(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
    }

    public void addItems(List<Mission> missionList) {
        if (this.missionList == null) {
            this.missionList = missionList;
        } else {
            //Note: If you're populating with a large dataset, you might want to
            //call the following code asychronously.
            mSections.clear();
            mSectionPositions.clear();

            //data is your adapter's dataset
            for (int i = 0, length = missionList.size(); i < length; i++) {
                String section = missionList.get(i).getName().substring(0,1);
                if (section.matches("\\d+(?:\\.\\d+)?")){
                    section = "#";
                }
                Timber.v("Adding section for %s as %s", missionList.get(i).getName(), section);
                if (!TextUtils.isEmpty(section) && !mSections.contains(section)) {
                    //This just adds a new section for each new letter
                    mSections.add(section);
                    mSectionPositions.add(i);
                }
            }
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
            m_theme = R.layout.dark_mission_list_item;
        } else {
            m_theme = R.layout.light_mission_list_item;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(m_theme, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Mission mission = missionList.get(i);

        holder.mission_name.setText(mission.getName());
        holder.mission_summary.setText(mission.getDescription());

        if (mission.getLaunch() != null && mission.getLaunch().getId() != null){

            if (mission.getLaunch().getVidURL() != null){
                ((MainActivity)mContext).mayLaunchUrl(Uri.parse(mission.getLaunch().getVidURL()));
            }
            //If there's no info on the launch hide the button and no need to check for launch date.
            if(mission.getLaunch().getId() == 0){
                holder.launchButton.setVisibility(View.GONE);
                holder.mission_vehicle.setVisibility(View.GONE);
                holder.mission_date.setVisibility(View.GONE);
            } else {
                if (mission.getLaunch().getId() != null &&  mission.getLaunch().getId() != 0){

                    //If we can find the name of the launch add it to the card.
                    if (mission.getLaunch().getName() != null){
                        holder.mission_vehicle.setText(mission.getLaunch().getName());
                        holder.mission_vehicle.setVisibility(View.VISIBLE);
                    } else {
                        holder.mission_vehicle.setVisibility(View.GONE);
                    }

                    //If we can find the date of the launch add it to the card.
                    if (mission.getLaunch().getNet() != null){
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
        if (mission.getInfoURL().length() == 0){
            holder.infoButton.setVisibility(View.INVISIBLE);
        } else {
            ((MainActivity)mContext).mayLaunchUrl(Uri.parse(mission.getInfoURL()));
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
        int finalPosition = getSectionForPosition(position);
        return mSections.get(finalPosition);
    }

    public int getSectionForPosition(int i) {
        for (int j = 0, length = mSectionPositions.size(); j < length; j++) {
            int sectionPosition = mSectionPositions.get(j);
            if (i <= sectionPosition) {
                return j;
            }
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mission_name, mission_summary, launchButton, infoButton, mission_vehicle,
                mission_date;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);

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
                    Intent exploreIntent = new Intent(mContext, LaunchDetailActivity.class);
                    exploreIntent.putExtra("TYPE", "LaunchID");
                    int id = missionList.get(position).getLaunch().getId();
                    exploreIntent.putExtra("id", id);
                    mContext.startActivity(exploreIntent);
                    break;
                case R.id.infoButton:
                    Timber.v("Info : %s", missionList.get(position).getInfoURL());
                    Activity activity = (Activity)mContext;
                    Utils.openCustomTab(activity, mContext, missionList.get(position).getInfoURL());
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
}
