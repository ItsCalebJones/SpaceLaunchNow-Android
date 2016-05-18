package me.calebjones.spacelaunchnow.content.interfaces;

import me.calebjones.spacelaunchnow.content.responses.LaunchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LibraryRequestInterface {

    String version = "1.2";

    //Get Launches Methods

    @GET(version + "/launch/next/1000&mode=verbose")
    Call<LaunchResponse> getUpcomingLaunches();

    @GET("dev/launch/next/1000&mode=verbose")
    Call<LaunchResponse> getDebugUpcomingLaunches();

    @GET(version + "/launch/{start_date}/{end_date}/?sort=desc&limit=1000")
    Call<LaunchResponse> getPreviousLaunches(@Path("start_date") String start_date,
                                             @Path("end_date") String end_date);

    @GET("dev/launch/{start_date}/{end_date}/?sort=desc&limit=1000")
    Call<LaunchResponse> getDebugPreviousLaunches(@Path("start_date") String start_date,
                                                  @Path("end_date") String end_date);

    @GET(version + "/launch?next=10&mode=verbose")
    Call<LaunchResponse> getNextLaunches();

    @GET("dev/launch?next=10&mode=verbose")
    Call<LaunchResponse> getDebugNextLaunches();

    @GET(version + "/launch/{launchID}?mode=verbose")
    Call<LaunchResponse> getLaunchByID(@Path("launchID") int launchID);

    @GET("dev/launch/{launchID}?mode=verbose")
    Call<LaunchResponse> getDebugLaunchByID(@Path("launchID") int launchID);

    //TODO Create/Replace Launch response with MissionResponse
    //Get Missions Methods

    @GET(version + "/mission?next=10&mode=verbose")
    Call<LaunchResponse> getAllMisisons();

    @GET("dev/mission?next=10&mode=verbose")
    Call<LaunchResponse> getDebugAllMissions();

    @GET(version + "/mission/{missionID}?mode=verbose")
    Call<LaunchResponse> getMissionByID(@Path("missionID") int missionID);

    @GET("dev/mission/{missionID}?mode=verbose")
    Call<LaunchResponse> getDebugMissionByID(@Path("missionID") int missionID);

    //TODO Create/Replace Launch response with AgencyResponse
    //Get Agency Methods

    @GET(version + "/agency?next=10&mode=verbose")
    Call<LaunchResponse> getAllAgency();

    @GET("dev/agency?next=10&mode=verbose")
    Call<LaunchResponse> getDebugAllAgency();

    @GET(version + "/agency/{agencyID}?mode=verbose")
    Call<LaunchResponse> getAgencyByID(@Path("agencyID") int agencyID);

    @GET("dev/agency/{agencyID}?mode=verbose")
    Call<LaunchResponse> getDebugAgencyByID(@Path("agencyID") int agencyID);

    //TODO Create/Replace Launch response with VehiclesResponse
    //Get Vehicles Methods

    @GET(version + "/rocket?next=10&mode=verbose")
    Call<LaunchResponse> getAllVehicles();

    @GET("dev/rocket?next=10&mode=verbose")
    Call<LaunchResponse> getDebugAllVehicles();

    @GET(version + "/rocket/{vehicleID}?mode=verbose")
    Call<LaunchResponse> getVehiclesByID(@Path("vehicleID") int vehicleID);

    @GET("dev/rocket/{vehicleID}?mode=verbose")
    Call<LaunchResponse> getDebugVehiclesByID(@Path("vehicleID") int vehicleID);
}
