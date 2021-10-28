package me.calebjones.spacelaunchnow.data.networking.interfaces;

import me.calebjones.spacelaunchnow.data.BuildConfig;
import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.calebjones.spacelaunchnow.data.models.main.dashboards.Starship;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherConfig;
import me.calebjones.spacelaunchnow.data.models.main.launcher.LauncherStage;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.Spacecraft;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftConfig;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.SpacecraftStage;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Expedition;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import me.calebjones.spacelaunchnow.data.networking.responses.base.AgencyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.AstronautResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.EventResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.ExpeditionResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchListResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LauncherStageResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.SpacecraftConfigResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LauncherConfigResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.SpacecraftResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.SpacecraftStageResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.SpacestationResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpaceLaunchNowService {

    String version = "api/ll/2.2.0";

    // Spacecraft Configs
    // GET: /config/spacecraft
    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_CODE})
    @GET(version + "/config/spacecraft")
    Call<SpacecraftConfigResponse> getSpacecraftConfigs(@Query("limit") int amount, @Query("offset") int offset,
                                                        @Query("search") String search, @Query("launch_agency") int status,
                                                        @Query("in_use") boolean inUse, @Query("human_rated") boolean humanRated);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/config/spacecraft/{id}/")
    Call<SpacecraftConfig> getSpacecraftConfigsById(@Path("id") int id);

    // Launcher Configs
    // GET: /config/launchers
    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/config/launchers")
    Call<LauncherConfigResponse> getLauncherConfigByAgency(@Query("launch_agency__name") String agency);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/config/launchers/")
    Call<LauncherConfigResponse> getLauncherConfigs(@Query("full_name") String vehicle);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/config/launchers/{id}/")
    Call<LauncherConfig> getLauncherConfigById(@Path("id") int id);


    // Agencies
    // GET: /agencies/
    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/agencies/")
    Call<AgencyResponse> getAgencies(@Query("featured") Boolean featured,
                                     @Query("mode") String mode,
                                     @Query("limit") int limit);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/agencies/")
    Call<AgencyResponse> getAgenciesWithOrbiters(@Query("spacecraft") boolean orbiters,
                                                 @Query("limit") int limit);


    // Launches
    // GET: /launch/
    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/upcoming/")
    Call<LaunchResponse> getUpcomingLaunches(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("mode") String mode);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/upcoming/")
    Call<LaunchResponse> getUpcomingLaunches(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("mode") String mode, @Query("search") String search);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/upcoming/")
    Call<LaunchResponse> getUpcomingLaunches(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("mode") String mode, @Query("search") String search,
                                             @Query("lsp__name") String lspName,
                                             @Query("launcher_config__id") Integer launcherId,
                                             @Query("lsp__ids") String lspIds,
                                             @Query("location__ids") String locationIds);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/upcoming/")
    Call<LaunchListResponse> getUpcomingLaunchesMini(@Query("limit") int amount, @Query("offset") int offset,
                                                     @Query("mode") String mode, @Query("search") String search,
                                                     @Query("lsp__name") String lspName, @Query("serial_number") String serialNumber,
                                                     @Query("launcher_config__id") Integer launcherId);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/upcoming/")
    Call<LaunchResponse> getUpcomingLaunches(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("mode") String mode, @Query("search") String search,
                                             @Query("lsp__name") String lspName);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/upcoming/")
    Call<LaunchResponse> getUpcomingLaunches(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("mode") String mode,
                                             @Query("launcher_config__id") Integer launcherId);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/upcoming/")
    Call<LaunchResponse> getUpcomingLaunches(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("mode") String mode, @Query("search") String search,
                                             @Query("lsp__name") String lspName, @Query("lsp__id") Integer lspId,
                                             @Query("launcher_config__id") Integer launcherId);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/previous/")
    Call<LaunchResponse> getPreviousLaunches(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("mode") String mode);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/previous/")
    Call<LaunchResponse> getPreviousLaunches(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("mode") String mode, @Query("search") String search);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/previous/")
    Call<LaunchResponse> getPreviousLaunchesByLspID(@Query("limit") int amount, @Query("offset") int offset,
                                                    @Query("mode") String mode, @Query("search") String search,
                                                    @Query("lsp__id") Integer lspId);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/previous/")
    Call<LaunchResponse> getPreviousLaunchesByLspName(@Query("limit") int amount, @Query("offset") int offset,
                                                      @Query("mode") String mode, @Query("search") String search,
                                                      @Query("lsp__name") String lspName);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/previous/")
    Call<LaunchResponse> getPreviousLaunches(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("mode") String mode, @Query("search") String search,
                                             @Query("lsp__name") String lspName, @Query("launcher_config__id") Integer lspId);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/previous/")
    Call<LaunchListResponse> getPreviousLaunchesMini(@Query("limit") int amount, @Query("offset") int offset,
                                                     @Query("mode") String mode, @Query("search") String search,
                                                     @Query("lsp__name") String lspName, @Query("serial_number") String serialNumber,
                                                     @Query("launcher_config__id") Integer lspId);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/previous/")
    Call<LaunchResponse> getPreviousLaunchesByLauncherID(@Query("limit") int amount, @Query("offset") int offset,
                                                         @Query("mode") String mode, @Query("search") String search,
                                                         @Query("launcher_config__id") Integer launcherId);


    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/{id}/")
    Call<Launch> getLaunchById(@Path("id") String id, @Query("mode") String mode);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/")
    Call<LaunchResponse> getLaunchBySlug(@Query("slug") String slug, @Query("mode") String mode);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launch/")
    Call<LaunchResponse> getLaunchesByDate(@Query("limit") int amount, @Query("offset") int offset,
                                           @Query("net__lte") String startDate, @Query("net__gte") String endDate,
                                           @Query("launcher_config__id") Integer launcherId);


    // Astronaut
    // GET: /astronaut/
    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/astronaut/{id}/?mode=launchlist")
    Call<Astronaut> getAstronautsById(@Path("id") int id);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/astronaut/")
    Call<AstronautResponse> getAstronauts(@Query("limit") int amount, @Query("offset") int offset,
                                          @Query("search") String search, @Query("status") Integer status,
                                          @Query("status_ids") String statuses, @Query("ordering") String order);

    // Spacestation
    // GET: /spacestations/
    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/spacestation/{id}/")
    Call<Spacestation> getSpacestationById(@Path("id") int id);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/spacestation/")
    Call<SpacestationResponse> getSpacestations(@Query("limit") int amount, @Query("offset") int offset,
                                               @Query("search") String search, @Query("status") Integer status,
                                                @Query("ordering") String ordering);

    // Spacecraft
    // GET: /spacecraft/
    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/spacecraft/{id}/")
    Call<Spacecraft> getSpacecraftById(@Path("id") int id);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/spacecraft/")
    Call<SpacecraftResponse> getSpacecraft(@Query("limit") int amount, @Query("offset") int offset,
                                           @Query("search") String search, @Query("status") Integer status,
                                           @Query("spacecraft_config") Integer spacecraft);

    // Spacecraft Flight
    // GET: /spacecraft/flight/
    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/spacecraft/flight/{id}/")
    Call<SpacecraftStage> getSpacecraftFlightById(@Path("id") int id);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/spacecraft/flight/")
    Call<SpacecraftStageResponse> getSpacecraftFlights(@Query("limit") int amount, @Query("offset") int offset);


    // Expedition
    // GET: /expedition/
    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/expedition/{id}/")
    Call<Expedition> getExpeditionById(@Path("id") int id);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/expedition/")
    Call<ExpeditionResponse> getExpedition(@Query("limit") int amount, @Query("offset") int offset,
                                           @Query("crew__astronaut") Integer astronaut,
                                           @Query("crew__astronaut__agency") Integer agency,
                                           @Query("space_station") Integer spacestation,
                                           @Query("end__lte") String endDate);


    // Launcher
    // GET: /launcher/
    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launcher/{id}/")
    Call<LauncherStage> getLauncherById(@Path("id") int id);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/launcher/")
    Call<LauncherStageResponse> getLaunchers(@Query("limit") int amount, @Query("offset") int offset,
                                             @Query("serial_number") String serialNumber,
                                             @Query("flight_proven") Boolean flightProven,
                                             @Query("launcher_config") Integer launcherConfig,
                                             @Query("launcher_config__launch_agency") Integer agency,
                                             @Query("mode") String mode);


    // Events
    // GET: /event
    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/event/upcoming/")
    Call<EventResponse> getUpcomingEvents(
            @Query("limit") int amount,
            @Query("offset") int offset,
            @Query("mode") String mode
    );

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/event/{id}/")
    Call<Event> getEventById(@Path("id") int id, @Query("mode") String mode);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/event/")
    Call<EventResponse> getEventBySlug(@Query("slug") String slug, @Query("mode") String mode);

    @Headers({"User-Agent: SpaceLaunchNow-" + BuildConfig.VERSION_NAME})
    @GET(version + "/dashboard/starship/")
    Call<Starship> getStarshipDashboard(@Query("mode") String mode);
}