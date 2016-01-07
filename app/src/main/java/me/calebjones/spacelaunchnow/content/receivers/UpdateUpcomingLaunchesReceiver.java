package me.calebjones.spacelaunchnow.content.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.RocketDataService;
import timber.log.Timber;

public class UpdateUpcomingLaunchesReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Timber.d("UpdateUpcomingLaunchesReceiver - Broadcast received!");

        Intent update_upcoming_launches = new Intent(context, LaunchDataService.class);
        update_upcoming_launches.setAction(Strings.ACTION_GET_ALL);
        context.startService(update_upcoming_launches);

        Intent update_rockets = new Intent(context, RocketDataService.class);
        update_rockets.setAction(Strings.ACTION_GET_ROCKETS);
        context.startService(update_rockets);
    }
}
