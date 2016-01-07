package me.calebjones.spacelaunchnow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.RocketDataService;
import me.calebjones.spacelaunchnow.ui.activity.SettingsActivity;
import me.calebjones.spacelaunchnow.ui.fragment.PreviousLaunchesFragment;
import me.calebjones.spacelaunchnow.ui.fragment.LaunchesFragment;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String NAV_ITEM_ID = "navItemId";
    private final Handler mDrawerActionHandler = new Handler();

    private final LaunchesFragment mLaunchFragment = new LaunchesFragment();
    private final PreviousLaunchesFragment mHistoryFragment = new PreviousLaunchesFragment();

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private SharedPreferences sharedPref;
    private static SharedPreference sharedPreference;
    private Context context;
    private BroadcastReceiver intentReceiver;

    private int mNavItemId;

    class LaunchBroadcastReceiver extends BroadcastReceiver {
        LaunchBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            Timber.d("Broadcast received!");
            if (intent != null) {
                String action = intent.getAction();
                Fragment fragment;
                if (Strings.ACTION_SUCCESS_UP_LAUNCHES.equals(action)) {
                    fragment = null;
                    try {
                        fragment = (Fragment) LaunchesFragment.class.newInstance();
                    } catch (Exception ignored) {
                    }
                    MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
                } else if (Strings.ACTION_SUCCESS_PREV_LAUNCHES.equals(action)) {
                    fragment = null;
                    try {
                        fragment = (Fragment) PreviousLaunchesFragment.class.newInstance();
                    } catch (Exception ignored) {
                    }
                    MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
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
            m_theme = R.style.DarkTheme_NoActionBar;
            m_layout = R.layout.dark_activity_main;
        } else {
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
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // load saved navigation state if present
        if (null == savedInstanceState) {
            mNavItemId = R.id.menu_launches;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // select the correct nav menu item
        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigate(mNavItemId);
        checkFirstBoot();
    }

    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_FAILURE_UPC_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_SUCCESS_PREV_LAUNCHES);
        intentFilter.addAction(Strings.ACTION_FAILURE_PREV_LAUNCHES);
        registerReceiver(this.intentReceiver, intentFilter);
    }

    public void onStop() {
        super.onStop();
        unregisterReceiver(this.intentReceiver);
    }

    public void checkFirstBoot() {
        if (sharedPreference.getFirstBoot()) {
            sharedPreference.setFirstBoot(false);
            getFirstLaunches();
        }
    }

    public void getFirstLaunches() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        String url = "https://launchlibrary.net/1.1/launch/1950-01-01/" + String.valueOf(formattedDate) + "?sort=desc&limit=100";

        Intent launchIntent = new Intent(this.context, LaunchDataService.class);
        launchIntent.setAction(Strings.ACTION_GET_ALL);
        launchIntent.putExtra("URL", url);
        this.context.startService(launchIntent);

        Intent rocketIntent = new Intent(this.context, RocketDataService.class);
        rocketIntent.setAction(Strings.ACTION_GET_ROCKETS);
        this.context.startService(rocketIntent);
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
        item.setChecked(true);
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
                        .replace(R.id.flContent, mLaunchFragment)
                        .commit();
                break;
            case R.id.menu_history:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContent, mHistoryFragment)
                        .commit();
                break;
            case R.id.menu_missions:
                Toast.makeText(getBaseContext(), "Work in progress! Thanks for your patience!", Toast.LENGTH_SHORT).show();
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
}
