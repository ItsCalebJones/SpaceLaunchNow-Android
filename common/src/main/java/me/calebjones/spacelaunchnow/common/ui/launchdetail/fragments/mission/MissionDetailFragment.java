package me.calebjones.spacelaunchnow.common.ui.launchdetail.fragments.mission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.constraintlayout.widget.Guideline;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.common.base.RetroFitFragment;
import me.calebjones.spacelaunchnow.common.ui.adapters.CrewAdapter;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.DetailsViewModel;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.launches.launcher.LauncherLaunchActivity;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.Mission;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherConfig;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;
import timber.log.Timber;

public class MissionDetailFragment extends RetroFitFragment {

    @BindView(R2.id.coreRecyclerView)
    RecyclerView coreRecyclerView;
    @BindView(R2.id.payload_type)
    TextView payloadType;
    @BindView(R2.id.payload_description)
    TextView payloadDescription;
    @BindView(R2.id.orbit)
    TextView orbit;
    @BindView(R2.id.payload_status)
    TextView payloadStatus;
    @BindView(R2.id.payload_infoButton)
    TextView payloadInfoButton;
    @BindView(R2.id.payload_wikiButton)
    TextView payloadWikiButton;
    @BindView(R2.id.launch_vehicle)
    TextView launchVehicleView;
    @BindView(R2.id.launch_configuration)
    TextView launchConfiguration;
    @BindView(R2.id.launch_family)
    TextView launchFamily;
    @BindView(R2.id.maiden_value)
    TextView maidenLaunch;

    @BindView(R2.id.launch_vehicle_specs_height_value)
    TextView launchVehicleSpecsHeight;
    @BindView(R2.id.launch_vehicle_specs_diameter_value)
    TextView launchVehicleSpecsDiameter;
    @BindView(R2.id.launch_vehicle_specs_stages_value)
    TextView launchVehicleSpecsStages;
    @BindView(R2.id.launch_vehicle_specs_leo_value)
    TextView launchVehicleSpecsLeo;
    @BindView(R2.id.launch_vehicle_specs_gto_value)
    TextView launchVehicleSpecsGto;
    @BindView(R2.id.launch_vehicle_specs_launch_mass_value)
    TextView launchVehicleSpecsLaunchMass;
    @BindView(R2.id.launch_vehicle_specs_thrust_value)
    TextView launchVehicleSpecsThrust;


    @BindView(R2.id.launch_success_value)
    TextView launchSuccess;
    @BindView(R2.id.consecutive_success_value)
    TextView consecutiveSuccess;
    @BindView(R2.id.launch_total_value)
    TextView launchTotal;
    @BindView(R2.id.launch_failure_value)
    TextView launchFailure;


    @BindView(R2.id.launch_vehicle_description)
    TextView launchVehicleDescription;
    @BindView(R2.id.vehicle_infoButton)
    Button vehicleInfoButton;
    @BindView(R2.id.vehicle_wikiButton)
    Button vehicleWikiButton;
    @BindView(R2.id.flightclub_infoButton)
    Button flightclubButton;
    @BindView(R2.id.launcher_launches)
    Button launchesButton;
    @BindView(R2.id.spacecraft_image)
    ImageView spacecraftImage;
    @BindView(R2.id.spacecraft_title)
    TextView spacecraftTitle;
    @BindView(R2.id.spacecraft_sub_title)
    TextView spacecraftSubTitle;
    @BindView(R2.id.spacecraft_guideline)
    Guideline spacecraftGuideline;
    @BindView(R2.id.destination_text)
    TextView destinationText;
    @BindView(R2.id.serial_number_text)
    TextView serialNumberText;
    @BindView(R2.id.status_text)
    TextView statusText;
    @BindView(R2.id.description)
    TextView description;
    @BindView(R2.id.spacecraft_card)
    CardView spacecraftCard;
    @BindView(R2.id.crew_recycler_view)
    RecyclerView crewReycler;
    @BindView(R2.id.mission_patch)
    ImageView missionPatch;

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
                if (mission.getOrbit() != null && mission.getOrbit().getAbbrev() != null) {
                    orbit.setVisibility(View.VISIBLE);
                    orbit.setText(String.format("%s (%s)", mission.getOrbit().getName(), mission.getOrbit().getAbbrev()));
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

            if(detailLaunch.getFlightclub() != null){
                flightclubButton.setVisibility(View.VISIBLE);
                flightclubButton.setOnClickListener(v -> {
                    Activity activity = (Activity) context;
                    Utils.openCustomTab(activity, context, detailLaunch.getFlightclub());
                });
            } else {
                flightclubButton.setVisibility(View.GONE);
            }


            launchVehicleView.setText(detailLaunch.getRocket().getConfiguration().getFullName());
            launchConfiguration.setText(detailLaunch.getRocket().getConfiguration().getVariant());
            launchFamily.setText(detailLaunch.getRocket().getConfiguration().getFamily());
            if (detailLaunch.getRocket().getConfiguration().getInfoUrl() != null && detailLaunch.getRocket().getConfiguration().getInfoUrl().length() > 0) {
                vehicleInfoButton.setOnClickListener(view -> {
                    Activity activity = (Activity) context;
                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getConfiguration().getInfoUrl());
                });
            } else {
                vehicleInfoButton.setVisibility(View.GONE);
            }

            if (detailLaunch.getPatches() != null && detailLaunch.getPatches().size() > 0){
                missionPatch.setVisibility(View.VISIBLE);
                GlideApp.with(context)
                        .load(detailLaunch.getPatches().get(0).getImageUrl())
                        .centerInside()
                        .into(missionPatch);
            } else {
                missionPatch.setVisibility(View.VISIBLE);
            }

            if (detailLaunch.getRocket().getConfiguration().getWikiUrl() != null && detailLaunch.getRocket().getConfiguration().getWikiUrl().length() > 0) {
                vehicleWikiButton.setOnClickListener(view -> {
                    Activity activity = (Activity) context;
                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getConfiguration().getWikiUrl());
                });
            } else {
                vehicleWikiButton.setVisibility(View.GONE);
            }
            configureLaunchVehicle(launch.getRocket().getConfiguration());

            if (launch.getRocket().getLauncherStage() != null && launch.getRocket().getLauncherStage().size() > 0) {
                coreRecyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                coreRecyclerView.setLayoutManager(layoutManager);
                StageInformationAdapter stageInformationAdapter = new StageInformationAdapter(launch, context);
                coreRecyclerView.setAdapter(stageInformationAdapter);
                coreRecyclerView.setHasFixedSize(true);
            } else {
                coreRecyclerView.setVisibility(View.GONE);
            }

            if (launch.getRocket().getSpacecraftStage() != null) {
                spacecraftCard.setVisibility(View.VISIBLE);
                SpacecraftStage stage = launch.getRocket().getSpacecraftStage();
                GlideApp.with(context)
                        .load(stage.getSpacecraft().getConfiguration().getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .centerCrop()
                        .into(spacecraftImage);
                spacecraftTitle.setText(stage.getSpacecraft().getConfiguration().getName());
                spacecraftSubTitle.setText(stage.getSpacecraft().getConfiguration().getAgency().getName());
                destinationText.setText(stage.getDestination());
                serialNumberText.setText(stage.getSpacecraft().getSerialNumber());
                statusText.setText(stage.getSpacecraft().getStatus().getName());
                description.setText(stage.getSpacecraft().getDescription());
                if (launch.getRocket().getSpacecraftStage().getLaunchCrew() != null
                        && launch.getRocket().getSpacecraftStage().getLaunchCrew().size() > 0) {
                    crewReycler.setLayoutManager(new LinearLayoutManager(context));
                    crewReycler.setAdapter(new CrewAdapter(context,
                            launch.getRocket().getSpacecraftStage().getLaunchCrew()));
                }
            } else {
                spacecraftCard.setVisibility(View.GONE);
            }

        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

    private void configureLaunchVehicle(LauncherConfig launchVehicle) {
        if (launchVehicle != null) {
            try {
                if (launchVehicle.getLength() != null) {
                    launchVehicleSpecsHeight.setText(String.format(context.getString(R.string.height_value), launchVehicle.getLength()));
                } else {
                    launchVehicleSpecsHeight.setText(" - ");
                }

                if (launchVehicle.getDiameter() != null) {
                    launchVehicleSpecsDiameter.setText(String.format(context.getString(R.string.diameter_value), launchVehicle.getDiameter()));
                } else {
                    launchVehicleSpecsDiameter.setText(" - ");
                }

                if (launchVehicle.getMaxStage() != null) {
                    launchVehicleSpecsStages.setText(String.format(context.getString(R.string.stage_value), launchVehicle.getMaxStage()));
                } else {
                    launchVehicleSpecsStages.setText(" - ");
                }

                if (launchVehicle.getLeoCapacity() != null) {
                    launchVehicleSpecsLeo.setText(String.format(context.getString(R.string.mass_leo_value), launchVehicle.getLeoCapacity()));
                } else {
                    launchVehicleSpecsLeo.setText(" - ");
                }

                if (launchVehicle.getGtoCapacity() != null) {
                    launchVehicleSpecsGto.setText(String.format(context.getString(R.string.mass_gto_value), launchVehicle.getGtoCapacity()));
                } else {
                    launchVehicleSpecsGto.setText(" - ");
                }

                if (launchVehicle.getLaunchMass() != null) {
                    launchVehicleSpecsLaunchMass.setText(String.format(context.getString(R.string.mass_launch_value), launchVehicle.getLaunchMass()));
                } else {
                    launchVehicleSpecsLaunchMass.setText(" - ");
                }

                if (launchVehicle.getToThrust() != null) {
                    launchVehicleSpecsThrust.setText(String.format(context.getString(R.string.thrust_value), launchVehicle.getToThrust()));
                } else {
                    launchVehicleSpecsThrust.setText(" - ");
                }

                if (launchVehicle.getConsecutiveSuccessfulLaunches() != null) {
                    consecutiveSuccess.setText(String.valueOf(launchVehicle.getConsecutiveSuccessfulLaunches()));
                } else {
                    consecutiveSuccess.setText(" - ");
                }

                if (launchVehicle.getSuccessfulLaunches() != null) {
                    launchSuccess.setText(String.valueOf(launchVehicle.getSuccessfulLaunches()));
                } else {
                    launchSuccess.setText(" - ");
                }

                if (launchVehicle.getTotalLaunchCount() != null) {
                    launchTotal.setText(String.valueOf(launchVehicle.getTotalLaunchCount()));
                } else {
                    launchTotal.setText(" - ");
                }

                if (launchVehicle.getFailedLaunches() != null) {
                    launchFailure.setText(String.valueOf(launchVehicle.getFailedLaunches()));
                } else {
                    launchFailure.setText(" - ");
                }

                if (launchVehicle.getMaidenFlight() != null){
                    //Setup SimpleDateFormat to parse out getNet launchDate.
                    SimpleDateFormat output = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy");
                    maidenLaunch.setText(output.format(launchVehicle.getMaidenFlight()));
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
                Timber.e(e);
            }
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



}
