package me.calebjones.spacelaunchnow.ui.fragment.launches;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.calendar.CalendarSyncService;
import me.calebjones.spacelaunchnow.content.adapter.CardBigAdapter;
import me.calebjones.spacelaunchnow.content.adapter.CardSmallAdapter;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.VehicleDataService;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.supporter.Products;
import me.calebjones.spacelaunchnow.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.ui.activity.DownloadActivity;
import me.calebjones.spacelaunchnow.ui.activity.MainActivity;
import me.calebjones.spacelaunchnow.ui.fragment.BaseFragment;
import me.calebjones.spacelaunchnow.utils.FileUtils;
import me.calebjones.spacelaunchnow.utils.SnackbarHandler;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;

public class NextLaunchFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.van_switch)
    AppCompatCheckBox vanSwitch;
    @BindView(R.id.ples_switch)
    AppCompatCheckBox plesSwitch;
    @BindView(R.id.KSC_switch)
    AppCompatCheckBox kscSwitch;
    @BindView(R.id.cape_switch)
    AppCompatCheckBox capeSwitch;
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

    private View view;
    private RecyclerView mRecyclerView;
    private CardBigAdapter adapter;
    private CardSmallAdapter smallAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CoordinatorLayout coordinatorLayout;
    private View color_reveal;
    private FloatingActionButton FABMenu;
    private Menu mMenu;
    private RealmResults<LaunchRealm> launchRealms;
    private ListPreferences sharedPreference;
    private SwitchPreferences switchPreferences;
    private SharedPreferences sharedPref;
    private Context context;

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
            if (sharedPreference.isNightModeActive(context)) {
                builder.setStyle(R.style.ShowCaseThemeDark).replaceEndButton(customButton).build();
            } else {
                builder.setStyle(R.style.ShowCaseThemeLight).replaceEndButton(customButton).build();
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
            smallAdapter = new CardSmallAdapter(getActivity());
        } else {
            if (adapter == null) {
                adapter = new CardBigAdapter(getActivity());
            }
        }

        if (sharedPreference.isNightModeActive(context)) {
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
                        showLoading();
                        displayLaunches();
                        if (switchPreferences.getCalendarStatus()) {
                            CalendarSyncService.startActionResync(context);
                        }
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


        //If preference is for small card, landscape tablets get three others get two.
        if (cardSizeSmall) {
            if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
                layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            } else {
                layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            }
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(smallAdapter);
        } else {
            if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet)) {
                layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(layoutManager);
            } else {
                linearLayoutManager = new LinearLayoutManager(context.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(linearLayoutManager);
            }
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
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!getRealm().isClosed()) {
            launchRealms.removeChangeListener(callback); // remove a particular listener
            // or
            launchRealms.removeChangeListeners(); // remove all registered listeners
        }
    }

    private RealmChangeListener callback = new RealmChangeListener<RealmResults<LaunchRealm>>() {
        @Override
        public void onChange(RealmResults<LaunchRealm> results) {
            Timber.v("Data changed - size: %s", results.size());

            int size = Integer.parseInt(sharedPref.getString("upcoming_value", "5"));
            if (cardSizeSmall) {
                smallAdapter.clear();
            } else {
                adapter.clear();
            }

            if (results.size() >= size) {
                setLayoutManager(size);
                if (cardSizeSmall) {
                    smallAdapter.addItems(results.subList(0, size));
                } else {
                    adapter.addItems(results.subList(0, size));
                }
            } else if (results.size() > 0) {
                setLayoutManager(size);
                if (cardSizeSmall) {
                    smallAdapter.addItems(results);
                } else {
                    adapter.addItems(results);
                }
            } else {
                adapter.clear();
            }
            hideLoading();
            launchRealms.removeChangeListeners();
        }
    };

    public void displayLaunches() {
        Timber.v("loadLaunches - showLoading");
        Date date = new Date();

        if (switchPreferences.getAllSwitch()) {
            launchRealms = getRealm().where(LaunchRealm.class)
                    .greaterThanOrEqualTo("net", date)
                    .findAllSortedAsync("net", Sort.ASCENDING);
            launchRealms.addChangeListener(callback);
            Timber.v("loadLaunches - Realm query created.");
        } else {
            filterLaunchRealm();
            Timber.v("loadLaunches - Filtered Realm query created.");
        }
    }

    private void setLayoutManager(int size) {
        if (getResources().getBoolean(R.bool.landscape) && getResources().getBoolean(R.bool.isTablet) && (launchRealms != null && launchRealms.size() == 1 || size == 1)) {
            linearLayoutManager = new LinearLayoutManager(context.getApplicationContext(),
                    LinearLayoutManager.VERTICAL, false);
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
    }

    private void filterLaunchRealm() {
        launchRealms = QueryBuilder.buildSwitchQueryAsync(context, getRealm());
        launchRealms.addChangeListener(callback);
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
            }
        });

        color_reveal.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void fetchData() {
        Timber.v("Sending GET_UP_LAUNCHES");
        Intent intent = new Intent(getContext(), LaunchDataService.class);
        intent.setAction(Strings.ACTION_GET_UP_LAUNCHES);
        getContext().startService(intent);
        Timber.d("Sending service intent!");
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
        Timber.d("OnResume!");
        setTitle();

        //First install
        if (switchPreferences.getVersionCode() == 0) {
            switchPreferences.setVersionCode(Utils.getVersionCode(context));
            showCaseView();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showCaseView();
                }
            }, 1000);

            //build 87 is where Realm change happened
        } else if (switchPreferences.getVersionCode() <= 87) {
            Intent intent = new Intent(context, DownloadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            //Upgrade post Realm change.
        } else if (Utils.getVersionCode(context) != switchPreferences.getVersionCode()) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showChangelogSnackbar();
                }
            }, 1000);
            switchPreferences.setVersionCode(Utils.getVersionCode(context));
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_FAILURE_UP_LAUNCHES);

        getActivity().registerReceiver(nextLaunchReceiver, intentFilter);

        showLoading();
        displayLaunches();
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.v("onPause");
        getActivity().unregisterReceiver(nextLaunchReceiver);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showCaseView();
                                }
                            }, 1000);
                        }
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
            if (intent.getAction().equals(Strings.ACTION_SUCCESS_UP_LAUNCHES)) {
                onFinishedRefreshing();
            } else if (intent.getAction().equals(Strings.ACTION_FAILURE_UP_LAUNCHES)) {
                hideLoading();
                SnackbarHandler.showErrorSnackbar(context, coordinatorLayout, intent);
            }
        }
    };

    @Override
    public void onRefresh() {
        fetchData();
        launchRealms.removeChangeListener(callback);
    }

    private void setTitle() {
        ((MainActivity) getActivity()).setActionBarTitle("Space Launch Now");
    }

    public void onFinishedRefreshing() {
        hideLoading();
        displayLaunches();
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

        if (id == R.id.debug_menu){
            String[] strings = new String[]{"Next Launch", "Supporter", "Debug Launches", "Log", "Background Sync", "Vehicles"};
            new MaterialDialog.Builder(getActivity())
                    .title("Debug Menu")
                    .items(strings)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            switch (which){
                                case 0:
                                    Intent nextIntent = new Intent(getActivity(), LaunchDataService.class);
                                    nextIntent.setAction(Strings.ACTION_UPDATE_NEXT_LAUNCH);
                                    getActivity().startService(nextIntent);
                                    break;
                                case 1:
                                    Realm realm = Realm.getDefaultInstance();
                                    if (sharedPreference.isDebugSupporterEnabled()) {
                                        sharedPreference.setDebugSupporter(false);
                                        realm.beginTransaction();
                                        realm.delete(Products.class);
                                        realm.commitTransaction();
                                        Snackbar.make(coordinatorLayout, "Supporter: " + sharedPreference.isDebugSupporterEnabled(),
                                                Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        sharedPreference.setDebugSupporter(true);
                                        realm.beginTransaction();
                                        realm.copyToRealm(SupporterHelper.getProduct(SupporterHelper.SKU_TWO_DOLLAR));
                                        realm.commitTransaction();
                                        Snackbar.make(coordinatorLayout, "Supporter: " + sharedPreference.isDebugSupporterEnabled(),
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 2:
                                    if (sharedPreference.isDebugEnabled()) {
                                        sharedPreference.setDebugLaunch(false);
                                    } else {
                                        sharedPreference.setDebugLaunch(true);
                                    }
                                    RealmResults<LaunchRealm> results = getRealm().where(LaunchRealm.class).findAll();

                                    getRealm().beginTransaction();
                                    results.deleteAllFromRealm();
                                    getRealm().commitTransaction();

                                    Timber.v("%s", sharedPreference.isDebugEnabled());
                                    Snackbar.make(coordinatorLayout, "Debug: " + sharedPreference.isDebugEnabled(),
                                            Snackbar.LENGTH_LONG).show();
                                    context.startService(new Intent(context, LaunchDataService.class)
                                            .setAction(Strings.ACTION_GET_ALL_WIFI));
                                    break;
                                case 3:
                                    try {
                                        byte[] data = FileUtils.readFile(FileUtils.getSuccessFile(context));
                                        if (data == null || data.length == 0) {
                                            Toast.makeText(context, "Null file.", Toast.LENGTH_SHORT).show();
                                        } else {

                                            new MaterialDialog.Builder(getActivity())
                                                    .title("History")
                                                    .content(new String(data))
                                                    .negativeText("Delete File")
                                                    .positiveText("Ok")
                                                    .neutralText("Save File")
                                                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            File textFile = new File(context.getCacheDir(), "success.txt");
                                                            Uri uriForFile = FileProvider.getUriForFile(context, "me.calebjones.spacelaunchnow", textFile);
                                                            context.grantUriPermission("me.calebjones.spacelaunchnow", uriForFile, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                                            Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                                                                    .setStream(uriForFile) // uri from FileProvider
                                                                    .setType("text/plain")
                                                                    .getIntent()
                                                                    .setAction(Intent.ACTION_SEND) //Change if needed
                                                                    .setDataAndType(uriForFile, "text/plain")
                                                                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                                            startActivity(Intent.createChooser(intent, "Save File"));
                                                        }
                                                    })
                                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            try {
                                                                FileUtils.delete(FileUtils.getSuccessFile(getContext()));
                                                            } catch (IOException e) {
                                                                Timber.e(e.getLocalizedMessage());
                                                            }
                                                        }
                                                    })
                                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .show();
                                        }

                                    } catch (IOException e) {
                                        Timber.e(e.getLocalizedMessage());
                                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case 4:
                                    Intent background = new Intent(getActivity(), LaunchDataService.class);
                                    background.setAction(Strings.ACTION_UPDATE_BACKGROUND);
                                    getActivity().startService(background);
                                    break;
                                case 5:
                                    Intent rocketIntent = new Intent(context, VehicleDataService.class);
                                    rocketIntent.setAction(Strings.ACTION_GET_VEHICLES_DETAIL);
                                    context.startService(rocketIntent);
                                    break;
                            }

                            return true;
                        }
                    })
                    .positiveText("Ok")
                    .show();

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
                    showLoading();
                    displayLaunches();
                    if (switchPreferences.getCalendarStatus()) {
                        CalendarSyncService.startActionResync(context);
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.v("onDestroyView");
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

    @OnClick(R.id.cape_switch)
    public void cape_switch() {
        confirm();
        switchPreferences.setSwitchCape(!switchPreferences.getSwitchCape());
        checkAll();
    }

    @OnClick(R.id.all_switch)
    public void all_switch() {
        confirm();
        switchPreferences.setAllSwitch(!switchPreferences.getAllSwitch());
        setUpSwitches();
    }
}


