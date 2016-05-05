package me.calebjones.spacelaunchnow.utils;

import me.calebjones.spacelaunchnow.content.models.LauncherResponse;
import me.calebjones.spacelaunchnow.content.models.OrbiterResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {

    @GET("orbiter")
    Call<OrbiterResponse> getOrbiter();

    @GET("launchers")
    Call<LauncherResponse> getLaunchers();
}
