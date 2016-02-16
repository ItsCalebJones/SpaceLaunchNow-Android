package me.calebjones.spacelaunchnow.content.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;

/**
 * This adapter takes data from SharedPreference/LoaderService and applies it to the LaunchesFragment
 */
public class LaunchCompactAdapter extends RecyclerView.Adapter<LaunchCompactAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter  {
    public int position;
    private List<Launch> launchList;
    private List<Integer> mSectionPositions;
    private List<String> mSections;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private static SharedPreference sharedPreference;

    public LaunchCompactAdapter(Context context) {
        rightNow = Calendar.getInstance();
        launchList = new ArrayList();
        mSectionPositions = new ArrayList<>();
        mSections = new ArrayList<>();
        sharedPreference = SharedPreference.getInstance(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
    }

    public void addItems(List<Launch> launchList) {
        if (this.launchList == null) {
            this.launchList = launchList;
        } else {
            final List<Launch> launches = launchList;
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    mSections.clear();
                    mSectionPositions.clear();

                    //Note: If you're populating with a large dataset, you might want to
                    //call the following code asychronously.
                    SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd yyyy hh:mm a zzz");
                    df.toLocalizedPattern();

                    //data is your adapter's dataset
                    for (int i = 0, length = launches.size(); i < length; i++) {
                        //In case data is removed
                        if (launches.size() >= i){
                            long longdate = launches.get(i).getWsstamp();
                            longdate = longdate * 1000;
                            Date date = new Date(longdate);
                            String section = parseDateToMMyyyy(df.format(date));
                            if (!TextUtils.isEmpty(section) && !mSections.contains(section)) {
                                //This just adds a new section for each new letter
                                mSections.add(section);
                                mSectionPositions.add(i);
                            }
                        }
                    }
                }
            });
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
        sharedPreference = SharedPreference.getInstance(mContext);

        if (sharedPreference.getNightMode()) {
            m_theme = R.layout.dark_historical_list_item;
        } else {
            m_theme = R.layout.light_historical_list_item;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(m_theme, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Launch launchItem = launchList.get(i);

        String title;
        String location;

        position = i;

        String launchDate;

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

            holder.launch_date.setText("To be determined... " + launchDate);
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
        if (launchItem.getLocation().getPads().size() > 0 && launchItem.getLocation().getPads().
                get(0).getAgencies().size() > 0) {
            location = launchItem.getLocation().getName() + " " + launchItem.getLocation().getPads()
                    .get(0).getAgencies().get(0).getCountryCode();

        } else {
            location = launchItem.getLocation().getName();
        }

        if (launchItem.getRocket().getAgencies().size() > 0) {
            title = launchItem.getRocket().getAgencies().get(0).getName() + " | " + (launchItem.getRocket().getName());
        } else {
            title = launchItem.getRocket().getName();
        }

        holder.mission.setText(launchItem.getName());
        holder.title.setText(title);
        holder.location.setText(location);

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
        public TextView title, content, location, launch_date, mission;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);

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
}
