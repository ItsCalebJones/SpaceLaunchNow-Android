package me.calebjones.spacelaunchnow.ui.launches.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.responses.base.AgencyResponse;
import me.calebjones.spacelaunchnow.ui.launches.agency.PreviousAgencyLaunchesFragment;
import me.calebjones.spacelaunchnow.ui.launches.agency.UpcomingAgencyLaunchesFragment;
import me.calebjones.spacelaunchnow.ui.settings.SettingsActivity;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LauncherLaunchActivity extends AppCompatActivity implements UpcomingLauncherLaunchesFragment.OnFragmentInteractionListener, PreviousLauncherLaunchesFragment.OnFragmentInteractionListener, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.menu)
    FloatingActionButton menu;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private UpcomingLauncherLaunchesFragment upcomingFragment;
    private PreviousLauncherLaunchesFragment previousFragment;
    private ArrayList<String> agencyList;
    private List<Agency> agencies;
    private boolean upcomingLoading = false;
    private boolean previousLoading = false;
    private String searchTerm = null;
    private String lspName = null;
    private String launcherName = null;
    private Integer launcherId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_launch);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lspName = extras.getString("lspName");
            launcherName = extras.getString("launcherName");
            launcherId = extras.getInt("launcherId");
        }
        setTitle();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager.setAdapter(mSectionsPagerAdapter);
        swipeRefresh.setOnRefreshListener(this);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        menu.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        setTitle();
        super.onResume();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.launcher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_supporter) {
            Intent intent = new Intent(this, SupporterActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showUpcomingLoading(boolean loading) {
        upcomingLoading = loading;
        showNetworkLoading();
    }

    @Override
    public void showPreviousLoading(boolean loading) {
        previousLoading = loading;
        showNetworkLoading();
    }

    @Override
    public void onRefresh() {
        lspName = null;
        searchTerm = null;
        refresh();
    }

    private void refresh() {
        if (upcomingFragment != null) {
            upcomingFragment.onRefresh(lspName, searchTerm);
        }
        if (previousFragment != null) {
            previousFragment.onRefresh(lspName, searchTerm);
        }
        setTitle();
    }

    private void setTitle() {
        if (launcherName != null) {
            toolbar.setTitle(launcherName);
        } else {
            toolbar.setTitle("Launches");
        }
    }

    private void showNetworkLoading() {
        if (upcomingLoading || previousLoading) {
            showLoading();
        } else {
            hideLoading();
        }
    }

    private void showLoading() {
        Timber.v("Show Loading...");
        swipeRefresh.post(() -> swipeRefresh.setRefreshing(true));
    }

    private void hideLoading() {
        Timber.v("Hide Loading...");
        swipeRefresh.post(() -> swipeRefresh.setRefreshing(false));
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return UpcomingLauncherLaunchesFragment.newInstance(searchTerm, lspName, launcherId);
                case 1:
                    return PreviousLauncherLaunchesFragment.newInstance(searchTerm, lspName, launcherId);
                default:
                    return null;
            }
        }

        // Here we can finally safely save a reference to the created
        // Fragment, no matter where it came from (either getItem() or
        // FragmentManger). Simply save the returned Fragment from
        // super.instantiateItem() into an appropriate reference depending
        // on the ViewPager position.
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    upcomingFragment = (UpcomingLauncherLaunchesFragment) createdFragment;
                    break;
                case 1:
                    previousFragment = (PreviousLauncherLaunchesFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
