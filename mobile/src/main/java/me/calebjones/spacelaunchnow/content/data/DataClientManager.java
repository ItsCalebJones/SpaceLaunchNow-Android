package me.calebjones.spacelaunchnow.content.data;

import android.content.Context;
import android.net.Uri;

import java.util.List;
import java.util.Set;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.content.data.next.NextLaunchDataRepository;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.services.NextLaunchTracker;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * This class is responsible for async loading of data via the DataClient and sending it to DataSaver to be saved.
 */

public class DataClientManager {

    private Context context;
    private DataSaver dataSaver;
    private NextLaunchTracker nextLaunchTracker;

    private boolean isLaunchByDate = false;
    private boolean isUpcomingLaunch = false;
    private boolean isUpcomingLaunchAll = false;
    private boolean isNextLaunches = false;
    private boolean isLaunchById = false;
    private boolean isAllAgencies = false;
    private boolean isAllMissions = false;
    private boolean isAllLocations = false;
    private boolean isAllPads = false;
    private boolean isVehicles = false;
    private boolean isRockets = false;
    private boolean isRocketFamily = false;
    private boolean isSaving = false;
    private boolean isSyncing = false;
    private Realm realm;

    public DataClientManager(Context context) {
        this.context = context;
        this.dataSaver = new DataSaver(context);
        nextLaunchTracker = new NextLaunchTracker(context);
    }

    public DataSaver getDataSaver() {
        return dataSaver;
    }

    public boolean isRunning() {
        if (isLaunchByDate || isUpcomingLaunch || isUpcomingLaunchAll || isNextLaunches || isLaunchById || isAllAgencies
                || isAllMissions || isAllLocations || isAllPads || isVehicles || isRockets || isRocketFamily || isSaving
                || isSyncing || dataSaver.isSaving || dataSaver.isSyncing) {
            return true;
        } else {
            return false;
        }
    }

    public void getNextUpcomingLaunchesMini() {
        isNextLaunches = true;
        Timber.i("Running getNextUpcomingLaunchesMini");
        DataClient.getInstance().getNextUpcomingLaunchesMini(20, 0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {

                if (response.isSuccessful()) {
                    isNextLaunches = false;
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), true);

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_MINI, true, call));

                    nextLaunchTracker.runUpdate();
                } else {
                    isNextLaunches = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_MINI, false, call, ErrorUtil.parseLibraryError(response)));

                    nextLaunchTracker.runUpdate();
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isNextLaunches = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_MINI, false, call, t.getLocalizedMessage()));

                nextLaunchTracker.runUpdate();
            }
        });
    }

}
