package me.calebjones.spacelaunchnow.content.jobs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
                .setPeriodic(TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(2))
                .setUpdateCurrent(true);

        builder.build().schedule();
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Timber.d("Running job ID: %s Tag: %s", params.getId(), params.getTag());
        if (Prefs.getBoolean("calendar_sync_state", false)) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            Context context = getContext();
            new Thread() {
                @Override
                public void run() {
                    DataSaver dataSaver = new DataSaver(context);
                    String locationIds = FilterBuilder.getLocationIds(context);
                    String lspIds = FilterBuilder.getLSPIds(context);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    int count = Integer.parseInt(sharedPref.getString("calendar_count", "5"));
                    // do async operation here
                    DataClient.getInstance().getNextUpcomingLaunches(count,0, locationIds, lspIds,  new Callback<LaunchResponse>() {
                        @Override
                        public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                            if (response.isSuccessful()) {
                                LaunchResponse launchResponse = response.body();

                                Timber.v("UpcomingLaunches Count: %s", launchResponse.getCount());
                                dataSaver.saveLaunchesToRealm(launchResponse.getLaunches(), false);
                            }
                            CalendarSyncManager calendarSyncManager = new CalendarSyncManager(getContext());
                            calendarSyncManager.resyncAllEvents();
                            countDownLatch.countDown();
                        }

                        @Override
                        public void onFailure(Call<LaunchResponse> call, Throwable t) {
                            CalendarSyncManager calendarSyncManager = new CalendarSyncManager(getContext());
                            calendarSyncManager.resyncAllEvents();
                            countDownLatch.countDown();
                        }
                    });
                }
            }.start();

            try {
                countDownLatch.await();
            } catch (InterruptedException ignored) {

            }
        }
        return Result.SUCCESS;
    }
}

