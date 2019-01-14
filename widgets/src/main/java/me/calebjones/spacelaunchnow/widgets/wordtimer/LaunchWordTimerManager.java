package me.calebjones.spacelaunchnow.widgets.wordtimer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.common.utils.UniqueIdentifier;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.widgets.R;
import me.calebjones.spacelaunchnow.common.prefs.SwitchPreferences;
import me.calebjones.spacelaunchnow.common.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.widgets.WidgetBroadcastReceiver;
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
        Timber.v("UpdateAppWidget %s", appWidgetId);
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
            RealmQuery<Launch> query = mRealm.where(Launch.class)
                    .greaterThanOrEqualTo("net", date);
            if (switchPreferences.getTBDSwitch()) {
                query.equalTo("status.id", 1);
            }
            launchRealms = query.findAll().sort("net", Sort.ASCENDING);
            Timber.v("loadLaunches - Realm query created.");
        } else {
            launchRealms = QueryBuilder.buildSwitchQuery(context, mRealm);
            Timber.v("loadLaunches - Filtered Realm query created.");
        }

        for (Launch launch : launchRealms) {
            if (launch.getNet() != null) {
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
        exploreIntent.setData(Uri.parse(exploreIntent.toUri(Intent.URI_INTENT_SCHEME)));
        exploreIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, UniqueIdentifier.getID(), exploreIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
        int red = Color.red(widgetBackgroundColor);
        int green = Color.green(widgetBackgroundColor);
        int blue = Color.blue(widgetBackgroundColor);
        remoteViews.setInt(R.id.bgcolor, "setColorFilter", Color.rgb(red,green,blue));
        remoteViews.setInt(R.id.bgcolor, "setAlpha", widgetAlpha);
        remoteViews.setTextColor(R.id.widget_launch_name, widgetTextColor);
        remoteViews.setTextColor(R.id.widget_mission_name, widgetSecondaryTextColor);
        remoteViews.setTextColor(R.id.countdown_days, widgetTextColor);
        remoteViews.setTextColor(R.id.countdown_days_label, widgetSecondaryTextColor);
        remoteViews.setTextColor(R.id.countdown_hours, widgetTextColor);
        remoteViews.setTextColor(R.id.countdown_hours_label, widgetSecondaryTextColor);
        remoteViews.setInt(R.id.widget_refresh_button, "setColorFilter", widgetIconColor);

        if (sharedPref.getBoolean("widget_refresh_enabled", false)) {
            remoteViews.setViewVisibility(R.id.widget_refresh_button, View.GONE);
        } else if (!sharedPref.getBoolean("widget_refresh_enabled", false)) {
            remoteViews.setViewVisibility(R.id.widget_refresh_button, View.VISIBLE);
        }
    }

    private void setLaunchName(Launch launchRealm) {
        String launchName = getLaunchName(launchRealm);

        if (launchName != null) {
            remoteViews.setTextViewText(R.id.widget_launch_name, launchName);
        } else {
            remoteViews.setTextViewText(R.id.widget_launch_name, "Unknown Launch");
        }
    }

    private String getLaunchName(Launch launchRealm) {
        //Replace with launch
        if (launchRealm.getRocket().getConfiguration() != null) {
            //Replace with mission name
            return launchRealm.getRocket().getConfiguration().getName();
        } else {
            return null;
        }
    }

    private String getMissionName(Launch launchRealm) {

        if (launchRealm.getMission() != null) {
            //Replace with mission name
            return launchRealm.getMission().getName();
        } else {
            return null;
        }
    }

    private long getFutureMilli(Launch launchRealm) {
        return getLaunchDate(launchRealm).getTimeInMillis();
    }

    private Calendar getLaunchDate(Launch launchRealm) {
        return Utils.DateToCalendar(launchRealm.getNet());
    }
}
