package me.calebjones.spacelaunchnow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import io.fabric.sdk.android.Fabric;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.MissionDataService;
import me.calebjones.spacelaunchnow.content.services.VehicleDataService;
import me.calebjones.spacelaunchnow.ui.activity.SettingsActivity;
import me.calebjones.spacelaunchnow.ui.fragment.launches.NextLaunchFragment;
import me.calebjones.spacelaunchnow.ui.fragment.missions.MissionFragment;
import me.calebjones.spacelaunchnow.ui.fragment.launches.LaunchesViewPager;
import me.calebjones.spacelaunchnow.ui.fragment.vehicles.VehiclesViewPager;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.customtab.CustomTabActivityHelper;
import timber.log.Timber;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String NAV_ITEM_ID = "navItemId";
    private final Handler mDrawerActionHandler = new Handler();
    private LaunchesViewPager mlaunchesViewPager;
    private MissionFragment mMissionFragment;
    private NextLaunchFragment mUpcomingFragment;
    private VehiclesViewPager mVehicleViewPager;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private SharedPreferences sharedPref;
    private NavigationView navigationView;
    private static SharedPreference sharedPreference;
    private CustomTabActivityHelper customTabActivityHelper;
    private Context context;
    private Boolean bool;
    private BroadcastReceiver intentReceiver;

    private static final int REQUEST_CODE = 5467;

    private int mNavItemId;

    public int statusColor;

    public void mayLaunchUrl(Uri parse) {
        if (customTabActivityHelper.mayLaunchUrl(parse, null, null)) {
            Timber.v("mayLaunchURL Accepted - %s", parse.toString());
        } else {
            Timber.v("mayLaunchURL Denied - %s", parse.toString());
        }
    }

    class LaunchBroadcastReceiver extends BroadcastReceiver {
        LaunchBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Timber.v("Broadcast received...");
            if (intent != null) {
                String action = intent.getAction();
                Timber.d("Broadcast action : %s", action);
                if (Strings.ACTION_SUCCESS_UP_LAUNCHES.equals(action)) {
                    if (mNavItemId == R.id.menu_next_launch && !sharedPreference.getFirstBoot()) {
                        if (mUpcomingFragment != null) {
                            mUpcomingFragment.onFinishedRefreshing();
                        }
                    }
                    if (mNavItemId == R.id.menu_launches) {
                    if (mlaunchesViewPager != null) {
                            mlaunchesViewPager.restartViews(Strings.ACTION_SUCCESS_UP_LAUNCHES);
                        }
                    }
                } else if (Strings.ACTION_SUCCESS_PREV_LAUNCHES.equals(action)) {
                    if (mlaunchesViewPager != null) {
                        mlaunchesViewPager.restartViews(Strings.ACTION_SUCCESS_PREV_LAUNCHES);
                    }
                } else if (Strings.ACTION_SUCCESS_MISSIONS.equals(action)) {
                    if (mMissionFragment != null) {
                        if (mMissionFragment.isVisible()) {
                            mMissionFragment.onFinishedRefreshing();
                        }
                    }
                }
            }
        }
    }


    public MainActivity() {
        this.intentReceiver = new LaunchBroadcastReceiver();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Fabric.isInitialized()) {
            Fabric.with(this, new Crashlytics());
        }
        Timber.d("onCreate");
        int m_theme;
        int m_layout;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.context = getApplicationContext();
        customTabActivityHelper = new CustomTabActivityHelper();

        sharedPreference = SharedPreference.getInstance(this.context);

        if (sharedPreference.getNightMode()) {
            sharedPreference.setNightModeStatus(true);
            statusColor = ContextCompat.getColor(context, R.color.darkPrimary_dark);
            m_theme = R.style.DarkTheme_NoActionBar;
            m_layout = R.layout.dark_activity_main;
        } else {
            sharedPreference.setNightModeStatus(false);
            statusColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
            m_theme = R.style.LightTheme_NoActionBar;
            m_layout = R.layout.activity_main;
        }

        if (getSharedPreferences("theme_changed", 0).getBoolean("recreate", false)) {
            SharedPreferences.Editor editor = getSharedPreferences("theme_changed", 0).edit();
            editor.putBoolean("recreate", false);
            editor.apply();
            recreate();
        }

        setTheme(m_theme);
        super.onCreate(savedInstanceState);
        setContentView(m_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupWindowAnimations();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                int color = statusColor;
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 0) & 0xFF;

                float currentScroll = slideOffset * 255;

                int currentScrollInt = Math.round(currentScroll);

                if ((slideOffset) < 1 && (slideOffset) > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = getWindow();
                        window.setStatusBarColor(Color.argb(reverseNumber(currentScrollInt, 0, 255), r, g, b));
                    }
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Timber.v("onDrawerOpened - Open");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Timber.v("onDrawerClosed - Closed");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.setStatusBarColor(statusColor);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.menu_next_launch;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        ImageView imageView = (ImageView) header.findViewById(R.id.backgroundView);

        //Figure this shit out
        int[] images = {
                R.drawable.nav_header,
                R.drawable.navbar_one,
                R.drawable.navbar_three,
                R.drawable.navbar_four,
        };
        int idx = new Random().nextInt(images.length);
        imageView.setImageDrawable(ContextCompat.getDrawable(context, images[idx]));

        // select the correct nav menu item
        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        checkFirstBoot();
    }

    private void setupWindowAnimations() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setDuration(1000);
            getWindow().setEnterTransition(slide);
            getWindow().setReturnTransition(slide);
        }
    }

    public int reverseNumber(int num, int min, int max) {
        int number = (max + min) - num;
        return number;
    }

    public void onStart() {
        super.onStart();
        Timber.v("MainActivity onStart!");
        customTabActivityHelper.bindCustomTabsService(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_FAILURE_UP_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_SUCCESS_PREV_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_FAILURE_PREV_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_SUCCESS_MISSIONS);
        intentFilter.addAction(Strings.ACTION_FAILURE_MISSIONS);
        intentFilter.addAction(Strings.ACTION_SUCCESS_VEHICLE_DETAILS);
        intentFilter.addAction(Strings.ACTION_FAILURE_VEHICLE_DETAILS);
        intentFilter.addAction(Strings.ACTION_SUCCESS_VEHICLES);
        intentFilter.addAction(Strings.ACTION_FAILURE_VEHICLES);
        registerReceiver(this.intentReceiver, intentFilter);

        mayLaunchUrl(Uri.parse("https://launchlibrary.net/"));
    }

    public void onStop() {
        super.onStop();
        Timber.v("MainActivity onStop!");
        unregisterReceiver(this.intentReceiver);
        customTabActivityHelper.unbindCustomTabsService(this);
    }

    public void checkFirstBoot() {
        if (sharedPreference.getFirstBoot()) {
            sharedPreference.setFiltered(false);
            getFirstLaunches();
            loadTutorial();
        } else {

            //Spawn thread to check if data is in a bad state.
            final Context context = this;
            Thread t = new Thread(new Runnable() {
                public void run() {
                    if (sharedPreference.getVehicles() == null || sharedPreference.getVehicles().size() == 0) {
                        Intent rocketIntent = new Intent(context, VehicleDataService.class);
                        rocketIntent.setAction(Strings.ACTION_GET_VEHICLES_DETAIL);
                        context.startService(rocketIntent);

                    }
                    if (sharedPreference.getLaunchesUpcoming() == null || sharedPreference.getLaunchesUpcoming().size() == 0) {
                        Intent launchUpIntent = new Intent(context, LaunchDataService.class);
                        launchUpIntent.setAction(Strings.ACTION_GET_UP_LAUNCHES);
                        context.startService(launchUpIntent);
                    }
                    if (sharedPreference.getLaunchesPrevious() == null || sharedPreference.getLaunchesPrevious().size() == 0) {
                        Calendar c = Calendar.getInstance();

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = df.format(c.getTime());

                        String url = "https://launchlibrary.net/1.1/launch/1950-01-01/" + String.valueOf(formattedDate) + "?sort=desc&limit=1000";

                        Intent launchPrevIntent = new Intent(context, LaunchDataService.class);
                        launchPrevIntent.putExtra("URL", url);
                        launchPrevIntent.setAction(Strings.ACTION_GET_PREV_LAUNCHES);
                        context.startService(launchPrevIntent);
                    }
                    if (sharedPreference.getMissionList() == null || sharedPreference.getMissionList().size() == 0) {
                        context.startService(new Intent(context, MissionDataService.class));
                    }
                }
            });

            t.start();

            navigate(mNavItemId);
        }
    }

    private void refreshLaunches() {
        Intent update_upcoming_launches = new Intent(context, LaunchDataService.class);
        update_upcoming_launches.setAction(Strings.ACTION_GET_UP_LAUNCHES);
        context.startService(update_upcoming_launches);
    }

    public void getFirstLaunches() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        String url = "https://launchlibrary.net/1.1/launch/1950-01-01/" + String.valueOf(formattedDate) + "?sort=desc&limit=1000";

        Intent launchIntent = new Intent(this.context, LaunchDataService.class);
        launchIntent.setAction(Strings.ACTION_GET_ALL);
        launchIntent.putExtra("URL", url);
        this.context.startService(launchIntent);

        Intent rocketIntent = new Intent(this.context, VehicleDataService.class);
        rocketIntent.setAction(Strings.ACTION_GET_VEHICLES_DETAIL);
        this.context.startService(rocketIntent);

        this.context.startService(new Intent(this, MissionDataService.class));
    }

    public void onResume() {
        super.onResume();
        if (getSharedPreferences("theme_changed", 0).getBoolean("recreate", false)) {
            SharedPreferences.Editor editor = getSharedPreferences("theme_changed", 0).edit();
            editor.putBoolean("recreate", false);
            editor.apply();
            recreate();
        }
    }

    public void setActionBarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() != 0) {
                getFragmentManager().popBackStack();
            } else {
                new MaterialDialog.Builder(this)
                        .title("Confirm Exit")
                        .negativeText("Cancel")
                        .positiveText("Exit")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (BuildConfig.DEBUG) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        mNavItemId = item.getItemId();

        // allow some time after closing the drawer before performing real navigation
        // so the user can see what is happening
        drawer.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(item.getItemId());
            }
        }, 350);

        return true;
    }

    private void navigate(final int itemId) {
        // perform the actual navigation logic, updating the main_menu content fragment etc
        FragmentManager fm = getSupportFragmentManager();
        switch (itemId) {
            case R.id.menu_next_launch:
                mNavItemId = R.id.menu_next_launch;
                // Check to see if we have retained the worker fragment.
                mUpcomingFragment = (NextLaunchFragment) fm.findFragmentByTag("NEXT_LAUNCH");

                // If not retained (or first time running), we need to create it.
                if (mUpcomingFragment == null) {
                    mUpcomingFragment = new NextLaunchFragment();
                    // Tell it who it is working with.
                    fm.beginTransaction().replace(R.id.flContent,mUpcomingFragment, "NEXT_LAUNCH").commit();
                }
                break;
            case R.id.menu_launches:
                mNavItemId = R.id.menu_launches;
                // Check to see if we have retained the worker fragment.
                mlaunchesViewPager = (LaunchesViewPager) fm.findFragmentByTag("LAUNCH_VIEWPAGER");

                // If not retained (or first time running), we need to create it.
                if (mlaunchesViewPager == null) {
                    mlaunchesViewPager = new LaunchesViewPager();
                    // Tell it who it is working with.
                    fm.beginTransaction().replace(R.id.flContent,mlaunchesViewPager, "LAUNCH_VIEWPAGER").commit();
                }
                break;
            case R.id.menu_missions:
                mNavItemId = R.id.menu_missions;
                setActionBarTitle("Missions");
                // Check to see if we have retained the worker fragment.
                mMissionFragment = (MissionFragment) fm.findFragmentByTag("MISSION_FRAGMENT");

                // If not retained (or first time running), we need to create it.
                if (mMissionFragment == null) {
                    mMissionFragment = new MissionFragment();
                    // Tell it who it is working with.
                    fm.beginTransaction().replace(R.id.flContent, mMissionFragment, "MISSION_FRAGMENT").commit();
                }
                break;
            case R.id.menu_vehicle:
                mNavItemId = R.id.menu_vehicle;
                setActionBarTitle("Vehicles");
                // Check to see if we have retained the worker fragment.
                mVehicleViewPager = (VehiclesViewPager) fm.findFragmentByTag("VEHICLE_VIEWPAGER");

                // If not retained (or first time running), we need to create it.
                if (mVehicleViewPager == null) {
                    mVehicleViewPager = new VehiclesViewPager();
                    // Tell it who it is working with.
                    fm.beginTransaction().replace(R.id.flContent, mVehicleViewPager, "VEHICLE_VIEWPAGER").commit();
                }
                break;
            case R.id.menu_launch:
                Utils.openCustomTab(this, getApplicationContext(), "https://launchlibrary.net/");
                break;
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsIntent);
                break;
            case R.id.menu_help:
                loadTutorial();
                break;
            case R.id.menu_feedback:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"cajones9119@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SpaceLaunchNow - Feedback");
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
                break;
            default:
                // ignore
                break;
        }
    }

    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }

    public void loadTutorial() {
        Intent mainAct = new Intent(this, MaterialTutorialActivity.class);
        mainAct.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems(this));
        startActivityForResult(mainAct, REQUEST_CODE);

    }

    private ArrayList<TutorialItem> getTutorialItems(Context context) {
        TutorialItem tutorialItem1 = new TutorialItem("Space Launch Now", "Keep up to date on all your favorite  orbital launches, missions, and launch vehicles.",
                R.color.slide_one, R.drawable.intro_slide_one_foreground, R.drawable.intro_slide_background);

        TutorialItem tutorialItem2 = new TutorialItem("Notification for Launches", "Get notifications for upcoming launches and look into the history of spaceflight",
                R.color.slide_two, R.drawable.intro_slide_two_foreground, R.drawable.intro_slide_background);

        TutorialItem tutorialItem3 = new TutorialItem("Keep Track of Missions", "Find out whats going in the world of spaceflight.",
                R.color.slide_three, R.drawable.intro_slide_three_foreground, R.drawable.intro_slide_background);

        TutorialItem tutorialItem4 = new TutorialItem("Find Launch Vehicles", "Get to know the vehicles that have taken us to orbit.",
                R.color.slide_four, R.drawable.intro_slide_four_foreground, R.drawable.intro_slide_background);

        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        tutorialItems.add(tutorialItem4);

        return tutorialItems;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //    super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (sharedPreference.getFirstBoot()) {
                sharedPreference.setFirstBoot(false);
                recreate();
            }
        }
    }


}
