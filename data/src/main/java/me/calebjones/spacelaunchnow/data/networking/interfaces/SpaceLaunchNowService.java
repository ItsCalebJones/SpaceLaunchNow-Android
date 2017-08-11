package me.calebjones.spacelaunchnow.data.networking.interfaces;

import me.calebjones.spacelaunchnow.data.networking.responses.base.LauncherResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.OrbiterResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SpaceLaunchNowService {

    String version = "v1";

    @GET(version + "/orbiters")
    Call<OrbiterResponse> getOrbiter();

    @GET(version + "/launchers")
    Call<LauncherResponse> getLaunchers();

    @GET(version + "/launcher_details")
    Call<VehicleResponse> getVehicles();
}
