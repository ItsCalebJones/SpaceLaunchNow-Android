package me.calebjones.spacelaunchnow.news.ui.news;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.Unbinder;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import me.calebjones.spacelaunchnow.news.R;
import me.calebjones.spacelaunchnow.data.networking.news.data.Callbacks;
import me.calebjones.spacelaunchnow.data.networking.news.data.NewsDataRepository;
import timber.log.Timber;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class NewsListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private String searchTerm;
    private NewsDataRepository dataRepository;
    private int nextOffset = 0;
    private int eventCount = 0;
    private boolean canLoadMore;
    private boolean statefulStateContentShow = false;
    private boolean firstLaunch = true;
    private Unbinder unbinder;
    private NewsRecyclerViewAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private StaggeredGridLayoutManager layoutManager;
    private List<Integer> statusIDs;
    private Integer[] statusIDsSelection;
    private RecyclerView recyclerView;
    private SimpleStatefulLayout statefulView;
    private SwipeRefreshLayout swipeRefreshLayout;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NewsListFragment() {
    }

    @SuppressWarnings("unused")
    public static NewsListFragment newInstance() {
        return new NewsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataRepository = new NewsDataRepository(getContext(), getRealm());
        setScreenName("News List Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        recyclerView = view.findViewById(R.id.news_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.news_refresh_layout);
        statefulView = view.findViewById(R.id.news_stateful_view);
        setHasOptionsMenu(true);

        // Set the adapter
        Context context = view.getContext();
        adapter = new NewsRecyclerViewAdapter(context);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        if (firstLaunch) {
            statefulView.showProgress();
        } else {
            statefulView.showContent();
        }

        canLoadMore = true;
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
            adapter.clear();
        }
        dataRepository.getNews(limit, forceRefresh, new Callbacks.NewsListCallback() {
            @Override
            public void onNewsLoaded(RealmResults<NewsItem> news) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                    updateAdapter(news);
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


    private void showNetworkLoading(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    private void updateAdapter(List<NewsItem> news) {

        if (news.size() > 0) {
            if (!statefulStateContentShow) {
                statefulView.showContent();
                statefulStateContentShow = true;
            }
            adapter.addItems(news);
        } else {
            statefulView.showEmpty();
            statefulStateContentShow = false;
            if (adapter != null) {
                adapter.clear();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
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
