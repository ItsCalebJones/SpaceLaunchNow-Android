package me.calebjones.spacelaunchnow.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.interfaces.QueryBuilder;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;


public class LaunchCardCompactWidgetProvider extends AppWidgetProvider {

    private int last_refresh_counter = 0;
    private Realm mRealm;
    public RemoteViews remoteViews;
    public SwitchPreferences switchPreferences;
    public static String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
    public static String ACTION_WIDGET_CLICK = "ActionReceiverClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.v("onUpdate");
        final int count = appWidgetIds.length;

        for (int widgetId : appWidgetIds) {
            Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);

            int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
            int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

            if (minWidth == 0 && maxWidth == 0 && minHeight == 0 && maxHeight == 0) {
                AppWidgetHost host = new AppWidgetHost(context, 0);
                host.deleteAppWidgetId(widgetId);
            }

            // Update The clock label using a shared method
            updateAppWidget(context, appWidgetManager, widgetId, options);
        }
        if (!mRealm.isClosed()) {
            mRealm.close();
        }
    }

    private LaunchRealm getLaunch(Context context) {
        Date date = new Date();

        switchPreferences = SwitchPreferences.getInstance(context);

        if (mRealm == null || mRealm.isClosed()) {
            // Create a new empty instance of Realm
            mRealm = Realm.getDefaultInstance();
        }

        RealmResults<LaunchRealm> launchRealms;
        if (switchPreferences.getAllSwitch()) {
            launchRealms = mRealm.where(LaunchRealm.class)
                    .greaterThanOrEqualTo("net", date)
                    .findAllSorted("net", Sort.ASCENDING);
            Timber.v("loadLaunches - Realm query created.");
        } else {
            launchRealms = QueryBuilder.buildSwitchQuery(context, mRealm);
            Timber.v("loadLaunches - Filtered Realm query created.");
        }

        for (LaunchRealm launch : launchRealms) {
            if (launch.getNetstamp() != 0) {
                return launch;
            }
        }
        return null;
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, Bundle options) {
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        Timber.v("Size: [%s-%s] x [%s-%s]", minWidth, maxWidth, minHeight, maxHeight);

        LaunchRealm launch = getLaunch(context);

        if (minWidth <= 200 || minHeight <= 100) {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_card_compact_small_dark);
        } else if (minWidth <= 320) {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_card_compact_dark);
        } else {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_card_compact_large_dark);
        }

        if (minWidth > 0 && maxWidth > 0 && minHeight > 0 && maxHeight > 0) {
            setLaunchName(context, launch, remoteViews, options);
            setLocationName(context, launch, remoteViews, options);
            setLaunchDate(context, launch, remoteViews);
            setCategoryIcon(context, launch, remoteViews);
            setRefreshIntent(context, launch, remoteViews);
            setWidgetStyle(context, remoteViews);

            pushWidgetUpdate(context, remoteViews);
        }
    }

    private void setRefreshIntent(Context context, LaunchRealm launch, RemoteViews remoteViews) {
        Intent refresh = new Intent(context, LaunchCardCompactWidgetProvider.class);
        refresh.setAction(ACTION_WIDGET_REFRESH);
        PendingIntent refreshPending = PendingIntent.getBroadcast(context, 0, refresh, 0);

        remoteViews.setOnClickPendingIntent(R.id.widget_compact_card_refresh_button, refreshPending);

        Intent exploreIntent = new Intent(context, LaunchDetailActivity.class);
        exploreIntent.putExtra("TYPE", "launch");
        exploreIntent.putExtra("launchID", launch.getId());
        exploreIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, exploreIntent, 0);

        remoteViews.setOnClickPendingIntent(R.id.widget_compact_card_frame, actionPendingIntent);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateAppWidget(context,appWidgetManager, appWidgetId, newOptions);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    public void setWidgetStyle(Context context, RemoteViews remoteViews) {
    }

    public void setLocationName(Context context, LaunchRealm launchRealm, RemoteViews remoteViews, Bundle options) {
        remoteViews.setTextViewText(R.id.widget_location, launchRealm.getLocation().getName());
    }

    public void setLaunchName(Context context, LaunchRealm launchRealm, RemoteViews remoteViews, Bundle options) {
        remoteViews.setTextViewText(R.id.widget_launch_rocket, getLaunchName(launchRealm));
    }

    private void setLaunchDate(Context context, LaunchRealm launch, RemoteViews remoteViews) {
        SimpleDateFormat sdf;
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("24_hour_mode", false)) {
            sdf = new SimpleDateFormat("MMMM dd, yyyy - kk:mm zzz");
        } else {
            sdf = new SimpleDateFormat("MMMM dd, yyyy - hh:mm a");
        }
        sdf.toLocalizedPattern();
        remoteViews.setTextViewText(R.id.widget_launch_date, sdf.format(launch.getNet()));
    }

    //TODO Light/Dark
    private void setCategoryIcon(Context context, LaunchRealm launch, RemoteViews remoteViews) {
        if (launch.getMissions() != null && launch.getMissions().size() > 0) {
            Utils.setCategoryIcon(remoteViews, launch.getMissions().get(0).getTypeName(), true, R.id.widget_categoryIcon);
        } else {
            remoteViews.setImageViewResource(R.id.widget_categoryIcon, R.drawable.ic_unknown_white);
        }
    }

    public String getLaunchName(LaunchRealm launchRealm) {
        //Replace with launch
        return launchRealm.getRocket().getName();
    }

    public String getMissionName(LaunchRealm launchRealm) {

        if (launchRealm.getMissions().size() > 0) {
            //Replace with mission name
            return launchRealm.getMissions().get(0).getName();
        } else {
            return "";
        }
    }

    public long getFutureMilli(LaunchRealm launchRealm) {
        return getLaunchDate(launchRealm).getTimeInMillis();
    }

    public Calendar getLaunchDate(LaunchRealm launchRealm) {

        //Replace with launchData
        long longdate = launchRealm.getNetstamp();
        longdate = longdate * 1000;
        final Date date = new Date(longdate);
        return Utils.DateToCalendar(date);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews){
        ComponentName myWidget = new ComponentName(context, LaunchCardCompactWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_WIDGET_REFRESH)) {
            Timber.v("onReceive", ACTION_WIDGET_REFRESH);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
            onUpdate(context,appWidgetManager,appWidgetIds);
        } else if (intent.getAction().equals(ACTION_WIDGET_CLICK)) {
            Timber.v("onReceive", ACTION_WIDGET_CLICK);
        } else {
            super.onReceive(context, intent);
        }
    }
}
