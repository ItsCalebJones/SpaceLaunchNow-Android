package me.calebjones.spacelaunchnow.ui.launchdetail.fragments;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.models.Forecast;
import android.zetterstrom.com.forecast.models.Unit;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.crashlytics.android.Crashlytics;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.mypopsy.maps.StaticMap;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseFragment;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.util.DialogAdapter;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.Pad;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.ui.launchdetail.DetailsViewModel;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.GlideApp;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import me.calebjones.spacelaunchnow.utils.views.CountDownTimer;
import me.calebjones.spacelaunchnow.utils.views.custom.CountDownView;
import me.calebjones.spacelaunchnow.utils.views.custom.WeatherCard;
import me.calebjones.spacelaunchnow.utils.youtube.YouTubeAPIHelper;
import me.calebjones.spacelaunchnow.utils.youtube.models.Video;
import me.calebjones.spacelaunchnow.utils.youtube.models.VideoResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class SummaryDetailFragment extends BaseFragment implements YouTubePlayer.OnInitializedListener {

    @BindView(R.id.countdown_status)
    TextView countdownStatus;
    @BindView(R.id.countdown_layout)
    CountDownView countDownView;
    @BindView(R.id.launch_summary)
    NestedScrollView launchSummary;
    @BindView(R.id.map_view_summary)
    ImageView mapView;
    @BindView(R.id.date)
    TextView launchDate;
    @BindView(R.id.watchButton)
    AppCompatButton watchButton;
    @BindView(R.id.launch_window_text)
    TextView launchWindowText;
    @BindView(R.id.error_message)
    TextView errorMessage;
    @BindView(R.id.youtube_view)
    View youTubeView;
    @BindView(R.id.weather_card)
    WeatherCard weatherCard;
    @BindView(R.id.videos_empty)
    TextView videosEmpty;

    private SharedPreferences sharedPref;
    private ListPreferences sharedPreference;
    private Context context;
    private CountDownTimer timer;
    public Launch detailLaunch;
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private YouTubePlayer summaryYouTubePlayer;
    private boolean nightMode;
    private String youTubeURL;
    private Dialog dialog;
    private boolean youTubePlaying = false;
    private int youTubeProgress = 0;
    public Disposable var;
    private boolean future = true;
    private Unbinder unbinder;
    private DetailsViewModel model;

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
        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_view, youTubePlayerFragment).commit();

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

    private void fetchPastWeather() {
        future = false;
        weatherCard.setTitle("Launch Day Weather");
        if (detailLaunch.getPad() != null) {

            Pad pad = detailLaunch.getPad();

            double latitude = Double.parseDouble(pad.getLatitude());
            double longitude = Double.parseDouble(pad.getLongitude());

            Unit unit;

            if (sharedPref.getBoolean("weather_US_SI", true)) {
                unit = Unit.US;
            } else {
                unit = Unit.SI;
            }

            long longTime = detailLaunch.getNet().getTime() / 1000;
            int time = (int) longTime;
            ForecastClient.getInstance().getForecast(latitude, longitude, time, null, unit, null, false, new Callback<Forecast>() {
                @Override
                public void onResponse(Call<Forecast> forecastCall, Response<Forecast> response) {
                    if (response.isSuccessful()) {
                        Forecast forecast = response.body();
                        if (SummaryDetailFragment.this.isVisible()) {
                            Analytics.getInstance().sendWeatherEvent(detailLaunch.getName(), true, "Success");
                            updateWeatherView(forecast);
                        }
                    } else {
                        Analytics.getInstance().sendWeatherEvent(detailLaunch.getName(), false, response.errorBody().toString());
                        Timber.e("Error: %s", response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Forecast> forecastCall, Throwable t) {
                    Analytics.getInstance().sendWeatherEvent(detailLaunch.getName(), false, t.getLocalizedMessage());
                    Timber.e("ERROR: %s", t.getLocalizedMessage());
                }
            });
        }
    }

    private void updateWeatherView(Forecast forecast) {
        weatherCard.setWeather(forecast, detailLaunch.getPad().getLocation().getName(), future, nightMode);
        weatherCard.setVisibility(View.VISIBLE);
    }

    private void fetchCurrentWeather() {
        future = true;
        // Sample WeatherLib client init
        if (detailLaunch.getPad() != null) {

            Pad pad = detailLaunch.getPad();

            double latitude = Double.parseDouble(pad.getLatitude());
            double longitude = Double.parseDouble(pad.getLongitude());

            Unit unit;

            if (sharedPref.getBoolean("weather_US_SI", true)) {
                unit = Unit.US;
            } else {
                unit = Unit.SI;
            }

            ForecastClient.getInstance()
                    .getForecast(latitude, longitude, null, null, unit, null, false, new Callback<Forecast>() {
                        @Override
                        public void onResponse(Call<Forecast> forecastCall, Response<Forecast> response) {
                            if (response.isSuccessful()) {
                                Forecast forecast = response.body();
                                if (SummaryDetailFragment.this.isVisible()) {
                                    updateWeatherView(forecast);
                                }
                            } else {
                                Timber.e("Error: %s", response.errorBody());
                            }
                        }

                        @Override
                        public void onFailure(Call<Forecast> forecastCall, Throwable t) {
                            Timber.e("ERROR: %s", t.getLocalizedMessage());
                        }
                    });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (summaryYouTubePlayer != null) {
            try {
                outState.putBoolean("youTubePlaying", summaryYouTubePlayer.isPlaying());
                outState.putInt("youTubeProgress", summaryYouTubePlayer.getCurrentTimeMillis());
                outState.putString("youTubeID", youTubeURL);
            } catch (IllegalStateException e) {
                Timber.e(e);
            }
        }
        super.onSaveInstanceState(outState);
    }

    private void setUpViews(Launch launch) {
        try {
            weatherCard.setVisibility(View.GONE);
            videosEmpty.setVisibility(View.GONE);
            detailLaunch = launch;

            // Check if Weather card is enabled, defaults to false if null.
            if (sharedPref.getBoolean("weather", false)) {
                if (detailLaunch.getNet().after(Calendar.getInstance().getTime())) {
                    fetchCurrentWeather();
                } else {
                    fetchPastWeather();
                }
            }

            setupCountdownTimer(launch);

            double dlat = 0;
            double dlon = 0;
            if (detailLaunch.getPad() != null) {
                dlat = Double.parseDouble(detailLaunch.getPad().getLatitude());
                dlon = Double.parseDouble(detailLaunch.getPad().getLongitude());
            }

            // Getting status
            if (dlat == 0 && dlon == 0 || Double.isNaN(dlat) || Double.isNaN(dlon) || dlat == Double.NaN || dlon == Double.NaN) {
                if (mapView != null) {
                    mapView.setVisibility(View.GONE);
                }
            } else {
                mapView.setVisibility(View.VISIBLE);
                final Resources res = context.getResources();
                final StaticMap map = new StaticMap()
                        .center(dlat, dlon)
                        .scale(4)
                        .type(StaticMap.Type.ROADMAP)
                        .zoom(7)
                        .marker(dlat, dlon)
                        .key(res.getString(R.string.GoogleMapsKey));

                //Strange but necessary to calculate the height/width
                mapView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        map.size(
                                mapView.getWidth() / 2,
                                mapView.getHeight() / 2
                        );

                        Timber.v("onPreDraw: %s", map.toString());
                        GlideApp.with(context)
                                .load(map.toString())
                                .error(R.drawable.placeholder)
                                .centerCrop()
                                .into(mapView);
                        mapView.getViewTreeObserver().removeOnPreDrawListener(this);
                        return true;
                    }
                });
            }

            //Setup SimpleDateFormat to parse out getNet launchDate.
            SimpleDateFormat input = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss zzz");
            SimpleDateFormat output = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy");
            input.toLocalizedPattern();

            Date mDate;
            String dateText = null;

            if (detailLaunch.getVidURLs() != null && detailLaunch.getVidURLs().size() > 0) {

                for (RealmStr url : detailLaunch.getVidURLs()) {
                    youTubeURL = getYouTubeID(url.getVal());
                    if (youTubeURL != null) break;
                }

                if (youTubeURL != null) {
                    Timber.v("Loading %s", youTubeURL);
                    mapView.setVisibility(View.GONE);
                    youTubeView.setVisibility(View.VISIBLE);
                    errorMessage.setVisibility(View.GONE);
                    final LaunchDetailActivity mainActivity = (LaunchDetailActivity) getActivity();
                    youTubePlayerFragment.initialize(context.getResources().getString(R.string.GoogleMapsKey), new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
                            mainActivity.youTubePlayer = youTubePlayer;
                            summaryYouTubePlayer = youTubePlayer;
                            youTubePlayer.cueVideo(youTubeURL);
                            Timber.v("YouTube Player - initialized: Progress - %s isPlaying - %s", youTubeProgress, youTubePlaying);
                            youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                                @Override
                                public void onFullscreen(boolean b) {
                                    Timber.v("onFullscreen");
                                    mainActivity.isYouTubePlayerFullScreen = b;
                                }
                            });
                            youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                                @Override
                                public void onPlaying() {
                                    Timber.v("onPlaying");
                                    mainActivity.videoPlaying();
                                }

                                @Override
                                public void onPaused() {
                                    Timber.v("onPaused");
                                    mainActivity.videoStopped();
                                }

                                @Override
                                public void onStopped() {
                                    Timber.v("onStopped");
                                    mainActivity.videoStopped();
                                }

                                @Override
                                public void onBuffering(boolean b) {
                                    Timber.v("onBuffering %s", b);
                                }

                                @Override
                                public void onSeekTo(int i) {
                                    Timber.v("onSeekTo %s", i);
                                }
                            });
                            youTubePlayer.setPlaylistEventListener(new YouTubePlayer.PlaylistEventListener() {
                                @Override
                                public void onPrevious() {
                                    Timber.v("onPrevious");
                                }

                                @Override
                                public void onNext() {
                                    Timber.v("onNext");
                                }

                                @Override
                                public void onPlaylistEnded() {
                                    Timber.v("onPlaylistEnded");
                                }
                            });
                            youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                                @Override
                                public void onLoading() {
                                    Timber.v("onLoading");
                                }

                                @Override
                                public void onLoaded(String s) {
                                    Timber.v("onLoaded %s", s);
                                    if (youTubeURL.contains("live")) {
                                        errorMessage.setVisibility(View.VISIBLE);
                                        errorMessage.setText(String.format("Live Broadcast %s", s));
                                    } else {
                                        errorMessage.setVisibility(View.GONE);
                                    }
                                    checkState();
                                }

                                @Override
                                public void onAdStarted() {
                                    Timber.v("onAdStarted");
                                }

                                @Override
                                public void onVideoStarted() {
                                    Timber.v("onVideoStarted");
                                }

                                @Override
                                public void onVideoEnded() {
                                    Timber.v("onVideoEnded");
                                }

                                @Override
                                public void onError(YouTubePlayer.ErrorReason errorReason) {
                                    Timber.v("onError - %s", errorReason.name());
                                    if (youTubeURL.contains("live")) {
                                        errorMessage.setVisibility(View.VISIBLE);
                                        errorMessage.setText("Broadcast may not be live.");
                                    } else {
                                        errorMessage.setVisibility(View.GONE);
                                    }
                                    if (errorReason.ordinal() == 4) {
                                        Toast.makeText(mainActivity, "Playback paused by YouTube while view is obstructed.", Toast.LENGTH_SHORT).show();
                                    }
                                    Crashlytics.log(errorReason.name());
                                }
                            });
                            youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);

                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                            if (youTubeInitializationResult.isUserRecoverableError()) {
                                youTubeInitializationResult.getErrorDialog(getActivity(), 1).show();
                            } else {
                                String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
                                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                watchButton.setVisibility(View.VISIBLE);
                watchButton.setOnClickListener(v -> {
                    Timber.d("Watch: %s", detailLaunch.getVidURLs().size());
                    if (detailLaunch.getVidURLs().size() > 0) {
                        final DialogAdapter adapter = new DialogAdapter((index, item, longClick) -> {
                            if (longClick) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, detailLaunch.getVidURLs().get(index).getVal()); // Simple text and URL to share
                                sendIntent.setType("text/plain");
                                context.startActivity(sendIntent);
                            } else {
                                String url = detailLaunch.getVidURLs().get(index).getVal();
                                String youTubeID = getYouTubeID(url);
                                if (summaryYouTubePlayer != null && youTubeID != null) {
                                    youTubeURL = youTubeID;
                                    if (dialog != null && dialog.isShowing())
                                        dialog.dismiss();
                                    summaryYouTubePlayer.cueVideo(youTubeURL);
                                    summaryYouTubePlayer.play();
                                } else {
                                    Uri watchUri = Uri.parse(url);
                                    Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                                    context.startActivity(i);
                                }
                            }
                        });

                        for (RealmStr s : detailLaunch.getVidURLs()) {
                            //Do your stuff here
                            try {
                                URI uri = new URI(s.getVal());

                                String name;
                                YouTubeAPIHelper youTubeAPIHelper = new YouTubeAPIHelper(context, context.getResources().getString(R.string.GoogleMapsKey));
                                if (uri.getHost().contains("youtube")) {
                                    name = "YouTube";
                                    String youTubeURL = getYouTubeID(s.getVal());
                                    if (youTubeURL.contains("spacex/live")) {
                                        adapter.add(new MaterialSimpleListItem.Builder(context)
                                                .content("YouTube - SpaceX Livestream")
                                                .build());
                                    } else {
                                        youTubeAPIHelper.getVideoById(youTubeURL,
                                                new Callback<VideoResponse>() {
                                                    @Override
                                                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                                                        if (response.isSuccessful()) {
                                                            if (response.body() != null) {
                                                                List<Video> videos = response.body().getVideos();
                                                                if (videos.size() > 0) {
                                                                    try {
                                                                        adapter.add(new MaterialSimpleListItem.Builder(context)
                                                                                .content(videos.get(0).getSnippet().getTitle())
                                                                                .build());
                                                                    } catch (Exception e) {
                                                                        adapter.add(new MaterialSimpleListItem.Builder(context)
                                                                                .content(name)
                                                                                .build());
                                                                    }
                                                                } else {
                                                                    adapter.add(new MaterialSimpleListItem.Builder(context)
                                                                            .content(name)
                                                                            .build());
                                                                }
                                                            } else {
                                                                adapter.add(new MaterialSimpleListItem.Builder(context)
                                                                        .content(name)
                                                                        .build());
                                                            }
                                                        } else {
                                                            adapter.add(new MaterialSimpleListItem.Builder(context)
                                                                    .content(name)
                                                                    .build());
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<VideoResponse> call, Throwable t) {
                                                        adapter.add(new MaterialSimpleListItem.Builder(context)
                                                                .content(name)
                                                                .build());
                                                    }
                                                });
                                    }
                                } else {
                                    name = uri.getHost();
                                    adapter.add(new MaterialSimpleListItem.Builder(context)
                                            .content(name)
                                            .build());
                                }

                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
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
                if (future){
                    videosEmpty.setVisibility(View.VISIBLE);
                }
                watchButton.setVisibility(View.GONE);
            }

            //Try to convert to Month day, Year.
            mDate = detailLaunch.getNet();
            dateText = output.format(mDate);

            launchDate.setText(Html.fromHtml("<b>Launch Date</b><br>" + dateText));

            if (detailLaunch.getWindowStart() != null && detailLaunch.getWindowStart() != null) {
                setWindowStamp();
            } else {
                launchWindowText.setVisibility(View.GONE);
            }

        } catch (NullPointerException e) {
            Timber.e(e);
        }
    }

    private void checkState() {
        if (youTubeProgress != 0 && summaryYouTubePlayer != null) {
            summaryYouTubePlayer.seekToMillis(youTubeProgress);
            if (youTubePlaying) {
                summaryYouTubePlayer.play();
            }
        }
    }

    private String getYouTubeID(String vidURL) {
        final String regex = "(youtu\\.be\\/|youtube\\.com\\/(watch\\?(.*&)?v=|(embed|v)\\/|c\\/))([a-zA-Z0-9_-]{11}|[a-zA-Z].*)";
        final Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(vidURL);
        Timber.v("Checking for match of %s", vidURL);
        if (matcher.find() && (matcher.group(1) != null || matcher.group(2) != null) && matcher.group(5) != null) {
            return matcher.group(5);
        }
        return null;
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

        boolean twentyFourHourMode = sharedPref.getBoolean("24_hour_mode", false);
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

        if (windowStart.equals(windowEnd)) {
            // Window Start and Window End match - meaning instantaneous.
            if (twentyFourHourMode) {
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            }

            TimeZone timeZone = dateFormat.getTimeZone();

            launchWindowText.setText(Html.fromHtml(String.format("<b>Instantaneous Launch Window</b><br>%s %s",
                    dateFormat.format(windowStart),
                    timeZone.getDisplayName(false, TimeZone.SHORT))));
        } else if (windowStart.after(windowEnd)) {
            // Launch data is not trustworthy - start is after end.
            if (twentyFourHourMode) {
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            }

            TimeZone timeZone = dateFormat.getTimeZone();

            launchWindowText.setText(String.format("%s %s",
                    dateFormat.format(windowStart),
                    timeZone.getDisplayName(false, TimeZone.SHORT)));
        } else if (windowStart.before(windowEnd)) {
            // Launch Window is properly configured
            if (twentyFourHourMode) {
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            }

            TimeZone timeZone = dateFormat.getTimeZone();
            String difference = Utils.printDifference(windowStart, windowEnd);
            launchWindowText.setText(Html.fromHtml(String.format("<b>Launch Window</b><br>%s - %s %s<br>%s",
                    dateFormat.format(windowStart),
                    dateFormat.format(windowEnd),
                    timeZone.getDisplayName(false, TimeZone.SHORT),
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

    @OnClick(R.id.map_view_summary)
    public void onViewClicked() {
        if (detailLaunch != null && detailLaunch.isValid() && detailLaunch.getPad().getLocation() != null) {
            String location = detailLaunch.getPad().getLocation().getName();
            location = (location.substring(location.indexOf(",") + 1));

            Timber.d("FAB: %s ", location);

            double dlat = Double.parseDouble(detailLaunch.getPad().getLatitude());
            double dlon = Double.parseDouble(detailLaunch.getPad().getLongitude());

            Uri gmmIntentUri = Uri.parse("geo:" + dlat + ", " + dlon + "?z=12&q=" + dlat + ", " + dlon);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            Analytics.getInstance().sendLaunchMapClicked(detailLaunch.getName());

            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                Toast.makeText(context, "Loading " + detailLaunch.getPad().getName(), Toast.LENGTH_LONG).show();
                context.startActivity(mapIntent);
            }
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean restored) {
        if (!restored) {
            youTubePlayer.cueVideo("fhWaJi1Hsfo"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), 1).show();
        } else {
            youTubeView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
        unbinder.unbind();
    }
}
