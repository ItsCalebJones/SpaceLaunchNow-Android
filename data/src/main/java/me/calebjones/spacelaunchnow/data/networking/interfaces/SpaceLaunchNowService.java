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

    String version = "2.0.0";
    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET(version + "/orbiters/")
    Call<OrbiterResponse> getOrbiter();

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET(version + "/agencies/")
    Call<LauncherResponse> getVehicleAgencies(@Query("featured") boolean featured);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launchers/")
    Call<VehicleResponse> getVehiclesByAgency(@Query("launch_agency__name") String agency);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launchers/")
    Call<VehicleResponse> getVehicle(@Query("full_name") String vehicle);
}
