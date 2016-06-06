package me.calebjones.spacelaunchnow.content.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CountDownTimer;
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
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mypopsy.maps.StaticMap;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.ui.widget.StaticMapView;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.content.models.realm.RealmStr;
import timber.log.Timber;

/**
 * Adapts UpcomingLaunch data to the LaunchFragment
 */
public class CardBigAdapter extends RecyclerView.Adapter<CardBigAdapter.ViewHolder> implements SectionIndexer {

    private String launchDate;
    private RealmList<LaunchRealm> launchList;
    private Context context;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private Boolean night;
    private Boolean play = false;

    private static ListPreferences sharedPreference;

    public int position;

    public CardBigAdapter(Context context) {
        rightNow = Calendar.getInstance();
        launchList = new RealmList<>();
        this.context = context;
        if (Utils.checkPlayServices(this.context)) {
            play = true;
        }
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
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        int m_theme;

        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreference = ListPreferences.getInstance(context);

        if (sharedPreference.getNightMode()) {
            night = true;
            m_theme = R.layout.dark_content_list_item;
        } else {
            night = false;
            m_theme = R.layout.light_content_list_item;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(m_theme, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final LaunchRealm launchItem = launchList.get(i);

        position = i;

        //Retrieve missionType
        if (launchItem.getMissions().size() != 0) {
            setCategoryIcon(holder, launchItem.getMissions().get(0).getTypeName());
        }

        //TODO These are slightly rounded when converting from double to long
        final double dlat = launchItem.getLocation().getPads().get(0).getLatitude();
        final double dlon = launchItem.getLocation().getPads().get(0).getLongitude();

        // Getting status
        if (play) {
            if (dlat == 0 && dlon == 0 || Double.isNaN(dlat) || Double.isNaN(dlon) || dlat == Double.NaN || dlon == Double.NaN) {
                if (holder.map_view != null) {
                    holder.map_view.setVisibility(View.GONE);
                    holder.exploreFab.setVisibility(View.GONE);
                }
            } else {
                holder.map_view.setVisibility(View.VISIBLE);
                holder.exploreFab.setVisibility(View.VISIBLE);
//                holder.setMapLocation(dlat, dlon);
//                // Keep track of MapView
//                mMaps.add(holder.map_view);
                final Resources res = context.getResources();
                final StaticMap map = new StaticMap()
                        .center(dlat, dlon)
                        .scale(4)
                        .type(StaticMap.Type.ROADMAP)
                        .zoom(4)
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
        } else {
            holder.map_view.setVisibility(View.GONE);
            holder.exploreFab.setVisibility(View.GONE);
        }

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd yyyy hh:mm a zzz");
        df.toLocalizedPattern();

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
                holder.timer.cancel();
            }
            holder.timer = new CountDownTimer(future.getTimeInMillis() - now.getTimeInMillis(), 1000) {
                StringBuilder time = new StringBuilder();

                @Override
                public void onFinish() {
                    holder.content_TMinus_status.setTypeface(Typeface.DEFAULT);
                    if (night){
                        holder.content_TMinus_status.setTextColor(ContextCompat.getColor(context, R.color.dark_theme_secondary_text_color));
                    } else {
                        holder.content_TMinus_status.setTextColor(ContextCompat.getColor(context, R.color.colorTextSecondary));
                    }
                    if (launchItem.getStatus() == 1) {
                        holder.content_TMinus_status.setText("Watch Live webcast for up to date status.");

                        //TODO - Get hold reason and show it
                    } else {
                        if(launchItem.getHoldreason() != null && launchItem.getHoldreason().length() > 1) {
                            holder.content_TMinus_status.setText(launchItem.getHoldreason());
                        } else {
                            holder.content_TMinus_status.setText("Watch Live webcast for up to date status.");
                        }
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
                    holder.content_TMinus_status.setTextColor(ContextCompat.getColor(context,R.color.red));
                    holder.content_TMinus_status.setText(String.format("L - %s days - %s:%s:%s", days, hours, minutes, seconds));
                }
            }.start();

        } else {
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
            //Get launch date
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy.");
            sdf.toLocalizedPattern();
            Date date = launchItem.getWindowstart();
            launchDate = sdf.format(date);

            holder.launch_date.setText("To be determined... " + launchDate);
        } else {
            if (sharedPref.getBoolean("local_time", true)) {
                Date date = launchItem.getWindowstart();
                launchDate = df.format(date);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a zzz");
                Date date = launchItem.getWindowstart();
                launchDate = sdf.format(date);
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
            if (separated[1].length() > 4) {
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
        holder.title.setText(launchItem.getRocket().getName());
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

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, content, location, content_mission, content_mission_description,
                launch_date, content_status, content_TMinus_status,
                watchButton, shareButton, exploreButton;
        public LinearLayout content_mission_description_view;
        public ImageView categoryIcon;
        public FloatingActionButton exploreFab;
        public CountDownTimer timer;

        public ImageView map_view;
        public GoogleMap gMap;
        protected LatLng mMapLocation;

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

            map_view = (ImageView) view.findViewById(R.id.map_view);
            map_view.setClickable(false);
//            initializeMapView();

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

            LaunchRealm launch = new LaunchRealm();
            launch = launchList.get(position);
            Intent sendIntent = new Intent();

            SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a zzz");
            df.toLocalizedPattern();

            Date date = launch.getWindowstart();
            String launchDate = df.format(date);

            switch (v.getId()) {
                case R.id.watchButton:
                    Timber.d("Watch: %s", launchList.get(position).getVidURLs().size());
                    if (launchList.get(position).getVidURLs().size() > 0) {
                        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(context);
                        for (RealmStr s : launchList.get(position).getVidURLs()) {
                            //Do your stuff here
                            adapter.add(new MaterialSimpleListItem.Builder(context)
                                    .content(s.getVal())
                                    .build());
                        }

                        new MaterialDialog.Builder(context)
                                .title("Select a source:")
                                .adapter(adapter, new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        Uri watchUri = Uri.parse(launchList.get(position).getVidURLs().get(which).getVal());
                                        Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                                        context.startActivity(i);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                    break;
                case R.id.exploreButton:
                    Timber.d("Explore: %s", launchList.get(position).getId());
                    Intent exploreIntent = new Intent(context, LaunchDetailActivity.class);
                    exploreIntent.putExtra("TYPE", "LaunchID");
                    exploreIntent.putExtra("launchID", launch.getId());
                    context.startActivity(exploreIntent);
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

                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        Toast.makeText(context, "Loading " + launchList.get(position).getLocation().getPads().get(0).getName(), Toast.LENGTH_LONG).show();
                        context.startActivity(mapIntent);
                    }
                    break;
            }
        }

//        public void initializeMapView() {
//            if (map_view != null) {
//                // Initialise the MapView
//                map_view.onCreate(null);
//                // Set the map ready callback to receive the GoogleMap object
//                map_view.getMapAsync(this);
//            }
//        }
//
//        public void setMapLocation(double lat, double lng) {
//            LatLng latLng = new LatLng(lat, lng);
//            mMapLocation = latLng;
//
//            // If the map is ready, update its content.
//            if (gMap != null) {
//                updateMapContents();
//            } else {
//                Timber.e("MapView is null");
//            }
//        }
//
//        @Override
//        public void onMapReady(GoogleMap googleMap) {
//            //TODO: Allow user to update this 1-normal 2-satellite 3-Terrain
//            // https://goo.gl/OkexW7
//            MapsInitializer.initialize(context.getApplicationContext());
//
//            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//            googleMap.getUiSettings().setMapToolbarEnabled(false);
//
//            gMap = googleMap;
//            gMap.getUiSettings().setAllGesturesEnabled(false);
//
//
//            // If we have map data, update the map content.
//            if (mMapLocation != null) {
//                updateMapContents();
//            } else {
//                Timber.e("mMapLocation is null");
//            }
//        }
//
//        protected void updateMapContents() {
//            // Since the mapView is re-used, need to remove pre-existing mapView features.
//            gMap.clear();
//
//            // Update the mapView feature data and camera position.
//            gMap.addMarker(new MarkerOptions().position(mMapLocation));
//
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mMapLocation, 3.2f);
//            gMap.moveCamera(cameraUpdate);
//        }
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
