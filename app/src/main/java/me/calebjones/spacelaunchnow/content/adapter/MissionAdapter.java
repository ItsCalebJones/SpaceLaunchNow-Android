package me.calebjones.spacelaunchnow.content.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.Mission;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import timber.log.Timber;

/**
 * This adapter takes data from SharedPreference/LoaderService and applies it to the LaunchesFragment
 */
public class MissionAdapter extends RecyclerView.Adapter<MissionAdapter.ViewHolder>{
    public int position;
    private List<Mission> missionList;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private static SharedPreference sharedPreference;

    public MissionAdapter(Context context) {
        rightNow = Calendar.getInstance();
        missionList = new ArrayList();
        sharedPreference = SharedPreference.getInstance(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
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
            if(mission.getLaunch().getId() == 0){
                holder.launchButton.setVisibility(View.GONE);
            } else {
                if (mission.getLaunch().getId() != null &&  mission.getLaunch().getId() != 0){
                    holder.launchButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.launchButton.setVisibility(View.GONE);
        }
        if (mission.getInfoURL().length() == 0){
            holder.infoButton.setVisibility(View.INVISIBLE);
        } else {
            holder.infoButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return missionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mission_name, mission_summary, launchButton, infoButton;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);

            mission_name = (TextView) view.findViewById(R.id.mission_name);
            mission_summary = (TextView) view.findViewById(R.id.mission_summary);
            launchButton = (TextView) view.findViewById(R.id.launchButton);
            infoButton = (TextView) view.findViewById(R.id.infoButton);

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
                    exploreIntent.putExtra("id", (missionList.get(position).getLaunch().getId()));
                    mContext.startActivity(exploreIntent);
                    break;
                case R.id.infoButton:
                    Timber.v("Info : %s", missionList.get(position).getInfoURL());
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(missionList.get(position).getInfoURL()));
                    mContext.startActivity(i);
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
