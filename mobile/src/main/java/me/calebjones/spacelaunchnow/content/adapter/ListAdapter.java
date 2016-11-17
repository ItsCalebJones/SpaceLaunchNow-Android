package me.calebjones.spacelaunchnow.content.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.Constants;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.Utils;

/**
 * This adapter takes data from ListPreferences/LoaderService and applies it to RecyclerView
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {
    public int position;
    private RealmList<LaunchRealm> launchList;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private static ListPreferences sharedPreference;

    public ListAdapter(Context context) {
        rightNow = Calendar.getInstance();
        launchList = new RealmList<>();
        sharedPreference = ListPreferences.getInstance(context);
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mContext = context;
    }

    public void addItems(List<LaunchRealm> launchList) {

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

        int m_theme;

        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreference = ListPreferences.getInstance(mContext);

        if (sharedPreference.isNightModeActive(mContext)) {
            night = true;
        } else {
            night = false;
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.launch_list_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final LaunchRealm launchItem = launchList.get(i);

        String missionType;
        String title;
        String location;
        String launchDate;

        position = i;

        //Retrieve missionType
        if (launchItem.getMissions().size() != 0) {
            Utils.setCategoryIcon(holder.categoryIcon, launchItem.getMissions().get(0).getTypeName(), night);
        } else {
            if (night) {
                holder.categoryIcon.setImageResource(R.drawable.ic_unknown_white);
            } else {
                holder.categoryIcon.setImageResource(R.drawable.ic_unknown);
            }
        }

        if (launchItem.getStatus() == 2) {
            //Get launch date
            if (sharedPref.getBoolean("local_time", true)) {
                SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy.");
                df.toLocalizedPattern();
                Date date = launchItem.getNet();
                launchDate = df.format(date);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy zzz");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = launchItem.getNet();
                launchDate = sdf.format(date);
            }
            holder.launch_date.setText(String.format("To be determined... %s", launchDate));
        } else {
            //Get launch date
            if (sharedPref.getBoolean("local_time", true)) {
                SimpleDateFormat df;
                if (sharedPref.getBoolean("24_hour_mode", false)) {
                    df = new SimpleDateFormat("EEEE, MMMM dd, yyyy - HH:mm zzz");
                } else {
                    df = new SimpleDateFormat("EEEE, MMMM dd, yyyy - hh:mm a zzz");
                }
                df.toLocalizedPattern();
                Date date = launchItem.getNet();
                launchDate = df.format(date);
            } else {
                SimpleDateFormat sdf;
                if (sharedPref.getBoolean("24_hour_mode", false)) {
                    sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy - HH:mm zzz");
                } else {
                    sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy - hh:mm a zzz");
                }
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = launchItem.getNet();
                launchDate = sdf.format(date);
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

        holder.title.setText(title);
        holder.mission.setText(launchItem.getName());
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

        Date date = launchList.get(position).getWindowstart();

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
            final LaunchRealm launch = launchList.get(getAdapterPosition());

            Intent updateIntent = new Intent(mContext, LaunchDataService.class);
            updateIntent.setAction(Constants.ACTION_UPDATE_LAUNCH);
            updateIntent.putExtra("launchID", launch.getId());
            mContext.startService(updateIntent);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(mContext, LaunchDetailActivity.class);
                    intent.putExtra("TYPE", "launch");
                    intent.putExtra("launchID", launch.getId());
                    mContext.startActivity(intent);
                }
            }, 1000);
        }
    }

    public void animateTo(List<LaunchRealm> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<LaunchRealm> newModels) {
        for (int i = launchList.size() - 1; i >= 0; i--) {
            if (!newModels.contains(launchList.get(i))) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<LaunchRealm> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final LaunchRealm model = newModels.get(i);
            if (!launchList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<LaunchRealm> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final LaunchRealm model = newModels.get(toPosition);
            final int fromPosition = launchList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public LaunchRealm removeItem(int position) {
        LaunchRealm model = launchList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, LaunchRealm model) {
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
                        holder.categoryIcon.setImageResource(R.drawable.ic_earth_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_earth);
                    }
                    break;
                case "Planetary Science":
                    if (night) {
                        holder.categoryIcon.setImageResource(R.drawable.ic_planetary_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_planetary);
                    }
                    break;
                case "Astrophysics":
                    if (night) {
                        holder.categoryIcon.setImageResource(R.drawable.ic_astrophysics_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_astrophysics);
                    }
                    break;
                case "Heliophysics":
                    if (night) {
                        holder.categoryIcon.setImageResource(R.drawable.ic_heliophysics_alt_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_heliophysics_alt);
                    }
                    break;
                case "Human Exploration":
                    if (night) {
                        holder.categoryIcon.setImageResource(R.drawable.ic_human_explore_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_human_explore);
                    }
                    break;
                case "Robotic Exploration":
                    if (night) {
                        holder.categoryIcon.setImageResource(R.drawable.ic_robotic_explore_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_robotic_explore);
                    }
                    break;
                case "Government/Top Secret":
                    if (night) {
                        holder.categoryIcon.setImageResource(R.drawable.ic_top_secret_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_top_secret);
                    }
                    break;
                case "Tourism":
                    if (night) {
                        holder.categoryIcon.setImageResource(R.drawable.ic_tourism_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_tourism);
                    }
                    break;
                case "Unknown":
                    if (night) {
                        holder.categoryIcon.setImageResource(R.drawable.ic_unknown_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_unknown);
                    }
                    break;
                case "Communications":
                    if (night) {
                        holder.categoryIcon.setImageResource(R.drawable.ic_satellite_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_satellite);
                    }
                    break;
                case "Resupply":
                    if (night) {
                        holder.categoryIcon.setImageResource(R.drawable.ic_resupply_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_resupply);
                    }
                    break;
                default:
                    if (night) {
                        holder.categoryIcon.setImageResource(R.drawable.ic_unknown_white);
                    } else {
                        holder.categoryIcon.setImageResource(R.drawable.ic_unknown);
                    }
                    break;
            }
        }
    }
}
