package me.calebjones.spacelaunchnow.data.networking.interfaces;

import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchWearResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WearService {

    String version = "1.3";

    @GET(version + "/launch?mode=verbose")
    Call<LaunchWearResponse> getWearNextLaunch(@Query("startdate") String date, @Query("lsp") int agency, @Query("limit") int limit);

    @GET(version + "/launch?mode=verbose")
    Call<LaunchWearResponse> getWearNextLaunch(@Query("startdate") String date, @Query("limit") int limit);

    @GET(version + "/launch/{id}/mode=verbose")
    Call<LaunchWearResponse> getWearLaunchByID(@Path("id") Integer id);

//TODO Once LL fixes issue with returning full lsp object return to this maybe
//    @GET(version + "/launch?fields=id,net,status,name")
//    Call<LaunchWearResponse> getWearNextLaunch(@Query("startdate") String date, @Query("lsp") int agency, @Query("limit") int limit);
//
//    @GET(version + "/launch?fields=id,net,status,name")
//    Call<LaunchWearResponse> getWearNextLaunch(@Query("startdate") String date, @Query("limit") int limit);
}
