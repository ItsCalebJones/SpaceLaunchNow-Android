package me.calebjones.spacelaunchnow.content.jobs;

import android.content.Context;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.content.data.DataClientManager;
import me.calebjones.spacelaunchnow.data.models.Constants;
import timber.log.Timber;

public class SyncWidgetJob extends Job {

    public static final String TAG = Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER + "_SYNC_WIDGET";


    public static void scheduleImmediately() {
        new JobRequest.Builder(SyncWidgetJob.TAG)
                .startNow()
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Timber.d("Running job ID: %s Tag: %s", params.getId(), params.getTag());
        DataClientManager dataClientManager = new DataClientManager(getContext());
        dataClientManager.getNextUpcomingLaunchesMini();

        int count = 0;
        while (dataClientManager.isRunning()) {
            try {
                count += 100;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Timber.e("ERROR - %s %s", TAG, e.getLocalizedMessage());
                Crashlytics.logException(e);
            }
        }
        Timber.i("%s complete...returning success after %s milliseconds.", TAG, count);
        return Result.SUCCESS;
    }
}

