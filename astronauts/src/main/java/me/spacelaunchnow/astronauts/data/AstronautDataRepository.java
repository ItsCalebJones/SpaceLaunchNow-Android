package me.spacelaunchnow.astronauts.data;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.UiThread;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.data.models.main.Agency;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
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
                              String search, int[] statusIDs, Callbacks.AstronautListCallback callback) {

        final Date now = Calendar.getInstance().getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        Date old = calendar.getTime();

        final RealmResults<Astronaut> astronauts = getAstronautsFromRealm(statusIDs);
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
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        astronauts.deleteAllFromRealm();
                    }
                });
                offset = 0;
            }
            Timber.v("Getting from network!");
            getAstronautsFromNetwork(limit, offset, search, statusIDs, callback);
        } else {
            callback.onAstronautsLoaded(astronauts, limit, offset);
        }
    }

    public RealmResults<Astronaut> getAstronautsFromRealm(int[] statusIDs) {
        RealmQuery<Astronaut> query = realm.where(Astronaut.class);

        for (int i = 0; i < statusIDs.length; i++) {
            query.equalTo("status.id", statusIDs[i]);
            if (i != statusIDs.length - 1) {
                query.or();
            }
        }
        return query.sort("name", Sort.ASCENDING).findAll();
    }

    private void getAstronautsFromNetwork(int limit, int offset, String search, final int[] statusIDs, final Callbacks.AstronautListCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getAstronautList(limit, offset, search, statusIDs, new Callbacks.AstronautListNetworkCallback() {
            @Override
            public void onSuccess(List<Astronaut> astronauts, int next, int total, boolean moreAvailable) {
                moreDataAvailable = moreAvailable;
                addAstronautsToRealm(astronauts);
                callback.onNetworkStateChanged(false);
                callback.onAstronautsLoaded(getAstronautsFromRealm(statusIDs), next, total);
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

    public void addAstronautsToRealm(final List<Astronaut> astronauts) {
        for (Astronaut astronaut : astronauts) {
            if (astronaut.getAgency() != null) {
                Agency previous = realm.where(Agency.class).equalTo("id", astronaut.getAgency().getId()).findFirst();
                if (previous != null) {
                    astronaut.setAgency(previous);
                }
            }

            if (astronaut.getFlights() != null && !astronaut.getFlights().isEmpty()) {
                RealmList<Launch> launches = new RealmList<>();
                for (Launch launch : astronaut.getFlights()) {
                    Launch existingLaunch = realm.where(Launch.class).equalTo("id", launch.getId()).findFirst();
                    if (existingLaunch == null) {
                        launches.add(launch);
                    } else {
                        launches.add(existingLaunch);
                    }
                }

                astronaut.setFlights(launches);

            }
            astronaut.setLastUpdate(Calendar.getInstance().getTime());
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(astronauts);
            }
        });
    }
}


