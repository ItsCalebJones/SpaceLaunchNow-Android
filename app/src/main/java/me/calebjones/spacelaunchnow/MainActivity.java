package me.calebjones.spacelaunchnow;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import me.calebjones.spacelaunchnow.content.database.DatabaseManager;
import me.calebjones.spacelaunchnow.content.loader.VehicleLoader;
import me.calebjones.spacelaunchnow.ui.fragment.PreviousLaunchesFragment;
import me.calebjones.spacelaunchnow.ui.fragment.LaunchesFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String NAV_ITEM_ID = "navItemId";
    private final Handler mDrawerActionHandler = new Handler();

    private final LaunchesFragment mLaunchFragment = new LaunchesFragment();
    private final PreviousLaunchesFragment mHistoryFragment = new PreviousLaunchesFragment();

    private Toolbar toolbar;
    private DrawerLayout drawer;

    private int mNavItemId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            public void run() {
                DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());
                databaseManager.rebuildDB(databaseManager.getWritableDatabase());

                VehicleLoader vehicleLoader = new VehicleLoader(getApplicationContext());
                vehicleLoader.execute("http://calebjones.me/app/launchvehicle.json");
            }
        }).start();

        setContentView(R.layout.activity_main);
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
        getMenuInflater().inflate(R.menu.main, menu);
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
        // perform the actual navigation logic, updating the main content fragment etc
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
                Toast.makeText(getBaseContext(), "Work in progress! Thanks for your patience!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_vehicle:
                Toast.makeText(getBaseContext(), "Work in progress! Thanks for your patience!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_favorites:
                Toast.makeText(getBaseContext(), "Work in progress! Thanks for your patience!",Toast.LENGTH_SHORT).show();
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
