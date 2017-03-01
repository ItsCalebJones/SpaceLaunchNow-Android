package me.calebjones.spacelaunchnow.content.services;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GcmNetworkManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.calendar.CalendarSyncService;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.jobs.NextLaunchJob;
import me.calebjones.spacelaunchnow.content.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.Launch;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchNotification;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import me.calebjones.spacelaunchnow.widget.LaunchCardCompactWidgetProvider;
import me.calebjones.spacelaunchnow.widget.LaunchTimerWidgetProvider;
import me.calebjones.spacelaunchnow.widget.LaunchWordTimerWidgetProvider;
import timber.log.Timber;


public class NextLaunchTracker extends IntentService {

    private GcmNetworkManager mGcmNetworkManager;
    private Launch nextLaunch;
    private boolean wear = false;
    private SharedPreferences sharedPref;
    private ListPreferences listPreferences;
    private SwitchPreferences switchPreferences;
    private Calendar rightNow;
    private AlarmManager alarmManager;
    private RealmResults<Launch> launchRealms;
    private long interval;

    private GoogleApiClient mGoogleApiClient;

    private Realm realm;
    private Boolean jobUpdated;

    public NextLaunchTracker() {
        super("NextLaunchTracker");
    }

    public void onCreate() {
        super.onCreate();
        Timber.d("NextLaunchTracker - onCreate");
        rightNow = Calendar.getInstance();
        super.onCreate();

        mGcmNetworkManager = GcmNetworkManager.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("onHandleIntent - %s", intent.describeContents());
        realm = Realm.getDefaultInstance();
        this.listPreferences = ListPreferences.getInstance(getApplicationContext());
        this.switchPreferences = SwitchPreferences.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);


        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar calDay = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calDay.add(Calendar.HOUR, 72);
        Date date = new Date();
        Date dateDay = new Date();
        dateDay = calDay.getTime();

        if (switchPreferences.getAllSwitch()) {
            launchRealms = realm.where(Launch.class)
                    .between("net", date, dateDay)
                    .findAllSorted("net", Sort.ASCENDING);
        } else {
            filterLaunchRealm(date, dateDay, realm);
        }

        if (launchRealms.size() > 0) {
            for (Launch realm : launchRealms) {
                checkNextLaunches(realm);
            }
        } else {
            scheduleUpdate(TimeUnit.MILLISECONDS.convert(12, TimeUnit.HOURS), 0);

            //If Calendar Sync is enabled sync it up
            if (switchPreferences.getCalendarStatus()) {
                syncCalendar();
            }
        }
        updateWidgets();
        realm.close();
    }

    private void updateWidgets() {
        Intent cardIntent = new Intent(this,LaunchCardCompactWidgetProvider.class);
        cardIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int cardIds[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), LaunchCardCompactWidgetProvider.class));
        cardIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,cardIds);
        sendBroadcast(cardIntent);

        Intent timerIntent = new Intent(this,LaunchTimerWidgetProvider.class);
        timerIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int timerIds[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), LaunchTimerWidgetProvider.class));
        timerIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,timerIds);
        sendBroadcast(timerIntent);

        Intent wordIntent = new Intent(this,LaunchWordTimerWidgetProvider.class);
        wordIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int wordIds[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), LaunchWordTimerWidgetProvider.class));
        wordIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,wordIds);
        sendBroadcast(wordIntent);
    }

    private void filterLaunchRealm(Date date, Date dateDay, Realm realm) {
        boolean first = true;
        RealmQuery<Launch> query = realm.where(Launch.class)
                .between("net", date, dateDay)
                .beginGroup();
        if (switchPreferences.getSwitchNasa()) {
            first = false;
            query.equalTo("rocket.agencies.id", 44)
                    .or()
                    .equalTo("location.pads.agencies.id", 44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 115)
                    .or()
                    .equalTo("location.pads.agencies.id", 115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 121)
                    .or()
                    .equalTo("location.pads.agencies.id", 121);
        }

        if (switchPreferences.getSwitchULA()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 124)
                    .or()
                    .equalTo("location.pads.agencies.id", 124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 111)
                    .or()
                    .equalTo("location.pads.agencies.id", 111)
                    .or()
                    .equalTo("rocket.agencies.id", 163)
                    .or()
                    .equalTo("location.pads.agencies.id", 163)
                    .or()
                    .equalTo("rocket.agencies.id", 63)
                    .or()
                    .equalTo("location.pads.agencies.id", 63);
        }
        if (switchPreferences.getSwitchCASC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 88)
                    .or()
                    .equalTo("location.pads.agencies.id", 88);
        }

        if (switchPreferences.getSwitchISRO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 31)
                    .or()
                    .equalTo("location.pads.agencies.id", 31);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 17);
        }

        if (switchPreferences.getSwitchCape()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 16);
        }

        if (switchPreferences.getSwitchPles()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 11);
        }

        if (switchPreferences.getSwitchVan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("location.id", 18);
        }

        launchRealms = query.endGroup().findAllSorted("net", Sort.ASCENDING);
    }

    private Launch filterLaunchRealm(Date date, Realm realm) {
        boolean first = true;
        RealmQuery<Launch> query = realm.where(Launch.class)
                .greaterThan("net", date)
                .beginGroup();
        if (switchPreferences.getSwitchNasa()) {
            first = false;
            query.equalTo("rocket.agencies.id", 44)
                    .or()
                    .equalTo("location.pads.agencies.id", 44);
        }

        if (switchPreferences.getSwitchArianespace()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 115)
                    .or()
                    .equalTo("location.pads.agencies.id", 115);
        }

        if (switchPreferences.getSwitchSpaceX()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 121)
                    .or()
                    .equalTo("location.pads.agencies.id", 121);
        }

        if (switchPreferences.getSwitchULA()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 124)
                    .or()
                    .equalTo("location.pads.agencies.id", 124);
        }

        if (switchPreferences.getSwitchRoscosmos()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 111)
                    .or()
                    .equalTo("location.pads.agencies.id", 111)
                    .or()
                    .equalTo("rocket.agencies.id", 163)
                    .or()
                    .equalTo("location.pads.agencies.id", 163)
                    .or()
                    .equalTo("rocket.agencies.id", 63)
                    .or()
                    .equalTo("location.pads.agencies.id", 63);
        }
        if (switchPreferences.getSwitchCASC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 88)
                    .or()
                    .equalTo("location.pads.agencies.id", 88);
        }

        if (switchPreferences.getSwitchISRO()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("rocket.agencies.id", 31)
                    .or()
                    .equalTo("location.pads.agencies.id", 31);
        }

        if (switchPreferences.getSwitchKSC()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 17);
        }

        if (switchPreferences.getSwitchCape()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 16);
        }

        if (switchPreferences.getSwitchPles()) {
            if (!first) {
                query.or();
            } else {
                first = false;
            }
            query.equalTo("location.id", 11);
        }

        if (switchPreferences.getSwitchVan()) {
            if (!first) {
                query.or();
            }
            query.equalTo("location.id", 18);
        }

        return query.endGroup().findFirst();
    }

    private void checkNextLaunches(Launch launch) {
        if (launch != null) {
            checkStatus(launch);
        }

        if (switchPreferences.getCalendarStatus()) {
            syncCalendar();
        }
    }

    private void syncCalendar() {
        CalendarSyncService.startActionSyncAll(this);
    }

    private void checkStatus(final Launch launch) {
        if (launch != null && launch.getNetstamp() > 0) {

            LaunchNotification notification = realm.where(LaunchNotification.class).equalTo("id", launch.getId()).findFirst();
            if (notification == null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.createObject(LaunchNotification.class, launch.getId());
                    }
                });
                notification = realm.where(LaunchNotification.class).equalTo("id", launch.getId()).findFirst();
            }

            long longdate = launch.getNetstamp();
            longdate = longdate * 1000;
            final Date date = new Date(longdate);

            Calendar future = DateToCalendar(date);
            Calendar now = rightNow;

            now.setTimeInMillis(System.currentTimeMillis());
            long timeToFinish = future.getTimeInMillis() - now.getTimeInMillis();
            boolean notify = (this.sharedPref.getBoolean("notifications_new_message", true) && launch.isNotifiable());

            //nextLaunch is in less then one hour
            if (timeToFinish > 0) {
                if (timeToFinish <= 610000) {
                    if (notify) {
                        int minutes = (int) ((timeToFinish / (1000 * 60)) % 60);
                        //Check settings to see if user should be notified.
                        if (!notification.isNotifiedTenMinute() && this.sharedPref.getBoolean("notifications_launch_minute", false)) {
                            notifyUserImminent(launch, minutes);
                            realm.beginTransaction();
                            notification.setNotifiedTenMinute(true);
                            realm.commitTransaction();
                        } else if (!notification.isNotifiedHour() && this.sharedPref.getBoolean("notifications_launch_imminent", true)) {
                            notifyUserImminent(launch, minutes);
                            realm.beginTransaction();
                            notification.setNotifiedHour(true);
                            realm.commitTransaction();
                        }
                    }
                    scheduleUpdate(timeToFinish - 601000, launch.getId());
                } else if (timeToFinish < 3600000) {
                    if (notify) {
                        int minutes = (int) ((timeToFinish / (1000 * 60)) % 60);
                        //Check settings to see if user should be notified.
                        if (this.sharedPref.getBoolean("notifications_launch_imminent", true)) {
                            if (!notification.isNotifiedHour()) {
                                notifyUserImminent(launch, minutes);
                                realm.beginTransaction();
                                notification.setNotifiedHour(true);
                                realm.commitTransaction();
                            }
                        }
                    }
                    scheduleUpdate((future.getTimeInMillis() - 600000) - now.getTimeInMillis(), launch.getId());

                    //Launch is in less then 24 hours
                } else if (timeToFinish < 86400000) {
                    Timber.v("Less than 24 hours.");
                    if (notify) {
                        int hours = (int) ((timeToFinish / (1000 * 60 * 60)) % 24);
                        //Check settings to see if user should be notified.
                        if (this.sharedPref.getBoolean("notifications_launch_day", true)) {
                            if (!notification.isNotifiedDay()) {

                                //Round up for standard notification.
                                if (hours == 23) {
                                    hours = 24;
                                }

                                notifyUser(launch, hours);
                                realm.beginTransaction();
                                notification.setNotifiedDay(true);
                                realm.commitTransaction();
                            }
                        }
                    }
                    interval = timeToFinish - 3601000;
//                    if (interval < 3600000) {
//                        interval = 3500000;
//                    }
                    scheduleUpdate(interval, launch.getId());
                    //Launch is within 48 hours
                } else if (timeToFinish < 172800000) {
                    scheduleUpdate((future.getTimeInMillis() - 86400000) - now.getTimeInMillis(), launch.getId());
                } else {
                    scheduleUpdate((timeToFinish / 2) + 43200000, launch.getId());
                }
            }
        } else {
            //Get sync period.
            String notificationTimer = this.sharedPref.getString("notification_sync_time", "24");

            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(notificationTimer);

            if (m.matches()) {
                int hrs = Integer.parseInt(m.group(1));
                interval = (long) hrs * 60 * 60 * 1000;
                scheduleUpdate(interval, launch.getId());
            }
        }
    }

    private void notifyUserImminent(Launch launch, int minutes) {
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(getApplicationContext());
        NotificationManager mNotifyManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        String launchDate;
        String expandedText;
        String launchName = launch.getName();
        String launchURL;
        String launchPad = launch.getLocation().getName();


        if (launch.getNet() != null) {
            //Get launch date
            if (sharedPref.getBoolean("local_time", true)) {
                SimpleDateFormat df = new SimpleDateFormat("hh:mm a zzz");
                df.toLocalizedPattern();
                Date date = launch.getNet();
                launchDate = df.format(date);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a zzz");
                Date date = launch.getNet();
                launchDate = sdf.format(date);
            }
            expandedText = "Go for launch at " + launchDate + ".";
        } else {
            expandedText = "Launch in " + minutes + " minutes.";
        }
        mBuilder.setSubText(launchPad);


        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent appIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        String ringtoneBox = sharedPref.getString("notifications_new_message_ringtone", "default ringtone");
        Uri alarmSound = Uri.parse(ringtoneBox);

        //TODO add launch image when ready from LL
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.nav_header));

        mBuilder.setContentTitle(launchName)
                .setContentText(expandedText)
                .setSmallIcon(R.drawable.ic_rocket_white)
                .setAutoCancel(true)
                .setContentText(expandedText)
                .extend(wearableExtender)
                .setContentIntent(appIntent)
                .setSound(alarmSound);


        //Check if heads up notifications are enabled, set priority to high if so.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                sharedPref.getBoolean("notifications_new_message_heads_up", true)) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        //Check if vibration is enabled.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN &&
                sharedPref.getBoolean("notifications_new_message_vibrate", true)) {
            mBuilder.setVibrate(new long[]{750, 750});
        }

        //Check if blinking LED is enabled.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
                && sharedPref.getBoolean("notifications_new_message_led", true)) {
            mBuilder.setLights(Color.GREEN, 3000, 3000);
        }


        if (sharedPref.getBoolean("notifications_new_message_webcast", false)) {
            if (launch.getVidURLs() != null && launch.getVidURLs().size() > 0) {
                // Sets up the Open and Share action buttons that will appear in the
                // big view of the notification.
                Intent vidIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(launch.getVidURLs().get(0).getVal()));
                PendingIntent vidPendingIntent = PendingIntent.getActivity(this, 0, vidIntent, 0);

                mBuilder.addAction(R.drawable.ic_open_in_browser_white, "Watch Live", vidPendingIntent);
                mNotifyManager.notify(Constants.NOTIF_ID_HOUR + launch.getId(), mBuilder.build());
            }
        } else {
            if (launch.getVidURLs() != null && launch.getVidURLs().size() > 0) {
                // Sets up the Open and Share action buttons that will appear in the
                // big view of the notification.
                Intent vidIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(launch.getVidURLs().get(0).getVal()));
                PendingIntent vidPendingIntent = PendingIntent.getActivity(this, 0, vidIntent, 0);

                mBuilder.addAction(R.drawable.ic_open_in_browser_white, "Watch Live", vidPendingIntent);
            }
            mNotifyManager.notify(Constants.NOTIF_ID_HOUR + launch.getId(), mBuilder.build());
        }
    }

    private void notifyUser(Launch launch, int hours) {
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(getApplicationContext());
        NotificationManager mNotifyManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        String launchDate;
        String expandedText;
        String launchName = launch.getName();
        String launchPad = launch.getLocation().getName();

        String ringtoneBox = sharedPref.getString("notifications_new_message_ringtone", "default ringtone");
        Uri alarmSound = Uri.parse(ringtoneBox);

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent appIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        if (launch.getNet() != null) {
            //Get launch date
            if (sharedPref.getBoolean("local_time", true)) {
                SimpleDateFormat df = new SimpleDateFormat("hh:mm a zzz");
                df.toLocalizedPattern();
                Date date = launch.getNet();
                launchDate = df.format(date);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a zzz");
                Date date = launch.getNet();
                launchDate = sdf.format(date);
            }
            expandedText = "Launch attempt in " + hours + " hours at " + launchDate;
        } else {
            expandedText = "Launch attempt in " + hours + " hours.";
        }
        mBuilder.setSubText(launchPad);

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true)
                        .setBackground(BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.nav_header));

        mBuilder.setContentTitle(launchName)
                .setContentText(expandedText)
                .setSmallIcon(R.drawable.ic_rocket_white)
                .setContentIntent(appIntent)
                .setContentText(expandedText)
                .extend(wearableExtender)
                .setSound(alarmSound)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                sharedPref.getBoolean("notifications_new_message_heads_up", true)) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN &&
                sharedPref.getBoolean("notifications_new_message_vibrate", true)) {
            mBuilder.setVibrate(new long[]{750, 750});
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
                && sharedPref.getBoolean("notifications_new_message_led", true)) {
            mBuilder.setLights(Color.GREEN, 3000, 3000);
        }

        if (sharedPref.getBoolean("notifications_new_message_webcast", false)) {
            if (launch.getVidURLs() != null && launch.getVidURLs().size() > 0) {
                mNotifyManager.notify(Constants.NOTIF_ID_HOUR + launch.getId(), mBuilder.build());
            }
        } else {
            mNotifyManager.notify(Constants.NOTIF_ID_HOUR + launch.getId(), mBuilder.build());
        }
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public void scheduleUpdate(long interval, int launchId) {
        if (interval < 0){
            interval = Math.abs(interval);
        } else if (interval == 0){
            interval = 360000;
        }
            NextLaunchJob.scheduleJob(interval, launchId);
            this.startService(new Intent(this, UpdateWearService.class));
    }

}
