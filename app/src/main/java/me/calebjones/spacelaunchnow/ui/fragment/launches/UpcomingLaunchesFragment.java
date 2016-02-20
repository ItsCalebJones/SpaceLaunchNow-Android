package me.calebjones.spacelaunchnow.ui.fragment.launches;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.content.adapter.LaunchCompactAdapter;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.LaunchBigAdapter;
import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingLaunchesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private View view;
    private RecyclerView mRecyclerView;
    private LaunchCompactAdapter adapter;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SlideInBottomAnimationAdapter animatorAdapter;
    private List<Launch> rocketLaunches;
    private SharedPreference sharedPreference;
    private SharedPreferences SharedPreferences;
    private Context context;

    private static final Field sChildFragmentManagerField;

    public UpcomingLaunchesFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.sharedPreference = SharedPreference.getInstance(getContext());
        this.rocketLaunches = new ArrayList();
        adapter = new LaunchCompactAdapter(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getContext();

        sharedPreference = SharedPreference.getInstance(this.context);

        if (!BuildConfig.DEBUG){
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("UpcomingLaunchesFragment")
                    .putContentType("Fragment"));
        }

        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        LayoutInflater lf = getActivity().getLayoutInflater();

        view = lf.inflate(R.layout.fragment_launches, container, false);
        View menu = view.findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        if (this.sharedPreference.getUpcomingFirstBoot()) {
            this.sharedPreference.setUpcomingFirstBoot(false);
            Timber.d("Upcoming Launch Fragment: First Boot.");
            if(this.sharedPreference.getLaunchesUpcoming().size() == 0){
                showLoading();
                fetchData();
            } else {
                this.rocketLaunches.clear();
                displayLaunches();
            }
        } else  {
            Timber.d("Upcoming Launch Fragment: Not First Boot.");
            this.rocketLaunches.clear();
            displayLaunches();
        }
        return view;
    }

    public void displayLaunches() {
        this.rocketLaunches = this.sharedPreference.getLaunchesUpcoming();

        if (rocketLaunches.size() == 0) {
            Timber.v("Upcoming launches is empty...fetching.");
            fetchData();
        } else {
            adapter.clear();
            List<Launch> goList = new ArrayList<>();
            List<Launch> noList = new ArrayList<>();
            for (int i = 0; i < rocketLaunches.size(); i++ ){
                if (rocketLaunches.get(i).getStatus() == 1){
                    goList.add(rocketLaunches.get(i));
                } else {
                    noList.add(rocketLaunches.get(i));
                }
            }
            goList.addAll(noList);
            adapter.addItems(goList);
        }
    }

    public void fetchData() {
        this.sharedPreference.removeUpcomingLaunches();
        Intent intent = new Intent(getContext(), LaunchDataService.class);
        intent.setAction(Strings.ACTION_GET_UP_LAUNCHES);
        Timber.d("Sending service intent!");
        getContext().startService(intent);
    }


    public void showLoading() {
        CircularProgressView progressView = (CircularProgressView)
                view.findViewById(R.id.progress_View);
        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
    }


    @Override
    public void onResume() {
        setTitle();
        Timber.d("OnResume!");
        super.onResume();
    }

    public void onRefresh() {
        fetchData();
    }

    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
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
        ((MainActivity) getActivity()).setActionBarTitle("Space Launch Now");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh){
            onRefresh();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement our filter logic
        final List<Launch> filteredModelList = filter(rocketLaunches, query);
        if (query.length() > 3){
            if (!BuildConfig.DEBUG){
                Answers.getInstance().logSearch(new SearchEvent()
                        .putQuery(query));
            }
        }
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

    private List<Launch> filter(List<Launch> models, String query) {
        query = query.toLowerCase();

        final List<Launch> filteredModelList = new ArrayList<>();
        for (Launch model : models) {
            final String name = model.getName().toLowerCase();
            final String rocketName = model.getRocket().getName().toLowerCase();
            final String locationName = model.getLocation().getName().toLowerCase();
            String missionName;

            //If pad and agency exist add it to location, otherwise get whats always available
            if (model.getLocation().getPads().size() > 0 && model.getLocation().getPads().
                    get(0).getAgencies().size() > 0){
                missionName = model.getLocation().getPads().get(0).getAgencies().get(0).getName() + " " + (model.getRocket().getName());
            } else {
                missionName = model.getRocket().getName();
            }
            missionName = missionName.toLowerCase();

            if (rocketName.contains(query) || locationName.contains(query) || missionName.contains(query) || name.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
