package me.calebjones.spacelaunchnow.widget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import me.calebjones.spacelaunchnow.content.jobs.SyncJob;
import me.calebjones.spacelaunchnow.content.jobs.SyncWidgetJob;
import timber.log.Timber;

public class WidgetJobService extends JobIntentService {

    private static final int JOB_ID = 1234598765;

    static void enqueueWork(Context context, Intent i) {
        Timber.v("Scheduling work...");
        enqueueWork(context, WidgetJobService.class, JOB_ID, i);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Timber.v("onHandleWork - Schedule Immediately.");
        SyncWidgetJob.scheduleImmediately();
    }
}
