package me.calebjones.spacelaunchnow.starship.data;

import android.content.Context;


import androidx.annotation.UiThread;
import io.realm.Realm;
import me.calebjones.spacelaunchnow.data.models.main.dashboards.Starship;

public class StarshipDataRepository {

    private StarshipDataLoader dataLoader;
    private Realm realm;
    private final Context context;

    public StarshipDataRepository(Context context, Realm realm) {
        this.context = context;
        this.dataLoader = new StarshipDataLoader(context);
        this.realm = realm;
    }

    @UiThread
    public void getStarshipDashboard(Callbacks.StarshipCallback callback) {
            getStarshipDashboardFromNetwork(callback);
    }


    private void getStarshipDashboardFromNetwork(final Callbacks.StarshipCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getStarshipDashboard(new Callbacks.StarshipNetworkCallback() {
            @Override
            public void onSuccess(Starship starship) {
                callback.onNetworkStateChanged(false);
                callback.onStarshipDashboardLoaded(starship);
            }

            @Override
            public void onNetworkFailure(int code) {
                callback.onNetworkStateChanged(false);
                callback.onError("Unable to load data.", null);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onNetworkStateChanged(false);
                callback.onError("An error has occurred! Uh oh.", throwable);
            }
        });
    }
}


