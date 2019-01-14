package me.calebjones.spacelaunchnow.widgets.launchcard;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.widgets.R;
import me.calebjones.spacelaunchnow.common.prefs.SwitchPreferences;
import me.calebjones.spacelaunchnow.common.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.common.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.common.utils.UniqueIdentifier;
import me.calebjones.spacelaunchnow.common.utils.Utils;
import me.calebjones.spacelaunchnow.widgets.WidgetBroadcastReceiver;
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
            RealmQuery<Launch> query = mRealm.where(Launch.class)
                    .greaterThanOrEqualTo("net", date);
            if (switchPreferences.getTBDSwitch()) {
                query.equalTo("status.id", 1);
            }
            launchRealms = query.sort("net", Sort.ASCENDING).findAll();
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

    public void updateAppWidget(int appWidgetId) {
        Timber.v("UpdateAppWidget %s", appWidgetId);
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        Timber.v("Size: [%s-%s] x [%s-%s]", minWidth, maxWidth, minHeight, maxHeight);

        Launch launch = getLaunch();

        if (minWidth <= 220 || minHeight <= 100) {
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
        exploreIntent.setData(Uri.parse(exploreIntent.toUri(Intent.URI_INTENT_SCHEME)));
        exploreIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, UniqueIdentifier.getID(), exploreIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.widget_compact_card_frame, actionPendingIntent);
    }

    private void setWidgetStyle() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        int colorWhite = 0xFFFFFFFF;
        int colorSecondaryWhite = 0xB3FFFFFF;
        int colorBackground = 0xFF303030;
        int widgetTextColor = sharedPref.getInt("widget_text_color",colorWhite);
        int widgetBackgroundColor = sharedPref.getInt("widget_background_color", colorBackground);
        int widgetSecondaryTextColor = sharedPref.getInt("widget_secondary_text_color",colorSecondaryWhite);
        int widgetIconColor = sharedPref.getInt("widget_icon_color", colorWhite);

        if(sharedPref.getBoolean("widget_theme_round_corner", true))
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
        remoteViews.setTextColor(R.id.widget_launch_mission, widgetTextColor);
        remoteViews.setTextColor(R.id.widget_launch_rocket, widgetSecondaryTextColor);
        remoteViews.setTextColor(R.id.widget_location, widgetSecondaryTextColor);
        remoteViews.setTextColor(R.id.widget_launch_date, widgetSecondaryTextColor);
        remoteViews.setInt(R.id.widget_categoryIcon, "setColorFilter", widgetIconColor);
        remoteViews.setInt(R.id.widget_compact_card_refresh_button, "setColorFilter", widgetIconColor);
        if (sharedPref.getBoolean("widget_refresh_enabled", false)) {
            remoteViews.setViewVisibility(R.id.widget_compact_card_refresh_button, View.GONE);
        } else if (!sharedPref.getBoolean("widget_refresh_enabled", false)) {
            remoteViews.setViewVisibility(R.id.widget_compact_card_refresh_button, View.VISIBLE);
        }
    }

    private void setLocationName(Launch launchRealm) {
        String locationName = null;

        if (launchRealm.getPad().getLocation() != null && launchRealm.getPad().getLocation().getName() != null) {
            locationName = launchRealm.getPad().getLocation().getName();
        }

        if (locationName != null) {
            remoteViews.setTextViewText(R.id.widget_location, locationName);
        } else {
            remoteViews.setTextViewText(R.id.widget_location, "Unknown Launch Location");
        }
    }

    private void setLaunchName(Launch launchRealm) {
        String title[];
        if (launchRealm.getName() != null) {
            title = launchRealm.getName().split("\\|");
            try {
                if (title.length > 0) {
                    remoteViews.setTextViewText(R.id.widget_launch_rocket, title[0].trim());
                    remoteViews.setTextViewText(R.id.widget_launch_mission, title[1].trim());
                } else {
                    remoteViews.setTextViewText(R.id.widget_launch_rocket, launchRealm.getName());
                    if (launchRealm.getMission() != null) {
                        remoteViews.setTextViewText(R.id.widget_launch_mission, launchRealm.getMission().getName());
                    }
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                remoteViews.setTextViewText(R.id.widget_launch_rocket, launchRealm.getName());
                if (launchRealm.getMission() != null) {
                    remoteViews.setTextViewText(R.id.widget_launch_mission, launchRealm.getMission().getName());
                }

            }
        }
    }

    private void setLaunchDate(Launch launch) {
        SimpleDateFormat sdf;
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("24_hour_mode", false)) {
            sdf = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy");
        } else {
            sdf = Utils.getSimpleDateFormatForUI("MMMM dd, yyyy");
        }
        sdf.toLocalizedPattern();
        if (launch.getNet() != null) {
            remoteViews.setTextViewText(R.id.widget_launch_date, sdf.format(launch.getNet()));
        } else {
            remoteViews.setTextViewText(R.id.widget_launch_date, "Unknown Launch Date");
        }
    }

    private void setCategoryIcon(Launch launch) {
        if (launch.getMission() != null && launch.getMission() != null) {
            Utils.setCategoryIcon(remoteViews, launch.getMission().getTypeName(), true, R.id.widget_categoryIcon);
        } else {
            remoteViews.setImageViewResource(R.id.widget_categoryIcon, R.drawable.ic_unknown_white);
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
