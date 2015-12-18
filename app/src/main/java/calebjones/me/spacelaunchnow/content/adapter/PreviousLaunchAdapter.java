package calebjones.me.spacelaunchnow.content.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import calebjones.me.spacelaunchnow.LaunchApplication;
import calebjones.me.spacelaunchnow.R;
import calebjones.me.spacelaunchnow.content.models.Launch;

/**
 * Created by cjones on 12/16/15.
 */
public class PreviousLaunchAdapter extends RecyclerView.Adapter<PreviousLaunchAdapter.ViewHolder>{
    public int position;
    public View mView;
    private List<Launch> launchList;
    private Context mContext;
    private Calendar rightNow;

    public PreviousLaunchAdapter(Context context, View view) {
        rightNow = Calendar.getInstance();
        this.mContext = context;
        this.mView = view;
    }

    public void addItems(List<Launch> launchList) {
        if (this.launchList == null) {
            this.launchList = launchList;
        } else {
            this.launchList.addAll(launchList);
        }
    }

    public void removeAll() {
        this.launchList.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                         int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        Launch launchItem = launchList.get(i);

        position = i;

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM yyyy hh:mm a zzz");
        df.toLocalizedPattern();

        //If timestamp is available calculate TMinus and date.
        if(launchItem.getWsstamp() > 0){
            long longdate = launchItem.getWsstamp();
            longdate = longdate * 1000;
            Date date = new Date(longdate);

            long countdown = rightNow.getTimeInMillis();

            Date date2 = new Date(countdown); // Insert value in date1 & date2 as your need

            long dateDiff = date.getTime() - date2.getTime();
            if (dateDiff < 0){
                holder.content_Tminus_title.setText("T Plus");
                dateDiff = Math.abs(dateDiff);
            }
            String hms;
            if (TimeUnit.MILLISECONDS.toHours(dateDiff) > 24){
                hms = String.format("%2d days", TimeUnit.MILLISECONDS.toDays(dateDiff));
            } else {
                hms = String.format("%2d days", TimeUnit.MILLISECONDS.toDays(dateDiff));
            }


            holder.launch_date.setText(df.format(date));
            holder.content_TMinus_status.setText(" " + hms);

        } else {
            Date date = new Date(launchItem.getWindowstart());
            holder.launch_date.setText(df.format(date));
            holder.content_TMinus_status.setText(" Unknown");
        }

        switch (launchItem.getStatus()) {
            case 1:
                //GO for launch
                holder.content_status.setText(R.string.status_go);
                holder.content_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorGo));
                holder.content_TMinus_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorGo));
                break;
            case 2:
                //NO GO for launch
                holder.content_status.setText(R.string.status_nogo);
                holder.content_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                holder.content_TMinus_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                break;
            case 3:
                //Success for launch
                holder.content_status.setText(R.string.status_success);
                holder.content_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorGo));
                holder.content_TMinus_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorGo));
                break;
            case 4:
                //Failure to launch
                holder.content_status.setText(R.string.status_failure);
                holder.content_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                holder.content_TMinus_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                break;
        }


        if (launchItem.getMissions().size() > 0){
            holder.content_mission.setText(launchItem.getMissions().get(0).getName());
            holder.content_mission_description.setText(launchItem.getMissions().
                    get(0).getDescription());
        }

        String location = launchItem.getLocation().getName();
        if (launchItem.getLocation().getPads().size() > 0 && launchItem.getLocation().getPads().
                get(0).getAgencies().size() > 0){
            location = launchItem.getLocation().getName() + launchItem.getLocation().getPads().
                    get(0).getAgencies().get(0).getCountryCode();
        }

        holder.title.setText(launchItem.getRocket().getName());
        holder.location.setText(location);

    }

    @Override
    public int getItemCount() {
        return launchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, content, location, content_mission, content_mission_description,
                launch_date, content_status_title, content_status, content_TMinus_status, content_Tminus_title;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.launch_rocket);
            location = (TextView) view.findViewById(R.id.location);
            content_mission = (TextView) view.findViewById(R.id.content_mission);
            content_mission_description = (TextView) view.findViewById(
                    R.id.content_mission_description);
            launch_date = (TextView) view.findViewById(R.id.launch_date);

            content_status_title = (TextView) view.findViewById(R.id.content_status_title);
            content_status = (TextView) view.findViewById(R.id.content_status);

            content_TMinus_status = (TextView) view.findViewById(R.id.content_TMinus_status);
            content_Tminus_title = (TextView) view.findViewById(R.id.content_Tminus_title);

            title.setOnClickListener(this);
            location.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();

            switch (v.getId()) {
                case R.id.launch_rocket:
                    Log.d(LaunchApplication.TAG, launchList.get(position).getName());
                    break;
                case R.id.location:
                    Log.d(LaunchApplication.TAG, launchList.get(position).getLocation().getName());
                    break;
            }
        }
    }
}
