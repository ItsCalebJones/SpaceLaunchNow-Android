package me.calebjones.spacelaunchnow.wear.launchdetail;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.wear.R;
import timber.log.Timber;

public class LaunchDetail extends WearableActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.content_title)
    TextView launchTitle;
    @BindView(R.id.countdown_days)
    TextView countdownDays;
    @BindView(R.id.countdown_hours)
    TextView countdownHours;
    @BindView(R.id.countdown_minutes)
    TextView countdownMinutes;
    @BindView(R.id.countdown_seconds)
    TextView countdownSeconds;
    @BindView(R.id.countdown_layout)
    LinearLayout countdownLayout;
    @BindView(R.id.content_mission)
    TextView launchMission;
    @BindView(R.id.content_mission_description)
    TextView launchMissionDescription;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.TitleCard)
    LinearLayout titleCard;
    @BindView(R.id.launchStatus)
    TextView launchStatus;
    @BindView(R.id.countdown_view)
    LinearLayout countdownView;
    private Launch launch;
    private Realm realm;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_detail);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        int launchID = 0;
        if (intent.getExtras() != null) {
            launchID = intent.getIntExtra("launchId", 0);
        }

        launch = realm.where(Launch.class).equalTo("id", launchID).findFirst();
        setupView(launch);

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {

    }

    private void setupView(Launch launch) {
        launchTitle.setText(launch.getRocket().getName());

        launchStatus.setText(getStatus(launch.getStatus()));

        if (launch.getMissions() != null && launch.getMissions().size() > 0) {
            launchMission.setText(launch.getMissions().get(0).getName());
            launchMissionDescription.setText(launch.getMissions().get(0).getDescription());
        }

        long future = launch.getNetstamp();
        future = future * 1000;
        long timeToFinish = future - System.currentTimeMillis();
        if (timeToFinish > 0) {
            timer = new CountDownTimer(timeToFinish, 1000) {
                StringBuilder time = new StringBuilder();

                @Override
                public void onFinish() {
                    Timber.v("Countdown finished.");

                    countdownDays.setText("- -");
                    countdownHours.setText("- -");
                    countdownMinutes.setText("- -");
                    countdownSeconds.setText("- -");
                }

                @Override
                public void onTick(long millisUntilFinished) {
                    time.setLength(0);

                    // Calculate the Days/Hours/Mins/Seconds numerically.
                    long longDays = millisUntilFinished / 86400000;
                    long longHours = (millisUntilFinished / 3600000) % 24;
                    long longMins = (millisUntilFinished / 60000) % 60;
                    long longSeconds = (millisUntilFinished / 1000) % 60;

                    String days = String.valueOf(longDays);
                    String hours;
                    String minutes;
                    String seconds;

                    // Translate those numerical values to string values.
                    if (longHours < 10) {
                        hours = "0" + String.valueOf(longHours);
                    } else {
                        hours = String.valueOf(longHours);
                    }

                    if (longMins < 10) {
                        minutes = "0" + String.valueOf(longMins);
                    } else {
                        minutes = String.valueOf(longMins);
                    }

                    if (longSeconds < 10) {
                        seconds = "0" + String.valueOf(longSeconds);
                    } else {
                        seconds = String.valueOf(longSeconds);
                    }


                    // Update the views
                    if (Integer.valueOf(days) > 0) {
                        countdownDays.setText(days);
                    } else {
                        countdownDays.setText("- -");
                    }

                    if (Integer.valueOf(hours) > 0) {
                        countdownHours.setText(hours);
                    } else if (Integer.valueOf(days) > 0) {
                        countdownHours.setText("00");
                    } else {
                        countdownHours.setText("- -");
                    }

                    if (Integer.valueOf(minutes) > 0) {
                        countdownMinutes.setText(minutes);
                    } else if (Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
                        countdownMinutes.setText("00");
                    } else {
                        countdownMinutes.setText("- -");
                    }

                    if (Integer.valueOf(seconds) > 0) {
                        countdownSeconds.setText(seconds);
                    } else if (Integer.valueOf(minutes) > 0 || Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
                        countdownSeconds.setText("00");
                    } else {
                        countdownSeconds.setText("- -");
                    }
                }
            }.start();
        } else {
            countdownView.setVisibility(View.GONE);
        }

    }

    private String getStatus(Integer status) {
        switch (status){
            case 0:
                break;
            case 1:
                return "Launch is GO";
            case 2:
                return "Launch is NO-GO";
            case 3:
                return "Launch was a SUCCESS";
            case 4:
                return "Launch failed";
        }
        return "Unknown Launch Status";
    }
}
