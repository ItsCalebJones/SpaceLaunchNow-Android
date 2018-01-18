package me.calebjones.spacelaunchnow.content.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.content.wear.WearWatchfaceManager;
import timber.log.Timber;


public class SyncWearJob extends Job {

    public static final String TAG = "SYNC_WEAR_JOB";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Timber.v("Run UpdateWearJob...");
        WearWatchfaceManager wearWatchfaceManager = new WearWatchfaceManager(getContext());
        wearWatchfaceManager.updateWear();
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        Timber.v("Scheduling UpdateWearJob...");

        JobRequest.Builder builder = new JobRequest.Builder(SyncWearJob.TAG)
                .setPeriodic(TimeUnit.HOURS.toMillis(1), TimeUnit.MINUTES.toMillis(30))
                .setUpdateCurrent(true);

        builder.build().schedule();
    }
}
