package me.calebjones.spacelaunchnow.common.content.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import timber.log.Timber;

public class DataJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case SyncCalendarJob.TAG:
                Timber.v(SyncCalendarJob.TAG);
                return new SyncCalendarJob();
            default:
                return null;
        }
    }


}
