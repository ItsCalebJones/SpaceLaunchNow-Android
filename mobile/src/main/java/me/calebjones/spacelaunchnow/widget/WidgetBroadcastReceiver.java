package me.calebjones.spacelaunchnow.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;



public class WidgetBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.v("OnReceive: Starting JobService");
        WidgetJobService.enqueueWork(context, intent);
    }
}
