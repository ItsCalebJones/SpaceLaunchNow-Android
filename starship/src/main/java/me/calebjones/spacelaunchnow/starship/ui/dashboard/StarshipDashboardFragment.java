package me.calebjones.spacelaunchnow.starship.ui.dashboard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.button.MaterialButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cz.kinst.jakub.view.SimpleStatefulLayout;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.prefs.ThemeHelper;
import me.calebjones.spacelaunchnow.common.ui.views.DialogAdapter;
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.common.youtube.models.VideoListItem;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;
import me.calebjones.spacelaunchnow.data.models.main.VidURL;
import me.calebjones.spacelaunchnow.data.models.main.dashboards.Starship;
import me.calebjones.spacelaunchnow.starship.StarshipDashboardViewModel;
import me.calebjones.spacelaunchnow.starship.data.StarshipDataRepository;
import me.calebjones.spacelaunchnow.starship.ui.upcoming.CombinedAdapter;
import me.spacelaunchnow.starship.R;
import me.spacelaunchnow.starship.R2;

/**
 * A fragment representing the Starship Dashboard
 */
public class StarshipDashboardFragment extends BaseFragment {

    @BindView(R2.id.youtube_view)
    YouTubePlayerView youtubeView;
    @BindView(R2.id.starship_dashboard_coordinator)
    CoordinatorLayout starshipDashboardCoordinator;
    @BindView(R2.id.upnext_recyclerview)
    RecyclerView upnextRecyclerview;
    @BindView(R2.id.roadclosure_recyclerview)
    RecyclerView roadclosureRecyclerview;
    @BindView(R2.id.notices_recyclerview)
    RecyclerView noticeRecyclerview;
    @BindView(R2.id.update_recyclerview)
    RecyclerView updateRecyclerview;
    @BindView(R2.id.live_streams_button)
    MaterialButton liveStreamsButton;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.description)
    TextView description;
    @BindView(R2.id.roadclosure_stateful_layout)
    SimpleStatefulLayout roadclosureStatefulLayout;
    @BindView(R2.id.notices_stateful_layout)
    SimpleStatefulLayout noticesStatefulLayout;
    @BindView(R2.id.upnext_stateful_layout)
    SimpleStatefulLayout upnextStatefulLayout;
    @BindView(R2.id.update_stateful_layout)
    SimpleStatefulLayout updateStatefulLayout;


    private StarshipDataRepository dataRepository;
    private boolean firstLaunch = true;
    private Unbinder unbinder;
    private StarshipDashboardViewModel model;
    private YouTubePlayer youTubePlayer;
    private RoadClosureAdapter roadClosureAdapter;
    private NoticesAdapter noticesAdapter;
    private UpdateAdapter updateAdapter;
    private Dialog dialog;
    private String youTubeURL;
    private CombinedAdapter adapter;


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
        model = ViewModelProviders.of(getParentFragment()).get(StarshipDashboardViewModel.class);
        model.getStarshipDashboard().observe(this, this::updateViews);

        roadClosureAdapter = new RoadClosureAdapter(getContext());
        roadclosureRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        roadclosureRecyclerview.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        roadclosureRecyclerview.setAdapter(roadClosureAdapter);

        noticesAdapter = new NoticesAdapter(getContext());
        noticeRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        noticeRecyclerview.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        noticeRecyclerview.setAdapter(noticesAdapter);

        adapter = new CombinedAdapter(getContext(), ThemeHelper.isDarkMode(getActivity()));
        upnextRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        upnextRecyclerview.setAdapter(adapter);

        updateAdapter = new UpdateAdapter(getContext());
        updateRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        updateRecyclerview.setAdapter(updateAdapter);


        if (savedInstanceState != null) {
            youTubeURL = savedInstanceState.getString("youTubeID");
        }

        return view;
    }


    private void loadVideo(YouTubePlayer youTubePlayer, String videoId) {
        if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED)
            youTubePlayer.cueVideo(videoId, 0);
        else
            youTubePlayer.cueVideo(videoId, 0);
        youTubePlayer.play();
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
        if (starship.getLiveStreams().size() > 0) {
            VidURL liveStream = starship.liveStreams.get(0);
            youTubeURL = Utils.getYouTubeID(liveStream.getUrl());
            title.setText(liveStream.getName());
            description.setText(liveStream.getDescription());
            youtubeView.initialize(youTubePlayer -> {
                youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        loadVideo(youTubePlayer, youTubeURL);
                        youtubeView.getPlayerUIController().enableLiveVideoUI(true);
                        youtubeView.getPlayerUIController().showFullscreenButton(false);
                    }
                });
            }, true);
        }
        ArrayList<Object> upcomingCombinedObjects = new ArrayList<>();
        if (starship.getUpcomingObjects().getEvents().size() > 0) {
            upcomingCombinedObjects.addAll(starship.getUpcomingObjects().getEvents());
        }

        if (starship.getUpcomingObjects().getLaunches().size() > 0) {
            upcomingCombinedObjects.addAll(starship.getUpcomingObjects().getLaunches());
        }

        if (upcomingCombinedObjects.size() > 0) {
            upnextStatefulLayout.showContent();
            upcomingCombinedObjects = sortMultiClassList(upcomingCombinedObjects);
            Object object = upcomingCombinedObjects.get(0);
            upcomingCombinedObjects = new ArrayList<>();
            upcomingCombinedObjects.add(object);
            adapter.addItems(upcomingCombinedObjects);
        } else {
            upnextStatefulLayout.showEmpty();
        }

        if (starship.getRoadClosures().size() > 0) {
            roadclosureStatefulLayout.showContent();
            roadClosureAdapter.addItems(starship.getRoadClosures());
        } else {
            roadclosureStatefulLayout.showEmpty();
        }

        if (starship.getNotices().size() > 0) {
            noticesStatefulLayout.showContent();
            noticesAdapter.addItems(starship.getNotices());
        } else {
            noticesStatefulLayout.showEmpty();
        }

        if (starship.getUpdates().size() > 0){
            updateStatefulLayout.showContent();
            updateAdapter = new UpdateAdapter(getContext());
            updateRecyclerview.setAdapter(updateAdapter);
            updateAdapter.addItems(starship.getUpdates());
        } else {
            updateStatefulLayout.showEmpty();
        }

        liveStreamsButton.setOnClickListener(v -> {
            if (starship.getLiveStreams().size() > 0) {
                final DialogAdapter adapter = new DialogAdapter((index, item, longClick) -> {
                    if (longClick) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, item.getVideoURL().toString()); // Simple text and URL to share
                        sendIntent.setType("text/plain");
                        getContext().startActivity(sendIntent);
                    } else {
                        String url = item.getVideoURL().toString();
                        String youTubeID = Utils.getYouTubeID(url);
                        if (youTubePlayer != null && youTubeID != null) {
                            youTubeURL = youTubeID;
                            if (dialog != null && dialog.isShowing())
                                dialog.dismiss();
                            youTubePlayer.cueVideo(youTubeURL, 0);
                            youTubePlayer.play();
                        } else {
                            Uri watchUri = Uri.parse(url);
                            Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                            getContext().startActivity(i);
                        }
                    }
                });

                for (VidURL s : starship.getLiveStreams()) {
                    //Do your stuff here
                    adapter.add(new VideoListItem.Builder(getContext())
                            .content(s.getName())
                            .videoURL(s.getUrl())
                            .build());
                }

                MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext())
                        .title("Select a Source")
                        .content("Long press an item to share.")
                        .adapter(adapter, null)
                        .negativeText("Cancel");
                dialog = builder.show();
            }
        });

    }

    public ArrayList<Object> sortMultiClassList(ArrayList<Object> yourList) {
        Collections.sort(yourList, (Comparator<Object>) (o1, o2) -> {
            if (o1 instanceof LaunchList && o2 instanceof LaunchList) {
                return ((LaunchList) o1).getNet().compareTo(((LaunchList) o2).getNet());
            } else if (o1 instanceof LaunchList && o2 instanceof Event) {
                return ((LaunchList) o1).getNet().compareTo(((Event) o2).getDate());
            } else if (o1 instanceof Event && o2 instanceof LaunchList) {
                return ((Event) o1).getDate().compareTo(((LaunchList) o2).getNet());
            } else if (o1 instanceof Event && o2 instanceof Event) {
                return ((Event) o1).getDate().compareTo(((Event) o2).getDate());
            } else {
                throw new IllegalArgumentException("Don't know how to compare");
            }
        });
        return yourList;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }



}
