package me.calebjones.spacelaunchnow.content.jobs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import me.calebjones.spacelaunchnow.content.models.Strings;


public class NextLaunchJob extends Job {

    public static final String TAG = Strings.ACTION_CHECK_NEXT_LAUNCH_TIMER;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        return null;
    }

    @Override
    protected void onReschedule(int newJobId) {
        // the rescheduled job has a new ID
    }

    public static void scheduleJob(long interval, Context context) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        long windowStart;
        long windowEnd;
        long intervalSeconds = interval / 1000;
        long intervalMinutes = intervalSeconds / 60;

        if (intervalMinutes > 30){
            windowStart = intervalSeconds - 600;
            windowEnd = intervalSeconds + 600;
        } else {
            windowStart = intervalSeconds - 60;
            windowEnd = intervalSeconds;
        }

        JobRequest.Builder builder = new JobRequest.Builder(NextLaunchJob.TAG)
                .setExecutionWindow(windowStart, windowEnd)
                .setUpdateCurrent(false);

        if (sharedPref.getBoolean("wifi_only", false)){
            builder.setRequiredNetworkType(JobRequest.NetworkType.UNMETERED);
        } else {
            builder.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED);
        }

        builder.build().schedule();
    }
}
