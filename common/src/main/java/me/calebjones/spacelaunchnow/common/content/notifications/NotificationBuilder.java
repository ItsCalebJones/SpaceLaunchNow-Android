package me.calebjones.spacelaunchnow.common.content.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import androidx.core.app.NotificationCompat;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import jonathanfinerty.once.Amount;
import jonathanfinerty.once.Once;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.activity.LaunchDetailActivity;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.common.content.notifications.NotificationHelper.CHANNEL_LAUNCH_IMMINENT;
import static me.calebjones.spacelaunchnow.common.content.notifications.NotificationHelper.CHANNEL_LAUNCH_REMINDER;
import static me.calebjones.spacelaunchnow.common.content.notifications.NotificationHelper.CHANNEL_LAUNCH_SILENT;
import static me.calebjones.spacelaunchnow.common.content.notifications.NotificationHelper.CHANNEL_NEWS;
import static me.calebjones.spacelaunchnow.common.content.notifications.NotificationHelper.CHANNEL_NEWS_NAME;

public class NotificationBuilder {

    private static String liveIcon = "\uD83D\uDD34";

    public static void buildNotification(Context context, Launch launch, String title, String expandedText, String channelId) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyManager;
        int notificationId;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_LAUNCH_REMINDER);
            mNotifyManager = notificationHelper.getManager();
        } else {
            mBuilder = new NotificationCompat.Builder(context);
            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        String launchPad = launch.getPad().getLocation().getName();
        boolean isDoNotDisturb = sharedPref.getBoolean("do_not_disturb_status", false);

        if (isDoNotDisturb) {
            try {
                isDoNotDisturb = isTimeBetweenTwoTime(sharedPref.getString("do_not_disturb_start_time", "22:00"),
                        sharedPref.getString("do_not_disturb_end_time", "08:00"),
                        new SimpleDateFormat("HH:mm").format(new Date()));
            } catch (ParseException e) {
                Timber.e(e);
                isDoNotDisturb = false;
            }
        }

        Intent resultIntent = new Intent(context, LaunchDetailActivity.class);
        resultIntent.putExtra("TYPE", "launch");
        resultIntent.putExtra("launchID", launch.getId());

        PendingIntent pending = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setSubText(launchPad);

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true);

        Once.markDone("SHOW_FILTER_SETTINGS");
        if (Once.beenDone("SHOW_FILTER_SETTINGS", Amount.lessThan(10))) {
            Intent intent;
            try {
                intent = new Intent(context, Class.forName("me.calebjones.spacelaunchnow.ui.main.MainActivity"));
            } catch (ClassNotFoundException e) {
                Timber.e(e);
                return;
            }
            intent.setAction("SHOW_FILTERS");
            PendingIntent archiveIntent = PendingIntent.getActivity(context,
                    2,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action filterSettings =
                    new NotificationCompat.Action.Builder(R.drawable.ic_filter,
                            "Filters", archiveIntent)
                            .build();

            mBuilder.addAction(filterSettings);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent notificationSettings = new Intent();
                notificationSettings.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

                //for Android 5-7
                notificationSettings.putExtra("app_package", context.getPackageName());
                notificationSettings.putExtra("app_uid", context.getApplicationInfo().uid);

                // for Android O
                notificationSettings.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());

                PendingIntent channelPendingIntent = PendingIntent.getActivity(context,
                        3,
                        notificationSettings,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Action channelSettings =
                        new NotificationCompat.Action.Builder(R.drawable.ic_notifications_white,
                                "Settings", channelPendingIntent)
                                .build();

                mBuilder.addAction(channelSettings);

            }

        }

        notificationId = (int) (launch.getNet().getTime() / 1000);

        mBuilder.setContentTitle(title)
                .setSmallIcon(R.drawable.ic_rocket)
                .setAutoCancel(true)
                .setContentText(expandedText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(expandedText))
                .extend(wearableExtender)
                .setContentIntent(pending);

        String ringtoneBox = sharedPref.getString("notifications_new_message_ringtone",
                "default ringtone");
        Uri alarmSound = Uri.parse(ringtoneBox);
        if (isDoNotDisturb) {
            mBuilder.setChannelId(CHANNEL_LAUNCH_SILENT);
        } else if (channelId.contains(CHANNEL_LAUNCH_IMMINENT)) {
            mBuilder.setChannelId(CHANNEL_LAUNCH_IMMINENT).setSound(alarmSound).setCategory(NotificationCompat.CATEGORY_ALARM);
        } else {
            mBuilder.setChannelId(channelId).setSound(alarmSound).setCategory(NotificationCompat.CATEGORY_EVENT);
        }

        if (launch.getRocket().getConfiguration().getImageUrl() != null
                && launch.getRocket().getConfiguration().getImageUrl().length() > 0
                && !launch.getRocket().getConfiguration().getImageUrl().contains("placeholder")) {
            Bitmap bitmap = null;
            try {
                bitmap = Utils.getBitMapFromUrl(context, launch.getRocket().getConfiguration().getImageUrl());
            } catch (ExecutionException | InterruptedException e) {
                Timber.e(e);
            }
            if (bitmap != null) {
                mBuilder.setLargeIcon(bitmap);
                wearableExtender.setBackground(bitmap);
            }
        }

        if (isDoNotDisturb) {
            mBuilder.setPriority(Notification.PRIORITY_LOW);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && sharedPref.getBoolean("notifications_new_message_heads_up", true)) {
                mBuilder.setPriority(Notification.PRIORITY_HIGH);
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN &&
                sharedPref.getBoolean("notifications_new_message_vibrate", true)) {
            mBuilder.setVibrate(new long[]{750, 750});
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
                && sharedPref.getBoolean("notifications_new_message_led", true)) {
            mBuilder.setLights(Color.GREEN, 3000, 3000);
        }

        mNotifyManager.notify(notificationId, mBuilder.build());
    }

    private static boolean isTimeBetweenTwoTime(String argStartTime,
                                               String argEndTime,
                                               String argCurrentTime) throws ParseException {
        String reg = "^([0-2][0-9]|[0-3]):([0-5][0-9])$";
        //
        if (argStartTime.matches(reg) && argEndTime.matches(reg)
                && argCurrentTime.matches(reg)) {
            boolean valid = false;
            // Start Time
            java.util.Date startTime = new SimpleDateFormat("HH:mm")
                    .parse(argStartTime);
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startTime);

            // Current Time
            java.util.Date currentTime = new SimpleDateFormat("HH:mm")
                    .parse(argCurrentTime);
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(currentTime);

            // End Time
            java.util.Date endTime = new SimpleDateFormat("HH:mm")
                    .parse(argEndTime);
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(endTime);

            //
            if (currentTime.compareTo(endTime) < 0) {

                currentCalendar.add(Calendar.DATE, 1);
                currentTime = currentCalendar.getTime();

            }

            if (startTime.compareTo(endTime) < 0) {

                startCalendar.add(Calendar.DATE, 1);
                startTime = startCalendar.getTime();

            }
            //
            if (currentTime.before(startTime)) {

                Timber.v(" Time is Lesser ");

                valid = false;
            } else {

                if (currentTime.after(endTime)) {
                    endCalendar.add(Calendar.DATE, 1);
                    endTime = endCalendar.getTime();

                }

                Timber.v("Comparing , Start Time /n %s", startTime);
                Timber.v("Comparing , End Time /n %s", endTime);
                Timber.v("Comparing , Current Time /n %s", currentTime);

                if (currentTime.before(endTime)) {
                    Timber.v("RESULT, Time lies b/w");
                    valid = true;
                } else {
                    valid = false;
                    Timber.v("RESULT, Time does not lies b/w");
                }

            }
            return valid;

        } else {
            throw new IllegalArgumentException(
                    "Not a valid time, expecting HH:MM format");
        }

    }


    private static String getTimeFormatted(Context context, Date date) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SimpleDateFormat sdf;
        if (!DateFormat.is24HourFormat(context)){
            sdf = Utils.getSimpleDateFormatForUI("h:mm a zzz");
        } else {
            sdf = Utils.getSimpleDateFormatForUI("HH:mm zzz");
        }
        //Get launch date
        if (sharedPref.getBoolean("local_time", true)) {
            return sdf.format(date);
        } else {
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(date);
        }
    }

    private static String getTimeFormattedLong(Context context, Date date) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SimpleDateFormat sdf;
        if (!DateFormat.is24HourFormat(context)){
            sdf = Utils.getSimpleDateFormatForUI("MMMM d, yyyy h:mm a zzz");
        } else {
            sdf = Utils.getSimpleDateFormatForUI("MMMM d, yyyy HH:mm zzz");
        }
        //Get launch date
        if (sharedPref.getBoolean("local_time", true)) {
            return sdf.format(date);
        } else {
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(date);
        }
    }

    public static void notifyUserTwentyFourHours(Context context, Launch launch) {
        String title;
        String expandedText;

        title = launch.getName();
        expandedText = String.format(context.getString(R.string.notification_twenty_four_hours), getTimeFormatted(context, launch.getNet()));

        buildNotification(context, launch, title, expandedText, CHANNEL_LAUNCH_REMINDER);
    }

    public static void notifyUserOneHour(Context context, Launch launch) {
        String title;
        String expandedText;

        title = launch.getName();
        expandedText = String.format(context.getString(R.string.notification_one_hour), getTimeFormatted(context, launch.getNet()));

        buildNotification(context, launch, title, expandedText, CHANNEL_LAUNCH_IMMINENT);
    }

    public static void notifyUserTenMinutes(Context context, Launch launch) {
        String title;
        String expandedText;

        if (launch.getVidURLs() != null && launch.getVidURLs().size() > 0) {
            title = liveIcon + " " + launch.getName();
        } else {
            title = launch.getName();
        }
        expandedText = String.format(context.getString(R.string.notification_ten_minutes), getTimeFormatted(context, launch.getNet()));

        buildNotification(context, launch, title, expandedText, CHANNEL_LAUNCH_IMMINENT);
    }

    public static void notifyUserOneMinute(Context context, Launch launch) {
        String title;
        String expandedText;

        if (launch.getVidURLs() != null && launch.getVidURLs().size() > 0) {
            title = liveIcon + " " + launch.getName();
        } else {
            title = launch.getName();
        }
        expandedText = String.format(context.getString(R.string.notification_one_minute), getTimeFormatted(context, launch.getNet()));

        buildNotification(context, launch, title, expandedText, CHANNEL_LAUNCH_IMMINENT);
    }

    public static void notifyUserInFlight(Context context, Launch launch) {
        String title;
        String expandedText;

        if (launch.getVidURLs() != null && launch.getVidURLs().size() > 0) {
            title = liveIcon + " " + launch.getName();
        } else {
            title = launch.getName();
        }
        expandedText = context.getString(R.string.notification_launch_liftoff);

        buildNotification(context, launch, title, expandedText, CHANNEL_LAUNCH_IMMINENT);
    }

    public static void notifyUserSuccess(Context context, Launch launch) {
        String title;
        String expandedText;

        title = launch.getName();
        expandedText = context.getString(R.string.notification_launch_successful);

        buildNotification(context, launch, title, expandedText, CHANNEL_LAUNCH_REMINDER);
    }

    public static void notifyUserFailure(Context context, Launch launch) {
        String title;
        String expandedText;

        title = launch.getName();
        expandedText = context.getString(R.string.notification_launch_failure);

        buildNotification(context, launch, title, expandedText, CHANNEL_LAUNCH_REMINDER);
    }

    public static void notifyUserPartialFailure(Context context, Launch launch) {
        String title;
        String expandedText;

        title = launch.getName();
        expandedText = context.getString(R.string.notification_launch_partial_failure);

        buildNotification(context, launch, title, expandedText, CHANNEL_LAUNCH_REMINDER);
    }

    public static void notifyUserLaunchScheduleChanged(Context context, Launch launch) {
        String title;
        String expandedText;

        title = "" + launch.getName();
        expandedText = String.format(context.getString(R.string.notification_schedule_changed), getTimeFormattedLong(context, launch.getNet()));

        buildNotification(context, launch, title, expandedText, CHANNEL_LAUNCH_REMINDER);
    }

    public static void notifyUserLaunchWebcastLive(Context context, Launch launch) {
        String title;
        String expandedText;

        title = "\uD83D\uDD34 Webcast Live - " + launch.getName();
        expandedText = "The live webcast has started!";

        buildNotification(context, launch, title, expandedText, CHANNEL_LAUNCH_REMINDER);
    }

    public static void notifyUserTest(Context context, Launch launch) {
        String title;
        String expandedText;
        title = launch.getName();
        expandedText = "Test Notification";
        buildNotification(context, launch, title, expandedText, CHANNEL_LAUNCH_REMINDER);
    }

    public static void notifyUserEventUpcoming(Context context, Event event) {
        String title;
        String expandedText;

        title = "" + event.getName();
        expandedText = event.getDescription();

        buildEventNotification(context, event, title, expandedText, CHANNEL_NEWS);
    }

    public static void notifyUserEventWebcastLive(Context context, Event event) {
        String title;
        String expandedText;

        title = "\uD83D\uDD34 Webcast Live - " + event.getName();
        expandedText = event.getDescription();

        buildEventNotification(context, event, title, expandedText, CHANNEL_NEWS);
    }

    private static void buildEventNotification(Context context, Event event, String title, String expandedText, String channelNewsName) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyManager;
        int notificationId;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_NEWS);
            mNotifyManager = notificationHelper.getManager();
        } else {
            mBuilder = new NotificationCompat.Builder(context);
            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        String launchPad = event.getLocation();
        boolean isDoNotDisturb = sharedPref.getBoolean("do_not_disturb_status", false);

        if (isDoNotDisturb) {
            try {
                isDoNotDisturb = isTimeBetweenTwoTime(sharedPref.getString("do_not_disturb_start_time", "22:00"),
                        sharedPref.getString("do_not_disturb_end_time", "08:00"),
                        new SimpleDateFormat("HH:mm").format(new Date()));
            } catch (ParseException e) {
                Timber.e(e);
                isDoNotDisturb = false;
            }
        }


        //TODO Add Event detail and load that page.
        Intent resultIntent;
        try {
            resultIntent = new Intent(context, Class.forName("me.calebjones.spacelaunchnow.events.detail.EventDetailsActivity"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        resultIntent.putExtra("eventId", event.getId());
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent eventIntent = PendingIntent.getActivity(context,
                2,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setSubText(launchPad);

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true);

        notificationId = (int) (event.getDate().getTime() / 1000);

        mBuilder.setContentTitle(title)
                .setSmallIcon(R.drawable.ic_rocket)
                .setAutoCancel(true)
                .setContentText(expandedText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(expandedText))
                .extend(wearableExtender)
                .setContentIntent(eventIntent);

//        if (event.getWebcastLive() && event.getVideoUrl() != null) {
//            Intent watchLive = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getVideoUrl()));
//
//
//
//            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, watchLive, 0);
//
//            NotificationCompat.Action channelSettings =
//                    new NotificationCompat.Action.Builder(R.drawable.ic_notifications_white,
//                            "Watch Live", contentIntent)
//                            .build();
//
//            mBuilder.addAction(channelSettings);
//        }

        String ringtoneBox = sharedPref.getString("notifications_new_message_ringtone",
                "default ringtone");
        Uri alarmSound = Uri.parse(ringtoneBox);
        if (isDoNotDisturb) {
            mBuilder.setChannelId(CHANNEL_LAUNCH_SILENT);
        } else {
            mBuilder.setChannelId(channelNewsName).setSound(alarmSound).setCategory(NotificationCompat.CATEGORY_EVENT);
        }

        if (event.getFeatureImage() != null
                && event.getFeatureImage().length() > 0) {
            Bitmap bitmap = null;
            try {
                bitmap = Utils.getBitMapFromUrl(context, event.getFeatureImage());
            } catch (ExecutionException | InterruptedException e) {
                Timber.e(e);
            }
            if (bitmap != null) {
                NotificationCompat.BigPictureStyle bpStyle = new NotificationCompat.BigPictureStyle();
                bpStyle.bigPicture(bitmap).build();
                mBuilder.setStyle(bpStyle);
                wearableExtender.setBackground(bitmap);
            }
        }

        if (isDoNotDisturb) {
            mBuilder.setPriority(Notification.PRIORITY_LOW);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && sharedPref.getBoolean("notifications_new_message_heads_up", true)) {
                mBuilder.setPriority(Notification.PRIORITY_HIGH);
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN &&
                sharedPref.getBoolean("notifications_new_message_vibrate", true)) {
            mBuilder.setVibrate(new long[]{750, 750});
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN
                && sharedPref.getBoolean("notifications_new_message_led", true)) {
            mBuilder.setLights(Color.GREEN, 3000, 3000);
        }

        mNotifyManager.notify(notificationId, mBuilder.build());
    }
}