package me.calebjones.spacelaunchnow.content.jobs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import me.calebjones.spacelaunchnow.content.models.Strings;


public class UpdateJob extends Job {

    public static final String TAG = Strings.ACTION_UPDATE_BACKGROUND;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        return null;
    }

    @Override
    protected void onReschedule(int newJobId) {
        // the rescheduled job has a new ID
    }

    public static void scheduleJob(Context context) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        //Get sync period.
        boolean dataSaver = sharedPref.getBoolean("data_saver", false);

        long interval;

        if (dataSaver){
            interval = 168 * 60 * 60;
        } else {
            interval = 24 * 60 * 60;
        }

        JobRequest.Builder builder = new JobRequest.Builder(UpdateJob.TAG)
                .setPeriodic(interval)
                .setUpdateCurrent(true);

        if (sharedPref.getBoolean("wifi_only", false)){
            builder.setRequiredNetworkType(JobRequest.NetworkType.UNMETERED);
        } else {
            builder.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED);
        }

        builder.build().schedule();
    }
}
