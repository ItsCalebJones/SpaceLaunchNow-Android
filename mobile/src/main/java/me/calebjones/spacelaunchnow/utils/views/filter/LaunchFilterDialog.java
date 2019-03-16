package me.calebjones.spacelaunchnow.utils.views.filter;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pixplicity.easyprefs.library.Prefs;
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.calebjones.spacelaunchnow.R;

public class LaunchFilterDialog extends BottomSheetDialogFragment {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.location_group)
    View locationGroup;
    @BindView(R.id.agency_group)
    View agencyGroup;
    @BindView(R.id.rocket_group)
    View rocketGroup;
    @BindView(R.id.apply)
    AppCompatButton apply;
    @BindView(R.id.cancel)
    AppCompatButton cancel;
    @BindView(R.id.location_subtitle)
    TextView locationSubtitle;
    @BindView(R.id.agency_subtitle)
    TextView agencySubtitle;
    @BindView(R.id.rocket_subtitle)
    TextView rocketSubtitle;
    Unbinder unbinder;
    @BindView(R.id.location_recycler)
    RecyclerView locationRecycler;
    @BindView(R.id.agency_recycler)
    RecyclerView agencyRecycler;
    @BindView(R.id.rocket_recycler)
    RecyclerView rocketRecycler;
    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;
    private SelectorAdapter locationAdapter;
    private SelectorAdapter agencyAdapter;
    private SelectorAdapter rocketAdapter;
    private LinearLayoutManager locationLayoutManager;
    private LinearLayoutManager agencyLayoutManager;
    private LinearLayoutManager rocketLayoutManager;
    private boolean locationShowing = false;
    private boolean agencyShowing = false;
    private boolean rocketShowing = false;

    public static LaunchFilterDialog newInstance() {
        return new LaunchFilterDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_launch_filter, container,
                false);

        // get the views and attach the listener

        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.location_group)
    public void onLocationGroupClicked() {
        if (locationShowing) {
            locationShowing = !locationShowing;
            TransitionManager.beginDelayedTransition(constraintLayout);
            locationRecycler.setVisibility(View.GONE);
        } else {
            if (agencyShowing) {
                agencyShowing = !agencyShowing;
                agencyRecycler.setVisibility(View.GONE);
            }
            if (rocketShowing) {
                rocketShowing = !rocketShowing;
                rocketRecycler.setVisibility(View.GONE);
            }
            locationShowing = !locationShowing;
            List<FilterItem> filterItemList = new ArrayList<>();
            filterItemList.add(new FilterItem("Cape Canaveral", Prefs.getBoolean("cape_canaveral_switch", true), "cape_canaveral_switch"));
            filterItemList.add(new FilterItem("Vandenberg", Prefs.getBoolean("vandenberg_switch", true), "vandenberg_switch"));
            filterItemList.add(new FilterItem("Cape Canaveral", Prefs.getBoolean("cape_canaveral_switch", true), "cape_canaveral_switch"));
            filterItemList.add(new FilterItem("Vandenberg", Prefs.getBoolean("vandenberg_switch", true), "vandenberg_switch"));
            filterItemList.add(new FilterItem("Cape Canaveral", Prefs.getBoolean("cape_canaveral_switch", true), "cape_canaveral_switch"));
            filterItemList.add(new FilterItem("Vandenberg", Prefs.getBoolean("vandenberg_switch", true), "vandenberg_switch"));
            filterItemList.add(new FilterItem("Cape Canaveral", Prefs.getBoolean("cape_canaveral_switch", true), "cape_canaveral_switch"));
            filterItemList.add(new FilterItem("Vandenberg", Prefs.getBoolean("vandenberg_switch", true), "vandenberg_switch"));
            filterItemList.add(new FilterItem("Cape Canaveral", Prefs.getBoolean("cape_canaveral_switch", true), "cape_canaveral_switch"));
            filterItemList.add(new FilterItem("Vandenberg", Prefs.getBoolean("vandenberg_switch", true), "vandenberg_switch"));
            filterItemList.add(new FilterItem("Cape Canaveral", Prefs.getBoolean("cape_canaveral_switch", true), "cape_canaveral_switch"));
            filterItemList.add(new FilterItem("Vandenberg", Prefs.getBoolean("vandenberg_switch", true), "vandenberg_switch"));
            filterItemList.add(new FilterItem("Cape Canaveral", Prefs.getBoolean("cape_canaveral_switch", true), "cape_canaveral_switch"));
            filterItemList.add(new FilterItem("Vandenberg", Prefs.getBoolean("vandenberg_switch", true), "vandenberg_switch"));
            filterItemList.add(new FilterItem("Cape Canaveral", Prefs.getBoolean("cape_canaveral_switch", true), "cape_canaveral_switch"));
            filterItemList.add(new FilterItem("Vandenberg", Prefs.getBoolean("vandenberg_switch", true), "vandenberg_switch"));
            filterItemList.add(new FilterItem("Cape Canaveral", Prefs.getBoolean("cape_canaveral_switch", true), "cape_canaveral_switch"));
            filterItemList.add(new FilterItem("Vandenberg", Prefs.getBoolean("vandenberg_switch", true), "vandenberg_switch"));


            locationLayoutManager = new LinearLayoutManager(getContext());
            locationAdapter = new SelectorAdapter(getContext());
            locationAdapter.addItems(filterItemList);
            locationRecycler.setLayoutManager(locationLayoutManager);
            locationRecycler.setAdapter(locationAdapter);
            TransitionManager.beginDelayedTransition(constraintLayout);
            locationRecycler.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.agency_group)
    public void onAgencyGroupClicked() {
        if (agencyShowing) {
            agencyShowing = !agencyShowing;
            TransitionManager.beginDelayedTransition(constraintLayout);
            agencyRecycler.setVisibility(View.GONE);
        } else {
            if (locationShowing) {
                locationShowing = !locationShowing;
                locationRecycler.setVisibility(View.GONE);
            }
            if (rocketShowing) {
                rocketShowing = !rocketShowing;
                rocketRecycler.setVisibility(View.GONE);
            }
            agencyShowing = !agencyShowing;
            List<FilterItem> filterItemList = new ArrayList<>();
            filterItemList.add(new FilterItem("SpaceX", Prefs.getBoolean("spacex_switch", true), "spacex_switch"));
            filterItemList.add(new FilterItem("ULA", Prefs.getBoolean("ula_switch", true), "ula_switch"));
            filterItemList.add(new FilterItem("SpaceX", Prefs.getBoolean("spacex_switch", true), "spacex_switch"));
            filterItemList.add(new FilterItem("ULA", Prefs.getBoolean("ula_switch", true), "ula_switch"));
            filterItemList.add(new FilterItem("SpaceX", Prefs.getBoolean("spacex_switch", true), "spacex_switch"));
            filterItemList.add(new FilterItem("ULA", Prefs.getBoolean("ula_switch", true), "ula_switch"));
            filterItemList.add(new FilterItem("SpaceX", Prefs.getBoolean("spacex_switch", true), "spacex_switch"));
            filterItemList.add(new FilterItem("ULA", Prefs.getBoolean("ula_switch", true), "ula_switch"));
            filterItemList.add(new FilterItem("SpaceX", Prefs.getBoolean("spacex_switch", true), "spacex_switch"));
            filterItemList.add(new FilterItem("ULA", Prefs.getBoolean("ula_switch", true), "ula_switch"));
            filterItemList.add(new FilterItem("SpaceX", Prefs.getBoolean("spacex_switch", true), "spacex_switch"));
            filterItemList.add(new FilterItem("ULA", Prefs.getBoolean("ula_switch", true), "ula_switch"));
            filterItemList.add(new FilterItem("SpaceX", Prefs.getBoolean("spacex_switch", true), "spacex_switch"));
            filterItemList.add(new FilterItem("ULA", Prefs.getBoolean("ula_switch", true), "ula_switch"));
            filterItemList.add(new FilterItem("SpaceX", Prefs.getBoolean("spacex_switch", true), "spacex_switch"));
            filterItemList.add(new FilterItem("ULA", Prefs.getBoolean("ula_switch", true), "ula_switch"));
            filterItemList.add(new FilterItem("SpaceX", Prefs.getBoolean("spacex_switch", true), "spacex_switch"));
            filterItemList.add(new FilterItem("ULA", Prefs.getBoolean("ula_switch", true), "ula_switch"));


            agencyLayoutManager = new LinearLayoutManager(getContext());
            agencyAdapter = new SelectorAdapter(getContext());
            agencyAdapter.addItems(filterItemList);
            agencyRecycler.setLayoutManager(agencyLayoutManager);
            agencyRecycler.setAdapter(agencyAdapter);
            TransitionManager.beginDelayedTransition(constraintLayout);
            agencyRecycler.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.rocket_group)
    public void onRocketGroupClicked() {
        if (rocketShowing) {
            rocketShowing = !rocketShowing;
            TransitionManager.beginDelayedTransition(constraintLayout);
            rocketRecycler.setVisibility(View.GONE);
        } else {
            if (locationShowing) {
                locationShowing = !locationShowing;
                locationRecycler.setVisibility(View.GONE);
            }
            if (agencyShowing) {
                agencyShowing = !agencyShowing;
                agencyRecycler.setVisibility(View.GONE);
            }
            rocketShowing = !rocketShowing;
            List<FilterItem> filterItemList = new ArrayList<>();
            filterItemList.add(new FilterItem("Falcon 9", Prefs.getBoolean("falcon_switch", true), "falcon_switch"));
            filterItemList.add(new FilterItem("Delta IV", Prefs.getBoolean("delta_IV_switch", true), "delta_iv_switch"));
            filterItemList.add(new FilterItem("Falcon 9", Prefs.getBoolean("falcon_switch", true), "falcon_switch"));
            filterItemList.add(new FilterItem("Delta IV", Prefs.getBoolean("delta_IV_switch", true), "delta_iv_switch"));
            filterItemList.add(new FilterItem("Falcon 9", Prefs.getBoolean("falcon_switch", true), "falcon_switch"));
            filterItemList.add(new FilterItem("Delta IV", Prefs.getBoolean("delta_IV_switch", true), "delta_iv_switch"));
            filterItemList.add(new FilterItem("Falcon 9", Prefs.getBoolean("falcon_switch", true), "falcon_switch"));
            filterItemList.add(new FilterItem("Delta IV", Prefs.getBoolean("delta_IV_switch", true), "delta_iv_switch"));
            filterItemList.add(new FilterItem("Falcon 9", Prefs.getBoolean("falcon_switch", true), "falcon_switch"));
            filterItemList.add(new FilterItem("Delta IV", Prefs.getBoolean("delta_IV_switch", true), "delta_iv_switch"));
            filterItemList.add(new FilterItem("Falcon 9", Prefs.getBoolean("falcon_switch", true), "falcon_switch"));
            filterItemList.add(new FilterItem("Delta IV", Prefs.getBoolean("delta_IV_switch", true), "delta_iv_switch"));
            filterItemList.add(new FilterItem("Falcon 9", Prefs.getBoolean("falcon_switch", true), "falcon_switch"));
            filterItemList.add(new FilterItem("Delta IV", Prefs.getBoolean("delta_IV_switch", true), "delta_iv_switch"));
            filterItemList.add(new FilterItem("Falcon 9", Prefs.getBoolean("falcon_switch", true), "falcon_switch"));
            filterItemList.add(new FilterItem("Delta IV", Prefs.getBoolean("delta_IV_switch", true), "delta_iv_switch"));
            filterItemList.add(new FilterItem("Falcon 9", Prefs.getBoolean("falcon_switch", true), "falcon_switch"));
            filterItemList.add(new FilterItem("Delta IV", Prefs.getBoolean("delta_IV_switch", true), "delta_iv_switch"));
            filterItemList.add(new FilterItem("Falcon 9", Prefs.getBoolean("falcon_switch", true), "falcon_switch"));
            filterItemList.add(new FilterItem("Delta IV", Prefs.getBoolean("delta_IV_switch", true), "delta_iv_switch"));
            filterItemList.add(new FilterItem("Falcon 9", Prefs.getBoolean("falcon_switch", true), "falcon_switch"));
            filterItemList.add(new FilterItem("Delta IV", Prefs.getBoolean("delta_IV_switch", true), "delta_iv_switch"));


            rocketLayoutManager = new LinearLayoutManager(getContext());
            rocketAdapter = new SelectorAdapter(getContext());
            rocketAdapter.addItems(filterItemList);
            rocketRecycler.setLayoutManager(rocketLayoutManager);
            rocketRecycler.setAdapter(rocketAdapter);
            TransitionManager.beginDelayedTransition(constraintLayout);
            rocketRecycler.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.apply)
    public void onApplyClicked() {
    }

    @OnClick(R.id.cancel)
    public void onCancelClicked() {
    }
}