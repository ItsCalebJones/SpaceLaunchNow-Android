package me.calebjones.spacelaunchnow.common.content.data.upcoming;


import android.content.Context;
import android.net.Uri;

import me.calebjones.spacelaunchnow.common.content.data.Callbacks;
import me.calebjones.spacelaunchnow.data.networking.DataSaver;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchListResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class UpcomingDataLoader {

    private DataSaver dataSaver;
    private Context context;

    public UpcomingDataLoader(Context context) {
        this.context = context;
        this.dataSaver = new DataSaver(context);
    }

    public DataSaver getDataSaver() {
        return dataSaver;
    }

    public void getUpcomingLaunchesList(int limit, int offset, String search, String lspName, String serialNumber, Integer launchId, Callbacks.ListNetworkCallbackMini networkCallback) {
        Timber.i("Running getUpcomingLaunchesList");
        DataClient.getInstance().getUpcomingLaunchesMini(limit, offset, search, lspName, serialNumber, launchId, new Callback<LaunchListResponse>() {
            @Override
            public void onResponse(Call<LaunchListResponse> call, Response<LaunchListResponse> response) {
                if (response.isSuccessful()) {
                    LaunchListResponse launchResponse = response.body();

                    Timber.v("UpcomingLaunches Count: %s", launchResponse.getCount());

                    if (launchResponse.getNext() != null) {
                        Uri uri = Uri.parse(launchResponse.getNext());
                        String limit = uri.getQueryParameter("limit");
                        String nextOffset = uri.getQueryParameter("offset");
                        String total = uri.getQueryParameter("offset");
                        int next = Integer.valueOf(nextOffset);
                        networkCallback.onSuccess(launchResponse.getLaunches(), next, launchResponse.getCount());
                    } else {
                        networkCallback.onSuccess(launchResponse.getLaunches(), 0, launchResponse.getCount());
                    }
                } else {
                    networkCallback.onNetworkFailure(response.code());
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, ErrorUtil.parseSpaceLaunchNowError(response)));

                }
            }

            @Override
            public void onFailure(Call<LaunchListResponse> call, Throwable t) {
                networkCallback.onFailure(t);
                dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, t.getLocalizedMessage()));
            }
        });
    }

}
