package me.calebjones.spacelaunchnow.common.content.data.previous;

import android.content.Context;

import java.util.List;

import androidx.annotation.UiThread;
import io.realm.Realm;
import me.calebjones.spacelaunchnow.common.content.data.Callbacks;
import me.calebjones.spacelaunchnow.data.models.main.LaunchList;

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
            public void onSuccess(List<LaunchList> launches, int next, int total) {
                callback.onNetworkStateChanged(false);
                callback.onLaunchesLoaded(launches, next, total);
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


