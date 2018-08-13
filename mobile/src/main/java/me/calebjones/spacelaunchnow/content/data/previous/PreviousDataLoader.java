package me.calebjones.spacelaunchnow.content.data.previous;


import android.content.Context;
import android.net.Uri;

import me.calebjones.spacelaunchnow.content.data.DataSaver;
import me.calebjones.spacelaunchnow.content.data.callbacks.Callbacks;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class PreviousDataLoader {

    private DataSaver dataSaver;
    private Context context;

    public PreviousDataLoader(Context context) {
        this.context = context;
        this.dataSaver = new DataSaver(context);
    }

    public DataSaver getDataSaver() {
        return dataSaver;
    }

    public void getPreviousLaunches(int limit, int offset, String search, Callbacks.ListNetworkCallback networkCallback) {
        Timber.i("Running getPreviousLaunches");
        DataClient.getInstance().getPreviousLaunches(limit, offset, search, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    LaunchResponse launchResponse = response.body();

                    Timber.v("Previous Launch Count: %s", launchResponse.getCount());

                    if (launchResponse.getNext() != null) {
                        Uri uri = Uri.parse(launchResponse.getNext());
                        String limit = uri.getQueryParameter("limit");
                        String nextOffset = uri.getQueryParameter("offset");
                        int next = Integer.valueOf(nextOffset);
                        networkCallback.onSuccess(launchResponse.getLaunches(), next);
                    } else {
                        networkCallback.onSuccess(launchResponse.getLaunches(), 0);
                    }
                } else {
                    networkCallback.onNetworkFailure(response.code());
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, ErrorUtil.parseLibraryError(response)));

                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                networkCallback.onFailure(t);
                dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, false, call, t.getLocalizedMessage()));
            }
        });
    }

}
