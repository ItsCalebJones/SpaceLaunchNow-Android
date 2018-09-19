package me.calebjones.spacelaunchnow.data.networking;

import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.networking.interfaces.SpaceLaunchNowService;
import me.calebjones.spacelaunchnow.data.networking.responses.base.AgencyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchListResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.LaunchResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class DataClient {

    private final SpaceLaunchNowService spaceLaunchNowService;
    private static DataClient mInstance;
    private Retrofit libraryRetrofit;
    private Retrofit libraryRetrofitThreaded;


    private Retrofit spaceLaunchNowRetrofit;

    private DataClient(String version, String token, boolean debug) {

        libraryRetrofit = RetrofitBuilder.getLibraryRetrofit(version);
        libraryRetrofitThreaded = RetrofitBuilder.getLibraryRetrofitThreaded(version);
        spaceLaunchNowRetrofit = RetrofitBuilder.getSpaceLaunchNowRetrofit(token, debug);
        spaceLaunchNowService = spaceLaunchNowRetrofit.create(SpaceLaunchNowService.class);

    }

    public Retrofit getLibraryRetrofit() {
        return libraryRetrofit;
    }

    public Retrofit getSpaceLaunchNowRetrofit() {
        return spaceLaunchNowRetrofit;
    }

    /**
     * Applications must call create to configure the DataClient singleton
     */
    public static void create(String version, String token, boolean debug) {
        mInstance = new DataClient(version, token, debug);
    }

    /**
     * Singleton accessor
     * <p/>
     * Will throw an exception if {@link #create(String version, String token, boolean debug)} was never called
     *
     * @return the DataClient singleton
     */
    public static DataClient getInstance() {
        if (mInstance == null) {
            throw new AssertionError("Did you forget to call create() ?");
        }
        return mInstance;
    }

    public Call<Launch> getLaunchById(int launchID, Callback<Launch> callback) {
        Call<Launch> call;

        call = spaceLaunchNowService.getLaunchById(launchID, "detailed");

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getNextUpcomingLaunchesMini(int limit, int offset, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = spaceLaunchNowService.getUpcomingLaunches(limit, offset, "list", null, null, null);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getNextUpcomingLaunches(int limit, int offset, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = spaceLaunchNowService.getUpcomingLaunches(limit, offset, "detailed", null, null, null);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getUpcomingLaunches(int limit, int offset, String search, String lspName, Integer launchId, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = spaceLaunchNowService.getUpcomingLaunches(limit, offset, "detailed", search, lspName, launchId);

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

    public Call<VehicleResponse> getVehiclesByAgency(String agency, Callback<VehicleResponse> callback) {
        Call<VehicleResponse> call = spaceLaunchNowService.getVehiclesByAgency(agency);

        call.enqueue(callback);

        return call;
    }

    public Call<AgencyResponse> getFeaturedAgencies(Callback<AgencyResponse> callback) {
        Call<AgencyResponse> call = spaceLaunchNowService.getAgencies(true, "list");

        call.enqueue(callback);

        return call;
    }
}
