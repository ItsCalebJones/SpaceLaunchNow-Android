package me.calebjones.spacelaunchnow.common.ui.launchdetail.fragments;

import android.app.Activity;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.common.prefs.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.DetailsViewModel;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.launches.agency.AgencyLaunchActivity;
import timber.log.Timber;

public class AgencyDetailFragment extends BaseFragment {

    @BindView(R2.id.lsp_logo)
    ImageView lspLogo;
    @BindView(R2.id.lsp_name)
    TextView lspName;
    @BindView(R2.id.lsp_type)
    TextView lspType;
    @BindView(R2.id.lsp_summary)
    TextView lspSummary;
    @BindView(R2.id.lsp_infoButton_one)
    AppCompatButton lspInfoButtonOne;
    @BindView(R2.id.lsp_wikiButton_one)
    AppCompatButton lspWikiButtonOne;
    @BindView(R2.id.lsp_card)
    CardView lspCard;
    @BindView(R2.id.lsp_administrator)
    TextView lspAdministrator;
    @BindView(R2.id.lsp_founded_year)
    TextView lspFoundedYear;
    @BindView(R2.id.lsp_agency)
    AppCompatButton lspAgency;

    @BindView(R2.id.launch_total_value)
    TextView launchTotal;
    @BindView(R2.id.consecutive_success_value)
    TextView consecutiveTotal;
    @BindView(R2.id.launch_success_value)
    TextView successTotal;
    @BindView(R2.id.launch_failure_value)
    TextView failureTotal;

    @BindView(R2.id.landing_total_value)
    TextView landingTotal;
    @BindView(R2.id.consecutive_landing_success_value)
    TextView consecutiveLandingTotal;
    @BindView(R2.id.landing_success_value)
    TextView landingSuccessTotal;
    @BindView(R2.id.landing_failure_value)
    TextView landingFailureTotal;



    private SharedPreferences sharedPref;
    private static ListPreferences sharedPreference;
    private Context context;

    public static Launch detailLaunch;
    private Unbinder unbinder;
    private DetailsViewModel model;

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

        unbinder = ButterKnife.bind(this, view);

        Timber.v("Creating views...");
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

            Timber.v("Setting up views...");
            lspCard.setVisibility(View.VISIBLE);

            lspAgency.setText(String.format(this.getString(R.string.view_rocket_launches), launch.getRocket().getConfiguration().getManufacturer().getName()));
            if (detailLaunch.getRocket().getConfiguration().getManufacturer().getLogoUrl() != null) {
                lspLogo.setVisibility(View.VISIBLE);
                GlideApp.with(context)
                        .load(detailLaunch.getRocket().getConfiguration().getManufacturer().getLogoUrl())
                        .centerInside()
                        .into(lspLogo);
            } else {
                lspLogo.setVisibility(View.GONE);
            }
            lspName.setText(detailLaunch.getRocket().getConfiguration().getManufacturer().getName());
            lspType.setText(detailLaunch.getRocket().getConfiguration().getManufacturer().getType());
            if (detailLaunch.getRocket().getConfiguration().getManufacturer().getAdministrator() != null) {
                lspAdministrator.setText(String.format("%s", detailLaunch.getRocket().getConfiguration().getManufacturer().getAdministrator()));
            } else {
                lspAdministrator.setText(R.string.unknown_administrator);
            }
            if (detailLaunch.getRocket().getConfiguration().getManufacturer().getFoundingYear() != null) {
                lspFoundedYear.setText(String.format(getString(R.string.founded_in), detailLaunch.getRocket().getConfiguration().getManufacturer().getFoundingYear()));
            } else {
                lspFoundedYear.setText(R.string.unknown_year);
            }
            lspSummary.setText(detailLaunch.getRocket().getConfiguration().getManufacturer().getDescription());
            if (detailLaunch.getRocket().getConfiguration().getManufacturer().getInfoUrl() == null) {
                lspInfoButtonOne.setVisibility(View.GONE);
            }

            if (detailLaunch.getRocket().getConfiguration().getManufacturer().getWikiUrl() == null) {
                lspWikiButtonOne.setVisibility(View.GONE);
            }
            lspAgency.setVisibility(View.VISIBLE);

            if (detailLaunch.getRocket().getConfiguration().getManufacturer() != null) {
                Agency agency = detailLaunch.getRocket().getConfiguration().getManufacturer();

                if (agency.getTotalLaunchCount() != null) {
                    launchTotal.setText(String.valueOf(agency.getTotalLaunchCount()));
                } else {
                    launchTotal.setText(" - ");
                }

                if (agency.getSuccessfulLaunches() != null) {
                    successTotal.setText(String.valueOf(agency.getSuccessfulLaunches()));
                } else {
                    successTotal.setText(" - ");
                }

                if (agency.getConsecutiveSuccessfulLaunches() != null) {
                    consecutiveTotal.setText(String.valueOf(agency.getConsecutiveSuccessfulLaunches()));
                } else {
                    consecutiveTotal.setText(" - ");
                }

                if (agency.getFailedLaunches() != null) {
                    failureTotal.setText(String.valueOf(agency.getFailedLaunches()));
                } else {
                    failureTotal.setText(" - ");
                }

                if (agency.getAttemptedLandings() != null) {
                    landingTotal.setText(String.valueOf(agency.getAttemptedLandings()));
                } else {
                    launchTotal.setText(" - ");
                }

                if (agency.getSuccessfulLandings() != null) {
                    landingSuccessTotal.setText(String.valueOf(agency.getSuccessfulLandings()));
                } else {
                    landingSuccessTotal.setText(" - ");
                }

                if (agency.getConsecutiveSuccessfulLandings() != null) {
                    consecutiveLandingTotal.setText(String.valueOf(agency.getConsecutiveSuccessfulLandings()));
                } else {
                    consecutiveLandingTotal.setText(" - ");
                }

                if (agency.getFailedLandings() != null) {
                    landingFailureTotal.setText(String.valueOf(agency.getFailedLandings()));
                } else {
                    landingFailureTotal.setText(" - ");
                }

            }

        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(DetailsViewModel.class);
        // update UI
        model.getLaunch().observe(this, this::setLaunch);
    }

    public static AgencyDetailFragment newInstance() {
        return new AgencyDetailFragment();
    }

    @OnClick(R2.id.lsp_infoButton_one)
    public void onLspInfoButtonOneClicked() {
        Activity activity = (Activity) context;
        Utils.openCustomTab(activity, context, detailLaunch.getRocket().getConfiguration().getManufacturer().getInfoUrl());
    }

    @OnClick(R2.id.lsp_wikiButton_one)
    public void onLspWikiButtonOneClicked() {
        Activity activity = (Activity) context;
        Utils.openCustomTab(activity, context, detailLaunch.getRocket().getConfiguration().getManufacturer().getWikiUrl());
    }

    @OnClick(R2.id.lsp_agency)
    public void onViewClicked() {
        Intent intent = new Intent(context, AgencyLaunchActivity.class);
        intent.putExtra("lspName", detailLaunch.getRocket().getConfiguration().getManufacturer().getName());
        startActivity(intent);
    }


}
