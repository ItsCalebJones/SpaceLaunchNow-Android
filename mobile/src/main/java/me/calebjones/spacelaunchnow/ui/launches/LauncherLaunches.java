package me.calebjones.spacelaunchnow.ui.launches;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseActivity;
import me.calebjones.spacelaunchnow.common.customviews.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.content.data.callbacks.Callbacks;
import me.calebjones.spacelaunchnow.content.data.previous.PreviousDataRepository;
import me.calebjones.spacelaunchnow.content.data.upcoming.UpcomingDataRepository;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.responses.base.AgencyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import me.calebjones.spacelaunchnow.ui.main.launches.ListAdapter;
import me.calebjones.spacelaunchnow.ui.settings.SettingsActivity;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterActivity;
import me.calebjones.spacelaunchnow.utils.views.EndlessRecyclerViewScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class LauncherLaunches extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    @BindView(R.id.menu)
    FloatingActionButton menu;

    private LinearLayoutManager linearLayoutManager;
    private ListAdapter adapter;
    private UpcomingDataRepository upcomingDataRepository;
    private PreviousDataRepository previousDataRepository;
    private int nextOffset = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private String searchTerm = null;
    private String lspName = null;
    private String launcherName = null;
    private Integer launcherId = null;
    private ArrayList<String> agencyList;
    private List<Launch> launches;
    private SwitchCompat switchCompat;
    public boolean canLoadMore;
    private boolean showUpcoming = true;

    public LauncherLaunches() {
        super("Launcher Activity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency_launches);
        ButterKnife.bind(this);
        upcomingDataRepository = new UpcomingDataRepository(this, getRealm());
        previousDataRepository = new PreviousDataRepository(this, getRealm());
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lspName = extras.getString("lspName");
            launcherName = extras.getString("launcherName");
            launcherId = extras.getInt("launcherId");
        }
        updateTitle();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        adapter = new ListAdapter(this);
        menu.setVisibility(View.GONE);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.setAdapter(adapter);
        swiperefresh.setOnRefreshListener(this);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (canLoadMore) {
                    fetchData(false);
                    swiperefresh.setRefreshing(true);
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }


    private void showAgencyDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.select_launch_agency)
                .content(R.string.select_launch_agency_description)
                .items(agencyList)
                .buttonRippleColorRes(R.color.colorAccentLight)
                .itemsCallback((dialog, view, which, text) -> {
                    lspName = String.valueOf(text);
                    fetchData(true);
                })
                .positiveText(R.string.filter)
                .negativeText(R.string.close)
                .icon(ContextCompat.getDrawable(this, R.mipmap.ic_launcher))
                .show();
    }

    public void fetchData(boolean forceRefresh) {
        Timber.v("Sending GET_UP_LAUNCHES");
        if (forceRefresh) {
            nextOffset = 0;
            adapter.clear();
        }
        if (showUpcoming) {
            upcomingDataRepository.getUpcomingLaunches(nextOffset, searchTerm, lspName, launcherId, new Callbacks.ListCallback() {
                @Override
                public void onLaunchesLoaded(List<Launch> launches, int next) {
                    Timber.v("Offset - %s", next);
                    nextOffset = next;
                    canLoadMore = next > 0;
                    updateAdapter(launches);
                    updateTitle();
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
                    }
                }
            });
        } else {
            previousDataRepository.getPreviousLaunches(nextOffset, searchTerm, lspName, launcherId, new Callbacks.ListCallback() {
                @Override
                public void onLaunchesLoaded(List<Launch> launches, int next) {
                    Timber.v("Offset - %s", next);
                    nextOffset = next;
                    canLoadMore = next > 0;
                    updateAdapter(launches);
                    updateTitle();
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
                    }
                }
            });

        }
    }

    private void updateTitle() {
        if (launcherName != null) {
            toolbar.setTitle(launcherName);
        } else {
            toolbar.setTitle("Launches");
        }

        if (showUpcoming && switchCompat != null) {
            switchCompat.setText("Upcoming");
        } else if (switchCompat != null) {
            switchCompat.setText("Previous");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.launcher_menu, menu);

        MenuItem item = menu.findItem(R.id.toggleservice);
        switchCompat = (SwitchCompat) MenuItemCompat.getActionView(item);
        switchCompat.setChecked(showUpcoming);
        switchCompat.setOnCheckedChangeListener((compoundButton, b) -> {
            showUpcoming = b;
            fetchData(true);
        });


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
    public void onResume() {
        Timber.d("OnResume!");
        if (adapter.getItemCount() == 0) {
            fetchData(false);
        }
        super.onResume();
    }

    private void updateAdapter(List<Launch> launches) {

        if (launches.size() > 0) {
            adapter.addItems(launches);
            adapter.notifyDataSetChanged();

        } else {
            if (adapter != null) {
                adapter.clear();
            }
        }
        scrollListener.resetState();
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
        swiperefresh.post(() -> swiperefresh.setRefreshing(true));
    }

    private void hideLoading() {
        Timber.v("Hide Loading...");
        swiperefresh.post(() -> swiperefresh.setRefreshing(false));
    }

    @Override
    public void onRefresh() {
        searchTerm = null;
        lspName = null;
        updateTitle();
        fetchData(true);
    }

    @OnClick(R.id.menu)
    public void onViewClicked() {
        showAgencyDialog();
    }
}
