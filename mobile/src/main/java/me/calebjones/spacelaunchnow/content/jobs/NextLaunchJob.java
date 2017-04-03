package me.calebjones.spacelaunchnow.content.jobs;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.util.Set;

import me.calebjones.spacelaunchnow.content.models.Constants;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import timber.log.Timber;

public class NextLaunchJob extends Job {

    public static final String TAG = Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER;

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

    @Override
    protected void onReschedule(int newJobId) {
        // the rescheduled job has a new ID
    }

    public static void scheduleIntervalJob(long interval, int launchId) {
        Timber.i("Searching JobRequests for %s", launchId);
        Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequestsForTag(NextLaunchJob.TAG);
        for (JobRequest jobRequest : jobRequests) {
            if (launchId == jobRequest.getExtras().getInt("key", 0)) {
                jobRequest.cancelAndEdit();
                Timber.d("Found a match, cancelling.");
            }
        }

        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putInt("key", launchId);

        JobRequest.Builder builder = new JobRequest.Builder(NextLaunchJob.TAG)
                .setExtras(extras)
                .setPersisted(true)
                .setExact(interval);

        builder.build().schedule();
    }
}
