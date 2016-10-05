package me.calebjones.spacelaunchnow.content.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;


public class BootReceiver extends BroadcastReceiver{
    private SharedPreferences sharedPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean("background_sync", true)) {
            Intent launchIntent = new Intent(context, LaunchDataService.class);
            launchIntent.setAction(Strings.ACTION_UPDATE_BACKGROUND);
            context.startService(launchIntent);
        }
    }
}
