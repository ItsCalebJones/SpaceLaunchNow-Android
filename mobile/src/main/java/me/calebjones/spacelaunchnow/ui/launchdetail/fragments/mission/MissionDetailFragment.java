package me.calebjones.spacelaunchnow.ui.launchdetail.fragments.mission;

import android.app.Activity;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.local.common.RetroFitFragment;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherConfig;
import me.calebjones.spacelaunchnow.data.models.main.Mission;
import me.calebjones.spacelaunchnow.ui.launchdetail.DetailsViewModel;
import me.calebjones.spacelaunchnow.ui.launches.launcher.LauncherLaunchActivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import timber.log.Timber;

public class MissionDetailFragment extends RetroFitFragment {

    @BindView(R.id.coreRecyclerView)
    RecyclerView coreRecyclerView;
    @BindView(R.id.vehicle_spec_view)
    Group vehicleSpecView;
    @BindView(R.id.payload_type)
    TextView payloadType;
    @BindView(R.id.payload_description)
    TextView payloadDescription;
    @BindView(R.id.orbit)
    TextView orbit;
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

    private Context context;
    public Launch detailLaunch;
    private Unbinder unbinder;
    private DetailsViewModel model;

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

        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.v("onDestroyView");
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setLaunch(Launch launch) {
        Timber.v("Launch update received: %s", launch.getName());
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
                if (mission.getOrbit() != null && mission.getOrbitAbbrev() != null) {
                    orbit.setVisibility(View.VISIBLE);
                    orbit.setText(String.format("%s (%s)", mission.getOrbit(), mission.getOrbitAbbrev()));
                } else {
                    orbit.setVisibility(View.GONE);
                }
                payloadInfoButton.setVisibility(View.GONE);
                payloadWikiButton.setVisibility(View.GONE);
            } else {
                payloadStatus.setText(R.string.unknown_mission_or_payload);

                payloadInfoButton.setVisibility(View.GONE);
                payloadWikiButton.setVisibility(View.GONE);
            }

            launchVehicleView.setText(detailLaunch.getRocket().getConfiguration().getFullName());
            launchConfiguration.setText(detailLaunch.getRocket().getConfiguration().getVariant());
            launchFamily.setText(detailLaunch.getRocket().getConfiguration().getFamily());
            if (detailLaunch.getRocket().getConfiguration().getInfoUrl() != null && detailLaunch.getRocket().getConfiguration().getInfoUrl().length() > 0) {
                vehicleInfoButton.setOnClickListener(view -> {
                    Activity activity = (Activity) context;
                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getConfiguration().getInfoUrl());
                    Analytics.getInstance().sendButtonClickedWithURL("Vehicle Info",
                            detailLaunch.getRocket().getConfiguration().getInfoUrl());
                });
            } else {
                vehicleInfoButton.setVisibility(View.GONE);
            }

            if (detailLaunch.getRocket().getConfiguration().getWikiUrl() != null && detailLaunch.getRocket().getConfiguration().getWikiUrl().length() > 0) {
                vehicleWikiButton.setOnClickListener(view -> {
                    Activity activity = (Activity) context;
                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getConfiguration().getWikiUrl());
                    Analytics.getInstance().sendButtonClickedWithURL("Vehicle Wiki",
                            detailLaunch.getRocket().getConfiguration().getWikiUrl());
                });
            } else {
                vehicleWikiButton.setVisibility(View.GONE);
            }
            configureLaunchVehicle(launch.getRocket().getConfiguration());

            if (launch.getRocket().getLauncherStage() != null && launch.getRocket().getLauncherStage().size() > 0){
                coreRecyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                coreRecyclerView.setLayoutManager(layoutManager);
                StageInformationAdapter stageInformationAdapter = new StageInformationAdapter(launch, context);
                coreRecyclerView.setAdapter(stageInformationAdapter);
                coreRecyclerView.setHasFixedSize(true);
            } else {
                coreRecyclerView.setVisibility(View.GONE);
            }


        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

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
        model = ViewModelProviders.of(getActivity()).get(DetailsViewModel.class);
        // update UI
        model.getLaunch().observe(this, this::setLaunch);
    }

    public static MissionDetailFragment newInstance() {
        return new MissionDetailFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
