package me.calebjones.spacelaunchnow.content.services;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.MainActivity;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.utils.CalendarUtil;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;


public class NextLaunchTracker extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Launch nextLaunch;
    private Launch updatedLaunch;
    private SharedPreferences sharedPref;
    private ListPreferences listPreferences;
    private SwitchPreferences switchPreferences;
    public static List<Launch> upcomingLaunchList;
    private Calendar rightNow;
    private AlarmManager alarmManager;
    private long interval;
    private static final String NAME_KEY = "me.calebjones.spacelaunchnow.wear.nextname";
    private static final String TIME_KEY = "me.calebjones.spacelaunchnow.wear.nexttime";

    private GoogleApiClient mGoogleApiClient;

    public NextLaunchTracker() {
        super("NextLaunchTracker");
    }

    public void onCreate() {
        super.onCreate();
        Timber.d("NextLaunchTracker - onCreate");
        rightNow = Calendar.getInstance();
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("onHandleIntent - %s", intent.describeContents());
        this.listPreferences = ListPreferences.getInstance(getApplicationContext());
        this.switchPreferences = SwitchPreferences.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        upcomingLaunchList = this.listPreferences.getNextLaunches();

        mGoogleApiClient.connect();
        Timber.d("mGoogleApiClient - connect");

        if (upcomingLaunchList != null && upcomingLaunchList.size() > 0) {
            checkNextLaunch();
        } else {
            interval = 3600000;
            scheduleUpdate();

            //If Calendar Sync is enabled sync it up
            if (switchPreferences.getCalendarStatus()){
                syncCalendar();
            }
        }
        stopSelf();
    }

    private void checkNextLaunch() {
        upcomingLaunchList = this.listPreferences.getNextLaunches();

        if (upcomingLaunchList != null && upcomingLaunchList.size() > 0) {
            nextLaunch = upcomingLaunchList.get(0);
            updatedLaunch = listPreferences.getNextLaunch();
        }

        //Check if the stored launch is still the next launch.
        if (updatedLaunch != null && nextLaunch != null) {

            //If they do not match this means nextLaunch has changed (launch executed, filter change, etc)
            if (nextLaunch.getId().intValue() != updatedLaunch.getId().intValue()) {

                debugNotificaiton(String.format("Launch has changed - Next: %s Stored: %s"
                        , nextLaunch.getId(), updatedLaunch.getId()));
                Intent nextIntent = new Intent(this, LaunchDataService.class);
                nextIntent.setAction(Strings.ACTION_GET_UP_LAUNCHES);
                startService(nextIntent);
                //They do match, check if the launch time has moved.
            } else {
                if (listPreferences.getNextLaunchTimestamp() != 0) {
                    if (Math.abs(listPreferences.getNextLaunchTimestamp()
                            - updatedLaunch.getNetstamp()) > 60) {
                        debugNotificaiton(String.format("Resetting notifiers - List: %s Stored: %s "
                                ,listPreferences.getNextLaunchTimestamp(), nextLaunch.getNetstamp()));
                        updatedLaunch.resetNotifiers();
                        listPreferences.setNextLaunchTimestamp(updatedLaunch.getNetstamp());

                        upcomingLaunchList.set(0, updatedLaunch);
                        listPreferences.setNextLaunches(upcomingLaunchList);
                        checkStatus(updatedLaunch);

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
                        this.getApplicationContext().sendBroadcast(broadcastIntent);
                    }
                } else if (updatedLaunch.getNetstamp() != 0) {
                    listPreferences.setNextLaunchTimestamp(updatedLaunch.getNetstamp());
                    debugNotificaiton("Updated timestamp to " + updatedLaunch.getNetstamp());

                    upcomingLaunchList.set(0, updatedLaunch);
                    listPreferences.setNextLaunches(upcomingLaunchList);
                    checkStatus(updatedLaunch);
                }
            }
        } else if (nextLaunch != null) {
            this.listPreferences.setNextLaunch(nextLaunch);
            checkStatus(nextLaunch);
        }
        //If Calendar Sync is enabled sync it up
        if (switchPreferences.getCalendarStatus()){
            syncCalendar();
        }
    }

    private void syncCalendar() {
        //Get the list of launches
        Launch launch = listPreferences.getNextLaunch();
        CalendarUtil provider = new CalendarUtil();

        //If CalendarID
        if (launch.getCalendarID() == null) {
            Integer id = provider.addEvent(this, launch);
            launch.setCalendarID(id);
            listPreferences.setNextLaunch(launch);
        } else {
            //Try to update, if it fails create event.
            if (!provider.updateEvent(this, launch)){
                Integer id = provider.addEvent(this, launch);
                launch.setCalendarID(id);
                listPreferences.setNextLaunch(launch);
            }
        }
    }

    private void checkStatus(Launch launch) {
        if (launch != null && launch.getNetstamp() > 0) {

            long longdate = launch.getNetstamp();
            longdate = longdate * 1000;
            final Date date = new Date(longdate);

            Calendar future = DateToCalendar(date);
            Calendar now = rightNow;

            now.setTimeInMillis(System.currentTimeMillis());
            long timeToFinish = future.getTimeInMillis() - now.getTimeInMillis();
            boolean notify = this.sharedPref.getBoolean("notifications_new_message", true);

            //Launch is in less then one hour
            if (timeToFinish > 0) {
                if (timeToFinish <= 610000) {
                    if (notify) {
                        int minutes = (int) ((timeToFinish / (1000 * 60)) % 60);
                        //Check settings to see if user should be notified.
                        if (this.sharedPref.getBoolean("notifications_launch_minute", false)) {
                            if (!launch.getIsNotifiedTenMinute()) {
                                notifyUserImminent(launch, minutes);
                                launch.setIsNotifiedTenMinute(true);
                                this.listPreferences.setNextLaunch(launch);
                            }
                        }
                    }
                    interval = (future.getTimeInMillis() + 3600000);
                    scheduleUpdate();
                } else if (timeToFinish < 3600000) {
                    if (notify) {
                        int minutes = (int) ((timeToFinish / (1000 * 60)) % 60);
                        //Check settings to see if user should be notified.
                        if (this.sharedPref.getBoolean("notifications_launch_imminent", true)) {
                            if (!launch.getIsNotifiedHour()) {
                                notifyUserImminent(launch, minutes);
                                launch.setIsNotifiedhour(true);
                                this.listPreferences.setNextLaunch(launch);
                            }
                        }
                    }
                    interval = ((future.getTimeInMillis() - 600000) - now.getTimeInMillis());
                    scheduleUpdate();

                    //Launch is in less then 24 hours
                } else if (timeToFinish < 86400000) {
                    Timber.v("Less than 24 hours.");
                    if (notify) {
                        int hours = (int) ((timeToFinish / (1000 * 60 * 60)) % 24);
                        //Check settings to see if user should be notified.
                        if (this.sharedPref.getBoolean("notifications_launch_day", true)) {
                            if (!launch.getIsNotifiedDay()) {
                                notifyUser(launch, hours);
                                launch.setIsNotifiedDay(true);
                                this.listPreferences.setNextLaunch(launch);
                            }
                        }
                    }
                    interval = timeToFinish / 2;
                    if (interval < 3600000) {
                        interval = 3500000;
                    }
                    scheduleUpdate();
                    //Launch is within 48 hours
                } else if (timeToFinish < 172800000) {
                    interval = ((future.getTimeInMillis() - 82800000) - now.getTimeInMillis());
                    scheduleUpdate();
                } else {
                    interval = (timeToFinish / 2) + 43200000;
                    scheduleUpdate();
                }
            } else {
                interval = 3600000;
                scheduleUpdate();
            }
        } else {
            interval = (86400000);
            scheduleUpdate();
        }
    }

    private void notifyUserImminent(Launch launch, int minutes) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

        String launchDate;
        String expandedText;
        String launchName = launch.getName();
        String launchURL = launch.getVidURL();
        String launchPad = launch.getLocation().getName();

        if (launch.getMissions().size() > 0) {
            expandedText = "Launch attempt in " + minutes + " minutes from " + launchPad + ". \n\n" + launch.getMissions().get(0).getDescription();
        } else {
            expandedText = "Launch attempt in " + minutes + " minutes from " + launchPad;
        }

        //Get launch date
        if (sharedPref.getBoolean("local_time", true)) {
            SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd yyyy hh:mm a zzz");
            df.toLocalizedPattern();
            Date date = new Date(launch.getWindowstart());
            launchDate = df.format(date);
        } else {
            launchDate = launch.getWindowstart();
        }

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent appIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        Intent shareLaunch = Utils.buildShareIntent(launch);
        PendingIntent sharePendingIntent = PendingIntent.getActivity(this, 0, shareLaunch, 0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //TODO add launch image when ready from LL
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.nav_header));

        mBuilder.setContentTitle(launchName)
                .setContentText("Launch attempt in " + minutes + " minutes from " + launchPad)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(expandedText)
                        .setSummaryText(launchDate))
                .extend(wearableExtender)
                .setSound(alarmSound)
                .addAction(R.drawable.ic_menu_share_white, "Share", sharePendingIntent);

        if (launch.getVidURL() != null && launch.getVidURL().length() > 0) {
            // Sets up the Open and Share action buttons that will appear in the
            // big view of the notification.
            Intent vidIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(launchURL));
            PendingIntent vidPendingIntent = PendingIntent.getActivity(this, 0, vidIntent, 0);

            mBuilder.addAction(R.drawable.ic_open_in_browser_white, "Watch Live", vidPendingIntent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && sharedPref.getBoolean("notifications_new_message_vibrate", true)) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH)
                    .setVibrate(new long[]{1000, 1000})
                    .setLights(Color.RED, 3000, 3000);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN && sharedPref.getBoolean("notifications_new_message_vibrate", true)) {
            mBuilder.setVibrate(new long[]{1000, 1000})
                    .setLights(Color.RED, 3000, 3000);
        }

        NotificationManager mNotifyManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify(Strings.NOTIF_ID_HOUR, mBuilder.build());
    }

    private void notifyUser(Launch launch, int hours) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

        String launchDate;
        String expandedText;
        String launchName = launch.getName();
        String launchPad = launch.getLocation().getName();

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent appIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        if (launch.getMissions().size() > 0) {
            expandedText = "Launch attempt in " + hours + " hours from " + launchPad + ". \n\n" + launch.getMissions().get(0).getDescription();
        } else {
            expandedText = "Launch attempt in " + hours + " hours from " + launchPad;
        }

        //Get launch date
        if (sharedPref.getBoolean("local_time", true)) {
            SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd yyyy hh:mm a zzz");
            df.toLocalizedPattern();
            Date date = new Date(launch.getWindowstart());
            launchDate = df.format(date);
        } else {
            launchDate = launch.getWindowstart();
        }

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.nav_header));

        mBuilder.setContentTitle(launchName)
                .setContentText("Launch attempt in " + hours + " hours from " + launchPad)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(appIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(expandedText)
                        .setSummaryText(launchDate))
                .extend(wearableExtender)
                .setSound(alarmSound)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && sharedPref.getBoolean("notifications_new_message_vibrate", true)) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH)
                    .setVibrate(new long[]{1000, 1000})
                    .setLights(Color.RED, 3000, 3000);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN && sharedPref.getBoolean("notifications_new_message_vibrate", true)) {
            mBuilder.setVibrate(new long[]{1000, 1000})
                    .setLights(Color.RED, 3000, 3000);
        }


        NotificationManager mNotifyManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyManager.notify(Strings.NOTIF_ID_DAY, mBuilder.build());
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public void debugNotificaiton(String message){
        if (BuildConfig.DEBUG) {
            long time = new Date().getTime();
            String tmpStr = String.valueOf(time);
            String last4Str = tmpStr.substring(tmpStr.length() - 5);
            int notificationId = Integer.valueOf(last4Str);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mBuilder.setContentTitle("Debug Notification")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

            NotificationManager mNotifyManager = (NotificationManager)
                    getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyManager.notify(notificationId, mBuilder.build());
        }
    }

    public void scheduleUpdate() {
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long nextUpdate = Calendar.getInstance().getTimeInMillis() + interval;

        if (BuildConfig.DEBUG) {
            updatedLaunch = listPreferences.getNextLaunch();
            upcomingLaunchList = listPreferences.getNextLaunches();

            if (upcomingLaunchList != null && upcomingLaunchList.size() > 0) {
                nextLaunch = upcomingLaunchList.get(0);
            }

            if (nextLaunch != null && updatedLaunch != null) {
                // Create a DateFormatter object for displaying date in specified format.
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss zz");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(nextUpdate);

                String intevalString = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(interval),
                        TimeUnit.MILLISECONDS.toMinutes(interval) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(interval)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(interval) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(interval)));

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setContentTitle("LaunchData Worked! - Next Launch")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Next Launch = " + nextLaunch.getName() + "\n\nStored Launch = " + updatedLaunch.getName())
                                .setSummaryText(String.format("Interval: %s | ", intevalString) + formatter.format(calendar.getTime())))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setAutoCancel(true);

                NotificationManager mNotifyManager = (NotificationManager)
                        getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyManager.notify(Strings.NOTIF_ID + 1, mBuilder.build());
            }
        }

        if (Utils.checkPlayServices(this)){
            sendToWear(listPreferences.getNextLaunch());
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, nextUpdate,
                PendingIntent.getBroadcast(this, 165432, new Intent(Strings.ACTION_CHECK_NEXT_LAUNCH_TIMER), 0));
    }

    // Create a data map and put data in it
    private void sendToWear(Launch launch) {
        if (launch != null && launch.getName() != null && launch.getNetstamp() != null) {
            Timber.v("Sending data...");
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/nextLaunch");

            putDataMapReq.getDataMap().putString(NAME_KEY, launch.getName());
            putDataMapReq.getDataMap().putInt(TIME_KEY, launch.getNetstamp());
            putDataMapReq.getDataMap().putLong("time", new Date().getTime());

            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
            DataApi.DataItemResult dataItemResult = Wearable.DataApi
                    .putDataItem(mGoogleApiClient, putDataReq).await();
            Timber.v("Sent");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Timber.d("onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.e("onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Timber.e("onConnectionFailed %s", connectionResult.getErrorMessage());
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Timber.d("Google Client Disconnect");
            mGoogleApiClient.disconnect();
        }
    }
}
