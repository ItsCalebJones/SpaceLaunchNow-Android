package me.spacelaunchnow.astronauts.list;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.utils.EndlessRecyclerViewScrollListener;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.spacelaunchnow.astronauts.R;
import me.spacelaunchnow.astronauts.R2;
import me.spacelaunchnow.astronauts.data.AstronautDataRepository;
import me.spacelaunchnow.astronauts.data.Callbacks;
import timber.log.Timber;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AstronautListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String ARG_STATUS_ID = "status-id";
    private String searchTerm;
    private OnListFragmentInteractionListener mListener;
    private AstronautDataRepository dataRepository;
    private List<Agency> agencies;
    private int nextOffset = 0;
    private int astronautCount = 0;
    private boolean canLoadMore;
    private boolean statefulStateContentShow = false;
    private boolean firstLaunch = true;
    private Unbinder unbinder;
    private AstronautRecyclerViewAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager linearLayoutManager;
    private int[] statusIDs;
    private boolean limitReached;

    @BindView(R2.id.astronaut_recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.astronaut_stateful_view)
    SimpleStatefulLayout statefulView;
    @BindView(R2.id.astronaut_coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R2.id.astronaut_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AstronautListFragment() {
    }

    @SuppressWarnings("unused")
    public static AstronautListFragment newInstance(int[] statusIDs) {
        AstronautListFragment fragment = new AstronautListFragment();
        Bundle args = new Bundle();
        args.putIntArray(ARG_STATUS_ID, statusIDs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            statusIDs = getArguments().getIntArray(ARG_STATUS_ID);
        }
        dataRepository = new AstronautDataRepository(getContext(), getRealm());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_astronaut_list, container, false);
        unbinder = ButterKnife.bind(this, view);

        // Set the adapter
        Context context = view.getContext();
        adapter = new AstronautRecyclerViewAdapter(mListener);
        linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        statefulView.showProgress();

        canLoadMore = true;
        limitReached = false;
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (canLoadMore) {
                    fetchData(false, false);
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        fetchData(false, firstLaunch);
        firstLaunch = false;
        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    public void fetchData(boolean forceRefresh, boolean firstLaunch) {
        Timber.v("fetchData - getting astronauts");
        nextOffset = astronautCount;
        int limit = 40;


        if (forceRefresh) {
            limitReached = false;
            adapter.clear();
        }

        if(!limitReached) {
            dataRepository.getAstronauts(limit, astronautCount, firstLaunch, forceRefresh, null, statusIDs, new Callbacks.AstronautListCallback() {
                @Override
                public void onAstronautsLoaded(RealmResults<Astronaut> astronauts, int next, int total) {
                    Timber.v("Offset - %s", next);
                    if(astronauts.size() == total){
                        limitReached = true;
                        canLoadMore = false;
                    }else {
                        astronautCount = astronauts.size();
                        canLoadMore = true;
                    }
                    updateAdapter(astronauts);
                }

                @Override
                public void onNetworkStateChanged(boolean refreshing) {
                showNetworkLoading(refreshing);
                }

                @Override
                public void onError(String message, @Nullable Throwable throwable) {
                    statefulView.showOffline();
                    statefulStateContentShow = false;
                    if (throwable != null) {
                        Timber.e(throwable);
                    } else {
                        Timber.e(message);
                    }
                }
            });
        }
    }

    private void showNetworkLoading(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    private void updateAdapter(List<Astronaut> astronauts) {

        if (astronauts.size() > 0) {
            if (!statefulStateContentShow) {
                statefulView.showContent();
                statefulStateContentShow = true;
            }
            adapter.addItems(astronauts);
        } else {
            statefulView.showEmpty();
            statefulStateContentShow = false;
            if (adapter != null) {
                adapter.clear();
            }
        }
        scrollListener.resetState();
    }

    public void onRefresh(String searchTerm) {
        this.searchTerm = searchTerm;
        fetchData(true, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {
        fetchData(true, false);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onAstronautClicked(Astronaut item);
    }
}
