package me.calebjones.spacelaunchnow.widget.launchcard;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.widget.WidgetBroadcastReceiver;
import timber.log.Timber;

/**
 * Created by Caleb on 11/21/2017.
 */

public class LaunchCardCompactManager {

    private Context context;
    private AppWidgetManager appWidgetManager;
    private RemoteViews remoteViews;
    private SwitchPreferences switchPreferences;

    public LaunchCardCompactManager(Context context){
        this.context = context;
        appWidgetManager = AppWidgetManager.getInstance(context);
    }

    private Launch getLaunch() {
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

    public void updateAppWidget(int appWidgetId) {
        Timber.v("UpdateAppWidget");
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        Timber.v("Size: [%s-%s] x [%s-%s]", minWidth, maxWidth, minHeight, maxHeight);

        Launch launch = getLaunch();

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

        if (launch != null) {
            setLaunchName(launch);
            setLocationName(launch);
            setLaunchDate(launch);
            setCategoryIcon(launch);
            setRefreshIntent(launch);
            setWidgetStyle();
        } else {
            remoteViews.setTextViewText(R.id.widget_launch_name, "Unknown Launch");
            remoteViews.setTextViewText(R.id.widget_mission_name, "Unknown Mission");
        }
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }

    private void setRefreshIntent(Launch launch) {
        Intent nextIntent = new Intent(context, WidgetBroadcastReceiver.class);
        PendingIntent refreshPending = PendingIntent.getBroadcast(context, 0, nextIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_compact_card_refresh_button, refreshPending);

        Intent exploreIntent = new Intent(context, LaunchDetailActivity.class);
        exploreIntent.putExtra("TYPE", "launch");
        exploreIntent.putExtra("launchID", launch.getId());
        exploreIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, exploreIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_compact_card_frame, actionPendingIntent);
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
        remoteViews.setTextColor(R.id.widget_launch_rocket, widgetTextColor);
        remoteViews.setTextColor(R.id.widget_location, widgetSecondaryTextColor);
        remoteViews.setTextColor(R.id.widget_launch_date, widgetSecondaryTextColor);
        remoteViews.setInt(R.id.widget_categoryIcon, "setColorFilter", widgetIconColor);
        remoteViews.setInt(R.id.widget_compact_card_refresh_button, "setColorFilter", widgetIconColor);
    }

    private void setLocationName(Launch launchRealm) {
        String locationName = null;

        if (launchRealm.getLocation() != null && launchRealm.getLocation().getName() != null) {
            locationName = launchRealm.getLocation().getName();
        }

        if (locationName != null) {
            remoteViews.setTextViewText(R.id.widget_location, locationName);
        } else {
            remoteViews.setTextViewText(R.id.widget_location, "Unknown Launch Location");
        }
    }

    private void setLaunchName(Launch launchRealm) {
        String launchName = getLaunchName(launchRealm);

        if (launchName != null) {
            remoteViews.setTextViewText(R.id.widget_launch_rocket, launchName);
        } else {
            remoteViews.setTextViewText(R.id.widget_launch_rocket, "Unknown Launch");
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

    private void setCategoryIcon(Launch launch) {
        if (launch.getMissions() != null && launch.getMissions().size() > 0) {
            Utils.setCategoryIcon(remoteViews, launch.getMissions().get(0).getTypeName(), true, R.id.widget_categoryIcon);
        } else {
            remoteViews.setImageViewResource(R.id.widget_categoryIcon, R.drawable.ic_unknown_white);
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
