package me.calebjones.spacelaunchnow.common.ui.launchdetail.data;


import android.content.Context;

import io.realm.Realm;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.data.networking.DataSaver;
import me.calebjones.spacelaunchnow.data.networking.error.ErrorUtil;
import me.calebjones.spacelaunchnow.data.networking.error.SpaceLaunchNowError;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class DetailsLaunchDataLoader {

    private DataSaver dataSaver;
    private Context context;

    public DetailsLaunchDataLoader(Context context) {
        this.context = context;
        this.dataSaver = new DataSaver(context);
    }

    public void getLaunch(String id, Realm realm, Callbacks.DetailsNetworkCallback networkCallback) {
        Timber.i("Running getAstronaut");

        DataClient.getInstance().getLaunchById(id, new Callback<Launch>() {
            @Override
            public void onResponse(Call<Launch> call, Response<Launch> response) {
                if (response.isSuccessful()) {
                    Launch launch = response.body();
                    Timber.v("Launch: %s", launch.getName());
                    dataSaver.saveLaunchToRealm(launch);
                    networkCallback.onSuccess(launch);
                } else {
                    SpaceLaunchNowError error = ErrorUtil.parseSpaceLaunchNowError(response);
                    if (error.getMessage() != null && error.getMessage().contains("None found")) {
                        final Launch launch = realm.where(Launch.class).equalTo("id", id).findFirst();
                        if (launch != null) {
                            realm.executeTransaction(realm -> launch.deleteFromRealm());
                        }
                        networkCallback.onLaunchDeleted();
                    } else {
                        networkCallback.onNetworkFailure(response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Launch> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }

    public void getLaunchBySlug(String slug, Realm realm, Callbacks.DetailsNetworkCallback networkCallback) {
        Timber.i("Running get Launch by Slug - %s", slug);

        DataClient.getInstance().getLaunchBySlug(slug, new Callback<LaunchResponse>() {
            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
                if (response.isSuccessful()) {
                    LaunchResponse launches = response.body();
                    if (launches != null && launches.getCount() == 1){
                        Launch launch = launches.getLaunches().get(0);
                        Timber.v("Launch: %s",launch.getName() );
                        dataSaver.saveLaunchToRealm(launch);
                        networkCallback.onSuccess(launch);
                    } else {
                        networkCallback.onNetworkFailure(404);
                    }

                } else {
                    SpaceLaunchNowError error = ErrorUtil.parseSpaceLaunchNowError(response);
                    if (error.getMessage() != null && error.getMessage().contains("None found")) {
                        final Launch launch = realm.where(Launch.class).equalTo("slug", slug).findFirst();
                        if (launch != null) {
                            realm.executeTransaction(realm -> launch.deleteFromRealm());
                        }
                        networkCallback.onLaunchDeleted();
                    } else {
                        networkCallback.onNetworkFailure(response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                networkCallback.onFailure(t);
            }
        });
    }
}
