package me.calebjones.spacelaunchnow.content.jobs;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.calebjones.spacelaunchnow.content.data.DataClientManager;
import me.calebjones.spacelaunchnow.data.models.Constants;
import timber.log.Timber;

public class SyncJob extends Job {

    public static final String TAG = Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER + "_SYNC";

    public static void schedulePeriodicJob(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("background_sync", true)) {
            Timber.i("Background sync enabled, configuring JobRequest.");

            JobRequest.Builder builder = new JobRequest.Builder(SyncJob.TAG)
                    .setUpdateCurrent(true);

            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("data_saver", false)) {
                Timber.v("DataSaver mode enabled...periodic set to once per week.");
                builder.setPeriodic(TimeUnit.DAYS.toMillis(7), 7200000);
            } else {
                Timber.v("DataSaver mode not enabled...every day.");
                builder.setPeriodic(TimeUnit.HOURS.toMillis(24), 7200000);
            }

            Timber.i("Scheduling JobRequests for %s", TAG);
            builder.build().schedule();
            JobUtils.logJobRequest();
        }
    }

    public static void scheduleImmediately() {
        new JobRequest.Builder(SyncJob.TAG)
                .startNow()
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Timber.d("Running job ID: %s Tag: %s", params.getId(), params.getTag());
        DataClientManager dataClientManager = new DataClientManager(getContext());
        dataClientManager.getNextUpcomingLaunchesMini();

        int count = 0;
        while (dataClientManager.isRunning()) {
            try {
                count += 100;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Timber.e("ERROR - %s %s", TAG, e.getLocalizedMessage());
                Crashlytics.logException(e);
            }
        }
        RealmConfiguration configuration = Realm.getDefaultConfiguration();
        if (configuration != null){
            Realm.compactRealm(configuration);
        }
        Timber.i("%s complete...returning success after %s milliseconds.", TAG, count);
        return Result.SUCCESS;
    }
}

