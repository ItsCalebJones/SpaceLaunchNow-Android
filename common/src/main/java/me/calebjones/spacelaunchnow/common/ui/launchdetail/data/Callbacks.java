package me.calebjones.spacelaunchnow.common.ui.launchdetail.data;

import androidx.annotation.Nullable;
import me.calebjones.spacelaunchnow.data.models.main.Launch;

public class Callbacks {

    public interface DetailsNetworkCallback {
        void onSuccess(Launch launch);
        void onNetworkFailure(int code);
        void onLaunchDeleted();
        void onFailure(Throwable throwable);
    }

    public interface DetailsCallback {
        void onLaunchLoaded(Launch launch);
        void onNetworkStateChanged(boolean refreshing);
        void onError(String message, @Nullable Throwable throwable);
        void onLaunchDeleted();
    }

}
