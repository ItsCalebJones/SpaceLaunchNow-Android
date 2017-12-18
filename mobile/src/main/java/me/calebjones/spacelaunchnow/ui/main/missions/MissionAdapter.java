package me.calebjones.spacelaunchnow.ui.main.missions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Mission;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to the UpcomingLaunchesFragment
 */
public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {
    public int position;
    private RealmList<Mission> missionList;

    private Context mContext;
    private Boolean night;
    private static ListPreferences sharedPreference;

    public MissionAdapter(Context context) {
        missionList = new RealmList<>();
        sharedPreference = ListPreferences.getInstance(context);
        this.mContext = context;
    }

    public void addItems(List<Mission> missionList) {

        if (this.missionList == null) {
            this.missionList.addAll(missionList);
        } else {
            this.missionList = new RealmList<>();
            this.missionList.addAll(missionList);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        missionList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        sharedPreference = ListPreferences.getInstance(mContext);

        if (sharedPreference.isNightModeActive(mContext)) {
            night = true;
        } else {
            night = false;
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mission_list_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Mission mission = missionList.get(holder.getAdapterPosition());

        //Retrieve missionType
        Timber.v("Position: %s - Type - %s", holder.getAdapterPosition(), mission.getTypeName());
        Utils.setCategoryIcon(holder.categoryIcon, mission.getTypeName(), night);

        holder.mission_name.setText(mission.getName());
        holder.mission_summary.setText(mission.getDescription());

        if (mission.getLaunch() != null && mission.getLaunch().getId() != null) {

            if (mission.getInfoURL() != null) {
                ((MainActivity) mContext).mayLaunchUrl(Uri.parse(mission.getInfoURL()));
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
                        if (mission.getLaunch().getName() != null) {
                            String[] launch = mission.getLaunch().getName().split(Pattern.quote(" |"));
                            holder.mission_vehicle.setText(launch[0]);
                            holder.mission_vehicle.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.mission_vehicle.setVisibility(View.GONE);
                    }

                    //If we can find the date of the launch add it to the card.
                    if (mission.getLaunch().getNet() != null) {
                        SimpleDateFormat outformat = new SimpleDateFormat("MMMM dd, yyyy");
                        outformat.toLocalizedPattern();

                        Date date;
                        String str = "";

                        date = mission.getLaunch().getNet();
                        str = outformat.format(date);
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
        if(mission.getInfoURL() != null) {
            if (mission.getInfoURL().length() == 0) {
                holder.infoButton.setVisibility(View.INVISIBLE);
            } else {
                ((MainActivity) mContext).mayLaunchUrl(Uri.parse(mission.getInfoURL()));
                holder.infoButton.setVisibility(View.VISIBLE);
            }
        } else {
            holder.infoButton.setVisibility(View.INVISIBLE);
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
        if (section.matches("[1-9.?@#$%^&*() ]")) {
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
                    Intent exploreIntent = new Intent(mContext, LaunchDetailActivity.class);
                    exploreIntent.putExtra("TYPE", "launch");
                    exploreIntent.putExtra("launchID", missionList
                            .get(position)
                            .getLaunch()
                            .getId());
                    mContext.startActivity(exploreIntent);
                    break;
                case R.id.infoButton:
                    Timber.v("Info : %s", missionList
                            .get(position)
                            .getInfoURL());
                    Activity activity = (Activity) mContext;

                    Utils.openCustomTab(activity, mContext, missionList
                            .get(position)
                            .getInfoURL());
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
