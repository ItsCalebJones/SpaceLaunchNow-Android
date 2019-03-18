package me.calebjones.spacelaunchnow.common.content.worker;

import android.content.Context;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.realm.Realm;
import me.calebjones.spacelaunchnow.data.models.LaunchNotification;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class WidgetSyncWorker extends Worker {

    private Context context;
    private WorkerParameters parameters;

    public WidgetSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        parameters = params;
    }


    public static void immediateOnetimeWorker() {
        WorkManager.getInstance().enqueue(new OneTimeWorkRequest.Builder(WidgetSyncWorker.class)
                .build());
    }

    private static boolean isLaunchTimeChanged(Launch previous, Launch item) {
        if ((Math.abs(previous.getNet().getTime() - item.getNet().getTime()) >= 360)) {
            return true;
        } else if (previous.getStatus() != null && item.getStatus() != null && previous.getStatus().getId().intValue() != item.getStatus().getId().intValue()) {
            return true;
        }
        return false;
    }

    @NonNull
    @Override
    public Result doWork() {
        Timber.d("Running job ID: %s Tag: %s", parameters.getId(), parameters.getTags());
        Call<LaunchResponse> call = DataClient.getInstance().getNextUpcomingLaunchesForWidgets(20, 0);

        try {
            Response<LaunchResponse> response = call.execute();
            if (response.isSuccessful()) {
                List<Launch> launches = response.body().getLaunches();
                Realm mRealm = Realm.getDefaultInstance();
                mRealm.executeTransaction(mRealm1 -> {
                    Date now = Calendar.getInstance().getTime();
                    for (final Launch item : launches) {
                        final Launch previous = mRealm1.where(Launch.class)
                                .equalTo("id", item.getId())
                                .findFirst();
                        if (previous != null) {
                            if (isLaunchTimeChanged(previous, item)) {
                                final LaunchNotification notification = mRealm1.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                                if (notification != null) {

                                    notification.resetNotifiers();
                                    mRealm1.copyToRealmOrUpdate(notification);
                                }
                            }
                            item.setLastUpdate(now);
                            item.setEventID(previous.getEventID());
                        }
                        Timber.v("Saving item: %s", item.getName());
                        mRealm1.copyToRealmOrUpdate(item);
                    }
                    mRealm1.copyToRealmOrUpdate(launches);
                });
                mRealm.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success();
    }
}
