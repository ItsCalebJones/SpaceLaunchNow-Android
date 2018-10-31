package me.calebjones.spacelaunchnow.content.jobs;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import me.calebjones.spacelaunchnow.calendar.CalendarSyncManager;
import me.calebjones.spacelaunchnow.content.data.DataClientManager;
import me.calebjones.spacelaunchnow.content.data.DataSaver;
import me.calebjones.spacelaunchnow.content.data.callbacks.Callbacks;
import me.calebjones.spacelaunchnow.content.util.FilterBuilder;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SyncCalendarJob extends Job {

    public static final String TAG = Constants.ACTION_CHECK_NEXT_LAUNCH_TIMER + "CALENDAR_SYNC_JOB";


    public static void scheduleImmediately() {
        new JobRequest.Builder(SyncCalendarJob.TAG)
                .startNow()
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    public static void scheduleDailyJob() {
        Timber.v("Scheduling UpdateWearJob...");

        JobRequest.Builder builder = new JobRequest.Builder(SyncCalendarJob.TAG)
                .setPeriodic(TimeUnit.DAYS.toMillis(1))
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setUpdateCurrent(true);

        builder.build().schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Timber.d("Running job ID: %s Tag: %s", params.getId(), params.getTag());
        if (Prefs.getBoolean("calendar_sync_state", false)) {
            Context context = getContext();
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
                        CalendarSyncManager calendarSyncManager = new CalendarSyncManager(getContext());
                        calendarSyncManager.resyncAllEvents();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Result.SUCCESS;
    }
}


