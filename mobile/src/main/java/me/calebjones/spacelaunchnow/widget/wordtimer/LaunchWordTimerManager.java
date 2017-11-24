package me.calebjones.spacelaunchnow.widget.wordtimer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.widget.WidgetBroadcastReceiver;
import timber.log.Timber;


public class LaunchWordTimerManager {

    private Context context;
    private AppWidgetManager appWidgetManager;
    private RemoteViews remoteViews;
    private SwitchPreferences switchPreferences;

    public LaunchWordTimerManager(Context context){
        this.context = context;
        appWidgetManager = AppWidgetManager.getInstance(context);
    }

    public void updateAppWidget(int appWidgetId) {
        Timber.v("UpdateAppWidget");
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        Timber.v("Size: [%s-%s] x [%s-%s]", minWidth, maxWidth, minHeight, maxHeight);

        Launch launch = getLaunch(context);

        if (minWidth <= 200 || minHeight <= 100) {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_word_timer_small_dark
            );
        } else if (minWidth <= 320) {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_word_timer_dark
            );
        } else {
            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_launch_word_timer_large_dark
            );
        }


        if (launch != null) {
            setLaunchName(launch);
            setMissionName(launch);
            setRefreshIntent(launch);
            setWidgetStyle();
            setLaunchTimer(launch);
        } else {
            remoteViews.setTextViewText(R.id.widget_launch_name, "Unknown Launch");
            remoteViews.setTextViewText(R.id.widget_mission_name, "Unknown Mission");
        }
        Timber.v("Publishing widget update.");
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }

    private Launch getLaunch(Context context) {
        Date date = new Date();

        switchPreferences = SwitchPreferences.getInstance(context);

        Realm mRealm = Realm.getDefaultInstance();

        RealmResults<Launch> launchRealms;
        if (switchPreferences.getAllSwitch()) {
            launchRealms = mRealm.where(Launch.class)
                    .greaterThanOrEqualTo("net", date)
                    .findAllSorted("net", Sort.ASCENDING);
            Timber.v("loadLaunches - Realm query created.");
        } else {
            launchRealms = QueryBuilder.buildSwitchQuery(context, mRealm);
            Timber.v("loadLaunches - Filtered Realm query created.");
        }

        for (Launch launch : launchRealms) {
            if (launch.getNetstamp() != null && launch.getNetstamp() != 0) {
                return launch;
            }
        }
        return null;
    }

    private void setRefreshIntent(Launch launch) {
        Intent nextIntent = new Intent(context, WidgetBroadcastReceiver.class);
        PendingIntent refreshPending = PendingIntent.getBroadcast(context, 0, nextIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPending);

        Intent exploreIntent = new Intent(context, LaunchDetailActivity.class);
        exploreIntent.putExtra("TYPE", "launch");
        exploreIntent.putExtra("launchID", launch.getId());
        exploreIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, exploreIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_countdown_timer_frame, actionPendingIntent);
    }

    private void setMissionName(Launch launch) {
        String missionName = getMissionName(launch);

        if (missionName != null) {
            remoteViews.setTextViewText(R.id.widget_mission_name, missionName);
        } else {
            remoteViews.setTextViewText(R.id.widget_mission_name, "Unknown Mission");
        }
    }

    private void setLaunchTimer(Launch launch) {

        long millisUntilFinished = getFutureMilli(launch) - System.currentTimeMillis();

        // Calculate the Days/Hours/Mins/Seconds numerically.
        long longDays = millisUntilFinished / 86400000;
        long longHours = (millisUntilFinished / 3600000) % 24;

        // Update the views=
        remoteViews.setTextViewText(R.id.countdown_days, String.valueOf(longDays));

        remoteViews.setTextViewText(R.id.countdown_hours, String.valueOf(longHours));

    }

    private void setWidgetStyle() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int colorWhite = 0xFFFFFFFF;
        int colorSecondaryWhite = 0xB3FFFFFF;
        int colorBackground = 0xFF303030;
        boolean widgetRounderCorners = sharedPref.getBoolean("widget_theme_round_corner", true);
        int widgetTextColor = sharedPref.getInt("widget_text_color",colorWhite);
        int widgetBackgroundColor = sharedPref.getInt("widget_background_color", colorBackground);
        int widgetSecondaryTextColor = sharedPref.getInt("widget_secondary_text_color",colorSecondaryWhite);
        int widgetIconColor = sharedPref.getInt("widget_icon_color",colorWhite);
        if(widgetRounderCorners)
            remoteViews.setImageViewResource(R.id.bgcolor, R.drawable.rounded);
        else
            remoteViews.setImageViewResource(R.id.bgcolor, R.drawable.squared);

        Timber.v("Configuring widget");
        int widgetAlpha = Color.alpha(widgetBackgroundColor);
        remoteViews.setInt(R.id.bgcolor, "setColorFilter", widgetBackgroundColor);
        remoteViews.setInt(R.id.bgcolor, "setAlpha", widgetAlpha);
        remoteViews.setTextColor(R.id.widget_launch_name, widgetTextColor);
        remoteViews.setTextColor(R.id.widget_mission_name, widgetSecondaryTextColor);
        remoteViews.setTextColor(R.id.countdown_days, widgetSecondaryTextColor);
        remoteViews.setTextColor(R.id.countdown_days_label, widgetSecondaryTextColor);
        remoteViews.setTextColor(R.id.countdown_hours, widgetSecondaryTextColor);
        remoteViews.setTextColor(R.id.countdown_hours_label, widgetSecondaryTextColor);
        remoteViews.setInt(R.id.widget_refresh_button, "setColorFilter", widgetIconColor);

    }

    private void setLaunchName(Launch launchRealm) {
        String launchName = getLaunchName(launchRealm);

        if (launchName != null) {
            remoteViews.setTextViewText(R.id.widget_launch_name, launchName);
        } else {
            remoteViews.setTextViewText(R.id.widget_launch_name, "Unknown Launch");
        }
    }

    private void setLaunchDate(Launch launch) {
        SimpleDateFormat sdf;
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("24_hour_mode", false)) {
            sdf = new SimpleDateFormat("MMMM dd, yyyy");
        } else {
            sdf = new SimpleDateFormat("MMMM dd, yyyy");
        }
        sdf.toLocalizedPattern();
        if (launch.getNet() != null) {
            remoteViews.setTextViewText(R.id.widget_launch_date, sdf.format(launch.getNet()));
        } else {
            remoteViews.setTextViewText(R.id.widget_launch_date, "Unknown Launch Date");
        }
    }

    private String getLaunchName(Launch launchRealm) {
        //Replace with launch
        if (launchRealm.getRocket() != null && launchRealm.getRocket().getName() != null) {
            //Replace with mission name
            return launchRealm.getRocket().getName();
        } else {
            return null;
        }
    }

    private String getMissionName(Launch launchRealm) {

        if (launchRealm.getMissions().size() > 0) {
            //Replace with mission name
            return launchRealm.getMissions().get(0).getName();
        } else {
            return null;
        }
    }

    private long getFutureMilli(Launch launchRealm) {
        return getLaunchDate(launchRealm).getTimeInMillis();
    }

    private Calendar getLaunchDate(Launch launchRealm) {

        //Replace with launchData
        long longdate = launchRealm.getNetstamp();
        longdate = longdate * 1000;
        final Date date = new Date(longdate);
        return Utils.DateToCalendar(date);
    }
}
