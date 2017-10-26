package me.calebjones.spacelaunchnow.content.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;

import me.calebjones.spacelaunchnow.calendar.CalendarSyncService;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.jobs.SyncJob;
import me.calebjones.spacelaunchnow.widget.LaunchCardCompactWidgetProvider;
import me.calebjones.spacelaunchnow.widget.LaunchTimerWidgetProvider;
import me.calebjones.spacelaunchnow.widget.LaunchWordTimerWidgetProvider;
import timber.log.Timber;

public class NextLaunchTracker extends IntentService {

    private SwitchPreferences switchPreferences;

public NextLaunchTracker() {
        super("NextLaunchTracker");
    }

    public void onCreate() {
        super.onCreate();
        Timber.d("NextLaunchTracker - onCreate");
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("onHandleIntent - %s", intent.describeContents());
        this.switchPreferences = SwitchPreferences.getInstance(getApplicationContext());

        if (switchPreferences.getCalendarStatus()) {
            syncCalendar();
        }

        syncWear();
        updateWidgets();
        SyncJob.schedulePeriodicJob(this);
    }

    private void updateWidgets() {
        Intent cardIntent = new Intent(this, LaunchCardCompactWidgetProvider.class);
        cardIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int cardIds[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), LaunchCardCompactWidgetProvider.class));
        cardIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, cardIds);
        sendBroadcast(cardIntent);

        Intent timerIntent = new Intent(this, LaunchTimerWidgetProvider.class);
        timerIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int timerIds[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), LaunchTimerWidgetProvider.class));
        timerIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, timerIds);
        sendBroadcast(timerIntent);

        Intent wordIntent = new Intent(this, LaunchWordTimerWidgetProvider.class);
        wordIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int wordIds[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), LaunchWordTimerWidgetProvider.class));
        wordIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, wordIds);
        sendBroadcast(wordIntent);
    }

    private void syncCalendar() {
        CalendarSyncService.startActionSyncAll(this);
    }

    public void syncWear() {
        this.startService(new Intent(this, UpdateWearService.class));
    }

}
