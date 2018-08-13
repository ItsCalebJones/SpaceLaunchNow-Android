package me.calebjones.spacelaunchnow.data.networking.interfaces;

import me.calebjones.spacelaunchnow.data.BuildConfig;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.networking.responses.base.AgencyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.OrbiterResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpaceLaunchNowService {

    String version = "3.0.0";

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/orbiters/")
    Call<OrbiterResponse> getOrbiter();

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/agencies/")
    Call<AgencyResponse> getAgencies(@Query("featured") boolean featured, @Query("mode") String mode);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launchers_string/")
    Call<VehicleResponse> getVehiclesByAgency(@Query("launch_agency__name") String agency);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launchers_string/")
    Call<VehicleResponse> getVehicle(@Query("full_name") String vehicle);


    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/upcoming/")
    Call<LaunchResponse> getUpcomingLaunches(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("mode") String mode, @Query("search") String search);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/previous/")
    Call<LaunchResponse> getPreviousLaunches(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("mode") String mode, @Query("search") String search);


    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/{id}/")
    Call<Launch> getLaunchById(@Path("id") int id);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/")
    Call<LaunchResponse> getLaunchesByDate(@Query("net__lte") String startDate, @Query("net__gte") String endDate, int offset);
}
