package me.calebjones.spacelaunchnow.wear.content.job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by Caleb on 1/17/2018.
 */

public class WearJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case NextLaunchJob.TAG:
                return new NextLaunchJob();
            default:
                return null;
        }
    }
}
