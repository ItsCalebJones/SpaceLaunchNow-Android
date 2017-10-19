package me.calebjones.spacelaunchnow.data.networking.interfaces;

import me.calebjones.spacelaunchnow.data.BuildConfig;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.AgencyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LocationResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.MissionResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.PadResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.RocketFamilyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.RocketResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LibraryService {

    //Get Launches Methods
    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("launch/next/1000&mode=verbose&limit=100")
    Call<LaunchResponse> getUpcomingLaunchesAll(@Query("offset") int offset);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("launch/next/50&mode=verbose&limit=10")
    Call<LaunchResponse> getUpcomingLaunches(@Query("offset") int offset);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("launch/{start_date}/{end_date}?mode=verbose")
    Call<LaunchResponse> getNextUpcomingLaunches(@Path("start_date") String start_date,
                                                 @Path("end_date") String end_date,
                                                 @Query("offset") int offset);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("launch?fields=net,name,location,status&limit=1000")
    Call<LaunchResponse> getLaunchesByDate(@Query("startdate") String start_date,
                                           @Query("enddate") String end_date,
                                           @Query("offset") int offset);
    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("launch?next=5&mode=verbose")
    Call<LaunchResponse> getNextLaunches(@Query("offset") int offset);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("launch/{launchID}?mode=verbose")
    Call<LaunchResponse> getLaunchByID(@Path("launchID") int launchID);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("launch?fields=id,net,status")
    Call<LaunchResponse> getNextUpcomingLaunchesMini(@Query("start_date") String start_date,
                                                     @Query("end_date") String end_date);
    //Get Missions Methods
    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("mission?mode=verbose&limit=500")
    Call<MissionResponse> getAllMisisons(@Query("offset") int offset);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("mission/{missionID}?mode=verbose")
    Call<MissionResponse> getMissionByID(@Path("missionID") int missionID);

    //Get Agency Methods
    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("agency?mode=verbose")
    Call<AgencyResponse> getAllAgency(@Query("offset") int offset);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("agency/{agencyID}?mode=verbose")
    Call<AgencyResponse> getAgencyByID(@Path("agencyID") int agencyID);

    //Get Rocket Methods
    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("rocket?mode=verbose")
    Call<RocketResponse> getAllRockets(@Query("offset") int offset);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("rocket/{vehicleID}?mode=verbose")
    Call<RocketResponse> getRocketsById(@Path("vehicleID") int vehicleID);

    //Get Pad Methods
    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("pad?mode=verbose")
    Call<PadResponse> getPads(@Query("offset") int offset);
    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("pad/{padId}?mode=verbose")
    Call<PadResponse> getPadsById(@Path("padId") int padID);

    //Get Location Methods
    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("location?mode=verbose")
    Call<LocationResponse> getLocations(@Query("offset") int offset);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("location/{locationId}?mode=verbose")
    Call<LocationResponse> getLocationsById(@Path("locationId") int locationId);

    //Get RocketFamily
    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("rocketfamily?mode=verbose")
    Call<RocketFamilyResponse> getAllRocketFamily(@Query("offset") int offset);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("rocketfamily/{rocketFamilyId}?mode=verbose")
    Call<RocketFamilyResponse> getRocketFamilyById(@Path("rocketFamilyId") int rocketFamilyId);
}
