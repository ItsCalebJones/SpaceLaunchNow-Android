package me.calebjones.spacelaunchnow.content.interfaces;

import java.util.List;

import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.content.responses.LaunchResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LibraryRequestInterface {

    @GET("launch?next=10&mode=verbose")
    Call<LaunchResponse> getNextLaunches();

    @GET("launch/{launchID}?mode=verbose")
    Call<LaunchResponse> getNextLaunchByID(@Path("launchID") int launchID);
}
