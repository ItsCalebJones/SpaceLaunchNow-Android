package me.calebjones.spacelaunchnow.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
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
import me.calebjones.spacelaunchnow.utils.Stopwatch;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;


public class LaunchCardCompactWidgetProvider extends AppWidgetProvider {

    private Realm mRealm;
    private LaunchRealm launchRealm;
    private int last_refresh_counter = 0;
    private Stopwatch stopwatch;
    public RemoteViews remoteViews;
    public SwitchPreferences switchPreferences;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.v("onUpdate");
        stopwatch = new Stopwatch();
        final int count = appWidgetIds.length;


        // If Launch is Null then go ahead and load the next launch. Otherwise check conditions before refreshing.
        if (launchRealm != null){
            if (launchRealm.getNet().before(new Date()) || last_refresh_counter > 3600) {
                launchRealm = getLaunch(context);
                last_refresh_counter = 0;
            } else {
                last_refresh_counter = last_refresh_counter ++;
            }
        } else {
            Timber.v("launchRealm is null - getting launch.");
            launchRealm = getLaunch(context);
        }
        Timber.v("Got launch %s", stopwatch.getElapsedTimeString());

        for (int widgetId : appWidgetIds) {
            Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);

            // Update The clock label using a shared method
            updateAppWidget(context, appWidgetManager, widgetId, options, launchRealm);
        }
        Timber.v("Finishing up %s", stopwatch.getElapsedTimeString());
        if (!mRealm.isClosed()) {
            mRealm.close();
        }
        Timber.v("Realm Closed %s", stopwatch.getElapsedTimeString());
        stopwatch.reset();
    }

    private LaunchRealm getLaunch(Context context) {
        Date date = new Date();

        Timber.v("getLaunch %s", stopwatch.getElapsedTimeString());

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
        Timber.v("realm returned %s", stopwatch.getElapsedTimeString());

        for (LaunchRealm launch : launchRealms) {
            if (launch.getNetstamp() != 0) {
                return launch;
            }
        }
        return null;
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int widgetId, Bundle options, LaunchRealm launch) {
        remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_launch_card_compact_dark);
        Timber.v("layout %s", stopwatch.getElapsedTimeString());

        setLaunchName(context, launch, remoteViews, options);
        Timber.v("setLaunchName %s", stopwatch.getElapsedTimeString());

        setLocationName(context, launch, remoteViews, options);
        Timber.v("setLocationName %s", stopwatch.getElapsedTimeString());

        setLaunchDate(context, launch, remoteViews);
        Timber.v("setLaunchDate %s", stopwatch.getElapsedTimeString());

        setCategoryIcon(context, launch, remoteViews);
        Timber.v("setCategoryIcon %s", stopwatch.getElapsedTimeString());

        setWidgetStyle(context, remoteViews);
        Timber.v("setWidgetStyle %s", stopwatch.getElapsedTimeString());

        pushWidgetUpdate(context, remoteViews);
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

    private void setCategoryIcon(Context context, LaunchRealm launch, RemoteViews remoteViews) {
        Utils.setCategoryIcon(remoteViews, launch.getMissions().get(0).getTypeName(), true, R.id.widget_categoryIcon);
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
}
