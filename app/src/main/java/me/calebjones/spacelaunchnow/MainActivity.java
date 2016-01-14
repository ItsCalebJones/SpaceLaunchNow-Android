package me.calebjones.spacelaunchnow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.MissionDataService;
import me.calebjones.spacelaunchnow.content.services.RocketDataService;
import me.calebjones.spacelaunchnow.ui.activity.SettingsActivity;
import me.calebjones.spacelaunchnow.ui.fragment.MissionFragment;
import me.calebjones.spacelaunchnow.ui.fragment.LaunchesViewPager;
import timber.log.Timber;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String NAV_ITEM_ID = "navItemId";
    private final Handler mDrawerActionHandler = new Handler();

    private LaunchesViewPager mlaunchesViewPager;
    private final MissionFragment mMissionFragment = new MissionFragment();

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private SharedPreferences sharedPref;
    private static SharedPreference sharedPreference;
    private Context context;
    private Boolean bool;
    private BroadcastReceiver intentReceiver;

    private static final int REQUEST_CODE = 5467;

    private int mNavItemId;

    public int statusColor;

    class LaunchBroadcastReceiver extends BroadcastReceiver {
        LaunchBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Timber.v("Broadcast received...");
            if (intent != null) {
                String action = intent.getAction();
                Timber.d("Broadcast action : %s", action);
                if (Strings.ACTION_SUCCESS_UP_LAUNCHES.equals(action)) {
                    mlaunchesViewPager.restartViews(Strings.ACTION_SUCCESS_UP_LAUNCHES);
                } else if (Strings.ACTION_SUCCESS_PREV_LAUNCHES.equals(action)) {
                    mlaunchesViewPager.restartViews(Strings.ACTION_SUCCESS_PREV_LAUNCHES);
                } else if (Strings.ACTION_SUCCESS_MISSIONS.equals(action)) {
                    if (mMissionFragment.isVisible()){
                        mMissionFragment.onFinishedRefreshing();
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
        int m_theme;
        int m_layout;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        this.context = getApplicationContext();

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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                Timber.v("onDrawerSlide -slideOffest: %s", slideOffset);
                int color = statusColor;
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 0) & 0xFF;

                float currentScroll = slideOffset * 255;
                float totalScroll = 255;

                int currentScrollInt = Math.round(currentScroll);


                Timber.v("onDrawerSlide -currentScroll: %s totalScroll: %s", currentScroll, totalScroll);

                if ((slideOffset) < 1 && (slideOffset) > 0){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Timber.v("onDrawerSlide - Open? currentScroll: %s", currentScroll);
                        Window window = getWindow();
                        window.setStatusBarColor(Color.argb(reverseNumber(currentScrollInt,0,255),r,g,b));
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

        mlaunchesViewPager = new LaunchesViewPager();

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.menu_launches;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
        imageView.setImageDrawable(ContextCompat.getDrawable(context,images[idx]));

        // select the correct nav menu item
        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        checkFirstBoot();
    }

    public int reverseNumber(int num, int min, int max) {
        int number = (max + min) - num;
        Timber.v("Number: %s",number);
        return number;
    }

    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_FAILURE_UPC_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_SUCCESS_PREV_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_FAILURE_PREV_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_SUCCESS_MISSIONS);
        intentFilter.addAction(Strings.ACTION_FAILURE_MISSIONS);
        intentFilter.addAction(Strings.ACTION_SUCCESS_ROCKETS);
        intentFilter.addAction(Strings.ACTION_FAILURE_ROCKETS);
        registerReceiver(this.intentReceiver, intentFilter);
    }

    public void onStop() {
        super.onStop();
        unregisterReceiver(this.intentReceiver);
    }

    public void checkFirstBoot() {
        if (sharedPreference.getFirstBoot()) {
            getFirstLaunches();
            loadTutorial();
        } else {
            navigate(mNavItemId);
        }
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

        Intent rocketIntent = new Intent(this.context, RocketDataService.class);
        rocketIntent.setAction(Strings.ACTION_GET_ROCKETS);
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

        if (mlaunchesViewPager == null){
            mlaunchesViewPager = new LaunchesViewPager();
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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
        switch (itemId) {
            case R.id.menu_launches:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, mlaunchesViewPager)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.menu_missions:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, mMissionFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.menu_vehicle:
                Toast.makeText(getBaseContext(), "Work in progress! Thanks for your patience!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_favorites:
                Toast.makeText(getBaseContext(), "Work in progress! Thanks for your patience!", Toast.LENGTH_SHORT).show();
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
        TutorialItem tutorialItem1 = new TutorialItem("Space Launch Now","Keep up to date on all your favorite  orbital launches, missions, and launch vehicles.",
                R.color.slide_one, R.drawable.intro_slide_one_foreground, R.drawable.intro_slide_background);

        TutorialItem tutorialItem2 = new TutorialItem("Notification for Launches","Get notifications for upcoming launches and look into the history of spaceflight",
                R.color.slide_two, R.drawable.intro_slide_two_foreground, R.drawable.intro_slide_background);

        TutorialItem tutorialItem3 = new TutorialItem("Keep Track of Missions","Find out whats going in the world of spaceflight.",
                R.color.slide_three,  R.drawable.intro_slide_three_foreground, R.drawable.intro_slide_background);

        TutorialItem tutorialItem4 = new TutorialItem("Find Launch Vehicles","Get to know the vehicles that have taken us to orbit.",
                R.color.slide_four,  R.drawable.intro_slide_four_foreground, R.drawable.intro_slide_background);

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
            sharedPreference.setFirstBoot(false);
            recreate();
        }
    }
}
