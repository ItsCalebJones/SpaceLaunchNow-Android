package me.calebjones.spacelaunchnow.content.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.Agency;
import me.calebjones.spacelaunchnow.data.models.realm.Location;
import me.calebjones.spacelaunchnow.data.models.realm.Mission;
import me.calebjones.spacelaunchnow.data.models.realm.Pad;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.data.models.realm.Rocket;
import me.calebjones.spacelaunchnow.data.models.realm.RocketDetails;
import me.calebjones.spacelaunchnow.data.models.realm.RocketFamily;
import me.calebjones.spacelaunchnow.data.models.realm.UpdateRecord;
import me.calebjones.spacelaunchnow.data.networking.interfaces.LibraryService;
import me.calebjones.spacelaunchnow.data.networking.interfaces.SpaceLaunchNowService;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.AgencyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LocationResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.MissionResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.PadResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.RocketFamilyResponse;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.RocketResponse;
import me.calebjones.spacelaunchnow.utils.Analytics;
import me.calebjones.spacelaunchnow.utils.FileUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;


/**
 * This grabs rockets from LaunchLibrary
 */
public class LibraryDataService extends IntentService {

    private SharedPreferences sharedPref;
    private ListPreferences listPreference;

    private Realm mRealm;

    private Retrofit apiRetrofit;
    private Retrofit libraryRetrofit;

    public LibraryDataService() {
        super("LibraryDataService");
    }

    public void onCreate() {
        Timber.d("LibraryDataService - onCreate");

        // Note there is a bug in GSON 2.5 that can cause it to StackOverflow when working with RealmObjects.
        // To work around this, use the ExclusionStrategy below or downgrade to 1.7.1
        // See more here: https://code.google.com/p/google-gson/issues/detail?id=440
        Type token = new TypeToken<RealmList<RealmStr>>() {
        }.getType();

        Gson gson = new GsonBuilder()
                .setDateFormat("MMMM dd, yyyy hh:mm:ss zzz")
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(token, new TypeAdapter<RealmList<RealmStr>>() {

                    @Override
                    public void write(JsonWriter out, RealmList<RealmStr> value) throws io.realm.internal.IOException {
                        // Ignore
                    }

                    @Override
                    public RealmList<RealmStr> read(JsonReader in) throws io.realm.internal.IOException, java.io.IOException {
                        RealmList<RealmStr> list = new RealmList<RealmStr>();
                        in.beginArray();
                        while (in.hasNext()) {
                            list.add(new RealmStr(in.nextString()));
                        }
                        in.endArray();
                        return list;
                    }
                })
                .create();

        libraryRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.LIBRARY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(Executors.newCachedThreadPool())
                .build();

        apiRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(Executors.newCachedThreadPool())
                .build();

        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.listPreference = ListPreferences.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Create a new empty instance of Realm
        mRealm = Realm.getDefaultInstance();

        if (intent != null) {
            Timber.d("LibraryDataService - Intent %s received!", intent.getAction());
            String action = intent.getAction();
            if(Constants.ACTION_GET_ALL_LIBRARY_DATA.equals(action)){
                listPreference.setLastVehicleUpdate(System.currentTimeMillis());
                getAllAgency();
                getAllLocations();
                getAllMissions();
                getAllPads();
                getBaseVehicleDetails();
                getLibraryRockets();
                getLibraryRocketsFamily();
            } else if (Constants.ACTION_GET_AGENCY.equals(action)) {
                getAllAgency();
            } else if (Constants.ACTION_GET_MISSION.equals(action)){
                getAllMissions();
            } else if (Constants.ACTION_GET_LOCATION.equals(action)){
                getAllLocations();
            } else if (Constants.ACTION_GET_PADS.equals(action)){
                getAllPads();
            } else if (Constants.ACTION_GET_VEHICLES_DETAIL.equals(action)) {
                listPreference.setLastVehicleUpdate(System.currentTimeMillis());
                getBaseVehicleDetails();
                getLibraryRockets();
                getLibraryRocketsFamily();
            } else if (Constants.ACTION_GET_VEHICLES.equals(action)) {
                getLibraryRockets();
                getLibraryRocketsFamily();
            }
        }
        mRealm.close();
    }

    private void getAllAgency() {
        LibraryService request = libraryRetrofit.create(LibraryService.class);
        Call<AgencyResponse> call;
        Response<AgencyResponse> launchResponse;
        RealmList<Agency> items = new RealmList<>();
        int offset = 0;
        int total = 10;
        int count;

        try {
            while (total != offset) {
                if (listPreference.isDebugEnabled()) {
                    call = request.getDebugAllAgency(offset);
                } else {
                    call = request.getAllAgency(offset);
                }
                launchResponse = call.execute();
                total = launchResponse.body().getTotal();
                count = launchResponse.body().getCount();
                offset = offset + count;
                Collections.addAll(items, launchResponse.body().getAgencies());
            }
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(items);
            mRealm.commitTransaction();

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_SUCCESS_AGENCY);
            LibraryDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

        } catch (Exception e) {
            Crashlytics.logException(e);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_FAILURE_AGENCY);
            LibraryDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void getAllMissions() {
        LibraryService request = libraryRetrofit.create(LibraryService.class);
        Call<MissionResponse> call = null;
        Response<MissionResponse> launchResponse;
        RealmList<Mission> items = new RealmList<>();
        int offset = 0;
        int total = 10;
        int count;

        try {
            while (total != offset) {
                if (listPreference.isDebugEnabled()) {
                    call = request.getDebugAllMissions(offset);
                } else {
                    call = request.getAllMisisons(offset);
                }
                launchResponse = call.execute();
                if (launchResponse.isSuccessful()) {
                    total = launchResponse.body().getTotal();
                    count = launchResponse.body().getCount();
                    offset = offset + count;
                    Timber.v("Count: %s", offset);
                    Collections.addAll(items, launchResponse.body().getMissions());
                } else {
                    throw new IOException(launchResponse.errorBody().string());
                }

                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(items);
                mRealm.commitTransaction();
            }

            Timber.v("Success!");

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_SUCCESS_MISSIONS);

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UpdateRecord updateRecord = new UpdateRecord();
                    updateRecord.setType(Constants.ACTION_GET_MISSION);
                    updateRecord.setDate(new Date());
                    updateRecord.setSuccessful(true);
                    realm.copyToRealmOrUpdate(updateRecord);
                }
            });

            FileUtils.saveSuccess(true, Constants.ACTION_GET_MISSION, this);

            Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_MISSION, call.request().url().toString(), true);

            this.sendBroadcast(broadcastIntent);

        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UpdateRecord updateRecord = new UpdateRecord();
                    updateRecord.setType(Constants.ACTION_GET_MISSION);
                    updateRecord.setDate(new Date());
                    updateRecord.setSuccessful(false);
                    realm.copyToRealmOrUpdate(updateRecord);
                }
            });

            FileUtils.saveSuccess(false, Constants.ACTION_GET_MISSION + " " + e.getLocalizedMessage(), this);

            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("error", e.getLocalizedMessage());
            broadcastIntent.setAction(Constants.ACTION_FAILURE_MISSIONS);

            Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_MISSION, call.request().url().toString(), false, e.getLocalizedMessage());

            this.sendBroadcast(broadcastIntent);
        }
    }

    private void getAllLocations() {
        LibraryService request = libraryRetrofit.create(LibraryService.class);
        Call<LocationResponse> call;
        Response<LocationResponse> launchResponse;
        RealmList<Location> items = new RealmList<>();
        int offset = 0;
        int total = 10;
        int count;

        try {
            while (total != offset) {
                if (listPreference.isDebugEnabled()) {
                    call = request.getDebugLocations(offset);
                } else {
                    call = request.getLocations(offset);
                }
                launchResponse = call.execute();
                total = launchResponse.body().getTotal();
                count = launchResponse.body().getCount();
                offset = offset + count;
                Collections.addAll(items, launchResponse.body().getLocations());
            }
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(items);
            mRealm.commitTransaction();

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_SUCCESS_LOCATION);
            LibraryDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

        } catch (Exception e) {
            Crashlytics.logException(e);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_FAILURE_LOCATION);
            LibraryDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void getAllPads() {
        LibraryService request = libraryRetrofit.create(LibraryService.class);
        Call<PadResponse> call;
        Response<PadResponse> launchResponse;
        RealmList<Pad> items = new RealmList<>();
        int offset = 0;
        int total = 10;
        int count;

        try {
            while (total != offset) {
                if (listPreference.isDebugEnabled()) {
                    call = request.getDebugPads(offset);
                } else {
                    call = request.getPads(offset);
                }
                launchResponse = call.execute();
                total = launchResponse.body().getTotal();
                count = launchResponse.body().getCount();
                offset = offset + count;
                Collections.addAll(items, launchResponse.body().getPads());
            }
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(items);
            mRealm.commitTransaction();

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_SUCCESS_PADS);
            LibraryDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

        } catch (Exception e) {
            Crashlytics.logException(e);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_FAILURE_PADS);
            LibraryDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void getBaseVehicleDetails() {
        SpaceLaunchNowService request = apiRetrofit.create(SpaceLaunchNowService.class);
        Call<VehicleResponse> call = null;
        Response<VehicleResponse> launchResponse;
        RealmList<RocketDetails> items = new RealmList<>();

        try {
            call = request.getVehicles();
            launchResponse = call.execute();
            if (launchResponse.isSuccessful()) {
                Collections.addAll(items, launchResponse.body().getVehicles());

                int count = 1;
                for (RocketDetails item : items) {
                    Timber.v("%s - %s of %s", item.getLV_Name(), count, items.size());
                    item.setName(item.getLV_Name() + " " + item.getLV_Variant());
                    count += 1;
                }

                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(items);
                mRealm.commitTransaction();

                Timber.v("getBaseVehicleDetails - Success");
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Constants.ACTION_SUCCESS_VEHICLE_DETAILS);
                LibraryDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

                Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), true);
            } else {
                Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), false, launchResponse.message());

            }
        } catch (IOException e) {

            Timber.e("LibraryDataService - ERROR: %s", e.getLocalizedMessage());
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_FAILURE_VEHICLE_DETAILS);
            LibraryDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

            Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), false, e.getLocalizedMessage());
        }
    }

    private void getLibraryRockets() {
        LibraryService request = libraryRetrofit.create(LibraryService.class);
        Call<RocketResponse> call = null;
        Response<RocketResponse> launchResponse = null;
        RealmList<Rocket> items = new RealmList<>();

        int offset = 0;
        int total = 10;
        int count;

        try {
            while (total != offset) {
                if (listPreference.isDebugEnabled()) {
                    call = request.getDebugAllRockets(offset);
                } else {
                    call = request.getAllRockets(offset);
                }
                launchResponse = call.execute();
                if (launchResponse.isSuccessful()) {
                    total = launchResponse.body().getTotal();
                    count = launchResponse.body().getCount();
                    offset = offset + count;
                    Collections.addAll(items, launchResponse.body().getRockets());
                }
            }

            if (items.size() > 0) {
                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(items);
                mRealm.commitTransaction();

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Constants.ACTION_SUCCESS_VEHICLES);
                LibraryDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

                Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), true);

            } else {

                Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), false, launchResponse.message());
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Constants.ACTION_FAILURE_VEHICLES);

            }
        } catch (IOException e) {
            e.printStackTrace();
            Timber.e("LibraryDataService - ERROR: %s", e.getLocalizedMessage());

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_FAILURE_VEHICLES);
            LibraryDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

            Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), false, e.getLocalizedMessage());

        }
    }

    private void getLibraryRocketsFamily() {
        LibraryService request = libraryRetrofit.create(LibraryService.class);
        Call<RocketFamilyResponse> call = null;
        Response<RocketFamilyResponse> launchResponse;
        RealmList<RocketFamily> items = new RealmList<>();

        int offset = 0;
        int total = 10;
        int count;

        try {
            while (total != offset) {
                if (listPreference.isDebugEnabled()) {
                    call = request.getDebugAllRocketFamily(offset);
                } else {
                    call = request.getAllRocketFamily(offset);
                }
                launchResponse = call.execute();
                if (launchResponse.isSuccessful()) {
                    total = launchResponse.body().getTotal();
                    count = launchResponse.body().getCount();
                    offset = offset + count;
                    Collections.addAll(items, launchResponse.body().getRocketFamilies());
                }
            }

            if (items.size() > 0) {
                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(items);
                mRealm.commitTransaction();
            }

            Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), true);
        } catch (IOException e) {
            e.printStackTrace();
            Timber.e("LibraryDataService - ERROR: %s", e.getLocalizedMessage());
            Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), false, e.getLocalizedMessage());
        }
    }
}
