package me.calebjones.spacelaunchnow.wear.launch;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.wear.launchdetail.LaunchDetail;
import me.calebjones.spacelaunchnow.wear.R;
import me.calebjones.spacelaunchnow.wear.content.ContentManager;
import me.calebjones.spacelaunchnow.wear.utils.Utils;
import timber.log.Timber;

public class LaunchAdapter extends RecyclerView.Adapter<LaunchViewHolder>{

    private int category;
    private RealmResults<Launch> launchList;
    private ContentManager contentManager;
    private SimpleDateFormat sdf;
    private Context context;


    public LaunchAdapter(Context context, int category, ContentManager contentManager) {
        this.context = context;
        this.category = category;
        this.contentManager = contentManager;
        sdf = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy");
        loadData();
    }

    public void loadData() {
        launchList = contentManager.getLaunchList(this.category);
        notifyDataSetChanged();
    }

    @Override
    public LaunchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.launch_list_item, parent, false);
        return new LaunchViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(LaunchViewHolder holder, int position) {
        Launch launch = launchList.get(position);

        String[] title;
        String launchDate;

        if (launch != null && launch.getMissions() != null
                && launch.getMissions().size() > 0
                && launch.getMissions().get(0) != null
                && launch.getMissions().get(0).getTypeName() != null) {
            Utils.setCategoryIcon(holder.launchIcon, launch.getMissions().get(0).getTypeName());
        } else {
            holder.launchIcon.setImageResource(R.drawable.ic_unknown_white);
        }

        if (launch.getStatus() != null && launch.getStatus() == 2) {
            //Get launch date
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = launch.getNet();
            launchDate = sdf.format(date);

        } else {
            sdf.toLocalizedPattern();
            Date date = launch.getNet();
            launchDate = sdf.format(date);
        }
        holder.launchDate.setText(launchDate);

        if (launch.getName() != null) {
            title = launch.getName().split("\\|");
            try {
                if (title.length > 0) {
                    holder.launchRocket.setText(title[1].trim());
                    holder.launchMission.setText(title[0].trim());
                } else {
                    holder.launchRocket.setText(launch.getName());
                    if (launch.getMissions().size() > 0) {
                        holder.launchMission.setText(launch.getMissions().get(0).getName());
                    }
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                holder.launchRocket.setText(launch.getName());
                if (launch.getMissions().size() > 0) {
                    holder.launchMission.setText(launch.getMissions().get(0).getName());
                }
            }
        }

        //If pad and agency exist add it to location, otherwise get whats always available
        if (launch.getLocation() != null) {
            holder.launchLocation.setText(launch.getLocation().getName());
        }
    }

    @Override
    public int getItemCount() {
        return launchList.size();
    }

    public void setCategory(int category) {
        this.category = category;
        contentManager.setCategory(this.category);
    }

    public void onClick(int position) {
        Intent intent = new Intent(context, LaunchDetail.class);
        intent.putExtra("launchId", launchList.get(position).getId());
        context.startActivity(intent);
        Timber.v("Launch: %s Position: %s", launchList.get(position).getName(), position);
    }

    public void cleanup() {
        contentManager.cleanup();
    }
}
