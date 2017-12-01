package me.calebjones.spacelaunchnow.content.data;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import java.net.MalformedURLException;
import java.net.URL;

import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.services.NextLaunchTracker;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.RocketDetail;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * This class is responsible for async loading of data via the DataClient and sending it to DataSaver to be saved.
 */

public class DataClientManager {

    private Context context;

    private DataRepositoryManager dataRepositoryManager;
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

    public DataClientManager(Context context) {
        this.context = context;
        this.dataRepositoryManager = new DataRepositoryManager(context, this);
        this.dataSaver = new DataSaver(context, this);
        nextLaunchTracker = new NextLaunchTracker(context);
    }

    public DataRepositoryManager getDataRepositoryManager() {
        return dataRepositoryManager;
    }
    
    public DataSaver getDataSaver(){
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
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
                    if (count < total) {
                        getLaunchesByDate(startDate, endDate, count);
                    } else {
                        isLaunchByDate = false;
                        ListPreferences.getInstance(context).isFresh(true);

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, true, call));
                    }
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
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.i("getLaunchesByDate - Successful - Total: %s Offset: %s Count: %s", total, offset, count);
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
                    if (count < total) {
                        getLaunchesByDate(startDate, endDate, count);
                    } else {
                        isLaunchByDate = false;
                        ListPreferences.getInstance(context).isFresh(true);
                        dataSaver.sendResult(new Result(Constants.ACTION_GET_PREV_LAUNCHES, true, call));
                    }
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
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
                    if (count < total) {
                        getNextUpcomingLaunches(count);
                    } else {
                        isUpcomingLaunch = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, true, call));

                        nextLaunchTracker.runUpdate();
                    }
                } else {
                    isUpcomingLaunch = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));

                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isUpcomingLaunch = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, t.getLocalizedMessage()));
            }
        });
    }

    private void getNextUpcomingLaunches(final int offset) {
        isUpcomingLaunch = true;
        Timber.i("Running getNextUpcomingLaunches - %s", offset);
        DataClient.getInstance().getNextUpcomingLaunches(offset, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.v("UpcomingLaunches Count: %s", count);
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
                    if (count < total) {
                        getNextUpcomingLaunches(count);
                    } else {
                        isUpcomingLaunch = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, true, call));

                        nextLaunchTracker.runUpdate();
                    }
                } else {
                    isUpcomingLaunch = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isUpcomingLaunch = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getUpcomingLaunches() {
        isUpcomingLaunchAll = true;
        Timber.i("Running getUpcomingLaunches");
        DataClient.getInstance().getUpcomingLaunches(0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    if (count < total) {
                        getUpcomingLaunches(count);
                    } else {
                        isUpcomingLaunchAll = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, true, call));

                        nextLaunchTracker.runUpdate();
                    }
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
        DataClient.getInstance().getUpcomingLaunches(offset, new Callback<LaunchResponse>() {

            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.v("UpcomingLaunches Count: %s", count);
                    if (count < total) {
                        getUpcomingLaunches(count);
                    } else {
                        isUpcomingLaunchAll = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES, true, call));

                        nextLaunchTracker.runUpdate();
                    }
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

    public void getUpcomingLaunchesAll() {
        isUpcomingLaunchAll = true;
        Timber.i("Running getUpcomingLaunchesAll");
        DataClient.getInstance().getUpcomingLaunchesAll(0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("UpcomingLaunches Count: %s", count);
                    if (count < total) {
                        getUpcomingLaunchesAll(count);
                    } else {
                        isUpcomingLaunchAll = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_ALL, true, call));

                        nextLaunchTracker.runUpdate();
                    }
                } else {
                    isUpcomingLaunchAll = false;
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_ALL, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isUpcomingLaunchAll = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_ALL, false, call, t.getLocalizedMessage()));

                nextLaunchTracker.runUpdate();

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
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.v("UpcomingLaunches Count: %s", count);
                    if (count < total) {
                        getUpcomingLaunchesAll(count);
                    } else {
                        isUpcomingLaunchAll = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_ALL, true, call));

                        nextLaunchTracker.runUpdate();
                    }
                } else {
                    isUpcomingLaunchAll = false;
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_ALL, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isUpcomingLaunchAll = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_ALL, false, call, t.getLocalizedMessage()));

                nextLaunchTracker.runUpdate();

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

    public void getLaunchById(int id) {
        isLaunchById = true;
        Timber.i("Running getLaunchById - with ID %s", id);
        DataClient.getInstance().getLaunchById(id, false, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, true, call));
                    isLaunchById = false;

                } else {
                    isLaunchById = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                isLaunchById = false;
                dataSaver.sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, false, call, t.getLocalizedMessage()));
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
                    dataSaver.saveObjectsToRealm(response.body().getAgencies());
                    if (count < total) {
                        getAllAgencies(count);
                    } else {
                        isAllAgencies = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_AGENCY, true, call));
                    }
                } else {
                    isAllAgencies = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_AGENCY, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<AgencyResponse> call, Throwable t) {
                isAllAgencies = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_AGENCY, false, call, t.getLocalizedMessage()));
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
                            dataSaver.saveObjectsToRealm(response.body().getAgencies());
                            if (count < total) {
                                getAllAgencies(count);
                            } else {
                                isAllAgencies = false;

                                dataSaver.sendResult(new Result(Constants.ACTION_GET_AGENCY, true, call));
                            }
                        } else {
                            isAllAgencies = false;

                            dataSaver.sendResult(new Result(Constants.ACTION_GET_AGENCY, false, call, ErrorUtil.parseLibraryError(response)));
                        }
                    }

                    @Override
                    public void onFailure(Call<AgencyResponse> call, Throwable t) {
                        isAllAgencies = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_AGENCY, false, call, t.getLocalizedMessage()));
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
                    Timber.v("Missions Count: %s", count);
                    dataSaver.saveObjectsToRealm(response.body().getMissions());
                    if (count < total) {
                        getAllMissions(count);
                    } else {
                        isAllMissions = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_MISSION, true, call));
                    }
                } else {
                    isAllMissions = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_MISSION, false, call, ErrorUtil.parseLibraryError(response)));
                }

            }

            @Override
            public void onFailure(Call<MissionResponse> call, Throwable t) {
                isAllMissions = false;

                Crashlytics.logException(t);

                dataSaver.sendResult(new Result(Constants.ACTION_GET_MISSION, false, call, t.getLocalizedMessage()));

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
                    Timber.v("Missions Count: %s", count);
                    dataSaver.saveObjectsToRealm(response.body().getMissions());
                    if (count < total) {
                        getAllMissions(count);
                    } else {
                        isAllMissions = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_MISSION, true, call));
                    }
                } else {
                    isAllMissions = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_MISSION, false, call, ErrorUtil.parseLibraryError(response)));
                }

            }

            @Override
            public void onFailure(Call<MissionResponse> call, Throwable t) {
                isAllMissions = false;
                dataSaver.sendResult(new Result(Constants.ACTION_GET_MISSION, false, call, t.getLocalizedMessage()));

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
                    dataSaver.saveObjectsToRealm(response.body().getLocations());
                    if (count < total) {
                        getAllLocations(count);
                    } else {
                        isAllLocations = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_LOCATION, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isAllLocations = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_LOCATION, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                isAllLocations = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_LOCATION, false, call, t.getLocalizedMessage()));
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
                    dataSaver.saveObjectsToRealm(response.body().getLocations());
                    if (count < total) {
                        getAllLocations(count);
                    } else {
                        isAllLocations = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_LOCATION, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isAllLocations = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_LOCATION, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                isAllLocations = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_LOCATION, false, call, t.getLocalizedMessage()));
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
                    dataSaver.saveObjectsToRealm(response.body().getPads());
                    if (count < total) {
                        getAllPads(count);
                    } else {
                        isAllPads = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_PADS, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isAllPads = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_PADS, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<PadResponse> call, Throwable t) {
                isAllPads = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_PADS, false, call, t.getLocalizedMessage()));
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
                    dataSaver.saveObjectsToRealm(response.body().getPads());
                    if (count < total) {
                        getAllPads(count);
                    } else {
                        isAllPads = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_PADS, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isAllPads = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_PADS, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<PadResponse> call, Throwable t) {
                isAllPads = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_PADS, false, call, t.getLocalizedMessage()));
            }
        });
    }

    public void getVehicles(final String family) {
        isVehicles = true;
        Timber.i("Running getVehicles");
        DataClient.getInstance().getVehicles(family, new Callback<VehicleResponse>() {
            @Override
            public void onResponse(Call<VehicleResponse> call, Response<VehicleResponse> response) {
                if (response.isSuccessful()) {
                    RocketDetail[] details = response.body().getVehicles();
                    dataSaver.saveObjectsToRealm(details);
                    isVehicles = false;
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES_DETAIL, true, call));
                } else {
                    isVehicles = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES_DETAIL, false, call, ErrorUtil.parseSpaceLaunchNowError(response)));
                }
            }

            @Override
            public void onFailure(Call<VehicleResponse> call, Throwable t) {
                isVehicles = false;
                dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES_DETAIL, false, call, t.getLocalizedMessage()));
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
                    dataSaver.saveObjectsToRealm(response.body().getRockets());
                    if (count < total) {
                        getRockets(count);
                    } else {
                        isRockets = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isRockets = false;
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, ErrorUtil.parseLibraryError(response)));

                }
            }

            @Override
            public void onFailure(Call<RocketResponse> call, Throwable t) {
                isRockets = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, t.getLocalizedMessage()));
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
                    dataSaver.saveObjectsToRealm(response.body().getRockets());
                    if (count < total) {
                        getAllPads(count);
                    } else {
                        isRockets = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isRockets = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<RocketResponse> call, Throwable t) {
                isRockets = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, t.getLocalizedMessage()));
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
                    dataSaver.saveObjectsToRealm(response.body().getRocketFamilies());
                    if (count < total) {
                        getRocketFamily(count);
                    } else {
                        isRocketFamily = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isRocketFamily = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<RocketFamilyResponse> call, Throwable t) {
                isRocketFamily = false;
                dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES, false, call, t.getLocalizedMessage()));
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
                    dataSaver.saveObjectsToRealm(response.body().getRocketFamilies());
                    if (count < total) {
                        getRocketFamily(count);
                    } else {
                        isRocketFamily = false;

                        dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES_FAMILY, true, call));
                    }
                } else {
                    //Some Error occurred.
                    isRocketFamily = false;

                    dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES_FAMILY, false, call, ErrorUtil.parseLibraryError(response)));
                }
            }

            @Override
            public void onFailure(Call<RocketFamilyResponse> call, Throwable t) {
                isRocketFamily = false;

                dataSaver.sendResult(new Result(Constants.ACTION_GET_VEHICLES_FAMILY, false, call, t.getLocalizedMessage()));
            }
        });

    }

}
