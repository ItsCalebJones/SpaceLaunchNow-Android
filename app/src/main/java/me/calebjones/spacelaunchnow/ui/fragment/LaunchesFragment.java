package me.calebjones.spacelaunchnow.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;

import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.content.loader.LaunchLoader;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.adapter.LaunchAdapter;
import jp.wasabeef.recyclerview.animators.adapters.SlideInBottomAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class LaunchesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private RecyclerView mRecyclerView;
    private LaunchAdapter adapter;
    private StaggeredGridLayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SlideInBottomAnimationAdapter animatorAdapter;

    public LaunchesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        LayoutInflater lf = getActivity().getLayoutInflater();

        view = lf.inflate(R.layout.fragment_launches, container, false);
        View menu = view.findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        if (getResources().getBoolean(R.bool.landscape)) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        } else {
            layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int topRowVerticalPostion = (mRecyclerView == null || mRecyclerView
                        .getChildCount() == 0) ? 0 : mRecyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(dx == 0 && topRowVerticalPostion >= 0);
            }
        });

                /*Set up Pull to refresh*/
        mSwipeRefreshLayout = (SwipeRefreshLayout)
                view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        ((MainActivity) getActivity()).setActionBarTitle("Upcoming Launches");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRecyclerView.getAdapter() == null) {
            LaunchLoader loader = new LaunchLoader() {
                @Override
                protected void onPreExecute() {
                    launchArrayList = new ArrayList<>();
                    CircularProgressView progressView = (CircularProgressView)
                            view.findViewById(R.id.progress_View);
                    progressView.setVisibility(View.VISIBLE);
                    progressView.startAnimation();
                }

                @Override
                protected void onPostExecute(List<Launch> result) {
                    CircularProgressView progressView = (CircularProgressView)
                            view.findViewById(R.id.progress_View);
                    progressView.clearAnimation();
                    progressView.setVisibility(View.GONE);
            /* Download complete. Lets update UI */
                    if (result != null) {
                        adapter = new LaunchAdapter(getActivity(),
                                getView().findViewById(R.id.fragment_feed_content));
                        adapter.addItems(result);
                        animatorAdapter = new SlideInBottomAnimationAdapter(adapter);
                        animatorAdapter.setDuration(250);
                        mRecyclerView.setAdapter(animatorAdapter);
                    } else Log.e(LaunchApplication.TAG, "Failed to fetch data!");
                }
            };
            loader.execute(LaunchApplication.LAUNCH_URL);
        }
    }

    @Override
    public void onRefresh() {
        LaunchLoader loader = new LaunchLoader() {
            @Override
            protected void onPreExecute() {
                launchArrayList = new ArrayList<>();
                if (adapter.getItemCount() != 0) {
                    adapter.removeAll();
                }
            }

            @Override
            protected void onPostExecute(List<Launch> result) {
            /* Download complete. Lets update UI */
                if (result != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    CircularProgressView progressView = (CircularProgressView)
                            view.findViewById(R.id.progress_View);
                    progressView.setVisibility(View.GONE);
                    adapter.addItems(result);
                    animatorAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(0);
                } else Log.e(LaunchApplication.TAG, "Failed to fetch data!");
            }
        };
        loader.execute(LaunchApplication.LAUNCH_URL);
    }
}
