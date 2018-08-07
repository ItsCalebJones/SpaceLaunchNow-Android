package me.calebjones.spacelaunchnow.ui.launchdetail.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
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
import me.calebjones.spacelaunchnow.content.data.DataSaver;
import me.calebjones.spacelaunchnow.content.events.LaunchEvent;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.Launcher;
import me.calebjones.spacelaunchnow.data.models.main.Mission;
import me.calebjones.spacelaunchnow.data.networking.interfaces.SpaceLaunchNowService;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MissionDetailFragment extends RetroFitFragment {

    private Context context;
    public Launch detailLaunch;
    private Launcher launchVehicle;
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
                payloadStatus.setText("Unknown Mission or Payload");

                payloadInfoButton.setVisibility(View.GONE);
                payloadWikiButton.setVisibility(View.GONE);
            }

            launchVehicleView.setText(detailLaunch.getLauncher().getName());
            launchConfiguration.setText(detailLaunch.getLauncher().getVariant());
            launchFamily.setText(detailLaunch.getLauncher().getFamily());
            if (detailLaunch.getLauncher().getInfoUrl() != null && detailLaunch.getLauncher().getInfoUrl().length() > 0){
                vehicleInfoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Activity activity = (Activity) context;
                        Utils.openCustomTab(activity, context, detailLaunch.getLauncher().getInfoUrl());
                        Analytics.getInstance().sendButtonClickedWithURL("Vehicle Info",
                                detailLaunch.getLauncher().getInfoUrl());
                    }
                });
            } else {
                vehicleInfoButton.setVisibility(View.GONE);
            }

            if (detailLaunch.getLauncher().getWikiUrl() != null && detailLaunch.getLauncher().getWikiUrl().length() > 0){
                vehicleWikiButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Activity activity = (Activity) context;
                        Utils.openCustomTab(activity, context, detailLaunch.getLauncher().getWikiUrl());
                        Analytics.getInstance().sendButtonClickedWithURL("Vehicle Wiki",
                                detailLaunch.getLauncher().getWikiUrl());
                    }
                });
            } else {
                vehicleWikiButton.setVisibility(View.GONE);
            }
            configureLaunchVehicle(launchVehicle);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

    private void configureLaunchVehicle(Launcher launchVehicle) {
        if (launchVehicle != null) {
            vehicleSpecView.setVisibility(View.VISIBLE);
            try {
                launchVehicleSpecsHeight.setText(String.format("Height: %s Meters", launchVehicle.getLength()));
                launchVehicleSpecsDiameter.setText(String.format("Diameter: %s Meters", launchVehicle.getDiameter()));
                launchVehicleSpecsStages.setText(String.format("Stages: %d", launchVehicle.getMaxStage()));
                launchVehicleSpecsLeo.setText(String.format("Payload to LEO: %s kg", launchVehicle.getLeoCapacity()));
                launchVehicleSpecsGto.setText(String.format("Payload to GTO: %s kg", launchVehicle.getGtoCapacity()));
                launchVehicleSpecsLaunchMass.setText(String.format("Mass at Launch: %s Tons", launchVehicle.getLaunchMass()));
                launchVehicleSpecsThrust.setText(String.format("Thrust at Launch: %s kN", launchVehicle.getToThrust()));
                if (launchVehicle.getDescription() != null && launchVehicle.getDescription().length() > 0) {
                    launchVehicleDescription.setText(launchVehicle.getDescription());
                    launchVehicleDescription.setVisibility(View.VISIBLE);
                } else {
                    launchVehicleDescription.setVisibility(View.GONE);
                }
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
