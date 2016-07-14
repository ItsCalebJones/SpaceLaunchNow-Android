package me.calebjones.spacelaunchnow.ui.fragment.launches.details;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.bumptech.glide.Glide;
import com.mypopsy.maps.StaticMap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.content.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.content.models.realm.RocketDetailsRealm;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.ui.fragment.BaseFragment;
import timber.log.Timber;


public class SummaryDetailFragment extends BaseFragment {

    private SharedPreferences sharedPref;
    private static ListPreferences sharedPreference;
    private Context context;
    public ImageView staticMap;

    public static LaunchRealm detailLaunch;
    private RocketDetailsRealm launchVehicle;
    private FloatingActionButton fab;
    private LinearLayout agency_one;
    private LinearLayout agency_two;
    private LinearLayout vehicle_spec_view;
    private TextView launch_date_title;
    private TextView date;
    private TextView launch_window_start;
    private TextView launch_window_end;
    private TextView launch_status;
    private TextView launch_vehicle;
    private TextView launch_configuration;
    private TextView launch_family;
    private TextView launch_vehicle_specs_height;
    private TextView launch_vehicle_specs_diameter;
    private TextView launch_vehicle_specs_stages;
    private TextView launch_vehicle_specs_leo;
    private TextView launch_vehicle_specs_gto;
    private TextView launch_vehicle_specs_launch_mass;
    private TextView launch_vehicle_specs_thrust;
    private TextView watchButton;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.context = getContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        if (sharedPreference.getNightMode()) {
            view = inflater.inflate(R.layout.dark_launch_summary, container, false);
        } else {
            view = inflater.inflate(R.layout.light_launch_summary, container, false);
        }

        staticMap = (ImageView) view.findViewById(R.id.map_view_summary);
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
        return view;
    }

    @Override
    public void onResume() {
        setUpViews();
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setUpViews(){
        detailLaunch = ((LaunchDetailActivity)getActivity()).getLaunch();
        if(detailLaunch.getRocket() != null) {
            getLaunchVehicle(detailLaunch);
        }

        double dlat = 0;
        double dlon = 0;
        if (detailLaunch.getLocation() != null && detailLaunch.getLocation().getPads() != null) {
            dlat = detailLaunch.getLocation().getPads().get(0).getLatitude();
            dlon = detailLaunch.getLocation().getPads().get(0).getLongitude();
        }

        // Getting status
        if (dlat == 0 && dlon == 0 || Double.isNaN(dlat) || Double.isNaN(dlon) || dlat == Double.NaN || dlon == Double.NaN) {
            if (staticMap != null) {
                staticMap.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            }
        } else {
            staticMap.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
//                holder.setMapLocation(dlat, dlon);
//                // Keep track of MapView
//                mMaps.add(holder.staticMap);
            final Resources res = context.getResources();
            final StaticMap map = new StaticMap()
                    .center(dlat, dlon)
                    .scale(4)
                    .type(StaticMap.Type.ROADMAP)
                    .zoom(7)
                    .marker(dlat, dlon)
                    .key(res.getString(R.string.GoogleMapsKey));

            //Strange but necessary to calculate the height/width
            staticMap.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    map.size(staticMap.getWidth() / 2,
                            staticMap.getHeight() / 2);

                    Timber.v("onPreDraw: %s", map.toString());
                    Glide.with(context).load(map.toString())
                            .error(R.drawable.placeholder)
                            .centerCrop()
                            .into(staticMap);
                    staticMap.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }

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

        if (detailLaunch.getVidURLs() != null && detailLaunch.getVidURLs().size() > 0){
            watchButton.setVisibility(View.VISIBLE);
            watchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detailLaunch.getVidURLs().size() > 1) {
                        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(context);
                        for (RealmStr s : detailLaunch.getVidURLs()) {
                            //Do your stuff here
                            adapter.add(new MaterialSimpleListItem.Builder(context)
                                    .content(s.toString())
                                    .build());
                        }

                        new MaterialDialog.Builder(context)
                                .title("Select a source:")
                                .adapter(adapter, new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        Uri watchUri = Uri.parse(detailLaunch.getVidURLs().get(which).toString());
                                        Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                                        startActivity(i);
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        Uri watchUri = Uri.parse(detailLaunch.getVidURL());
                        Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                        startActivity(i);
                    }
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
            launch_vehicle_specs_stages.setText(String.format("Stages: %d", launchVehicle.getMax_Stage()));
            launch_vehicle_specs_leo.setText(String.format("Payload to LEO: %s kg", launchVehicle.getLEOCapacity()));
            launch_vehicle_specs_gto.setText(String.format("Payload to GTO: %s kg", launchVehicle.getGTOCapacity()));
            launch_vehicle_specs_launch_mass.setText(String.format("Mass at Launch: %s Tons", launchVehicle.getLaunchMass()));
            launch_vehicle_specs_thrust.setText(String.format("Thrust at Launch: %s kN", launchVehicle.getTOThrust()));
        } else {
            vehicle_spec_view.setVisibility(View.GONE);
        }

        //Try to convert to Month day, Year.
        mDate = detailLaunch.getNet();
        dateText = output.format(mDate);
        if (mDate.before(Calendar.getInstance().getTime())){
            launch_date_title.setText("Launch Date");
        }

        date.setText(dateText);

        //TODO: Get launch window only if wstamp and westamp is available, hide otherwise.
        if (detailLaunch.getWsstamp() > 0 && detailLaunch.getWestamp() > 0){
            setWindowStamp();
        } else {
            setWindowStartEnd();
        }

    }

    private void getLaunchVehicle(LaunchRealm vehicle) {
        String query;
        if (vehicle.getRocket().getName().contains("Space Shuttle")){
            query = "Space Shuttle";
        } else {
            query = vehicle.getRocket().getName();
        }

        launchVehicle = getRealm().where(RocketDetailsRealm.class).contains("name", query).findFirst();
    }


    private void setWindowStamp() {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter;
        if (sharedPref.getBoolean("24_hour_mode", false)) {
            formatter = new SimpleDateFormat("kk:mm zzz");
        } else {
            formatter = new SimpleDateFormat("hh:mm a zzz");
        }

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
            if(detailLaunch.getWindowstart().toString().length() > 0
                    || detailLaunch.getWindowend().toString().length() > 0){
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
}