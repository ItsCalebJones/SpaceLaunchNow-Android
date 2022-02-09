package me.calebjones.spacelaunchnow.events.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import io.realm.RealmResults;

import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.utils.EndlessRecyclerViewScrollListener;
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.events.R;
import me.calebjones.spacelaunchnow.events.R2;
import me.calebjones.spacelaunchnow.events.data.Callbacks;
import me.calebjones.spacelaunchnow.events.data.EventDataRepository;

import timber.log.Timber;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class EventListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private String searchTerm;
    private EventDataRepository dataRepository;
    private int nextOffset = 0;
    private int eventCount = 0;
    private boolean canLoadMore;
    private boolean statefulStateContentShow = false;
    private boolean firstLaunch = true;
    private Unbinder unbinder;
    private EventRecyclerViewAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private List<Integer> statusIDs;
    private Integer[] statusIDsSelection;
    private boolean limitReached;

    @BindView(R2.id.event_recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.event_stateful_view)
    SimpleStatefulLayout statefulView;
    @BindView(R2.id.event_coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R2.id.event_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventListFragment() {
    }



    @SuppressWarnings("unused")
    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataRepository = new EventDataRepository(getContext(), getRealm());
        setScreenName("Events List Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        // Set the adapter
        Context context = view.getContext();
        adapter = new EventRecyclerViewAdapter(context);
        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        if (firstLaunch) {
            statefulView.showProgress();
        } else {
            statefulView.showContent();
        }

        canLoadMore = true;
        limitReached = false;
        statefulView.setOfflineRetryOnClickListener(v -> onRefresh());
        fetchData(false, firstLaunch, false);
        firstLaunch = false;
        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    private void fetchData(boolean forceRefresh, boolean firstLaunch, boolean searchQuery) {
        Timber.v("fetchData - getting astronauts");
        nextOffset = eventCount;
        int limit = 40;


        if (forceRefresh || searchQuery) {
            eventCount = 0;
            limitReached = false;
            adapter.clear();
        }

        if (!limitReached) {
            dataRepository.getEvents(limit, eventCount, forceRefresh, new Callbacks.EventListCallback() {
                @Override
                public void onEventsLoaded(RealmResults<Event> events, int next, int total) {
                    Timber.v("Offset - %s", next);
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                        if (events.size() == total) {
                            limitReached = true;
                            canLoadMore = false;
                        } else {
                            eventCount = events.size();
                            canLoadMore = true;
                        }
                        updateAdapter(events);
                    }
                }

                @Override
                public void onNetworkStateChanged(boolean refreshing) {
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        showNetworkLoading(refreshing);
                    }
                }

                @Override
                public void onError(String message, @Nullable Throwable throwable) {
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        statefulView.showOffline();
                        statefulStateContentShow = false;
                        if (throwable != null) {
                            Timber.e(throwable);
                        } else {
                            Timber.e(message);
                        }
                    }
                }
            });
        }
    }


    private void showNetworkLoading(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    private void updateAdapter(List<Event> events) {

        if (events.size() > 0) {
            if (!statefulStateContentShow) {
                statefulView.showContent();
                statefulStateContentShow = true;
            }
            adapter.addItems(events);
        } else {
            statefulView.showEmpty();
            statefulStateContentShow = false;
            if (adapter != null) {
                adapter.clear();
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }



    @Override
    public void onRefresh() {
        if (searchTerm != null || statusIDs != null) {
            statusIDs = null;
            searchTerm = null;
            swipeRefreshLayout.setRefreshing(false);
            fetchData(false, false, false);
        } else {
            fetchData(true, false, false);
        }
    }
}
