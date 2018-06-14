package me.calebjones.spacelaunchnow.content.data;

import android.content.Context;
import android.support.annotation.UiThread;

import com.crashlytics.android.Crashlytics;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.UpdateRecord;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Location;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Mission;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import timber.log.Timber;

/**
 * Responsible for retrieving data from the Realm cache.
 */

public class DataRepository {

    private DataClient dataClient;
    private DataClientManager dataClientManager;
    private Realm realm;

    private final Context context;

    public DataRepository(Context context, Realm realm) {
        this.context = context;
        this.dataClient = DataClient.getInstance();
        this.dataClientManager = new DataClientManager(context);
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
        launchCallback.onLaunchesLoaded(QueryBuilder.buildUpcomingSwitchQuery(context, realm));

        RealmResults<UpdateRecord> records = realm.where(UpdateRecord.class)
                .equalTo("type", Constants.ACTION_GET_UP_LAUNCHES_ALL)
                .or()
                .equalTo("type", Constants.ACTION_GET_UP_LAUNCHES)
                .or()
                .equalTo("type", Constants.ACTION_GET_NEXT_LAUNCHES)
                .sort("date", Sort.DESCENDING)
                .findAll();

        if (records != null && records.size() > 0) {
            UpdateRecord record = records.first();
            Date currentDate = new Date();
            Date lastUpdateDate = record.getDate();
            long timeSinceUpdate = currentDate.getTime() - lastUpdateDate.getTime();
            long timeMaxUpdate = TimeUnit.HOURS.toMillis(1);
            Timber.d("Time since last upcoming launches sync %s", timeSinceUpdate);
            if (timeSinceUpdate > timeMaxUpdate || forceRefresh) {
                Timber.d("%s greater then %s - updating library data.", timeSinceUpdate, timeMaxUpdate);
                getNextUpcomingLaunchesFromNetwork(launchCallback);
            }
        } else {
            getNextUpcomingLaunchesFromNetwork(launchCallback);
        }
    }

    private void getNextUpcomingLaunchesFromNetwork(LaunchCallback callback){
        Date currentDate = new Date();
        callback.onRefreshingFromNetwork();
        dataClientManager.getNextUpcomingLaunches(new NetworkCallback() {
            @Override
            public void onSuccess() {
                callback.onNetworkResultReceived();
                Realm mRealm = Realm.getDefaultInstance();
                RealmResults<Launch> launches = QueryBuilder.buildUpcomingSwitchQuery(context, mRealm);
                for (Launch launch : launches){
                    Date lastUpdate = launch.getLastUpdate();
                    Date net = launch.getNet();
                    // Check time between NET and NOW
                    long netDiffInMs = currentDate.getTime() - net.getTime();
                    long netDiffInHours = TimeUnit.MILLISECONDS.toHours(netDiffInMs);
                    long lastUpdateDiffInMs = currentDate.getTime() - lastUpdate.getTime();
                    long lastUpdateDiffInHours = TimeUnit.MILLISECONDS.toHours(lastUpdateDiffInMs);
                    if (netDiffInHours <= 168){
                        if (lastUpdateDiffInHours > 24){
                            dataClientManager.getLaunchById(launch.getId());
                        }
                    }
                }
                callback.onLaunchesLoaded(launches);
                realm.close();
            }

            @Override
            public void onNetworkFailure(int code) {
                callback.onNetworkError("Unable to load launch data.");
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public interface NetworkCallback {
        void onSuccess();
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface LaunchCallback {
        void onLaunchesLoaded(RealmResults<Launch> launches);
        void onRefreshingFromNetwork();
        void onNetworkResultReceived();
        void onNetworkError(String message);
        void onError(Throwable throwable);
    }
}


