package me.calebjones.spacelaunchnow.content.jobs;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.content.data.DataRepositoryManager;
import me.calebjones.spacelaunchnow.data.models.Constants;
import timber.log.Timber;

public class UpdateJob extends Job {

    public static final String TAG = Constants.ACTION_UPDATE_BACKGROUND;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Timber.d("Running job ID: %s Tag: %s", params.getId(), params.getTag());
        DataRepositoryManager dataRepositoryManager = new DataRepositoryManager(getContext());
        dataRepositoryManager.syncBackground();
        int count = 0;
        while (dataRepositoryManager.getDataClientManager().isRunning()) {
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

    @Override
    protected void onReschedule(int newJobId) {
        // the rescheduled job has a new ID
        super.onReschedule(newJobId);
        JobUtils.logJobRequest();
    }

    public static void scheduleJob(Context context) {
        JobRequest.Builder builder = new JobRequest.Builder(UpdateJob.TAG)
                .setUpdateCurrent(true);


        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("data_saver", false)) {
            builder.setPeriodic(TimeUnit.DAYS.toMillis(7));
            Timber.v("DataSaver enabled - Scheduling periodic at seven days.");
        } else {
            builder.setPeriodic(TimeUnit.DAYS.toMillis(1));
            Timber.v("DataSaver not enabled - Scheduling periodic at default every day.");
        }

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("wifi_only", false)) {
            builder.setRequiredNetworkType(JobRequest.NetworkType.UNMETERED);
            Timber.v("WiFi Only enabled");
        } else {
            builder.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED);
            Timber.v("WiFi Only not-enabled");
        }

        Timber.i("Scheduling JobRequests for %s", TAG);
        builder.build().schedule();
        JobUtils.logJobRequest();
    }
}
