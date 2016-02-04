package me.calebjones.spacelaunchnow.ui.fragment.launches;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Agency;
import me.calebjones.spacelaunchnow.content.models.Location;
import me.calebjones.spacelaunchnow.content.models.LocationAgency;
import me.calebjones.spacelaunchnow.content.models.Mission;
import me.calebjones.spacelaunchnow.content.models.Pad;
import me.calebjones.spacelaunchnow.content.models.Rocket;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.LaunchAdapter;
import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class LaunchesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private RecyclerView mRecyclerView;
    private LaunchAdapter adapter;
    private StaggeredGridLayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SlideInBottomAnimationAdapter animatorAdapter;
    private List<Launch> rocketLaunches;
    private SharedPreference sharedPreference;
    private SharedPreferences SharedPreferences;
    private static final Field sChildFragmentManagerField;
    private Context context;

    public LaunchesFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.sharedPreference = SharedPreference.getInstance(getContext());
        this.rocketLaunches = new ArrayList();
        adapter = new LaunchAdapter(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getContext();

        sharedPreference = SharedPreference.getInstance(this.context);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("LaunchesFragment")
                .putContentType("Fragment"));

        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        LayoutInflater lf = getActivity().getLayoutInflater();

        view = lf.inflate(R.layout.fragment_launches, container, false);
        View menu = view.findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int topRowVerticalPostion = (mRecyclerView == null || mRecyclerView
                        .getChildCount() == 0) ? 0 : mRecyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(dx == 0 && topRowVerticalPostion >= 0);
            }
        });
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        animatorAdapter = new SlideInBottomAnimationAdapter(adapter);
        animatorAdapter.setDuration(350);
        mRecyclerView.setAdapter(animatorAdapter);

        /*Set up Pull to refresh*/
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

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
            adapter.addItems(rocketLaunches);
        }
    }

    public void recreate(){
        recreate();
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

    @Override
    public void onRefresh() {
        adapter.clear();
        showLoading();
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
        //
//        if (BuildConfig.DEBUG) {
//            menu.clear();
//            inflater.inflate(R.menu.debug_menu, menu);
//        }
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

        if (id == R.id.debug_add_launch){
            if (sharedPreference.getDebugLaunch()){
                sharedPreference.setDebugLaunch(false);
            } else {
                sharedPreference.setDebugLaunch(true);
            }
            onRefresh();
        }
        return super.onOptionsItemSelected(item);
    }

}
