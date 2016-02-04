package me.calebjones.spacelaunchnow.content.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;


public class BootReceiver extends BroadcastReceiver{
    private SharedPreferences sharedPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean("background_sync", true)) {
            Calendar c = Calendar.getInstance();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c.getTime());

            String url = "https://launchlibrary.net/1.1/launch/1950-01-01/" + String.valueOf(formattedDate) + "?sort=desc&limit=1000";

            Intent launchIntent = new Intent(context, LaunchDataService.class);
            launchIntent.setAction(Strings.ACTION_GET_ALL);
            launchIntent.putExtra("URL", url);
            context.startService(launchIntent);
        }
    }
}
