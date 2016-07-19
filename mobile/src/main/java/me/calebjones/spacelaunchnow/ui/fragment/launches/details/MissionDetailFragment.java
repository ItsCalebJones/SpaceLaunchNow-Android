package me.calebjones.spacelaunchnow.ui.fragment.launches.details;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.pwittchen.weathericonview.WeatherIconView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.content.models.realm.MissionRealm;
import me.calebjones.spacelaunchnow.content.models.realm.RocketDetailsRealm;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.ui.fragment.BaseFragment;
import me.calebjones.spacelaunchnow.utils.Utils;

public class MissionDetailFragment extends BaseFragment {

    private SharedPreferences sharedPref;
    private static ListPreferences sharedPreference;
    private Context context;
    public static LaunchRealm detailLaunch;
    private RocketDetailsRealm launchVehicle;
    @BindView(R.id.vehicle_spec_view)
    View vehicle_spec_view;
    @BindView(R.id.payload_description)
    TextView payload_description;
    @BindView(R.id.payload_status)
    TextView payload_status;
    @BindView(R.id.payload_infoButton)
    TextView payload_infoButton;
    @BindView(R.id.payload_wikiButton)
    TextView payload_wikiButton;
    @BindView(R.id.launch_vehicle)
    TextView launch_vehicle;
    @BindView(R.id.launch_configuration)
    TextView launch_configuration;
    @BindView(R.id.launch_family)
    TextView launch_family;
    @BindView(R.id.launch_vehicle_specs_height)
    TextView launch_vehicle_specs_height;
    @BindView(R.id.launch_vehicle_specs_diameter)
    TextView launch_vehicle_specs_diameter;
    @BindView(R.id.launch_vehicle_specs_stages)
    TextView launch_vehicle_specs_stages;
    @BindView(R.id.launch_vehicle_specs_leo)
    TextView launch_vehicle_specs_leo;
    @BindView(R.id.launch_vehicle_specs_gto)
    TextView launch_vehicle_specs_gto;
    @BindView(R.id.launch_vehicle_specs_launch_mass)
    TextView launch_vehicle_specs_launch_mass;
    @BindView(R.id.launch_vehicle_specs_thrust)
    TextView launch_vehicle_specs_thrust;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        this.context = getContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        if (sharedPreference.getNightMode()) {
            view = inflater.inflate(R.layout.dark_launch_payload, container, false);
        } else {
            view = inflater.inflate(R.layout.light_launch_payload, container, false);
        }

        detailLaunch = ((LaunchDetailActivity) getActivity()).getLaunch();

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {

        setUpViews();
        super.onResume();
    }

    private void setUpViews() {
        detailLaunch = ((LaunchDetailActivity) getActivity()).getLaunch();
        if (detailLaunch.getRocket() != null) {
            getLaunchVehicle(detailLaunch);
        }

        if (detailLaunch.getMissions().size() > 0) {
            final MissionRealm mission = getRealm().where(MissionRealm.class)
                    .equalTo("id", detailLaunch.getMissions().get(0).getId())
                    .findFirst();

            payload_status.setText(mission.getName());
            payload_description.setText(mission.getDescription());

            if (mission.getInfoURL() != null && mission.getInfoURL().length() > 0) {

                ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(mission.getInfoURL()));

                payload_infoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Activity activity = (Activity) context;
                        Utils.openCustomTab(activity, context, mission.getInfoURL());
                    }
                });
            } else {
                payload_infoButton.setVisibility(View.GONE);
            }
            if (mission.getWikiURL() != null && mission.getWikiURL().length() > 0) {

                ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(mission.getWikiURL()));

                payload_wikiButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Activity activity = (Activity) context;
                        Utils.openCustomTab(activity, context, mission.getWikiURL());
                    }
                });
            } else {
                payload_wikiButton.setVisibility(View.GONE);
            }
        } else {
            payload_status.setText("Unknown Mission and Payload");

            payload_infoButton.setVisibility(View.GONE);
            payload_wikiButton.setVisibility(View.GONE);
        }

        launch_vehicle.setText(detailLaunch.getRocket().getName());
        launch_configuration.setText(String.format("Configuration: %s", detailLaunch.getRocket().getConfiguration()));
        launch_family.setText(String.format("Family: %s", detailLaunch.getRocket().getFamilyname()));
        if (launchVehicle != null) {
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
    }

    private void getLaunchVehicle(LaunchRealm vehicle) {
        String query;
        if (vehicle.getRocket().getName().contains("Space Shuttle")) {
            query = "Space Shuttle";
        } else {
            query = vehicle.getRocket().getName();
        }

        launchVehicle = getRealm().where(RocketDetailsRealm.class).contains("name", query).findFirst();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static Fragment newInstance() {
        return new MissionDetailFragment();
    }

}