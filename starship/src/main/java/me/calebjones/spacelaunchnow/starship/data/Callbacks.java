package me.calebjones.spacelaunchnow.starship.data;

import androidx.annotation.Nullable;
import me.calebjones.spacelaunchnow.data.models.main.dashboards.Starship;

public class Callbacks {
    public interface StarshipNetworkCallback {
        void onSuccess(Starship starship);
        void onNetworkFailure(int code);
        void onFailure(Throwable throwable);
    }

    public interface StarshipCallback {
        void onStarshipDashboardLoaded(Starship starship);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
    }

}
