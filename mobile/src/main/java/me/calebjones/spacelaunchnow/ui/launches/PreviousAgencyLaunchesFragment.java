package me.calebjones.spacelaunchnow.ui.launches;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.customviews.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.content.data.callbacks.Callbacks;
import me.calebjones.spacelaunchnow.content.data.previous.PreviousDataRepository;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.responses.base.AgencyResponse;
import me.calebjones.spacelaunchnow.ui.main.launches.ListAdapter;
import me.calebjones.spacelaunchnow.utils.views.EndlessRecyclerViewScrollListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PreviousAgencyLaunchesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviousAgencyLaunchesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String SEARCH_TERM = "searchTerm";
    private static final String LSP_NAME = "lspName";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.stateful_view)
    SimpleStatefulLayout statefulView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.menu)
    FloatingActionButton menu;
    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;

    private LinearLayoutManager linearLayoutManager;
    private ListAdapter adapter;
    private String searchTerm = null;
    private String lspName = null;
    private PreviousDataRepository previousDataRepository;
    private int nextOffset = 0;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ArrayList<String> agencyList;
    private List<Agency> agencies;
    public boolean canLoadMore;
    private boolean statefulStateContentShow = false;
    private Context context;

    private OnFragmentInteractionListener mListener;

    public PreviousAgencyLaunchesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param searchTerm Parameter 1.
     * @param lspName    Parameter 2.
     * @return A new instance of fragment PreviousAgencyLaunchesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PreviousAgencyLaunchesFragment newInstance(String searchTerm, String lspName) {
        PreviousAgencyLaunchesFragment fragment = new PreviousAgencyLaunchesFragment();
        Bundle args = new Bundle();
        args.putString(SEARCH_TERM, searchTerm);
        args.putString(LSP_NAME, lspName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchTerm = getArguments().getString(SEARCH_TERM);
            lspName = getArguments().getString(LSP_NAME);
        }
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agency_launch, container, false);
        ButterKnife.bind(this, view);

        adapter = new ListAdapter(context);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        recyclerView.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(this);
        statefulView.showProgress();
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (canLoadMore) {
                    fetchData(false);
                    swipeRefresh.setRefreshing(true);
                }
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
        getFeaturedAgencies();
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getFeaturedAgencies() {
        DataClient.getInstance().getFeaturedAgencies(new Callback<AgencyResponse>() {
            @Override
            public void onResponse(Call<AgencyResponse> call, Response<AgencyResponse> response) {
                if (response.isSuccessful()) {
                    List<Agency> agencies = response.body().getAgencies();
                    agencyList = new ArrayList<>();
                    for (Agency agency : agencies) {
                        agencyList.add(agency.getName());
                    }
                    menu.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<AgencyResponse> call, Throwable t) {

            }
        });
    }

    public void fetchData(boolean forceRefresh) {
        Timber.v("Sending GET_UP_LAUNCHES");
        if (forceRefresh) {
            nextOffset = 0;
            adapter.clear();
        }
        previousDataRepository.getPreviousLaunches(nextOffset, searchTerm, lspName, null, new Callbacks.ListCallback() {
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
                if (throwable != null) {
                    Timber.e(throwable);
                } else {
                    Timber.e(message);
                }
            }
        });
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
            if (statefulStateContentShow) {
                statefulView.showEmpty();
                statefulStateContentShow = false;
            }
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
        swipeRefresh.post(() -> swipeRefresh.setRefreshing(true));
    }

    private void hideLoading() {
        Timber.v("Hide Loading...");
        swipeRefresh.post(() -> swipeRefresh.setRefreshing(false));
    }

    @Override
    public void onRefresh() {
        searchTerm = null;
        lspName = null;
        fetchData(true);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void setLSPName(String lspName);

        void setSearchTerm(String searchTerm);
    }
}
