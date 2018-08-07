package me.calebjones.spacelaunchnow.content.data.next;


import android.content.Context;
import android.net.Uri;

import java.util.Set;

import me.calebjones.spacelaunchnow.content.data.DataSaver;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.interfaces.LibraryService;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NextLaunchDataLoader {

    private LibraryService libraryService;
    private DataSaver dataSaver;
    private Context context;

    public NextLaunchDataLoader(Context context) {
        this.context = context;
        this.dataSaver = new DataSaver(context);
        libraryService = DataClient.getInstance().getLibraryService();
    }

    public DataSaver getDataSaver() {
        return dataSaver;
    }

    public void getNextUpcomingLaunches(int limit, NextLaunchDataRepository.NetworkCallback networkCallback) {
        Timber.i("Running getNextUpcomingLaunches");
        DataClient.getInstance().getNextUpcomingLaunches(limit, 0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    LaunchResponse launchResponse = response.body();

                    Timber.v("UpcomingLaunches Count: %s", launchResponse.getCount());
                    dataSaver.saveLaunchesToRealm(launchResponse.getLaunches(), false);
                    networkCallback.onSuccess();
                    dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, true, call));
                    if (launchResponse.getNext() != null) {
                        Uri uri = Uri.parse(launchResponse.getNext());
                        String limit = uri.getQueryParameter("limit");
                        String offset = uri.getQueryParameter("offset");
                        Timber.v("Test");
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
