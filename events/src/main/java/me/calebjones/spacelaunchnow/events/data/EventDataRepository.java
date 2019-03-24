package me.calebjones.spacelaunchnow.events.data;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.UiThread;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import timber.log.Timber;

/**
 * Responsible for retrieving data from the Realm cache.
 */

public class EventDataRepository {

    private EventDataLoader dataLoader;
    private Realm realm;
    private final Context context;
    private boolean moreDataAvailable;

    public EventDataRepository(Context context, Realm realm) {
        this.context = context;
        this.dataLoader = new EventDataLoader(context);
        this.realm = realm;
    }

    @UiThread
    public void getEvents(int limit, int offset, boolean forceUpdate, Callbacks.EventListCallback callback) {

        final Date now = Calendar.getInstance().getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_WEEK, -1);
        Date old = calendar.getTime();

        final RealmResults<Event> events = getEventsFromRealm();
        Timber.v("Current count in DB: %s", events.size());
        for (Event event : events) {
            if (event.getLastUpdate() != null && event.getLastUpdate().before(old)) {
                forceUpdate = true;
            }
        }

        Timber.v("Limit: %s Offset: %s", limit, offset);
        if (forceUpdate || events.size() == 0 || events.size() < limit + offset) {
            if (forceUpdate) {
                //delete cache first
                realm.executeTransaction(realm -> events.deleteAllFromRealm());
                offset = 0;
            }
            Timber.v("Getting from network!");
            getEventsFromNetwork(limit, offset, callback);
        } else {
            callback.onEventsLoaded(events, limit, offset);
        }
    }

    //TODO fix query
    public RealmResults<Event> getEventsFromRealm() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        Date date = calendar.getTime();
        RealmQuery<Event> query = realm.where(Event.class).isNotNull("id").greaterThanOrEqualTo("date", date);
        return query.sort("date", Sort.ASCENDING).findAll();
    }

    public Event getEventByIdFromRealm(int id) {
        return realm.where(Event.class).equalTo("id", id).findFirst();
    }


    private void getEventsFromNetwork(int limit, int offset, final Callbacks.EventListCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getEventList(limit, offset, new Callbacks.EventListNetworkCallback() {
            @Override
            public void onSuccess(List<Event> events, int next, int total, boolean moreAvailable) {
                moreDataAvailable = moreAvailable;
                addEventsToRealm(events);
                callback.onNetworkStateChanged(false);
                callback.onEventsLoaded(getEventsFromRealm(), next, total);
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
    public void getEventById(int id, Callbacks.EventCallback callback) {
        callback.onEventLoaded(getEventByIdFromRealm(id));
        getEventByIdFromNetwork(id, callback);
    }

    private void getEventByIdFromNetwork(int id, final Callbacks.EventCallback callback) {

        callback.onNetworkStateChanged(true);
        dataLoader.getEvent(id, new Callbacks.EventNetworkCallback() {
            @Override
            public void onSuccess(Event event) {

                callback.onNetworkStateChanged(false);
                callback.onEventLoaded(event);
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

    public void addEventsToRealm(final List<Event> events) {
        realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(events));
    }
}


