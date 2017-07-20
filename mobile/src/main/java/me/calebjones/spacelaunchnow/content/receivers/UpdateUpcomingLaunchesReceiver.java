package me.calebjones.spacelaunchnow.content.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.data.models.Constants;
import timber.log.Timber;

public class UpdateUpcomingLaunchesReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPref;

    public void onReceive(Context context, Intent intent) {
        Timber.d("UpdateUpcomingLaunchesReceiver - Broadcast %s received!", intent.getAction());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String action = intent.getAction();

        if (Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER.equals(action)) {
            LaunchDataService.startActionUpdateNextLaunchMini(context);
            Toast.makeText(context, "Refreshing launch data...", Toast.LENGTH_SHORT).show();
        }
    }
}
