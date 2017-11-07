package me.calebjones.spacelaunchnow.content.services;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import me.calebjones.spacelaunchnow.calendar.CalendarSyncManager;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.jobs.SyncJob;
import me.calebjones.spacelaunchnow.content.wear.WearWatchfaceManager;
import me.calebjones.spacelaunchnow.widget.LaunchCardCompactWidgetProvider;
import me.calebjones.spacelaunchnow.widget.LaunchTimerWidgetProvider;
import me.calebjones.spacelaunchnow.widget.LaunchWordTimerWidgetProvider;
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

        scheduleWear();
        updateWidgets();
        SyncJob.schedulePeriodicJob(context);
    }

    private void updateWidgets() {
        Intent cardIntent = new Intent(context, LaunchCardCompactWidgetProvider.class);
        cardIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int cardIds[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LaunchCardCompactWidgetProvider.class));
        cardIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, cardIds);
        context.sendBroadcast(cardIntent);

        Intent timerIntent = new Intent(context, LaunchTimerWidgetProvider.class);
        timerIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int timerIds[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LaunchTimerWidgetProvider.class));
        timerIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, timerIds);
        context.sendBroadcast(timerIntent);

        Intent wordIntent = new Intent(context, LaunchWordTimerWidgetProvider.class);
        wordIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int wordIds[] = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, LaunchWordTimerWidgetProvider.class));
        wordIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, wordIds);
        context.sendBroadcast(wordIntent);
    }

    private void syncCalendar() {
        CalendarSyncManager calendarSyncManager = new CalendarSyncManager(context);
        calendarSyncManager.syncAllEevnts();
    }

    public void scheduleWear() {
        WearWatchfaceManager watchfaceManager = new WearWatchfaceManager(context);
        watchfaceManager.updateWear();
    }

}
