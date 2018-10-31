package me.calebjones.spacelaunchnow.widget.launchlist;

import android.content.Context;
import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.data.models.Constants;

public class UpdateLaunchListJob extends Job {

    public static final String TAG = Constants.ACTION_UPDATE_LAUNCH_LIST;
    public SwitchPreferences switchPreferences;
    private Context context;

    @NonNull
    @Override
    protected Job.Result onRunJob(Job.Params params) {
        context = getContext();
        PersistableBundleCompat extras = params.getExtras();
        int appWidgetId = extras.getInt("appWidgetId", 0);
        LaunchListManager launchListManager = new LaunchListManager(context);
        launchListManager.updateAppWidget(appWidgetId);
        return Job.Result.SUCCESS;
    }

    public static void runJobImmediately(int id) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putInt("appWidgetId", id);
        new JobRequest.Builder(UpdateLaunchListJob.TAG)
                .addExtras(extras)
                .startNow()
                .build()
                .schedule();
    }

}
