package me.calebjones.spacelaunchnow.content.jobs;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.content.services.LibraryDataManager;
import timber.log.Timber;


public class LibraryDataJob extends Job {

    public static final String TAG = "LIBRARY_DATA_JOB";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        LibraryDataManager libraryDataManager = new LibraryDataManager(getContext());
        libraryDataManager.getAllLibraryData();
        int count = 0;
        while (libraryDataManager.isRunning() && count < 60000) {
            try {
                count += 100;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Timber.e("ERROR - %s %s", TAG, e.getLocalizedMessage());
                Crashlytics.logException(e);
                return Result.FAILURE;
            }
        }
        Timber.i("%s complete...returning success after %s milliseconds.", TAG, count);
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        Timber.v("Scheduling UpdateWearJob...");

        JobRequest.Builder builder = new JobRequest.Builder(LibraryDataJob.TAG)
                .setPeriodic(TimeUnit.DAYS.toMillis(30))
                .setUpdateCurrent(true);

        builder.build().schedule();
    }
}
