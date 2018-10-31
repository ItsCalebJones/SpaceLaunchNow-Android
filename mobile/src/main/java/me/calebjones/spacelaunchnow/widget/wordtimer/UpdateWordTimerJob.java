package me.calebjones.spacelaunchnow.widget.wordtimer;

import android.content.Context;
import androidx.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.data.models.Constants;


public class UpdateWordTimerJob extends Job {

    public static final String TAG = Constants.ACTION_UPDATE_WORD_TIMER;
    public SwitchPreferences switchPreferences;
    private Context context;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        context = getContext();
        PersistableBundleCompat extras = params.getExtras();
        int appWidgetId = extras.getInt("appWidgetId", 0);
        LaunchWordTimerManager launchWordTimerManager = new LaunchWordTimerManager(context);
        launchWordTimerManager.updateAppWidget(appWidgetId);
        return Result.SUCCESS;
    }

    public static void runJobImmediately(int id) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putInt("appWidgetId", id);
        new JobRequest.Builder(UpdateWordTimerJob.TAG)
                .addExtras(extras)
                .startNow()
                .build()
                .schedule();
    }

}
