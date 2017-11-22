package me.calebjones.spacelaunchnow.widget.wordtimer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.ui.launchdetail.activity.LaunchDetailActivity;
import me.calebjones.spacelaunchnow.widget.WidgetBroadcastReceiver;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.widget.launchcard.LaunchCardCompactManager;
import timber.log.Timber;


public class UpdateWordTimerJob extends Job {

    public static final String TAG = Constants.ACTION_UPDATE_WORD_TIMER;
    public SwitchPreferences switchPreferences;
    private Context context;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        context = getContext();
        PersistableBundleCompat extras = params.getExtras();
        int appWidgetId = extras.getInt("appWidgetId", 0);
        LaunchWordTimerManager launchWordTimerManager = new LaunchWordTimerManager(context);
        launchWordTimerManager.updateAppWidget(appWidgetId);
        return Result.SUCCESS;
    }

    public static void runJobImmediately(int id) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putInt("appWidgetId", id);
        new JobRequest.Builder(UpdateWordTimerJob.TAG)
                .addExtras(extras)
                .startNow()
                .build()
                .schedule();
    }

}
