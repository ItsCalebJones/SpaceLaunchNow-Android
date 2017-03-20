package me.calebjones.spacelaunchnow.content.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.data.models.realm.Rocket;
import me.calebjones.spacelaunchnow.data.models.realm.RocketDetails;
import me.calebjones.spacelaunchnow.data.models.realm.RocketFamily;
import me.calebjones.spacelaunchnow.data.models.realm.UpdateRecord;
import me.calebjones.spacelaunchnow.data.networking.interfaces.APIRequestInterface;
import me.calebjones.spacelaunchnow.data.networking.interfaces.LibraryRequestInterface;
import me.calebjones.spacelaunchnow.data.networking.responses.base.VehicleResponse;
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
 * This grabs details from my own hosted JSON file.
 */
//TODO delete and point to library data service
public class VehicleDataService extends IntentService {


    private SharedPreferences sharedPref;
    private ListPreferences listPreference;
    private Realm mRealm;

    private Retrofit apiRetrofit;
    private Retrofit libraryRetrofit;

    public VehicleDataService() {
        super("VehicleDataService");
    }

    public void onCreate() {
        Timber.d("LaunchDataService - onCreate");
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
                .build();

        apiRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
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
        Timber.d("VehicleDataService - Intent received!");

        // Create a new empty instance of Realm
        mRealm = Realm.getDefaultInstance();

        if (intent != null) {
            String action = intent.getAction();
            if (Constants.ACTION_GET_VEHICLES_DETAIL.equals(action)) {
                listPreference.setLastVehicleUpdate(System.currentTimeMillis());
                boolean success;
                success = getBaseVehicleDetails();
                success = getLibraryRockets();
                success = getLibraryRocketsFamily();
                final boolean finalSuccess = success;
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        UpdateRecord updateRecord = new UpdateRecord();
                        updateRecord.setType(Constants.ACTION_GET_VEHICLES_DETAIL);
                        updateRecord.setDate(new Date());
                        updateRecord.setSuccessful(finalSuccess);
                        realm.copyToRealmOrUpdate(updateRecord);
                    }
                });

                FileUtils.saveSuccess(success, Constants.ACTION_GET_VEHICLES_DETAIL, this);
            }
        }
        mRealm.close();
    }

    private boolean getBaseVehicleDetails() {
        APIRequestInterface request = apiRetrofit.create(APIRequestInterface.class);
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
                    Timber.v("%s - %s of %s",item.getLV_Name(), count, items.size());
                    item.setName(item.getLV_Name() + " " + item.getLV_Variant());
                    count += 1;
                }

                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(items);
                mRealm.commitTransaction();

                Timber.v("getBaseVehicleDetails - Success");
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Constants.ACTION_SUCCESS_VEHICLE_DETAILS);
                VehicleDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

                Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), true);
                return true;
            } else {
                Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), false, launchResponse.message());
                return false;
            }
        } catch (IOException e) {

            Timber.e("VehicleDataService - ERROR: %s", e.getLocalizedMessage());
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_FAILURE_VEHICLE_DETAILS);
            VehicleDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

            Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), false, e.getLocalizedMessage());
            return false;
        }
    }

    private boolean getLibraryRockets() {
        LibraryRequestInterface request = libraryRetrofit.create(LibraryRequestInterface.class);
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
                VehicleDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

                Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), true);
                return true;
            } else {

                Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), false, launchResponse.message());
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Constants.ACTION_FAILURE_VEHICLES);
                VehicleDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Timber.e("VehicleDataService - ERROR: %s", e.getLocalizedMessage());

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_FAILURE_VEHICLES);
            VehicleDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

            Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), false, e.getLocalizedMessage());
            return false;
        }
    }

    private boolean getLibraryRocketsFamily() {
        LibraryRequestInterface request = libraryRetrofit.create(LibraryRequestInterface.class);
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
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Timber.e("VehicleDataService - ERROR: %s", e.getLocalizedMessage());
            Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_VEHICLES_DETAIL, call.request().url().toString(), false, e.getLocalizedMessage());
            return false;
        }
    }
}
