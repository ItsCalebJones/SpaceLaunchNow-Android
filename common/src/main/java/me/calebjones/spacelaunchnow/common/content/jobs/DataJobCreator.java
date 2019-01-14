package me.calebjones.spacelaunchnow.common.content.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import timber.log.Timber;

public class DataJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
//            case UpdateWearJob.TAG:
//                Timber.v(UpdateWearJob.TAG);
//                return new UpdateWearJob();
//            case SyncWearJob.TAG:
//                Timber.v(SyncWearJob.TAG);
//                return new SyncWearJob();
//            case SyncJob.TAG:
//                Timber.v(SyncJob.TAG);
//                return new SyncJob();
//            case UpdateLaunchCardJob.TAG:
//                Timber.v(UpdateLaunchCardJob.TAG);
//                return new UpdateLaunchCardJob();
//            case UpdateWordTimerJob.TAG:
//                Timber.v(UpdateWordTimerJob.TAG);
//                return new UpdateWordTimerJob();
//            case SyncWidgetJob.TAG:
//                Timber.v(SyncWidgetJob.TAG);
//                return new SyncWidgetJob();
//            case UpdateLaunchListJob.TAG:
//                Timber.v(UpdateLaunchListJob.TAG);
//                return new UpdateLaunchListJob();
//            case SyncCalendarJob.TAG:
//                Timber.v(SyncCalendarJob.TAG);
//                return new SyncCalendarJob();
            default:
                return null;
        }
    }


}
