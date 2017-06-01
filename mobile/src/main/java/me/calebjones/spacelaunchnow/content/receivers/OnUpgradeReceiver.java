package me.calebjones.spacelaunchnow.content.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.calebjones.spacelaunchnow.content.jobs.SyncJob;
import me.calebjones.spacelaunchnow.content.jobs.UpdateJob;
import me.calebjones.spacelaunchnow.content.services.NextLaunchTracker;

public class OnUpgradeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SyncJob.schedulePeriodicJob(context);
        UpdateJob.scheduleJob(context);
        context.startService(new Intent(context, NextLaunchTracker.class));
    }
}
