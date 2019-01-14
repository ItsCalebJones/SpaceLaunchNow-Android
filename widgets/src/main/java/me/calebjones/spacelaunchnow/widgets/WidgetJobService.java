package me.calebjones.spacelaunchnow.widgets;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import me.calebjones.spacelaunchnow.common.content.jobs.SyncWidgetJob;
import me.calebjones.spacelaunchnow.widgets.launchcard.LaunchCardCompactManager;
import me.calebjones.spacelaunchnow.widgets.launchlist.LaunchListManager;
import me.calebjones.spacelaunchnow.widgets.launchlist.LaunchListWidgetProvider;
import me.calebjones.spacelaunchnow.widgets.wordtimer.LaunchWordTimerManager;
import me.calebjones.spacelaunchnow.widgets.launchcard.LaunchCardCompactWidgetProvider;
import me.calebjones.spacelaunchnow.widgets.wordtimer.LaunchWordTimerWidgetProvider;
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
        LaunchListManager launchListManager = new LaunchListManager(context);
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

            int listIds[] = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(new ComponentName(context,
                            LaunchListWidgetProvider.class));

            for (int id : listIds){
                launchListManager.updateAppWidget(id);
            }
        } else {
            SyncWidgetJob.scheduleImmediately();
        }
    }
}
