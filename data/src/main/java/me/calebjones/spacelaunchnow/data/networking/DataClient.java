package me.calebjones.spacelaunchnow.data.networking;

import android.support.annotation.NonNull;

import java.io.IOException;

import me.calebjones.spacelaunchnow.data.helpers.Utils;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.data.networking.interfaces.LibraryService;
import me.calebjones.spacelaunchnow.data.networking.interfaces.SpaceLaunchNowService;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.AgencyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LocationResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.MissionResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.PadResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.RocketFamilyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.RocketResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

public class DataClient {

    //    private final String mCacheControl;
    private final LibraryService libraryServiceThreaded;
    private final LibraryService libraryService;
    private final SpaceLaunchNowService spaceLaunchNowService;
    private static DataClient mInstance;
    private Retrofit libraryRetrofit;
    private Retrofit libraryRetrofitThreaded;
    private Retrofit spaceLaunchNowRetrofit;

    private DataClient(String version) {

        //TODO figure out caching strategy
//        CacheControl cacheControl =
//                new CacheControl.Builder().maxAge(forecastConfiguration.getCacheMaxAge(), TimeUnit.SECONDS)
//                        .build();
//        mCacheControl = cacheControl.toString();
        libraryRetrofit = RetrofitBuilder.getLibraryRetrofit(version);
        libraryRetrofitThreaded = RetrofitBuilder.getLibraryRetrofitThreaded(version);
        spaceLaunchNowRetrofit = RetrofitBuilder.getSpaceLaunchNowRetrofit();

        libraryService = libraryRetrofit.create(LibraryService.class);
        libraryServiceThreaded = libraryRetrofitThreaded.create(LibraryService.class);
        spaceLaunchNowService = spaceLaunchNowRetrofit.create(SpaceLaunchNowService.class);

    }

    public Retrofit getLibraryRetrofit() {
        return libraryRetrofit;
    }

    public Retrofit getSpaceLaunchNowRetrofit() {
        return libraryRetrofit;
    }

    /**
     * Applications must call create to configure the DataClient singleton
     */
    public static void create(String version) {
        mInstance = new DataClient(version);
    }

    /**
     * Singleton accessor
     * <p/>
     * Will throw an exception if {@link #create(String version)} was never called
     *
     * @return the DataClient singleton
     */
    public static DataClient getInstance() {
        if (mInstance == null) {
            throw new AssertionError("Did you forget to call create() ?");
        }
        return mInstance;
    }

    public Call<LaunchResponse> getLaunchById(int launchID, boolean isUIThread, @NonNull Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call;
        if (isUIThread) {
            call = libraryService.getLaunchByID(launchID);
        } else {
            call = libraryServiceThreaded.getLaunchByID(launchID);
        }

        call.enqueue(callback);

        Timber.v("Creating getLaunchByID for Launch: %s", launchID);

        return call;
    }

    public Launch[] getLaunchByIdForNotification(int launchID) throws IOException {
        Call<LaunchResponse> call = libraryService.getLaunchByID(launchID);
        Response<LaunchResponse> response = call.execute();

        // Here call newPostResponse.code() to get response code
        int statusCode = response.code();
        if (statusCode == 200) {
            if (response.isSuccessful()) {
                Launch[] launches;
                try {
                    launches = response.body().getLaunches();
                } catch (NullPointerException e){
                    return null;
                }
                if (launches != null) {
                    return launches;
                }
            }
        }
        return null;
    }

    public Call<LaunchResponse> getNextUpcomingLaunchesMini(Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = libraryServiceThreaded.getNextUpcomingLaunchesMini(Utils.getStartDate(-1), Utils.getEndDate(10));

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getNextUpcomingLaunches(int offset, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = libraryServiceThreaded.getNextUpcomingLaunches(Utils.getStartDate(-1), Utils.getEndDate(10), offset);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getUpcomingLaunches(int offset, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = libraryServiceThreaded.getUpcomingLaunches(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getUpcomingLaunchesAll(int offset, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = libraryServiceThreaded.getUpcomingLaunchesAll(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getLaunchesByDate(String startDate, String endDate, int offset, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = libraryServiceThreaded.getLaunchesByDate(startDate, endDate, offset);

        call.enqueue(callback);

        return call;
    }

    public Call<AgencyResponse> getAllAgencies(int offset, Callback<AgencyResponse> callback) {
        Call<AgencyResponse> call = libraryServiceThreaded.getAllAgency(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<MissionResponse> getAllMissions(int offset, Callback<MissionResponse> callback) {
        Call<MissionResponse> call = libraryServiceThreaded.getAllMisisons(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<LocationResponse> getAllLocations(int offset, Callback<LocationResponse> callback) {
        Call<LocationResponse> call = libraryServiceThreaded.getLocations(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<LocationResponse> getLocationById(int locationID, Callback<LocationResponse> callback) {
        Call<LocationResponse> call = libraryServiceThreaded.getLocationsById(locationID);

        call.enqueue(callback);

        return call;
    }

    public Call<PadResponse> getAllPads(int offset, Callback<PadResponse> callback) {
        Call<PadResponse> call = libraryServiceThreaded.getPads(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<PadResponse> getPadsByID(int padID, Callback<PadResponse> callback) {
        Call<PadResponse> call = libraryServiceThreaded.getPadsById(padID);

        call.enqueue(callback);

        return call;
    }

    public Call<VehicleResponse> getVehicles(Callback<VehicleResponse> callback) {
        Call<VehicleResponse> call = spaceLaunchNowService.getVehicles();

        call.enqueue(callback);

        return call;
    }

    public Call<RocketResponse> getRockets(int offset, Callback<RocketResponse> callback) {
        Call<RocketResponse> call = libraryServiceThreaded.getAllRockets(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<RocketFamilyResponse> getRocketFamily(int offset, Callback<RocketFamilyResponse> callback) {
        Call<RocketFamilyResponse> call = libraryServiceThreaded.getAllRocketFamily(offset);

        call.enqueue(callback);

        return call;
    }
}
