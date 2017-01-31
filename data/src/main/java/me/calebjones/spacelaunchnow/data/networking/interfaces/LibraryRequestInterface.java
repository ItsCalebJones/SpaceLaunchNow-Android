package me.calebjones.spacelaunchnow.data.networking.interfaces;

import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.AgencyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LocationResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.MissionResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.PadResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.RocketFamilyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.RocketResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LibraryRequestInterface {

    String version = "1.2.1";

    //Get Launches Methods

    @GET(version + "/launch/next/1000&mode=verbose&limit=100")
    Call<LaunchResponse> getUpcomingLaunchesAll(@Query("offset") int offset);

    @GET("dev/launch/next/1000&mode=verbose&limit=100")
    Call<LaunchResponse> getDebugUpcomingLaunchesAll(@Query("offset") int offset);

    @GET(version + "/launch/next/25&mode=verbose")
    Call<LaunchResponse> getUpcomingLaunches(@Query("offset") int offset);

    @GET("dev/launch/next/25&mode=verbose")
    Call<LaunchResponse> getDebugUpcomingLaunches(@Query("offset") int offset);

    @GET(version + "/launch/{start_date}/{end_date}/?limit=300")
    Call<LaunchResponse> getLaunchesByDate(@Path("start_date") String start_date,
                                           @Path("end_date") String end_date,
                                           @Query("offset") int offset);

    @GET("dev/launch/{start_date}/{end_date}/?limit=300")
    Call<LaunchResponse> getDebugLaunchesByDate(@Path("start_date") String start_date,
                                                @Path("end_date") String end_date,
                                                @Query("offset") int offset);

    @GET(version + "/launch?next=5&mode=verbose")
    Call<LaunchResponse> getNextLaunches(@Query("offset") int offset);

    @GET("dev/launch?next=5&mode=verbose")
    Call<LaunchResponse> getDebugNextLaunches(@Query("offset") int offset);

    @GET(version + "/launch/{launchID}?mode=verbose")
    Call<LaunchResponse> getLaunchByID(@Path("launchID") int launchID);

    @GET("dev/launch/{launchID}?mode=verbose")
    Call<LaunchResponse> getDebugLaunchByID(@Path("launchID") int launchID);

    @GET(version + "/launch?next=10&fields=id,net,status")
    Call<LaunchResponse> getMiniNextLaunch();

    @GET("dev/launch?next=10&fields=id,net,status")
    Call<LaunchResponse> getDebugMiniNextLaunch();

    @GET(version + "/launch?next=25&fields=id,net,name,")
    Call<LaunchResponse> getWearNextLaunch();

    //Get Missions Methods

    @GET(version + "/mission?mode=verbose&limit=500")
    Call<MissionResponse> getAllMisisons(@Query("offset") int offset);

    @GET("dev/mission?mode=verbose&limit=500")
    Call<MissionResponse> getDebugAllMissions(@Query("offset") int offset);

    @GET(version + "/mission/{missionID}?mode=verbose")
    Call<MissionResponse> getMissionByID(@Path("missionID") int missionID);

    @GET("dev/mission/{missionID}?mode=verbose")
    Call<MissionResponse> getDebugMissionByID(@Path("missionID") int missionID);

    //Get Agency Methods

    @GET(version + "/agency?mode=verbose")
    Call<AgencyResponse> getAllAgency(@Query("offset") int offset);

    @GET("dev/agency?mode=verbose")
    Call<AgencyResponse> getDebugAllAgency(@Query("offset") int offset);

    @GET(version + "/agency/{agencyID}?mode=verbose")
    Call<AgencyResponse> getAgencyByID(@Path("agencyID") int agencyID);

    @GET("dev/agency/{agencyID}?mode=verbose")
    Call<AgencyResponse> getDebugAgencyByID(@Path("agencyID") int agencyID);

    //Get Rocket Methods

    @GET(version + "/rocket?mode=verbose")
    Call<RocketResponse> getAllRockets(@Query("offset") int offset);

    @GET("dev/rocket?mode=verbose")
    Call<RocketResponse> getDebugAllRockets(@Query("offset") int offset);

    @GET(version + "/rocket/{vehicleID}?mode=verbose")
    Call<RocketResponse> getRocketsById(@Path("vehicleID") int vehicleID);

    @GET("dev/rocket/{vehicleID}?mode=verbose")
    Call<RocketResponse> getDebugRocketsById(@Path("vehicleID") int vehicleID);

    //Get Pad Methods

    @GET(version + "/pad?mode=verbose")
    Call<PadResponse> getPads(@Query("offset") int offset);

    @GET("dev/pad?mode=verbose")
    Call<PadResponse> getDebugPads(@Query("offset") int offset);

    @GET(version + "/pad/{padId}?mode=verbose")
    Call<PadResponse> getPadsById(@Path("padId") int padID);

    @GET("dev/pad/{padId}?mode=verbose")
    Call<PadResponse> getDebugPadsById(@Path("padId") int padID);

    //Get Location Methods

    @GET(version + "/location?mode=verbose")
    Call<LocationResponse> getLocations(@Query("offset") int offset);

    @GET("dev/location?mode=verbose")
    Call<LocationResponse> getDebugLocations(@Query("offset") int offset);

    @GET(version + "/location/{locationId}?mode=verbose")
    Call<LocationResponse> getLocationsById(@Path("locationId") int locationId);

    @GET("dev/location/{locationId}?mode=verbose")
    Call<LocationResponse> getDebugLocationsById(@Path("locationId") int locationId);

    //Get RocketFamily

    @GET(version + "/rocketfamily?mode=verbose")
    Call<RocketFamilyResponse> getAllRocketFamily(@Query("offset") int offset);

    @GET("dev/rocketfamily?mode=verbose")
    Call<RocketFamilyResponse> getDebugAllRocketFamily(@Query("offset") int offset);

    @GET(version + "/rocketfamily/{rocketfamilyId}?mode=verbose")
    Call<RocketFamilyResponse> getRocketFamilyById(@Path("rocketfamilyId") int rocketfamilyId);

    @GET("dev/rocketfamily/{rocketfamilyId}?mode=verbose")
    Call<RocketFamilyResponse> getDebugRocketFamilyById(@Path("rocketfamilyId") int rocketfamilyId);
}
