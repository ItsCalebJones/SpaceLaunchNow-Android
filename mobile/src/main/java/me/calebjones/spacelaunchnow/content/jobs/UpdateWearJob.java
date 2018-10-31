package me.calebjones.spacelaunchnow.content.jobs;

import android.content.Intent;
import androidx.annotation.NonNull;

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

    public static void scheduleJobNow() {
        Timber.v("Scheduling UpdateWearJob...");

        JobRequest.Builder builder = new JobRequest.Builder(UpdateWearJob.TAG)
                .startNow()
                .setUpdateCurrent(true);

        builder.build().schedule();
    }
}
