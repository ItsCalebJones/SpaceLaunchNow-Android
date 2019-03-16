package me.calebjones.spacelaunchnow.wear.content.job;

import androidx.annotation.NonNull;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import java.util.concurrent.TimeUnit;

public class NextLaunchJob extends Job {

    public static final String TAG = "NEXT_LAUNCH_JOB";

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        return null;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(NextLaunchJob.TAG)
                .setPeriodic(TimeUnit.HOURS.toMillis(1), TimeUnit.MINUTES.toMillis(15))
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }
}
