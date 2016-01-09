package me.calebjones.spacelaunchnow.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.LaunchVehicle;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import timber.log.Timber;

public class AgencyDetailFragment extends Fragment {

    private SharedPreferences sharedPref;
    private static SharedPreference sharedPreference;
    private Context context;
    public MapView map_view;
    public GoogleMap gMap;
    protected LatLng mMapLocation;

    public static Launch detailLaunch;
    private LaunchVehicle launchVehicle;
    private LinearLayout mission_one, mission_two, agency_one, agency_two;
    private TextView mission_agency_type, mission_agency_one, mission_agency_type_one,
            mission_infoButton_one, mission_wikiButton_one, mission_agency_two,
            mission_agency_type_two, mission_infoButton_two, mission_wikiButton_two,
            launch_agency_type, launch_vehicle_agency_one, launch_agency_type_one, infoButton_one,
            wikiButton_one, launch_vehicle_agency_two, launch_agency_type_two, infoButton_two,
            wikiButton_two;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.context = getContext();

        sharedPreference = SharedPreference.getInstance(this.context);


        if (sharedPreference.getNightMode()) {
            view = inflater.inflate(R.layout.dark_launch_agency, container, false);
        } else {
            view = inflater.inflate(R.layout.light_launch_agency, container, false);
        }

        mission_one = (LinearLayout) view.findViewById(R.id.mission_one);
        mission_two = (LinearLayout) view.findViewById(R.id.mission_two);

        mission_agency_type = (TextView) view.findViewById(R.id.mission_agency_type);

        mission_agency_one = (TextView) view.findViewById(R.id.mission_vehicle_agency_one);
        mission_agency_type_one = (TextView) view.findViewById(R.id.mission_agency_type_one);
        mission_infoButton_one = (TextView) view.findViewById(R.id.mission_infoButton_one);
        mission_wikiButton_one  = (TextView) view.findViewById(R.id.mission_wikiButton_one);

        mission_agency_two = (TextView) view.findViewById(R.id.mission_vehicle_agency_two);
        mission_agency_type_two = (TextView) view.findViewById(R.id.mission_agency_type_two);
        mission_infoButton_two = (TextView) view.findViewById(R.id.mission_infoButton_two);
        mission_wikiButton_two  = (TextView) view.findViewById(R.id.mission_wikiButton_two);

        agency_one = (LinearLayout) view.findViewById(R.id.agency_one);
        agency_two = (LinearLayout) view.findViewById(R.id.agency_two);

        launch_agency_type = (TextView) view.findViewById(R.id.launch_agency_type);

        launch_vehicle_agency_one = (TextView) view.findViewById(R.id.launch_vehicle_agency_one);
        launch_agency_type_one = (TextView) view.findViewById(R.id.launch_agency_type_one);
        infoButton_one = (TextView) view.findViewById(R.id.infoButton_one);
        wikiButton_one  = (TextView) view.findViewById(R.id.wikiButton_one);

        launch_vehicle_agency_two = (TextView) view.findViewById(R.id.launch_vehicle_agency_two);
        launch_agency_type_two = (TextView) view.findViewById(R.id.launch_agency_type_two);
        infoButton_two = (TextView) view.findViewById(R.id.infoButton_two);
        wikiButton_two  = (TextView) view.findViewById(R.id.wikiButton_two);

        setUpViews();
        return view;
    }

    public void setUpViews(){
        detailLaunch = ((LaunchDetailActivity)getActivity()).getLaunch();


        int pads = detailLaunch.getLocation().getPads().size();
        int mission_agencies= 0;
        if (pads > 0){
            mission_agencies = detailLaunch.getLocation().getPads().get(0).getAgencies().size();
        }
        int vehicle_agencies = detailLaunch.getRocket().getAgencies().size();

        if (mission_agencies >= 2){
            setTwoMissionAgencies();
        } else if (mission_agencies == 1){
            setOneMissionAgencies();
        } else {
            setNoMissionAgencies();
        }

        if (vehicle_agencies >= 2){
            setTwoVehicleAgencies();
        } else if (vehicle_agencies == 1){
            setOneVehicleAgencies();
        } else {
            setNoVehicleAgencies();
        }
    }

    private void setNoMissionAgencies() {
        launch_agency_type.setVisibility(View.VISIBLE);
        agency_one.setVisibility(View.GONE);
        agency_two.setVisibility(View.GONE);

    }

    private void setOneMissionAgencies() {
        String agency = getAgencyType(detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getType());
        agency_one.setVisibility(View.VISIBLE);
        agency_two.setVisibility(View.GONE);
        launch_agency_type.setVisibility(View.GONE);
        launch_vehicle_agency_one.setText(String.format("%s (%s)", detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getName(), detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getAbbrev()));
        launch_agency_type_one.setText("Type: " + agency);

        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getWikiURL().length() > 0){
            wikiButton_one.setVisibility(View.VISIBLE);
            wikiButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getWikiURL()));
                    startActivity(i);
                    Timber.d("Wiki One!");
                }
            });
        } else {
            wikiButton_one.setVisibility(View.GONE);
        }


        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getInfoURL().length() > 0){
            infoButton_one.setVisibility(View.VISIBLE);
            infoButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getInfoURL()));
                    startActivity(i);
                    Timber.d("Wiki One!");
                }
            });
        } else {
            infoButton_one.setVisibility(View.GONE);
        }
    }

    private void setTwoMissionAgencies() {
        String agency = getAgencyType(detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getType());
        String agencyTwo = getAgencyType(detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getType());

        agency_one.setVisibility(View.VISIBLE);
        agency_two.setVisibility(View.VISIBLE);
        launch_agency_type.setVisibility(View.GONE);
        launch_agency_type_one.setText(String.format("Type: %s", agency));
        launch_agency_type_two.setText(String.format("Type: %s", agencyTwo));
        launch_vehicle_agency_one.setText(String.format("%s (%s)", detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getName(), detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getAbbrev()));
        launch_vehicle_agency_two.setText(String.format("%s (%s)", detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getName(), detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getAbbrev()));

        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getWikiURL().length() > 0){
            wikiButton_one.setVisibility(View.VISIBLE);
            wikiButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getWikiURL()));
                    startActivity(i);
                }
            });
        } else {
            wikiButton_one.setVisibility(View.GONE);
        }


        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getInfoURL().length() > 0){
            infoButton_one.setVisibility(View.VISIBLE);
            infoButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getInfoURL()));
                    startActivity(i);
                }
            });
        } else {
            infoButton_one.setVisibility(View.GONE);
        }

        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getWikiURL().length() > 0){
            wikiButton_two.setVisibility(View.VISIBLE);
            wikiButton_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getWikiURL()));
                    startActivity(i);
                }
            });
        } else {
            wikiButton_two.setVisibility(View.GONE);
        }


        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getInfoURL().length() > 0){
            infoButton_two.setVisibility(View.VISIBLE);
            infoButton_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getInfoURL()));
                    startActivity(i);
                }
            });
        } else {
            infoButton_two.setVisibility(View.GONE);
        }
    }

    private void setNoVehicleAgencies() {
        mission_agency_type.setVisibility(View.VISIBLE);
        mission_one.setVisibility(View.GONE);
        mission_two.setVisibility(View.GONE);

    }

    private void setOneVehicleAgencies() {
        String countryCode = detailLaunch.getRocket().getAgencies().get(0).getCountryCode();
        String agency = getAgencyType(detailLaunch.getRocket().getAgencies().get(0).getType());
        if (countryCode.length() > 9){
            countryCode = countryCode.substring(0,2) + ", "+ countryCode.substring(4,6) + ", "+ countryCode.substring(7,9) + "...";
            agency = agency + " | Multinational";
        } else {
            countryCode = countryCode.substring(0,2);
        }
        mission_one.setVisibility(View.VISIBLE);
        mission_two.setVisibility(View.GONE);
        mission_agency_type.setVisibility(View.GONE);
        mission_agency_one.setText(String.format("%s (%s) %s", detailLaunch.getRocket().getAgencies().get(0).getName(), detailLaunch.getRocket().getAgencies().get(0).getAbbrev(), countryCode));
        mission_agency_type_one.setText("Type: " + agency);

        if (detailLaunch.getRocket().getAgencies().get(0).getInfoURL().length() > 0){
            mission_wikiButton_one.setVisibility(View.VISIBLE);
            mission_wikiButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getWikiURL()));
                    startActivity(i);
                    Timber.d("Wiki One!");
                }
            });
        } else {
            mission_wikiButton_one.setVisibility(View.GONE);
        }


        if (detailLaunch.getRocket().getAgencies().get(0).getInfoURL().length() > 0){
            mission_infoButton_one.setVisibility(View.VISIBLE);
            mission_infoButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getInfoURL()));
                    startActivity(i);
                    Timber.d("Wiki One!");
                }
            });
        } else {
            mission_infoButton_one.setVisibility(View.GONE);
        }
    }

    private void setTwoVehicleAgencies() {
        String countryCode = detailLaunch.getRocket().getAgencies().get(0).getCountryCode();
        String agency = getAgencyType(detailLaunch.getRocket().getAgencies().get(0).getType());
        if (countryCode.length() > 9){
            countryCode = countryCode.substring(0,2) + ", "+ countryCode.substring(4,6) + ", "+ countryCode.substring(7,9) + "...";
            agency = agency + " | Multinational";
        } else {
            countryCode = countryCode.substring(0,2);
        }
        String countryCodeTwo = detailLaunch.getRocket().getAgencies().get(1).getCountryCode();
        String agencyTwo = getAgencyType(detailLaunch.getRocket().getAgencies().get(1).getType());
        if (countryCodeTwo.length() > 9){
            countryCodeTwo = countryCodeTwo.substring(0,2) + ", "+ countryCodeTwo.substring(4,6) + ", "+ countryCodeTwo.substring(7,9) + "...";
            agencyTwo = agencyTwo + " | Multinational";
        } else {
            countryCodeTwo = countryCodeTwo.substring(0,2);
        }
        mission_one.setVisibility(View.VISIBLE);
        mission_two.setVisibility(View.VISIBLE);
        mission_agency_type.setVisibility(View.GONE);
        mission_agency_type_one.setText(String.format("Type: %s", agency));
        mission_agency_type_two.setText(String.format("Type: %s", agencyTwo));
        mission_agency_one.setText(String.format("%s (%s) %s", detailLaunch.getRocket().getAgencies().get(0).getName(), detailLaunch.getRocket().getAgencies().get(0).getAbbrev(), countryCode));
        mission_agency_two.setText(String.format("%s (%s) %s", detailLaunch.getRocket().getAgencies().get(1).getName(), detailLaunch.getRocket().getAgencies().get(1).getAbbrev(), countryCodeTwo));

        if (detailLaunch.getRocket().getAgencies().get(0).getInfoURL().length() > 0){
            mission_wikiButton_one.setVisibility(View.VISIBLE);
            mission_wikiButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getWikiURL()));
                    startActivity(i);
                }
            });
        } else {
            mission_wikiButton_one.setVisibility(View.GONE);
        }


        if (detailLaunch.getRocket().getAgencies().get(0).getInfoURL().length() > 0){
            mission_infoButton_one.setVisibility(View.VISIBLE);
            mission_infoButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getInfoURL()));
                    startActivity(i);
                }
            });
        } else {
            mission_infoButton_one.setVisibility(View.GONE);
        }

        if (detailLaunch.getRocket().getAgencies().get(1).getInfoURL().length() > 0){
            mission_wikiButton_two.setVisibility(View.VISIBLE);
            mission_wikiButton_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getRocket().getAgencies().get(1).getWikiURL()));
                    startActivity(i);
                }
            });
        } else {
            mission_wikiButton_two.setVisibility(View.GONE);
        }

        if (detailLaunch.getRocket().getAgencies().get(1).getInfoURL().length() > 1){
            mission_infoButton_two.setVisibility(View.VISIBLE);
            mission_infoButton_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(detailLaunch.getRocket().getAgencies().get(1).getInfoURL()));
                    startActivity(i);
                }
            });
        } else {
            mission_infoButton_two.setVisibility(View.GONE);
        }
    }

    private String getAgencyType(int type){
        String agency;
        switch (type){
            case 1:
                agency = "Government";
                break;
            case 2:
                agency = "Multinational";
                break;
            case 3:
                agency = "Commercial";
                break;
            case 4:
                agency = "Educational";
                break;
            case 5:
                agency = "Private";
                break;
            case 6:
                agency = "Unknown";
                break;
            default:
                agency = "Unknown";
        }
        return agency;
    }


    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static Fragment newInstance() {
        return new AgencyDetailFragment();
    }


}