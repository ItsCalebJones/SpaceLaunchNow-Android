package me.calebjones.spacelaunchnow.content.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import timber.log.Timber;

/**
 * Adapts UpcomingLaunch data to the LaunchFragment
 */
public class LaunchBigAdapter extends RecyclerView.Adapter<LaunchBigAdapter.ViewHolder> implements SectionIndexer {
    public int position;
    private String launchDate;
    private List<Launch> launchList;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private static SharedPreference sharedPreference;

    public LaunchBigAdapter(Context context) {
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
        sharedPreference = SharedPreference.getInstance(mContext);

        if (sharedPreference.getNightMode()) {
            m_theme = R.layout.dark_content_list_item;
        } else {
            m_theme = R.layout.light_content_list_item;
        }
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(m_theme, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        final Launch launchItem = launchList.get(i);


        position = i;

        //TODO These are slightly rounded when converting from double to long
        double dlat = launchItem.getLocation().getPads().get(0).getLatitude();
        double dlon = launchItem.getLocation().getPads().get(0).getLongitude();

        if (dlat == 0 && dlon == 0 || Double.isNaN(dlat) || Double.isNaN(dlon) || dlat == Double.NaN || dlon == Double.NaN) {
            if (holder.map_view != null) {
                holder.map_view.setVisibility(View.GONE);
                holder.exploreFab.setVisibility(View.GONE);
            }
        } else {
            holder.map_view.setVisibility(View.VISIBLE);
            holder.exploreFab.setVisibility(View.VISIBLE);
            holder.setMapLocation(dlat, dlon);
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
                holder.content_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                break;
            case 3:
                //Success for launch
                holder.content_status.setText(R.string.status_success);
                holder.content_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorGo));
                break;
            case 4:
                //Failure to launch
                holder.content_status.setText(R.string.status_failure);
                holder.content_status.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                break;
        }

        //If timestamp is available calculate TMinus and date.
        if (launchItem.getNetstamp() > 0) {
            long longdate = launchItem.getNetstamp();
            longdate = longdate * 1000;
            final Date date = new Date(longdate);

            Calendar future = DateToCalendar(date);
            Calendar now = rightNow;
            CountDownTimer timer;

            now.setTimeInMillis(System.currentTimeMillis());
            long timeToFinish = future.getTimeInMillis() - now.getTimeInMillis();
            if (timeToFinish < 86400000) {
                timer = new CountDownTimer(future.getTimeInMillis() - now.getTimeInMillis(), 1000) {
                    StringBuilder time = new StringBuilder();

                    @Override
                    public void onFinish() {
                        holder.content_TMinus_status.setText("Check back later for launch status.");
                        //mTextView.setText("Times Up!");
                    }

                    @Override
                    public void onTick(long millisUntilFinished) {
                        time.setLength(0);
                        // Use days if appropriate
                        long hours = (millisUntilFinished / 3600000) % 24;
                        long mins = (millisUntilFinished / 60000) % 60;
                        long seconds = (millisUntilFinished / 1000) % 60;

                        if (Long.valueOf(hours) >= 1) {
                            holder.content_TMinus_status.setText(String.format("%s Hours : %s Minutes : %s Seconds", hours, mins, seconds));
                        } else if (Long.valueOf(mins) > 1) {
                            holder.content_TMinus_status.setText(String.format("%s Minutes : %s Seconds", mins, seconds));
                        }
                    }
                }.start();
            } else {
                long days = timeToFinish / 86400000;
                long hours = (timeToFinish / 3600000) % 24;
                holder.content_TMinus_status.setText(String.format("%s Day(s) : %s Hour(s)", Long.valueOf(days), Long.valueOf(hours)));
            }

        } else {
            holder.content_TMinus_status.setText(" Unknown");
        }

        //Get launch date
        if (sharedPref.getBoolean("local_time", true)) {
            Date date = new Date(launchItem.getWindowstart());
            launchDate = df.format(date);
        } else {
            launchDate = launchItem.getWindowstart();
        }
        holder.launch_date.setText(launchDate);

        if (launchItem.getVidURL().length() == 0) {
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

        //If location is available then see if pad and agency informaiton is avaialble.
        if (launchItem.getLocation().getName() != null) {
            if (launchItem.getLocation().getPads().size() > 0 && launchItem.getLocation().getPads().
                    get(0).getAgencies().size() > 0) {

                //Get the first CountryCode
                String country = launchItem.getLocation().getPads().
                        get(0).getAgencies().get(0).getCountryCode();
                country = (country.substring(0, 3));

                //Get the location remove the pad information
                String location = launchItem.getLocation().getName() + " " + country;
                launchItem.getLocation().getPads().
                        get(0).getAgencies().get(0).getCountryCode();
                location = (location.substring(location.indexOf(", ") + 2));

                holder.location.setText(location);
            } else {
                holder.location.setText(launchItem.getLocation().getName()
                        .substring(launchItem.getLocation().getName().indexOf(", ") + 2));
            }
        }
        holder.title.setText(launchItem.getRocket().getName());
    }

    //Recycling GoogleMap for list item
    @Override
    public void onViewRecycled(ViewHolder holder) {
        // Cleanup MapView here?
        if (holder.gMap != null) {
            System.gc();
            holder.gMap.clear();
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

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback {
        public TextView title, content, location, content_mission, content_mission_description,
                launch_date, content_status, content_TMinus_status,
                watchButton, shareButton, exploreButton;
        public LinearLayout content_mission_description_view;
        public FloatingActionButton exploreFab;

        public MapView map_view;
        public GoogleMap gMap;
        protected LatLng mMapLocation;

        //Add content to the card
        public ViewHolder(View view) {
            super(view);

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

            map_view = (MapView) view.findViewById(R.id.map_view);

            shareButton.setOnClickListener(this);
            exploreButton.setOnClickListener(this);
            watchButton.setOnClickListener(this);
            exploreFab.setOnClickListener(this);

            if (map_view != null) {
                map_view.onCreate(null);
                map_view.onResume();
                map_view.getMapAsync(this);
            }
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
                    exploreIntent.putExtra("TYPE", "Launch");
                    exploreIntent.putExtra("launch", launch);
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
                case R.id.fab:
                    String location = launchList.get(position).getLocation().getName();
                    location = (location.substring(location.indexOf(",") + 1));

                    Timber.d("FAB: %s ", location);

                    double dlat = launchList.get(position).getLocation().getPads().get(0).getLatitude();
                    double dlon = launchList.get(position).getLocation().getPads().get(0).getLongitude();

                    Uri gmmIntentUri = Uri.parse("geo:" + dlat + ", " + dlon + "?z=12&q=" + dlat + ", " + dlon);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
                        Toast.makeText(mContext, "Loading " + launchList.get(position).getLocation().getPads().get(0).getName(), Toast.LENGTH_LONG).show();
                        mContext.startActivity(mapIntent);
                    }
                    break;
            }
        }

        public void setMapLocation(double lat, double lng) {
            mMapLocation = new LatLng(lat, lng);

            // If the map is ready, update its content.
            if (gMap != null) {
                updateMapContents();
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            //TODO: Allow user to update this 1-normal 2-satellite 3-Terrain
            // https://goo.gl/OkexW7
            MapsInitializer.initialize(mContext);

            gMap = googleMap;
            gMap.getUiSettings().setAllGesturesEnabled(false);

            googleMap.setMapType(1);
            googleMap.getUiSettings().setMapToolbarEnabled(false);

            // If we have map data, update the map content.
            if (mMapLocation != null) {
                updateMapContents();
            }
        }

        protected void updateMapContents() {
            // Since the mapView is re-used, need to remove pre-existing mapView features.
            gMap.clear();

            // Update the mapView feature data and camera position.
            gMap.addMarker(new MarkerOptions().position(mMapLocation));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mMapLocation, 3.2f);
            gMap.moveCamera(cameraUpdate);
        }
    }
}
