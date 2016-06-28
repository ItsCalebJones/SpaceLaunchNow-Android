package me.calebjones.spacelaunchnow.content.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.MissionDataService;
import me.calebjones.spacelaunchnow.utils.Connectivity;
import timber.log.Timber;

public class UpdateUpcomingLaunchesReceiver extends BroadcastReceiver {
    private static ListPreferences listPreference;
    private static SwitchPreferences switchPreferences;
    private SharedPreferences sharedPref;

    public void onReceive(Context context, Intent intent) {
        Timber.d("UpdateUpcomingLaunchesReceiver - Broadcast %s received!", intent.getAction());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        listPreference = ListPreferences.getInstance(context);
        switchPreferences = SwitchPreferences.getInstance(context);
        String action = intent.getAction();
        if (sharedPref.getBoolean("background_sync", true)) {
            if (Strings.ACTION_UPDATE_UP_LAUNCHES.equals(action)) {
                switchPreferences.setPrevFiltered(false);

                Intent update_upcoming_launches = new Intent(context, LaunchDataService.class);
                update_upcoming_launches.setAction(Strings.ACTION_GET_ALL_WIFI);
                context.startService(update_upcoming_launches);

                context.startService(new Intent(context, MissionDataService.class));

            } else if (Strings.ACTION_UPDATE_PREV_LAUNCHES.equals(action)) {

                switchPreferences.setPrevFiltered(false);
                Intent update_prev_launches = new Intent(context, LaunchDataService.class);

                int id = intent.getIntExtra("id", 0);

                if (id != 0) {
                    Timber.d("Checking ID: %s", id);
                    update_prev_launches.putExtra("id", id);
                }

                update_prev_launches.setAction(Strings.ACTION_GET_PREV_LAUNCHES);
                context.startService(update_prev_launches);
            }
        }

        if (Strings.ACTION_CHECK_NEXT_LAUNCH_TIMER.equals(action)) {
            if(Connectivity.isConnectedWifi(context)){
                Intent nextIntent = new Intent(context, LaunchDataService.class);
                nextIntent.setAction(Strings.ACTION_GET_UP_LAUNCHES);
                context.startService(nextIntent);
            } else {
                Intent nextIntent = new Intent(context, LaunchDataService.class);
                nextIntent.setAction(Strings.ACTION_UPDATE_NEXT_LAUNCH);
                context.startService(nextIntent);
            }
        }
    }
}
