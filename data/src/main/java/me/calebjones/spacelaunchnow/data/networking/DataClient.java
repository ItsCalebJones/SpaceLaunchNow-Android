package me.calebjones.spacelaunchnow.data.networking;

import java.io.IOException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.realm.Realm;
import me.calebjones.spacelaunchnow.data.helpers.Utils;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
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
    private final LibraryService libraryServiceThreadedLowPriority;
    private final LibraryService libraryService;
    private final SpaceLaunchNowService spaceLaunchNowService;
    private static DataClient mInstance;
    private Retrofit libraryRetrofit;
    private Retrofit libraryRetrofitThreaded;

    public LibraryService getLibraryServiceThreaded() {
        return libraryServiceThreaded;
    }

    public LibraryService getLibraryServiceThreadedLowPriority() {
        return libraryServiceThreadedLowPriority;
    }

    public LibraryService getLibraryService() {
        return libraryService;
    }

    public SpaceLaunchNowService getSpaceLaunchNowService() {
        return spaceLaunchNowService;
    }

    private Retrofit spaceLaunchNowRetrofit;
    private BehaviorSubject<Boolean> networkInUse;



    private DataClient(String version, String token) {

        //TODO figure out caching strategy
//        CacheControl cacheControl =
//                new CacheControl.Builder().maxAge(forecastConfiguration.getCacheMaxAge(), TimeUnit.SECONDS)
//                        .build();
//        mCacheControl = cacheControl.toString();
        libraryRetrofit = RetrofitBuilder.getLibraryRetrofit(version);
        libraryRetrofitThreaded = RetrofitBuilder.getLibraryRetrofitThreaded(version);
        spaceLaunchNowRetrofit = RetrofitBuilder.getSpaceLaunchNowRetrofit(token);
        libraryService = libraryRetrofit.create(LibraryService.class);
        libraryServiceThreaded = libraryRetrofitThreaded.create(LibraryService.class);
        spaceLaunchNowService = spaceLaunchNowRetrofit.create(SpaceLaunchNowService.class);
        libraryServiceThreadedLowPriority = RetrofitBuilder.getLibraryRetrofitLowestThreaded(version).create(LibraryService.class);

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
    public static void create(String version, String token) {
        mInstance = new DataClient(version, token);
    }

    /**
     * Singleton accessor
     * <p/>
     * Will throw an exception if {@link #create(String version, String token)} was never called
     *
     * @return the DataClient singleton
     */
    public static DataClient getInstance() {
        if (mInstance == null) {
            throw new AssertionError("Did you forget to call create() ?");
        }
        return mInstance;
    }

    public Call<LaunchResponse> getLaunchById(int launchID, boolean isUIThread, Callback<LaunchResponse> callback) {
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

    public Call<LaunchResponse> getNextUpcomingLaunchesMini(Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = libraryServiceThreaded.getNextUpcomingLaunchesMini(Utils.getStartDate(-1), Utils.getEndDate(10));

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getNextUpcomingLaunches(int offset, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = libraryService.getNextUpcomingLaunches(Utils.getStartDate(-2), Utils.getEndDate(30), offset, 10);

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
        Call<LaunchResponse> call = libraryServiceThreadedLowPriority.getLaunchesByDate(startDate, endDate, offset);

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

    public Call<VehicleResponse> getVehiclesByAgency(String agency, Callback<VehicleResponse> callback) {
        Call<VehicleResponse> call = spaceLaunchNowService.getVehiclesByAgency(agency);

        call.enqueue(callback);

        return call;
    }

    public Call<VehicleResponse> getVehicles(String vehicle, Callback<VehicleResponse> callback){
        Call<VehicleResponse> call = spaceLaunchNowService.getVehicle(vehicle);

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
