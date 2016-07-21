package me.calebjones.spacelaunchnow.ui.fragment.launches.details;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.zetterstrom.com.forecast.ForecastClient;
import android.zetterstrom.com.forecast.models.Forecast;
import android.zetterstrom.com.forecast.models.Unit;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.bumptech.glide.Glide;
import com.github.pwittchen.weathericonview.WeatherIconView;
import com.mypopsy.maps.StaticMap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.content.models.realm.PadRealm;
import me.calebjones.spacelaunchnow.content.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.content.models.realm.RocketDetailsRealm;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.ui.fragment.BaseFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class SummaryDetailFragment extends BaseFragment {

    private SharedPreferences sharedPref;
    private static ListPreferences sharedPreference;
    private Context context;

    public static LaunchRealm detailLaunch;
    private RocketDetailsRealm launchVehicle;
    private boolean nightmode;

    @BindView(R.id.map_view_summary)
    ImageView staticMap;
    @BindView(R.id.fab_explore)
    FloatingActionButton fab;
    @BindView(R.id.launch_date_title)
    TextView launch_date_title;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.launch_window_start)
    TextView launch_window_start;
    @BindView(R.id.launch_window_end)
    TextView launch_window_end;
    @BindView(R.id.launch_status)
    TextView launch_status;
    @BindView(R.id.watchButton)
    AppCompatButton watchButton;
    @BindView(R.id.weather_card)
    CardView weatherCard;
    @BindView(R.id.weather_icon)
    WeatherIconView weatherIconView;
    @BindView(R.id.weather_current_temp)
    TextView weatherCurrentTemp;
    @BindView(R.id.weather_feels_like)
    TextView weatherFeelsLike;
    @BindView(R.id.weather_low_high)
    TextView weatherLowHigh;
    @BindView(R.id.weather_location)
    TextView weatherLocation;
    @BindView(R.id.weather_percip_chance)
    TextView weatherPrecip;
    @BindView(R.id.weather_wind_speed)
    TextView weatherWindSpeed;
    @BindView(R.id.weather_summary_day)
    TextView weatherSummaryDay;
    @BindView(R.id.day_two_weather_icon)
    WeatherIconView dayTwoWeatherIconView;
    @BindView(R.id.day_two_low_high)
    TextView dayTwoWeatherLowHigh;
    @BindView(R.id.day_two_precip_prob)
    TextView dayTwoWeatherPrecip;
    @BindView(R.id.day_two_weather_wind_speed)
    TextView dayTwoWeatherWindSpeed;
    @BindView(R.id.day_two_day)
    TextView dayTwoDay;
    @BindView(R.id.day_three_weather_icon)
    WeatherIconView dayThreeWeatherIconView;
    @BindView(R.id.day_three_low_high)
    TextView dayThreeWeatherLowHigh;
    @BindView(R.id.day_three_precip_prob)
    TextView dayThreeWeatherPrecip;
    @BindView(R.id.day_three_weather_wind_speed)
    TextView dayThreeWeatherWindSpeed;
    @BindView(R.id.day_three_day)
    TextView dayThreeDay;
    @BindView(R.id.day_four_weather_icon)
    WeatherIconView dayFourWeatherIconView;
    @BindView(R.id.day_four_low_high)
    TextView dayFourWeatherLowHigh;
    @BindView(R.id.day_four_precip_prob)
    TextView dayFourWeatherPrecip;
    @BindView(R.id.day_four_weather_wind_speed)
    TextView dayFourWeatherWindSpeed;
    @BindView(R.id.day_four_day)
    TextView dayFourDay;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        this.context = getContext();

        sharedPreference = ListPreferences.getInstance(this.context);

        if (sharedPreference.getNightMode()) {
            nightmode = true;
            view = inflater.inflate(R.layout.dark_launch_summary, container, false);
        } else {
            nightmode = false;
            view = inflater.inflate(R.layout.light_launch_summary, container, false);
        }

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        detailLaunch = ((LaunchDetailActivity) getActivity()).getLaunch();
        setUpViews();
        fetchCurrentWeather();
        //TODO enforce paywall
//        if (sharedPref.getBoolean("weather", false)) {
//        } else {
//            weatherCard.setVisibility(View.GONE);
//        }
        super.onResume();
    }


    private void fetchCurrentWeather() {
        // Sample WeatherLib client init
        if (detailLaunch.getLocation().getPads().size() > 0) {

            PadRealm pad = detailLaunch.getLocation().getPads().get(0);

            double latitude = pad.getLatitude();
            double longitude = pad.getLongitude();

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
                                updateWeatherView(forecast);
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

    private void updateWeatherView(Forecast forecast) {
        final String temp;
        final String speed;
        String precip;
        String pressure;
        String visibility;

        if (sharedPref.getBoolean("weather_US_SI", true)) {
            temp = "F";
            speed = "Mph";
            precip = "in.";
            pressure = "mb";
            visibility = "mile";
        } else {
            temp = "C";
            speed = "m/s";
            precip = "cm";
            pressure = "hPa";
            visibility = "km";
        }
        if (forecast.getCurrently() != null) {
            if (forecast.getCurrently().getTemperature() != null) {
                String currentTemp = String.valueOf(Math.round(forecast.getCurrently().getTemperature())) + (char) 0x00B0 + " " + temp;
                weatherCurrentTemp.setText(currentTemp);
            }
            if (forecast.getCurrently().getApparentTemperature() != null) {
                String feelsLikeTemp = "Feels like " + String.valueOf(Math.round(forecast.getCurrently().getApparentTemperature())) + (char) 0x00B0;
                weatherFeelsLike.setText(feelsLikeTemp);
            }

            if (forecast.getCurrently().getWindSpeed() != null) {
                String windSpeed = String.valueOf(Math.round(forecast.getCurrently().getWindSpeed())) + " " + speed;
                weatherWindSpeed.setText(windSpeed);
            }
        }
        if (forecast.getDaily() != null && forecast.getDaily().getDataPoints() != null && forecast.getDaily().getDataPoints().size() > 0) {
            String highTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(0).getTemperatureMax()));
            String lowTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(0).getTemperatureMin()));
            String lowHigh = lowTemp + (char) 0x00B0 + " " + temp + " | " + highTemp + (char) 0x00B0+ " " + temp;
            weatherLowHigh.setText(lowHigh);

            if (forecast.getDaily().getDataPoints().get(0).getPrecipProbability() != null) {
                double testPrecip = forecast.getDaily().getDataPoints().get(0).getPrecipProbability();
                String precipProb = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(0).getPrecipProbability() * 100) + "%");
                weatherPrecip.setText(precipProb);
            }

            if (forecast.getDaily().getDataPoints().size() >= 3){
                //Day One!
                setIconView(dayTwoWeatherIconView, forecast.getDaily().getDataPoints().get(1).getIcon().getText());

                //Get Low - High temp
                String dayTwoHighTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(1).getTemperatureMax()));
                String dayTwoLowTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(1).getTemperatureMin()));
                String dayTwoLowHigh = lowTemp + (char) 0x00B0 + " " + temp + " | " + highTemp + (char) 0x00B0+ " " + temp;

                //Get rain prop
                String dayTwoPrecipProb = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(1).getPrecipProbability() * 100) + "%");

                //Get Wind speed
                String dayTwoWindSpeed = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(1).getWindSpeed())) + " " + speed;

                //Get day date
                String dayTwoDate = new SimpleDateFormat("EE").format(forecast.getDaily().getDataPoints().get(1).getTime());

                dayTwoWeatherLowHigh.setText(dayTwoLowHigh);
                dayTwoWeatherPrecip.setText(dayTwoPrecipProb);
                dayTwoWeatherWindSpeed.setText(dayTwoWindSpeed);
                dayTwoDay.setText(dayTwoDate);

                //Day Two!
                setIconView(dayThreeWeatherIconView, forecast.getDaily().getDataPoints().get(2).getIcon().getText());

                //Get Low - High temp
                String dayThreeHighTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(2).getTemperatureMax()));
                String dayThreeLowTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(2).getTemperatureMin()));
                String dayThreeLowHigh = lowTemp + (char) 0x00B0 + " " + temp + " | " + highTemp + (char) 0x00B0+ " " + temp;

                //Get rain prop
                String dayThreePrecipProb = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(2).getPrecipProbability() * 100) + "%");

                //Get Wind speed
                String dayThreeWindSpeed = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(2).getWindSpeed())) + " " + speed;

                //Get day date
                String dayThreeDate = new SimpleDateFormat("EE").format(forecast.getDaily().getDataPoints().get(2).getTime());

                dayThreeWeatherLowHigh.setText(dayThreeLowHigh);
                dayThreeWeatherPrecip.setText(dayThreePrecipProb);
                dayThreeWeatherWindSpeed.setText(dayThreeWindSpeed);
                dayThreeDay.setText(dayThreeDate);

                //Day Three!
                setIconView(dayFourWeatherIconView, forecast.getDaily().getDataPoints().get(3).getIcon().getText());

                //Get Low - High temp
                String dayFourHighTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(3).getTemperatureMax()));
                String dayFourLowTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(3).getTemperatureMin()));
                String dayFourLowHigh = lowTemp + (char) 0x00B0 + " " + temp + " | " + highTemp + (char) 0x00B0+ " " + temp;

                String dayFourPrecipProb = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(3).getPrecipProbability() * 100) + "%");

                //Get Wind speed
                String dayFourWindSpeed = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(3).getWindSpeed())) + " " + speed;

                //Get day date
                String dayFourDate = new SimpleDateFormat("EE").format(forecast.getDaily().getDataPoints().get(3).getTime());

                dayFourWeatherLowHigh.setText(dayFourLowHigh);
                dayFourWeatherPrecip.setText(dayFourPrecipProb);
                dayFourWeatherWindSpeed.setText(dayFourWindSpeed);
                dayFourDay.setText(dayFourDate);
            }
        }

        if (forecast.getCurrently().getIcon() != null && forecast.getCurrently().getIcon().getText() != null) {
            setIconView(weatherIconView, forecast.getCurrently().getIcon().getText());
        }


        weatherSummaryDay.setText(forecast.getDaily().getSummary());
        weatherLocation.setText(detailLaunch.getLocation().getName());
        weatherCard.setVisibility(View.VISIBLE);
    }

    private void setIconView(WeatherIconView view, String icon) {
        if (icon.contains("partly-cloudy-day")) {
            view.setIconResource(getString(R.string.wi_forecast_io_partly_cloudy_day));
        } else if (icon.contains("partly-cloudy-night")) {
            view.setIconResource(getString(R.string.wi_forecast_io_partly_cloudy_night));
        } else if (icon.contains("clear-day")) {
            view.setIconResource(getString(R.string.wi_forecast_io_clear_day));
        } else if (icon.contains("clear-night")) {
            view.setIconResource(getString(R.string.wi_forecast_io_clear_night));
        } else if (icon.contains("rain")) {
            view.setIconResource(getString(R.string.wi_forecast_io_rain));
        } else if (icon.contains("snow")) {
            view.setIconResource(getString(R.string.wi_forecast_io_snow));
        } else if (icon.contains("sleet")) {
            view.setIconResource(getString(R.string.wi_forecast_io_sleet));
        } else if (icon.contains("wind")) {
            view.setIconResource(getString(R.string.wi_forecast_io_wind));
        } else if (icon.contains("fog")) {
            view.setIconResource(getString(R.string.wi_forecast_io_fog));
        } else if (icon.contains("cloudy")) {
            view.setIconResource(getString(R.string.wi_forecast_io_cloudy));
        } else if (icon.contains("hail")) {
            view.setIconResource(getString(R.string.wi_forecast_io_hail));
        } else if (icon.contains("thunderstorm")) {
            view.setIconResource(getString(R.string.wi_forecast_io_thunderstorm));
        } else if (icon.contains("tornado")) {
            view.setIconResource(getString(R.string.wi_forecast_io_tornado));
        }
        if (nightmode){
            view.setIconColor(Color.WHITE);
        } else {
            view.setIconColor(Color.BLACK);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setUpViews() {
        if (detailLaunch.getRocket() != null) {
            getLaunchVehicle(detailLaunch);
        }

        double dlat = 0;
        double dlon = 0;
        if (detailLaunch.getLocation() != null && detailLaunch.getLocation().getPads() != null) {
            dlat = detailLaunch.getLocation().getPads().get(0).getLatitude();
            dlon = detailLaunch.getLocation().getPads().get(0).getLongitude();
        }

        // Getting status
        if (dlat == 0 && dlon == 0 || Double.isNaN(dlat) || Double.isNaN(dlon) || dlat == Double.NaN || dlon == Double.NaN) {
            if (staticMap != null) {
                staticMap.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            }
        } else {
            staticMap.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
//                holder.setMapLocation(dlat, dlon);
//                // Keep track of MapView
//                mMaps.add(holder.staticMap);
            final Resources res = context.getResources();
            final StaticMap map = new StaticMap()
                    .center(dlat, dlon)
                    .scale(4)
                    .type(StaticMap.Type.ROADMAP)
                    .zoom(7)
                    .marker(dlat, dlon)
                    .key(res.getString(R.string.GoogleMapsKey));

            //Strange but necessary to calculate the height/width
            staticMap.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    map.size(staticMap.getWidth() / 2,
                            staticMap.getHeight() / 2);

                    Timber.v("onPreDraw: %s", map.toString());
                    Glide.with(context).load(map.toString())
                            .error(R.drawable.placeholder)
                            .centerCrop()
                            .into(staticMap);
                    staticMap.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }

        //Setup SimpleDateFormat to parse out getNet date.
        SimpleDateFormat input = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss zzz");
        SimpleDateFormat output = new SimpleDateFormat("MMMM dd, yyyy");
        input.toLocalizedPattern();

        Date mDate;
        String dateText = null;

        switch (detailLaunch.getStatus()) {
            case 1:
                launch_status.setText("Launch is GO");
                break;
            case 2:
                launch_status.setText("Launch is NO-GO");
                break;
            case 3:
                launch_status.setText("Launch was successful.");
                break;
            case 4:
                launch_status.setText("Launch failure occurred.");
                break;
        }


        if (detailLaunch.getVidURLs() != null && detailLaunch.getVidURLs().size() > 0) {
            watchButton.setVisibility(View.VISIBLE);
            watchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Timber.d("Watch: %s", detailLaunch.getVidURLs().size());
                    if (detailLaunch.getVidURLs().size() > 0) {
                        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(context);
                        for (RealmStr s : detailLaunch.getVidURLs()) {
                            //Do your stuff here
                            adapter.add(new MaterialSimpleListItem.Builder(context)
                                    .content(s.getVal())
                                    .build());
                        }

                        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                                .title("Select a source:")
                                .adapter(adapter, new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        Uri watchUri = Uri.parse(detailLaunch.getVidURLs().get(which).getVal());
                                        Intent i = new Intent(Intent.ACTION_VIEW, watchUri);
                                        context.startActivity(i);
                                        dialog.dismiss();
                                    }
                                });
                        if (sharedPreference.getNightMode()) {
                            builder.theme(Theme.DARK);
                        }
                        builder.show();
                    }
                }
            });
        } else {
            watchButton.setVisibility(View.GONE);
        }

        //Try to convert to Month day, Year.
        mDate = detailLaunch.getNet();
        dateText = output.format(mDate);
        if (mDate.before(Calendar.getInstance().getTime())) {
            launch_date_title.setText("Launch Date");
        }

        date.setText(dateText);

        //TODO: Get launch window only if wstamp and westamp is available, hide otherwise.
        if (detailLaunch.getWsstamp() > 0 && detailLaunch.getWestamp() > 0) {
            setWindowStamp();
        } else {
            launch_window_start.setVisibility(View.GONE);
            launch_window_end.setVisibility(View.GONE);
        }

    }

    private void getLaunchVehicle(LaunchRealm vehicle) {
        String query;
        if (vehicle.getRocket().getName().contains("Space Shuttle")) {
            query = "Space Shuttle";
        } else {
            query = vehicle.getRocket().getName();
        }

        launchVehicle = getRealm().where(RocketDetailsRealm.class).contains("name", query).findFirst();
    }

    private void setWindowStamp() {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter;
        if (sharedPref.getBoolean("24_hour_mode", false)) {
            formatter = new SimpleDateFormat("kk:mm zzz");
        } else {
            formatter = new SimpleDateFormat("hh:mm a zzz");
        }

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendarStart = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long startDate = detailLaunch.getWsstamp();
        startDate = startDate * 1000;
        calendarStart.setTimeInMillis(startDate);
        String start = formatter.format(calendarStart.getTime());

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendarEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long endDate = detailLaunch.getWestamp();
        endDate = endDate * 1000;
        calendarEnd.setTimeInMillis(endDate);
        String end = formatter.format(calendarEnd.getTime());

        if (!start.equals(end)) {
            if (start.length() == 0
                    || end.length() == 0) {
                launch_window_start.setText(String.format("Launch Time: %s",
                        start));
                launch_window_end.setVisibility(View.GONE);
            } else {
                launch_window_start.setText(String.format("Window Start: %s",
                        start));
                launch_window_end.setVisibility(View.VISIBLE);
                launch_window_end.setText(String.format("Window End: %s",
                        end));
            }
        } else {
            launch_window_start.setText(String.format("Launch Time: %s",
                    start));
            launch_window_end.setVisibility(View.GONE);
        }
    }

    private void setWindowStartEnd() {
        if (!detailLaunch.getWindowstart().equals(detailLaunch.getWindowend())) {
            if (detailLaunch.getWindowstart().toString().length() > 0
                    || detailLaunch.getWindowend().toString().length() > 0) {
                launch_window_start.setText("Launch Window unavailable.");
                launch_window_end.setVisibility(View.INVISIBLE);
            } else {
                launch_window_start.setText(String.format("Window Start: %s",
                        detailLaunch.getWindowstart()));
                launch_window_end.setVisibility(View.VISIBLE);
                launch_window_end.setText(String.format("Window End: %s",
                        detailLaunch.getWindowend()));
            }
        } else {
            launch_window_start.setText(String.format("Launch Time: %s",
                    detailLaunch.getWindowstart()));
            launch_window_end.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static Fragment newInstance() {
        return new SummaryDetailFragment();
    }
}