package me.calebjones.spacelaunchnow.content.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetail;
import timber.log.Timber;

/**
 * Adapts UpcomingLaunch data to the LaunchFragment
 */
public class LaunchAdapter extends RecyclerView.Adapter<LaunchAdapter.ViewHolder> implements SectionIndexer {
    public int position;
    private List<Launch> launchList;
    private Context mContext;
    private Calendar rightNow;
    private SharedPreferences sharedPref;
    private static SharedPreference sharedPreference;

    public LaunchAdapter(Context context) {
        rightNow = Calendar.getInstance();
        launchList = new ArrayList<>();
        this.mContext = context;

    }

    public void addItems(List<Launch> launchList) {
        if (this.launchList == null) {
            this.launchList = launchList;
        } else {
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

        if (dlat == 0 && dlon == 0) {
            if (holder.map_view != null) {
                holder.map_view.setVisibility(View.GONE);
                holder.exploreFab.setVisibility(View.GONE);
            }
        } else {
            holder.setMapLocation(dlat, dlon);
        }

        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd yyyy hh:mm a zzz");
        df.toLocalizedPattern();

        //If timestamp is available calculate TMinus and date.
        if (launchItem.getWsstamp() > 0) {
            long longdate = launchItem.getWsstamp();
            longdate = longdate * 1000;
            Date date = new Date(longdate);

            long countdown = rightNow.getTimeInMillis();

            Date date2 = new Date(countdown); // Insert value in date1 & date2 as your need

            long dateDiff = date.getTime() - date2.getTime();
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(dateDiff),
                    TimeUnit.MILLISECONDS.toMinutes(dateDiff) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(dateDiff) % TimeUnit.MINUTES.toSeconds(1));

            holder.launch_date.setText(df.format(date));
            holder.content_TMinus_status.setText(" " + hms);
        } else {
            Date date = new Date(launchItem.getWindowstart());
            holder.launch_date.setText(df.format(date));
            holder.content_TMinus_status.setText(" Unknown");
        }

        if (launchItem.getVidURL().length() == 0) {
            holder.watchButton.setVisibility(View.GONE);
        }

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


        if (launchItem.getMissions().size() > 0) {
            holder.content_mission.setText(launchItem.getMissions().get(0).getName());
            holder.content_mission_description.setText(launchItem.getMissions().
                    get(0).getDescription());
        }

        //If location is available then see if pad and agency informaiton is avaialble.
        if (launchItem.getLocation().getName() != null) {
            if (launchItem.getLocation().getPads().size() > 0 && launchItem.getLocation().getPads().
                    get(0).getAgencies().size() > 0) {

                //Get the first CountryCode
                String country = launchItem.getLocation().getPads().
                        get(0).getAgencies().get(0).getCountryCode();
                country = (country.substring(0, 2));

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
            holder.gMap.clear();
            holder.gMap.setMapType(GoogleMap.MAP_TYPE_NONE);
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
        public TextView title, content, location, content_mission, content_mission_description,
                launch_date, content_status_title, content_status, content_TMinus_status,
                content_Tminus_title, watchButton, shareButton, exploreButton;
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

            content_status_title = (TextView) view.findViewById(R.id.content_status_title);
            content_status = (TextView) view.findViewById(R.id.content_status);

            content_TMinus_status = (TextView) view.findViewById(R.id.content_TMinus_status);
            content_Tminus_title = (TextView) view.findViewById(R.id.content_Tminus_title);

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

            switch (v.getId()) {
                case R.id.watchButton:
                    Timber.d("Watch: %s", launchList.get(position).getVidURL());
                    Uri watchUri = Uri.parse(launchList.get(position).getVidURL());
                    Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                    mContext.startActivity(i);
                    break;
                case R.id.exploreButton:
                    Timber.d("Explore: %s", launchList.get(position).getId());
                    Launch launch = launchList.get(position);
                    Intent exploreIntent = new Intent(mContext, LaunchDetail.class);
                    exploreIntent.putExtra("launch", launch);
                    mContext.startActivity(exploreIntent);
                    break;
                case R.id.shareButton:
                    Timber.d("Share: %s", launchList.get(position).getLocation().getName());
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
