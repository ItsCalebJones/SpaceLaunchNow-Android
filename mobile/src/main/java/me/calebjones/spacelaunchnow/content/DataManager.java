package me.calebjones.spacelaunchnow.content;

import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;

import java.util.Collections;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.services.NextLaunchTracker;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.data.models.LaunchNotification;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.UpdateRecord;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.AgencyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LocationResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.MissionResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.PadResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.RocketFamilyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.RocketResponse;
import me.calebjones.spacelaunchnow.utils.Analytics;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * This class is responsible for async loading of data via the DataClient and saving it to Realm for future use.
 */

public class DataManager {

    private Context context;

    public DataRepositoryManager dataRepositoryManager;

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

    public DataManager(Context context) {
        this.context = context;
        this.dataRepositoryManager = new DataRepositoryManager(context, this);
    }

    public DataRepositoryManager getDataRepositoryManager() {
        return dataRepositoryManager;
    }

    public boolean isRunning() {
        if (isLaunchByDate || isUpcomingLaunch || isUpcomingLaunchAll || isNextLaunches || isLaunchById || isAllAgencies
                || isAllMissions || isAllLocations || isAllPads || isVehicles || isRockets || isRocketFamily || isSaving
                || isSyncing) {
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
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    saveLaunchesToRealm(response.body().getLaunches(), false);
                    if (count < total) {
                        getLaunchesByDate(startDate, endDate, count);
                    } else {
                        isLaunchByDate = false;
                        ListPreferences.getInstance(context).isFresh(true);

                        sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, true, call));
                    }
                } else {
                    isLaunchByDate = false;
                    sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isLaunchByDate = false;

                sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, false, call, t.getLocalizedMessage()));
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
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.i("getLaunchesByDate - Successful - Total: %s Offset: %s Count: %s", total, offset, count);
                    saveLaunchesToRealm(response.body().getLaunches(), false);
                    if (count < total) {
                        getLaunchesByDate(startDate, endDate, count);
                    } else {
                        isLaunchByDate = false;
                        ListPreferences.getInstance(context).isFresh(true);
                        sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, true, call));
                    }
                } else {
                    isLaunchByDate = false;
                    sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isLaunchByDate = false;

                sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getNextUpcomingLaunches() {
        isUpcomingLaunch = true;
        Timber.i("Running getNextUpcomingLaunches");
        DataClient.getInstance().getNextUpcomingLaunches(0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("getNextUpcomingLaunches Count: %s", count);
                    saveLaunchesToRealm(response.body().getLaunches(), false);
                    if (count < total) {
                        getNextUpcomingLaunches(count);
                    } else {
                        isUpcomingLaunch = false;

                        sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, true, call));

                        context.startService(new Intent(context, NextLaunchTracker.class));
                    }
                } else {
                    isUpcomingLaunch = false;

                    sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));

                    context.startService(new Intent(context, NextLaunchTracker.class));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isUpcomingLaunch = false;

                sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, t.getLocalizedMessage()));

                context.startService(new Intent(context, NextLaunchTracker.class));
            }
        });
    }

    private void getNextUpcomingLaunches(int offset) {
        isUpcomingLaunch = true;
        Timber.i("Running getNextUpcomingLaunches - %s", offset);
        DataClient.getInstance().getNextUpcomingLaunches(offset, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    saveLaunchesToRealm(response.body().getLaunches(), false);
                    if (count < total) {
                        getNextUpcomingLaunches(count);
                    } else {
                        isUpcomingLaunch = false;

                        sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, true, call));
                    }
                } else {
                    sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                //TODO handle errors
                isUpcomingLaunch = false;

                sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, t.getLocalizedMessage()));

                context.startService(new Intent(context, NextLaunchTracker.class));
            }
        });
    }

    public void getUpcomingLaunchesAll() {
        isUpcomingLaunchAll = true;
        Timber.i("Running getUpcomingLaunchesAll");
        DataClient.getInstance().getUpcomingLaunchesAll(0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    saveLaunchesToRealm(response.body().getLaunches(), false);
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    if (count < total) {
                        getUpcomingLaunchesAll(count);
                    } else {
                        isUpcomingLaunchAll = false;

                        sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, true, call));

                        context.startService(new Intent(context, NextLaunchTracker.class));
                    }
                } else {
                    sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isUpcomingLaunchAll = false;

                sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, t.getLocalizedMessage()));

                context.startService(new Intent(context, NextLaunchTracker.class));

            }
        });
    }

    private void getUpcomingLaunchesAll(final int offset) {
        isUpcomingLaunchAll = true;
        Timber.i("Running getUpcomingLaunchesAll - %s", offset);
        DataClient.getInstance().getUpcomingLaunchesAll(offset, new Callback<LaunchResponse>() {

            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    saveLaunchesToRealm(response.body().getLaunches(), false);
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.v("UpcomingLaunches Count: %s", count);
                    if (count < total) {
                        getUpcomingLaunchesAll(count);
                    } else {
                        isUpcomingLaunchAll = false;

                        sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, true, call));

                        context.startService(new Intent(context, NextLaunchTracker.class));
                    }
                } else {
                    sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isUpcomingLaunchAll = false;

                sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, t.getLocalizedMessage()));

                context.startService(new Intent(context, NextLaunchTracker.class));

            }
        });
    }

    public void getNextUpcomingLaunchesMini() {
        isNextLaunches = true;
        Timber.i("Running getNextUpcomingLaunchesMini");
        DataClient.getInstance().getNextUpcomingLaunchesMini(new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {

                if (response.isSuccessful()) {
                    saveLaunchesToRealm(response.body().getLaunches(), true);

                    sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, true, call));

                    context.startService(new Intent(context, NextLaunchTracker.class));
                } else {
                    isNextLaunches = false;

                    sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));

                    context.startService(new Intent(context, NextLaunchTracker.class));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isNextLaunches = false;

                sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, false, call, t.getLocalizedMessage()));

                context.startService(new Intent(context, NextLaunchTracker.class));
            }
        });
    }

    public void getLaunchById(int id) {
        isLaunchById = true;
        Timber.i("Running getLaunchById - with ID %s", id);
        DataClient.getInstance().getLaunchById(id, false, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    saveLaunchesToRealm(response.body().getLaunches(), false);

                    sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, true, call));

                } else {
                    isLaunchById = false;

                    sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isLaunchById = false;
                sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getAllAgencies() {
        isAllAgencies = true;
        Timber.i("Running getAllAgencies");
        DataClient.getInstance().getAllAgencies(0, new Callback<AgencyResponse>() {
            @Override
            public void onResponse(Call<AgencyResponse> call, Response<AgencyResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    saveObjectsToRealm(response.body().getAgencies());
                    if (count < total) {
                        getAllAgencies(count);
                    } else {
                        isAllAgencies = false;

                        sendResult(new Result(Constants.ACTION_GET_AGENCY, true, call));
                    }
                } else {
                    isAllAgencies = false;

                    sendResult(new Result(Constants.ACTION_GET_AGENCY, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<AgencyResponse> call, Throwable t) {
                isAllAgencies = false;

                sendResult(new Result(Constants.ACTION_GET_AGENCY, false, call, t.getLocalizedMessage()));
            }
        });
    }

    private void getAllAgencies(final int offset) {
        isAllAgencies = true;
        Timber.i("Running getAllAgencies - %s", offset);
        DataClient.getInstance().getAllAgencies(
                offset, new Callback<AgencyResponse>() {
                    @Override
                    public void onResponse(Call<AgencyResponse> call, Response<AgencyResponse> response) {
                        if (response.isSuccessful()) {
                            int total = response.body().getTotal();
                            int count = response.body().getCount() + offset;
                            Timber.v("UpcomingLaunches Count: %s", count);
                            saveObjectsToRealm(response.body().getAgencies());
                            if (count < total) {
                                getAllAgencies(count);
                            } else {
                                isAllAgencies = false;

                                sendResult(new Result(Constants.ACTION_GET_AGENCY, true, call));
                            }
                        } else {
                            isAllAgencies = false;

                            sendResult(new Result(Constants.ACTION_GET_AGENCY, false, call, ErrorUtil.parseLibraryError(response)));
                        }
                    }

                    @Override
                    public void onFailure(Call<AgencyResponse> call, Throwable t) {
                        isAllAgencies = false;

                        sendResult(new Result(Constants.ACTION_GET_AGENCY, false, call, t.getLocalizedMessage()));
                    }
                });
    }

    public void getAllMissions() {
        isAllMissions = true;
        Timber.i("Running getAllMissions");
        DataClient.getInstance().getAllMissions(0, new Callback<MissionResponse>() {
            @Override
            public void onResponse(Call<MissionResponse> call, Response<MissionResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    saveObjectsToRealm(response.body().getMissions());
                    if (count < total) {
                        getAllMissions(count);
                    } else {
                        isAllMissions = false;

                        sendResult(new Result(Constants.ACTION_GET_MISSION, true, call));
                    }
                } else {
                    isAllMissions = false;

                    sendResult(new Result(Constants.ACTION_GET_MISSION, false, call, ErrorUtil.parseLibraryError(response)));
                }

            }

            @Override
            public void onFailure(Call<MissionResponse> call, Throwable t) {
                isAllMissions = false;

                Crashlytics.logException(t);

                sendResult(new Result(Constants.ACTION_GET_MISSION, false, call, t.getLocalizedMessage()));

            }
        });
    }

    private void getAllMissions(final int offset) {
        isAllMissions = true;
        Timber.i("Running getAllMissions - %s", offset);
        DataClient.getInstance().getAllMissions(offset, new Callback<MissionResponse>() {
            @Override
            public void onResponse(Call<MissionResponse> call, Response<MissionResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.v("UpcomingLaunches Count: %s", count);
                    saveObjectsToRealm(response.body().getMissions());
                    if (count < total) {
                        getAllMissions(count);
                    } else {
                        isAllMissions = false;

                        sendResult(new Result(Constants.ACTION_GET_MISSION, true, call));
                    }
                } else {
                    isAllMissions = false;

                    sendResult(new Result(Constants.ACTION_GET_MISSION, false, call, ErrorUtil.parseLibraryError(response)));
                }

            }

            @Override
            public void onFailure(Call<MissionResponse> call, Throwable t) {
                isAllMissions = false;
                sendResult(new Result(Constants.ACTION_GET_MISSION, false, call, t.getLocalizedMessage()));

            }
        });
    }

    public void getAllLocations() {
        isAllLocations = true;
        Timber.i("Running getAllLocations");
        DataClient.getInstance().getAllLocations(0, new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("Locations - Count: %s", count);
                    saveObjectsToRealm(response.body().getLocations());
                    if (count < total) {
                        getAllLocations(count);
                    } else {
                        isAllLocations = false;

                        sendResult(new Result(Constants.ACTION_GET_LOCATION, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isAllLocations = false;

                    sendResult(new Result(Constants.ACTION_GET_LOCATION, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                isAllLocations = false;

                sendResult(new Result(Constants.ACTION_GET_LOCATION, false, call, t.getLocalizedMessage()));
            }
        });
    }

    private void getAllLocations(final int offset) {
        isAllLocations = true;
        Timber.i("Running getAllLocations - %s", offset);
        DataClient.getInstance().getAllLocations(0, new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.v("Locations - Count: %s", count);
                    saveObjectsToRealm(response.body().getLocations());
                    if (count < total) {
                        getAllLocations(count);
                    } else {
                        isAllLocations = false;

                        sendResult(new Result(Constants.ACTION_GET_LOCATION, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isAllLocations = false;

                    sendResult(new Result(Constants.ACTION_GET_LOCATION, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                isAllLocations = false;

                sendResult(new Result(Constants.ACTION_GET_LOCATION, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getAllPads() {
        isAllPads = true;
        Timber.i("Running getAllPads");
        DataClient.getInstance().getAllPads(0, new Callback<PadResponse>() {
            @Override
            public void onResponse(Call<PadResponse> call, Response<PadResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("Pads - Count: %s", count);
                    saveObjectsToRealm(response.body().getPads());
                    if (count < total) {
                        getAllPads(count);
                    } else {
                        isAllPads = false;

                        sendResult(new Result(Constants.ACTION_GET_PADS, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isAllPads = false;

                    sendResult(new Result(Constants.ACTION_GET_PADS, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<PadResponse> call, Throwable t) {
                isAllPads = false;

                sendResult(new Result(Constants.ACTION_GET_PADS, false, call, t.getLocalizedMessage()));
            }
        });
    }

    private void getAllPads(final int offset) {
        isAllPads = true;
        Timber.i("Running getAllPads - %s", offset);
        DataClient.getInstance().getAllPads(offset, new Callback<PadResponse>() {
            @Override
            public void onResponse(Call<PadResponse> call, Response<PadResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.v("Locations - Count: %s", count);
                    saveObjectsToRealm(response.body().getPads());
                    if (count < total) {
                        getAllPads(count);
                    } else {
                        isAllPads = false;

                        sendResult(new Result(Constants.ACTION_GET_PADS, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isAllPads = false;

                    sendResult(new Result(Constants.ACTION_GET_PADS, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<PadResponse> call, Throwable t) {
                isAllPads = false;

                sendResult(new Result(Constants.ACTION_GET_PADS, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getVehicles() {
        isVehicles = true;
        Timber.i("Running getVehicles");
        DataClient.getInstance().getVehicles(new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                if (response.isSuccessful()) {
                    saveObjectsToRealm(response.body().getVehicles());
                    isVehicles = false;
                    sendResult(new Result(Constants.ACTION_GET_VEHICLES_DETAIL, true, call));
                } else {
                    isVehicles = false;

                    sendResult(new Result(Constants.ACTION_GET_VEHICLES_DETAIL, false, call, ErrorUtil.parseSpaceLaunchNowError(response)));
                }
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                isVehicles = false;
                sendResult(new Result(Constants.ACTION_GET_VEHICLES_DETAIL, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getRockets() {
        isRockets = true;
        Timber.i("Running getRockets");
        DataClient.getInstance().getRockets(0, new Callback<RocketResponse>() {
            @Override
            public void onResponse(Call<RocketResponse> call, Response<RocketResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("Rockets - Count: %s", count);
                    saveObjectsToRealm(response.body().getRockets());
                    if (count < total) {
                        getRockets(count);
                    } else {
                        isRockets = false;

                        sendResult(new Result(Constants.ACTION_GET_VEHICLES, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isRockets = false;
                    sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, ErrorUtil.parseLibraryError(response)));

                }
            }

            @Override
            public void onFailure(Call<RocketResponse> call, Throwable t) {
                isRockets = false;

                sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getRockets(final int offset) {
        isRockets = true;
        Timber.i("Running getRockets - %s", offset);
        DataClient.getInstance().getRockets(offset, new Callback<RocketResponse>() {
            @Override
            public void onResponse(Call<RocketResponse> call, Response<RocketResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.v("Rockets - Count: %s", count);
                    saveObjectsToRealm(response.body().getRockets());
                    if (count < total) {
                        getAllPads(count);
                    } else {
                        isRockets = false;

                        sendResult(new Result(Constants.ACTION_GET_VEHICLES, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isRockets = false;

                    sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<RocketResponse> call, Throwable t) {
                isRockets = false;

                sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getRocketFamily() {
        isRocketFamily = true;
        Timber.i("Running getRocketFamily");
        DataClient.getInstance().getRocketFamily(0, new Callback<RocketFamilyResponse>() {
            @Override
            public void onResponse(Call<RocketFamilyResponse> call, Response<RocketFamilyResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("RocketFamily - Count: %s", count);
                    saveObjectsToRealm(response.body().getRocketFamilies());
                    if (count < total) {
                        getRocketFamily(count);
                    } else {
                        isRocketFamily = false;

                        sendResult(new Result(Constants.ACTION_GET_VEHICLES, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isRocketFamily = false;

                    sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<RocketFamilyResponse> call, Throwable t) {
                isRocketFamily = false;
                sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, t.getLocalizedMessage()));
            }
        });

    }

    public void getRocketFamily(final int offset) {
        isRocketFamily = true;
        Timber.i("Running getRocketFamily - %s", offset);
        DataClient.getInstance().getRocketFamily(offset, new Callback<RocketFamilyResponse>() {
            @Override
            public void onResponse(Call<RocketFamilyResponse> call, Response<RocketFamilyResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.v("RocketFamily - Count: %s", count);
                    saveObjectsToRealm(response.body().getRocketFamilies());
                    if (count < total) {
                        getRocketFamily(count);
                    } else {
                        isRocketFamily = false;

                        sendResult(new Result(Constants.ACTION_GET_VEHICLES_FAMILY, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isRocketFamily = false;

                    sendResult(new Result(Constants.ACTION_GET_VEHICLES_FAMILY, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<RocketFamilyResponse> call, Throwable t) {
                isRocketFamily = false;

                sendResult(new Result(Constants.ACTION_GET_VEHICLES_FAMILY, false, call, t.getLocalizedMessage()));
            }
        });

    }

    private void saveObjectsToRealm(final RealmObject[] objects) {
        Realm mRealm = Realm.getDefaultInstance();
        final RealmList<RealmObject> realmList = new RealmList<>();
        Collections.addAll(realmList, objects);
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(realmList);
            }
        });
        mRealm.close();
    }

    private void saveLaunchesToRealm(Launch[] launches, boolean mini) {
        isSaving = true;
        Realm mRealm = Realm.getDefaultInstance();

        if (mini) {
            for (Launch item : launches) {
                Launch previous = mRealm.where(Launch.class)
                        .equalTo("id", item.getId())
                        .findFirst();
                if (previous != null) {
                    if ((!previous.getNet().equals(item.getNet()) || (previous.getStatus().intValue() != item.getStatus().intValue()))) {
                        Timber.v("%s status has changed.", item.getName());
                        LaunchNotification notification = mRealm.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                        mRealm.beginTransaction();
                        if (notification != null) {
                            notification.resetNotifiers();
                            mRealm.copyToRealmOrUpdate(notification);
                        }
                        previous.resetNotifiers();
                        mRealm.copyToRealmOrUpdate(previous);
                        mRealm.commitTransaction();
                        getLaunchById(item.getId());
                    }
                }
            }
        } else {
            for (Launch item : launches) {
                mRealm.beginTransaction();
                Launch previous = mRealm.where(Launch.class)
                        .equalTo("id", item.getId())
                        .findFirst();
                if (previous != null) {

                    //TODO need to only reset notifiers if launch time changes greater then user configurable number
                    if ((!previous.getNet().equals(item.getNet()) || (previous.getStatus().intValue() != item.getStatus().intValue()))) {
                        Timber.v("%s successful has changed.", item.getName());
                        LaunchNotification notification = mRealm.where(LaunchNotification.class).equalTo("id", item.getId()).findFirst();
                        if (notification != null) {
                            notification.resetNotifiers();
                            mRealm.copyToRealmOrUpdate(notification);
                        }
                    }
                    item.setEventID(previous.getEventID());
                    item.setSyncCalendar(previous.syncCalendar());
                    item.setLaunchTimeStamp(previous.getLaunchTimeStamp());
                    item.setNotifiable(previous.isNotifiable());
                }
                if (item.getLocation() != null) {
                    item.getLocation().setPrimaryID();
                }
                Timber.v("Saving item: %s", item.getName());
                mRealm.copyToRealmOrUpdate(item);
                mRealm.commitTransaction();
            }
        }
        syncNotifiers();
        isSaving = false;
    }

    public void syncNotifiers() {
        isSyncing = true;
        RealmResults<Launch> launchRealms;
        Date date = new Date();

        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        Realm mRealm = Realm.getDefaultInstance();

        if (switchPreferences.getAllSwitch()) {
            launchRealms = mRealm.where(Launch.class)
                    .greaterThanOrEqualTo("net", date)
                    .findAllSorted("net", Sort.ASCENDING);
        } else {
            launchRealms = QueryBuilder.buildSwitchQuery(context, mRealm);
        }

        for (final Launch launchRealm : launchRealms) {
            if (!launchRealm.isUserToggledNotifiable() && !launchRealm.isNotifiable()) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        launchRealm.setNotifiable(true);
                    }
                });
            }
        }
        mRealm.close();
        isSyncing = false;
    }

    private void sendResult(final Result result) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(result.getAction());
        broadcastIntent.putExtra("result", result.isSuccessful());

        if (result.isSuccessful()) {
            Timber.i("%s - Successful: %s", result.getAction(), result.isSuccessful());

            Analytics.from(context).sendNetworkEvent(result.getAction(), result.getRequestURL(), result.isSuccessful());

        } else if (!result.isSuccessful() && result.getErrorMessage() != null) {
            Timber.e("%s - ERROR: %s", result.getAction(), result.getErrorMessage());

            broadcastIntent.putExtra("error", result.getErrorMessage());

            Crashlytics.log(result.getErrorMessage());

            Analytics.from(context).sendNetworkEvent(result.getAction(), result.getRequestURL(), result.isSuccessful(), result.getErrorMessage());

        } else if (!result.isSuccessful()) {
            Timber.e("%s - ERROR: Unknown - URL: %s", result.getAction(), result.getRequestURL());

            Crashlytics.log(result.getAction() + " - " + result.getRequestURL());

            broadcastIntent.putExtra("error", "Unknown error has occurred.");

            Analytics.from(context).sendNetworkEvent(result.getAction(), result.getRequestURL(), result.isSuccessful());
        }

        context.sendBroadcast(broadcastIntent);

        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UpdateRecord updateRecord = new UpdateRecord();
                updateRecord.setType(result.getAction());
                updateRecord.setDate(new Date());
                updateRecord.setSuccessful(result.isSuccessful());
                realm.copyToRealmOrUpdate(updateRecord);
            }
        });
    }

}
