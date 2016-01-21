package me.calebjones.spacelaunchnow.content.services;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;


public class NextLaunchTracker extends IntentService {

    private Launch nextLaunch;
    private Launch storedLaunch;
    private SharedPreferences sharedPref;
    private SharedPreference sharedPreference;
    public static List<Launch> upcomingLaunchList;
    private Calendar rightNow;
    private AlarmManager alarmManager;
    private long interval;

    public NextLaunchTracker() {
        super("NextLaunchTracker");
    }

    public void onCreate() {
        Timber.d("NextLaunchTracker - onCreate");
        rightNow = Calendar.getInstance();
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.sharedPreference = SharedPreference.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        upcomingLaunchList = this.sharedPreference.getLaunchesUpcoming();
        if (upcomingLaunchList.size() > 0){
            checkNextLaunch();
        } else {
            interval = 3600000;
            scheduleUpdate();
        }
    }

    private void checkNextLaunch() {
        upcomingLaunchList = this.sharedPreference.getLaunchesUpcoming();
        nextLaunch = upcomingLaunchList.get(0);
        storedLaunch = this.sharedPreference.getNextLaunch();

        //Check if the stored launch is still the next launch.
        if (storedLaunch != null) {
            //If they do not match this means nextLaunch has changed IE a launch executed.
            if (nextLaunch.getId().intValue() != storedLaunch.getId().intValue()){
                this.sharedPreference.setNextLaunch(nextLaunch);

                Intent updatePreviousLaunches = new Intent(this, LaunchDataService.class);
                updatePreviousLaunches.setAction(Strings.ACTION_GET_PREV_LAUNCHES);
                updatePreviousLaunches.putExtra("id", storedLaunch.getId());
                updatePreviousLaunches.putExtra("URL", Utils.getBaseURL());
                startService(updatePreviousLaunches);

                storedLaunch = nextLaunch;
                checkStatus(storedLaunch);
            } else {
                checkStatus(storedLaunch);
            }
        } else {
            this.sharedPreference.setNextLaunch(nextLaunch);
            storedLaunch = nextLaunch;
            checkStatus(storedLaunch);
        }
    }

    //TODO THIS IS BUGGED
    private void checkStatus(Launch launch) {
        if (launch.getWsstamp() > 0) {
            long longdate = launch.getWsstamp();
            longdate = longdate * 1000;
            final Date date = new Date(longdate);

            Calendar future = DateToCalendar(date);
            Calendar now = rightNow;

            now.setTimeInMillis(System.currentTimeMillis());
            long timeToFinish = future.getTimeInMillis() - now.getTimeInMillis();
            boolean notify = this.sharedPref.getBoolean("notifications_new_message", true);

            //Launch is in less then one hour
            if (timeToFinish < 3600000) {
                if (notify){
                    //Check settings to see if user should be notified.
                    if (this.sharedPref.getBoolean("notifications_launch_imminent", true)){
                        if (!launch.getIsNotifiedHour()) {
                            notifyUserImmminent();
                            launch.setIsNotifiedhour(true);
                            this.sharedPreference.setNextLaunch(launch);
                        }
                        //If its a saved launch check notification
                    } else if (launch.isFavorite() && this.sharedPref.getBoolean("notifications_launch_imminent_saved", true)){
                        if (!launch.getIsNotifiedHour()) {
                            notifyUserImmminent();
                            launch.setIsNotifiedhour(true);
                            this.sharedPreference.setNextLaunch(launch);
                        }
                    }
                }
                interval = 3600000;
                scheduleUpdate();

            //Launch is in less then 24 hours
            } else if (timeToFinish < 86400000) {
                Timber.v("Less than 24 hours.");
                if (notify) {
                    //Check settings to see if user should be notified.
                    if (this.sharedPref.getBoolean("notifications_launch_day", false)) {
                        if (!launch.getIsNotifiedDay()) {
                            notifyUser();
                            launch.setIsNotifiedDay(true);
                            this.sharedPreference.setNextLaunch(launch);
                        }
                        //If its a saved launch check notification
                    } else if (launch.isFavorite() && this.sharedPref.getBoolean("notifications_launch_day_saved", false)) {
                        if (!launch.getIsNotifiedDay()) {
                            notifyUser();
                            launch.setIsNotifiedDay(true);
                            this.sharedPreference.setNextLaunch(launch);
                        }
                    }
                }
                interval = timeToFinish / 2;
                if (interval < 3600000){
                    interval = 3500000;
                }
                scheduleUpdate();
            } else {
                interval = (timeToFinish / 2) + 43200000;
                scheduleUpdate();
            }
        } else {
            interval = (86400000);
            scheduleUpdate();
        }
    }

    private void notifyUserImmminent() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setContentTitle("Launch less then 1 hour!")
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true);

        NotificationManager mNotifyManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify(Strings.NOTIF_ID_HOUR, mBuilder.build());
    }

    private void notifyUser() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setContentTitle("Launch less then 24 hours!")
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true);

        NotificationManager mNotifyManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify(Strings.NOTIF_ID_DAY, mBuilder.build());
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public void scheduleUpdate() {
        Timber.d("scheduleUpdate - Interval: %s", interval);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long nextUpdate = Calendar.getInstance().getTimeInMillis() + interval;
        Timber.d("scheduleUpdated in %s milliseconds.", nextUpdate);
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextUpdate,
                PendingIntent.getBroadcast(this, 165432, new Intent(Strings.ACTION_CHECK_NEXT_LAUNCH_TIMER), 0));

    }

}
