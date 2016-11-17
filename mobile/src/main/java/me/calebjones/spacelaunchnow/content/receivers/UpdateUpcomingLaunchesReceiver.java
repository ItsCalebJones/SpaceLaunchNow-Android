package me.calebjones.spacelaunchnow.content.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.models.Constants;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.utils.Connectivity;
import timber.log.Timber;

import static android.content.Context.ALARM_SERVICE;

public class UpdateUpcomingLaunchesReceiver extends BroadcastReceiver {
    private static ListPreferences listPreference;
    private static SwitchPreferences switchPreferences;
    private SharedPreferences sharedPref;

    public void onReceive(Context context, Intent intent) {
        Timber.d("UpdateUpcomingLaunchesReceiver - Broadcast %s received!", intent.getAction());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String action = intent.getAction();

        if (Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER.equals(action)) {
            boolean wifiOnly = sharedPref.getBoolean("wifi_only", false);
            if (wifiOnly){
                if (Connectivity.isConnectedWifi(context)){
                    Intent nextIntent = new Intent(context, LaunchDataService.class);
                    nextIntent.setAction(Constants.ACTION_UPDATE_BACKGROUND);
                    context.startService(nextIntent);
                } else {
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    long nextUpdate = Calendar.getInstance().getTimeInMillis() + (8 * 60 * 60 * 1000);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, nextUpdate,
                            PendingIntent.getBroadcast(context, 165432, new Intent(Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER), 0));
                }
            } else {
                Intent nextIntent = new Intent(context, LaunchDataService.class);
                nextIntent.setAction(Constants.ACTION_UPDATE_BACKGROUND);
                context.startService(nextIntent);
            }
        }
    }
}
