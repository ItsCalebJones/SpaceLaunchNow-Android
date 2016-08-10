package me.calebjones.spacelaunchnow.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import timber.log.Timber;

public class CountDownWidgetService extends Service {

    public boolean enabled;
    private RealmResults<LaunchRealm> launchRealms;
    private Realm mRealm;
    private ScheduledFuture<?> scheduledFuture;

    public CountDownWidgetService() {
        this.enabled = false;
    }

    public void updateWidget() {
        Timber.v("updateWidget - called");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, CountDownWidgetProvider.class));

        if (appWidgetIds.length > 0) {
            Context baseContext = getBaseContext();

            // Update The clock label using a shared method
            Intent intent = new Intent(baseContext, CountDownWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            Timber.v("updateWidget - sending broadcast.");
            baseContext.sendBroadcast(intent);
        } else {
            Timber.v("Stopping service.");
            stopSelf();
        }
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {
        mRealm = Realm.getDefaultInstance();
        this.enabled = true;
    }

    public void onDestroy() {
        mRealm.close();
        this.enabled = false;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        Executors.newSingleThreadScheduledExecutor().schedule(new CountdownWidgetCallback(this), 1, TimeUnit.SECONDS);
        return 1;
    }
}
