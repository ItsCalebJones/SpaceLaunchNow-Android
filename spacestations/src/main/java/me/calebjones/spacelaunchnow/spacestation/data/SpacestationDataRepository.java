package me.calebjones.spacelaunchnow.spacestation.data;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.UiThread;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import timber.log.Timber;

/**
 * Responsible for retrieving data from the Realm cache.
 */

public class SpacestationDataRepository {

    private SpacestationDataLoader dataLoader;
    private Realm realm;
    private final Context context;
    private boolean moreDataAvailable;

    public SpacestationDataRepository(Context context, Realm realm) {
        this.context = context;
        this.dataLoader = new SpacestationDataLoader(context);
        this.realm = realm;
    }

    @UiThread
    public void getSpacestations(int limit, int offset, boolean forceUpdate,
                                 String search,
                                 Callbacks.SpacestationListCallback callback) {

        final Date now = Calendar.getInstance().getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        Date old = calendar.getTime();

        final RealmResults<Spacestation> spacestations = getSpacestationsFromRealm();
        Timber.v("Current count in DB: %s", spacestations.size());
        for (Spacestation spacestation : spacestations) {
            if (spacestation.getLastUpdate() != null && spacestation.getLastUpdate().before(old)) {
                forceUpdate = true;
            }
        }

        Timber.v("Limit: %s Offset: %s", limit, offset);
        if (forceUpdate || spacestations.size() == 0 || spacestations.size() < limit + offset) {
            if (forceUpdate) {
                //delete cache first
                realm.executeTransaction(realm -> spacestations.deleteAllFromRealm());
                offset = 0;
            }
            Timber.v("Getting from network!");
            getSpacestationsFromNetwork(limit, offset, search, callback);
        } else {
            callback.onSpacestationsLoaded(spacestations, limit, offset);
        }
    }

    public RealmResults<Spacestation> getSpacestationsFromRealm() {
        RealmQuery<Spacestation> query = realm.where(Spacestation.class).isNotNull("id");
        return query.sort("name", Sort.ASCENDING).findAll();
    }

    public Spacestation getSpacestationByIdFromRealm(int id) {
        return realm.where(Spacestation.class).equalTo("id", id).findFirst();
    }


    private void getSpacestationsFromNetwork(int limit, int offset, final String search, final Callbacks.SpacestationListCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getSpacestationList(limit, offset, search, new Callbacks.SpacestationListNetworkCallback() {
            @Override
            public void onSuccess(List<Spacestation> astronauts, int next, int total, boolean moreAvailable) {
                moreDataAvailable = moreAvailable;
                addSpacestationsToRealm(astronauts);
                callback.onNetworkStateChanged(false);
                callback.onSpacestationsLoaded(getSpacestationsFromRealm(), next, total);
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
    public void getSpacestationById(int id, Callbacks.SpacestationCallback callback) {
        callback.onSpacestationLoaded(getSpacestationByIdFromRealm(id));
        getSpacestationByIdFromNetwork(id, callback);
    }

    private void getSpacestationByIdFromNetwork(int id, final Callbacks.SpacestationCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getSpacestation(id, new Callbacks.SpacestationNetworkCallback() {
            @Override
            public void onSuccess(Spacestation astronaut) {

                callback.onNetworkStateChanged(false);
                callback.onSpacestationLoaded(astronaut);
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

    public void addSpacestationsToRealm(final List<Spacestation> astronauts) {
        realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(astronauts));
    }
}


