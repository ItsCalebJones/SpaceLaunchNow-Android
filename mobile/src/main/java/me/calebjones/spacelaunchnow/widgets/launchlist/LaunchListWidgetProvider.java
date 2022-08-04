package me.calebjones.spacelaunchnow.widgets.launchlist;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import me.calebjones.spacelaunchnow.common.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.widgets.WidgetBroadcastReceiver;
import timber.log.Timber;


public class LaunchListWidgetProvider extends AppWidgetProvider {

    public static String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
    public static String ACTION_WIDGET_CLICK = "ActionReceiverClick";
    public static final String DETAIL_ACTION = "me.calebjones.spacelaunchnow.LaunchListWidgetProvider.DETAIL_ACTION";
    public static final String LAUNCH_ID = "me.calebjones.spacelaunchnow.LaunchListWidgetProvider.LAUNCH_ID";


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Intent nextIntent = new Intent(context, WidgetBroadcastReceiver.class);
        nextIntent.putExtra("updateUIOnly", true);
        context.sendBroadcast(nextIntent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.v("onUpdate");
        LaunchListManager launchListManager = new LaunchListManager(context);
        for (int widgetId : appWidgetIds) {
            launchListManager.updateAppWidget(widgetId);
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
        } else if (intent.getAction().equals(DETAIL_ACTION)) {

            String launchId = intent.getStringExtra(LAUNCH_ID);

            Intent exploreIntent = new Intent(context, LaunchDetailActivity.class);
            exploreIntent.putExtra("TYPE", "launch");
            exploreIntent.putExtra("launchID", launchId);
            exploreIntent.setData(Uri.parse(exploreIntent.toUri(Intent.URI_INTENT_SCHEME)));
            exploreIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(exploreIntent);

        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        LaunchListManager launchListManager = new LaunchListManager(context);
        launchListManager.updateAppWidget(appWidgetId);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

}
