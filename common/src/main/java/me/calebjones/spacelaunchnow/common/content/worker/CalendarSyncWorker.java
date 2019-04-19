package me.calebjones.spacelaunchnow.common.content.worker;

import android.content.Context;

import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import me.calebjones.spacelaunchnow.common.content.calendar.CalendarSyncManager;
import me.calebjones.spacelaunchnow.common.content.util.FilterBuilder;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.DataSaver;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class CalendarSyncWorker extends Worker {

    private Context context;
    private WorkerParameters parameters;

    public CalendarSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        parameters = params;
    }

    public static void scheduleWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest calendarSync =
                new PeriodicWorkRequest.Builder(CalendarSyncWorker.class, 3, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();
        WorkManager.getInstance()
                .enqueue(calendarSync);
    }

    public static void syncImmediately() {
        WorkManager.getInstance().cancelAllWorkByTag("syncCalendar");
        WorkManager.getInstance().enqueue(new OneTimeWorkRequest.Builder(CalendarSyncWorker.class)
                .setConstraints(new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build())
                .addTag("syncCalendar")
                .build());
    }

    @Override
    public Result doWork() {
        // Do the work here--in this case, upload the images.
        Timber.d("Running job ID: %s Tag: %s", parameters.getId(), parameters.getTags());
        if (Prefs.getBoolean("calendar_sync_state", false)) {
            DataSaver dataSaver = new DataSaver(context);
            String locationIds = FilterBuilder.getLocationIds(context);
            String lspIds = FilterBuilder.getLSPIds(context);
            int count = 10;
            Call<LaunchResponse> call = DataClient.getInstance().getNextUpcomingLaunchesSynchronous(count, 0, locationIds, lspIds);
            try {
                Response<LaunchResponse> response = call.execute();
                if (response.isSuccessful()) {
                    LaunchResponse launchResponse = response.body();
                    if (launchResponse != null) {
                        Timber.v("UpcomingLaunches Count: %s", launchResponse.getCount());
                        dataSaver.saveLaunchesToRealm(launchResponse.getLaunches(), false);
                        CalendarSyncManager calendarSyncManager = new CalendarSyncManager(context);
                        calendarSyncManager.resyncAllEvents();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return Result.failure();
            }
        }
        // Indicate whether the task finished successfully with the Result
        return Result.success();
    }
}
