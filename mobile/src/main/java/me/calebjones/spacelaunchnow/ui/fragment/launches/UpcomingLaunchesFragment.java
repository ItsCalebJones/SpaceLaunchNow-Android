package me.calebjones.spacelaunchnow.ui.fragment.launches;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.ListAdapter;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.interfaces.QueryBuilder;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.ui.activity.MainActivity;
import me.calebjones.spacelaunchnow.ui.customviews.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.ui.fragment.BaseFragment;
import me.calebjones.spacelaunchnow.utils.SnackbarHandler;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingLaunchesFragment extends BaseFragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private RecyclerView mRecyclerView;
    private ListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RealmResults<LaunchRealm> launchRealms;
    private SwitchPreferences switchPreferences;
    private ListPreferences listPreference;
    private SharedPreferences SharedPreferences;
    private FloatingActionMenu menu;
    private FloatingActionButton agency, vehicle, country, location, reset;
    private int mScrollOffset = 4;
    private Context context;
    private CoordinatorLayout coordinatorLayout;

    private static final Field sChildFragmentManagerField;

    public UpcomingLaunchesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getContext();

        listPreference = ListPreferences.getInstance(this.context);

        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("UpcomingLaunchesFragment")
                    .putContentType("Fragment"));
        }

        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        SharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.listPreference = ListPreferences.getInstance(getContext());
        this.switchPreferences = SwitchPreferences.getInstance(getContext());
        adapter = new ListAdapter(getContext());

        LayoutInflater lf = getActivity().getLayoutInflater();

        view = lf.inflate(R.layout.fragment_launches, container, false);

        agency = (FloatingActionButton) view.findViewById(R.id.agency);
        vehicle = (FloatingActionButton) view.findViewById(R.id.vehicle);
        country = (FloatingActionButton) view.findViewById(R.id.location);
        location = (FloatingActionButton) view.findViewById(R.id.launch_location);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);

        reset = (FloatingActionButton) view.findViewById(R.id.reset);
        menu = (FloatingActionMenu) view.findViewById(R.id.menu);

                /*Set up Pull to refresh*/
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.launches_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
                if (Math.abs(dy) > mScrollOffset) {
                    if (dy > 0) {
                        menu.hideMenu(true);
                    } else {
                        menu.showMenu(true);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        CircularProgressView progressView = (CircularProgressView)
                view.findViewById(R.id.progress_View);
        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();

        setUpFab();
        return view;
    }

    public static UpcomingLaunchesFragment newInstance(String text) {

        UpcomingLaunchesFragment u = new UpcomingLaunchesFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        u.setArguments(b);

        return u;
    }

    private void setUpFab() {
        menu.setClosedOnTouchOutside(true);

        createCustomAnimation();

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPreferences.resetAllUpFilters();
                if (switchPreferences.isUpFiltered()) {
                    switchPreferences.setUpFiltered(false);
                    listPreference.resetUpTitle();
                    loadData();
                    setTitle();
                }
            }
        });

        agency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAgencyDialog();
            }
        });
        vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVehicleDialog();
            }
        });
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountryDialog();
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationDialog();
            }
        });
    }

    private void showCountryDialog() {
        new MaterialDialog.Builder(getContext())
                .title("Select a Country")
                .content("Check an country below, to remove all filters use reset icon in the toolbar.")
                .items(R.array.country)
                .buttonRippleColorRes(R.color.colorAccentLight)
                .itemsCallbackMultiChoice(switchPreferences.getUpCountryFiltered(), new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        switchPreferences.setUpCountryFiltered(which);
                        ArrayList<String> keyArray = new ArrayList<>();
                        for (int i = 0; i < which.length; i++) {
                            keyArray.add(text[i].toString());
                        }
                        if (keyArray.size() > 0) {
                            switchPreferences.setUpCountryFilteredArray(keyArray);
                            switchPreferences.setUpFiltered(true);
                        } else {
                            switchPreferences.resetCountryUpFilters();
                        }
                        fetchDataFiltered();
                        menu.toggle(false);
                        return true;
                    }
                })
                .positiveText("Filter")
                .negativeText("Close")
                .icon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher))
                .show();
    }

    private void showLocationDialog() {
        new MaterialDialog.Builder(getContext())
                .title("Select a Location")
                .content("Check an location below, to remove all filters use reset icon in the toolbar.")
                .items(R.array.location)
                .buttonRippleColorRes(R.color.colorAccentLight)
                .itemsCallbackMultiChoice(switchPreferences.getUpLocationFiltered(), new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        switchPreferences.setUpLocationFiltered(which);
                        ArrayList<String> keyArray = new ArrayList<>();
                        for (int i = 0; i < which.length; i++) {
                            keyArray.add(text[i].toString());
                        }
                        if (keyArray.size() > 0) {
                            switchPreferences.setUpLocationFilteredArray(keyArray);
                            switchPreferences.setUpFiltered(true);
                        } else {
                            switchPreferences.resetLocationUpFilters();
                        }
                        fetchDataFiltered();
                        menu.toggle(false);
                        return true;
                    }
                })
                .positiveText("Filter")
                .negativeText("Close")
                .icon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher))
                .show();
    }

    private void showAgencyDialog() {
        new MaterialDialog.Builder(getContext())
                .title("Select an Agency")
                .content("Check an agency below, to remove all filters use reset icon in the toolbar.")
                .items(R.array.agencies)
                .buttonRippleColorRes(R.color.colorAccentLight)
                .itemsCallbackMultiChoice(switchPreferences.getUpAgencyFiltered(), new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        switchPreferences.setUpAgencyFiltered(which);
                        ArrayList<String> keyArray = new ArrayList<>();
                        for (int i = 0; i < which.length; i++) {
                            keyArray.add(text[i].toString());
                        }
                        if (keyArray.size() > 0) {
                            switchPreferences.setUpAgencyFilterArray(keyArray);
                            switchPreferences.setUpFiltered(true);
                        } else {
                            switchPreferences.resetAgencyUpFilters();
                        }
                        fetchDataFiltered();
                        menu.toggle(false);
                        return true;
                    }
                })
                .positiveText("Filter")
                .negativeText("Close")
                .icon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher))
                .show();
    }

    private void showVehicleDialog() {
        new MaterialDialog.Builder(getContext())
                .title("Select a Launch Vehicle")
                .content("Check a vehicle below, to remove all filters use reset icon in the toolbar.")
                .items(R.array.vehicles)
                .buttonRippleColorRes(R.color.colorAccentLight)
                .itemsCallbackMultiChoice(switchPreferences.getUpVehicleFiltered(), new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        switchPreferences.setUpVehicleFiltered(which);
                        ArrayList<String> keyArray = new ArrayList<>();
                        for (int i = 0; i < which.length; i++) {
                            keyArray.add(text[i].toString());
                        }
                        if (keyArray.size() > 0) {
                            switchPreferences.setUpVehicleFilteredArray(keyArray);
                            switchPreferences.setUpFiltered(true);
                        } else {
                            switchPreferences.resetVehicleUpFilters();
                        }
                        fetchDataFiltered();
                        menu.toggle(false);
                        return true;
                    }
                })
                .positiveText("Filter")
                .negativeText("Close")
                .icon(ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher))
                .show();
    }

    private void createCustomAnimation() {

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleY", 1.0f, 0.2f);

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(menu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                menu.getMenuIconView().setImageResource(menu.isOpened()
                        ? R.drawable.ic_sort : R.drawable.ic_close);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menu.setIconToggleAnimatorSet(set);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void fetchDataFiltered() {
        loadData();
        rebuildTitle();
    }

    private RealmChangeListener callback = new RealmChangeListener<RealmResults<LaunchRealm>>() {
        @Override
        public void onChange(RealmResults<LaunchRealm> results) {
            Timber.v("Data changed - size: %s", results.size());
            adapter.clear();

            if (results.size() > 0) {
                results.sort("net", Sort.ASCENDING);
                adapter.addItems(results);
            }
            hideLoading();
            launchRealms.removeChangeListeners();
        }
    };


    public void loadData() {
        launchRealms = QueryBuilder.buildUpQueryAsync(context, getRealm());
        launchRealms.addChangeListener(callback);
    }

    public void getUpcomingLaunchData() {
        Timber.d("Sending GET_UP_LAUNCHES");
        Intent intent = new Intent(getContext(), LaunchDataService.class);
        intent.setAction(Strings.ACTION_GET_UP_LAUNCHES);
        getContext().startService(intent);
        getRealm().removeAllChangeListeners();
    }


    private void showLoading() {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void hideLoading() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        CircularProgressView progressView = (CircularProgressView)
                view.findViewById(R.id.progress_View);
        progressView.setVisibility(View.GONE);
        progressView.resetAnimation();
    }

    private final BroadcastReceiver nextLaunchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.v("Received: %s", intent.getAction());
            hideLoading();
            if (intent.getAction().equals(Strings.ACTION_SUCCESS_UP_LAUNCHES)) {
                loadData();
            } else if (intent.getAction().equals(Strings.ACTION_FAILURE_UP_LAUNCHES)) {
                SnackbarHandler.showErrorSnackbar(context, coordinatorLayout, intent);
            }
        }
    };

    private void rebuildTitle() {
        String title = "";
        ArrayList<String> agency = switchPreferences.getUpAgencyFilteredArray();
        ArrayList<String> country = switchPreferences.getUpCountryFilteredArray();
        ArrayList<String> location = switchPreferences.getUpLocationFilteredArray();
        ArrayList<String> vehicle = switchPreferences.getUpVehicleFilteredArray();

        if (agency != null) {
            for (String key : agency) {
                if (title.length() == 0) {
                    title = key;
                } else {
                    title = title + " | " + key;
                }
            }
        }

        if (country != null) {
            for (String key : country) {
                if (title.length() == 0) {
                    title = key;
                } else {
                    title = title + " | " + key;
                }
            }
        }


        if (location != null) {
            for (String key : location) {
                if (title.length() == 0) {
                    title = key;
                } else {
                    title = title + " | " + key;
                }
            }
        }

        if (vehicle != null) {
            for (String key : vehicle) {
                if (title.length() == 0) {
                    title = key;
                } else {
                    title = title + " | " + key;
                }
            }
        }

        if (title.length() > 0) {
            listPreference.setUpTitle(title);
        } else {
            listPreference.resetUpTitle();
        }
        setTitle();
    }

    @Override
    public void onResume() {
        Timber.d("OnResume!");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_FAILURE_UP_LAUNCHES);

        getActivity().registerReceiver(nextLaunchReceiver, intentFilter);
        setTitle();
        loadData();
        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(nextLaunchReceiver);
        super.onPause();
    }

    public void onRefresh() {
        launchRealms.removeChangeListeners();
        if (!switchPreferences.isUpFiltered()) {
            getUpcomingLaunchData();
        } else {
            switchPreferences.setUpFiltered(false);
            switchPreferences.resetAllUpFilters();
            loadData();
            setTitle();
        }
    }

    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Crashlytics.logException(e);
            Timber.e("Error getting mChildFragmentManager field %s", e);
        }
        sChildFragmentManagerField = f;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (sChildFragmentManagerField != null) {
            try {
                sChildFragmentManagerField.set(this, null);
            } catch (Exception e) {
                Crashlytics.logException(e);
                e.getLocalizedMessage();
                Timber.e("Error setting mChildFragmentManager field %s ", e);
            }
        }
    }


    //Currently only used to debug
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.upcoming_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    private void setTitle() {
        ((MainActivity) getActivity()).setActionBarTitle(listPreference.getUpTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            onRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        switchPreferences.setPrevFiltered(true);
        // Here is where we are going to implement our filter logic
        Answers.getInstance().logSearch(new SearchEvent()
                .putQuery(query));
        final List<LaunchRealm> filteredModelList = filter(launchRealms, query);
        if (filteredModelList.size() > 100) {
            adapter.clear();
            adapter.addItems(filteredModelList);
        } else {
            adapter.animateTo(filteredModelList);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.scrollToPosition(0);
            }
        }, 500);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<LaunchRealm> filter(List<LaunchRealm> models, String query) {
        query = query.toLowerCase();

        final List<LaunchRealm> filteredModelList = new ArrayList<>();
        for (LaunchRealm model : models) {
            final String name = model.getName().toLowerCase();
            final String rocketName = model.getRocket().getName().toLowerCase();
            final String locationName = model.getLocation().getName().toLowerCase();
            String missionName = null;
            String missionDescription = null;
            String agencyName = null;

            if (model.getRocket().getAgencies() != null && model.getRocket().getAgencies().size() > 0){
                agencyName = model.getRocket().getAgencies().get(0).getName().toLowerCase();
            }

            if (model.getMissions().size() > 0) {
                missionName = model.getMissions().get(0).getName().toLowerCase();
                missionDescription = model.getMissions().get(0).getDescription().toLowerCase();
            }

            if (rocketName.contains(query) || locationName.contains(query) || (agencyName != null && agencyName.contains(query)) || name.contains(query)) {
                filteredModelList.add(model);
            } else {
                if (missionName != null && (missionName.contains(query) || missionDescription.contains(query))) {
                    filteredModelList.add(model);
                }
            }

        }
        return filteredModelList;
    }
}
