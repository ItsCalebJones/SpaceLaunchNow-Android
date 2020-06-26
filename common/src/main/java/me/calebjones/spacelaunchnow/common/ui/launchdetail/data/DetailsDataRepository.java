package me.calebjones.spacelaunchnow.common.ui.launchdetail.data;

import android.content.Context;

import androidx.annotation.UiThread;
import io.realm.Realm;
import me.calebjones.spacelaunchnow.data.models.main.Launch;

/**
 * Responsible for retrieving data from the Realm cache.
 */

public class DetailsDataRepository {

    private DetailsLaunchDataLoader dataLoader;
    private Realm realm;

    private final Context context;

    public DetailsDataRepository(Context context, Realm realm) {
        this.context = context;
        this.dataLoader = new DetailsLaunchDataLoader(context);
        this.realm = realm;
    }

    @UiThread
    public Launch getLaunch(String launchId) {
        Launch launch = realm.where(Launch.class).equalTo("id", launchId).findFirst();
        if (launch != null) {
            return launch;
        } else {
            return null;
        }
    }

    @UiThread
    public Launch getLaunchBySlug(String slug) {
        Launch launch = realm.where(Launch.class).equalTo("slug", slug).findFirst();
        if (launch != null) {
            return launch;
        } else {
            return null;
        }
    }

    @UiThread
    public void getLaunchFromNetwork(String launchId, Callbacks.DetailsCallback launchCallback) {
        launchCallback.onNetworkStateChanged(true);
        dataLoader.getLaunch(launchId, realm, new Callbacks.DetailsNetworkCallback() {
            @Override
            public void onSuccess(Launch launch) {
                launchCallback.onNetworkStateChanged(false);
                launchCallback.onLaunchLoaded(launch);
            }

            @Override
            public void onNetworkFailure(int code) {
                launchCallback.onNetworkStateChanged(false);
                launchCallback.onError("Unable to load launch data.", null);
            }

            @Override
            public void onLaunchDeleted() {
                launchCallback.onNetworkStateChanged(false);
                launchCallback.onLaunchDeleted();
            }

            @Override
            public void onFailure(Throwable throwable) {
                launchCallback.onNetworkStateChanged(false);
                launchCallback.onError("An error has occurred! Uh oh.", throwable);
            }
        });
    }

    @UiThread
    public void fetchDataFromNetworkBySlug(String slug, Callbacks.DetailsCallback launchCallback) {
        launchCallback.onNetworkStateChanged(true);
        dataLoader.getLaunchBySlug(slug, realm, new Callbacks.DetailsNetworkCallback() {
            @Override
            public void onSuccess(Launch launch) {
                launchCallback.onNetworkStateChanged(false);
                launchCallback.onLaunchLoaded(launch);
            }

            @Override
            public void onNetworkFailure(int code) {
                launchCallback.onNetworkStateChanged(false);
                launchCallback.onError("Unable to load launch data.", null);
            }

            @Override
            public void onLaunchDeleted() {
                launchCallback.onNetworkStateChanged(false);
                launchCallback.onLaunchDeleted();
            }

            @Override
            public void onFailure(Throwable throwable) {
                launchCallback.onNetworkStateChanged(false);
                launchCallback.onError("An error has occurred! Uh oh.", throwable);
            }
        });
    }
}


