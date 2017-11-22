package me.calebjones.spacelaunchnow.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import me.calebjones.spacelaunchnow.content.jobs.SyncWidgetJob;
import me.calebjones.spacelaunchnow.widget.launchcard.LaunchCardCompactManager;
import me.calebjones.spacelaunchnow.widget.launchcard.UpdateLaunchCardJob;
import me.calebjones.spacelaunchnow.widget.wordtimer.LaunchWordTimerManager;
import me.calebjones.spacelaunchnow.widget.wordtimer.UpdateWordTimerJob;
import me.calebjones.spacelaunchnow.widget.launchcard.LaunchCardCompactWidgetProvider;
import me.calebjones.spacelaunchnow.widget.wordtimer.LaunchWordTimerWidgetProvider;
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
        Context context = getApplicationContext();
        LaunchCardCompactManager launchCardCompactManager = new LaunchCardCompactManager(context);
        LaunchWordTimerManager launchWordTimerManager = new LaunchWordTimerManager(context);
        if (intent.hasExtra("updateUIOnly") && intent.getBooleanExtra("updateUIOnly", false)){
            int cardIds[] = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(new ComponentName(context,
                            LaunchCardCompactWidgetProvider.class));

            for (int id : cardIds){
                launchCardCompactManager.updateAppWidget(id);
            }

            int timerIds[] = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(new ComponentName(context,
                            LaunchWordTimerWidgetProvider.class));

            for (int id : timerIds){
                launchWordTimerManager.updateAppWidget(id);
            }
        } else {
            SyncWidgetJob.scheduleImmediately();
        }
    }
}
