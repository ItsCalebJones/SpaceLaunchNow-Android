package me.calebjones.spacelaunchnow.content.data.previous;

import android.content.Context;
import android.support.annotation.UiThread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.content.data.callbacks.Callbacks;
import me.calebjones.spacelaunchnow.content.util.QueryBuilder;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.UpdateRecord;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Responsible for retrieving data from the Realm cache.
 */

public class PreviousDataRepository {

    private PreviousDataLoader dataLoader;
    private Realm realm;

    private final Context context;

    public PreviousDataRepository(Context context, Realm realm) {
        this.context = context;
        this.dataLoader = new PreviousDataLoader(context);
        this.realm = realm;
    }

    @UiThread
    public void getPreviousLaunches(int count, String search, String lspName, String serialNumber, Integer launchId, Callbacks.ListCallbackMini launchCallback) {
        getPreviousLaunchesFromNetwork(count, search, lspName, serialNumber, launchId,  launchCallback);
    }


    private void getPreviousLaunchesFromNetwork(int count, String search, String lspName, String serialNumber, Integer launcherId, Callbacks.ListCallbackMini callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getPreviousLaunches(30, count, search, lspName, serialNumber, launcherId, new Callbacks.ListNetworkCallbackMini() {
            @Override
            public void onSuccess(List<LaunchList> launches, int next) {
                callback.onNetworkStateChanged(false);
                callback.onLaunchesLoaded(launches, next);
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
}


