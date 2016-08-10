package me.calebjones.spacelaunchnow.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.interfaces.QueryBuilder;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;


public class CountDownWidgetProvider extends AppWidgetProvider {

    private Realm mRealm;
    private RealmResults<LaunchRealm> launchRealms;
    public RemoteViews remoteViews;
    public SwitchPreferences switchPreferences;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int widgetId : appWidgetIds) {
            // Update The clock label using a shared method
            updateAppWidget(context, appWidgetManager, widgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        context.startService(new Intent(context,  CountDownWidgetService.class));
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        context.stopService(new Intent(context, CountDownWidgetService.class));
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        //Get Switch config
        switchPreferences = SwitchPreferences.getInstance(context);

        // Create a new empty instance of Realm
        mRealm = Realm.getDefaultInstance();

        Date date = new Date();

        //TODO get widget settings and apply correct layout
        remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.countdown_widget_dark);

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
                setLaunchName(context, launch, remoteViews);
                setMissionName(context, launch, remoteViews);
                setLaunchTimer(context, launch, remoteViews, appWidgetManager, widgetId);
                break;
            }
        }

        setWidgetStyle(context, remoteViews);
        setUpdateIntent(context, remoteViews, widgetId);

        appWidgetManager.updateAppWidget(widgetId, remoteViews);

        mRealm.close();
    }

    private void setUpdateIntent(Context context, RemoteViews remoteViews, int widgetId) {
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

    public void setWidgetStyle(Context context, RemoteViews remoteViews) {
    }

    public void setLaunchTimer(Context context, LaunchRealm launchRealm, final RemoteViews remoteViews, final AppWidgetManager appWidgetManager, final int widgetId) {
        long millisUntilFinished = getFutureMilli(launchRealm) - System.currentTimeMillis();

        // Calculate the Days/Hours/Mins/Seconds numerically.
        long longDays = millisUntilFinished / 86400000;
        long longHours = (millisUntilFinished / 3600000) % 24;
        long longMinutes = (millisUntilFinished / 60000) % 60;
        long longSeconds = (millisUntilFinished / 1000) % 60;

        String days = String.valueOf(longDays);
        String hours;
        String minutes;
        String seconds;

        // Translate those numerical values to string values.
        if (longHours < 10) {
            hours = "0" + String.valueOf(longHours);
        } else {
            hours = String.valueOf(longHours);
        }

        if (longMinutes < 10) {
            minutes = "0" + String.valueOf(longMinutes);
        } else {
            minutes = String.valueOf(longMinutes);
        }

        if (longSeconds < 10) {
            seconds = "0" + String.valueOf(longSeconds);
        } else {
            seconds = String.valueOf(longSeconds);
        }


        // Update the views
        if (Integer.valueOf(days) > 99) {
            remoteViews.setTextViewText(R.id.countdown_days, "99+");
        } else if (Integer.valueOf(days) > 0) {
            remoteViews.setTextViewText(R.id.countdown_days, days);
        } else {
            remoteViews.setTextViewText(R.id.countdown_days, "- -");
        }

        if (Integer.valueOf(hours) > 0) {
            remoteViews.setTextViewText(R.id.countdown_hours, hours);
        } else if (Integer.valueOf(days) > 0) {
            remoteViews.setTextViewText(R.id.countdown_hours, "00");
        } else {
            remoteViews.setTextViewText(R.id.countdown_hours, "- -");
        }

        if (Integer.valueOf(minutes) > 0) {
            remoteViews.setTextViewText(R.id.countdown_minutes, minutes);
        } else if (Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
            remoteViews.setTextViewText(R.id.countdown_minutes, "00");
        } else {
            remoteViews.setTextViewText(R.id.countdown_minutes, "- -");
        }

        if (Integer.valueOf(seconds) > 0) {
            remoteViews.setTextViewText(R.id.countdown_seconds, seconds);
        } else if (Integer.valueOf(minutes) > 0 || Integer.valueOf(hours) > 0 || Integer.valueOf(days) > 0) {
            remoteViews.setTextViewText(R.id.countdown_seconds, "60");
        } else {
            remoteViews.setTextViewText(R.id.countdown_seconds, "- -");
        }
        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }

    public void setMissionName(Context context, LaunchRealm launchRealm, RemoteViews remoteViews) {
        remoteViews.setTextViewText(R.id.widget_mission_name, getMissionName(launchRealm));
    }

    public void setLaunchName(Context context, LaunchRealm launchRealm, RemoteViews remoteViews) {
        remoteViews.setTextViewText(R.id.widget_launch_name, getLaunchName(launchRealm));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Timber.d("Clock update: %s", intent.getAction());
        // Get the widget manager and ids for this widget provider, then call the shared
        // clock update method.
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int appWidgetID : ids) {
            updateAppWidget(context, appWidgetManager, appWidgetID);
        }
    }
}
