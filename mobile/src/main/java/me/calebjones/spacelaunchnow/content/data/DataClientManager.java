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

    public void getLaunchesByDate(final String startDate, final String endDate) {
        isLaunchByDate = true;
        Timber.i("Running getLaunchesByDate - %s %s", startDate, endDate);
        DataClient.getInstance().getLaunchesByDate(startDate, endDate, 0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
//                    int total = response.body().getTotal();
//                    int count = response.body().getCount();
//                    Timber.v("UpcomingLaunches Count: %s", count);
//                    dataSaver.saveLaunchesToRealmAsync(response.body().getLaunches());
//                    if (count < total) {
//                        getLaunchesByDate(startDate, endDate, count);
//                    } else {
//                        isLaunchByDate = false;
//                        ListPreferences.getInstance(context).isFresh(true);
//                        dataSaver.sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, true, call));
//                    }
                } else {
                    isLaunchByDate = false;
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isLaunchByDate = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getLaunchesByDate(final String startDate, final String endDate, final int offset) {
        ListPreferences.getInstance(context).isFresh(true);
        isLaunchByDate = true;
        Timber.i("Running getLaunchesByDate w/ offset - %s %s %s", startDate, endDate, offset);
        DataClient.getInstance().getLaunchesByDate(startDate, endDate, offset, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
//                    Timber.i("getLaunchesByDate - Successful - Total: %s Offset: %s Count: %s", total, offset, count);
                    List<Launch> launches = response.body().getLaunches();
                    dataSaver.saveLaunchesToRealmAsync(launches);
//                    if (count < total) {
//                        getLaunchesByDate(startDate, endDate, count);
//                    } else {
//                        isLaunchByDate = false;
//                        ListPreferences.getInstance(context).isFresh(true);
//                        dataSaver.sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, true, call));
//                    }
                } else {
                    isLaunchByDate = false;
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isLaunchByDate = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getNextUpcomingLaunches(NextLaunchDataRepository.NetworkCallback networkCallback) {
        isUpcomingLaunch = true;
        Timber.i("Running getNextUpcomingLaunches");
        DataClient.getInstance().getNextUpcomingLaunches(0, 0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
//                    int total = response.body().getTotal();
//                    int count = response.body().getCount();
//                    Timber.v("getNextUpcomingLaunches Count: %s", count);
//                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
//                    if (count < total) {
//                        getNextUpcomingLaunches(count, networkCallback);
//                    } else {
//                        isUpcomingLaunch = false;
//
//                        dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, true, call));
//
//                        nextLaunchTracker.runUpdate();
//                        networkCallback.onSuccess();
//                    }
                } else {
                    isUpcomingLaunch = false;
                    networkCallback.onNetworkFailure(response.code());
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));

                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isUpcomingLaunch = false;
                networkCallback.onFailure(t);
                dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, t.getLocalizedMessage()));
            }
        });
    }

    private void getNextUpcomingLaunches(final int offset, NextLaunchDataRepository.NetworkCallback networkCallback) {
        isUpcomingLaunch = true;
        Timber.i("Running getNextUpcomingLaunches - %s", offset);
        DataClient.getInstance().getNextUpcomingLaunches(10, offset, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LaunchResponse launchResponse = response.body();

                    Timber.v("UpcomingLaunches Count: %s", launchResponse.getCount());
                    dataSaver.saveLaunchesToRealm(launchResponse.getLaunches(), false);
                    if (launchResponse.getNext() != null) {
                        Uri uri = Uri.parse(launchResponse.getNext());
                        String server = uri.getAuthority();
                        String path = uri.getPath();
                        String protocol = uri.getScheme();
                        Set<String> args = uri.getQueryParameterNames();
//                        getNextUpcomingLaunches(count, networkCallback);
                    } else {
                        isUpcomingLaunch = false;
                        networkCallback.onSuccess();
                        dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, true, call));

                        nextLaunchTracker.runUpdate();
                    }
                } else {
                    isUpcomingLaunch = false;
                    networkCallback.onNetworkFailure(response.code());
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isUpcomingLaunch = false;
                networkCallback.onFailure(t);
                dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getUpcomingLaunches() {
        isUpcomingLaunchAll = true;
        Timber.i("Running getUpcomingLaunches");
        DataClient.getInstance().getUpcomingLaunches(0, 0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
//                    int total = response.body().getTotal();
//                    int count = response.body().getCount();
//                    Timber.v("UpcomingLaunches Count: %s", count);
//                    if (count < total) {
//                        getUpcomingLaunches(count);
//                    } else {
//                        isUpcomingLaunchAll = false;
//
//                        dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, true, call));
//
//                        nextLaunchTracker.runUpdate();
//                    }
                } else {
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isUpcomingLaunchAll = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, t.getLocalizedMessage()));

                nextLaunchTracker.runUpdate();

            }
        });
    }

    private void getUpcomingLaunches(final int offset) {
        isUpcomingLaunchAll = true;
        Timber.i("Running getUpcomingLaunches - %s", offset);
        DataClient.getInstance().getUpcomingLaunches(0, offset, new Callback<LaunchResponse>() {

            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
//                    int total = response.body().getTotal();
//                    int count = response.body().getCount() + offset;
//                    Timber.v("UpcomingLaunches Count: %s", count);
//                    if (count < total) {
//                        getUpcomingLaunches(count);
//                    } else {
//                        isUpcomingLaunchAll = false;
//
//                        dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, true, call));
//
//                        nextLaunchTracker.runUpdate();
//                    }
                } else {
                    isUpcomingLaunchAll = false;
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isUpcomingLaunchAll = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, t.getLocalizedMessage()));

                nextLaunchTracker.runUpdate();

            }
        });
    }

    public void getNextUpcomingLaunchesMini() {
        isNextLaunches = true;
        Timber.i("Running getNextUpcomingLaunchesMini");
        DataClient.getInstance().getNextUpcomingLaunchesMini(0, 0, new Callback<LaunchResponse>() {
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
