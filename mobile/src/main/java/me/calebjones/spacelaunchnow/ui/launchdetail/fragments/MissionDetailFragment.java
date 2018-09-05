package me.calebjones.spacelaunchnow.ui.launchdetail.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.RetroFitFragment;
import me.calebjones.spacelaunchnow.content.events.LaunchEvent;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.LauncherConfig;
import me.calebjones.spacelaunchnow.data.models.main.Mission;
import me.calebjones.spacelaunchnow.ui.launches.launcher.LauncherLaunchActivity;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class MissionDetailFragment extends RetroFitFragment {

    private Context context;
    public Launch detailLaunch;
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
    @BindView(R.id.launch_vehicle_description)
    TextView launchVehicleDescription;
    @BindView(R.id.vehicle_infoButton)
    AppCompatButton vehicleInfoButton;
    @BindView(R.id.vehicle_wikiButton)
    AppCompatButton vehicleWikiButton;
    @BindView(R.id.launcher_launches)
    AppCompatButton launchesButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName("Mission Detail Fragment");
        // retain this fragment
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view;
        context = getContext();
        view = inflater.inflate(R.layout.detail_launch_payload, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        if (detailLaunch != null && detailLaunch.isValid()) {
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

            if (detailLaunch.getMission() != null) {
                final Mission mission = detailLaunch.getMission();

                payloadStatus.setText(mission.getName());
                payloadDescription.setText(mission.getDescription());
                payloadType.setText(mission.getTypeName());
                payloadInfoButton.setVisibility(View.GONE);
                payloadWikiButton.setVisibility(View.GONE);
            } else {
                payloadStatus.setText(R.string.unknown_mission_or_payload);

                payloadInfoButton.setVisibility(View.GONE);
                payloadWikiButton.setVisibility(View.GONE);
            }

            launchVehicleView.setText(detailLaunch.getLauncherConfig().getFullName());
            launchConfiguration.setText(detailLaunch.getLauncherConfig().getVariant());
            launchFamily.setText(detailLaunch.getLauncherConfig().getFamily());
            if (detailLaunch.getLauncherConfig().getInfoUrl() != null && detailLaunch.getLauncherConfig().getInfoUrl().length() > 0){
                vehicleInfoButton.setOnClickListener(view -> {
                    Activity activity = (Activity) context;
                    Utils.openCustomTab(activity, context, detailLaunch.getLauncherConfig().getInfoUrl());
                    Analytics.getInstance().sendButtonClickedWithURL("Vehicle Info",
                            detailLaunch.getLauncherConfig().getInfoUrl());
                });
            } else {
                vehicleInfoButton.setVisibility(View.GONE);
            }

            if (detailLaunch.getLauncherConfig().getWikiUrl() != null && detailLaunch.getLauncherConfig().getWikiUrl().length() > 0){
                vehicleWikiButton.setOnClickListener(view -> {
                    Activity activity = (Activity) context;
                    Utils.openCustomTab(activity, context, detailLaunch.getLauncherConfig().getWikiUrl());
                    Analytics.getInstance().sendButtonClickedWithURL("Vehicle Wiki",
                            detailLaunch.getLauncherConfig().getWikiUrl());
                });
            } else {
                vehicleWikiButton.setVisibility(View.GONE);
            }
            configureLaunchVehicle(launch.getLauncherConfig());
        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

    @SuppressLint("StringFormatMatches")
    private void configureLaunchVehicle(LauncherConfig launchVehicle) {
        if (launchVehicle != null) {
            vehicleSpecView.setVisibility(View.VISIBLE);
            try {
                if (launchVehicle.getLength() != null) {
                    launchVehicleSpecsHeight.setText(String.format(context.getString(R.string.height_full), launchVehicle.getLength()));
                } else {
                    launchVehicleSpecsHeight.setText(context.getString(R.string.height));
                }

                if (launchVehicle.getDiameter() != null) {
                    launchVehicleSpecsDiameter.setText(String.format(context.getString(R.string.diameter_full), launchVehicle.getDiameter()));
                } else {
                    launchVehicleSpecsDiameter.setText(context.getString(R.string.diameter));
                }

                if (launchVehicle.getMaxStage() != null) {
                    launchVehicleSpecsStages.setText(String.format(context.getString(R.string.stage_full), launchVehicle.getMaxStage()));
                } else {
                    launchVehicleSpecsStages.setText(context.getString(R.string.stages));
                }

                if (launchVehicle.getLeoCapacity() != null) {
                    launchVehicleSpecsLeo.setText(String.format(context.getString(R.string.mass_leo_full), launchVehicle.getLeoCapacity()));
                } else {
                    launchVehicleSpecsLeo.setText(context.getString(R.string.mass_to_leo));
                }

                if (launchVehicle.getGtoCapacity() != null) {
                    launchVehicleSpecsGto.setText(String.format(context.getString(R.string.mass_gto_full), launchVehicle.getGtoCapacity()));
                } else {
                    launchVehicleSpecsGto.setText(context.getString(R.string.mass_to_gto));
                }

                if (launchVehicle.getLaunchMass() != null) {
                    launchVehicleSpecsLaunchMass.setText(String.format(context.getString(R.string.mass_launch_full), launchVehicle.getLaunchMass()));
                } else {
                    launchVehicleSpecsLaunchMass.setText(context.getString(R.string.mass_at_launch));
                }

                if (launchVehicle.getDescription() != null && launchVehicle.getDescription().length() > 0) {
                    launchVehicleDescription.setText(launchVehicle.getDescription());
                    launchVehicleDescription.setVisibility(View.VISIBLE);
                } else {
                    launchVehicleDescription.setVisibility(View.GONE);
                }
                launchesButton.setText(String.format(getString(R.string.view_rocket_launches), launchVehicle.getName()));
                launchesButton.setOnClickListener(v -> {
                    Intent launches = new Intent(context, LauncherLaunchActivity.class);
                    launches.putExtra("launcherId", launchVehicle.getId());
                    launches.putExtra("launcherName", launchVehicle.getName());
                    context.startActivity(launches);
                });
            } catch (NullPointerException e) {
                Crashlytics.log(String.format("Error parsing launch vehicle %s", launchVehicle.getName()));
                Crashlytics.logException(e);
            }
        } else {
            vehicleSpecView.setVisibility(View.GONE);
        }
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
