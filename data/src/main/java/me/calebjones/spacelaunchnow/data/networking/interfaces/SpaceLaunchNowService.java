package me.calebjones.spacelaunchnow.data.networking.interfaces;

import me.calebjones.spacelaunchnow.data.BuildConfig;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LauncherResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.OrbiterResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface SpaceLaunchNowService {

    String version = "v1";
    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET(version + "/orbiters/")
    Call<OrbiterResponse> getOrbiter();

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET(version + "/agency/")
    Call<LauncherResponse> getAgencies();

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launcher_details/")
    Call<VehicleResponse> getVehicles(@Query("launch_agency__agency") String agency);
}
