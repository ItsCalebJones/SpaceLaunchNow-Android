package me.calebjones.spacelaunchnow.ui.fragment.launches.details;

import android.app.Activity;
import android.content.Context;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.natives.RocketDetails;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.Utils;

public class AgencyDetailFragment extends Fragment {

    private SharedPreferences sharedPref;
    private static ListPreferences sharedPreference;
    private Context context;

    public static LaunchRealm detailLaunch;
    private RocketDetails launchVehicle;

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

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.context = getContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        view = inflater.inflate(R.layout.detail_launch_agency, container, false);

        ButterKnife.bind(this, view);

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
        mission_agency_type.setVisibility(View.VISIBLE);
        mission_one.setVisibility(View.GONE);
        mission_two.setVisibility(View.GONE);

    }

    private void setOneMissionAgencies() {
        mission_agency_type.setVisibility(View.VISIBLE);
        mission_agency_type.setText(detailLaunch.getLocation().getPads().get(0).getName());
        String agencyTypeOne = getAgencyType(detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getType());
        String agencyNameOne = detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getName();
        String agencyAbbrevOne = detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getAbbrev();

        checkLaunchSummary(agencyAbbrevOne, mission_agency_summary_one);

        mission_one.setVisibility(View.VISIBLE);
        mission_two.setVisibility(View.GONE);
        mission_agency_one.setText(String.format("%s (%s)", agencyNameOne, agencyAbbrevOne));
        mission_agency_type_one.setText("Type: " + agencyTypeOne);

        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getWikiURL().length() > 0){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getWikiURL()));

            mission_wikiButton_one.setVisibility(View.VISIBLE);
            mission_wikiButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity, context, detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getWikiURL());
                }
            });
        } else {
            mission_wikiButton_one.setVisibility(View.GONE);
        }


        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getInfoURL().length() > 0){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getLocation()
                    .getPads().get(0).getAgencies().get(0).getInfoURL()));

            mission_infoButton_one.setVisibility(View.VISIBLE);
            mission_infoButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity, context, detailLaunch.getLocation().getPads()
                            .get(0).getAgencies().get(0).getInfoURL());
                }
            });
        } else {
            mission_infoButton_one.setVisibility(View.GONE);
        }
    }

    private void setTwoMissionAgencies() {
        mission_agency_type.setVisibility(View.VISIBLE);
        mission_agency_type.setText(detailLaunch.getLocation().getPads().get(0).getName());

        String agencyTypeOne = getAgencyType(detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getType());
        String agencyNameOne = detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getName();
        String agencyAbbrevOne = detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getAbbrev();
        String agencyTypeTwo = getAgencyType(detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getType());
        String agencyNameTwo = detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getName();
        String agencyAbbrevTwo = detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getAbbrev();

        checkLaunchSummary(agencyAbbrevOne, mission_agency_summary_one);
        checkLaunchSummary(agencyAbbrevTwo, mission_agency_summary_two);

        mission_one.setVisibility(View.VISIBLE);
        mission_two.setVisibility(View.VISIBLE);
        mission_agency_type_one.setText(String.format("Type: %s", agencyTypeOne));
        mission_agency_type_two.setText(String.format("Type: %s", agencyTypeTwo));
        mission_agency_one.setText(String.format("%s (%s)", agencyNameOne, agencyAbbrevOne));
        mission_agency_two.setText(String.format("%s (%s)", agencyNameTwo, agencyAbbrevTwo));

        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getWikiURL().length() > 0){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getWikiURL()));

            mission_wikiButton_one.setVisibility(View.VISIBLE);
            mission_wikiButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity,context, detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getWikiURL());

                }
            });
        } else {
            mission_wikiButton_one.setVisibility(View.GONE);
        }


        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getInfoURL().length() > 0){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getInfoURL()));

            mission_infoButton_one.setVisibility(View.VISIBLE);
            mission_infoButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity, context, detailLaunch.getLocation().getPads().get(0).getAgencies().get(0).getInfoURL());

                }
            });
        } else {
            mission_infoButton_one.setVisibility(View.GONE);
        }

        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getWikiURL().length() > 0){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getWikiURL()));

            mission_wikiButton_two.setVisibility(View.VISIBLE);
            mission_wikiButton_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity, context, detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getWikiURL());
                }
            });
        } else {
            mission_wikiButton_two.setVisibility(View.GONE);
        }


        if (detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getInfoURL().length() > 0){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getInfoURL()));

            mission_infoButton_two.setVisibility(View.VISIBLE);
            mission_infoButton_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity, context, detailLaunch.getLocation().getPads().get(0).getAgencies().get(1).getInfoURL());
                }
            });
        } else {
            mission_infoButton_two.setVisibility(View.GONE);
        }
    }

    private void setNoVehicleAgencies() {
        launch_agency_type.setVisibility(View.VISIBLE);
        launch_one.setVisibility(View.GONE);
        launch_two.setVisibility(View.GONE);

    }

    private void setOneVehicleAgencies() {
        launch_agency_type.setVisibility(View.VISIBLE);
        launch_agency_type.setText(detailLaunch.getRocket().getName());
        String countryCode = detailLaunch.getRocket().getAgencies().get(0).getCountryCode();
        String agencyType = getAgencyType(detailLaunch.getRocket().getAgencies().get(0).getType());
        String agencyName = detailLaunch.getRocket().getAgencies().get(0).getName();
        String agencyAbbrev = detailLaunch.getRocket().getAgencies().get(0).getAbbrev();

        checkLaunchSummary(agencyAbbrev, launch_agency_summary_one);

        if (countryCode.length() > 10){
            countryCode = countryCode.substring(0,3) + ", "+ countryCode.substring(4,7) + ", "+ countryCode.substring(7,10) + "...";
            agencyType = agencyType + " | Multinational";
        }  else if (countryCode.length() > 4 && countryCode.length() < 10){
            countryCode = countryCode.substring(0,3);
        }

        launch_one.setVisibility(View.VISIBLE);
        launch_two.setVisibility(View.GONE);
        launch_vehicle_agency_one.setText(String.format("%s (%s) %s",agencyName , agencyAbbrev, countryCode));
        launch_agency_type_one.setText("Type: " + agencyType);

        if (detailLaunch.getRocket().getAgencies().get(0).getInfoURL().length() > 0){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getWikiURL()));

            wikiButton_one.setVisibility(View.VISIBLE);
            wikiButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(0).getWikiURL());
                }
            });
        } else {
            wikiButton_one.setVisibility(View.GONE);
        }


        if (detailLaunch.getRocket().getAgencies().get(0).getInfoURL().length() > 0){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getInfoURL()));

            infoButton_one.setVisibility(View.VISIBLE);
            infoButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(0).getInfoURL());
                }
            });
        } else {
            infoButton_one.setVisibility(View.GONE);
        }
    }

    private void setTwoVehicleAgencies() {
        String countryCode = detailLaunch.getRocket().getAgencies().get(0).getCountryCode();
        String agencyOne = getAgencyType(detailLaunch.getRocket().getAgencies().get(0).getType());
        String agencyNameOne = detailLaunch.getRocket().getAgencies().get(0).getName();
        String agencyAbbrevOne = detailLaunch.getRocket().getAgencies().get(0).getAbbrev();

        checkLaunchSummary(agencyAbbrevOne, launch_agency_summary_one);

        if (countryCode.length() > 10){
            countryCode = countryCode.substring(0,3) + ", "+ countryCode.substring(4,7) + ", "+ countryCode.substring(7,10) + "...";
            agencyOne = agencyOne + " | Multinational";
        }  else if (countryCode.length() > 4 && countryCode.length() < 10) {
            countryCode = countryCode.substring(0, 3);
        }

        String countryCodeTwo = detailLaunch.getRocket().getAgencies().get(1).getCountryCode();
        String agencyTwo = getAgencyType(detailLaunch.getRocket().getAgencies().get(1).getType());
        String agencyNameTwo = detailLaunch.getRocket().getAgencies().get(1).getName();
        String agencyAbbrevTwo = detailLaunch.getRocket().getAgencies().get(1).getAbbrev();

        checkLaunchSummary(agencyAbbrevTwo, launch_agency_summary_two);

        if (countryCodeTwo.length() > 10){
            countryCodeTwo = countryCodeTwo.substring(0,3) + ", "+ countryCodeTwo.substring(4,7) + ", "+ countryCodeTwo.substring(7,10) + "...";
            agencyTwo = agencyTwo + " | Multinational";
        }  else if (countryCodeTwo.length() > 4 && countryCodeTwo.length() < 10) {
            countryCodeTwo = countryCodeTwo.substring(0, 3);
        }

        launch_one.setVisibility(View.VISIBLE);
        launch_two.setVisibility(View.VISIBLE);
        launch_agency_type_one.setText(String.format("Type: %s", agencyOne));
        launch_agency_type_two.setText(String.format("Type: %s", agencyTwo));
        launch_vehicle_agency_one.setText(String.format("%s (%s) %s",agencyNameOne, agencyAbbrevOne , countryCode));
        launch_vehicle_agency_two.setText(String.format("%s (%s) %s", agencyNameTwo,agencyAbbrevTwo, countryCodeTwo));

        if (detailLaunch.getRocket().getAgencies().get(0).getInfoURL().length() > 0){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getWikiURL()));
            wikiButton_one.setVisibility(View.VISIBLE);
            wikiButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(0).getWikiURL());
                }
            });
        } else {
            wikiButton_one.setVisibility(View.GONE);
        }


        if (detailLaunch.getRocket().getAgencies().get(0).getInfoURL().length() > 0){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(0).getInfoURL()));

            infoButton_one.setVisibility(View.VISIBLE);
            infoButton_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(0).getInfoURL());
                }
            });
        } else {
            infoButton_one.setVisibility(View.GONE);
        }

        if (detailLaunch.getRocket().getAgencies().get(1).getWikiURL().length() > 0){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(1).getWikiURL()));

            wikiButton_two.setVisibility(View.VISIBLE);
            wikiButton_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(1).getWikiURL());
                }
            });
        } else {
            wikiButton_two.setVisibility(View.GONE);
        }

        if (detailLaunch.getRocket().getAgencies().get(1).getInfoURL().length() > 1){
            ((LaunchDetailActivity)context).mayLaunchUrl(Uri.parse(detailLaunch.getRocket().getAgencies().get(1).getInfoURL()));

            infoButton_two.setVisibility(View.VISIBLE);
            infoButton_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = (Activity)context;
                    Utils.openCustomTab(activity, context, detailLaunch.getRocket().getAgencies().get(1).getInfoURL());
                }
            });
        } else {
            infoButton_two.setVisibility(View.GONE);
        }
    }

    private void checkLaunchSummary(String abbrev, TextView view) {
        if (abbrev.equalsIgnoreCase("spx")){
            view.setText(R.string.spacex_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("usaf")){
            view.setText(R.string.usaf_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("cnsa")){
            view.setText(R.string.cnsa_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("casc")){
            view.setText(R.string.casc_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("isro")){
            view.setText(R.string.isro_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("vko")){
            view.setText(R.string.vko_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("fka")){
            view.setText(R.string.roscosmos_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("ula")){
            view.setText(R.string.ula_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("gd")){
            view.setText(R.string.gd_summary);
            view.setVisibility(View.VISIBLE);
        } else if (abbrev.equalsIgnoreCase("asa")){
            view.setText(R.string.asa_summary);
            view.setVisibility(View.VISIBLE);
        }  else if (abbrev.equalsIgnoreCase("TsSKB-Progress")){
            view.setText(R.string.TsSKB_summary);
            view.setVisibility(View.VISIBLE);
        }  else if (abbrev.equalsIgnoreCase("nasa")){
            view.setText(R.string.nasa_summary);
            view.setVisibility(View.VISIBLE);
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