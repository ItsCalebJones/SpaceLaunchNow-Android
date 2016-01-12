package me.calebjones.spacelaunchnow.content.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.content.services.RocketDataService;
import timber.log.Timber;

public class UpdateUpcomingLaunchesReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Timber.d("UpdateUpcomingLaunchesReceiver - Broadcast received!");

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        String url = "https://launchlibrary.net/1.1/launch/1950-01-01/" + String.valueOf(formattedDate) + "?sort=desc&limit=1000";

        Intent update_upcoming_launches = new Intent(context, LaunchDataService.class);
        update_upcoming_launches.setAction(Strings.ACTION_GET_ALL);
        update_upcoming_launches.putExtra("URL", url);
        context.startService(update_upcoming_launches);

        Intent update_rockets = new Intent(context, RocketDataService.class);
        update_rockets.setAction(Strings.ACTION_GET_ROCKETS);
        context.startService(update_rockets);
    }
}
