package me.calebjones.spacelaunchnow.content.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import me.calebjones.spacelaunchnow.widget.launchcard.UpdateLaunchCardJob;
import me.calebjones.spacelaunchnow.widget.launchlist.UpdateLaunchListJob;
import me.calebjones.spacelaunchnow.widget.wordtimer.UpdateWordTimerJob;
import timber.log.Timber;

public class DataJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case UpdateJob.TAG:
                Timber.v(UpdateJob.TAG);
                return new UpdateJob();
            case UpdateWearJob.TAG:
                Timber.v(UpdateWearJob.TAG);
                return new UpdateWearJob();
            case SyncWearJob.TAG:
                Timber.v(SyncWearJob.TAG);
                return new SyncWearJob();
            case SyncJob.TAG:
                Timber.v(SyncJob.TAG);
                return new SyncJob();
            case UpdateLaunchCardJob.TAG:
                Timber.v(UpdateLaunchCardJob.TAG);
                return new UpdateLaunchCardJob();
            case UpdateWordTimerJob.TAG:
                Timber.v(UpdateWordTimerJob.TAG);
                return new UpdateWordTimerJob();
            case SyncWidgetJob.TAG:
                Timber.v(SyncWidgetJob.TAG);
                return new SyncWidgetJob();
            case LibraryDataJob.TAG:
                Timber.v(LibraryDataJob.TAG);
                return new LibraryDataJob();
            case UpdateLaunchListJob.TAG:
                Timber.v(UpdateLaunchListJob.TAG);
                return new UpdateLaunchListJob();
            default:
                return null;
        }
    }


}
