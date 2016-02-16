package me.calebjones.spacelaunchnow.ui.fragment.launches.details;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.DatabaseManager;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.RocketDetails;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import timber.log.Timber;
import xyz.hanks.library.SmallBang;

public class SummaryDetailFragment extends Fragment implements OnMapReadyCallback {

    private SharedPreferences sharedPref;
    private static SharedPreference sharedPreference;
    private Context context;
    private SmallBang mSmallBang;
    public MapView map_view;
    public GoogleMap gMap;
    protected LatLng mMapLocation;

    public static Launch detailLaunch;
    private RocketDetails launchVehicle;
    private FloatingActionButton fab;
    private LinearLayout agency_one, agency_two, vehicle_spec_view;
    private TextView launch_date_title, date, launch_window_start, launch_window_end, launch_status
            , launch_vehicle, launch_configuration, launch_family, launch_vehicle_specs_height,
            launch_vehicle_specs_diameter,launch_vehicle_specs_stages,launch_vehicle_specs_leo,
            launch_vehicle_specs_gto,launch_vehicle_specs_launch_mass, launch_vehicle_specs_thrust,
            watchButton;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.context = getContext();

        sharedPreference = SharedPreference.getInstance(this.context);


        if (sharedPreference.getNightMode()) {
            view = inflater.inflate(R.layout.dark_launch_summary, container, false);
        } else {
            view = inflater.inflate(R.layout.light_launch_summary, container, false);
        }

        map_view = (MapView) view.findViewById(R.id.map_view_summary);
        launch_date_title = (TextView) view.findViewById(R.id.launch_date_title);
        date = (TextView) view.findViewById(R.id.date);
        launch_window_start = (TextView) view.findViewById(R.id.launch_window_start);
        launch_window_end = (TextView) view.findViewById(R.id.launch_window_end);
        launch_status = (TextView) view.findViewById(R.id.launch_status);
        launch_vehicle = (TextView) view.findViewById(R.id.launch_vehicle);
        launch_configuration = (TextView) view.findViewById(R.id.launch_configuration);
        launch_family = (TextView) view.findViewById(R.id.launch_family);
        launch_vehicle_specs_stages = (TextView) view.findViewById(R.id.launch_vehicle_specs_stages);
        launch_vehicle_specs_height = (TextView) view.findViewById(R.id.launch_vehicle_specs_height);
        launch_vehicle_specs_diameter = (TextView) view.findViewById(R.id.launch_vehicle_specs_diameter);
        launch_vehicle_specs_leo = (TextView) view.findViewById(R.id.launch_vehicle_specs_leo);
        launch_vehicle_specs_gto = (TextView) view.findViewById(R.id.launch_vehicle_specs_gto);
        launch_vehicle_specs_launch_mass = (TextView) view.findViewById(R.id.launch_vehicle_specs_launch_mass);
        launch_vehicle_specs_thrust = (TextView) view.findViewById(R.id.launch_vehicle_specs_thrust);
        watchButton = (TextView) view.findViewById(R.id.watchButton);
        agency_one = (LinearLayout) view.findViewById(R.id.agency_one);
        agency_two = (LinearLayout) view.findViewById(R.id.agency_two);
        vehicle_spec_view = (LinearLayout) view.findViewById(R.id.vehicle_spec_view);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_explore);

        setUpViews();
        return view;
    }

    private void setUpMap() {
        if (map_view != null) {
            map_view.onCreate(null);
            map_view.onResume();
            map_view.getMapAsync(this);
        }
        double dlat = detailLaunch.getLocation().getPads().get(0).getLatitude();
        double dlon = detailLaunch.getLocation().getPads().get(0).getLongitude();
        setMapLocation(dlat, dlon);
    }

    public void setUpViews(){
        detailLaunch = ((LaunchDetailActivity)getActivity()).getLaunch();
        getLaunchVehicle(detailLaunch);

        setUpMap();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = detailLaunch.getLocation().getName();
                location = (location.substring(location.indexOf(",") + 1));

                Timber.d("FAB: %s ", location);

                double dlat = detailLaunch.getLocation().getPads().get(0).getLatitude();
                double dlon = detailLaunch.getLocation().getPads().get(0).getLongitude();

                Uri gmmIntentUri = Uri.parse("geo:" + dlat + ", " + dlon + "?z=12&q=" + dlat + ", " + dlon);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    Toast.makeText(context, "Loading " + detailLaunch.getLocation().getPads().get(0).getName(), Toast.LENGTH_LONG).show();
                    context.startActivity(mapIntent);
                }
            }
        });

        //Setup SimpleDateFormat to parse out getNet date.
        SimpleDateFormat input = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss zzz");
        SimpleDateFormat output = new SimpleDateFormat("MMMM dd, yyyy");
        input.toLocalizedPattern();

        Date mDate;
        String dateText = null;

        switch(detailLaunch.getStatus()){
            case 1:
                launch_status.setText("Launch is GO");
                break;
            case 2:
                launch_status.setText("Launch is NO-GO");
                break;
            case 3:
                launch_status.setText("Launch was successful.");
                break;
            case 4:
                launch_status.setText("Launch failure occurred.");
                break;
        }

        if (detailLaunch.getVidURL() != null && detailLaunch.getVidURL().length() > 0){
            watchButton.setVisibility(View.VISIBLE);
            watchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getVidURL()));
                    startActivity(i);
                }
            });
        } else {
            watchButton.setVisibility(View.GONE);
        }

        launch_vehicle.setText(detailLaunch.getRocket().getName());
        launch_configuration.setText(String.format("Configuration: %s", detailLaunch.getRocket().getConfiguration()));
        launch_family.setText(String.format("Family: %s", detailLaunch.getRocket().getFamilyname()));
        if (launchVehicle != null){
            vehicle_spec_view.setVisibility(View.VISIBLE);
            launch_vehicle_specs_height.setText(String.format("Height: %s Meters", launchVehicle.getLength()));
            launch_vehicle_specs_diameter.setText(String.format("Diameter: %s Meters", launchVehicle.getDiameter()));
            launch_vehicle_specs_stages.setText(String.format("Stages: %d", launchVehicle.getMaxStage()));
            launch_vehicle_specs_leo.setText(String.format("Payload to LEO: %s kg", launchVehicle.getLEOCapacity()));
            launch_vehicle_specs_gto.setText(String.format("Payload to GTO: %s kg", launchVehicle.getGTOCapacity()));
            launch_vehicle_specs_launch_mass.setText(String.format("Mass at Launch: %s Tons", launchVehicle.getLaunchMass()));
            launch_vehicle_specs_thrust.setText(String.format("Thrust at Launch: %s kN", launchVehicle.getTOThrust()));
        } else {
            vehicle_spec_view.setVisibility(View.GONE);
        }

        //Try to convert to Month day, Year.
        try {
            mDate = input.parse(detailLaunch.getNet());
            dateText = output.format(mDate);
            if (mDate.before(Calendar.getInstance().getTime())){
                launch_date_title.setText("Launch Date");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        date.setText(dateText);

        //TODO: Get launch window only if wstamp and westamp is available, hide otherwise.
        if (detailLaunch.getWsstamp() > 0 && detailLaunch.getWestamp() > 0){
            setWindowStamp();
        } else {
            setWindowStartEnd();
        }

    }

    private void getLaunchVehicle(Launch vehicle) {
        String query;
        if (vehicle.getRocket().getName().contains("Space Shuttle")){
            query = "Space Shuttle";
        } else {
            query = vehicle.getRocket().getName();
        }
        DatabaseManager databaseManager = new DatabaseManager(context);
        launchVehicle = databaseManager.getLaunchVehicle(query);
    }


    private void setWindowStamp() {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aaa zzz");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long startDate = detailLaunch.getWsstamp();
        startDate = startDate * 1000;
        calendarStart.setTimeInMillis(startDate);
        String start = formatter.format(calendarStart.getTime());

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendarEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long endDate = detailLaunch.getWestamp();
        endDate = endDate * 1000;
        calendarEnd.setTimeInMillis(endDate);
        String end = formatter.format(calendarEnd.getTime());

        if (!start.equals(end)){
            if(start.length() == 0
                    || end.length() == 0){
                launch_window_start.setText(String.format("Launch Time: %s",
                        start));
                launch_window_end.setVisibility(View.GONE);
            } else{
                launch_window_start.setText(String.format("Window Start: %s",
                        start));
                launch_window_end.setVisibility(View.VISIBLE);
                launch_window_end.setText(String.format("Window End: %s",
                        end));
            }
        } else {
            launch_window_start.setText(String.format("Launch Time: %s",
                    start));
            launch_window_end.setVisibility(View.GONE);
        }
    }

    private void setWindowStartEnd() {
        if (!detailLaunch.getWindowstart().equals(detailLaunch.getWindowend())){
            if(detailLaunch.getWindowstart().length() > 0
                    || detailLaunch.getWindowend().length() > 0){
                launch_window_start.setText("Launch Window unavailable.");
                launch_window_end.setVisibility(View.INVISIBLE);
            } else{
                launch_window_start.setText(String.format("Window Start: %s",
                        detailLaunch.getWindowstart()));
                launch_window_end.setVisibility(View.VISIBLE);
                launch_window_end.setText(String.format("Window End: %s",
                        detailLaunch.getWindowend()));
            }
        } else {
            launch_window_start.setText(String.format("Launch Time: %s",
                    detailLaunch.getWindowstart()));
            launch_window_end.setVisibility(View.INVISIBLE);
        }
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static Fragment newInstance() {
        return new SummaryDetailFragment();
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
        MapsInitializer.initialize(context);

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

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mMapLocation, 8f);
        gMap.moveCamera(cameraUpdate);
    }
}