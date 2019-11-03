package me.calebjones.spacelaunchnow.astronauts.data;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.UiThread;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import timber.log.Timber;

/**
 * Responsible for retrieving data from the Realm cache.
 */

public class AstronautDataRepository {

    private AstronautDataLoader dataLoader;
    private Realm realm;
    private final Context context;
    private boolean moreDataAvailable;

    public AstronautDataRepository(Context context, Realm realm) {
        this.context = context;
        this.dataLoader = new AstronautDataLoader(context);
        this.realm = realm;
    }

    @UiThread
    public void getAstronauts(int limit, int offset, boolean firstLaunch, boolean forceUpdate,
                              String search, List<Integer> statusIDs, Callbacks.AstronautListCallback callback) {

        final Date now = Calendar.getInstance().getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        Date old = calendar.getTime();

        final RealmResults<Astronaut> astronauts = getAstronautsFromRealm(statusIDs, search);
        Timber.v("Current count in DB: %s", astronauts.size());
        for (Astronaut astronaut : astronauts){
            if (astronaut.getLastUpdate() != null && astronaut.getLastUpdate().before(old)){
                forceUpdate = true;
            }
        }

        Timber.v("Limit: %s Offset: %s", limit, offset);
        if (firstLaunch || forceUpdate || astronauts.size() == 0 || astronauts.size() < limit + offset) {
            if (forceUpdate) {
                //delete cache first
                realm.executeTransaction(realm -> astronauts.deleteAllFromRealm());
                offset = 0;
            }
            Timber.v("Getting from network!");
            getAstronautsFromNetwork(limit, offset, search, statusIDs, callback);
        } else {
            callback.onAstronautsLoaded(astronauts, limit, offset);
        }
    }

    public RealmResults<Astronaut> getAstronautsFromRealm(List<Integer> statusIDs, String searchTerm) {
        RealmQuery<Astronaut> query = realm.where(Astronaut.class).isNotNull("id").and();
        if (searchTerm != null){
            query.beginGroup();
            query.contains("name", searchTerm, Case.INSENSITIVE).or();
            query.contains("agency.name", searchTerm, Case.INSENSITIVE).or();
            query.contains("agency.abbrev", searchTerm, Case.INSENSITIVE);
            query.endGroup().and();
        }

        if (statusIDs != null && statusIDs.size() > 0) {
            boolean first = true;
            for (int statusID : statusIDs) {
                if (!first) {
                    query.or();
                } else {
                    first = false;
                    query.beginGroup();
                }
                query.equalTo("status.id", statusID);
            }
            query.endGroup();
        }

        return query.sort("name", Sort.ASCENDING).findAll();
    }

    public Astronaut getAstronautByIdFromRealm(int id) {
        return realm.where(Astronaut.class).equalTo("id", id).findFirst();
    }


    private void getAstronautsFromNetwork(int limit, int offset, final String search, final List<Integer> statusIDs, final Callbacks.AstronautListCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getAstronautList(limit, offset, search, statusIDs, new Callbacks.AstronautListNetworkCallback() {
            @Override
            public void onSuccess(List<Astronaut> astronauts, int next, int total, boolean moreAvailable) {
                moreDataAvailable = moreAvailable;
                addAstronautsToRealm(astronauts);
                callback.onNetworkStateChanged(false);
                callback.onAstronautsLoaded(getAstronautsFromRealm(statusIDs, search), next, total);
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
        callback.onAstronautLoaded(getAstronautByIdFromRealm(id));
        getAstronautByIdFromNetwork(id, callback);
    }

    private void getAstronautByIdFromNetwork(int id, final Callbacks.AstronautCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getAstronaut(id, new Callbacks.AstronautNetworkCallback() {
            @Override
            public void onSuccess(Astronaut astronaut) {

                callback.onNetworkStateChanged(false);
                callback.onAstronautLoaded(astronaut);
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

    public void addAstronautsToRealm(final List<Astronaut> astronauts) {
        for (Astronaut astronaut : astronauts) {
            if (astronaut.getAgency() != null) {
                Agency previous = realm.where(Agency.class).equalTo("id", astronaut.getAgency().getId()).findFirst();
                if (previous != null) {
                    if (previous.getAbbrev() != null) {
                        astronaut.setAgency(previous);
                    }
                }
            }

            astronaut.setLastUpdate(Calendar.getInstance().getTime());
            if (astronaut.id == null){
                Timber.v("WTF");
            }
        }

        realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(astronauts));
    }
}


