package me.calebjones.spacelaunchnow.content.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import timber.log.Timber;

public class UpdateUpcomingLaunchesReceiver extends BroadcastReceiver {
    private static SharedPreference sharedPreference;
    private SharedPreferences sharedPref;

    public void onReceive(Context context, Intent intent) {
        Timber.d("UpdateUpcomingLaunchesReceiver - Broadcast %s received!", intent.getAction());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreference = SharedPreference.getInstance(context);
        String action = intent.getAction();
        if (sharedPref.getBoolean("background_sync", true)) {
            if (Strings.ACTION_UPDATE_UP_LAUNCHES.equals(action)) {
                sharedPreference.setFiltered(false);

                Intent update_upcoming_launches = new Intent(context, LaunchDataService.class);
                update_upcoming_launches.setAction(Strings.ACTION_GET_ALL);
                context.startService(update_upcoming_launches);

            } else if (Strings.ACTION_CHECK_NEXT_LAUNCH_TIMER.equals(action)) {

                Intent update_upcoming_launches = new Intent(context, LaunchDataService.class);
                update_upcoming_launches.setAction(Strings.ACTION_UPDATE_NEXT_LAUNCH);
                context.startService(update_upcoming_launches);

            } else if (Strings.ACTION_UPDATE_PREV_LAUNCHES.equals(action)) {

                sharedPreference.setFiltered(false);
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
    }
}
