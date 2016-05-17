package me.calebjones.spacelaunchnow.ui.fragment.launches;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.maps.MapView;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.LaunchBigAdapter;
import me.calebjones.spacelaunchnow.content.adapter.LaunchSmallAdapter;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.VehicleDataService;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class NextLaunchFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.van_switch)
    AppCompatCheckBox vanSwitch;
    @Bind(R.id.ples_switch)
    AppCompatCheckBox plesSwitch;
    @Bind(R.id.KSC_switch)
    AppCompatCheckBox kscSwitch;
    @Bind(R.id.cape_switch)
    AppCompatCheckBox capeSwitch;
    @Bind(R.id.nasa_switch)
    AppCompatCheckBox nasaSwitch;
    @Bind(R.id.spacex_switch)
    AppCompatCheckBox spacexSwitch;
    @Bind(R.id.roscosmos_switch)
    AppCompatCheckBox roscosmosSwitch;
    @Bind(R.id.ula_switch)
    AppCompatCheckBox ulaSwitch;
    @Bind(R.id.arianespace_switch)
    AppCompatCheckBox arianespaceSwitch;
    @Bind(R.id.casc_switch)
    AppCompatCheckBox cascSwitch;
    @Bind(R.id.isro_switch)
    AppCompatCheckBox isroSwitch;
    @Bind(R.id.all_switch)
    AppCompatCheckBox customSwitch;

    private View view;
    private RecyclerView mRecyclerView;
    private LaunchBigAdapter adapter;
    private LaunchSmallAdapter smallAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private View color_reveal;
    private FloatingActionButton FABMenu;
    private Menu mMenu;
    private RealmResults<LaunchRealm> rocketLaunches;
    private ListPreferences sharedPreference;
    private SwitchPreferences switchPreferences;
    private SharedPreferences sharedPref;
    private Context context;
    private Realm realm;
    private boolean active;
    private boolean switchChanged;
    private boolean cardSizeSmall;

    public NextLaunchFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreference = ListPreferences.getInstance(getActivity().getApplication());
        switchPreferences = SwitchPreferences.getInstance(getActivity().getApplication());
    }

    public void showCaseView() {
        if (NextLaunchFragment.this.isVisible()) {
            Button customButton = (Button) getLayoutInflater(null).inflate(R.layout.view_custom_button, null);
            ViewTarget pinMenuItem = new ViewTarget(R.id.action_alert, getActivity());
            ShowcaseView.Builder builder = new ShowcaseView.Builder(getActivity())
                    .withNewStyleShowcase()
                    .setTarget(pinMenuItem)
                    .setContentTitle("Launch Filtering")
                    .setContentText("Only receive notifications for launches that you care about.");
            if (sharedPreference.getNightMode()) {
                builder.setStyle(R.style.ShowCaseThemeDark).replaceEndButton(customButton).hideOnTouchOutside().build();
            } else {
                builder.setStyle(R.style.ShowCaseThemeLight).replaceEndButton(customButton).hideOnTouchOutside().build();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = getActivity().getApplicationContext();
        final int color;
        active = false;

        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        cardSizeSmall = sharedPref.getBoolean("card_size_small", false);
        if (cardSizeSmall) {
            smallAdapter = new LaunchSmallAdapter(getActivity());
        } else {
            if (adapter == null) {
                adapter = new LaunchBigAdapter(getActivity());
            }
        }

        if (sharedPreference.getNightMode()) {
            color = R.color.darkPrimary;
        } else {
            color = R.color.colorPrimary;
        }

        sharedPreference = ListPreferences.getInstance(context);

        if (!BuildConfig.DEBUG) {
            if (!BuildConfig.DEBUG) {
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("NextLaunchFragment")
                        .putContentType("Fragment"));
            }
        }

        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        LayoutInflater lf = getActivity().getLayoutInflater();
        view = lf.inflate(R.layout.fragment_upcoming, container, false);
        ButterKnife.bind(this, view);

        setUpSwitches();
        color_reveal = view.findViewById(R.id.color_reveal);
        color_reveal.setBackgroundColor(ContextCompat.getColor(context, color));
        FABMenu = (FloatingActionButton) view.findViewById(R.id.menu);
        FABMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpSwitches();
                if (!active) {
                    switchChanged = false;
                    active = true;
                    mSwipeRefreshLayout.setEnabled(false);
                    FABMenu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        showView();
                    } else {
                        color_reveal.setVisibility(View.VISIBLE);
                    }
                } else {
                    active = false;
                    FABMenu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_filter));
                    mSwipeRefreshLayout.setEnabled(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        hideView();
                    } else {
                        color_reveal.setVisibility(View.INVISIBLE);
                    }
                    if (switchChanged) {
                        refreshView();
                    }
                }
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            linearLayoutManager = new LinearLayoutManager(context.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }

        if (cardSizeSmall) {
            mRecyclerView.setAdapter(smallAdapter);
        } else {
            mRecyclerView.setAdapter(adapter);
        }

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);

        /*Set up Pull to refresh*/
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    private void refreshView() {
        if (cardSizeSmall) {
            smallAdapter.clear();
        } else {
            adapter.clear();
        }

        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet) && rocketLaunches.size() == 1) {
            linearLayoutManager = new LinearLayoutManager(context.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            if (cardSizeSmall) {
                mRecyclerView.setAdapter(smallAdapter);
            } else {
                mRecyclerView.setAdapter(adapter);
            }
        } else if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            if (cardSizeSmall) {
                mRecyclerView.setAdapter(smallAdapter);
            } else {
                mRecyclerView.setAdapter(adapter);
            }
        }
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        if (cardSizeSmall) {
            adapter.addItems(rocketLaunches);
            adapter.notifyDataSetChanged();
        } else {
            adapter.addItems(rocketLaunches);
            adapter.notifyDataSetChanged();
        }
    }


    public void displayLaunches() {
        int size = Integer.parseInt(sharedPref.getString("upcoming_value", "5"));
        RealmQuery<LaunchRealm> query = realm.where(LaunchRealm.class);
        RealmResults<LaunchRealm> results = query.findAll().sort("netstamp", Sort.DESCENDING);
        rocketLaunches = results;

        if (rocketLaunches != null) {
            if (rocketLaunches.size() == 0) {
                Timber.v("Next launches is empty...");
            } else {
                if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet) && rocketLaunches.size() == 1) {
                    linearLayoutManager = new LinearLayoutManager(context.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                    if (cardSizeSmall) {
                        mRecyclerView.setAdapter(smallAdapter);
                    } else {
                        mRecyclerView.setAdapter(adapter);
                    }
                } else if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
                    layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(layoutManager);
                    if (cardSizeSmall) {
                        mRecyclerView.setAdapter(smallAdapter);
                    } else {
                        mRecyclerView.setAdapter(adapter);
                    }
                }
                if (cardSizeSmall) {
                    adapter.clear();
                    adapter.addItems(rocketLaunches);
                } else {
                    adapter.clear();
                    adapter.addItems(rocketLaunches);
                }
            }
        }
    }

    private void setUpSwitches() {
        customSwitch.setChecked(switchPreferences.getAllSwitch());
        nasaSwitch.setChecked(switchPreferences.getSwitchNasa());
        spacexSwitch.setChecked(switchPreferences.getSwitchSpaceX());
        roscosmosSwitch.setChecked(switchPreferences.getSwitchRoscosmos());
        ulaSwitch.setChecked(switchPreferences.getSwitchULA());
        arianespaceSwitch.setChecked(switchPreferences.getSwitchArianespace());
        cascSwitch.setChecked(switchPreferences.getSwitchCASC());
        isroSwitch.setChecked(switchPreferences.getSwitchISRO());
        plesSwitch.setChecked(switchPreferences.getSwitchPles());
        capeSwitch.setChecked(switchPreferences.getSwitchCape());
        vanSwitch.setChecked(switchPreferences.getSwitchVan());
        kscSwitch.setChecked(switchPreferences.getSwitchKSC());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void hideView() {

        // get the center for the clipping circle
        int x = (int) (FABMenu.getX() + FABMenu.getWidth() / 2);
        int y = (int) (FABMenu.getY() + FABMenu.getHeight() / 2);

        // get the initial radius for the clipping circle
        int initialRadius = Math.max(color_reveal.getWidth(), color_reveal.getHeight());

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(color_reveal, x, y, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                color_reveal.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showView() {

        // get the center for the clipping circle
        int x = (int) (FABMenu.getX() + FABMenu.getWidth() / 2);
        int y = (int) (FABMenu.getY() + FABMenu.getHeight() / 2);

        // get the final radius for the clipping circle
        int finalRadius = Math.max(color_reveal.getWidth(), color_reveal.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(color_reveal, x, y, 0, finalRadius);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

//                showAlertDialog();
            }
        });

        color_reveal.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void fetchData() {
        this.sharedPreference.removeUpcomingLaunches();
        Timber.v("Sending GET_UP_LAUNCHES");
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
        super.onResume();
        setTitle();
        Timber.d("OnResume!");
        if (Utils.getVersionCode(context) != switchPreferences.getVersionCode()) {
            showChangelogSnackbar();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_FAILURE_UP_LAUNCHES);

        getActivity().registerReceiver(nextLaunchReceiver, intentFilter);

        if (adapter != null) {
            HashSet<MapView> maps = adapter.getMaps();
            for (MapView map : maps) {
                map.onResume();
            }
        }
        if (this.sharedPreference.getUpcomingFirstBoot()) {
            this.sharedPreference.setUpcomingFirstBoot(false);
            Timber.d("Upcoming Launch Fragment: First Boot.");
            if (this.sharedPreference.getLaunchesUpcoming() != null) {
                displayLaunches();
            }
        } else {
            Timber.d("Upcoming Launch Fragment: Not First Boot.");
            displayLaunches();
        }
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(nextLaunchReceiver);
        if (adapter != null) {
            HashSet<MapView> maps = adapter.getMaps();
            for (MapView map : maps) {
                map.onPause();
            }
        }
        super.onPause();
    }

    @Override
    public void onLowMemory(){
        if (adapter != null) {
            HashSet<MapView> maps = adapter.getMaps();
            for (MapView map : maps) {
                map.onLowMemory();
            }
        }
        super.onLowMemory();
    }

    @Override
    public void onDestroy(){
        if (adapter != null) {
            HashSet<MapView> maps = adapter.getMaps();
            for (MapView map : maps) {
                map.onDestroy();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (adapter != null) {
            HashSet<MapView> maps = adapter.getMaps();
            for (MapView map : maps) {
                map.onSaveInstanceState(outState);
            }
        }
        super.onSaveInstanceState(outState);
    }

    private void showChangelogSnackbar() {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Updated to version " + Utils.getVersionName(context), Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        Timber.v("Current Version code: %s", switchPreferences.getVersionCode());
                        if (switchPreferences.getVersionCode() <= 43) {
                            showCaseView();
                        }
                        switchPreferences.setVersionCode(Utils.getVersionCode(context));
                    }
                })
                .setAction("Changelog", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MainActivity) getActivity()).showWhatsNew();
                    }
                });
        snackbar.show();
    }

    private final BroadcastReceiver nextLaunchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.v("Received: %s", intent.getAction());
            if (intent.getAction().equals(Strings.ACTION_SUCCESS_UP_LAUNCHES)){
                onFinishedRefreshing();
            } else if (intent.getAction().equals(Strings.ACTION_FAILURE_UP_LAUNCHES)){
                hideLoading();
                showErrorSnackbar(intent.getStringExtra("error"));
            }
        }
    };

    private void showErrorSnackbar(String error) {
        Snackbar
                .make(coordinatorLayout, "Error - " + error, Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .show();
    }

    @Override
    public void onRefresh() {
        fetchData();
    }

    private void setTitle() {
        ((MainActivity) getActivity()).setActionBarTitle("Space Launch Now");
    }

    public void onFinishedRefreshing() {
        displayLaunches();
        mSwipeRefreshLayout.setRefreshing(false);
        hideLoading();
    }

    public void hideLoading() {
        CircularProgressView progressView = (CircularProgressView)
                view.findViewById(R.id.progress_View);
        progressView.setVisibility(View.GONE);
        progressView.resetAnimation();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    //Currently only used to debug
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //
        if (BuildConfig.DEBUG) {
            menu.clear();
            inflater.inflate(R.menu.debug_menu, menu);
            mMenu = menu;
        } else {
            menu.clear();
            inflater.inflate(R.menu.next_menu, menu);
            mMenu = menu;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.debug_add_launch) {
            if (sharedPreference.isDebugEnabled()) {
                sharedPreference.setDebugLaunch(false);
            } else {
                sharedPreference.setDebugLaunch(true);
            }
            RealmResults<LaunchRealm> results = realm.where(LaunchRealm.class).findAll();
            realm.beginTransaction();
            results.deleteAllFromRealm();
            realm.commitTransaction();
            Timber.v("%s", sharedPreference.isDebugEnabled());
            Snackbar.make(coordinatorLayout, "Debug: " + sharedPreference.isDebugEnabled(), Snackbar.LENGTH_LONG).show();
            onRefresh();
        } else if (id == R.id.debug_next_launch) {
            Intent nextIntent = new Intent(getActivity(), LaunchDataService.class);
            nextIntent.setAction(Strings.ACTION_UPDATE_NEXT_LAUNCH);
            getActivity().startService(nextIntent);
        } else if (id == R.id.action_alert) {
            if (!active) {
                switchChanged = false;
                active = true;
                mSwipeRefreshLayout.setEnabled(false);
                FABMenu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    showView();
                } else {
                    color_reveal.setVisibility(View.VISIBLE);
                }
            } else {
                active = false;
                FABMenu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_alert));
                mSwipeRefreshLayout.setEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    hideView();
                } else {
                    color_reveal.setVisibility(View.INVISIBLE);
                }
                if (switchChanged) {
                    refreshView();
                }
            }
        } else if (id == R.id.debug_vehicle) {
            Intent rocketIntent = new Intent(context, VehicleDataService.class);
            rocketIntent.setAction(Strings.ACTION_GET_VEHICLES_DETAIL);
            context.startService(rocketIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.v("onDestroyView");
        ButterKnife.unbind(this);
    }

    private void confirm() {
        if (!switchChanged) {
            FABMenu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check));
        }
        switchChanged = true;
    }

    @OnClick(R.id.nasa_switch)
    public void nasa_switch() {
        confirm();
        switchPreferences.setSwitchNasa(!switchPreferences.getSwitchNasa());
    }


    @OnClick(R.id.spacex_switch)
    public void spacex_switch() {
        confirm();
        switchPreferences.setSwitchSpaceX(!switchPreferences.getSwitchSpaceX());
    }

    @OnClick(R.id.roscosmos_switch)
    public void roscosmos_switch() {
        confirm();
        switchPreferences.setSwitchRoscosmos(!switchPreferences.getSwitchRoscosmos());
    }

    @OnClick(R.id.ula_switch)
    public void ula_switch() {
        confirm();
        switchPreferences.setSwitchULA(!switchPreferences.getSwitchULA());
    }

    @OnClick(R.id.arianespace_switch)
    public void arianespace_switch() {
        confirm();
        switchPreferences.setSwitchArianespace(!switchPreferences.getSwitchArianespace());
    }

    @OnClick(R.id.casc_switch)
    public void casc_switch() {
        confirm();
        switchPreferences.setSwitchCASC(!switchPreferences.getSwitchCASC());
    }

    @OnClick(R.id.isro_switch)
    public void isro_switch() {
        confirm();
        switchPreferences.setSwitchISRO(!switchPreferences.getSwitchISRO());
    }

    @OnClick(R.id.KSC_switch)
    public void KSC_switch() {
        confirm();
        switchPreferences.setSwitchKSC(!switchPreferences.getSwitchKSC());
    }

    @OnClick(R.id.ples_switch)
    public void ples_switch() {
        confirm();
        switchPreferences.setSwitchPles(!switchPreferences.getSwitchPles());
    }

    @OnClick(R.id.van_switch)
    public void van_switch() {
        confirm();
        switchPreferences.setSwitchVan(!switchPreferences.getSwitchVan());
    }

    @OnClick(R.id.cape_switch)
    public void cape_switch() {
        confirm();
        switchPreferences.setSwitchCape(!switchPreferences.getSwitchCape());
    }

    @OnClick(R.id.all_switch)
    public void all_switch() {
        confirm();
        switchPreferences.setAllSwitch(!switchPreferences.getAllSwitch());
        setUpSwitches();
    }
}
