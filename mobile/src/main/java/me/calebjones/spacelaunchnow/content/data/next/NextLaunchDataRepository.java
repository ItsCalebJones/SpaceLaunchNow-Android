package me.calebjones.spacelaunchnow.content.data.next;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.UiThread;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.common.content.data.Callbacks;
import me.calebjones.spacelaunchnow.common.prefs.SwitchPreferences;
import me.calebjones.spacelaunchnow.common.content.util.FilterBuilder;
import me.calebjones.spacelaunchnow.common.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.UpdateRecord;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
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

    @UiThread
    public void getNextUpcomingLaunches(int count, boolean forceRefresh, Callbacks.NextLaunchesCallback nextLaunchesCallback){
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
            long timeMaxUpdate = TimeUnit.MINUTES.toMillis(10);
            Timber.d("Time since last upcoming launches sync %s", timeSinceUpdate);
            if (timeSinceUpdate > timeMaxUpdate || forceRefresh) {

                Timber.d("%s greater then %s - updating library data.", timeSinceUpdate, timeMaxUpdate);
                if (!forceRefresh) {
                    nextLaunchesCallback.onLaunchesLoaded(QueryBuilder.buildUpcomingSwitchQuery(context, realm, false));
                }
                if (forceRefresh) {
                    RealmResults<Launch> launches = realm.where(Launch.class).findAll();
                    realm.executeTransaction(realm -> launches.deleteAllFromRealm());
                }

                getNextUpcomingLaunchesFromNetwork(count, nextLaunchesCallback);
            } else {
                nextLaunchesCallback.onLaunchesLoaded(QueryBuilder.buildUpcomingSwitchQuery(context, realm, false));
            }
        } else {
            getNextUpcomingLaunchesFromNetwork(count, nextLaunchesCallback);
        }
    }

    private void getNextUpcomingLaunchesFromNetwork(int count, Callbacks.NextLaunchesCallback callback){

        String locationIds = null;
        String lspIds = null;

        SwitchPreferences switchPreferences = SwitchPreferences.getInstance(context);
        if (!switchPreferences.getAllSwitch()) {
            lspIds = FilterBuilder.getLSPIds(context);
            locationIds = FilterBuilder.getLocationIds(context);
        }

        callback.onNetworkStateChanged(true);
        dataLoader.getNextUpcomingLaunches(count, locationIds, lspIds, new Callbacks.NextNetworkCallback() {
            @Override
            public void onSuccess() {
                callback.onNetworkStateChanged(false);
                RealmResults<Launch> launches = QueryBuilder.buildUpcomingSwitchQuery(context, realm, false);
                callback.onLaunchesLoaded(launches);
            }

            @Override
            public void onNetworkFailure(int code) {
                callback.onNetworkStateChanged(false);
                callback.onError("Unable to load launch data.", null);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onNetworkStateChanged(false);
                callback.onError("An error has occurred! Uh oh.", throwable);
            }
        });
    }
}


