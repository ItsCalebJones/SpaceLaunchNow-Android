package me.calebjones.spacelaunchnow.content.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.VehicleDataService;
import timber.log.Timber;

public class UpdateUpcomingLaunchesReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Timber.d("UpdateUpcomingLaunchesReceiver - Broadcast %s received!", intent.getAction());
        String action = intent.getAction();
        if (Strings.ACTION_UPDATE_UP_LAUNCHES.equals(action)) {
        Intent update_upcoming_launches = new Intent(context, LaunchDataService.class);
        update_upcoming_launches.setAction(Strings.ACTION_GET_ALL);
        context.startService(update_upcoming_launches);
        Intent update_rockets = new Intent(context, VehicleDataService.class);
        update_rockets.setAction(Strings.ACTION_GET_VEHICLES_DETAIL);
        context.startService(update_rockets);
        } else if (Strings.ACTION_CHECK_NEXT_LAUNCH_TIMER.equals(action)) {
            Intent update_upcoming_launches = new Intent(context, LaunchDataService.class);
            update_upcoming_launches.setAction(Strings.ACTION_GET_UP_LAUNCHES);
            context.startService(update_upcoming_launches);
        } else if (Strings.ACTION_UPDATE_PREV_LAUNCHES.equals(action)){
            Intent update_prev_launches = new Intent(context, LaunchDataService.class);
            int id = intent.getIntExtra("id", 0);
            if (id != 0){
                Timber.d("Checking ID: %s", id);
                update_prev_launches.putExtra("id", id);
            }
            update_prev_launches.setAction(Strings.ACTION_GET_PREV_LAUNCHES);
            context.startService(update_prev_launches);
        }
    }
}
