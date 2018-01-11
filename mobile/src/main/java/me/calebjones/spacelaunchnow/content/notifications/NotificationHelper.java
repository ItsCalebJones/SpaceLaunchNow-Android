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


class NotificationHelper extends ContextWrapper {
    private NotificationManager notifManager;
    public static final String CHANNEL_LAUNCH_IMMINENT = "me.calebjones.spacelaunchnow.LAUNCH_IMMINENT";
    public static final String CHANNEL_LAUNCH_IMMINENT_NAME = "Launch Imminent";
    public static final String CHANNEL_LAUNCH_UPDATE = "me.calebjones.spacelaunchnow.LAUNCH_UPDATE";
    public static final String CHANNEL_LAUNCH_UPDATE_NAME = "Launch Update";

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
                CHANNEL_LAUNCH_UPDATE_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        statusChanged.enableLights(false);
        statusChanged.enableVibration(true);
        statusChanged.setLightColor(Color.RED);
        statusChanged.setShowBadge(true);
        getManager().createNotificationChannel(statusChanged);

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
