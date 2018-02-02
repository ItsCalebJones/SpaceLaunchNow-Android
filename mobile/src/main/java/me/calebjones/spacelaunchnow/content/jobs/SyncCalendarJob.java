package me.calebjones.spacelaunchnow.content.jobs;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import me.calebjones.spacelaunchnow.calendar.CalendarSyncManager;
import me.calebjones.spacelaunchnow.content.data.DataClientManager;
import me.calebjones.spacelaunchnow.data.models.Constants;
import timber.log.Timber;

public class SyncCalendarJob extends Job {

    public static final String TAG = Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER + "CALENDAR_SYNC_JOB";


    public static void scheduleImmediately() {
        new JobRequest.Builder(SyncCalendarJob.TAG)
                .startNow()
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Timber.d("Running job ID: %s Tag: %s", params.getId(), params.getTag());
        CalendarSyncManager calendarSyncManager = new CalendarSyncManager(getContext());
        calendarSyncManager.resyncAllEvents();
        return Result.SUCCESS;
    }
}

