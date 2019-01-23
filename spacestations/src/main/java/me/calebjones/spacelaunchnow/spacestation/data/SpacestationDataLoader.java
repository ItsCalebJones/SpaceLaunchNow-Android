package me.calebjones.spacelaunchnow.spacestation.data;


import android.content.Context;
import android.net.Uri;

import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.error.SpaceLaunchNowError;
import me.calebjones.spacelaunchnow.data.networking.responses.base.SpacestationResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class SpacestationDataLoader {

    private Context context;

    public SpacestationDataLoader(Context context) {
        this.context = context;
    }

    public void getSpacestationList(int limit, int offset, String search,
                                 final Callbacks.SpacestationListNetworkCallback networkCallback) {
        Timber.i("Running getUpcomingLaunchesList");

        DataClient.getInstance().getSpacestations(limit, offset, search, null, new Callback<SpacestationResponse>() {
            @Override
            public void onResponse(Call<SpacestationResponse> call, Response<SpacestationResponse> response) {
                if (response.isSuccessful()) {
                    SpacestationResponse responseBody = response.body();

                    Timber.v("Astronauts returned Count: %s", responseBody.getCount());

                    if (responseBody.getNext() != null) {
                        Uri uri = Uri.parse(responseBody.getNext());
                        String limit = uri.getQueryParameter("limit");
                        String nextOffset = uri.getQueryParameter("offset");
                        String total = uri.getQueryParameter("offset");
                        int next = Integer.valueOf(nextOffset);
                        networkCallback.onSuccess(responseBody.getSpacestations(), next, responseBody.getCount(), true);
                    } else {
                        networkCallback.onSuccess(responseBody.getSpacestations(), 0, responseBody.getCount(), false);
                    }
                } else {
                    SpaceLaunchNowError error = ErrorUtil.parseSpaceLaunchNowError(response);
                    Timber.e(error.getMessage());
                    networkCallback.onNetworkFailure(response.code());

                }
            }

            @Override
            public void onFailure(Call<SpacestationResponse> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }

    public void getSpacestation(int id, final Callbacks.SpacestationNetworkCallback networkCallback) {
        Timber.i("Running getUpcomingLaunchesList");
        DataClient.getInstance().getSpacestationById(id, new Callback<Spacestation>() {
            @Override
            public void onResponse(Call<Spacestation> call, Response<Spacestation> response) {
                if (response.isSuccessful()) {
                    Spacestation spacestation = response.body();
                    networkCallback.onSuccess(spacestation);

                } else {
                    networkCallback.onNetworkFailure(response.code());

                }
            }

            @Override
            public void onFailure(Call<Spacestation> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }

}
