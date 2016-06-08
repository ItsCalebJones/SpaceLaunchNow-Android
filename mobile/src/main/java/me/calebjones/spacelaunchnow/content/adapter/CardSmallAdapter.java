package me.calebjones.spacelaunchnow.content.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.legacy.Launch;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import timber.log.Timber;

/**
 * Adapts UpcomingLaunch data to the LaunchFragment
 */
public class CardSmallAdapter extends RecyclerView.Adapter<CardSmallAdapter.ViewHolder> {
    public int position;
    private String launchDate;
    private List<Launch> launchList;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private static ListPreferences sharedPreference;

    public CardSmallAdapter(Context context) {
        rightNow = Calendar.getInstance();
        launchList = new ArrayList<>();
        this.mContext = context;

    }

    public void addItems(List<Launch> launchList) {
        if (this.launchList == null) {
            this.launchList = launchList;
        } else {
            this.launchList.addAll(launchList);
            this.notifyDataSetChanged();
        }
    }

    public void clearData() {
        int size = this.launchList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.launchList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void clear() {
        launchList.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        int m_theme;

        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreference = ListPreferences.getInstance(mContext);

        if (sharedPreference.getNightMode()) {
            night = true;
            m_theme = R.layout.dark_content_list_item;
        } else {
            night = false;
            m_theme = R.layout.light_small_content_list_item;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(m_theme, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Launch launchItem = launchList.get(i);

        position = i;

        //Retrieve missionType
        if (launchItem.getMissions().size() != 0) {
            setCategoryIcon(holder, launchItem.getMissions().get(0).getTypeName());
        }

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd yyyy hh:mm a zzz");
        df.toLocalizedPattern();

        switch (launchItem.getStatus()) {
            case 1:
                //GO for launch
                holder.content_status.setText(R.string.status_go);
                holder.content_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorGo));
                break;
            case 2:
                //NO GO for launch
                holder.content_status.setText(R.string.status_nogo);
                holder.content_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                break;
            case 3:
                //Success for launch
                holder.content_status.setText(R.string.status_success);
                holder.content_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorGo));
                break;
            case 4:
                //Failure to launch
                holder.content_status.setText(R.string.status_failure);
                holder.content_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                break;
        }

        //If timestamp is available calculate TMinus and date.
        if (launchItem.getNetstamp() > 0) {
            long longdate = launchItem.getNetstamp();
            longdate = longdate * 1000;
            final Date date = new Date(longdate);

            Calendar future = DateToCalendar(date);
            Calendar now = rightNow;

            now.setTimeInMillis(System.currentTimeMillis());

            holder.timer = new CountDownTimer(future.getTimeInMillis() - now.getTimeInMillis(), 1000) {
                StringBuilder time = new StringBuilder();

                @Override
                public void onFinish() {
                    holder.content_TMinus_status.setTypeface(Typeface.DEFAULT);
                    if (night){
                        holder.content_TMinus_status.setTextColor(ContextCompat.getColor(mContext, R.color.dark_theme_secondary_text_color));
                    } else {
                        holder.content_TMinus_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextSecondary));
                    }
                    if (launchItem.getStatus() == 1) {
                        holder.content_TMinus_status.setText("Watch Live webcast for up to date status.");

                        //TODO - Get hold reason and show it
                    } else {
                        holder.content_TMinus_status.setText("Watch Live webcast for up to date status.");
                    }
                }

                @Override
                public void onTick(long millisUntilFinished) {
                    time.setLength(0);
                    // Use days if appropriate
                    long longDays = millisUntilFinished / 86400000;
                    long longHours = (millisUntilFinished / 3600000) % 24;
                    long longMins = (millisUntilFinished / 60000) % 60;
                    long longSeconds = (millisUntilFinished / 1000) % 60;

                    String days = String.valueOf(longDays);
                    String hours;
                    String minutes;
                    String seconds;
                    if (longHours < 10){
                        hours = "0" + String.valueOf(longHours);
                    } else {
                        hours = String.valueOf(longHours);
                    }

                    if (longMins < 10){
                        minutes = "0" + String.valueOf(longMins);
                    } else {
                        minutes = String.valueOf(longMins);
                    }

                    if (longSeconds < 10){
                        seconds = "0" + String.valueOf(longSeconds);
                    } else {
                        seconds = String.valueOf(longSeconds);
                    }
                    holder.content_TMinus_status.setTypeface(Typeface.SANS_SERIF);
                    holder.content_TMinus_status.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    holder.content_TMinus_status.setText(String.format("L - %s days - %s:%s:%s", days, hours, minutes, seconds));
                }
            }.start();

        } else {
            holder.content_TMinus_status.setTypeface(Typeface.DEFAULT);
            holder.content_TMinus_status.setTextColor(ContextCompat.getColor(mContext,R.color.colorTextSecondary));
            if (holder.timer != null) {
                holder.timer.cancel();
            }
            if (launchItem.getStatus() != 1) {
                if (launchItem.getRocket().getAgencies().size() > 0) {
                    holder.content_TMinus_status.setText(String.format("Pending confirmed GO from %s", launchItem.getRocket().getAgencies().get(0).getName()));
                } else {
                    holder.content_TMinus_status.setText("Pending confirmed GO from launch agency");
                }
            } else {
                holder.content_TMinus_status.setText("Unknown");
            }
        }

        //Get launch date
        if (launchItem.getStatus() == 2) {
            //Get launch date
            if (sharedPref.getBoolean("local_time", true)) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy.");
                sdf.toLocalizedPattern();
                Date date = new Date(launchItem.getWindowstart());
                launchDate = sdf.format(date);
            } else {
                launchDate = launchItem.getWindowstart();
            }

            holder.launch_date.setText("To be determined... " + launchDate);
        } else {
            if (sharedPref.getBoolean("local_time", true)) {
                Date date = new Date(launchItem.getWindowstart());
                launchDate = df.format(date);
            } else {
                launchDate = launchItem.getWindowstart();
            }
            holder.launch_date.setText(launchDate);
        }


        if (launchItem.getMissions().size() > 0) {
            String description = launchItem.getMissions().
                    get(0).getDescription();
            if (description.length() > 0) {
                holder.content_mission_description_view.setVisibility(View.VISIBLE);
                holder.content_mission_description.setText(description);
            }
        } else {
            holder.content_mission_description_view.setVisibility(View.GONE);
        }

        //If location is available then see if pad and agency informaiton is avaialble.
        if (launchItem.getLocation().getName() != null) {
            holder.location.setText(launchItem.getLocation().getName());
        }
        holder.title.setText(launchItem.getRocket().getName());
    }

    @Override
    public int getItemCount() {
        return launchList.size();
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, content, location, content_mission_description, content_status,
                launch_date, content_TMinus_status,
                watchButton, shareButton, exploreButton;
        public ImageView categoryIcon;
        public LinearLayout content_mission_description_view;
        public CountDownTimer timer;


        //Add content to the card
        public ViewHolder(View view) {
            super(view);

            content_status = (TextView) view.findViewById(R.id.content_status);
            categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);
            exploreButton = (TextView) view.findViewById(R.id.exploreButton);
            shareButton = (TextView) view.findViewById(R.id.shareButton);
            watchButton = (TextView) view.findViewById(R.id.watchButton);
            title = (TextView) view.findViewById(R.id.launch_rocket);
            location = (TextView) view.findViewById(R.id.location);
            content_mission_description = (TextView) view.findViewById(
                    R.id.content_mission_description);
            launch_date = (TextView) view.findViewById(R.id.launch_date);
            content_TMinus_status = (TextView) view.findViewById(R.id.content_TMinus_status);
            content_mission_description_view = (LinearLayout) view.findViewById(R.id.content_mission_description_view);

            shareButton.setOnClickListener(this);
            exploreButton.setOnClickListener(this);
            watchButton.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            Timber.d("onClick at %s", position);

            Launch launch = new Launch();
            launch = launchList.get(position);
            Intent sendIntent = new Intent();

            SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a zzz");
            df.toLocalizedPattern();

            Date date = new Date(launch.getWindowstart());
            String launchDate = df.format(date);

            switch (v.getId()) {
                case R.id.watchButton:
                    Timber.d("Watch: %s", launchList.get(position).getVidURL());
                    Uri watchUri = Uri.parse(launchList.get(position).getVidURL());
                    Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                    mContext.startActivity(i);
                    break;
                case R.id.exploreButton:
                    Timber.d("Explore: %s", launchList.get(position).getId());
                    Intent exploreIntent = new Intent(mContext, LaunchDetailActivity.class);
                    exploreIntent.putExtra("TYPE", "launch");
                    exploreIntent.putExtra("launchID", launch);
                    mContext.startActivity(exploreIntent);
                    break;
                case R.id.shareButton:
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, launch.getName());
                    if (launch.getVidURL() != null) {
                        if (launch.getLocation().getPads().size() > 0 && launch.getLocation().getPads().
                                get(0).getAgencies().size() > 0) {
                            //Get the first CountryCode
                            String country = launch.getLocation().getPads().
                                    get(0).getAgencies().get(0).getCountryCode();
                            country = (country.substring(0, 3));

                            sendIntent.putExtra(Intent.EXTRA_TEXT, launch.getName() + " launching from "
                                    + launch.getLocation().getName() + " " + country + "\n \n"
                                    + launchDate
                                    + "\n\nWatch: " + launch.getVidURL() + "\n"
                                    + " \n\nvia Space Launch Now and Launch Library");
                        } else {
                            sendIntent.putExtra(Intent.EXTRA_TEXT, launch.getName() + " launching from "
                                    + launch.getLocation().getName() + "\n \n"
                                    + launchDate
                                    + "\n\nWatch: " + launch.getVidURL() + "\n"
                                    + " \n\nvia Space Launch Now and Launch Library");
                        }
                    } else {
                        if (launch.getLocation().getPads().size() > 0 && launch.getLocation().getPads().
                                get(0).getAgencies().size() > 0) {
                            //Get the first CountryCode
                            String country = launch.getLocation().getPads().
                                    get(0).getAgencies().get(0).getCountryCode();
                            country = (country.substring(0, 3));

                            sendIntent.putExtra(Intent.EXTRA_TEXT, launch.getName() + " launching from "
                                    + launch.getLocation().getName() + " " + country + "\n \n"
                                    + launchDate
                                    + " \n\nvia Space Launch Now and Launch Library");
                        } else {
                            sendIntent.putExtra(Intent.EXTRA_TEXT, launch.getName() + " launching from "
                                    + launch.getLocation().getName() + "\n \n"
                                    + launchDate
                                    + " \n\nvia Space Launch Now and Launch Library");
                        }
                    }
                    sendIntent.setType("text/plain");
                    mContext.startActivity(sendIntent);
                    break;
            }
        }
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