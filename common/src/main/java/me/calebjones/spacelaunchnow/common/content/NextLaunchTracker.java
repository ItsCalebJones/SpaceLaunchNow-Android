package me.calebjones.spacelaunchnow.common.content;

import android.content.Context;

import me.calebjones.spacelaunchnow.common.prefs.SwitchPreferences;
import me.calebjones.spacelaunchnow.common.content.jobs.SyncJob;
import timber.log.Timber;

public class NextLaunchTracker {

    private SwitchPreferences switchPreferences;
    private Context context;

// TODO
public NextLaunchTracker(Context context) {
    Timber.d("NextLaunchTracker - onCreate");
    this.context = context;
    }


    public void runUpdate() {
        this.switchPreferences = SwitchPreferences.getInstance(context);

//        if (switchPreferences.getCalendarStatus()) {
//            syncCalendar();
//        }
//
//        updateWear();
//        updateWidgets();
        SyncJob.schedulePeriodicJob(context);
    }

//    public void updateWidgets() {
//        LaunchCardCompactManager launchCardCompactManager = new LaunchCardCompactManager(context);
//        LaunchWordTimerManager launchWordTimerManager = new LaunchWordTimerManager(context);
//        LaunchListManager launchListManager = new LaunchListManager(context);
//
//        int cardIds[] = AppWidgetManager.getInstance(context)
//                .getAppWidgetIds(new ComponentName(context,
//                        LaunchCardCompactWidgetProvider.class));
//
//        for (int id : cardIds){
//            launchCardCompactManager.updateAppWidget(id);
//        }
//
//        int timerIds[] = AppWidgetManager.getInstance(context)
//                .getAppWidgetIds(new ComponentName(context,
//                        LaunchWordTimerWidgetProvider.class));
//
//        for (int id : timerIds){
//            launchWordTimerManager.updateAppWidget(id);
//        }
//
//        int listIds[] = AppWidgetManager.getInstance(context)
//                .getAppWidgetIds(new ComponentName(context,
//                        LaunchListWidgetProvider.class));
//
//        for (int id : listIds){
//            launchListManager.updateAppWidget(id);
//        }
//    }
//
//    public void syncCalendar() {
//        CalendarSyncManager calendarSyncManager = new CalendarSyncManager(context);
//        calendarSyncManager.syncAllEevnts();
//    }
//
//    public void updateWear() {
//        final WearWatchfaceManager watchfaceManager = new WearWatchfaceManager(context);
//        new Thread(() -> watchfaceManager.updateWear()).start();
//    }
}
