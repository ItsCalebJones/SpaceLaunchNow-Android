package me.calebjones.spacelaunchnow.widget.launchcard;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import me.calebjones.spacelaunchnow.content.jobs.UpdateLaunchCardJob;
import timber.log.Timber;


public class LaunchCardCompactWidgetProvider extends AppWidgetProvider {

    public static String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
    public static String ACTION_WIDGET_CLICK = "ActionReceiverClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.v("onUpdate");
        for (int widgetId : appWidgetIds) {
            UpdateLaunchCardJob.runJobImmediately(widgetId);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_WIDGET_REFRESH)) {
            Timber.v("onReceive %s", ACTION_WIDGET_REFRESH);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
            onUpdate(context, appWidgetManager, appWidgetIds);
        } else if (intent.getAction().equals(ACTION_WIDGET_CLICK)) {
            Timber.v("onReceive %s", ACTION_WIDGET_CLICK);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        UpdateLaunchCardJob.runJobImmediately(appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }
}
