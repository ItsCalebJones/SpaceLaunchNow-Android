package me.calebjones.spacelaunchnow.starship.ui.upcoming;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.prefs.ThemeHelper;
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;
import me.calebjones.spacelaunchnow.data.models.main.dashboards.Starship;
import me.calebjones.spacelaunchnow.events.list.EventRecyclerViewAdapter;
import me.calebjones.spacelaunchnow.starship.StarshipDashboardViewModel;
import me.spacelaunchnow.starship.R;
import me.spacelaunchnow.starship.R2;

/**
 * A fragment representing the Starship Dashboard
 */
public class StarshipUpcomingFragment extends BaseFragment {

    @BindView(R2.id.launch_recycler)
    RecyclerView launchRecycler;
    @BindView(R2.id.starship_dashboard_coordinator)
    CoordinatorLayout starshipDashboardCoordinator;
    @BindView(R2.id.combined_stateful_layout)
    SimpleStatefulLayout combinedStatefulLayout;
    @BindView(R2.id.upcoming_switch)
    MaterialButtonToggleGroup upcomingSwitch;
    @BindView(R2.id.upcomingButton)
    Button upcomingButton;
    @BindView(R2.id.previousButton)
    Button previousButton;

    private Unbinder unbinder;
    private StarshipDashboardViewModel model;
    private CombinedAdapter adapter;
    private EventRecyclerViewAdapter eventRecyclerViewAdapter;
    private boolean showUpcoming = true;
    private Starship starship;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StarshipUpcomingFragment() {
    }

    @SuppressWarnings("unused")
    public static StarshipUpcomingFragment newInstance() {
        return new StarshipUpcomingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName("Starship Dashboard Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starship_dashboard_upcoming, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        model = ViewModelProviders.of(getParentFragment()).get(StarshipDashboardViewModel.class);
        model.getStarshipDashboard().observe(this, this::viewRefreshed);


        upcomingSwitch.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.upcomingButton) {
                    showUpcoming = true;
                    updateViews(starship, false);
                } else if (checkedId == R.id.previousButton) {
                    showUpcoming = false;
                    updateViews(starship, false);
                }
            }
        });

        return view;
    }

    private void viewRefreshed(Starship starship){
        updateViews(starship, true);
    }


    private void updateViews(Starship starship, boolean refreshed) {
        launchRecycler.smoothScrollToPosition(0);
        this.starship = starship;
        ArrayList<Object> combinedObjects = new ArrayList<>();


        if (showUpcoming) {
            if (this.starship.getUpcomingObjects().getEvents().size() > 0) {
                combinedObjects.addAll(this.starship.getUpcomingObjects().getEvents());
            }

            if (this.starship.getUpcomingObjects().getLaunches().size() > 0) {
                combinedObjects.addAll(this.starship.getUpcomingObjects().getLaunches());
            }

            if (combinedObjects.size() > 0) {
                combinedStatefulLayout.showContent();
            } else {
                combinedStatefulLayout.showEmpty();
            }
            combinedObjects = sortMultiClassList(combinedObjects, true);
        } else {
            if (this.starship.getPreviousObjects().getEvents().size() > 0) {
                combinedObjects.addAll(this.starship.getPreviousObjects().getEvents());
            }

            if (this.starship.getPreviousObjects().getLaunches().size() > 0) {
                combinedObjects.addAll(this.starship.getPreviousObjects().getLaunches());
            }

            if (combinedObjects.size() > 0) {
                combinedStatefulLayout.showContent();
            } else {
                combinedStatefulLayout.showEmpty();
            }

            combinedObjects = sortMultiClassList(combinedObjects, false);
        }

        if (refreshed) {
            adapter = new CombinedAdapter(getContext(), ThemeHelper.isDarkMode(getActivity()));
            launchRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            launchRecycler.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
            launchRecycler.setAdapter(adapter);
        } else {
            adapter.clear();
        }
        adapter.addItems(combinedObjects);
    }

    public ArrayList<Object> sortMultiClassList(ArrayList<Object> yourList, boolean ascending) {

        if (ascending) {
            Collections.sort(yourList, (Comparator<Object>) (o1, o2) -> {
                if (o1 instanceof LaunchList && o2 instanceof LaunchList) {
                    return ((LaunchList) o1).getNet().compareTo(((LaunchList) o2).getNet());
                } else if (o1 instanceof LaunchList && o2 instanceof Event) {
                    return ((LaunchList) o1).getNet().compareTo(((Event) o2).getDate());
                } else if (o1 instanceof Event && o2 instanceof LaunchList) {
                    return ((Event) o1).getDate().compareTo(((LaunchList) o2).getNet());
                } else if (o1 instanceof Event && o2 instanceof Event) {
                    return ((Event) o1).getDate().compareTo(((Event) o2).getDate());
                } else {
                    throw new IllegalArgumentException("Don't know how to compare");
                }
            });
        } else {
            Collections.sort(yourList, (Comparator<Object>) (o1, o2) -> {
                if (o2 instanceof LaunchList && o1 instanceof LaunchList) {
                    return ((LaunchList) o2).getNet().compareTo(((LaunchList) o1).getNet());
                } else if (o2 instanceof LaunchList && o1 instanceof Event) {
                    return ((LaunchList) o2).getNet().compareTo(((Event) o1).getDate());
                } else if (o2 instanceof Event && o1 instanceof LaunchList) {
                    return ((Event) o2).getDate().compareTo(((LaunchList) o1).getNet());
                } else if (o2 instanceof Event && o1 instanceof Event) {
                    return ((Event) o2).getDate().compareTo(((Event) o1).getDate());
                } else {
                    throw new IllegalArgumentException("Don't know how to compare");
                }
            });
        }
        return yourList;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
