package me.spacelaunchnow.astronauts.list;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.utils.EndlessRecyclerViewScrollListener;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;
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
public class AstronautListFragment extends BaseFragment {

    private static final String ARG_STATUS_ID = "status-id";
    private int statusId = 1;
    private String searchTerm;
    private OnListFragmentInteractionListener mListener;
    private AstronautDataRepository dataRepository;
    private List<Agency> agencies;
    private int nextOffset = 0;
    public boolean canLoadMore;
    private boolean statefulStateContentShow = false;
    private Unbinder unbinder;
    private AstronautRecyclerViewAdapter adapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager linearLayoutManager;

    @BindView(R2.id.astronaut_recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.astronaut_stateful_view)
    SimpleStatefulLayout statefulView;
    @BindView(R2.id.astronaut_coordinator)
    CoordinatorLayout coordinatorLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AstronautListFragment() {
    }

    @SuppressWarnings("unused")
    public static AstronautListFragment newInstance(int statusId) {
        AstronautListFragment fragment = new AstronautListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STATUS_ID, statusId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            statusId = getArguments().getInt(ARG_STATUS_ID);
        }
        dataRepository = new AstronautDataRepository(getContext());
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
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        statefulView.showProgress();
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (canLoadMore) {
                    fetchData(false);
                }
            }
        };
        dataRepository.getAstronauts(0, 20, searchTerm, statusId, new Callbacks.AstronautListCallback() {
            @Override
            public void onLaunchesLoaded(List<Astronaut> astronauts, int nextOffset, int total) {
                updateAdapter(astronauts);
            }

            @Override
            public void onNetworkStateChanged(boolean refreshing) {

            }

            @Override
            public void onError(String message, @Nullable Throwable throwable) {

            }
        });
        return view;
    }

    public void fetchData(boolean forceRefresh) {
        Timber.v("fetchData - getting astronauts");
        if (forceRefresh) {
            nextOffset = 0;
            adapter.clear();
        }

        dataRepository.getAstronauts(20, nextOffset, null, statusId, new Callbacks.AstronautListCallback() {
            @Override
            public void onLaunchesLoaded(List<Astronaut> astronauts, int next, int total) {
                Timber.v("Offset - %s", next);
                nextOffset = next;
                canLoadMore = next > 0;
                updateAdapter(astronauts);
            }

            @Override
            public void onNetworkStateChanged(boolean refreshing) {
//                showNetworkLoading(refreshing);
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

    private void updateAdapter(List<Astronaut> astronauts) {

        if (astronauts.size() > 0) {
            if (!statefulStateContentShow) {
                statefulView.showContent();
                statefulStateContentShow = true;
            }
            adapter.addItems(astronauts);
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

    public void onRefresh(String searchTerm) {
        this.searchTerm = searchTerm;
        fetchData(true);
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
        // TODO: Update argument type and name
        void onListFragmentInteraction(Astronaut item);
    }
}
