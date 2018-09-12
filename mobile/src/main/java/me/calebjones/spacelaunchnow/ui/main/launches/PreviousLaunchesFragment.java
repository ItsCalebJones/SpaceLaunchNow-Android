package me.calebjones.spacelaunchnow.ui.main.launches;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseFragment;
import me.calebjones.spacelaunchnow.common.customviews.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.content.data.callbacks.Callbacks;
import me.calebjones.spacelaunchnow.content.data.previous.PreviousDataRepository;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.utils.views.EndlessRecyclerViewScrollListener;
import me.calebjones.spacelaunchnow.utils.views.SnackbarHandler;
import timber.log.Timber;
import java.lang.reflect.Field;

public class PreviousLaunchesFragment extends BaseFragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private ListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Context context;
    private PreviousDataRepository previousDataRepository;
    private int nextOffset = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private String searchTerm = null;

    @BindView(R.id.stateful_view)
    SimpleStatefulLayout statefulView;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    public boolean canLoadMore;
    private boolean statefulStateContentShow = false;
    private static final Field sChildFragmentManagerField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName("Previous Launch Fragment");
        previousDataRepository = new PreviousDataRepository(context, getRealm());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getContext();
        canLoadMore = true;
        setHasOptionsMenu(true);
        adapter = new ListAdapter(getContext());

        view = inflater.inflate(R.layout.fragment_launches, container, false);
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        mRecyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (canLoadMore) {
                    fetchData(false);
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);
        statefulView.showProgress();
        statefulView.setOfflineRetryOnClickListener(v -> fetchData(true));
        return view;
    }

    public static PreviousLaunchesFragment newInstance(String text) {

        PreviousLaunchesFragment u = new PreviousLaunchesFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        u.setArguments(b);

        return u;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void updateAdapter(List<Launch> launches) {
        if (launches.size() > 0) {
            if (!statefulStateContentShow) {
                statefulView.showContent();
                statefulStateContentShow = true;
            }
            adapter.addItems(launches);
            adapter.notifyDataSetChanged();

        } else {
            statefulView.showEmpty();
            statefulStateContentShow = false;
            if (adapter != null) {
                adapter.clear();
            }
        }
        scrollListener.resetState();
    }

    public void fetchData(boolean forceRefresh) {
        Timber.v("Sending GET_UP_LAUNCHES");
        if (forceRefresh) {
            nextOffset = 0;
            adapter.clear();
        }
        previousDataRepository.getPreviousLaunches(nextOffset, searchTerm, null, null, new Callbacks.ListCallback() {
            @Override
            public void onLaunchesLoaded(List<Launch> launches, int next) {
                Timber.v("Offset - %s", next);
                nextOffset = next;
                canLoadMore = next > 0;
                updateAdapter(launches);
            }

            @Override
            public void onNetworkStateChanged(boolean refreshing) {
                showNetworkLoading(refreshing);
            }

            @Override
            public void onError(String message, @Nullable Throwable throwable) {
                statefulView.showOffline();
                statefulStateContentShow = false;
                showNetworkLoading(false);
                if (throwable != null) {
                    Timber.e(throwable);
                } else {
                    Timber.e(message);
                }
                SnackbarHandler.showErrorSnackbar(context, coordinatorLayout, message);
            }
        });
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
        Timber.d("OnResume!");
        if (adapter.getItemCount() == 0) {
            fetchData(false);
        }
        super.onResume();
    }

    @Override
    public void onRefresh() {
        searchTerm = null;
        fetchData(true);
    }

    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Crashlytics.logException(e);
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
                Crashlytics.logException(e);
                e.getLocalizedMessage();
                Timber.e("Error setting mChildFragmentManager field %s ", e);
            }
        }
    }


    //Currently only used to debug
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.upcoming_menu, menu);

        if (SupporterHelper.isSupporter()) {
            menu.removeItem(R.id.action_supporter);
        }

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            onRefresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchTerm = query;
        fetchData(true);
        return false;
    }
}


