package me.calebjones.spacelaunchnow.ui.main.next;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.nekocode.badge.BadgeDrawable;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.data.LaunchStatus;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.util.DialogAdapter;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.views.CountDownTimer;
import timber.log.Timber;

/**
 * Adapts UpcomingLaunch data to the LaunchFragment
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> implements SectionIndexer {

    private static ListPreferences sharedPreference;
    public int position;
    private RealmList<Launch> launchList;
    private Context context;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private Boolean play = false;
    private int nightColor;
    private int color;
    private int accentColor;

    public CardAdapter(Context context) {
        rightNow = Calendar.getInstance();
        launchList = new RealmList<>();
        this.context = context;
        if (Utils.checkPlayServices(this.context)) {
            play = true;
        }
        nightColor = ContextCompat.getColor(context, R.color.dark_theme_secondary_text_color);
        color = ContextCompat.getColor(context, R.color.colorTextSecondary);
        accentColor = ContextCompat.getColor(context, R.color.colorAccent);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreference = ListPreferences.getInstance(context);
        night = sharedPreference.isNightModeActive(context);
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_card_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "StringFormatMatches"})
    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        Launch launchItem = launchList.get(i);
        Timber.i("Binding %s", launchItem.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.cardView.setElevation(7);
        }
        BadgeDrawable.Builder landingType =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                        .badgeColor(accentColor)
                        .textSize(36)
                        .padding(16, 10, 16, 10, 32)
                        .strokeWidth(4);

        BadgeDrawable.Builder landingLocation =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                        .badgeColor(accentColor)
                        .textSize(36)
                        .padding(16, 10, 16, 10, 32)
                        .strokeWidth(4);
        String title;
        try {
            if (launchItem.isValid()) {
                if (launchItem.getLauncherConfig() != null) {
                    if (launchItem.getLsp() != null) {
                        title = launchItem.getLsp().getName() + " | " + (launchItem.getLauncherConfig().getName());
                    } else {
                        title = launchItem.getLauncherConfig().getName();
                    }
                } else if (launchItem.getName() != null) {
                    title = launchItem.getName();
                } else {
                    Timber.e("Error - launch item is effectively null.");
                    title = context.getString(R.string.error_unknown_launch);
                }

                holder.title.setText(title);

                //Retrieve missionType
                if (launchItem.getMission() != null) {
                    Utils.setCategoryIcon(holder.categoryIcon, launchItem.getMission().getTypeName(), true);
                } else {
                    holder.categoryIcon.setImageResource(R.drawable.ic_unknown_white);
                }

                double dlat = 0;
                double dlon = 0;
                if (launchItem.getPad() != null) {
                    dlat = Double.parseDouble(launchItem.getPad().getLatitude());
                    dlon = Double.parseDouble(launchItem.getPad().getLongitude());
                }

                // Getting status
                if (dlat == 0 && dlon == 0 || Double.isNaN(dlat) || Double.isNaN(dlon) || dlat == Double.NaN || dlon == Double.NaN) {
                    if (holder.mapView != null) {
                        holder.mapView.setVisibility(View.GONE);
                    }
                } else {
                    holder.mapView.setVisibility(View.VISIBLE);
                    holder.bindView(new LaunchLocation(launchItem, new LatLng(dlat, dlon)));
                }

                if (launchItem.getLanding() != null) {
                    if (launchItem.getLanding().getLandingLocation() != null) {
                        holder.landingLocation.setVisibility(View.VISIBLE);
                        holder.landingLocation.setText(landingLocation.text2(launchItem.getLanding().getLandingLocation().getAbbrev()).text1("Landing").build().toSpannable());
                    } else {
                        holder.landingLocation.setVisibility(View.GONE);
                    }
                    if (launchItem.getLanding().getLandingLocation() != null) {
                        holder.landingType.setVisibility(View.VISIBLE);
                        holder.landingType.setText(landingType.text2(launchItem.getLanding().getLandingType().getAbbrev()).text1("Type").build().toSpannable());
                    } else {
                        holder.landingType.setVisibility(View.GONE);
                    }
                } else {
                    holder.landingLocation.setVisibility(View.GONE);
                    holder.landingType.setVisibility(View.GONE);
                }

                holder.contentStatus.setText(LaunchStatus.getLaunchStatusTitle(context, launchItem.getStatus().getId()));

                //If timestamp is available calculate TMinus and date.
                if (launchItem.getNet().getTime() > 0 && (launchItem.getStatus().getId() == 1 || launchItem.getStatus().getId() == 2)) {
                    //TODO VERIFY THIS STILL WORKS
                    long longdate = launchItem.getNet().getTime();
                    final Date date = new Date(longdate);

                    Calendar future = DateToCalendar(date);
                    Calendar now = rightNow;

                    now.setTimeInMillis(System.currentTimeMillis());
                    if (holder.timer != null) {
                        Timber.v("Timer is not null, cancelling.");
                        holder.timer.cancel();
                    }

                    final int status = launchItem.getStatus().getId();
                    final String hold = launchItem.getHoldreason();

                    holder.contentTMinusStatus.setTypeface(Typeface.SANS_SERIF);
                    holder.contentTMinusStatus.setTextColor(accentColor);

//                    holder.countdownView.setVisibility(View.VISIBLE);
                    long timeToFinish = future.getTimeInMillis() - now.getTimeInMillis();
                    if (timeToFinish > 0) {
                        holder.timer = new CountDownTimer(timeToFinish, 1000) {
                            StringBuilder time = new StringBuilder();

                            @Override
                            public void onFinish() {
                                Timber.v("Countdown finished.");
                                holder.contentTMinusStatus.setTypeface(Typeface.DEFAULT);
                                if (night) {
                                    holder.contentTMinusStatus.setTextColor(nightColor);
                                } else {
                                    holder.contentTMinusStatus.setTextColor(color);
                                }
                                if (status == 1) {
                                    holder.contentTMinusStatus.setText(R.string.watch_webcast);

                                } else {
                                    if (hold != null && hold.length() > 1) {
                                        holder.contentTMinusStatus.setText(hold);
                                    } else {
                                        holder.contentTMinusStatus.setText(R.string.watch_webcast);
                                    }
                                }
                                holder.contentTMinusStatus.setVisibility(View.VISIBLE);
                                holder.countdownDays.setText("00");
                                holder.countdownHours.setText("00");
                                holder.countdownMinutes.setText("00");
                                holder.countdownSeconds.setText("00");
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
                                    holder.countdownDays.setText("00");
                                }

                                if (Integer.valueOf(hours) > 0) {
                                    holder.countdownHours.setText(hours);
                                } else if (Integer.valueOf(days) > 0) {
                                    holder.countdownHours.setText("00");
                                } else {
                                    holder.countdownHours.setText("00");
                                }

                                if (Integer.valueOf(minutes) > 0) {
                                    holder.countdownMinutes.setText(minutes);
                                } else if (Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
                                    holder.countdownMinutes.setText("00");
                                } else {
                                    holder.countdownMinutes.setText("00");
                                }

                                if (Integer.valueOf(seconds) > 0) {
                                    holder.countdownSeconds.setText(seconds);
                                } else if (Integer.valueOf(minutes) > 0 || Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
                                    holder.countdownSeconds.setText("00");
                                } else {
                                    holder.countdownSeconds.setText("00");
                                }

                                // Hide status if countdown is active.
                                holder.contentTMinusStatus.setVisibility(View.GONE);
                            }
                        }.start();
                    } else {
                        showStatusDescription(launchItem, holder);
                    }

                } else {
                    showStatusDescription(launchItem, holder);
                }

                //Get launch date
                if (launchItem.getStatus().getId() == 2) {

                    if (launchItem.getNet() != null) {
                        //Get launch date

                        SimpleDateFormat sdf = Utils.getSimpleDateFormatForUI("MMMM d, yyyy");
                        sdf.toLocalizedPattern();
                        Date date = launchItem.getNet();
                        String launchTime = sdf.format(date);
                        if (launchItem.getTbddate()) {
                            launchTime = launchTime + " " + context.getString(R.string.unconfirmed);
                        }
                        holder.launchDateCompact.setText(launchTime);
                    }
                } else {
                    SimpleDateFormat sdf = Utils.getSimpleDateFormatForUI("MMMM d, yyyy HH:mm zzz");
                    sdf.toLocalizedPattern();
                    Date date = launchItem.getNet();
                    holder.launchDateCompact.setText(sdf.format(date));
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


                if (launchItem.getMission() != null) {
                    holder.contentMission.setText(launchItem.getMission().getName());
                    String description = launchItem.getMission().getDescription();
                    if (description.length() > 0) {
                        holder.contentMissionDescriptionView.setVisibility(View.VISIBLE);
                        holder.contentMissionDescription.setText(description);
                    }
                } else {
                    String[] separated = launchItem.getName().split(" \\| ");
                    try {
                        if (separated.length > 0 && separated[1].length() > 4) {
                            holder.contentMission.setText(separated[1].trim());
                        } else {
                            holder.contentMission.setText("Unknown Mission");
                        }
                    } catch (ArrayIndexOutOfBoundsException exception) {
                        holder.contentMission.setText("Unknown Mission");
                    }
                    holder.contentMissionDescriptionView.setVisibility(View.GONE);
                }

                //If pad and agency_menu exist add it to location, otherwise get whats always available
                if (launchItem.getLocation() != null) {
                    holder.location.setText(launchItem.getLocation().getName());
                } else {
                    holder.location.setText("");
                }
            }
        } catch (NullPointerException e) {
            Crashlytics.logException(e);
        }
    }

    private void showStatusDescription(Launch launchItem, ViewHolder holder) {
        holder.contentTMinusStatus.setVisibility(View.VISIBLE);
        holder.contentTMinusStatus.setTypeface(Typeface.DEFAULT);
        if (night) {
            holder.contentTMinusStatus.setTextColor(ContextCompat.getColor(context, R.color.dark_theme_secondary_text_color));
        } else {
            holder.contentTMinusStatus.setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary));
        }
        if (holder.timer != null) {
            holder.timer.cancel();
        }
        if (launchItem.getStatus().getId() == 2) {
            holder.countdownView.setVisibility(View.GONE);
            if (launchItem.getLsp() != null) {
                holder.contentTMinusStatus.setText(String.format(context.getString(R.string.pending_confirmed_go_specific), launchItem.getLsp().getName()));
            } else {
                holder.contentTMinusStatus.setText(R.string.pending_confirmed_go);
            }
        } else if (launchItem.getStatus().getId() == 3) {
            holder.countdownView.setVisibility(View.GONE);
            holder.contentTMinusStatus.setText("Launch was a success!");
        } else if (launchItem.getStatus().getId() == 4) {
            holder.countdownView.setVisibility(View.GONE);
            holder.contentTMinusStatus.setText("A launch failure has occurred.");
        } else if (launchItem.getStatus().getId() == 5) {
            holder.contentTMinusStatus.setText("A hold has been placed on the launch.");
        } else if (launchItem.getStatus().getId() == 6) {
            holder.countdownDays.setText("00");
            holder.countdownHours.setText("00");
            holder.countdownMinutes.setText("00");
            holder.countdownSeconds.setText("00");
            holder.contentTMinusStatus.setText("The launch is currently in flight.");
        } else if (launchItem.getStatus().getId() == 7) {
            holder.countdownView.setVisibility(View.GONE);
            holder.contentTMinusStatus.setText("Launch was a partial failure, payload separated into an incorrect orbit.");
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback {
        @BindView(R.id.categoryIcon)
        public ImageView categoryIcon;
        @BindView(R.id.launch_rocket)
        public TextView title;
        @BindView(R.id.location)
        public TextView location;
        @BindView(R.id.launch_date_compact)
        public TextView launchDateCompact;
        @BindView(R.id.TitleCard)
        public LinearLayout titleCard;
        @BindView(R.id.map_view)
        public MapView mapView;
        @BindView(R.id.content_status)
        public TextView contentStatus;
        @BindView(R.id.content_TMinus_status)
        public TextView contentTMinusStatus;
        @BindView(R.id.countdown_days)
        public TextView countdownDays;
        @BindView(R.id.countdown_hours)
        public TextView countdownHours;
        @BindView(R.id.countdown_minutes)
        public TextView countdownMinutes;
        @BindView(R.id.countdown_seconds)
        public TextView countdownSeconds;
        @BindView(R.id.countdown_layout)
        public LinearLayout countdownView;
        @BindView(R.id.content_mission)
        public TextView contentMission;
        @BindView(R.id.content_mission_description)
        public TextView contentMissionDescription;
        @BindView(R.id.content_mission_description_view)
        public LinearLayout contentMissionDescriptionView;
        @BindView(R.id.watchButton)
        public AppCompatButton watchButton;
        @BindView(R.id.shareButton)
        public AppCompatButton shareButton;
        @BindView(R.id.exploreButton)
        public AppCompatButton exploreButton;
        @BindView(R.id.lnrLayout)
        public LinearLayout lnrLayout;
        @BindView(R.id.card_view)
        public CardView cardView;
        @BindView(R.id.landing_location)
        TextView landingLocation;
        @BindView(R.id.landing_type)
        TextView landingType;
        public View layout;
        public GoogleMap map;
        public CountDownTimer timer;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);
            layout = view;
            ButterKnife.bind(this, view);

            mapView.setClickable(false);

            titleCard.setOnClickListener(this);
            shareButton.setOnClickListener(this);
            exploreButton.setOnClickListener(this);
            watchButton.setOnClickListener(this);
            mapView.setOnClickListener(this);

            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
                mapView.setVisibility(View.INVISIBLE);
            }
        }

        //React to click events.
        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            Timber.d("onClick at %s", position);

            final Launch launch = launchList.get(position);
            Intent sendIntent = new Intent();

            SimpleDateFormat df = Utils.getSimpleDateFormatForUI("EEEE, MMMM d, yyyy - hh:mm a zzz");
            df.toLocalizedPattern();

            Date date = launch.getNet();
            String launchDate = df.format(date);

            switch (v.getId()) {
                case R.id.watchButton:
                    Timber.d("Watch: %s", launch.getVidURLs().size());
                    Analytics.getInstance().sendButtonClicked("Watch Button - Opening Dialogue");
                    if (launch.getVidURLs().size() > 0) {
                        final DialogAdapter adapter = new DialogAdapter((index, item, longClick) -> {
                            try {
                                if (longClick) {
                                    Intent sendIntent1 = new Intent();
                                    sendIntent1.setAction(Intent.ACTION_SEND);
                                    sendIntent1.putExtra(Intent.EXTRA_TEXT, launch.getVidURLs().get(index).getVal()); // Simple text and URL to share
                                    sendIntent1.setType("text/plain");
                                    context.startActivity(sendIntent1);
                                    Analytics.getInstance().sendButtonClickedWithURL("Watch Button - URL Long Clicked", launch.getVidURLs().get(index).getVal());
                                } else {
                                    Uri watchUri = Uri.parse(launch.getVidURLs().get(index).getVal());
                                    Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                                    context.startActivity(i);
                                    Analytics.getInstance().sendButtonClickedWithURL("Watch Button - URL", launch.getVidURLs().get(index).getVal());
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                Timber.e(e);
                                Toast.makeText(context, "Ops, an error occurred.", Toast.LENGTH_SHORT).show();
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
                    Analytics.getInstance().sendButtonClicked("Explore Button", launch.getName());

                    Intent exploreIntent = new Intent(context, LaunchDetailActivity.class);
                    exploreIntent.putExtra("TYPE", "launch");
                    exploreIntent.putExtra("launchID", launch.getId());
                    context.startActivity(exploreIntent);
                    break;
                case R.id.shareButton:
                    String message;
                    if (launch.getVidURLs().size() > 0) {
                        if (launch.getLocation() != null) {

                            message = launch.getName() + " launching from "
                                    + launch.getLocation().getName() + "\n\n"
                                    + launchDate;
                        } else if (launch.getLocation() != null) {
                            message = launch.getName() + " launching from "
                                    + launch.getLocation().getName() + "\n\n"
                                    + launchDate;
                        } else {
                            message = launch.getName()
                                    + "\n\n"
                                    + launchDate;
                        }
                    } else {
                        if (launch.getLocation() != null) {

                            message = launch.getName() + " launching from "
                                    + launch.getLocation().getName() + "\n\n"
                                    + launchDate;
                        } else if (launch.getLocation() != null) {
                            message = launch.getName() + " launching from "
                                    + launch.getLocation().getName() + "\n\n"
                                    + launchDate;
                        } else {
                            message = launch.getName()
                                    + "\n\n"
                                    + launchDate;
                        }
                    }
                    ShareCompat.IntentBuilder.from((Activity) context)
                            .setType("text/plain")
                            .setChooserTitle("Share: " + launch.getName())
                            .setText(String.format("%s\n\nWatch Live: %s", message, launch.getUrl()))
                            .startChooser();
                    Analytics.getInstance().sendLaunchShared("Explore Button", launch.getName() + "-" + launch.getId().toString());
                    break;
                case R.id.map_view:
                    String location = launchList.get(position).getLocation().getName();
                    location = (location.substring(location.indexOf(",") + 1));

                    Timber.d("FAB: %s ", location);

                    double dlat = Double.parseDouble(launchList.get(position).getPad().getLatitude());
                    double dlon = Double.parseDouble(launchList.get(position).getPad().getLongitude());

                    Uri gmmIntentUri = Uri.parse("geo:" + dlat + ", " + dlon + "?z=12&q=" + dlat + ", " + dlon);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    Analytics.getInstance().sendLaunchMapClicked(launch.getName());

                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        Toast.makeText(context, "Loading " + launchList.get(position).getPad().getName(), Toast.LENGTH_LONG).show();
                        context.startActivity(mapIntent);
                    }
                    break;
                case R.id.TitleCard:
                    Timber.d("Explore: %s", launchList.get(position).getId());
                    Analytics.getInstance().sendButtonClicked("Title Card", launch.getName());

                    Intent titleIntent = new Intent(context, LaunchDetailActivity.class);
                    titleIntent.putExtra("TYPE", "launch");
                    titleIntent.putExtra("launchID", launch.getId());
                    context.startActivity(titleIntent);
                    break;
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            Timber.v("onMapReady called.");
            MapsInitializer.initialize(context);
            map = googleMap;
            setMapLocation();
        }

        private void setMapLocation() {
            if (map == null) {
                Timber.d("setMapLocation - map is null");
                return;
            }

            LaunchLocation data = (LaunchLocation) mapView.getTag();
            if (data == null) {
                Timber.d("setMapLocation - data is null");
                return;
            }

            mapView.setVisibility(View.VISIBLE);
            map.getUiSettings().setMapToolbarEnabled(true);
            map.getUiSettings().setZoomGesturesEnabled(false);
            map.getUiSettings().setScrollGesturesEnabled(false);
            Timber.d("setMapLocation - Moving the camera.");
            // Add a marker for this item and set the camera
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(data.location, 7f));
            map.addMarker(new MarkerOptions()
                    .position(data.location)
                    .title(data.launch.getLocation().getName())
                    .snippet(data.launch.getPad().getName())
                    .infoWindowAnchor(0.5f, 0.5f));

            // Set the map type back to normal.
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        private void bindView(LaunchLocation item) {
            Timber.d("bindView - %s", item.launch.getName());
            // Store a reference of the ViewHolder object in the layout.
            layout.setTag(this);
            // Store a reference to the item in the mapView's tag. We use it to get the
            // coordinate of a location, when setting the map location.
            mapView.setTag(item);
            mapView.onResume();
            setMapLocation();
        }
    }

    private static class LaunchLocation {

        public final Launch launch;
        public final LatLng location;

        LaunchLocation(Launch launch, LatLng location) {
            this.launch = launch;
            this.location = location;
        }
    }

    /**
     * RecycleListener that completely clears the {@link GoogleMap}
     * attached to a row in the RecyclerView.
     * Sets the map type to {@link GoogleMap#MAP_TYPE_NONE} and clears
     * the map.
     */
    public RecyclerView.RecyclerListener mRecycleListener = holder -> {
        ViewHolder mapHolder = (ViewHolder) holder;
        if (mapHolder != null && mapHolder.map != null) {
            Timber.d("Clearing map!");
            // Clear the map and free up resources by changing the map type to none.
            // Also reset the map when it gets reattached to layout, so the previous map would
            // not be displayed.
            mapHolder.map.clear();
            mapHolder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    };
}
