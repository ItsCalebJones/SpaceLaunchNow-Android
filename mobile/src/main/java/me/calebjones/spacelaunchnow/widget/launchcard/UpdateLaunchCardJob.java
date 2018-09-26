package me.calebjones.spacelaunchnow.widget.launchcard;

import android.content.Context;
import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.data.models.Constants;


public class UpdateLaunchCardJob extends Job {

    public static final String TAG = Constants.ACTION_UPDATE_LAUNCH_CARD;
    public SwitchPreferences switchPreferences;
    private Context context;
    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        context = getContext();
        PersistableBundleCompat extras = params.getExtras();
        int appWidgetId = extras.getInt("appWidgetId", 0);
        LaunchCardCompactManager launchCardCompactManager = new LaunchCardCompactManager(context);
        launchCardCompactManager.updateAppWidget(appWidgetId);
        return Result.SUCCESS;
    }

    public static void runJobImmediately(int id) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putInt("appWidgetId", id);
        new JobRequest.Builder(UpdateLaunchCardJob.TAG)
                .addExtras(extras)
                .startNow()
                .build()
                .schedule();
    }
}
