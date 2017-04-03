package me.calebjones.spacelaunchnow.content.jobs;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.content.models.Constants;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import timber.log.Timber;

public class SyncJob extends Job {

    public static final String TAG = Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER + "_SYNC";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Timber.v("Running job ID: %s Tag: %s", params.getId(), params.getTag());
        if (LaunchDataService.getNextLaunches(getContext())) {
            return Result.SUCCESS;
        } else {
            return Result.RESCHEDULE;
        }
    }

    public static void schedulePeriodicJob(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("background_sync", true)) {
            Timber.v("Background sync enabled, configuring JobRequest.");


            JobRequest.Builder builder = new JobRequest.Builder(SyncJob.TAG)
                    .setUpdateCurrent(true)
                    .setPersisted(true);

            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("data_saver", false)) {
                Timber.v("DataSaver mode enabled...periodic set to once per day.");
                builder.setPeriodic(TimeUnit.DAYS.toMillis(1), 7200000);
            } else {
                Timber.v("DataSaver mode not enabled...every six hours.");
                builder.setPeriodic(TimeUnit.HOURS.toMillis(6), 7200000);
            }

            builder.build().schedule();
        }
    }
}
