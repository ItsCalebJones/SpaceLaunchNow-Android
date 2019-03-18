package me.calebjones.spacelaunchnow.common.content.worker;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import me.calebjones.spacelaunchnow.common.content.wear.WearWatchfaceManager;

public class WearSyncWorker extends Worker {

    private Context context;
    private WorkerParameters parameters;

    public WearSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        parameters = params;
    }

    public static void schedulePeriodicWorker() {
        PeriodicWorkRequest calendarSync =
                new PeriodicWorkRequest.Builder(WearSyncWorker.class, 1, TimeUnit.HOURS)
                        .build();
        WorkManager.getInstance()
                .enqueue(calendarSync);
    }

    public static void syncImmediately() {
        WorkManager.getInstance().enqueue(new OneTimeWorkRequest.Builder(WearSyncWorker.class)
                .build());
    }


    @NonNull
    @Override
    public Result doWork() {
        WearWatchfaceManager wearWatchfaceManager = new WearWatchfaceManager(context);
        wearWatchfaceManager.updateWear();
        return Result.success();
    }
}
