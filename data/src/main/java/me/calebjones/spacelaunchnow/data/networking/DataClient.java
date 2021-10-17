package me.calebjones.spacelaunchnow.data.networking;

import java.io.IOException;

import me.calebjones.spacelaunchnow.data.models.main.Event;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.astronaut.Astronaut;
import me.calebjones.spacelaunchnow.data.models.main.dashboards.Starship;
import me.calebjones.spacelaunchnow.data.models.main.spacecraft.Spacecraft;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Expedition;
import me.calebjones.spacelaunchnow.data.models.main.spacestation.Spacestation;
import me.calebjones.spacelaunchnow.data.networking.interfaces.SpaceLaunchNowService;
import me.calebjones.spacelaunchnow.data.networking.responses.base.AgencyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.AstronautResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.EventResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.ExpeditionResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchListResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LauncherConfigResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.SpacecraftResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.SpacestationResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class DataClient {

    private final SpaceLaunchNowService spaceLaunchNowService;
    private static DataClient mInstance;


    private Retrofit spaceLaunchNowRetrofit;

    private DataClient(String token, String endpoint) {
        spaceLaunchNowRetrofit = RetrofitBuilder.getSpaceLaunchNowRetrofit(token, endpoint);
        spaceLaunchNowService = spaceLaunchNowRetrofit.create(SpaceLaunchNowService.class);
    }

    public Retrofit getSpaceLaunchNowRetrofit() {
        return spaceLaunchNowRetrofit;
    }

    /**
     * Applications must call create to configure the DataClient singleton
     */
    public static void create(String token, String endpoint) {
        mInstance = new DataClient(token, endpoint);
    }

    /**
     * Singleton accessor
     * <p/>
     * Will throw an exception if {@link #create(String token, String endpoint)} was never called
     *
     * @return the DataClient singleton
     */
    public static DataClient getInstance() {
        if (mInstance == null) {
            throw new AssertionError("Did you forget to call create() ?");
        }
        return mInstance;
    }

    public Call<Launch> getLaunchById(String launchID, Callback<Launch> callback) {
        Call<Launch> call;

        call = spaceLaunchNowService.getLaunchById(launchID, "detailed");

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getLaunchBySlug(String slug, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call;

        call = spaceLaunchNowService.getLaunchBySlug(slug, "detailed");

        call.enqueue(callback);

        return call;
    }

    public Call<Starship> getStarshipDashboard(Callback<Starship> callback) {
        Call<Starship> call;

        call = spaceLaunchNowService.getStarshipDashboard("list");

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getNextUpcomingLaunchesForWidgets(int limit, int offset) {
        Call<LaunchResponse> call = spaceLaunchNowService.getUpcomingLaunches(limit, offset, "detailed", null, null, null, null, null);
        return call;
    }

    public Call<LaunchResponse> getNextUpcomingLaunches(int limit, int offset, String location_ids, String lsp_ids, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = spaceLaunchNowService.getUpcomingLaunches(limit, offset, "detailed", null, null, null, lsp_ids, location_ids);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getNextUpcomingLaunchesSynchronous(int limit, int offset, String location_ids, String lsp_ids) {
        Call<LaunchResponse> call = spaceLaunchNowService.getUpcomingLaunches(limit, offset, "detailed", null, null, null, lsp_ids, location_ids);
        return call;
    }

    public Call<LaunchResponse> getUpcomingLaunches(int limit, int offset, String search, String lspName, Integer launchId, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = spaceLaunchNowService.getUpcomingLaunches(limit, offset, "detailed", search, lspName, launchId, null, null);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchListResponse> getUpcomingLaunchesMini(int limit, int offset, String search, String lspName, String serialNumber, Integer launchId, Callback<LaunchListResponse> callback) {
        Call<LaunchListResponse> call = spaceLaunchNowService.getUpcomingLaunchesMini(limit, offset, "list", search, lspName, serialNumber, launchId);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getPreviousLaunches(int limit, int offset, String search, String lspName, Integer launchId, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = spaceLaunchNowService.getPreviousLaunches(limit, offset, "detailed", search, lspName, launchId);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchListResponse> getPreviousLaunchesMini(int limit, int offset, String search, String lspName, String serialNumber, Integer launchId, Callback<LaunchListResponse> callback) {
        Call<LaunchListResponse> call = spaceLaunchNowService.getPreviousLaunchesMini(limit, offset, "list", search, lspName, serialNumber, launchId);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getLaunchesByDate(int limit, int offset,String startDate, String endDate, Integer launchId, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = spaceLaunchNowService.getLaunchesByDate(limit, offset, startDate, endDate, launchId);

        call.enqueue(callback);

        return call;
    }

    public Call<LauncherConfigResponse> getVehiclesByAgency(String agency, Callback<LauncherConfigResponse> callback) {
        Call<LauncherConfigResponse> call = spaceLaunchNowService.getLauncherConfigByAgency(agency);

        call.enqueue(callback);

        return call;
    }

    public Call<AgencyResponse> getFeaturedAgencies(Callback<AgencyResponse> callback, int limit) {
        Call<AgencyResponse> call = spaceLaunchNowService.getAgencies(true, "list", limit);

        call.enqueue(callback);

        return call;
    }

    public Call<AstronautResponse> getAstronauts(int limit, int offset, String search, Integer status, String statuses, Callback<AstronautResponse> callback) {
        Call<AstronautResponse> call = spaceLaunchNowService.getAstronauts(limit, offset, search, status, statuses, "name");

        call.enqueue(callback);

        return call;
    }

    public Call<Astronaut> getAstronautsById(int id, Callback<Astronaut> callback) {
        Call<Astronaut> call = spaceLaunchNowService.getAstronautsById(id);

        call.enqueue(callback);

        return call;
    }

    public Call<SpacestationResponse> getSpacestations(int limit, int offset, String search, Integer status, Callback<SpacestationResponse> callback) {
        Call<SpacestationResponse> call = spaceLaunchNowService.getSpacestations(limit, offset, search, status, "status");

        call.enqueue(callback);

        return call;
    }

    public Call<Spacestation> getSpacestationById(int id, Callback<Spacestation> callback) {
        Call<Spacestation> call = spaceLaunchNowService.getSpacestationById(id);

        call.enqueue(callback);

        return call;
    }

    public Call<ExpeditionResponse> getExpeditions(int limit, int offset, Integer astronaut, Integer agency, Integer spacestation, String endDate, Callback<ExpeditionResponse> callback) {
        Call<ExpeditionResponse> call = spaceLaunchNowService.getExpedition(limit, offset, astronaut, agency, spacestation, endDate);

        call.enqueue(callback);

        return call;
    }

    public Call<Expedition> getExpeditionById(int id, Callback<Expedition> callback) {
        Call<Expedition> call = spaceLaunchNowService.getExpeditionById(id);

        call.enqueue(callback);

        return call;
    }


    public Call<SpacecraftResponse> getSpacecraft(int limit, int offset, Callback<SpacecraftResponse> callback) {
        Call<SpacecraftResponse> call = spaceLaunchNowService.getSpacecraft(limit, offset, null, null, null);

        call.enqueue(callback);

        return call;
    }

    public Call<Spacecraft> getSpacecraftById(int id, Callback<Spacecraft> callback) {
        Call<Spacecraft> call = spaceLaunchNowService.getSpacecraftById(id);

        call.enqueue(callback);

        return call;
    }

    public Call<EventResponse> getUpcomingEvents(int limit, int offset, Callback<EventResponse> callback) {
        Call<EventResponse> call = spaceLaunchNowService.getUpcomingEvents(limit, offset, "list");

        call.enqueue(callback);

        return call;
    }

    public Call<Event> getEventById(int id, Callback<Event> callback) {
        Call<Event> call = spaceLaunchNowService.getEventById(id, "list");

        call.enqueue(callback);

        return call;
    }

    public Call<EventResponse> getEventBySlug(String slug, Callback<EventResponse> callback) {
        Call<EventResponse> call = spaceLaunchNowService.getEventBySlug(slug, "list");

        call.enqueue(callback);

        return call;
    }

    public Call<EventResponse> getEventBySlug(String slug, Callback<EventResponse> callback) {
        Call<EventResponse> call = spaceLaunchNowService.getEventBySlug(slug);

        call.enqueue(callback);

        return call;
    }
}
