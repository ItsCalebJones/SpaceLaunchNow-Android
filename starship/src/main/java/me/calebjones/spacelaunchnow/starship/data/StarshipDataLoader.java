package me.calebjones.spacelaunchnow.starship.data;
import android.content.Context;

import org.jetbrains.annotations.NotNull;

import me.calebjones.spacelaunchnow.data.models.main.dashboards.Starship;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.error.SpaceLaunchNowError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class StarshipDataLoader {

    private Context context;

    public StarshipDataLoader(Context context) {
        this.context = context;
    }

    public void getStarshipDashboard(final Callbacks.StarshipNetworkCallback networkCallback) {
        Timber.i("Running getStarshipDashboard");

        DataClient.getInstance().getStarshipDashboard(new Callback<Starship>() {
            @Override
            public void onResponse(Call<Starship> call, Response<Starship> response) {
                if (response.isSuccessful()) {
                    Starship starship = response.body();
                    networkCallback.onSuccess(starship);
                } else {
                    SpaceLaunchNowError error = ErrorUtil.parseSpaceLaunchNowError(response);
                    Timber.e(error.getMessage());
                    networkCallback.onNetworkFailure(response.code());

                }
            }

            @Override
            public void onFailure(Call<Starship> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }
}
