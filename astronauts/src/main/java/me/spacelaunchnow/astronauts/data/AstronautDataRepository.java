package me.spacelaunchnow.astronauts.data;

import android.content.Context;

import java.util.List;

import androidx.annotation.UiThread;
import io.realm.Realm;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;

/**
 * Responsible for retrieving data from the Realm cache.
 */

public class AstronautDataRepository {

    private AstronautDataLoader dataLoader;

    private final Context context;

    public AstronautDataRepository(Context context) {
        this.context = context;
        this.dataLoader = new AstronautDataLoader(context);
    }

    @UiThread
    public void getAstronauts(int limit, int offset, String search, Integer status, Callbacks.AstronautListCallback callback) {
        getAstronautsFromNetwork(limit, offset, search, status, callback);
    }

    private void getAstronautsFromNetwork(int limit, int offset, String search, Integer status, final Callbacks.AstronautListCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getAstronautList(limit, offset, search, status, new Callbacks.AstronautListNetworkCallback() {
            @Override
            public void onSuccess(List<Astronaut> astronauts, int next, int total) {

                    callback.onNetworkStateChanged(false);
                    callback.onLaunchesLoaded(astronauts, next, total);
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

    @UiThread
    public void getAstronautById(int id, Callbacks.AstronautCallback callback) {
        getAstronautByIdFromNetwork(id, callback);
    }

    private void getAstronautByIdFromNetwork(int id, final Callbacks.AstronautCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getAstronaut(id, new Callbacks.AstronautNetworkCallback() {
            @Override
            public void onSuccess(Astronaut astronaut) {

                callback.onNetworkStateChanged(false);
                callback.onLaunchesLoaded(astronaut);
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


