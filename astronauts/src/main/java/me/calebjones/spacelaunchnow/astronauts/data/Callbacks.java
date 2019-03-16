package me.calebjones.spacelaunchnow.astronauts.data;

import java.util.List;

import androidx.annotation.Nullable;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;

public class Callbacks {
    public interface AstronautListNetworkCallback {
        void onSuccess(List<Astronaut> astronauts, int next, int total, boolean moreAvailable);
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface AstronautListCallback {
        void onAstronautsLoaded(RealmResults<Astronaut> astronauts, int nextOffset, int total);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }

    public interface AstronautNetworkCallback {
        void onSuccess(Astronaut astronaut);
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface AstronautCallback {
        void onAstronautLoaded(Astronaut astronaut);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }

}
