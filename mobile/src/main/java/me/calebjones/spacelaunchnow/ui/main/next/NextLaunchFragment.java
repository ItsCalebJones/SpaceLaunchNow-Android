package me.calebjones.spacelaunchnow.ui.main.next;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.mrapp.android.preference.activity.PreferenceActivity;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseFragment;
import me.calebjones.spacelaunchnow.content.data.callbacks.Callbacks;
import me.calebjones.spacelaunchnow.content.data.next.NextLaunchDataRepository;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.jobs.SyncCalendarJob;
import me.calebjones.spacelaunchnow.content.jobs.UpdateWearJob;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.ui.debug.DebugActivity;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import me.calebjones.spacelaunchnow.ui.settings.SettingsActivity;
import me.calebjones.spacelaunchnow.ui.settings.fragments.NotificationsFragment;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.views.SnackbarHandler;
import me.calebjones.spacelaunchnow.widget.launchcard.LaunchCardCompactManager;
import me.calebjones.spacelaunchnow.widget.launchcard.LaunchCardCompactWidgetProvider;
import me.calebjones.spacelaunchnow.widget.launchlist.LaunchListManager;
import me.calebjones.spacelaunchnow.widget.launchlist.LaunchListWidgetProvider;
import me.calebjones.spacelaunchnow.widget.wordtimer.LaunchWordTimerManager;
import me.calebjones.spacelaunchnow.widget.wordtimer.LaunchWordTimerWidgetProvider;
import timber.log.Timber;

public class NextLaunchFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.van_switch)
    AppCompatCheckBox vanSwitch;
    @BindView(R.id.ples_switch)
    AppCompatCheckBox plesSwitch;
    @BindView(R.id.KSC_switch)
    AppCompatCheckBox kscSwitch;
    @BindView(R.id.nasa_switch)
    AppCompatCheckBox nasaSwitch;
    @BindView(R.id.spacex_switch)
    AppCompatCheckBox spacexSwitch;
    @BindView(R.id.roscosmos_switch)
    AppCompatCheckBox roscosmosSwitch;
    @BindView(R.id.ula_switch)
    AppCompatCheckBox ulaSwitch;
    @BindView(R.id.arianespace_switch)
    AppCompatCheckBox arianespaceSwitch;
    @BindView(R.id.casc_switch)
    AppCompatCheckBox cascSwitch;
    @BindView(R.id.isro_switch)
    AppCompatCheckBox isroSwitch;
    @BindView(R.id.all_switch)
    AppCompatCheckBox customSwitch;
    @BindView(R.id.tbd_launch)
    SwitchCompat tbdLaunchSwitch;
    @BindView(R.id.persist_last_launch)
    SwitchCompat persistLastSwitch;
    @BindView(R.id.tbd_info)
    AppCompatImageView noGoInfo;
    @BindView(R.id.last_launch_info)
    AppCompatImageView lastLaunchInfo;
    @BindView(R.id.action_notification_settings)
    AppCompatButton notificationsSettings;
    @BindView(R.id.view_more_launches)
    AppCompatButton viewMoreLaunches;

    private View view;
    private RecyclerView mRecyclerView;
    private CardAdapter adapter;
    private StaggeredGridLayoutManager layoutManager;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private View color_reveal;
    private View no_data;
    private FloatingActionButton FABMenu;
    private Menu mMenu;
    private RealmResults<Launch> launchRealms;
    private ListPreferences sharedPreference;
    private SwitchPreferences switchPreferences;
    private SharedPreferences sharedPref;
    private Context context;
    private int preferredCount;
    private NextLaunchDataRepository nextLaunchDataRepository;
    private CallBackListener callBackListener;
    private boolean filterViewShowing;
    private boolean switchChanged;
    Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        sharedPreference = ListPreferences.getInstance(context);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        switchPreferences = SwitchPreferences.getInstance(context);
        nextLaunchDataRepository = new NextLaunchDataRepository(context, getRealm());
        setScreenName("Next Launch Fragment");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getActivity() is fully created in onActivityCreated and instanceOf differentiate it between different Activities
        if (getActivity() instanceof CallBackListener)
            callBackListener = (CallBackListener) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final int color;
        filterViewShowing = false;

        if (adapter == null) {
            adapter = new CardAdapter(context);
        }

        if (sharedPreference.isNightModeActive(context)) {
            color = R.color.darkPrimary;
        } else {
            color = R.color.colorPrimary;
        }

        sharedPreference = ListPreferences.getInstance(context);

        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        unbinder = ButterKnife.bind(this, view);

        setUpSwitches();
        no_data = view.findViewById(R.id.no_launches);
        color_reveal = view.findViewById(R.id.color_reveal);
        color_reveal.setBackgroundColor(ContextCompat.getColor(context, color));
        FABMenu = view.findViewById(R.id.menu);
        FABMenu.setOnClickListener(v -> checkFilter());
        if (switchPreferences.getNextFABHidden()) {
            FABMenu.setVisibility(View.GONE);
        } else {
            FABMenu.setVisibility(View.VISIBLE);
        }

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        //If preference is for small card, landscape tablets get three others get two.
        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            linearLayoutManager = new LinearLayoutManager(context.getApplicationContext(), RecyclerView.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
        mRecyclerView.setAdapter(adapter);


        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);

        /*Set up Pull to refresh*/
        mSwipeRefreshLayout = view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);

        //Enable no data by default
        no_data.setVisibility(View.VISIBLE);
        viewMoreLaunches.setVisibility(View.GONE);
        return view;
    }

    public boolean isFilterShown(){
        return filterViewShowing;
    }

    @Override
    public void onStart() {
        Timber.v("onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setLayoutManager(int size) {
        if (!isDetached() && isAdded()) {
            if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet) && (launchRealms != null && launchRealms.size() == 1 || size == 1)) {
                linearLayoutManager = new LinearLayoutManager(context.getApplicationContext(),
                        LinearLayoutManager.VERTICAL, false
                );
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
            } else if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
                layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(adapter);
            }
        } else if (isDetached()) {
            Timber.v("View is detached.");
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
        vanSwitch.setChecked(switchPreferences.getSwitchVan());
        kscSwitch.setChecked(switchPreferences.getSwitchKSC());
        tbdLaunchSwitch.setChecked(switchPreferences.getTBDSwitch());
        persistLastSwitch.setChecked(switchPreferences.getPersistSwitch());
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
            }
        });

        color_reveal.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void fetchData(boolean forceRefresh) {
        Timber.v("Sending GET_UP_LAUNCHES");
        preferredCount = 10;
        nextLaunchDataRepository.getNextUpcomingLaunches(preferredCount, forceRefresh, new Callbacks.NextLaunchesCallback() {
            @Override
            public void onLaunchesLoaded(RealmResults<Launch> launches) {
                updateAdapter(launches);
            }

            @Override
            public void onNetworkStateChanged(boolean refreshing) {
                showNetworkLoading(refreshing);
            }

            @Override
            public void onError(String message, @Nullable Throwable throwable) {
                if (throwable != null) {
                    Timber.e(throwable);
                } else {
                    Timber.e(message);
                    SnackbarHandler.showErrorSnackbar(context, coordinatorLayout, message);
                }
                SnackbarHandler.showErrorSnackbar(context, coordinatorLayout, message);
            }
        });
    }

    private void updateAdapter(RealmResults<Launch> launches) {
        adapter.clear();
        preferredCount = 10;
        if (launches.size() >= preferredCount) {
            no_data.setVisibility(View.GONE);
            viewMoreLaunches.setVisibility(View.VISIBLE);
            setLayoutManager(preferredCount);
            adapter.addItems(launches.subList(0, preferredCount));
            adapter.notifyDataSetChanged();

        } else if (launches.size() > 0) {
            no_data.setVisibility(View.GONE);
            viewMoreLaunches.setVisibility(View.VISIBLE);
            setLayoutManager(preferredCount);
            adapter.addItems(launches);
            adapter.notifyDataSetChanged();

        } else {
            no_data.setVisibility(View.VISIBLE);
            viewMoreLaunches.setVisibility(View.GONE);
            if (adapter != null) {
                adapter.clear();
            }
        }
    }

    private void showNetworkLoading(boolean loading) {
        if (loading) {
            showLoading();
        } else {
            hideLoading();
        }
    }

    private void showLoading() {
        Timber.v("Show Loading...");
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
    }

    private void hideLoading() {
        Timber.v("Hide Loading...");
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
    }


    @Override
    public void onResume() {
        super.onResume();
        Timber.v("onResume");
        setTitle();
        fetchData(false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getBoolean("SHOW_FILTERS")) {
                if (!filterViewShowing) {
                    new Handler().postDelayed(this::checkFilter, 500);
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        fetchData(true);
    }

    private void setTitle() {
        ((MainActivity) getActivity()).setActionBarTitle("Space Launch Now");
    }

    //Currently only used to debug
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (BuildConfig.DEBUG) {
            menu.clear();
            inflater.inflate(R.menu.debug_menu, menu);
            mMenu = menu;
        } else {
            menu.clear();
            inflater.inflate(R.menu.next_menu, menu);
            mMenu = menu;
        }

        if (switchPreferences.getNextFABHidden()) {
            MenuItem item = menu.findItem(R.id.action_FAB);
            item.setTitle("Show FAB");
        }

        if (SupporterHelper.isSupporter()) {
            mMenu.removeItem(R.id.action_supporter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.debug_menu) {
            Intent debugIntent = new Intent(getActivity(), DebugActivity.class);
            startActivity(debugIntent);

        } else if (id == R.id.action_alert) {
            checkFilter();
        } else if (id == R.id.action_FAB) {
            switchPreferences.setNextFABHidden(!switchPreferences.getNextFABHidden());
            if (switchPreferences.getNextFABHidden()) {
                item.setTitle("Show FAB");
                if (switchPreferences.getNextFABHidden()) {

                    FABMenu.setVisibility(View.GONE);
                }
            } else {
                item.setTitle("Hide FAB");
                if (!switchPreferences.getNextFABHidden()) {
                    FABMenu.setVisibility(View.VISIBLE);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void checkFilter() {

        if (!filterViewShowing) {
            Analytics.getInstance().sendButtonClicked("Show Launch filters.");
            switchChanged = false;
            filterViewShowing = true;
            mSwipeRefreshLayout.setEnabled(false);
            FABMenu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                showView();
            } else {
                color_reveal.setVisibility(View.VISIBLE);
            }
        } else {
            Analytics.getInstance().sendButtonClicked("Hide Launch filters.");
            filterViewShowing = false;
            FABMenu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_notifications_white));
            mSwipeRefreshLayout.setEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                hideView();
            } else {
                color_reveal.setVisibility(View.INVISIBLE);
            }
            if (switchChanged) {
                LaunchCardCompactManager launchCardCompactManager = new LaunchCardCompactManager(context);
                LaunchWordTimerManager launchWordTimerManager = new LaunchWordTimerManager(context);
                LaunchListManager launchListManager = new LaunchListManager(context);

                int cardIds[] = AppWidgetManager.getInstance(context)
                        .getAppWidgetIds(new ComponentName(context,
                                LaunchCardCompactWidgetProvider.class));

                for (int id : cardIds) {
                    launchCardCompactManager.updateAppWidget(id);
                }

                int timerIds[] = AppWidgetManager.getInstance(context)
                        .getAppWidgetIds(new ComponentName(context,
                                LaunchWordTimerWidgetProvider.class));

                for (int id : timerIds) {
                    launchWordTimerManager.updateAppWidget(id);
                }

                int listIds[] = AppWidgetManager.getInstance(context)
                        .getAppWidgetIds(new ComponentName(context,
                                LaunchListWidgetProvider.class));

                for (int id : listIds) {
                    launchListManager.updateAppWidget(id);
                }

                UpdateWearJob.scheduleJobNow();
                fetchData(true);
                if (switchPreferences.getCalendarStatus()) {
                    SyncCalendarJob.scheduleImmediately();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.v("onDestroyView");
        callBackListener = null;
        mSwipeRefreshLayout.setOnRefreshListener(null);
        unbinder.unbind();
    }

    private void confirm() {
        if (!switchChanged) {
            FABMenu.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check));
        }
        switchChanged = true;
    }

    private void checkAll() {
        if (switchPreferences.getAllSwitch()) {
            switchPreferences.setAllSwitch(false);
            customSwitch.setChecked(false);
        }
    }

    @OnClick(R.id.nasa_switch)
    public void nasa_switch() {
        confirm();
        switchPreferences.setSwitchNasa(!switchPreferences.getSwitchNasa());
        checkAll();
    }

    @OnClick(R.id.spacex_switch)
    public void spacex_switch() {
        confirm();
        switchPreferences.setSwitchSpaceX(!switchPreferences.getSwitchSpaceX());
        checkAll();
    }

    @OnClick(R.id.roscosmos_switch)
    public void roscosmos_switch() {
        confirm();
        switchPreferences.setSwitchRoscosmos(!switchPreferences.getSwitchRoscosmos());
        checkAll();
    }

    @OnClick(R.id.ula_switch)
    public void ula_switch() {
        confirm();
        switchPreferences.setSwitchULA(!switchPreferences.getSwitchULA());
        checkAll();
    }

    @OnClick(R.id.arianespace_switch)
    public void arianespace_switch() {
        confirm();
        switchPreferences.setSwitchArianespace(!switchPreferences.getSwitchArianespace());
        checkAll();
    }

    @OnClick(R.id.casc_switch)
    public void casc_switch() {
        confirm();
        switchPreferences.setSwitchCASC(!switchPreferences.getSwitchCASC());
        checkAll();
    }

    @OnClick(R.id.isro_switch)
    public void isro_switch() {
        confirm();
        switchPreferences.setSwitchISRO(!switchPreferences.getSwitchISRO());
        checkAll();
    }

    @OnClick(R.id.KSC_switch)
    public void KSC_switch() {
        confirm();
        switchPreferences.setSwitchKSC(!switchPreferences.getSwitchKSC());
        checkAll();
    }

    @OnClick(R.id.ples_switch)
    public void ples_switch() {
        confirm();
        switchPreferences.setSwitchPles(plesSwitch.isChecked());
        checkAll();
    }

    @OnClick(R.id.van_switch)
    public void van_switch() {
        confirm();
        switchPreferences.setSwitchVan(!switchPreferences.getSwitchVan());
        checkAll();
    }

    @OnClick(R.id.all_switch)
    public void all_switch() {
        confirm();
        switchPreferences.setAllSwitch(!switchPreferences.getAllSwitch());
        setUpSwitches();
    }

    @OnClick(R.id.tbd_launch)
    public void noGoSwitch() {
        confirm();
        switchPreferences.setNoGoSwitch(tbdLaunchSwitch.isChecked());
    }

    @OnClick(R.id.persist_last_launch)
    public void setPersistLastSwitch() {
        confirm();
        switchPreferences.setPersistLastSwitch(persistLastSwitch.isChecked());
    }

    @OnClick(R.id.action_notification_settings)
    public void onNotificationSettingsClicked() {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, NotificationsFragment.class.getName());
        intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
        startActivity(intent);
    }

    @OnClick({R.id.tbd_info, R.id.last_launch_info})
    public void onViewClicked(View view) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context);
        dialog.positiveText("Ok");
        switch (view.getId()) {
            case R.id.tbd_info:
                dialog.title(R.string.no_go).content(R.string.no_go_description).show();
                break;
            case R.id.last_launch_info:
                dialog.title(R.string.launch_info).content(R.string.launch_info_description).show();
                break;
        }
    }

    @OnClick({R.id.view_more_launches, R.id.view_more_launches2})
    public void onViewClicked() {
        callBackListener.onNavigateToLaunches();
    }

    public interface CallBackListener {
        void onNavigateToLaunches();// pass any parameter in your onCallBack which you want to return
    }
}


