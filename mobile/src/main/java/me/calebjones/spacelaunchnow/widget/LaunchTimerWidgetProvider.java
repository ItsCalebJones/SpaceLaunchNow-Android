package me.calebjones.spacelaunchnow.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.models.Constants;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;


public class LaunchTimerWidgetProvider extends AppWidgetProvider {

    private Realm mRealm;
    private int last_refresh_counter = 0;
    private static CountDownTimer countDownTimer;
    private static boolean invalid = false;
    public RemoteViews remoteViews;
    public SwitchPreferences switchPreferences;
    public static String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
    public static String ACTION_WIDGET_CLICK = "ActionReceiverClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.v("onUpdate");
        if (!ListPreferences.getInstance(context).getFirstBoot()) {
            final int count = appWidgetIds.length;

            // If Launch is Null then go ahead and load the next launch. Otherwise check conditions before refreshing.
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
        } else {
            setRefreshIntentInitial(context, new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_timer_dark));
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

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int widgetId,
                                Bundle options) {
        invalid = true;
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        LaunchRealm launch = getLaunch(context);

        Timber.v("Size: [%s-%s] x [%s-%s]", minWidth, maxWidth, minHeight, maxHeight);

        if (minWidth <= 200 || minHeight <= 100) {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_timer_small_dark);
        } else if (minWidth <= 320) {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_timer_dark);
        } else {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_timer_large_dark);
        }


        if (minWidth > 0 && maxWidth > 0 && minHeight > 0 && maxHeight > 0) {
            if (launch != null) {
                setLaunchName(context, launch, remoteViews, options);
                setMissionName(context, launch, remoteViews, options);
                setRefreshIntent(context, launch, remoteViews);
                setWidgetStyle(context, remoteViews);
                setLaunchTimer(context, launch, remoteViews, appWidgetManager, widgetId, options);
            } else {
                remoteViews.setTextViewText(R.id.widget_launch_name, "Unknown Launch");
                remoteViews.setTextViewText(R.id.widget_mission_name, "Unknown Mission");
            }
            pushWidgetUpdate(context, remoteViews);
        }
    }

    private void setRefreshIntent(Context context, LaunchRealm launch, RemoteViews remoteViews) {
        Intent nextIntent = new Intent(Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER);
        PendingIntent refreshPending = PendingIntent.getBroadcast(context, 0, nextIntent, 0);

        remoteViews.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPending);

        Intent exploreIntent = new Intent(context, LaunchDetailActivity.class);
        exploreIntent.putExtra("TYPE", "launch");
        exploreIntent.putExtra("launchID", launch.getId());
        exploreIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, exploreIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_countdown_timer_frame, actionPendingIntent);
    }

    private void setRefreshIntentInitial(Context context, RemoteViews remoteViews) {
        Intent nextIntent = new Intent(Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER);
        PendingIntent refreshPending = PendingIntent.getBroadcast(context, 0, nextIntent, 0);

        remoteViews.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPending);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        if (!ListPreferences.getInstance(context).getFirstBoot()) {
            updateAppWidget(context, appWidgetManager, appWidgetId, newOptions);
        } else {
            setRefreshIntentInitial(context, new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_timer_dark));
        }
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ListPreferences.getInstance(context).getFirstBoot()) {
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
        } else {
            setRefreshIntentInitial(context, new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_word_timer_dark));
        }
    }

    public String getLaunchName(LaunchRealm launchRealm) {
        //Replace with launch
        if (launchRealm.getRocket() != null && launchRealm.getRocket().getName() != null) {
            //Replace with mission name
            return launchRealm.getRocket().getName();
        } else {
            return null;
        }
    }

    public String getMissionName(LaunchRealm launchRealm) {

        if (launchRealm.getMissions().size() > 0) {
            //Replace with mission name
            return launchRealm.getMissions().get(0).getName();
        } else {
            return null;
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

        long millisUntilFinished = getFutureMilli(launchRealm) - System.currentTimeMillis();

        if (countDownTimer != null) {
            Timber.v("Cancelling countdown timer.");
            countDownTimer.cancel();
        }
        invalid = false;
        countDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
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

                if (minWidth != newMinWidth || maxWidth != newMaxWidth || minHeight != newMinHeight || maxHeight != newMaxHeight || invalid) {
                    Timber.v("Cancelling countdown timer - onClick - invalid = %s", invalid);
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
        String missionName = getMissionName(launchRealm);

        if (missionName != null) {
            remoteViews.setTextViewText(R.id.widget_mission_name, missionName);
        } else {
            remoteViews.setTextViewText(R.id.widget_mission_name, "Unknown Mission");
        }
    }

    public void setLaunchName(Context context, LaunchRealm launchRealm, RemoteViews remoteViews, Bundle options) {
        String launchName = getLaunchName(launchRealm);

        if (launchName != null) {
            remoteViews.setTextViewText(R.id.widget_launch_name, launchName);
        } else {
            remoteViews.setTextViewText(R.id.widget_launch_name, "Unknown Launch");
        }
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, LaunchTimerWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }
}