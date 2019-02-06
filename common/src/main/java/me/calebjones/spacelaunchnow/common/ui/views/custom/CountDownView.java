package me.calebjones.spacelaunchnow.common.ui.views.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.R2;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.common.ui.views.CountDownTimer;
import timber.log.Timber;

public class CountDownView extends ConstraintLayout {

    @BindView(R2.id.common_countdown_layout)
    ConstraintLayout constraintLayout;
    @BindView(R2.id.countdown_days)
    TextView countdownDays;
    @BindView(R2.id.countdown_hours)
    TextView countdownHours;
    @BindView(R2.id.countdown_minutes)
    TextView countdownMinutes;
    @BindView(R2.id.countdown_seconds)
    TextView countdownSeconds;
    @BindView(R2.id.countdown_status)
    TextView countdownStatus;
    @BindView(R2.id.status_pill)
    StatusPillView statusPill;
    @BindView(R2.id.common_status_reason)
    TextView statusReason;
    @BindView(R2.id.countdown_view_group)
    Group countdownGroup;
    private CountDownTimer timer;
    private Disposable var;
    private Launch launch;
    private Context context;

    public CountDownView(Context context) {
        super(context);
        init(context);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        inflate(context, R.layout.countdown_layout_view, this);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);
        this.context = context;
    }

    private void resetCountdown() {
        if (timer != null) {
            Timber.v("Timer is not null, cancelling.");
            timer.cancel();
        }

        if (var != null) {
            var.dispose();
        }
    }

    private void startLaunchCountdown(long timeToFinish) {
        timer = new CountDownTimer(timeToFinish, 1000) {
            StringBuilder time = new StringBuilder();

            @Override
            public void onFinish() {
                Timber.v("Countdown finished.");
                countdownDays.setText("00");
                countdownHours.setText("00");
                countdownMinutes.setText("00");
                countdownSeconds.setText("00");
                countdownStatus.setVisibility(View.VISIBLE);
                countdownStatus.setText("+");
                countUpTimer(launch.getNet().getTime());
            }

            @Override
            public void onTick(long millisUntilFinished) {
                time.setLength(0);
                setCountdownView(millisUntilFinished);
            }
        }.start();
    }

    private void setLaunchCountdownComplete() {
        countdownDays.setText("00");
        countdownHours.setText("00");
        countdownMinutes.setText("00");
        countdownSeconds.setText("00");
    }

    private void launchInFlight() {
        countdownStatus.setVisibility(View.VISIBLE);
        countUpTimer(launch.getNet().getTime());
    }

    private void launchStatusUnknown() {
        countdownDays.setText("- -");
        countdownHours.setText("- -");
        countdownMinutes.setText("- -");
        countdownSeconds.setText("- -");
        countdownGroup.setVisibility(GONE);
    }

    private void countUpTimer(long longDate) {
        var = Observable
                .interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(
                        time -> {
                            Calendar currentTime = Calendar.getInstance();
                            long timeSince = currentTime.getTimeInMillis() - longDate;
                            setCountdownView(timeSince);
                        });
    }

    private void setCountdownView(long millisUntilFinished) {

        // Calculate the Days/Hours/Mins/Seconds numerically.
        long longDays = millisUntilFinished / 86400000;
        long longHours = (millisUntilFinished / 3600000) % 24;
        long longMins = (millisUntilFinished / 60000) % 60;
        long longSeconds = (millisUntilFinished / 1000) % 60;

        String days;
        String hours;
        String minutes;
        String seconds;

        if (longDays < 10) {
            days = "0" + String.valueOf(longDays);
        } else {
            days = String.valueOf(longDays);
        }


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


        try {
            // Update the views
            if (Integer.valueOf(days) > 0) {
                countdownDays.setText(days);
            } else {
                countdownDays.setText("00");
            }

            if (Integer.valueOf(hours) > 0) {
                countdownHours.setText(hours);
            } else if (Integer.valueOf(days) > 0) {
                countdownHours.setText("00");
            } else {
                countdownHours.setText("00");
            }

            if (Integer.valueOf(minutes) > 0) {
                countdownMinutes.setText(minutes);
            } else if (Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
                countdownMinutes.setText("00");
            } else {
                countdownMinutes.setText("00");
            }

            if (Integer.valueOf(seconds) > 0) {
                countdownSeconds.setText(seconds);
            } else if (Integer.valueOf(minutes) > 0 || Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
                countdownSeconds.setText("00");
            } else {
                countdownSeconds.setText("00");
            }
        } catch (NumberFormatException e) {
            countdownHours.setText("00");
            countdownDays.setText("00");
            countdownMinutes.setText("00");
            countdownSeconds.setText("00");
        }
    }

    public void setLaunch(Launch launch) {
        this.launch = launch;
        checkCountdownTimer(this.launch);
        statusPill.setStatus(launch);
    }

    private void checkCountdownTimer(Launch launch) {
        long longdate = launch.getNet().getTime();
        final Date date = new Date(longdate);

        Calendar launchDate = DateToCalendar(date);
        Calendar now = Calendar.getInstance();

        now.setTimeInMillis(System.currentTimeMillis());

        resetCountdown();

        String hold = launch.getHoldreason();
        String failure = launch.getFailreason();
        statusReason.setVisibility(GONE);
        if (hold != null) {
            statusReason.setText(hold);
            setReasonConstraintToBottom();
            statusReason.setVisibility(VISIBLE);
        }

        if (failure != null) {
            statusReason.setText(failure);
            setReasonConstraintToBottom();
            statusReason.setVisibility(VISIBLE);
        }

        if (launch.getTbddate()){
            statusReason.setText(R.string.date_unconfirmed);
            statusReason.setVisibility(VISIBLE);
            setReasonConstraintToStatusPill();
        } else if (!launch.getTbddate() && launch.getTbdtime()){
            statusReason.setText(R.string.date_confirmed);
            setReasonConstraintToStatusPill();
            statusReason.setVisibility(VISIBLE);
        }

        long timeToFinish = launchDate.getTimeInMillis() - now.getTimeInMillis();
        countdownGroup.setVisibility(VISIBLE);
        if (timeToFinish > 0 && (launch.getStatus().getId() == 1 || launch.getStatus().getId() == 1)) {
            startLaunchCountdown(timeToFinish);
        } else if (launch.getStatus().getId() == 3 || launch.getStatus().getId() == 4 || launch.getStatus().getId() == 7) {
            setLaunchCountdownComplete();
        } else if (launch.getStatus().getId() == 6 || launch.getStatus().getId() == 1) {
            launchInFlight();
        } else {
            launchStatusUnknown();
        }
    }

    private void setReasonConstraintToBottom() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.common_status_reason, ConstraintSet.TOP, R.id.bottom_divider, ConstraintSet.BOTTOM,20);
        constraintSet.applyTo(constraintLayout);
    }

    private void setReasonConstraintToStatusPill() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.common_status_reason, ConstraintSet.TOP, R.id.status_pill, ConstraintSet.BOTTOM,20);
        constraintSet.applyTo(constraintLayout);
    }

    public Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
