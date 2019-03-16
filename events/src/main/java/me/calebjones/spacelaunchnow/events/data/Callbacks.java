package me.calebjones.spacelaunchnow.events.data;

import java.util.List;

import androidx.annotation.Nullable;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.main.Event;

public class Callbacks {
    public interface EventListNetworkCallback {
        void onSuccess(List<Event> astronauts, int next, int total, boolean moreAvailable);
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface EventListCallback {
        void onEventsLoaded(RealmResults<Event> astronauts, int nextOffset, int total);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }

    public interface EventNetworkCallback {
        void onSuccess(Event event);
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface EventCallback {
        void onEventLoaded(Event event);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }
}
