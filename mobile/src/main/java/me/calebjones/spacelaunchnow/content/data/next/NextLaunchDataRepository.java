package me.calebjones.spacelaunchnow.content.data.next;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import com.crashlytics.android.Crashlytics;

import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.content.data.DataClientManager;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.UpdateRecord;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Location;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Mission;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Responsible for retrieving data from the Realm cache.
 */

public class NextLaunchDataRepository {

    private NextLaunchDataLoader dataLoader;
    private Realm realm;

    private final Context context;

    public NextLaunchDataRepository(Context context, Realm realm) {
        this.context = context;
        this.dataLoader = new NextLaunchDataLoader(context);
        this.realm = realm;
    }

    public RealmResults<Launch> getPreviousLaunchData(Realm realm) {
        Timber.i("Syncing launch data...");
        if (ListPreferences.getInstance(context).isFresh()){
            realm.executeTransactionAsync(realm1 -> {
                RealmResults<Launch> launches = null;
                try {
                    launches = QueryBuilder.buildPrevQuery(context, realm1);
                    if (ListPreferences.getInstance(context).isFresh()) {
                        Timber.d("%d launches to sync.", launches.size());
                        for (Launch launch : launches) {
                            Timber.v("Syncing launch %s for mission/location data.", launch.getName());
                            if (launch.getMissions().size() == 0) {
                                RealmResults<Mission> missions = realm1.where(Mission.class).equalTo("launch.id", launch.getId()).findAll();
                                if (missions.size() > 0) {
                                    Timber.v("Matched launch %s with %s mission", launch.getId(), missions.get(0).getId());
                                    final RealmList<Mission> results = new RealmList<Mission>();
                                    results.addAll(missions.subList(0, missions.size()));
                                    launch.setMissions(results);
                                    realm1.copyToRealmOrUpdate(launch);
                                } else {
                                    Timber.v("Unable to match Launch %s to a mission.", launch.getId());
                                }
                            }
                            if (launch.getLocation() == null) {
                                Location location = realm1.where(Location.class).equalTo("pads.id", launch.getLocationid()).findFirst();
                                if (location != null) {
                                    Timber.v("Matched launch %s with %s location", launch.getId(), location.getName());
                                    launch.setLocation(location);
                                    realm1.copyToRealmOrUpdate(launch);
                                } else {
                                    Timber.v("Unable to match Launch %s to a location.", launch.getId());
                                }
                            }
                        }
                        realm1.copyToRealmOrUpdate(launches);
                        ListPreferences.getInstance(context).isFresh(false);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }
        try {
            return QueryBuilder.buildPrevQueryAsync(context, realm);
        } catch (ParseException e) {
            Crashlytics.logException(e);
            return null;
        }
    }

    @UiThread
    public void getNextUpcomingLaunches(boolean forceRefresh, LaunchCallback launchCallback){
        RealmResults<UpdateRecord> records = realm.where(UpdateRecord.class)
                .equalTo("type", Constants.ACTION_GET_UP_LAUNCHES_ALL)
                .or()
                .equalTo("type", Constants.ACTION_GET_UP_LAUNCHES)
                .or()
                .equalTo("type", Constants.ACTION_GET_NEXT_LAUNCHES)
                .sort("date", Sort.DESCENDING)
                .findAll();

        // Start loading data from the network if needed
        // It will put all data into Realm
        if (records != null && records.size() > 0) {
            UpdateRecord record = records.first();
            Date currentDate = new Date();
            Date lastUpdateDate = record.getDate();
            long timeSinceUpdate = currentDate.getTime() - lastUpdateDate.getTime();
            long timeMaxUpdate = TimeUnit.MINUTES.toMillis(1);
            Timber.d("Time since last upcoming launches sync %s", timeSinceUpdate);
            if (timeSinceUpdate > timeMaxUpdate || forceRefresh) {
                Timber.d("%s greater then %s - updating library data.", timeSinceUpdate, timeMaxUpdate);
                getNextUpcomingLaunchesFromNetwork(launchCallback);
            } else {
                launchCallback.onLaunchesLoaded(QueryBuilder.buildUpcomingSwitchQuery(context, realm));
            }
            checkForStaleLaunches();
        } else {
            getNextUpcomingLaunchesFromNetwork(launchCallback);
        }
    }

    private void getNextUpcomingLaunchesFromNetwork(LaunchCallback callback){

        callback.onNetworkStateChanged(true);
        dataLoader.getNextUpcomingLaunches(new NetworkCallback() {
            @Override
            public void onSuccess() {
                callback.onNetworkStateChanged(false);
                RealmResults<Launch> launches = QueryBuilder.buildUpcomingSwitchQuery(context, realm);
                callback.onLaunchesLoaded(launches);
            }

            @Override
            public void onNetworkFailure(int code) {
                callback.onError("Unable to load launch data.", null);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onError("An error has occurred! Uh oh.", throwable);
            }
        });
    }

    private void checkForStaleLaunches(){
        Date currentDate = new Date();
        Realm mRealm = Realm.getDefaultInstance();
        RealmResults<Launch> launches = QueryBuilder.buildUpcomingSwitchQuery(context, mRealm);
        for (Launch launch : launches) {
            Date lastUpdate = launch.getLastUpdate();
            if (lastUpdate == null){
                lastUpdate = currentDate;
            }
            Date net = launch.getNet();
            // Check time between NET and NOW
            long netDiffInMs = currentDate.getTime() - net.getTime();
            long netDiffInHours = TimeUnit.MILLISECONDS.toHours(netDiffInMs);
            long lastUpdateDiffInMs = currentDate.getTime() - lastUpdate.getTime();
            long lastUpdateDiffInHours = TimeUnit.MILLISECONDS.toHours(lastUpdateDiffInMs);
            if (netDiffInHours <= 168) {
                if (lastUpdateDiffInHours > 24) {
                    int id = launch.getId();
                    DataClient.getInstance().getLaunchById(launch.getId(), false, new Callback<LaunchResponse>() {
                        @Override
                        public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                            if (response.isSuccessful()) {
                                dataLoader.getDataSaver().saveLaunchesToRealm(response.body().getLaunches(), false);

                                dataLoader.getDataSaver().sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, true, call));

                            } else {
                                if (response.code() == 404) {
                                    dataLoader.getDataSaver().deleteLaunch(id);
                                }

                                dataLoader.getDataSaver().sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, false, call, ErrorUtil.parseLibraryError(response)));
                            }
                        }

                        @Override
                        public void onFailure(Call<LaunchResponse> call, Throwable t) {
                            dataLoader.getDataSaver().deleteLaunch(id);
                            dataLoader.getDataSaver().sendResult(new Result(Constants.ACTION_GET_UP_LAUNCHES_BY_ID, false, call, t.getLocalizedMessage()));
                        }
                    });
                }
            }
        }
        mRealm.close();
    }

    public interface NetworkCallback {
        void onSuccess();
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface LaunchCallback {
        void onLaunchesLoaded(RealmResults<Launch> launches);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }
}


