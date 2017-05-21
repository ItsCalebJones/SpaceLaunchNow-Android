package me.calebjones.spacelaunchnow.ui.main.next;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.mypopsy.maps.StaticMap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.util.DialogAdapter;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.Analytics;
import me.calebjones.spacelaunchnow.utils.CountDownTimer;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

/**
 * Adapts UpcomingLaunch data to the LaunchFragment
 */
public class CardBigAdapter extends RecyclerView.Adapter<CardBigAdapter.ViewHolder> implements SectionIndexer {

    private static ListPreferences sharedPreference;
    public int position;
    private String launchDate;
    private RealmList<Launch> launchList;
    private Context context;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private Boolean play = false;
    private int nightColor;
    private int color;
    private int accentColor;

    public CardBigAdapter(Context context) {
        rightNow = Calendar.getInstance();
        launchList = new RealmList<>();
        this.context = context;
        if (Utils.checkPlayServices(this.context)) {
            play = true;
        }
        nightColor = ContextCompat.getColor(context, R.color.dark_theme_secondary_text_color);
        color = ContextCompat.getColor(context, R.color.colorTextSecondary);
        accentColor = ContextCompat.getColor(context, R.color.colorAccent);
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public void addItems(List<Launch> launchList) {
        if (this.launchList != null) {
            this.launchList.addAll(launchList);
        } else {
            this.launchList = new RealmList<>();
            this.launchList.addAll(launchList);
        }
        this.notifyDataSetChanged();
    }

    public void clear() {
        if (launchList != null) {
            launchList.clear();
            this.notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        int m_theme;

        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreference = ListPreferences.getInstance(context);

        if (sharedPreference.isNightModeActive(context)) {
            night = true;
        } else {
            night = false;
        }

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_card_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        Launch launchItem = launchList.get(i);

        String title;
        try {
            if (launchItem.isValid()) {
                if (launchItem.getRocket() != null) {
                    if (launchItem.getRocket().getAgencies() != null && launchItem.getRocket().getAgencies().size() > 0) {
                        title = launchItem.getRocket().getAgencies().get(0).getName() + " | " + (launchItem.getRocket().getName());
                    } else {
                        title = launchItem.getRocket().getName();
                    }
                } else if (launchItem.getName() != null){
                    title = launchItem.getName();
                } else {
                    Timber.e("Error - launch item is effectively null.");
                    title = "Error - Unknown Launch";
                }

                holder.title.setText(title);

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

                double dlat = 0;
                double dlon = 0;
                if (launchItem.getLocation() != null && launchItem.getLocation().getPads() != null) {
                    dlat = launchItem.getLocation().getPads().get(0).getLatitude();
                    dlon = launchItem.getLocation().getPads().get(0).getLongitude();
                }

                // Getting status
                if (dlat == 0 && dlon == 0 || Double.isNaN(dlat) || Double.isNaN(dlon) || dlat == Double.NaN || dlon == Double.NaN) {
                    if (holder.map_view != null) {
                        holder.map_view.setVisibility(View.GONE);
                        holder.exploreFab.setVisibility(View.GONE);

                    }
                } else {
                    holder.map_view.setVisibility(View.VISIBLE);
                    holder.exploreFab.setVisibility(View.VISIBLE);
                    final Resources res = context.getResources();
                    final StaticMap map = new StaticMap()
                            .center(dlat, dlon)
                            .scale(4)
                            .type(StaticMap.Type.ROADMAP)
                            .zoom(5)
                            .marker(dlat, dlon)
                            .key(res.getString(R.string.GoogleMapsKey));

                    //Strange but necessary to calculate the height/width
                    holder.map_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            map.size(holder.map_view.getWidth() / 2,
                                    holder.map_view.getHeight() / 2);

                            Timber.v("onPreDraw: %s", map.toString());
                            Glide.with(context).load(map.toString())
                                    .error(R.drawable.placeholder)
                                    .into(holder.map_view);
                            holder.map_view.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        }
                    });
                }

                switch (launchItem.getStatus()) {
                    case 1:
                        String go = context.getResources().getString(R.string.status_go);
                        if (launchItem.getProbability() != null && launchItem.getProbability() > 0) {
                            go = String.format("%s | Forecast - %s%%", go, launchItem.getProbability());
                        }
                        //GO for launch
                        holder.content_status.setText(go);
                        holder.content_status.setTextColor(ContextCompat.getColor(context, R.color.colorGo));
                        break;
                    case 2:
                        //NO GO for launch
                        holder.content_status.setText(R.string.status_nogo);
                        holder.content_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                        break;
                    case 3:
                        //Success for launch
                        holder.content_status.setText(R.string.status_success);
                        holder.content_status.setTextColor(ContextCompat.getColor(context, R.color.colorGo));
                        break;
                    case 4:
                        //Failure to launch
                        holder.content_status.setText(R.string.status_failure);
                        holder.content_status.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
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
                    if (holder.timer != null) {
                        Timber.v("Timer is not null, cancelling.");
                        holder.timer.cancel();
                    }

                    final int status = launchItem.getStatus();
                    final String hold = launchItem.getHoldreason();

                    holder.content_TMinus_status.setTypeface(Typeface.SANS_SERIF);
                    holder.content_TMinus_status.setTextColor(accentColor);

                    holder.countdownView.setVisibility(View.VISIBLE);
                    long timeToFinish = future.getTimeInMillis() - now.getTimeInMillis();
                    holder.timer = new CountDownTimer(timeToFinish, 1000) {
                        StringBuilder time = new StringBuilder();

                        @Override
                        public void onFinish() {
                            Timber.v("Countdown finished.");
                            holder.content_TMinus_status.setTypeface(Typeface.DEFAULT);
                            if (night) {
                                holder.content_TMinus_status.setTextColor(nightColor);
                            } else {
                                holder.content_TMinus_status.setTextColor(color);
                            }
                            if (status == 1) {
                                holder.content_TMinus_status.setText("Watch Live webcast for up to date status.");

                            } else {
                                if (hold != null && hold.length() > 1) {
                                    holder.content_TMinus_status.setText(hold);
                                } else {
                                    holder.content_TMinus_status.setText("Watch Live webcast for up to date status.");
                                }
                            }
                            holder.content_TMinus_status.setVisibility(View.VISIBLE);
                            holder.countdownDays.setText("- -");
                            holder.countdownHours.setText("- -");
                            holder.countdownMinutes.setText("- -");
                            holder.countdownSeconds.setText("- -");
                        }

                        @Override
                        public void onTick(long millisUntilFinished) {
                            time.setLength(0);

                            // Calculate the Days/Hours/Mins/Seconds numerically.
                            long longDays = millisUntilFinished / 86400000;
                            long longHours = (millisUntilFinished / 3600000) % 24;
                            long longMins = (millisUntilFinished / 60000) % 60;
                            long longSeconds = (millisUntilFinished / 1000) % 60;

                            String days = String.valueOf(longDays);
                            String hours;
                            String minutes;
                            String seconds;

                            // Translate those numerical values to string values.
                            if (longHours < 10) {
                                hours = "0" + String.valueOf(longHours);
                            } else {
                                hours = String.valueOf(longHours);
                            }

                            if (longMins < 10) {
                                minutes = "0" + String.valueOf(longMins);
                            } else {
                                minutes = String.valueOf(longMins);
                            }

                            if (longSeconds < 10) {
                                seconds = "0" + String.valueOf(longSeconds);
                            } else {
                                seconds = String.valueOf(longSeconds);
                            }


                            // Update the views
                            if (Integer.valueOf(days) > 0) {
                                holder.countdownDays.setText(days);
                            } else {
                                holder.countdownDays.setText("- -");
                            }

                            if (Integer.valueOf(hours) > 0) {
                                holder.countdownHours.setText(hours);
                            } else if (Integer.valueOf(days) > 0) {
                                holder.countdownHours.setText("00");
                            } else {
                                holder.countdownHours.setText("- -");
                            }

                            if (Integer.valueOf(minutes) > 0) {
                                holder.countdownMinutes.setText(minutes);
                            } else if (Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
                                holder.countdownMinutes.setText("00");
                            } else {
                                holder.countdownMinutes.setText("- -");
                            }

                            if (Integer.valueOf(seconds) > 0) {
                                holder.countdownSeconds.setText(seconds);
                            } else if (Integer.valueOf(minutes) > 0 || Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
                                holder.countdownSeconds.setText("00");
                            } else {
                                holder.countdownSeconds.setText("- -");
                            }

                            // Hide status if countdown is active.
                            holder.content_TMinus_status.setVisibility(View.GONE);
                        }
                    }.start();

                } else {
                    holder.countdownView.setVisibility(View.GONE);
                    holder.content_TMinus_status.setTypeface(Typeface.DEFAULT);
                    if (night) {
                        holder.content_TMinus_status.setTextColor(ContextCompat.getColor(context, R.color.dark_theme_secondary_text_color));
                    } else {
                        holder.content_TMinus_status.setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary));
                    }
                    if (holder.timer != null) {
                        holder.timer.cancel();
                    }
                    if (launchItem.getStatus() != 1) {
                        if (launchItem.getRocket().getAgencies().size() > 0) {
                            holder.content_TMinus_status.setText(String.format("Pending confirmed GO from %s", launchItem.getRocket().getAgencies().get(0).getName()));
                        } else {
                            holder.content_TMinus_status.setText("Pending confirmed GO for Launch from launch agency");
                        }
                    } else {
                        holder.content_TMinus_status.setText("Unknown");
                    }
                }

                //Get launch date
                if (launchItem.getStatus() == 2) {

                    if (launchItem.getNet() != null) {
                        //Get launch date
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy.");
                        sdf.toLocalizedPattern();
                        Date date = launchItem.getNet();
                        launchDate = sdf.format(date);
                        holder.launch_date.setText("To be determined... " + launchDate);
                    }
                } else {
                    if (launchItem.getNet() != null) {
                        if (sharedPref.getBoolean("local_time", true)) {
                            SimpleDateFormat sdf;
                            if (sharedPref.getBoolean("24_hour_mode", false)) {
                                sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy - HH:mm zzz");
                            } else {
                                sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy - hh:mm a zzz");
                            }
                            sdf.toLocalizedPattern();
                            Date date = launchItem.getNet();
                            launchDate = sdf.format(date);
                        } else {
                            SimpleDateFormat sdf;
                            if (sharedPref.getBoolean("24_hour_mode", false)) {
                                sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy - HH:mm zzz");
                            } else {
                                sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy - hh:mm a zzz");
                            }
                            Date date = launchItem.getNet();
                            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                            launchDate = sdf.format(date);
                        }
                    } else {
                        launchDate = "To be determined... ";
                    }
                    holder.launch_date.setText(launchDate);
                }

                if (launchItem.getVidURLs() != null) {
                    if (launchItem.getVidURLs().size() == 0) {
                        holder.watchButton.setVisibility(View.GONE);
                    } else {
                        holder.watchButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.watchButton.setVisibility(View.GONE);
                }


                if (launchItem.getMissions().size() > 0) {
                    holder.content_mission.setText(launchItem.getMissions().get(0).getName());
                    String description = launchItem.getMissions().
                            get(0).getDescription();
                    if (description.length() > 0) {
                        holder.content_mission_description_view.setVisibility(View.VISIBLE);
                        holder.content_mission_description.setText(description);
                    }
                } else {
                    String[] separated = launchItem.getName().split(" \\| ");
                    if (separated.length > 0 && separated[1].length() > 4) {
                        holder.content_mission.setText(separated[1].trim());
                    } else {
                        holder.content_mission.setText("Unknown Mission");
                    }
                    holder.content_mission_description_view.setVisibility(View.GONE);
                }

                //If pad and agency exist add it to location, otherwise get whats always available
                if (launchItem.getLocation() != null) {
                    holder.location.setText(launchItem.getLocation().getName());
                }
            }
        } catch (NullPointerException e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public int getItemCount() {
        return launchList.size();
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {

        if (position >= getItemCount()) {
            position = getItemCount() - 1;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public TextView content;
        public TextView location;
        public TextView content_mission;
        public TextView content_mission_description;
        public TextView launch_date;
        public TextView content_status;
        public TextView content_TMinus_status;
        public TextView watchButton;
        public TextView shareButton;
        public TextView exploreButton;
        public TextView countdownDays;
        public TextView countdownHours;
        public TextView countdownMinutes;
        public TextView countdownSeconds;
        public LinearLayout content_mission_description_view;
        public ImageView categoryIcon;
        public FloatingActionButton exploreFab;
        public CountDownTimer timer;
        public View countdownView;

        public ImageView map_view;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);

            categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);
            exploreFab = (FloatingActionButton) view.findViewById(R.id.fab);
            exploreButton = (TextView) view.findViewById(R.id.exploreButton);
            shareButton = (TextView) view.findViewById(R.id.shareButton);
            watchButton = (TextView) view.findViewById(R.id.watchButton);
            title = (TextView) view.findViewById(R.id.launch_rocket);
            location = (TextView) view.findViewById(R.id.location);
            content_mission = (TextView) view.findViewById(R.id.content_mission);
            content_mission_description = (TextView) view.findViewById(
                    R.id.content_mission_description);
            launch_date = (TextView) view.findViewById(R.id.launch_date);
            content_status = (TextView) view.findViewById(R.id.content_status);
            content_TMinus_status = (TextView) view.findViewById(R.id.content_TMinus_status);
            content_mission_description_view = (LinearLayout) view.findViewById(R.id.content_mission_description_view);

            countdownDays = (TextView) view.findViewById(R.id.countdown_days);
            countdownHours = (TextView) view.findViewById(R.id.countdown_hours);
            countdownMinutes = (TextView) view.findViewById(R.id.countdown_minutes);
            countdownSeconds = (TextView) view.findViewById(R.id.countdown_seconds);
            countdownView = view.findViewById(R.id.countdown_layout);

            map_view = (ImageView) view.findViewById(R.id.map_view);
            map_view.setClickable(false);

            shareButton.setOnClickListener(this);
            exploreButton.setOnClickListener(this);
            watchButton.setOnClickListener(this);
            exploreFab.setOnClickListener(this);
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            Timber.d("onClick at %s", position);

            final Launch launch = launchList.get(position);
            Intent sendIntent = new Intent();

            SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM dd, yyyy - hh:mm a zzz");
            df.toLocalizedPattern();

            Date date = launch.getNet();
            String launchDate = df.format(date);

            switch (v.getId()) {
                case R.id.watchButton:
                    Timber.d("Watch: %s", launch.getVidURLs().size());
                    Analytics.from(context).sendButtonClicked("Watch Button - Opening Dialogue");
                    if (launch.getVidURLs().size() > 0) {
                        final DialogAdapter adapter = new DialogAdapter(new DialogAdapter.Callback() {

                            @Override
                            public void onListItemSelected(int index, MaterialSimpleListItem item, boolean longClick) {
                                if (longClick) {
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, launch.getVidURLs().get(index).getVal()); // Simple text and URL to share
                                    sendIntent.setType("text/plain");
                                    context.startActivity(sendIntent);
                                    Analytics.from(context).sendButtonClickedWithURL("Watch Button - URL Long Clicked", launch.getVidURLs().get(index).getVal());
                                } else {
                                    Uri watchUri = Uri.parse(launch.getVidURLs().get(index).getVal());
                                    Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                                    context.startActivity(i);
                                    Analytics.from(context).sendButtonClickedWithURL("Watch Button - URL", launch.getVidURLs().get(index).getVal());
                                }

                            }
                        });
                        for (RealmStr s : launch.getVidURLs()) {
                            //Do your stuff here
                            adapter.add(new MaterialSimpleListItem.Builder(context)
                                    .content(s.getVal())
                                    .build());
                        }

                        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                                .title("Select a Source")
                                .content("Long press for additional options.")
                                .adapter(adapter, null)
                                .negativeText("Cancel");
                        builder.show();
                    }
                    break;
                case R.id.exploreButton:
                    Timber.d("Explore: %s", launchList.get(position).getId());
                    Analytics.from(context).sendButtonClicked("Explore Button", launch.getName());

                    Intent exploreIntent = new Intent(context, LaunchDetailActivity.class);
                    exploreIntent.putExtra("TYPE", "launch");
                    exploreIntent.putExtra("launchID", launch.getId());
                    context.startActivity(exploreIntent);
                    break;
                case R.id.shareButton:
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, launch.getName());
                    if (launch.getVidURLs().size() > 0) {
                        if (launch.getLocation().getPads().size() > 0 && launch.getLocation().getPads().
                                get(0).getAgencies().size() > 0) {
                            //Get the first CountryCode
                            String country = launch.getLocation().getPads().
                                    get(0).getAgencies().get(0).getCountryCode();
                            country = (country.substring(0, 3));

                            sendIntent.putExtra(Intent.EXTRA_TEXT, launch.getName() + " launching from "
                                    + launch.getLocation().getName() + " " + country + "\n \n"
                                    + launchDate
                                    + "\n\nWatch: " + launch.getVidURLs().get(0) + "\n");
                        } else {
                            sendIntent.putExtra(Intent.EXTRA_TEXT, launch.getName() + " launching from "
                                    + launch.getLocation().getName() + "\n \n"
                                    + launchDate
                                    + "\n\nWatch: " + launch.getVidURLs().get(0) + "\n");
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
                                    + launchDate);
                        } else {
                            sendIntent.putExtra(Intent.EXTRA_TEXT, launch.getName() + " launching from "
                                    + launch.getLocation().getName() + "\n \n"
                                    + launchDate);
                        }
                    }
                    Analytics.from(context).sendLaunchShared("Explore Button", launch.getName() + "-" + launch.getId().toString());
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                    break;
                case R.id.fab:
                    String location = launchList.get(position).getLocation().getName();
                    location = (location.substring(location.indexOf(",") + 1));

                    Timber.d("FAB: %s ", location);

                    double dlat = launchList.get(position).getLocation().getPads().get(0).getLatitude();
                    double dlon = launchList.get(position).getLocation().getPads().get(0).getLongitude();

                    Uri gmmIntentUri = Uri.parse("geo:" + dlat + ", " + dlon + "?z=12&q=" + dlat + ", " + dlon);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    Analytics.from(context).sendLaunchMapClicked(launch.getName());

                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        Toast.makeText(context, "Loading " + launchList.get(position).getLocation().getPads().get(0).getName(), Toast.LENGTH_LONG).show();
                        context.startActivity(mapIntent);
                    }
                    break;
            }
        }

    }
}
