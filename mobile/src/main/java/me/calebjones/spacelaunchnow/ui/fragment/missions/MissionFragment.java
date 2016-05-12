package me.calebjones.spacelaunchnow.ui.fragment.missions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.maps.MapView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.MissionAdapter;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.Mission;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.MissionDataService;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class MissionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private View view, empty;
    private RecyclerView mRecyclerView;
    private MissionAdapter adapter;
    private StaggeredGridLayoutManager staggeredLayoutManager;
    private LinearLayoutManager layoutManager;
    private List<Mission> missionList;
    private ListPreferences sharedPreference;
    private CoordinatorLayout coordinatorLayout;
    private android.content.SharedPreferences SharedPreferences;
    private FloatingActionButton menu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.sharedPreference = ListPreferences.getInstance(getActivity().getApplicationContext());
        this.missionList = new ArrayList();
        adapter = new MissionAdapter(getActivity().getApplicationContext(), getActivity());
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
        menu.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView hideView = (RecyclerView) view.findViewById(R.id.recycler_view_staggered);
        if (mRecyclerView.getVisibility() != View.VISIBLE) {
            hideView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);


                /*Set up Pull to refresh*/

        if (this.sharedPreference.getUpcomingFirstBoot()) {
            this.sharedPreference.setUpcomingFirstBoot(false);
            Timber.d("Mission Fragment: First Boot.");
            displayMissions();
        } else {
            Timber.d("Mission Fragment: Not First Boot.");
            displayMissions();
        }
        return view;
    }

    private void displayMissions() {
        this.missionList = this.sharedPreference.getMissionList();
        if (missionList != null) {
            if (missionList.size() == 0) {
                empty.setVisibility(View.VISIBLE);
            } else {
                empty.setVisibility(View.GONE);
                adapter.clear();
                adapter.addItems(missionList);
            }
        } else {
            empty.setVisibility(View.VISIBLE);
        }
    }

    private final BroadcastReceiver nextLaunchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.v("Received: %s", intent.getAction());
            if (intent.getAction().equals(Strings.ACTION_SUCCESS_MISSIONS)){
                onFinishedRefreshing();
            } else if (intent.getAction().equals(Strings.ACTION_FAILURE_MISSIONS)){
                hideLoading();
                showErrorSnackbar(intent.getStringExtra("error"));
            }
        }
    };

    @Override
    public void onResume() {
        Timber.d("OnResume!");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Strings.ACTION_SUCCESS_MISSIONS);
        intentFilter.addAction(Strings.ACTION_FAILURE_MISSIONS);

        getActivity().registerReceiver(nextLaunchReceiver, intentFilter);

        super.onResume();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(nextLaunchReceiver);
        super.onPause();
    }

    @Override
    public void onRefresh() {
        fetchData();
    }

    public void onFinishedRefreshing() {
        missionList.clear();
        displayMissions();
        hideLoading();
    }

    public void fetchData() {
        Timber.d("Sending service intent!");
        showLoading();
        getContext().startService(new Intent(getContext(), MissionDataService.class));
    }

    private void showLoading() {
        CircularProgressView progressView = (CircularProgressView)
                view.findViewById(R.id.progress_View);
        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
    }

    private void hideLoading() {
        CircularProgressView progressView = (CircularProgressView)
                view.findViewById(R.id.progress_View);
        progressView.setVisibility(View.GONE);
        progressView.resetAnimation();
    }

    private void showErrorSnackbar(String error) {
        Snackbar
                .make(coordinatorLayout, "Error - " + error, Snackbar.LENGTH_LONG)
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
            showLoading();
            fetchData();
            return true;
        }

        if (id == R.id.return_home) {
            mRecyclerView.scrollToPosition(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement our filter logic
        final List<Mission> filteredModelList = filter(missionList, query);
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
