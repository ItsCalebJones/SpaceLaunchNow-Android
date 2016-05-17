package me.calebjones.spacelaunchnow.content.interfaces;

import me.calebjones.spacelaunchnow.content.responses.LaunchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LibraryRequestInterface {

    @GET("1.2/launch?next=10&mode=verbose")
    Call<LaunchResponse> getNextLaunches();

    @GET("dev/launch?next=10&mode=verbose")
    Call<LaunchResponse> getDebugNextLaunches();

    @GET("1.2/launch/{launchID}?mode=verbose")
    Call<LaunchResponse> getNextLaunchByID(@Path("launchID") int launchID);

    @GET("dev/launch/{launchID}?mode=verbose")
    Call<LaunchResponse> getDebugNextLaunchByID(@Path("launchID") int launchID);
}
