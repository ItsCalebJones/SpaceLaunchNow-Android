package me.calebjones.spacelaunchnow.content.jobs;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.content.services.UpdateWearService;
import timber.log.Timber;


public class UpdateWearJob extends Job {

    public static final String TAG = "UPDATE_WEAR_JOB";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        getContext().startService(new Intent(getContext(), UpdateWearService.class));
        return Result.SUCCESS;
    }

    @Override
    protected void onReschedule(int newJobId) {
        // the rescheduled job has a new ID
    }

    public static void scheduleJob() {
        Timber.v("Scheduling UpdateWearJob...");

        JobRequest.Builder builder = new JobRequest.Builder(UpdateWearJob.TAG)
                .setPeriodic(TimeUnit.HOURS.toMillis(12), TimeUnit.HOURS.toMillis(1))
                .setUpdateCurrent(true);

        builder.build().schedule();
    }
}
