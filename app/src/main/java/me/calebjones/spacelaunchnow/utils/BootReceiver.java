package me.calebjones.spacelaunchnow.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import me.calebjones.spacelaunchnow.LaunchApplication;
import timber.log.Timber;

/**
 * Created by cjones on 11/10/15.
 */
public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("Boot Received!");

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Boolean notificationCheckBox = sharedPreferences
                .getBoolean("notifications_new_message", false);
        Timber.d("Notification: %s", String.valueOf(notificationCheckBox));
        if (notificationCheckBox) {
//            context.startService(new Intent(context, LaunchDataService.class));
        }
    }
}
