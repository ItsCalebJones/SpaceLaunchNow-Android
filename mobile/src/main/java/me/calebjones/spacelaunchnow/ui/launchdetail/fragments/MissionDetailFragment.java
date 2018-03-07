package me.calebjones.spacelaunchnow.ui.launchdetail.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import me.calebjones.spacelaunchnow.common.BaseFragment;
import me.calebjones.spacelaunchnow.common.RetroFitFragment;
import me.calebjones.spacelaunchnow.content.data.DataSaver;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.events.LaunchEvent;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Mission;
import me.calebjones.spacelaunchnow.data.models.spacelaunchnow.RocketDetail;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.interfaces.SpaceLaunchNowService;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LauncherResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MissionDetailFragment extends RetroFitFragment {

    private Context context;
    public Launch detailLaunch;
    private RocketDetail launchVehicle;
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
            launchConfiguration.setText(detailLaunch.getRocket().getConfiguration());
            launchFamily.setText(detailLaunch.getRocket().getFamilyname());
            if (detailLaunch.getRocket().getInfoURL() != null && detailLaunch.getRocket().getInfoURL().length() > 0){
                vehicleInfoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Activity activity = (Activity) context;
                        Utils.openCustomTab(activity, context, detailLaunch.getRocket().getInfoURL());
                        Analytics.from(getActivity()).sendButtonClickedWithURL("Vehicle Info",
                                detailLaunch.getRocket().getInfoURL());
                    }
                });
            } else {
                vehicleInfoButton.setVisibility(View.GONE);
            }

            if (detailLaunch.getRocket().getWikiURL() != null && detailLaunch.getRocket().getWikiURL().length() > 0){
                vehicleWikiButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Activity activity = (Activity) context;
                        Utils.openCustomTab(activity, context, detailLaunch.getRocket().getWikiURL());
                        Analytics.from(getActivity()).sendButtonClickedWithURL("Vehicle Wiki",
                                detailLaunch.getRocket().getWikiURL());
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

    private void configureLaunchVehicle(RocketDetail launchVehicle) {
        if (launchVehicle != null) {
            vehicleSpecView.setVisibility(View.VISIBLE);
            try {
                launchVehicleSpecsHeight.setText(String.format("Height: %s Meters", launchVehicle.getLength()));
                launchVehicleSpecsDiameter.setText(String.format("Diameter: %s Meters", launchVehicle.getDiameter()));
                launchVehicleSpecsStages.setText(String.format("Stages: %d", launchVehicle.getMaxStage()));
                launchVehicleSpecsLeo.setText(String.format("Payload to LEO: %s kg", launchVehicle.getLEOCapacity()));
                launchVehicleSpecsGto.setText(String.format("Payload to GTO: %s kg", launchVehicle.getGTOCapacity()));
                launchVehicleSpecsLaunchMass.setText(String.format("Mass at Launch: %s Tons", launchVehicle.getLaunchMass()));
                launchVehicleSpecsThrust.setText(String.format("Thrust at Launch: %s kN", launchVehicle.getTOThrust()));
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

    private void getLaunchVehicle(Launch vehicle) {
        final String query = vehicle.getRocket().getName();

        SpaceLaunchNowService request = getSpaceLaunchNowRetrofit().create(SpaceLaunchNowService.class);
        Call<VehicleResponse> call = request.getVehicle(query);
        call.enqueue(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                if (response.isSuccessful()) {
                    RocketDetail[] details = response.body().getVehicles();
                    DataSaver dataSaver = new DataSaver(context);
                    dataSaver.saveObjectsToRealm(details);
                    if (details.length > 0) {
                        launchVehicle = details[0];
                        configureLaunchVehicle(launchVehicle);
                    }
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES_DETAIL, true, call));
                } else {
                    launchVehicle = getRealm().where(RocketDetail.class).contains("fullName", query).findFirst();
                }
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                Crashlytics.logException(t);
                if (!getRealm().isClosed()) {
                    launchVehicle = getRealm().where(RocketDetail.class).contains("fullName", query).findFirst();
                }
            }
        });
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
