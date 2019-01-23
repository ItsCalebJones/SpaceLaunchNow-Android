package me.calebjones.spacelaunchnow.spacestation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.utils.EndlessRecyclerViewScrollListener;
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import me.calebjones.spacelaunchnow.spacestation.data.Callbacks;
import me.calebjones.spacelaunchnow.spacestation.data.SpacestationDataRepository;
import timber.log.Timber;

public class SpacestationListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView spacestationRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SimpleStatefulLayout spacestationStatefulView;

    private String searchTerm;
    private SpacestationDataRepository dataRepository;
    private int nextOffset = 0;
    private int stationCount = 0;
    private boolean canLoadMore;
    private boolean statefulStateContentShow = false;
    private boolean firstLaunch = true;
    private SpacestationRecyclerViewAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager linearLayoutManager;
    private boolean limitReached;

    public static SpacestationListFragment newInstance() {
        return new SpacestationListFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataRepository = new SpacestationDataRepository(getContext(), getRealm());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spacestations_fragment, container, false);
        spacestationRecyclerView = view.findViewById(R.id.spacestation_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.spacestation_refresh_layout);
        spacestationStatefulView = view.findViewById(R.id.spacestation_stateful_view);
        setHasOptionsMenu(true);

        // Set the adapter
        Context context = view.getContext();
        adapter = new SpacestationRecyclerViewAdapter(context);
        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        spacestationRecyclerView.setLayoutManager(linearLayoutManager);
        spacestationRecyclerView.setAdapter(adapter);
        if (firstLaunch) {
            spacestationStatefulView.showProgress();
        } else {
            spacestationStatefulView.showContent();
        }

        canLoadMore = true;
        limitReached = false;
        spacestationStatefulView.setOfflineRetryOnClickListener(v -> onRefresh());
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (canLoadMore) {
                    boolean searchQuery = false;
                    if (searchTerm != null) {
                        searchQuery = true;
                    }
                    fetchData(false, false, searchQuery);
                }
            }
        };
        spacestationRecyclerView.addOnScrollListener(scrollListener);
        fetchData(false, firstLaunch, false);
        firstLaunch = false;
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    private void fetchData(boolean forceRefresh, boolean firstLaunch, boolean searchQuery) {
        Timber.v("fetchData - getting astronauts");
        nextOffset = stationCount;
        int limit = 40;


        if (forceRefresh || searchQuery) {
            stationCount = 0;
            limitReached = false;
            adapter.clear();
        }

        if (!limitReached) {
            dataRepository.getSpacestations(limit, stationCount, firstLaunch, null, new Callbacks.SpacestationListCallback() {
                @Override
                public void onSpacestationsLoaded(RealmResults<Spacestation> spacestations, int next, int total) {
                    Timber.v("Offset - %s", next);
                    if(getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        if (spacestations.size() == total) {
                            limitReached = true;
                            canLoadMore = false;
                        } else {
                            stationCount = spacestations.size();
                            canLoadMore = true;
                        }
                        updateAdapter(spacestations);
                    }
                }

                @Override
                public void onNetworkStateChanged(boolean refreshing) {
                    if(getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        showNetworkLoading(refreshing);
                    }
                }

                @Override
                public void onError(String message, @Nullable Throwable throwable) {
                    if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                        spacestationStatefulView.showOffline();
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

    private void updateAdapter(List<Spacestation> spacestations) {

        if (spacestations.size() > 0) {
            if (!statefulStateContentShow) {
                spacestationStatefulView.showContent();
                statefulStateContentShow = true;
            }
            adapter.addItems(spacestations);
        } else {
            spacestationStatefulView.showEmpty();
            statefulStateContentShow = false;
            if (adapter != null) {
                adapter.clear();
            }
        }
        scrollListener.resetState();
    }

    private void showNetworkLoading(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onRefresh() {

    }
}
