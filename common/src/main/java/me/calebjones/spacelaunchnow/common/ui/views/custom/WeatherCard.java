package me.calebjones.spacelaunchnow.common.ui.views.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.zetterstrom.com.forecast.models.DataPoint;
import android.zetterstrom.com.forecast.models.Forecast;

import com.github.pwittchen.weathericonview.WeatherIconView;

import java.text.SimpleDateFormat;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;

public class WeatherCard extends CardView {

    @BindView(R2.id.weather_title)
    TextView weatherTitle;
    @BindView(R2.id.weather_icon)
    WeatherIconView weatherIcon;
    @BindView(R2.id.weather_percip_chance_icon)
    WeatherIconView weatherPrecipIcon;
    @BindView(R2.id.weather_percip_chance)
    TextView weatherPercipChance;
    @BindView(R2.id.weather_wind_speed_icon)
    WeatherIconView weatherSpeedIcon;
    @BindView(R2.id.weather_wind_speed)
    TextView weatherWindSpeed;
    @BindView(R2.id.weather_current_temp)
    TextView weatherCurrentTemp;
    @BindView(R2.id.weather_feels_like)
    TextView weatherFeelsLike;
    @BindView(R2.id.weather_low_high)
    TextView weatherLowHigh;
    @BindView(R2.id.weather_summary_day)
    TextView weatherSummaryDay;
    @BindView(R2.id.weather_location)
    TextView weatherLocation;
    @BindView(R2.id.day_two_weather_icon)
    WeatherIconView dayTwoWeatherIcon;
    @BindView(R2.id.day_two_day)
    TextView dayTwoDay;
    @BindView(R2.id.day_two_low_high)
    TextView dayTwoWeatherLowHigh;
    @BindView(R2.id.day_two_precip_prob_icon)
    WeatherIconView dayTwoWeatherPrecipIcon;
    @BindView(R2.id.day_two_precip_prob)
    TextView dayTwoWeatherPrecip;
    @BindView(R2.id.day_two_weather_wind_speed_icon)
    WeatherIconView dayTwoWeatherWindIcon;
    @BindView(R2.id.day_two_weather_wind_speed)
    TextView dayTwoWeatherWindSpeed;
    @BindView(R2.id.day_three_weather_icon)
    WeatherIconView dayThreeWeatherIcon;
    @BindView(R2.id.day_three_day)
    TextView dayThreeDay;
    @BindView(R2.id.day_three_low_high)
    TextView dayThreeWeatherLowHigh;
    @BindView(R2.id.day_three_precip_prob_icon)
    WeatherIconView dayThreeWeatherPrecipIcon;
    @BindView(R2.id.day_three_precip_prob)
    TextView dayThreeWeatherPrecip;
    @BindView(R2.id.day_three_weather_wind_speed_icon)
    WeatherIconView dayThreeWeatherWindIcon;
    @BindView(R2.id.day_three_weather_wind_speed)
    TextView dayThreeWeatherWindSpeed;
    @BindView(R2.id.day_four_weather_icon)
    WeatherIconView dayFourWeatherIcon;
    @BindView(R2.id.day_four_day)
    TextView dayFourDay;
    @BindView(R2.id.day_four_low_high)
    TextView dayFourWeatherLowHigh;
    @BindView(R2.id.day_four_precip_prob_icon)
    WeatherIconView dayFourWeatherPrecipIcon;
    @BindView(R2.id.day_four_precip_prob)
    TextView dayFourWeatherPrecip;
    @BindView(R2.id.day_four_weather_wind_speed_icon)
    WeatherIconView dayFourWeatherWindIcon;
    @BindView(R2.id.day_four_weather_wind_speed)
    TextView dayFourWeatherWindSpeed;
    @BindView(R2.id.three_day_forecast)
    Group threeDayForecast;
    @BindView(R2.id.constraintLayout)
    ConstraintLayout constraintLayout;

    private SharedPreferences sharedPref;
    private boolean nightMode = false;
    private boolean current = true;
    private Forecast forecast;
    private String location;

    public WeatherCard(Context context) {
        super(context);
        init(context);
    }

    public WeatherCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WeatherCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.custom_weather_card, this);
        ButterKnife.bind(this);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
    }

    ;

    public void setWeather(Forecast forecast, String location, boolean current, boolean nightMode) {
        this.forecast = forecast;
        this.current = current;
        this.location = location;
        this.nightMode = nightMode;
        if (current) {
            updateCurrentWeatherView(forecast, location);
        } else {
            updatePastWeatherView(forecast, location);
        }
    }

    public void setTitle(String title) {
        weatherTitle.setText(title);
    }

    private void updateCurrentWeatherView(Forecast forecast, String location) {
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
                String feelsLikeTemp = "Feels like ";
                feelsLikeTemp = feelsLikeTemp + String.valueOf(Math.round(forecast.getCurrently().getApparentTemperature())) + (char) 0x00B0;
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
            String lowHigh = lowTemp + (char) 0x00B0 + " " + temp + " | " + highTemp + (char) 0x00B0 + " " + temp;
            weatherLowHigh.setText(lowHigh);

            if (forecast.getDaily().getDataPoints().get(0).getPrecipProbability() != null) {
                String precipProb = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(0).getPrecipProbability() * 100) + "%");
                weatherPercipChance.setText(precipProb);
            }

            if (forecast.getDaily().getDataPoints().size() >= 3) {

                DataPoint dayOne = forecast.getDaily().getDataPoints().get(1);

                if (dayOne.getIcon() != null && dayOne.getIcon().getText() != null) {
                    //Day One!
                    setIconView(dayTwoWeatherIcon, dayOne.getIcon().getText());
                }

                String dayTwoLowHigh = "";
                if (dayOne.getTemperatureMax() != null && dayOne.getTemperatureMin() != null) {
                    //Get Low - High temp
                    String dayTwoHighTemp = String.valueOf(Math.round(dayOne.getTemperatureMax()));
                    String dayTwoLowTemp = String.valueOf(Math.round(dayOne.getTemperatureMin()));
                    dayTwoLowHigh = dayTwoLowTemp + (char) 0x00B0 + " " + temp + " | " + dayTwoHighTemp + (char) 0x00B0 + " " + temp;
                }

                //Get rain prop
                String dayTwoPrecipProb = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(1).getPrecipProbability() * 100) + "%");

                //Get Wind speed
                String dayTwoWindSpeed = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(1).getWindSpeed())) + " " + speed;

                //Get day date
                String dayTwoDate = new SimpleDateFormat("EE ").format(forecast.getDaily().getDataPoints().get(1).getTime());

                dayTwoWeatherLowHigh.setText(dayTwoLowHigh);
                dayTwoWeatherPrecip.setText(dayTwoPrecipProb);
                dayTwoWeatherWindSpeed.setText(dayTwoWindSpeed);
                dayTwoDay.setText(dayTwoDate);

                //Day Two!
                setIconView(dayThreeWeatherIcon, forecast.getDaily().getDataPoints().get(2).getIcon().getText());

                //Get Low - High temp
                String dayThreeHighTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(2).getTemperatureMax()));
                String dayThreeLowTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(2).getTemperatureMin()));
                String dayThreeLowHigh = dayThreeLowTemp + (char) 0x00B0 + " " + temp + " | " + dayThreeHighTemp + (char) 0x00B0 + " " + temp;

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
                setIconView(dayFourWeatherIcon, forecast.getDaily().getDataPoints().get(3).getIcon().getText());

                //Get Low - High temp
                String dayFourHighTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(3).getTemperatureMax()));
                String dayFourLowTemp = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(3).getTemperatureMin()));
                String dayFourLowHigh = dayFourLowTemp + (char) 0x00B0 + " " + temp + " | " + dayFourHighTemp + (char) 0x00B0 + " " + temp;

                String dayFourPrecipProb = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(3).getPrecipProbability() * 100) + "%");

                //Get Wind speed
                String dayFourWindSpeed = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(3).getWindSpeed())) + " " + speed;

                //Get day date
                String dayFourDate = new SimpleDateFormat("EE").format(forecast.getDaily().getDataPoints().get(3).getTime());

                dayFourWeatherLowHigh.setText(dayFourLowHigh);
                dayFourWeatherPrecip.setText(dayFourPrecipProb);
                dayFourWeatherWindSpeed.setText(dayFourWindSpeed);
                dayFourDay.setText(dayFourDate);
            } else {
                threeDayForecast.setVisibility(View.GONE);
            }
        }

        if (forecast.getCurrently().getIcon() != null && forecast.getCurrently().getIcon().getText() != null) {
            setIconView(weatherIcon, forecast.getCurrently().getIcon().getText());
        }

        if (forecast.getDaily() != null && forecast.getDaily().getSummary() != null) {
            weatherSummaryDay.setText(forecast.getDaily().getSummary());
        } else if (forecast.getCurrently() != null && forecast.getCurrently().getSummary() != null) {
            weatherSummaryDay.setText(forecast.getCurrently().getSummary());
        } else {
            weatherSummaryDay.setVisibility(View.GONE);
        }

        weatherLocation.setText(location);

        if (nightMode) {
            dayTwoWeatherWindIcon.setIconColor(Color.WHITE);
            dayTwoWeatherPrecipIcon.setIconColor(Color.WHITE);
            dayThreeWeatherWindIcon.setIconColor(Color.WHITE);
            dayThreeWeatherPrecipIcon.setIconColor(Color.WHITE);
            dayFourWeatherWindIcon.setIconColor(Color.WHITE);
            dayFourWeatherPrecipIcon.setIconColor(Color.WHITE);
            weatherPrecipIcon.setIconColor(Color.WHITE);
            weatherSpeedIcon.setIconColor(Color.WHITE);
        }
    }

    private void updatePastWeatherView(Forecast forecast, String location) {
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
                String feelsLikeTemp = "Feels like ";
                feelsLikeTemp = feelsLikeTemp + String.valueOf(Math.round(forecast.getCurrently().getApparentTemperature())) + (char) 0x00B0;
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
            String lowHigh = lowTemp + (char) 0x00B0 + " " + temp + " | " + highTemp + (char) 0x00B0 + " " + temp;
            weatherLowHigh.setText(lowHigh);

            if (forecast.getDaily().getDataPoints().get(0).getPrecipProbability() != null) {
                String precipProb = String.valueOf(Math.round(forecast.getDaily().getDataPoints().get(0).getPrecipProbability() * 100) + "%");
                weatherPercipChance.setText(precipProb);
            }
            threeDayForecast.setVisibility(View.GONE);
        }

        if (forecast.getCurrently().getIcon() != null && forecast.getCurrently().getIcon().getText() != null) {
            setIconView(weatherIcon, forecast.getCurrently().getIcon().getText());
        }

        if (forecast.getDaily() != null && forecast.getDaily().getDataPoints() != null && forecast.getDaily().getDataPoints().size() > 0 && forecast.getDaily().getDataPoints().get(0).getSummary() != null) {
            weatherSummaryDay.setText(forecast.getDaily().getDataPoints().get(0).getSummary());
        } else if (forecast.getCurrently() != null && forecast.getCurrently().getSummary() != null) {
            weatherSummaryDay.setText(forecast.getCurrently().getSummary());
        } else {
            weatherSummaryDay.setVisibility(View.GONE);
        }

        weatherLocation.setText(location);

        if (nightMode) {
            dayTwoWeatherWindIcon.setIconColor(Color.WHITE);
            dayTwoWeatherPrecipIcon.setIconColor(Color.WHITE);
            dayThreeWeatherWindIcon.setIconColor(Color.WHITE);
            dayThreeWeatherPrecipIcon.setIconColor(Color.WHITE);
            dayFourWeatherWindIcon.setIconColor(Color.WHITE);
            dayFourWeatherPrecipIcon.setIconColor(Color.WHITE);
            weatherPrecipIcon.setIconColor(Color.WHITE);
            weatherSpeedIcon.setIconColor(Color.WHITE);
        }
    }

    private void setIconView(WeatherIconView view, String icon) {
        if (icon.contains("partly-cloudy-day")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_partly_cloudy_day));
        } else if (icon.contains("partly-cloudy-night")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_partly_cloudy_night));
        } else if (icon.contains("clear-day")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_clear_day));
        } else if (icon.contains("clear-night")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_clear_night));
        } else if (icon.contains("rain")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_rain));
        } else if (icon.contains("snow")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_snow));
        } else if (icon.contains("sleet")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_sleet));
        } else if (icon.contains("wind")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_wind));
        } else if (icon.contains("fog")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_fog));
        } else if (icon.contains("cloudy")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_cloudy));
        } else if (icon.contains("hail")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_hail));
        } else if (icon.contains("thunderstorm")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_thunderstorm));
        } else if (icon.contains("tornado")) {
            view.setIconResource(getContext().getString(R.string.wi_forecast_io_tornado));
        }
        if (nightMode) {
            view.setIconColor(Color.WHITE);
        } else {
            view.setIconColor(Color.BLACK);
        }
    }

}
