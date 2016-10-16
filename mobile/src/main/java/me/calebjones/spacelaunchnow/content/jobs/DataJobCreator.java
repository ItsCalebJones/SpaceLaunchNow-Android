package me.calebjones.spacelaunchnow.content.jobs;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

import timber.log.Timber;

import static me.calebjones.spacelaunchnow.content.models.Strings.ACTION_CHECK_NEXT_LAUNCH_TIMER;
import static me.calebjones.spacelaunchnow.content.models.Strings.ACTION_UPDATE_BACKGROUND;

public class DataJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case ACTION_CHECK_NEXT_LAUNCH_TIMER:
                Timber.i(ACTION_CHECK_NEXT_LAUNCH_TIMER);
                return new NextLaunchJob();
            case ACTION_UPDATE_BACKGROUND:
                Timber.i(ACTION_UPDATE_BACKGROUND);
                return new UpdateJob();
            default:
                return null;
        }
    }
}
