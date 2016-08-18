package me.calebjones.spacelaunchnow.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
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


public class LaunchTimerWidgetProvider extends AppWidgetProvider {

    private Realm mRealm;
    private CountDownTimer countDownTimer;
    private int last_refresh_counter = 0;
    public RemoteViews remoteViews;
    public SwitchPreferences switchPreferences;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.v("onUpdate");
        final int count = appWidgetIds.length;

        // If Launch is Null then go ahead and load the next launch. Otherwise check conditions before refreshing.
        for (int widgetId : appWidgetIds) {
            Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);

            // Update The clock label using a shared method
            updateAppWidget(context, appWidgetManager, widgetId, options);
        }
        if (!mRealm.isClosed()) {
            mRealm.close();
        }
    }

    @Override
    public void onEnabled(Context context) {
        Timber.v("Widget placed, starting service...");
    }


    @Override
    public void onDisabled(Context context) {
        Timber.v("Widget(s) removed, stopping service...");
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

        LaunchRealm launch = getLaunch(context);

        Timber.v("Size: [%s-%s] x [%s-%s]", minWidth, maxWidth, minHeight, maxHeight);

        if (maxWidth <= 200 || maxHeight <= 100) {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_timer_compact_dark);
        } else if (maxWidth <= 420 || maxHeight <= 175) {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_timer_small_dark);
        } else if (maxWidth <= 320 || maxHeight <= 175) {

        } else {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_timer_large_dark);
        }

        setLaunchName(context, launch, remoteViews, options);
        setMissionName(context, launch, remoteViews, options);
        setLaunchTimer(context, launch, remoteViews, appWidgetManager, widgetId, options);

        setWidgetStyle(context, remoteViews);

        pushWidgetUpdate(context, remoteViews);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        updateAppWidget(context,appWidgetManager, appWidgetId, newOptions);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
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

    public void setLaunchTimer(Context context, LaunchRealm launchRealm, final RemoteViews remoteViews, final AppWidgetManager appWidgetManager, final int widgetId, final Bundle options) {

        countDownTimer = new CountDownTimer(getFutureMilli(launchRealm) - System.currentTimeMillis(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
                int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
                int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
                int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

                Bundle newOptions = appWidgetManager.getAppWidgetOptions(widgetId);

                int newMinWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
                int newMaxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
                int newMinHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
                int newMaxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

                if (minWidth != newMinWidth || maxWidth != newMaxWidth || minHeight != newMinHeight || maxHeight != newMaxHeight) {
                    this.cancel();
                }

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

            @Override
            public void onFinish() {
                remoteViews.setTextViewText(R.id.countdown_days, "- -");
                remoteViews.setTextViewText(R.id.countdown_hours, "- -");
                remoteViews.setTextViewText(R.id.countdown_seconds, "- -");
                appWidgetManager.updateAppWidget(widgetId, remoteViews);
                this.cancel();
            }
        }.start();
    }

    public void setMissionName(Context context, LaunchRealm launchRealm, RemoteViews remoteViews, Bundle options) {
        remoteViews.setTextViewText(R.id.widget_mission_name, getMissionName(launchRealm));
    }

    public void setLaunchName(Context context, LaunchRealm launchRealm, RemoteViews remoteViews, Bundle options) {
        remoteViews.setTextViewText(R.id.widget_launch_name, getLaunchName(launchRealm));
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, LaunchTimerWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }
}
