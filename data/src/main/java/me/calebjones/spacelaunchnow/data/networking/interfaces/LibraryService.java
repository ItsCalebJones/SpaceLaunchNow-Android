package me.calebjones.spacelaunchnow.data.networking.interfaces;

import me.calebjones.spacelaunchnow.data.BuildConfig;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchResponse;
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
    @GET("launch/next/100?mode=verbose")
    Call<LaunchResponse> getUpcomingLaunchesAll(@Query("offset") int offset);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("launch/next/50?mode=verbose")
    Call<LaunchResponse> getUpcomingLaunches(@Query("offset") int offset);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("launch/{start_date}/{end_date}?mode=verbose")
    Call<LaunchResponse> getNextUpcomingLaunches(@Path("start_date") String start_date,
                                                 @Path("end_date") String end_date,
                                                 @Query("offset") int offset,
                                                 @Query("limit") int limit);

    @Headers({
            "User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME
    })
    @GET("launch?limit=100&mode=verbose")
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

}
