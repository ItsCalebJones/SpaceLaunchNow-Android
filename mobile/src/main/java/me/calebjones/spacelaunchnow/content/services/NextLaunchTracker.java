package me.calebjones.spacelaunchnow.content.services;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import me.calebjones.spacelaunchnow.calendar.CalendarSyncManager;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.jobs.SyncJob;
import me.calebjones.spacelaunchnow.widget.launchcard.LaunchCardCompactManager;
import me.calebjones.spacelaunchnow.widget.launchlist.LaunchListManager;
import me.calebjones.spacelaunchnow.widget.launchlist.LaunchListWidgetProvider;
import me.calebjones.spacelaunchnow.widget.wordtimer.LaunchWordTimerManager;
import me.calebjones.spacelaunchnow.content.wear.WearWatchfaceManager;
import me.calebjones.spacelaunchnow.widget.launchcard.LaunchCardCompactWidgetProvider;
import me.calebjones.spacelaunchnow.widget.wordtimer.LaunchWordTimerWidgetProvider;
import timber.log.Timber;

public class NextLaunchTracker {

    private SwitchPreferences switchPreferences;
    private Context context;

public NextLaunchTracker(Context context) {
    Timber.d("NextLaunchTracker - onCreate");
    this.context = context;
    }


    public void runUpdate() {
        this.switchPreferences = SwitchPreferences.getInstance(context);

        if (switchPreferences.getCalendarStatus()) {
            syncCalendar();
        }

        updateWear();
        updateWidgets();
        SyncJob.schedulePeriodicJob(context);
    }

    public void updateWidgets() {
        LaunchCardCompactManager launchCardCompactManager = new LaunchCardCompactManager(context);
        LaunchWordTimerManager launchWordTimerManager = new LaunchWordTimerManager(context);
        LaunchListManager launchListManager = new LaunchListManager(context);

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
    }

    public void syncCalendar() {
        CalendarSyncManager calendarSyncManager = new CalendarSyncManager(context);
        calendarSyncManager.syncAllEevnts();
    }

    public void updateWear() {
        WearWatchfaceManager watchfaceManager = new WearWatchfaceManager(context);
        watchfaceManager.updateWear();
    }

}
