package me.calebjones.spacelaunchnow.ui.main.missions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseFragment;
import me.calebjones.spacelaunchnow.content.data.DataManager;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Mission;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.views.SnackbarHandler;
import timber.log.Timber;

public class MissionFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private View view, empty;
    private RecyclerView mRecyclerView;
    private MissionAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RealmResults<Mission> missionList;
    private ListPreferences sharedPreference;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private android.content.SharedPreferences SharedPreferences;
    private FloatingActionButton menu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.sharedPreference = ListPreferences.getInstance(getActivity().getApplicationContext());
        adapter = new MissionAdapter(getActivity().getApplicationContext(), getActivity());
        setScreenName("Mission Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        LayoutInflater lf = getActivity().getLayoutInflater();

        view = lf.inflate(R.layout.fragment_missions, container, false);
        empty = view.findViewById(R.id.empty_launch_root);
        menu = (FloatingActionButton) view.findViewById(R.id.menu);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_view);
        swipeRefreshLayout.setOnRefreshListener(this);
        menu.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    private RealmChangeListener callback = new RealmChangeListener<RealmResults<Mission>>() {
        @Override
        public void onChange(RealmResults<Mission> results) {
            Timber.v("Data changed - size: %s", results.size());
            adapter.clear();

            if (results.size() > 0) {
                adapter.addItems(results);
                missionList = results;
            } else {
                showErrorSnackbar("Unable to load missions.");
            }
            hideLoading();
        }
    };
    
    private void displayMissions() {
        showLoading();
        missionList = getRealm().where(Mission.class)
                .findAllSortedAsync("name", Sort.ASCENDING);
        missionList.addChangeListener(callback);
    }

    private final BroadcastReceiver missionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.v("Received: %s", intent.getAction());
            hideLoading();
            if (intent.getAction().equals(Constants.ACTION_GET_MISSION)) {
                if (intent.getExtras().getBoolean("result")) {
                    onFinishedRefreshing();
                } else {
                    hideLoading();
                    SnackbarHandler.showErrorSnackbar(context, coordinatorLayout, intent.getStringExtra("error"));
                }
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        Timber.d("OnResume!");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_GET_MISSION);

        getActivity().registerReceiver(missionReceiver, intentFilter);
        displayMissions();

        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(missionReceiver);
        super.onPause();
    }

    @Override
    public void onRefresh() {
        missionList.removeChangeListeners();
        fetchData();
    }

    public void onFinishedRefreshing() {
        displayMissions();
        hideLoading();
    }

    public void fetchData() {
        Timber.d("Sending service intent!");
        showLoading();
        DataManager dataManager = new DataManager(getActivity());
        dataManager.getAllMissions();
        showSnackbar("Updating mission data... this may take a few seconds.");
    }

    private void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    private void hideLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showErrorSnackbar(String error) {
        Snackbar
                .make(coordinatorLayout, "Error - " + error, Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .show();
    }

    private void showSnackbar(String message) {
        Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.mission_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            Analytics.from(getActivity()).sendButtonClicked("Mission - Refresh Clicked");
            showLoading();
            fetchData();
            return true;
        }

        if (id == R.id.return_home) {
            Analytics.from(getActivity()).sendButtonClicked("Mission - Home Clicked");
            mRecyclerView.scrollToPosition(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement our filter logic
        final List<Mission> filteredModelList = filter(missionList, query);
        Analytics.from(getActivity()).sendSearchEvent(query, "Mission", filteredModelList.size());
        adapter.animateTo(filteredModelList);
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

    private List<Mission> filter(List<Mission> models, String query) {
        query = query.toLowerCase();
        final List<Mission> filteredModelList = new ArrayList<>();
        for (Mission model : models) {
            final String missionName = model.getName().toLowerCase();
            final String summaryText = model.getDescription().toLowerCase();

            if (missionName.contains(query) || summaryText.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
