package me.calebjones.spacelaunchnow.widgets;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.calebjones.spacelaunchnow.widgets.launchcard.UpdateLaunchCardJob;
import me.calebjones.spacelaunchnow.widgets.launchlist.UpdateLaunchListJob;
import me.calebjones.spacelaunchnow.widgets.wordtimer.UpdateWordTimerJob;
import timber.log.Timber;

public class WidgetJobCreator implements JobCreator {

    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case UpdateLaunchCardJob.TAG:
                Timber.v(UpdateLaunchCardJob.TAG);
                return new UpdateLaunchCardJob();
            case UpdateWordTimerJob.TAG:
                Timber.v(UpdateWordTimerJob.TAG);
                return new UpdateWordTimerJob();
            case SyncWidgetJob.TAG:
                Timber.v(SyncWidgetJob.TAG);
                return new SyncWidgetJob();
            case UpdateLaunchListJob.TAG:
                Timber.v(UpdateLaunchListJob.TAG);
                return new UpdateLaunchListJob();
            default:
                return null;
        }
    }
}
