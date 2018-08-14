package me.calebjones.spacelaunchnow.content.data.upcoming;

import android.content.Context;
import android.support.annotation.UiThread;

import java.util.List;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.content.data.callbacks.Callbacks;
import me.calebjones.spacelaunchnow.data.models.main.Launch;

/**
 * Responsible for retrieving data from the Realm cache.
 */

public class UpcomingDataRepository {

    private UpcomingDataLoader dataLoader;
    private Realm realm;

    private final Context context;

    public UpcomingDataRepository(Context context, Realm realm) {
        this.context = context;
        this.dataLoader = new UpcomingDataLoader(context);
        this.realm = realm;
    }


    @UiThread
    public void getUpcomingLaunches(int count, String search, String lspName, Integer launcherId, Callbacks.ListCallback launchCallback) {
        getUpcomingLaunchesFromNetwork(count, search, lspName, launcherId, launchCallback);
    }

    private void getUpcomingLaunchesFromNetwork(int count, String search, String lspName, Integer launcherId, Callbacks.ListCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getUpcomingLaunches(30, count, search, lspName, launcherId, new Callbacks.ListNetworkCallback() {
            @Override
            public void onSuccess(List<Launch> launches, int next) {
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


