package me.calebjones.spacelaunchnow.ui.launchdetail.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseFragment;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.events.LaunchEvent;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.data.models.Mission;
import me.calebjones.spacelaunchnow.data.models.RocketDetails;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class MissionDetailFragment extends BaseFragment {

    private SharedPreferences sharedPref;
    private static ListPreferences sharedPreference;
    private Context context;
    public static Launch detailLaunch;
    private RocketDetails launchVehicle;
    @BindView(R.id.vehicle_spec_view)
    View vehicleSpecView;
    @BindView(R.id.payload_type)
    TextView payloadType;
    @BindView(R.id.payload_description)
    TextView payloadDescription;
    @BindView(R.id.payload_status)
    TextView payloadStatus;
    @BindView(R.id.payload_infoButton)
    TextView payloadInfoButton;
    @BindView(R.id.payload_wikiButton)
    TextView payloadWikiButton;
    @BindView(R.id.launch_vehicle)
    TextView launchVehicleView;
    @BindView(R.id.launch_configuration)
    TextView launchConfiguration;
    @BindView(R.id.launch_family)
    TextView launchFamily;
    @BindView(R.id.launch_vehicle_specs_height)
    TextView launchVehicleSpecsHeight;
    @BindView(R.id.launch_vehicle_specs_diameter)
    TextView launchVehicleSpecsDiameter;
    @BindView(R.id.launch_vehicle_specs_stages)
    TextView launchVehicleSpecsStages;
    @BindView(R.id.launch_vehicle_specs_leo)
    TextView launchVehicleSpecsLeo;
    @BindView(R.id.launch_vehicle_specs_gto)
    TextView launchVehicleSpecsGto;
    @BindView(R.id.launch_vehicle_specs_launch_mass)
    TextView launchVehicleSpecsLaunchMass;
    @BindView(R.id.launch_vehicle_specs_thrust)
    TextView launchVehicleSpecsThrust;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName("Mission Detail Fragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        this.context = getContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        view = inflater.inflate(R.layout.detail_launch_payload, container, false);

        detailLaunch = ((LaunchDetailActivity) getActivity()).getLaunch();

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        if (detailLaunch != null) {
            setUpViews(detailLaunch);
        }
        super.onResume();
    }

    public void setLaunch(Launch launch) {
        detailLaunch = launch;
        setUpViews(launch);
    }

    private void setUpViews(Launch launch) {
        try {
            detailLaunch = launch;
            if (detailLaunch.getRocket() != null) {
                getLaunchVehicle(detailLaunch);
            }

            if (detailLaunch.getMissions().size() > 0) {
                final Mission mission = getRealm().where(Mission.class)
                        .equalTo("id", detailLaunch.getMissions().get(0).getId())
                        .findFirst();

                payloadStatus.setText(mission.getName());
                payloadDescription.setText(mission.getDescription());
                payloadType.setText(mission.getTypeName());

                if (mission.getInfoURL() != null && mission.getInfoURL().length() > 0) {

                    ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(mission.getInfoURL()));

                    payloadInfoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Activity activity = (Activity) context;
                            Utils.openCustomTab(activity, context, mission.getInfoURL());
                            Analytics.from(getActivity()).sendButtonClickedWithURL(
                                    "Mission Info",
                                    detailLaunch.getName(),
                                    mission.getInfoURL()
                            );
                        }
                    });
                } else {
                    payloadInfoButton.setVisibility(View.GONE);
                }
                if (mission.getWikiURL() != null && mission.getWikiURL().length() > 0) {

                    ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(mission.getWikiURL()));

                    payloadWikiButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Activity activity = (Activity) context;
                            Utils.openCustomTab(activity, context, mission.getWikiURL());
                            Analytics.from(getActivity()).sendButtonClickedWithURL(
                                    "Mission Wiki",
                                    detailLaunch.getName(),
                                    mission.getWikiURL()
                            );
                        }
                    });
                } else {
                    payloadWikiButton.setVisibility(View.GONE);
                }
            } else {
                payloadStatus.setText("Unknown Mission or Payload");

                payloadInfoButton.setVisibility(View.GONE);
                payloadWikiButton.setVisibility(View.GONE);
            }

            launchVehicleView.setText(detailLaunch.getRocket().getName());
            launchConfiguration.setText(String.format("Configuration: %s", detailLaunch.getRocket().getConfiguration()));
            launchFamily.setText(String.format("Family: %s", detailLaunch.getRocket().getFamilyname()));
            if (launchVehicle != null) {
                vehicleSpecView.setVisibility(View.VISIBLE);
                launchVehicleSpecsHeight.setText(String.format("Height: %s Meters", launchVehicle.getLength()));
                launchVehicleSpecsDiameter.setText(String.format("Diameter: %s Meters", launchVehicle.getDiameter()));
                launchVehicleSpecsStages.setText(String.format("Stages: %d", launchVehicle.getMax_Stage()));
                launchVehicleSpecsLeo.setText(String.format("Payload to LEO: %s kg", launchVehicle.getLEOCapacity()));
                launchVehicleSpecsGto.setText(String.format("Payload to GTO: %s kg", launchVehicle.getGTOCapacity()));
                launchVehicleSpecsLaunchMass.setText(String.format("Mass at Launch: %s Tons", launchVehicle.getLaunchMass()));
                launchVehicleSpecsThrust.setText(String.format("Thrust at Launch: %s kN", launchVehicle.getTOThrust()));
            } else {
                vehicleSpecView.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

    private void getLaunchVehicle(Launch vehicle) {
        String query;
        if (vehicle.getRocket().getName().contains("Space Shuttle")) {
            query = "Space Shuttle";
        } else {
            query = vehicle.getRocket().getName();
        }

        launchVehicle = getRealm().where(RocketDetails.class).contains("name", query).findFirst();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static MissionDetailFragment newInstance() {
        return new MissionDetailFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LaunchEvent event) {
        setLaunch(event.launch);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
