package me.calebjones.spacelaunchnow.content.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;

import me.calebjones.spacelaunchnow.R;


public class NotificationHelper extends ContextWrapper {
    private NotificationManager notifManager;
    public static final String CHANNEL_LAUNCH_IMMINENT = "me.calebjones.spacelaunchnow.LAUNCH_IMMINENT";
    public static final String CHANNEL_LAUNCH_IMMINENT_NAME = "Launch Notification (Less then One Hour)";
    public static final String CHANNEL_LAUNCH_REMINDER = "me.calebjones.spacelaunchnow.LAUNCH_REMINDER";
    public static final String CHANNEL_LAUNCH_REMINDER_NAME = "Launch Notification (24 Hour)";
    public static final String CHANNEL_LAUNCH_WEEKLY = "me.calebjones.spacelaunchnow.LAUNCH_WEEKLY_DIGEST";
    public static final String CHANNEL_LAUNCH_WEEKLY_NAME = "Weekly Summary";
    public static final String CHANNEL_LAUNCH_UPDATE = "me.calebjones.spacelaunchnow.LAUNCH_UPDATE";
    public static final String CHANNEL_LAUNCH_UPDATE_NAME = "Launch Status Updates";
    public static final String CHANNEL_LAUNCH_SILENT = "me.calebjones.spacelaunchnow.LAUNCH_SILENT";
    public static final String CHANNEL_LAUNCH_SILENT_NAME = "Do Not Disturb";

//Create your notification channels//

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {

        NotificationChannel launchImminent = new NotificationChannel(CHANNEL_LAUNCH_IMMINENT,
                CHANNEL_LAUNCH_IMMINENT_NAME, NotificationManager.IMPORTANCE_HIGH);
        launchImminent.enableLights(true);
        launchImminent.setLightColor(ContextCompat.getColor(this, R.color.primary));
        launchImminent.setShowBadge(true);
        launchImminent.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(launchImminent);

        NotificationChannel statusChanged = new NotificationChannel(CHANNEL_LAUNCH_UPDATE,
                CHANNEL_LAUNCH_REMINDER_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        statusChanged.enableLights(false);
        statusChanged.enableVibration(true);
        statusChanged.setLightColor(Color.RED);
        statusChanged.setShowBadge(true);
        getManager().createNotificationChannel(statusChanged);

        NotificationChannel launchReminder = new NotificationChannel(CHANNEL_LAUNCH_REMINDER,
                CHANNEL_LAUNCH_UPDATE_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        launchReminder.enableLights(false);
        launchReminder.setLightColor(Color.RED);
        launchReminder.setShowBadge(true);
        getManager().createNotificationChannel(launchReminder);

        NotificationChannel launchWeeklySummary = new NotificationChannel(CHANNEL_LAUNCH_WEEKLY,
                CHANNEL_LAUNCH_WEEKLY_NAME, NotificationManager.IMPORTANCE_LOW);
        launchWeeklySummary.enableLights(false);
        launchWeeklySummary.setLightColor(Color.RED);
        launchWeeklySummary.setShowBadge(true);
        getManager().createNotificationChannel(launchWeeklySummary);

        NotificationChannel silentChannel = new NotificationChannel(CHANNEL_LAUNCH_SILENT,
                CHANNEL_LAUNCH_SILENT_NAME, NotificationManager.IMPORTANCE_MIN);
        silentChannel.enableLights(false);
        silentChannel.setLightColor(Color.RED);
        silentChannel.setShowBadge(true);
        getManager().createNotificationChannel(silentChannel);

    }


    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

//Send your notifications to the NotificationManager system service//

    public NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }
}
