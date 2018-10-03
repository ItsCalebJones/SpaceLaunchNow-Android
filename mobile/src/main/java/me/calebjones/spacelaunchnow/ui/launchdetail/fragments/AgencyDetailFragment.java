package me.calebjones.spacelaunchnow.ui.launchdetail.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseFragment;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.events.LaunchEvent;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.ui.launchdetail.OnFragmentInteractionListener;
import me.calebjones.spacelaunchnow.ui.launches.agency.AgencyLaunchActivity;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class AgencyDetailFragment extends BaseFragment {

    @BindView(R.id.lsp_logo)
    ImageView lspLogo;
    @BindView(R.id.lsp_name)
    TextView lspName;
    @BindView(R.id.lsp_type)
    TextView lspType;
    @BindView(R.id.lsp_summary)
    TextView lspSummary;
    @BindView(R.id.lsp_infoButton_one)
    AppCompatButton lspInfoButtonOne;
    @BindView(R.id.lsp_wikiButton_one)
    AppCompatButton lspWikiButtonOne;
    @BindView(R.id.lsp_card)
    CardView lspCard;
    @BindView(R.id.lsp_administrator)
    TextView lspAdministrator;
    @BindView(R.id.lsp_founded_year)
    TextView lspFoundedYear;
    @BindView(R.id.lsp_agency)
    AppCompatButton lspAgency;
    private SharedPreferences sharedPref;
    private static ListPreferences sharedPreference;
    private Context context;
    private OnFragmentInteractionListener mListener;

    public static Launch detailLaunch;

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
        } else {
            mListener.sendLaunchToFragment(OnFragmentInteractionListener.AGENCY);
        }

        return view;
    }

    @Override
    public void onResume() {
        if (detailLaunch != null && detailLaunch.isValid()) {
            setUpViews(detailLaunch);
        } else {
            mListener.sendLaunchToFragment(OnFragmentInteractionListener.AGENCY);
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

            Timber.v("Setting up views...");
            lspCard.setVisibility(View.VISIBLE);

            lspAgency.setText(String.format(this.getString(R.string.view_rocket_launches), launch.getRocket().getConfiguration().getLaunchServiceProvider().getName()));
            if (detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getLogoUrl() != null) {
                lspLogo.setVisibility(View.VISIBLE);
                GlideApp.with(context)
                        .load(detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getLogoUrl())
                        .centerInside()
                        .into(lspLogo);
            } else {
                lspLogo.setVisibility(View.GONE);
            }
            lspName.setText(detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getName());
            lspType.setText(detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getType());
            if (detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getAdministrator() != null) {
                lspAdministrator.setText(String.format("%s", detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getAdministrator()));
            } else {
                lspAdministrator.setText(R.string.unknown_administrator);
            }
            if (detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getFoundingYear() != null) {
                lspFoundedYear.setText(String.format(getString(R.string.founded_in), detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getFoundingYear()));
            } else {
                lspFoundedYear.setText(R.string.unknown_year);
            }
            lspSummary.setText(detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getDescription());
            if (detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getInfoUrl() == null) {
                lspInfoButtonOne.setVisibility(View.GONE);
            }

            if (detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getWikiUrl() == null) {
                lspWikiButtonOne.setVisibility(View.GONE);
            }
            lspAgency.setVisibility(View.VISIBLE);

        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static AgencyDetailFragment newInstance() {
        return new AgencyDetailFragment();
    }

    @OnClick(R.id.lsp_infoButton_one)
    public void onLspInfoButtonOneClicked() {
        Activity activity = (Activity) context;
        Utils.openCustomTab(activity, context, detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getInfoUrl());
    }

    @OnClick(R.id.lsp_wikiButton_one)
    public void onLspWikiButtonOneClicked() {
        Activity activity = (Activity) context;
        Utils.openCustomTab(activity, context, detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getWikiUrl());
    }

    @OnClick(R.id.lsp_agency)
    public void onViewClicked() {
        Intent intent = new Intent(context, AgencyLaunchActivity.class);
        intent.putExtra("lspName", detailLaunch.getRocket().getConfiguration().getLaunchServiceProvider().getName());
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
