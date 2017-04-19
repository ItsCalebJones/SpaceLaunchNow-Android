package me.calebjones.spacelaunchnow.data.networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import me.calebjones.spacelaunchnow.data.helpers.Utils;
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
import retrofit2.Retrofit;
import timber.log.Timber;

public class DataClient {

//    private final String mCacheControl;
    private final LibraryService libraryService;
    private final SpaceLaunchNowService spaceLaunchNowService;
    private static DataClient mInstance;
    private Retrofit libraryRetrofit;
    private Retrofit spaceLaunchNowRetrofit;

    private DataClient(String version){

        //TODO figure out caching strategy
//        CacheControl cacheControl =
//                new CacheControl.Builder().maxAge(forecastConfiguration.getCacheMaxAge(), TimeUnit.SECONDS)
//                        .build();
//        mCacheControl = cacheControl.toString();
        libraryRetrofit = RetrofitBuilder.getLibraryRetrofit(version);
        spaceLaunchNowRetrofit = RetrofitBuilder.getSpaceLaunchNowRetrofit();

        libraryService = libraryRetrofit.create(LibraryService.class);
        spaceLaunchNowService = spaceLaunchNowRetrofit.create(SpaceLaunchNowService.class);

    }

    public Retrofit getLibraryRetrofit(){
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
     * Will throw an exception if {@link #create()} was never called
     *
     * @return the DataClient singleton
     */
    public static DataClient getInstance() {
        if (mInstance == null) {
            throw new AssertionError("Did you forget to call create() ?");
        }
        return mInstance;
    }

    public Call<LaunchResponse> getLaunchById(int launchID, @NonNull Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = libraryService.getLaunchByID(launchID);

        call.enqueue(callback);

        Timber.v("Creating getLaunchByID for Launch: %s", launchID);

        return call;
    }

    public Call<LaunchResponse> getNextLaunches(Callback<LaunchResponse> callback){
        Call<LaunchResponse> call = libraryService.getMiniNextLaunch(Utils.getStartDate(-1), Utils.getEndDate(10));

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getUpcomingLaunches(int offset, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = libraryService.getUpcomingLaunches(Utils.getStartDate(-1), Utils.getEndDate(10), offset);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getUpcomingLaunchesAll(int offset, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = libraryService.getUpcomingLaunchesAll(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<LaunchResponse> getLaunchesByDate(String startDate, String endDate, int offset, Callback<LaunchResponse> callback) {
        Call<LaunchResponse> call = libraryService.getLaunchesByDate(startDate, endDate, offset);

        call.enqueue(callback);

        return call;
    }

    public Call<AgencyResponse> getAllAgencies(int offset, Callback<AgencyResponse> callback) {
        Call<AgencyResponse> call = libraryService.getAllAgency(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<MissionResponse> getAllMissions(int offset, Callback<MissionResponse> callback) {
        Call<MissionResponse> call = libraryService.getAllMisisons(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<LocationResponse> getAllLocations(int offset, Callback<LocationResponse> callback) {
        Call<LocationResponse> call = libraryService.getLocations(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<LocationResponse> getLocationById(int locationID, Callback<LocationResponse> callback) {
        Call<LocationResponse> call = libraryService.getLocationsById(locationID);

        call.enqueue(callback);

        return call;
    }

    public Call<PadResponse> getAllPads(int offset, Callback<PadResponse> callback) {
        Call<PadResponse> call = libraryService.getPads(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<PadResponse> getPadsByID(int padID, Callback<PadResponse> callback) {
        Call<PadResponse> call = libraryService.getPadsById(padID);

        call.enqueue(callback);

        return call;
    }

    public Call<VehicleResponse> getVehicles(Callback<VehicleResponse> callback){
        Call<VehicleResponse> call = spaceLaunchNowService.getVehicles();

        call.enqueue(callback);

        return call;
    }

    public Call<RocketResponse> getRockets(int offset, Callback<RocketResponse> callback) {
        Call<RocketResponse> call = libraryService.getAllRockets(offset);

        call.enqueue(callback);

        return call;
    }

    public Call<RocketFamilyResponse> getRocketFamily(int offset, Callback<RocketFamilyResponse> callback) {
        Call<RocketFamilyResponse> call = libraryService.getAllRocketFamily(offset);

        call.enqueue(callback);

        return call;
    }
}
