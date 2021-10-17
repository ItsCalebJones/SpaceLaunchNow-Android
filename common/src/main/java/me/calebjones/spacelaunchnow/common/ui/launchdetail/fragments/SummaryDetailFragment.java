package me.calebjones.spacelaunchnow.common.ui.launchdetail.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.models.Forecast;
import android.zetterstrom.com.forecast.models.Unit;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.common.base.BaseFragment;
import me.calebjones.spacelaunchnow.common.prefs.ListPreferences;
import me.calebjones.spacelaunchnow.common.ui.adapters.ListAdapter;
import me.calebjones.spacelaunchnow.common.ui.adapters.NewsListAdapter;
import me.calebjones.spacelaunchnow.common.ui.adapters.UpdateAdapter;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.DetailsViewModel;
import me.calebjones.spacelaunchnow.common.ui.views.CountDownTimer;
import me.calebjones.spacelaunchnow.common.ui.views.DialogAdapter;
import me.calebjones.spacelaunchnow.common.utils.SimpleDividerItemDecoration;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.common.youtube.YouTubeAPIHelper;
import me.calebjones.spacelaunchnow.common.youtube.models.Video;
import me.calebjones.spacelaunchnow.common.youtube.models.VideoListItem;
import me.calebjones.spacelaunchnow.common.youtube.models.VideoResponse;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.Pad;
import me.calebjones.spacelaunchnow.data.models.main.VidURL;
import me.calebjones.spacelaunchnow.data.models.main.news.NewsItem;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;

import me.calebjones.spacelaunchnow.common.ui.views.custom.CountDownView;
import me.calebjones.spacelaunchnow.common.ui.views.custom.WeatherCard;
import me.calebjones.spacelaunchnow.data.networking.news.data.Callbacks;
import me.calebjones.spacelaunchnow.data.networking.news.data.NewsDataRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class SummaryDetailFragment extends BaseFragment implements YouTubePlayerListener {

    @BindView(R2.id.countdown_status)
    TextView countdownStatus;
    @BindView(R2.id.countdown_layout)
    CountDownView countDownView;
    @BindView(R2.id.launch_summary)
    NestedScrollView launchSummary;
    @BindView(R2.id.date)
    TextView launchDate;
    @BindView(R2.id.watchButton)
    AppCompatButton watchButton;
    @BindView(R2.id.launch_window_text)
    TextView launchWindowText;
    @BindView(R2.id.error_message)
    TextView errorMessage;
    @BindView(R2.id.weather_card)
    WeatherCard weatherCard;
    @BindView(R2.id.videos_empty)
    TextView videosEmpty;
    @BindView(R2.id.news_recycler_view)
    RecyclerView recyclerView;
    @BindView(R2.id.youtube_view)
    YouTubePlayerView youTubePlayerView;
    @BindView(R2.id.related_card)
    View relatedCard;
    @BindView(R2.id.update_card)
    View updateCard;
    @BindView(R2.id.update_recycler_view)
    RecyclerView updateRecyclerView;

    private SharedPreferences sharedPref;
    private ListPreferences sharedPreference;
    private Context context;
    private CountDownTimer timer;
    public Launch detailLaunch;
    private boolean nightMode;
    private String youTubeURL;
    private Dialog dialog;
    private boolean youTubePlaying = false;
    private int youTubeProgress = 0;
    public Disposable var;
    private boolean future = true;
    private Unbinder unbinder;
    private DetailsViewModel model;
    private YouTubePlayer youTubePlayer;
    private NewsDataRepository dataRepository;
    private NewsListAdapter listAdapter;
    boolean isYouTubePlaying = false;
    private UpdateAdapter updateAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreenName("Summary Detail Fragment");
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.context = getContext();

        sharedPreference = ListPreferences.getInstance(this.context);
        if (sharedPreference.isNightModeActive(context)) {
            nightMode = true;
        } else {
            nightMode = false;
        }

        View view = inflater.inflate(R.layout.detail_launch_summary, container, false);

        unbinder = ButterKnife.bind(this, view);
        dataRepository = new NewsDataRepository(getContext(), getRealm());

        if (savedInstanceState != null) {
            youTubePlaying = savedInstanceState.getBoolean("youTubePlaying", false);
            youTubeProgress = savedInstanceState.getInt("youTubeProgress", 0);
            youTubeURL = savedInstanceState.getString("youTubeID");
        }

        return view;
    }


    @Override
    public void onResume() {

        if (sharedPreference.isNightModeActive(context)) {
            nightMode = true;
        } else {
            nightMode = false;
        }
        super.onResume();
    }

    public void setLaunch(Launch launch) {
        Timber.v("Launch update received: %s", launch.getName());
        detailLaunch = launch;
        setUpViews(launch);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
//        if (youTubePlayer != null) {
//            try {
//                outState.putBoolean("youTubePlaying", youTubePlayer.);
//                outState.putInt("youTubeProgress", );
//                outState.putString("youTubeID", youTubeURL);
//            } catch (IllegalStateException e) {
//                Timber.e(e);
//            }
//        }
        super.onSaveInstanceState(outState);
    }

    private void setUpViews(Launch launch) {
        try {
            getLifecycle().addObserver(youTubePlayerView);
            videosEmpty.setVisibility(View.GONE);
            detailLaunch = launch;

            fetchRelatedNews(launch.getId());

            setupCountdownTimer(launch);

            //Setup SimpleDateFormat to parse out getNet launchDate.
            SimpleDateFormat input = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss zzz");
            SimpleDateFormat output = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy");
            input.toLocalizedPattern();

            Date mDate;
            String dateText = null;

            if (detailLaunch.getVidURLs() != null && detailLaunch.getVidURLs().size() > 0) {

                for (VidURL url : detailLaunch.getVidURLs()) {
                    youTubeURL = Utils.getYouTubeID(url.getUrl());
                    if (youTubeURL != null) break;
                }

                if (youTubeURL != null) {
                    Timber.v("Loading %s", youTubeURL);
                    youTubePlayerView.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.GONE);
//                    youTubePlayerView.getPlayerUIController().enableLiveVideoUI(true);
                    youTubePlayerView.getPlayerUIController().showFullscreenButton(false);
                    youTubePlayerView.initialize(youTubePlayer -> {
                        youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady() {
                                loadVideo(youTubePlayer, youTubeURL);
                            }
                        });
                    }, true);
                }

                watchButton.setVisibility(View.VISIBLE);
                watchButton.setOnClickListener(v -> {
                    Timber.d("Watch: %s", detailLaunch.getVidURLs().size());
                    if (detailLaunch.getVidURLs().size() > 0) {
                        final DialogAdapter adapter = new DialogAdapter((index, item, longClick) -> {
                            if (longClick) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, item.getVideoURL().toString()); // Simple text and URL to share
                                sendIntent.setType("text/plain");
                                context.startActivity(sendIntent);
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
                                    context.startActivity(i);
                                }
                            }
                        });

                        for (VidURL s : detailLaunch.getVidURLs()) {
                            //Do your stuff here
                            adapter.add(new VideoListItem.Builder(context)
                                    .content(s.getName())
                                    .videoURL(s.getUrl())
                                    .build());
                        }

                        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                                .title("Select a Source")
                                .content("Long press an item to share.")
                                .adapter(adapter, null)
                                .negativeText("Cancel");
                        dialog = builder.show();
                    }
                });

            } else {
                if (future) {
                    videosEmpty.setVisibility(View.VISIBLE);
                }
                watchButton.setVisibility(View.GONE);
                errorMessage.setText(getString(R.string.video_source_unavailable));
                errorMessage.setVisibility(View.VISIBLE);
            }

            if (detailLaunch.getUpdates() != null && detailLaunch.getUpdates().size() > 0){
                updateAdapter = new UpdateAdapter(context);
                updateRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                updateRecyclerView.setAdapter(updateAdapter);
                updateAdapter.addItems(detailLaunch.getUpdates());
                updateCard.setVisibility(View.VISIBLE);
            } else {
                updateCard.setVisibility(View.GONE);
            }

            //Try to convert to Month day, Year.
            mDate = detailLaunch.getNet();
            dateText = output.format(mDate);

            launchDate.setText(Html.fromHtml(String.format(getString(R.string.launch_date), dateText)));

            if (detailLaunch.getWindowStart() != null && detailLaunch.getWindowStart() != null) {
                setWindowStamp();
            } else {
                launchWindowText.setVisibility(View.GONE);
            }

        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

    private void fetchRelatedNews(String id) {
        dataRepository.getNewsByLaunch(id, new Callbacks.NewsListCallback() {
            @Override
            public void onNewsLoaded(RealmResults<NewsItem> news) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                    Timber.v(news.toString());
                    if (news.size() > 0) {
                        listAdapter = new NewsListAdapter(context);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(listAdapter);
                        listAdapter.addItems(news);
                        relatedCard.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNetworkStateChanged(boolean refreshing) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {

                }
            }

            @Override
            public void onError(String message, @Nullable Throwable throwable) {
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {

                    if (throwable != null) {
                        Timber.e(throwable);
                    } else {
                        Timber.e(message);
                    }
                }
            }
        });
    }


    private void loadVideo(YouTubePlayer youTubePlayer, String videoId) {
        youTubePlayer.addListener(this);
        if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED)
            youTubePlayer.cueVideo(videoId, 0);
        else
            youTubePlayer.cueVideo(videoId, 0);
    }

    private void setupCountdownTimer(Launch launch) {
        //If timestamp is available calculate TMinus and launchDate.
        if (launch.getNet() != null) {
            countDownView.setLaunch(launch);
        }
    }


    private void setWindowStamp() {
        // Create a DateFormatter object for displaying launchDate in specified format.

        Date windowStart = detailLaunch.getWindowStart();
        Date windowEnd = detailLaunch.getWindowEnd();
        SimpleDateFormat dateFormat;

        if (sharedPref.getString("time_format", "Default").contains("12-Hour")) {
            dateFormat = Utils.getSimpleDateFormatForUI("h:mm a zzz");
        } else if (sharedPref.getString("time_format", "Default").contains("24-Hour")) {
            dateFormat = Utils.getSimpleDateFormatForUI("HH:mm zzz");
        } else if (android.text.format.DateFormat.is24HourFormat(context)) {
            dateFormat = Utils.getSimpleDateFormatForUI("HH:mm zzz");
        } else {
            dateFormat = Utils.getSimpleDateFormatForUI("h:mm a zzz");
        }

        dateFormat.toLocalizedPattern();

        if (!sharedPref.getBoolean("local_time", false)){
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        } else {
            dateFormat.setTimeZone(TimeZone.getDefault());
        }


        if (windowStart.equals(windowEnd)) {
            // Window Start and Window End match - meaning instantaneous.

            TimeZone timeZone = dateFormat.getTimeZone();

            launchWindowText.setText(Html.fromHtml(String.format(getString(R.string.instantaneous_launch_window),
                    dateFormat.format(windowStart))));
        } else if (windowStart.after(windowEnd)) {
            // Launch data is not trustworthy - start is after end.

            TimeZone timeZone = dateFormat.getTimeZone();

            launchWindowText.setText(dateFormat.format(windowStart));
        } else if (windowStart.before(windowEnd)) {
            // Launch Window is properly configured

            String difference = Utils.printDifference(windowStart, windowEnd);
            launchWindowText.setText(Html.fromHtml(String.format(getString(R.string.launch_window_extras),
                    dateFormat.format(windowStart),
                    dateFormat.format(windowEnd),
                    difference)));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        model = ViewModelProviders.of(getActivity()).get(DetailsViewModel.class);
        // update UI
        model.getLaunch().observe(this, this::setLaunch);
    }

    public static SummaryDetailFragment newInstance() {
        return new SummaryDetailFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
        unbinder.unbind();
    }
    @Override
    public void onReady() {

    }

    @Override
    public void onStateChange(@NonNull PlayerConstants.PlayerState state) {
        if (state == PlayerConstants.PlayerState.PLAYING) {
            isYouTubePlaying = true;
        } else {
            isYouTubePlaying = false;
        }
    }

    @Override
    public void onPlaybackQualityChange(@NonNull PlayerConstants.PlaybackQuality playbackQuality) {

    }

    @Override
    public void onPlaybackRateChange(@NonNull PlayerConstants.PlaybackRate playbackRate) {

    }

    @Override
    public void onError(@NonNull PlayerConstants.PlayerError error) {

    }

    @Override
    public void onApiChange() {

    }

    @Override
    public void onCurrentSecond(float second) {

    }

    @Override
    public void onVideoDuration(float duration) {

    }

    @Override
    public void onVideoLoadedFraction(float loadedFraction) {

    }

    @Override
    public void onVideoId(@NonNull String videoId) {
        YouTubeAPIHelper youTubeAPIHelper = new YouTubeAPIHelper(context,
                context.getResources().getString(R.string.GoogleMapsKey));
        youTubePlayerView.getPlayerUIController().enableLiveVideoUI(false);
        youTubeAPIHelper.getVideoById(videoId, new retrofit2.Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Video> videos = response.body().getVideos();
                    if (videos.size() > 0) {
                        try {
                            if (videos.get(0).getSnippet().getLiveBroadcastContent().contains("live")){
                                youTubePlayerView.getPlayerUIController().enableLiveVideoUI(true);
                            }
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
            }
        });
    }
}