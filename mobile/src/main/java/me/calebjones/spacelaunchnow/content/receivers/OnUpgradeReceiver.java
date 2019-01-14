package me.calebjones.spacelaunchnow.content.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.calebjones.spacelaunchnow.common.content.jobs.SyncJob;

public class OnUpgradeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SyncJob.schedulePeriodicJob(context);
    }
}
