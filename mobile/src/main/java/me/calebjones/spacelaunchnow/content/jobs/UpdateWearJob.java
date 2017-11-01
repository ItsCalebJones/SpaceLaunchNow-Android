package me.calebjones.spacelaunchnow.content.jobs;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.content.wear.WearWatchfaceManager;
import timber.log.Timber;


public class UpdateWearJob extends Job {

    public static final String TAG = "UPDATE_WEAR_JOB";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
         WearWatchfaceManager wearWatchfaceManager = new WearWatchfaceManager(getContext());
         wearWatchfaceManager.updateWear();
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        Timber.v("Scheduling UpdateWearJob...");

        JobRequest.Builder builder = new JobRequest.Builder(UpdateWearJob.TAG)
                .setPeriodic(TimeUnit.HOURS.toMillis(12), TimeUnit.HOURS.toMillis(1))
                .setPersisted(true)
                .setUpdateCurrent(true);

        builder.build().schedule();
    }
}
