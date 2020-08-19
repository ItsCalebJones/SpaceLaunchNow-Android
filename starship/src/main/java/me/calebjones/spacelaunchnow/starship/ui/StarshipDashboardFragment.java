package me.calebjones.spacelaunchnow.starship.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.DetailsViewModel;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.calebjones.spacelaunchnow.data.models.main.dashboards.Starship;
import me.calebjones.spacelaunchnow.starship.StarshipListViewModel;
import me.calebjones.spacelaunchnow.starship.data.Callbacks;
import me.calebjones.spacelaunchnow.starship.data.StarshipDataRepository;
import me.spacelaunchnow.starship.R;
import me.spacelaunchnow.starship.R2;
import timber.log.Timber;

/**
 * A fragment representing the Starship Dashboard
 */
public class StarshipDashboardFragment extends BaseFragment {

    @BindView(R2.id.launches_title)
    TextView launchesTitle;
    @BindView(R2.id.event_title)
    TextView eventsTitle;
    @BindView(R2.id.launch_recycler)
    RecyclerView launchRecycler;
    @BindView(R2.id.event_recycler)
    RecyclerView eventRecyclerView  ;
    @BindView(R2.id.youtube_view)
    YouTubePlayerView youtubeView;
    @BindView(R2.id.starship_dashboard_coordinator)
    CoordinatorLayout starshipDashboardCoordinator;


    private StarshipDataRepository dataRepository;
    private boolean firstLaunch = true;
    private Unbinder unbinder;
    private StarshipListViewModel model;
    private YouTubePlayer youTubePlayer;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StarshipDashboardFragment() {
    }

    @SuppressWarnings("unused")
    public static StarshipDashboardFragment newInstance() {
        return new StarshipDashboardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataRepository = new StarshipDataRepository(getContext(), getRealm());
        setScreenName("Starship Dashboard Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_starship_dashboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        getLifecycle().addObserver(youtubeView);
        model = ViewModelProviders.of(getParentFragment()).get(StarshipListViewModel.class);
        model.getStarshipDashboard().observe(this, this::updateViews);
        return view;
    }


    private void loadVideo(YouTubePlayer youTubePlayer, String videoId) {
        if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED)
            youTubePlayer.cueVideo(videoId, 0);
        else
            youTubePlayer.cueVideo(videoId, 0);
    }

    //Currently only used to debug
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateViews(Starship starship) {
        youtubeView.initialize(youTubePlayer -> {
            youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady() {
                    loadVideo(youTubePlayer, "rCnl4IZOPe0");
                    youtubeView.getPlayerUIController().enableLiveVideoUI(true);

                }
            });
        }, true);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
