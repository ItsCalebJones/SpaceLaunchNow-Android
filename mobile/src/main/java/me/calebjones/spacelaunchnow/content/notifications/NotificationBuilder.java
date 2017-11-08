package me.calebjones.spacelaunchnow.content.notifications;

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
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.ui.main.MainActivity;
import me.calebjones.spacelaunchnow.utils.Utils;

import static me.calebjones.spacelaunchnow.content.notifications.NotificationHelper.CHANNEL_LAUNCH_IMMINENT;

public class NotificationBuilder {
    public static void notifyUser(Context context, Launch launch, long timeToFinish) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyManager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_LAUNCH_IMMINENT);
            mNotifyManager = notificationHelper.getManager();
        } else {
            mBuilder = new NotificationCompat.Builder(context);
            mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        String launchDate;
        String expandedText;
        String launchName = launch.getName();
        String launchPad = launch.getLocation().getName();

        String ringtoneBox = sharedPref.getString("notifications_new_message_ringtone", "default ringtone");
        Uri alarmSound = Uri.parse(ringtoneBox);

        Intent resultIntent = new Intent(context, LaunchDetailActivity.class);
        resultIntent.putExtra("TYPE", "launch");
        resultIntent.putExtra("launchID", launch.getId());

        PendingIntent pending = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
            expandedText = getContentText(timeToFinish, launchDate);
        } else {
            expandedText = getContentText(timeToFinish);
        }
        mBuilder.setSubText(launchPad);

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(true);
        if (launch.getRocket().getImageURL() != null && launch.getRocket().getImageURL().length() > 0 && !launch.getRocket().getImageURL().contains("placeholder")) {
            wearableExtender.setBackground(Utils.getBitMapFromUrl(context, launch.getRocket().getImageURL()));
        } else {
            wearableExtender.setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.nav_header
            ));
        }

        mBuilder.setContentTitle(launchName)
                .setContentText(expandedText)
                .setSmallIcon(R.drawable.ic_rocket_white)
                .setAutoCancel(true)
                .setContentText(expandedText)
                .extend(wearableExtender)
                .setContentIntent(pending)
                .setSound(alarmSound)
                .setChannelId(CHANNEL_LAUNCH_IMMINENT);

        if (launch.getRocket().getImageURL() != null && launch.getRocket().getImageURL().length() > 0 && !launch.getRocket().getImageURL().contains("placeholder")) {
            Bitmap bitmap = Utils.getBitMapFromUrl(context, launch.getRocket().getImageURL());
            if (bitmap != null){
                mBuilder.setLargeIcon(bitmap);
            }
        }

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

    private static String getContentText(long timeToFinish) {
        if (timeToFinish < 3600000){
            int minutes = (int) ((timeToFinish / (1000 * 60)) % 60);
            if (minutes == 9) {
                return "Launch attempt in ten minutes.";
            } else if (minutes == 59){
                return "Launch attempt in one hour.";
            } else {
                return "Launch attempt in " + minutes + " minutes.";
            }
        } else {
            int hours = (int) ((timeToFinish / (1000 * 60 * 60)) % 24);
            if (hours == 23) {
                return "Launch attempt in twenty-four hours.";
            } else {
                return "Launch attempt in " + hours + " hours.";
            }
        }
    }

    private static String getContentText(long timeToFinish, String launchDate) {
        if (timeToFinish < 3600000){
            int minutes = (int) ((timeToFinish / (1000 * 60)) % 60);
            if (minutes == 9) {
                return "Launch attempt in ten minutes at " + launchDate;
            } else if (minutes == 59){
                return "Launch attempt in one hour at " + launchDate;
            } else {
                return "Launch attempt in " + minutes + " minutes at " + launchDate;
            }
        } else {
            int hours = (int) ((timeToFinish / (1000 * 60 * 60)) % 24);
            if (hours == 23) {
                return "Launch attempt in twenty-four hours at " + launchDate;
            } else {
                return "Launch attempt in " + hours + " hours at " + launchDate;
            }
        }
    }
}