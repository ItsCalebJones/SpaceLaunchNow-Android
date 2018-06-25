package me.calebjones.spacelaunchnow.content.data.next;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import me.calebjones.spacelaunchnow.content.data.DataClientManager;
import me.calebjones.spacelaunchnow.content.data.DataSaver;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Result;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.interfaces.LibraryService;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchResponse;
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

    public void getNextUpcomingLaunches(NextLaunchDataRepository.NetworkCallback networkCallback) {
        Timber.i("Running getNextUpcomingLaunches");
        DataClient.getInstance().getNextUpcomingLaunches(0, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount();
                    Timber.v("getNextUpcomingLaunches Count: %s", count);
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
                    if (count < total) {
                        getNextUpcomingLaunches(count, networkCallback);
                    } else {
                        dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, true, call));
                        networkCallback.onSuccess();
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

    private void getNextUpcomingLaunches(final int offset, NextLaunchDataRepository.NetworkCallback networkCallback) {
        Timber.i("Running getNextUpcomingLaunches - %s", offset);
        DataClient.getInstance().getNextUpcomingLaunches(offset, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    int total = response.body().getTotal();
                    int count = response.body().getCount() + offset;
                    Timber.v("UpcomingLaunches Count: %s", count);
                    dataSaver.saveLaunchesToRealm(response.body().getLaunches(), false);
                    if (count < total) {
                        getNextUpcomingLaunches(count, networkCallback);
                    } else {
                        networkCallback.onSuccess();
                        dataSaver.sendResult(new Result(Constants.ACTION_GET_NEXT_LAUNCHES, true, call));

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
