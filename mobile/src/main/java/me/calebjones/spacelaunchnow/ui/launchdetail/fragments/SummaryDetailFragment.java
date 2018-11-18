package me.calebjones.spacelaunchnow.ui.launchdetail.fragments;

import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
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
import com.mypopsy.maps.StaticMap;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerListener;

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


public class SummaryDetailFragment extends BaseFragment implements YouTubePlayerListener {

    @BindView(R.id.countdown_status)
    TextView countdownStatus;
    @BindView(R.id.countdown_layout)
    CountDownView countDownView;
    @BindView(R.id.launch_summary)
    NestedScrollView launchSummary;
    @BindView(R.id.date)
    TextView launchDate;
    @BindView(R.id.watchButton)
    AppCompatButton watchButton;
    @BindView(R.id.launch_window_text)
    TextView launchWindowText;
    @BindView(R.id.error_message)
    TextView errorMessage;
    @BindView(R.id.weather_card)
    WeatherCard weatherCard;
    @BindView(R.id.videos_empty)
    TextView videosEmpty;
    @BindView(R.id.youtube_view)
    YouTubePlayerView youTubePlayerView;

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
    boolean isYouTubePlaying = false;

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
                        final DialogAdapter adapter = new DialogAdapter(context, (index, item, longClick) -> {
                            if (longClick) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, detailLaunch.getVidURLs().get(index).getVal()); // Simple text and URL to share
                                sendIntent.setType("text/plain");
                                context.startActivity(sendIntent);
                            } else {
                                String url = detailLaunch.getVidURLs().get(index).getVal();
                                String youTubeID = getYouTubeID(url);
                                if (youTubePlayer != null && youTubeID != null) {
                                    youTubeURL = youTubeID;
                                    if (dialog != null && dialog.isShowing())
                                        dialog.dismiss();
                                    youTubePlayer.loadVideo(youTubeURL, 0);
                                } else {
                                    Uri watchUri = Uri.parse(url);
                                    Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                                    context.startActivity(i);
                                }
                            }
                        });

                        for (RealmStr string : detailLaunch.getVidURLs()) {
                            adapter.add(new MaterialSimpleListItem.Builder(context)
                                    .content(string.getVal())
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
                errorMessage.setText("Video sources unavailable.");
                errorMessage.setVisibility(View.VISIBLE);
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


    private void loadVideo(YouTubePlayer youTubePlayer, String videoId) {
        youTubePlayer.addListener(this);
        if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED)
            youTubePlayer.loadVideo(videoId, 0);
        else
            youTubePlayer.cueVideo(videoId, 0);
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

        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

        if (windowStart.equals(windowEnd)) {
            // Window Start and Window End match - meaning instantaneous.

            TimeZone timeZone = dateFormat.getTimeZone();

            launchWindowText.setText(Html.fromHtml(String.format("<b>Instantaneous Launch Window</b><br>%s %s",
                    dateFormat.format(windowStart),
                    timeZone.getDisplayName(false, TimeZone.SHORT))));
        } else if (windowStart.after(windowEnd)) {
            // Launch data is not trustworthy - start is after end.

            TimeZone timeZone = dateFormat.getTimeZone();

            launchWindowText.setText(String.format("%s %s",
                    dateFormat.format(windowStart),
                    timeZone.getDisplayName(false, TimeZone.SHORT)));
        } else if (windowStart.before(windowEnd)) {
            // Launch Window is properly configured

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
