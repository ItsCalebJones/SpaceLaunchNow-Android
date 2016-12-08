package me.calebjones.spacelaunchnow.content.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import timber.log.Timber;

public class DataJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case NextLaunchJob.TAG:
                Timber.i(NextLaunchJob.TAG);
                return new NextLaunchJob();
            case UpdateJob.TAG:
                Timber.i(UpdateJob.TAG);
                return new UpdateJob();
            case UpdateWearJob.TAG:
                Timber.i(UpdateWearJob.TAG);
                return new UpdateWearJob();
            default:
                return null;
        }
    }
}
