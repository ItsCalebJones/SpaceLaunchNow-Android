package me.calebjones.spacelaunchnow.iss.data;

import java.util.List;

import androidx.annotation.Nullable;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;

public class Callbacks {
    public interface SpacestationListNetworkCallback {
        void onSuccess(List<Spacestation> astronauts, int next, int total, boolean moreAvailable);
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface SpacestationListCallback {
        void onSpacestationsLoaded(RealmResults<Spacestation> astronauts, int nextOffset, int total);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }

    public interface SpacestationNetworkCallback {
        void onSuccess(Spacestation astronaut);
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface SpacestationCallback {
        void onSpacestationLoaded(Spacestation astronaut);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }
}
