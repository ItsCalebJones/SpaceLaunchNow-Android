package me.calebjones.spacelaunchnow.content.jobs;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.util.Set;

import me.calebjones.spacelaunchnow.content.DataManager;
import me.calebjones.spacelaunchnow.data.models.Constants;
import timber.log.Timber;

public class NextLaunchJob extends Job {

    public static final String TAG = Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Timber.d("Running job ID: %s Tag: %s", params.getId(), params.getTag());
        DataManager dataManager = new DataManager(getContext());
        dataManager.getNextUpcomingLaunchesMini();
        int count = 0;
        while (dataManager.isRunning()) {
            try {
                count += 100;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Timber.e("ERROR - %s %s", TAG, e.getLocalizedMessage());
                Crashlytics.logException(e);
            }
        }
        Timber.i("%s complete...returning success.", TAG);
        return Result.SUCCESS;
    }

    @Override
    protected void onReschedule(int newJobId) {
        // the rescheduled job has a new ID
        super.onReschedule(newJobId);
        JobUtils.logJobRequest();
    }

    public static void scheduleIntervalJob(long interval, int launchId) {
        Timber.i("Searching JobRequests for %s", launchId);
         Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequestsForTag(NextLaunchJob.TAG);
        for (JobRequest jobRequest : jobRequests) {
            if (jobRequest.getExtras() != null) {
                if (launchId == jobRequest.getExtras().getInt("key", 0)) {
                    jobRequest.cancelAndEdit();
                    Timber.d("Found a match, cancelling.");
                }
            }
        }

        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putInt("key", launchId);

        if (interval <= 0){
            interval = 60000;
        }

        JobRequest.Builder builder = new JobRequest.Builder(NextLaunchJob.TAG)
                .setExtras(extras)
                .setExact(interval);

        Timber.i("Scheduling JobRequests for %s", TAG);
        builder.build().schedule();
        JobUtils.logJobRequest();
    }
}
