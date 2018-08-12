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
import android.widget.LinearLayout;
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
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class AgencyDetailFragment extends BaseFragment {

    private SharedPreferences sharedPref;
    private static ListPreferences sharedPreference;
    private Context context;

    public static Launch detailLaunch;

    @BindView(R.id.mission_one)
    LinearLayout mission_one;
    @BindView(R.id.mission_two)
    LinearLayout mission_two;
    @BindView(R.id.agency_one)
    LinearLayout launch_one;
    @BindView(R.id.agency_two)
    LinearLayout launch_two;
    @BindView(R.id.mission_agency_type)
    TextView mission_agency_type;
    @BindView(R.id.mission_vehicle_agency_one)
    TextView mission_agency_one;
    @BindView(R.id.mission_agency_type_one)
    TextView mission_agency_type_one;
    @BindView(R.id.mission_infoButton_one)
    TextView mission_infoButton_one;
    @BindView(R.id.mission_wikiButton_one)
    TextView mission_wikiButton_one;
    @BindView(R.id.mission_vehicle_agency_two)
    TextView mission_agency_two;
    @BindView(R.id.mission_agency_type_two)
    TextView mission_agency_type_two;
    @BindView(R.id.mission_infoButton_two)
    TextView mission_infoButton_two;
    @BindView(R.id.mission_wikiButton_two)
    TextView mission_wikiButton_two;
    @BindView(R.id.launch_agency_type)
    TextView launch_agency_type;
    @BindView(R.id.launch_vehicle_agency_one)
    TextView launch_vehicle_agency_one;
    @BindView(R.id.launch_agency_type_one)
    TextView launch_agency_type_one;
    @BindView(R.id.infoButton_one)
    TextView infoButton_one;
    @BindView(R.id.wikiButton_one)
    TextView wikiButton_one;
    @BindView(R.id.launch_vehicle_agency_two)
    TextView launch_vehicle_agency_two;
    @BindView(R.id.launch_agency_type_two)
    TextView launch_agency_type_two;
    @BindView(R.id.infoButton_two)
    TextView infoButton_two;
    @BindView(R.id.wikiButton_two)
    TextView wikiButton_two;
    @BindView(R.id.launch_agency_summary_one)
    TextView launch_agency_summary_one;
    @BindView(R.id.launch_agency_summary_two)
    TextView launch_agency_summary_two;
    @BindView(R.id.mission_agency_summary_one)
    TextView mission_agency_summary_one;
    @BindView(R.id.mission_agency_summary_two)
    TextView mission_agency_summary_two;
    @BindView(R.id.mission_agency_title)
    TextView mission_agency_title;
    @BindView(R.id.vehicle_agency_title)
    TextView vehicle_agency_title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName("Agency Detail Fragment");
        // retain this fragment
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.context = getContext();
        setScreenName("LauncherAgency Detail Fragment");

        sharedPreference = ListPreferences.getInstance(this.context);

        view = inflater.inflate(R.layout.detail_launch_agency, container, false);

        ButterKnife.bind(this, view);

        Timber.v("Creating views...");

        if (detailLaunch != null && detailLaunch.isValid()) {
            setUpViews(detailLaunch);
        }

        return view;
    }

    @Override
    public void onResume() {
        if (detailLaunch != null && detailLaunch.isValid()) {
            setUpViews(detailLaunch);
        }
        super.onResume();
    }

    private void setLaunch(Launch launch) {
        detailLaunch = launch;
        setUpViews(launch);
    }

    // TODO redo for payloads
    private void setUpViews(Launch launch) {
        try {
            detailLaunch = launch;

            Timber.v("Setting up views...");

//            int pads = 0;
//            int mission_agencies = 0;
//            int vehicle_agencies = 0;
//            if (detailLaunch.getLocation() != null
//                    && detailLaunch.getPad() !=null) {
//                pads = detailLaunch.getPad().size();
//            }
//            if (pads > 0 && detailLaunch.getLocation() != null
//                    && detailLaunch.getPad() !=null) {
//                mission_agencies = detailLaunch.getLsp().si;
//            }
//            if (detailLaunch.getLauncher() != null && detailLaunch.getRocket().getAgencies() != null) {
//                vehicle_agencies = detailLaunch.getRocket().getAgencies().size();
//            }
//
//            if (detailLaunch.getMissions() !=null && detailLaunch.getMissions().size() > 0){
//                mission_agency_type.setText(detailLaunch.getMissions().get(0).getName());
//            } else {
//                mission_agency_type.setText(R.string.unknown_mission);
//            }
//
//            if (mission_agencies >= 2) {
//                setTwoMissionAgencies();
//            } else if (mission_agencies == 1) {
//                setOneMissionAgencies();
//            } else {
//                setNoMissionAgencies();
//            }
//
//            if (vehicle_agencies >= 2) {
//                setTwoVehicleAgencies();
//            } else if (vehicle_agencies == 1) {
//                setOneVehicleAgencies();
//            } else {
//                setNoVehicleAgencies();
//            }
        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

//    private void setNoMissionAgencies() {
//        mission_agency_type.setVisibility(View.VISIBLE);
//        mission_one.setVisibility(View.GONE);
//        mission_two.setVisibility(View.GONE);
//
//        mission_agency_title.setText(R.string.mission_agency);
//        mission_agency_type.setText(R.string.unknown_mission);
//    }
//
//    private void setOneMissionAgencies() {
//        mission_agency_type.setVisibility(View.VISIBLE);
//        String agencyTypeOne = "";
//        String agencyNameOne = "";
//        String agencyAbbrevOne = "";
//        try {
//            agencyTypeOne = getAgencyType(detailLaunch.getPad().get(0).getAgencies().get(0).getType());
//            agencyNameOne = detailLaunch.getPad().get(0).getAgencies().get(0).getName();
//            agencyAbbrevOne = detailLaunch.getPad().get(0).getAgencies().get(0).getAbbrev();
//        } catch (NullPointerException e){
//            Timber.e(e);
//        }
//
//        checkLaunchSummary(agencyAbbrevOne, mission_agency_summary_one);
//
//        mission_one.setVisibility(View.VISIBLE);
//        mission_two.setVisibility(View.GONE);
//        mission_agency_one.setText(String.format("%s (%s)", agencyNameOne, agencyAbbrevOne));
//        mission_agency_type_one.setText("Type: " + agencyTypeOne);
//
//        if (detailLaunch.getPad().get(0).getAgencies().get(0).getWikiURL() != null  &&
//                detailLaunch.getPad().get(0).getAgencies().get(0).getWikiURL().length() > 0) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getPad().get(0).getAgencies().get(0).getWikiURL()));
//
//            mission_wikiButton_one.setVisibility(View.VISIBLE);
//            mission_wikiButton_one.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getPad().get(0).getAgencies().get(0).getWikiURL());
//                    Analytics.getInstance().sendButtonClickedWithURL("Mission Wiki",
//                            detailLaunch.getName(),
//                            detailLaunch.getLocation()
//                                    .getPads()
//                                    .get(0)
//                                    .getAgencies()
//                                    .get(0)
//                                    .getWikiURL());
//                }
//            });
//        } else {
//            mission_wikiButton_one.setVisibility(View.GONE);
//        }
//
//
//        if (detailLaunch.getPad().get(0).getAgencies().get(0).getInfoURL() != null &&
//                detailLaunch.getPad().get(0).getAgencies().get(0).getInfoURL().length() > 0) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getLocation()
//                    .getPads().get(0).getAgencies().get(0).getInfoURL()));
//
//            mission_infoButton_one.setVisibility(View.VISIBLE);
//            mission_infoButton_one.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getPad()
//                            .get(0).getAgencies().get(0).getInfoURL());
//                    Analytics.getInstance().sendButtonClickedWithURL(
//                            "Mission Info",
//                            detailLaunch.getName(),
//                            detailLaunch.getPad()
//                                    .get(0).getAgencies().get(0).getInfoURL()
//                    );
//                }
//            });
//        } else {
//            mission_infoButton_one.setVisibility(View.GONE);
//        }
//    }
//
//    private void setTwoMissionAgencies() {
//        mission_agency_type.setVisibility(View.VISIBLE);
//        mission_agency_title.setText(R.string.mission_agencies);
//
//        String agencyTypeOne = "";
//        String agencyNameOne = "";
//        String agencyAbbrevOne = "";
//        String agencyAbbrevTwo = "";
//        String agencyTypeTwo = "";
//        String agencyNameTwo = "";
//        mission_agency_type.setText(agencyTypeOne);
//        mission_agency_type_one.setText(agencyTypeOne);
//        mission_agency_type_two.setText(agencyTypeTwo);
//        mission_agency_one.setText(agencyAbbrevOne);
//        mission_agency_two.setText(agencyAbbrevTwo);
//        try {
//            agencyTypeOne = getAgencyType(detailLaunch.getPad().get(0).getAgencies().get(0).getType());
//            agencyNameOne = detailLaunch.getPad().get(0).getAgencies().get(0).getName();
//            agencyAbbrevOne = detailLaunch.getPad().get(0).getAgencies().get(0).getAbbrev();
//            agencyTypeTwo = getAgencyType(detailLaunch.getPad().get(0).getAgencies().get(1).getType());
//            agencyNameTwo = detailLaunch.getPad().get(0).getAgencies().get(1).getName();
//            agencyAbbrevTwo = detailLaunch.getPad().get(0).getAgencies().get(1).getAbbrev();
//        } catch (NullPointerException e){
//            Timber.e(e);
//        }
//
//        checkLaunchSummary(agencyAbbrevOne, mission_agency_summary_one);
//        checkLaunchSummary(agencyAbbrevTwo, mission_agency_summary_two);
//
//        mission_one.setVisibility(View.VISIBLE);
//        mission_two.setVisibility(View.VISIBLE);
//        mission_agency_type.setText(String.format("%s | %s", agencyTypeOne, agencyTypeTwo));
//        mission_agency_type_one.setText(String.format("Type: %s", agencyTypeOne));
//        mission_agency_type_two.setText(String.format("Type: %s", agencyTypeTwo));
//        mission_agency_one.setText(String.format("%s (%s)", agencyNameOne, agencyAbbrevOne));
//        mission_agency_two.setText(String.format("%s (%s)", agencyNameTwo, agencyAbbrevTwo));
//
//        if (detailLaunch.getPad().get(0).getAgencies().get(0).getWikiURL() != null &&
//                detailLaunch.getPad().get(0).getAgencies().get(0).getWikiURL().length() > 0) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getPad().get(0).getAgencies().get(0).getWikiURL()));
//
//            mission_wikiButton_one.setVisibility(View.VISIBLE);
//            mission_wikiButton_one.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getPad().get(0).getAgencies().get(0).getWikiURL());
//                    Analytics.getInstance().sendButtonClickedWithURL(
//                            "Mission Wiki",
//                            detailLaunch.getName(),
//                            detailLaunch.getPad().get(0).getAgencies().get(0).getWikiURL());
//                }
//            });
//        } else {
//            mission_wikiButton_one.setVisibility(View.GONE);
//        }
//
//
//        if (detailLaunch.getPad().get(0).getAgencies().get(0).getInfoURL() != null &&
//                detailLaunch.getPad().get(0).getAgencies().get(0).getInfoURL().length() > 0) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getPad().get(0).getAgencies().get(0).getInfoURL()));
//
//            mission_infoButton_one.setVisibility(View.VISIBLE);
//            mission_infoButton_one.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getPad().get(0).getAgencies().get(0).getInfoURL());
//                    Analytics.getInstance().sendButtonClickedWithURL(
//                            "Mission Info",
//                            detailLaunch.getName(),
//                            detailLaunch.getPad().get(0).getAgencies().get(0).getInfoURL());
//
//                }
//            });
//        } else {
//            mission_infoButton_one.setVisibility(View.GONE);
//        }
//
//        if (detailLaunch.getPad().get(0).getAgencies().get(1).getWikiURL() != null &&
//                detailLaunch.getPad().get(0).getAgencies().get(1).getWikiURL().length() > 0) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getPad().get(0).getAgencies().get(1).getWikiURL()));
//
//            mission_wikiButton_two.setVisibility(View.VISIBLE);
//            mission_wikiButton_two.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getPad().get(0).getAgencies().get(1).getWikiURL());
//                    Analytics.getInstance().sendButtonClickedWithURL(
//                            "Mission Wiki",
//                            detailLaunch.getName(),
//                            detailLaunch.getPad().get(0).getAgencies().get(1).getWikiURL());
//                }
//            });
//        } else {
//            mission_wikiButton_two.setVisibility(View.GONE);
//        }
//
//
//        if (detailLaunch.getPad().get(0).getAgencies().get(1).getInfoURL() != null &&
//                detailLaunch.getPad().get(0).getAgencies().get(1).getInfoURL().length() > 0) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getPad().get(0).getAgencies().get(1).getInfoURL()));
//
//            mission_infoButton_two.setVisibility(View.VISIBLE);
//            mission_infoButton_two.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getPad().get(0).getAgencies().get(1).getInfoURL());
//                    Analytics.getInstance().sendButtonClickedWithURL(
//                            "Mission Info",
//                            detailLaunch.getName(),
//                            detailLaunch.getPad().get(0).getAgencies().get(1).getInfoURL());
//                }
//            });
//        } else {
//            mission_infoButton_two.setVisibility(View.GONE);
//        }
//    }
//
//    private void setNoVehicleAgencies() {
//        launch_agency_type.setVisibility(View.VISIBLE);
//        launch_one.setVisibility(View.GONE);
//        launch_two.setVisibility(View.GONE);
//
//        vehicle_agency_title.setText("Vehicle LauncherAgency");
//        launch_agency_type.setText("Unknown");
//
//    }
//
//    private void setOneVehicleAgencies() {
//        launch_agency_type.setVisibility(View.VISIBLE);
//        launch_agency_type.setText(detailLaunch.getRocket().getName());
//
//        String countryCode = "";
//        String agencyType = "";
//        String agencyName = "";
//        String agencyAbbrev = "";
//        try {
//            countryCode = detailLaunch.getRocket().getAgencies().get(0).getCountryCode();
//            agencyType = getAgencyType(detailLaunch.getRocket().getAgencies().get(0).getType());
//            agencyName = detailLaunch.getRocket().getAgencies().get(0).getName();
//            agencyAbbrev = detailLaunch.getRocket().getAgencies().get(0).getAbbrev();
//        } catch (NullPointerException e){
//            Timber.e(e);
//        }
//
//        checkLaunchSummary(agencyAbbrev, launch_agency_summary_one);
//
//        if (countryCode.length() > 10) {
//            countryCode = countryCode.substring(0, 3) + ", " + countryCode.substring(4, 7) + ", " + countryCode.substring(7, 10) + "...";
//            agencyType = agencyType + " | Multinational";
//        } else if (countryCode.length() > 4 && countryCode.length() < 10) {
//            countryCode = countryCode.substring(0, 3);
//        }
//
//        launch_one.setVisibility(View.VISIBLE);
//        launch_two.setVisibility(View.GONE);
//        launch_vehicle_agency_one.setText(String.format("%s (%s) %s", agencyName, agencyAbbrev, countryCode));
//        launch_agency_type_one.setText("Type: " + agencyType);
//
//        if (detailLaunch.getRocket().getAgencies().get(0).getWikiURL() != null &&
//                detailLaunch.getRocket().getAgencies().get(0).getWikiURL().length() > 0) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getWikiURL()));
//
//            wikiButton_one.setVisibility(View.VISIBLE);
//            wikiButton_one.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(0).getWikiURL());
//                    Analytics.getInstance().sendButtonClickedWithURL(
//                            "Mission Wiki",
//                            detailLaunch.getName(),
//                            detailLaunch.getRocket().getAgencies().get(0).getWikiURL()
//                    );
//                }
//            });
//        } else {
//            wikiButton_one.setVisibility(View.GONE);
//        }
//
//
//        if (detailLaunch.getRocket().getAgencies().get(0).getInfoURL() != null &&
//                detailLaunch.getRocket().getAgencies().get(0).getInfoURL().length() > 0) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getInfoURL()));
//
//            infoButton_one.setVisibility(View.VISIBLE);
//            infoButton_one.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(0).getInfoURL());
//                    Analytics.getInstance().sendButtonClickedWithURL(
//                            "Mission Info",
//                            detailLaunch.getName(),
//                            detailLaunch.getRocket().getAgencies().get(0).getInfoURL()
//                    );
//                }
//            });
//        } else {
//            infoButton_one.setVisibility(View.GONE);
//        }
//    }
//
//    private void setTwoVehicleAgencies() {
//        String countryCode = "";
//        String agencyOne = "";
//        String agencyNameOne = "";
//        String agencyAbbrevOne = "";
//        try {
//            countryCode = detailLaunch.getRocket().getAgencies().get(0).getCountryCode();
//            agencyOne = getAgencyType(detailLaunch.getRocket().getAgencies().get(0).getType());
//            agencyNameOne = detailLaunch.getRocket().getAgencies().get(0).getName();
//            agencyAbbrevOne = detailLaunch.getRocket().getAgencies().get(0).getAbbrev();
//        } catch (NullPointerException e){
//            Timber.e(e);
//        }
//
//        vehicle_agency_title.setText("Vehicle Agencies");
//
//        checkLaunchSummary(agencyAbbrevOne, launch_agency_summary_one);
//
//        if (countryCode.length() > 10) {
//            countryCode = countryCode.substring(0, 3) + ", " + countryCode.substring(4, 7) + ", " + countryCode.substring(7, 10) + "...";
//            agencyOne = agencyOne + " | Multinational";
//        } else if (countryCode.length() > 4 && countryCode.length() < 10) {
//            countryCode = countryCode.substring(0, 3);
//        }
//
//        String countryCodeTwo = "";
//        String agencyTwo = "";
//        String agencyNameTwo = "";
//        String agencyAbbrevTwo = "";
//
//        try {
//            countryCodeTwo = detailLaunch.getRocket().getAgencies().get(1).getCountryCode();
//            agencyTwo = getAgencyType(detailLaunch.getRocket().getAgencies().get(1).getType());
//            agencyNameTwo = detailLaunch.getRocket().getAgencies().get(1).getName();
//            agencyAbbrevTwo = detailLaunch.getRocket().getAgencies().get(1).getAbbrev();
//        } catch (NullPointerException e){
//            Timber.e(e);
//        }
//
//        checkLaunchSummary(agencyAbbrevTwo, launch_agency_summary_two);
//
//        if (countryCodeTwo.length() > 10) {
//            countryCodeTwo = countryCodeTwo.substring(0, 3) + ", " + countryCodeTwo.substring(4, 7) + ", " + countryCodeTwo.substring(7, 10) + "...";
//            agencyTwo = agencyTwo + " | Multinational";
//        } else if (countryCodeTwo.length() > 4 && countryCodeTwo.length() < 10) {
//            countryCodeTwo = countryCodeTwo.substring(0, 3);
//        }
//
//        launch_one.setVisibility(View.VISIBLE);
//        launch_two.setVisibility(View.VISIBLE);
//        launch_agency_type_one.setText(String.format("Type: %s", agencyOne));
//        launch_agency_type_two.setText(String.format("Type: %s", agencyTwo));
//        launch_vehicle_agency_one.setText(String.format("%s (%s) %s", agencyNameOne, agencyAbbrevOne, countryCode));
//        launch_vehicle_agency_two.setText(String.format("%s (%s) %s", agencyNameTwo, agencyAbbrevTwo, countryCodeTwo));
//
//        if (detailLaunch.getRocket().getAgencies().get(0).getInfoURL() != null &&
//                detailLaunch.getRocket().getAgencies().get(0).getInfoURL().length() > 0) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getWikiURL()));
//            wikiButton_one.setVisibility(View.VISIBLE);
//            wikiButton_one.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(0).getWikiURL());
//                    Analytics.getInstance().sendButtonClickedWithURL(
//                            "Mission Wiki",
//                            detailLaunch.getName(),
//                            detailLaunch.getRocket().getAgencies().get(0).getInfoURL()
//                    );
//                }
//            });
//        } else {
//            wikiButton_one.setVisibility(View.GONE);
//        }
//
//
//        if (detailLaunch.getRocket().getAgencies().get(0).getInfoURL() != null &&
//                detailLaunch.getRocket().getAgencies().get(0).getInfoURL().length() > 0) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getInfoURL()));
//
//            infoButton_one.setVisibility(View.VISIBLE);
//            infoButton_one.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(0).getInfoURL());
//                    Analytics.getInstance().sendButtonClickedWithURL(
//                            "Mission Info",
//                            detailLaunch.getName(),
//                            detailLaunch.getRocket().getAgencies().get(0).getInfoURL()
//                    );
//                }
//            });
//        } else {
//            infoButton_one.setVisibility(View.GONE);
//        }
//
//        if (detailLaunch.getRocket().getAgencies().get(1).getWikiURL() != null &&
//                detailLaunch.getRocket().getAgencies().get(1).getWikiURL().length() > 0) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(1).getWikiURL()));
//
//            wikiButton_two.setVisibility(View.VISIBLE);
//            wikiButton_two.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(1).getWikiURL());
//                    Analytics.getInstance().sendButtonClickedWithURL(
//                            "Mission Wiki",
//                            detailLaunch.getName(),
//                            detailLaunch.getRocket().getAgencies().get(1).getWikiURL()
//                    );
//                }
//            });
//        } else {
//            wikiButton_two.setVisibility(View.GONE);
//        }
//
//        if (detailLaunch.getRocket().getAgencies().get(1).getInfoURL() != null &&
//                detailLaunch.getRocket().getAgencies().get(1).getInfoURL().length() > 1) {
//            ((LaunchDetailActivity) context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(1).getInfoURL()));
//
//            infoButton_two.setVisibility(View.VISIBLE);
//            infoButton_two.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Activity activity = (Activity) context;
//                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(1).getInfoURL());
//                    Analytics.getInstance().sendButtonClickedWithURL(
//                            "Mission Info",
//                            detailLaunch.getName(),
//                            detailLaunch.getRocket().getAgencies().get(1).getInfoURL()
//                    );
//                }
//            });
//        } else {
//            infoButton_two.setVisibility(View.GONE);
//        }
//    }

    private void checkLaunchSummary(String abbrev, TextView view) {
        if (abbrev.equalsIgnoreCase("spx")) {
            view.setText(R.string.spacex_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("usaf")) {
            view.setText(R.string.usaf_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("cnsa")) {
            view.setText(R.string.cnsa_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("casc")) {
            view.setText(R.string.casc_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("isro")) {
            view.setText(R.string.isro_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("vko")) {
            view.setText(R.string.vko_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("fka")) {
            view.setText(R.string.roscosmos_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("ula")) {
            view.setText(R.string.ula_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("gd")) {
            view.setText(R.string.gd_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("asa")) {
            view.setText(R.string.asa_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("TsSKB-Progress")) {
            view.setText(R.string.TsSKB_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("nasa")) {
            view.setText(R.string.nasa_summary);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setText("");
            view.setVisibility(View.GONE);
        }
    }

    private String getAgencyType(int type) {
        String agency;
        switch (type) {
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static AgencyDetailFragment newInstance() {
        return new AgencyDetailFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LaunchEvent event) {
        setLaunch(event.launch);
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.v("On Start");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        Timber.v("On Stop");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
