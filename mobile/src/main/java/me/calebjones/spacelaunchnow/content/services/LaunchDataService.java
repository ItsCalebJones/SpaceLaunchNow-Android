package me.calebjones.spacelaunchnow.content.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.BuildConfig;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.interfaces.LibraryRequestInterface;
import me.calebjones.spacelaunchnow.content.models.Launcher;
import me.calebjones.spacelaunchnow.content.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.Location;
import me.calebjones.spacelaunchnow.content.models.LocationAgency;
import me.calebjones.spacelaunchnow.content.models.Mission;
import me.calebjones.spacelaunchnow.content.models.Pad;
import me.calebjones.spacelaunchnow.content.models.Rocket;
import me.calebjones.spacelaunchnow.content.models.RocketAgency;
import me.calebjones.spacelaunchnow.content.responses.LaunchResponse;
import me.calebjones.spacelaunchnow.content.responses.LauncherResponse;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.custom.RealmStr;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class LaunchDataService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static List<Launch> upcomingLaunchList;
    private static List<Launch> previousLaunchList;
    private Launch prevLaunch;
    private Launch storedPrevLaunch;
    private AlarmManager alarmManager;
    private SharedPreferences sharedPref;
    private ListPreferences listPreference;
    private SwitchPreferences switchPreferences;

    private static final String NAME_KEY = "me.calebjones.spacelaunchnow.wear.nextname";
    private static final String TIME_KEY = "me.calebjones.spacelaunchnow.wear.nexttime";

    private GoogleApiClient mGoogleApiClient;

    private Realm mRealm;

    public LaunchDataService() {
        super("LaunchDataService");
    }

    public void onCreate() {
        Timber.d("LaunchDataService - UpComingLaunchService onCreate");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.listPreference = ListPreferences.getInstance(getApplicationContext());
        this.switchPreferences = SwitchPreferences.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("LaunchDataService - Intent received:  %s ", intent.getAction());
        String action = intent.getAction();

        mGoogleApiClient.connect();
        Timber.d("mGoogleApiClient - connect");

        if (Strings.ACTION_GET_ALL.equals(action)) {
            Timber.v("Intent action received: %s", action);
            if (this.sharedPref.getBoolean("background", true)) {
                scheduleLaunchUpdates();
            }

            getUpcomingLaunches();
            getPreviousLaunches(Utils.getBaseURL(getApplicationContext()));

            Intent rocketIntent = new Intent(getApplicationContext(), VehicleDataService.class);
            rocketIntent.setAction(Strings.ACTION_GET_VEHICLES_DETAIL);
            startService(rocketIntent);

            startService(new Intent(this, MissionDataService.class));
        } else if (Strings.ACTION_GET_UP_LAUNCHES.equals(action)) {
            Timber.v("Intent action received: %s", action);
            if (this.sharedPref.getBoolean("background", true)) {
                scheduleLaunchUpdates();
            }
            getUpcomingLaunches();
        } else if (Strings.ACTION_GET_PREV_LAUNCHES.equals(action)) {
            Timber.v("Intent action received: %s", action);
            getPreviousLaunches(intent.getStringExtra("URL"));
        } else if (Strings.ACTION_UPDATE_NEXT_LAUNCH.equals(action)) {
            Timber.v("Intent action received: %s", action);
            getNextLaunches();
            Timber.v("Starting service NextLaunchTracker");
        } else {
            Timber.e("LaunchDataService - onHandleIntent: ERROR - Unknown Intent %s", action);
        }
        Timber.v("Finished!");
    }




    private void getPreviousLaunches(String sUrl) {
        HttpURLConnection urlConnection;
        try {
            /* forming th java.net.URL object */
            URL url = new URL(sUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

                /* for Get request */
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
            if (statusCode == 200) {

                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    response.append(line);
                }

                previousLaunchList = parseMultipleResult(response.toString());

                Timber.d("LaunchDataService - Previous Launches list:  %s ", previousLaunchList.size());

                if (this.switchPreferences.getPrevFiltered()) {
                    this.listPreference.setPreviousLaunchesFiltered(previousLaunchList);
                } else {
                    LaunchDataService.this.cleanCachePrevious();
                    this.listPreference.setPreviousLaunches(previousLaunchList);
                }
//                this.listPreference.syncPreviousMissions();

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Strings.ACTION_SUCCESS_PREV_LAUNCHES);
                LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

            } else {
                Crashlytics.log(Log.ERROR, "LaunchDataService", "Failed to retrieve upcoming launches: " + statusCode);

                Answers.getInstance().logCustom(new CustomEvent("Failed Data Sync")
                        .putCustomAttribute("Status", statusCode));

                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtra("error", "Network Code: " + statusCode);
                broadcastIntent.setAction(Strings.ACTION_FAILURE_PREV_LAUNCHES);
                LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
            }

        } catch (Exception e) {
            Timber.e("LaunchDataService - getPreviousLaunches ERROR: %s", e.getLocalizedMessage());
            if (Utils.isNetworkAvailable(this)) {
                Crashlytics.setBool("Network", Utils.isNetworkAvailable(this));
                Crashlytics.logException(e);
            }

            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("error", e.getLocalizedMessage());
            broadcastIntent.setAction(Strings.ACTION_FAILURE_PREV_LAUNCHES);
            LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void getUpcomingLaunches() {
        // create a converter compatible with Realm
        // GSON can parse the data.
        // Note there is a bug in GSON 2.5 that can cause it to StackOverflow when working with RealmObjects.
        // To work around this, use the ExclusionStrategy below or downgrade to 1.7.1
        // See more here: https://code.google.com/p/google-gson/issues/detail?id=440
        Type token = new TypeToken<RealmList<RealmStr>>(){}.getType();
        Gson gson = new GsonBuilder()
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Strings.LIBRARY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        LibraryRequestInterface request = retrofit.create(LibraryRequestInterface.class);
        Call<LaunchResponse> call;

        if(listPreference.isDebugEnabled()){
            call = request.getDebugNextLaunches();
        } else {
            call = request.getNextLaunches();
        }

        Response<LaunchResponse> launchResponse;
        try {
            // Init Realm
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                    .deleteRealmIfMigrationNeeded()
                    .build();

            // Clear the realm from last time
            //Realm.deleteRealm(realmConfiguration);

            // Create a new empty instance of Realm
            mRealm = Realm.getInstance(realmConfiguration);

            launchResponse = call.execute();
            if(launchResponse.isSuccess()){
                RealmList<LaunchRealm> items = new RealmList<>(launchResponse.body().getLaunches());

                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(items);
                mRealm.commitTransaction();
                mRealm.close();

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
                LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
            } else {
                Timber.e("ERROR: %s", launchResponse.errorBody());
                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtra("error", launchResponse.errorBody().toString());
                broadcastIntent.setAction(Strings.ACTION_FAILURE_UP_LAUNCHES);
                LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
            }
        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("error", e.getLocalizedMessage());
            broadcastIntent.setAction(Strings.ACTION_FAILURE_UP_LAUNCHES);
            LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void getNextLaunches() {
        HttpURLConnection urlConnection;
        try {
            /* forming th java.net.URL object */
            URL url;

            //Used for loading debug launches/reproducing bugs
            if (listPreference.isDebugEnabled()) {
                url = new URL(Strings.DEBUG_NEXT_URL);
            } else {
                url = new URL(Strings.NEXT_URL);
            }

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
            if (statusCode == 200) {

                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = r.readLine()) != null) {
                    response.append(line);
                }
                List<Launch> launches = parseMultipleResult(response.toString());

                upcomingLaunchList = listPreference.getNextLaunches();
                launches = listPreference.filterLaunches(launches);

                if (upcomingLaunchList.get(0).getId().intValue() == launches.get(0).getId().intValue()) {
                    Launch storedLaunch = listPreference.getNextLaunch();
                    Launch nextLaunch = launches.get(0);
                    if (nextLaunch != null) {
                        if (nextLaunch.getId().intValue() == listPreference.getNextLaunch().getId().intValue()) {
                            if (storedLaunch.getIsNotifiedDay()) {
                                nextLaunch.setIsNotifiedDay(true);
                            }
                            if (storedLaunch.getIsNotifiedHour()) {
                                nextLaunch.setIsNotifiedhour(true);
                            }
                            if (storedLaunch.getIsNotifiedTenMinute()) {
                                nextLaunch.setIsNotifiedTenMinute(true);
                            }
                            if (storedLaunch.isFavorite()) {
                                nextLaunch.isFavorite();
                            }
                            if (storedLaunch.getCalendarID() != null) {
                                nextLaunch.setCalendarID(storedLaunch.getCalendarID());
                            }
                            listPreference.setNextLaunch(nextLaunch);
                        } else {
                            listPreference.setNextLaunch(nextLaunch);
                        }
                    }
                } else {
                    getUpcomingLaunches();
                }
            } else {
                Crashlytics.log(Log.ERROR, "LaunchDataService", "Failed to retrieve upcoming launches: " + statusCode);

                if (!BuildConfig.DEBUG) {
                    Answers.getInstance().logCustom(new CustomEvent("Failed Data Sync")
                            .putCustomAttribute("Status", statusCode));
                }

                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtra("error", "Network Code: " + statusCode);
                broadcastIntent.setAction(Strings.ACTION_FAILURE_UP_LAUNCHES);
                LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
            }

        } catch (Exception e) {
            Timber.e("LaunchDataService - getUpcomingLaunches ERROR: %s", e.getLocalizedMessage());
            if (Utils.isNetworkAvailable(this)) {
                Crashlytics.setBool("Network", Utils.isNetworkAvailable(this));
                Crashlytics.logException(e);
            }

            if (BuildConfig.DEBUG) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setContentTitle("LaunchData Failed!")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setAutoCancel(true);

                NotificationManager mNotifyManager = (NotificationManager)
                        getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyManager.notify(Strings.NOTIF_ID, mBuilder.build());
            }

            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("error", e.getLocalizedMessage());
            broadcastIntent.setAction(Strings.ACTION_FAILURE_UP_LAUNCHES);
            LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void updateNextLaunchById(int id) {
        HttpURLConnection urlConnection;
        try {
            /* forming th java.net.URL object */
            URL url;

            //Used for loading debug lauches/reproducing bugs
            if (listPreference.isDebugEnabled()) {
                url = new URL(String.format(Strings.DEBUG_NEXT_URL_BY_ID, id));
            } else {
                url = new URL(String.format(Strings.NEXT_URL_BY_ID, id));
            }

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
            if (statusCode == 200) {

                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = r.readLine()) != null) {
                    response.append(line);
                }

                if (BuildConfig.DEBUG) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                    mBuilder.setContentTitle("LaunchData Worked!")
                            .setSmallIcon(R.drawable.ic_notification)
                            .setAutoCancel(true);

                    NotificationManager mNotifyManager = (NotificationManager)
                            getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotifyManager.notify(Strings.NOTIF_ID, mBuilder.build());
                }

                Launch nextLaunch = parseSingleResult(response.toString());
                Launch storedLaunch = listPreference.getNextLaunch();
                if (nextLaunch != null) {
                    if (nextLaunch.getId().intValue() == listPreference.getNextLaunch().getId().intValue()) {
                        if (storedLaunch.getIsNotifiedDay()) {
                            nextLaunch.setIsNotifiedDay(true);
                        }
                        if (storedLaunch.getIsNotifiedHour()) {
                            nextLaunch.setIsNotifiedhour(true);
                        }
                        if (storedLaunch.getIsNotifiedTenMinute()) {
                            nextLaunch.setIsNotifiedTenMinute(true);
                        }
                        if (storedLaunch.isFavorite()) {
                            nextLaunch.isFavorite();
                        }
                        if (storedLaunch.getCalendarID() != null) {
                            nextLaunch.setCalendarID(storedLaunch.getCalendarID());
                        }
                        listPreference.setNextLaunch(nextLaunch);
                    } else {
                        listPreference.setNextLaunch(nextLaunch);
                    }
                    List<Launch> mLaunchList = listPreference.getNextLaunches();
                    if (mLaunchList.get(0).getId().intValue() == nextLaunch.getId().intValue()) {
                        mLaunchList.set(0, nextLaunch);
                        listPreference.setNextLaunches(mLaunchList);
                    }
                }
            } else {
                Crashlytics.log(Log.ERROR, "LaunchDataService", "Failed to retrieve next launch: " + statusCode);

                if (!BuildConfig.DEBUG) {
                    Answers.getInstance().logCustom(new CustomEvent("Failed Data Sync")
                            .putCustomAttribute("Status", statusCode));
                }
            }

        } catch (Exception e) {
            Timber.e("LaunchDataService - getUpcomingLaunches ERROR: %s", e.getLocalizedMessage());
            if (Utils.isNetworkAvailable(this)) {
                Crashlytics.setBool("Network", Utils.isNetworkAvailable(this));
                Crashlytics.logException(e);
            }

            if (BuildConfig.DEBUG) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setContentTitle("LaunchData Failed!")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setAutoCancel(true);

                NotificationManager mNotifyManager = (NotificationManager)
                        getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyManager.notify(Strings.NOTIF_ID, mBuilder.build());
            }
        }
    }

    private void updateStoredLaunch() {
        HttpURLConnection urlConnection;
        try {
            /* forming th java.net.URL object */
            URL url;

            //Used for loading debug lauches/reproducing bugs
            if (listPreference.isDebugEnabled()) {
                url = new URL(String.format(Strings.DEBUG_NEXT_URL_BY_ID, listPreference.getNextLaunch().getId()));
            } else {
                url = new URL(String.format(Strings.NEXT_URL_BY_ID, listPreference.getNextLaunch().getId()));
            }

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
            if (statusCode == 200) {

                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = r.readLine()) != null) {
                    response.append(line);
                }

                if (BuildConfig.DEBUG) {
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                    mBuilder.setContentTitle("LaunchData Worked!")
                            .setSmallIcon(R.drawable.ic_notification)
                            .setAutoCancel(true);

                    NotificationManager mNotifyManager = (NotificationManager)
                            getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotifyManager.notify(Strings.NOTIF_ID, mBuilder.build());
                }

                Launch nextLaunch = parseSingleResult(response.toString());
                Launch storedLaunch = listPreference.getNextLaunch();
                if (nextLaunch != null && nextLaunch.getId().intValue() == listPreference.getNextLaunch().getId().intValue()) {
                    if (storedLaunch.getIsNotifiedDay()) {
                        nextLaunch.setIsNotifiedDay(true);
                    }
                    if (storedLaunch.getIsNotifiedHour()) {
                        nextLaunch.setIsNotifiedhour(true);
                    }
                    if (storedLaunch.getIsNotifiedTenMinute()) {
                        nextLaunch.setIsNotifiedTenMinute(true);
                    }
                    if (storedLaunch.isFavorite()) {
                        nextLaunch.isFavorite();
                    }
                    if (storedLaunch.getCalendarID() != null) {
                        nextLaunch.setCalendarID(storedLaunch.getCalendarID());
                    }
                    this.listPreference.setNextLaunch(nextLaunch);
                }
            } else {
                Crashlytics.log(Log.ERROR, "LaunchDataService", "Failed to retrieve next launch: " + statusCode);

                if (!BuildConfig.DEBUG) {
                    Answers.getInstance().logCustom(new CustomEvent("Failed Data Sync")
                            .putCustomAttribute("Status", statusCode));
                }
            }

        } catch (Exception e) {
            Timber.e("LaunchDataService - getUpcomingLaunches ERROR: %s", e.getLocalizedMessage());
            if (Utils.isNetworkAvailable(this)) {
                Crashlytics.setBool("Network", Utils.isNetworkAvailable(this));
                Crashlytics.logException(e);
            }

            if (BuildConfig.DEBUG) {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setContentTitle("LaunchData Failed!")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setAutoCancel(true);

                NotificationManager mNotifyManager = (NotificationManager)
                        getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyManager.notify(Strings.NOTIF_ID, mBuilder.build());
            }
        }
    }

    private void cleanCacheUpcoming() {
        this.listPreference.removeUpcomingLaunches();
    }

    private void cleanCachePrevious() {
        this.listPreference.removePreviousLaunches();
    }

    @Nullable
    private Launch parseSingleResult(String result) {
        JSONObject response;
        try {
            response = new JSONObject(result);
            JSONArray launchesArray = response.optJSONArray("launches");
            if (launchesArray.length() > 0) {
                return parseLaunchArray(launchesArray.optJSONObject(0));
            } else {
                return null;
            }
        } catch (JSONException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Launch> parseMultipleResult(String result) throws JSONException {
        try {

            /*Initialize array if null*/
            List<Launch> list = new ArrayList<>();

            JSONObject response = new JSONObject(result);
            JSONArray launchesArray = response.optJSONArray("launches");
            for (int i = 0; i < launchesArray.length(); i++) {
                list.add(parseLaunchArray(launchesArray.optJSONObject(i)));
            }
            return list;
        } catch (JSONException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Launch> parseSimpleMultipleResult(String result) throws JSONException {
        try {

            /*Initialize array if null*/
            List<Launch> list = new ArrayList<>();

            JSONObject response = new JSONObject(result);
            JSONArray launchesArray = response.optJSONArray("launches");
            for (int i = 0; i < launchesArray.length(); i++) {
                list.add(parseSimpleResponse(launchesArray.optJSONObject(i)));
            }
            return list;
        } catch (JSONException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Launch parseSimpleResponse(JSONObject launchesObj) throws JSONException {
        Launch launch = new Launch();
        launch.setId(launchesObj.optInt("id"));
        launch.setNetstamp(launchesObj.optInt("netstamp"));
        return launch;
    }

    private Launch parseLaunchArray(JSONObject launchesObj) throws JSONException {
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy kk:mm:ss zzz", Locale.US);
        JSONObject rocketObj = launchesObj.optJSONObject("rocket");
        JSONObject locationObj = launchesObj.optJSONObject("location");

        Launch launch = new Launch();

        launch.setName(launchesObj.optString("name"));
        launch.setId(launchesObj.optInt("id"));
        launch.setNet(launchesObj.optString("net"));
        try {
            launch.setStartDate(df.parse(launchesObj.optString("net")));
        } catch (ParseException e) {
            Timber.e(e.getLocalizedMessage());
            launch.setStartDate(null);
        }
        try {
            launch.setEndDate(df.parse(launchesObj.optString("windowend")));
        } catch (ParseException e) {
            Timber.e(e.getLocalizedMessage());
            launch.setEndDate(null);
        }
        launch.setWindowstart(launchesObj.optString("windowstart"));
        launch.setWindowend(launchesObj.optString("windowend"));
        launch.setNetstamp(launchesObj.optInt("netstamp"));
        launch.setWsstamp(launchesObj.optInt("wsstamp"));
        launch.setWestamp(launchesObj.optInt("westamp"));
        launch.setProbability(launchesObj.optInt("probability"));
        launch.setHashtag(launchesObj.optString("hashtag"));
        launch.setFailreason(launchesObj.optString("failreason"));
        launch.setHoldreason(launchesObj.optString("holdreason"));
        launch.setStatus(launchesObj.optInt("status"));
        JSONArray vidURLs = launchesObj.getJSONArray("vidURLs");
        if (vidURLs.length() > 0) {
            launch.setVidURL(vidURLs.get(0).toString());
            ArrayList<String> listdata = new ArrayList<>();
            for (int o = 0; o < vidURLs.length(); o++) {
                listdata.add(vidURLs.get(o).toString());
            }
            launch.setVidURLs(listdata);

        }

        //Start Parsing Rockets
        if (rocketObj != null) {
            Rocket rocket = new Rocket();
            rocket.setId(rocketObj.optInt("id"));
            rocket.setName(rocketObj.optString("name"));
            rocket.setFamilyname(rocketObj.optString("familyname"));
            rocket.setConfiguration(rocketObj.optString("configuration"));
            rocket.setImageURL(rocketObj.optString("imageURL"));

            JSONArray agencies = rocketObj.optJSONArray("agencies");
            if (agencies != null) {
                List<RocketAgency> rocketList = new ArrayList<>();
                for (int a = 0; a < agencies.length(); a++) {
                    RocketAgency rocketAgency = new RocketAgency();
                    JSONObject agencyObj = agencies.optJSONObject(a);
                    rocketAgency.setId(agencyObj.optInt("id"));
                    rocketAgency.setName(agencyObj.optString("name"));
                    rocketAgency.setAbbrev(agencyObj.optString("abbrev"));
                    rocketAgency.setCountryCode(agencyObj.optString("countryCode"));
                    rocketAgency.setType(agencyObj.optInt("type"));
                    rocketAgency.setInfoURL(agencyObj.optString("infoURL"));
                    rocketAgency.setWikiURL(agencyObj.optString("wikiURL"));

                    rocketList.add(rocketAgency);
                }
                rocket.setAgencies(rocketList);
            }
            launch.setRocket(rocket);
        }

        //Start Parsing Locations
        if (locationObj != null) {

            Pad locationPads = new Pad();
            Location location = new Location();

            JSONArray pads = locationObj.optJSONArray("pads");

            if (pads != null) {
                List<Pad> locationPadsList = new ArrayList<>();
                for (int a = 0; a < pads.length(); a++) {
                    JSONObject padsObj = pads.optJSONObject(a);

                    location.setId(locationObj.optInt("id"));
                    location.setName(locationObj.optString("name"));

                    locationPads.setName(padsObj.optString("name"));
                    locationPads.setId(padsObj.optInt("id"));
                    locationPads.setLatitude(padsObj.optDouble("latitude"));
                    locationPads.setLongitude(padsObj.optDouble("longitude"));
                    locationPads.setMapURL(padsObj.optString("mapURL"));
                    locationPads.setInfoURL(padsObj.optString("infoURL"));
                    locationPads.setWikiURL(padsObj.optString("wikiURL"));

                    JSONArray padAgencies = padsObj.optJSONArray("agencies");
                    if (padAgencies != null) {
                        List<LocationAgency> locationAgencies = new ArrayList<>();
                        for (int b = 0; b < padAgencies.length(); b++) {
                            JSONObject padAgenciesObj = padAgencies.optJSONObject(b);
                            LocationAgency locationAgency = new LocationAgency();
                            locationAgency.setName(padAgenciesObj.optString("name"));
                            locationAgency.setId(padAgenciesObj.optInt("id"));
                            locationAgency.setAbbrev(padAgenciesObj.optString("abbrev"));
                            locationAgency.setCountryCode(padAgenciesObj
                                    .optString("countryCode"));
                            locationAgency.setType(padAgenciesObj.optInt("type"));
                            locationAgency.setInfoURL(padAgenciesObj.optString("infoURL"));
                            locationAgency.setWikiURL(padAgenciesObj.optString("wikiURL"));

                            locationAgencies.add(locationAgency);
                        }
                        locationPads.setAgencies(locationAgencies);
                    }
                    locationPadsList.add(locationPads);
                }
                location.setPads(locationPadsList);
            }
            launch.setLocation(location);
        }

        // Start parsing Missions
        JSONArray missions = launchesObj.optJSONArray("missions");
        if (missions != null) {
            List<Mission> missionList = new ArrayList<>();
            for (int c = 0; c < missions.length(); c++) {
                JSONObject missionObj = missions.optJSONObject(c);
                Mission mission = new Mission();
                mission.setId(missionObj.optInt("id"));
                mission.setType(missionObj.optInt("type"));
                mission.setTypeName(Utils.getTypeName(missionObj.optInt("type")));
                mission.setName(missionObj.optString("name"));
                mission.setDescription(missionObj.optString("description"));

                missionList.add(mission);
            }
            launch.setMissions(missionList);
        }

        return launch;
    }

    public void scheduleLaunchUpdates() {
        Timber.d("LaunchDataService - scheduleLaunchUpdates");

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Get sync period.
        String notificationTimer = this.sharedPref.getString("notification_sync_time", "4");

        long interval;

        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(notificationTimer);

        if (m.matches()) {
            int hrs = Integer.parseInt(m.group(1));
            interval = (long) hrs * 60 * 60 * 1000;
            Timber.d("LaunchDataService - Notification Timer: %s to millisecond %s", notificationTimer, interval);

            long nextUpdate = Calendar.getInstance().getTimeInMillis() + interval;
            Timber.d("LaunchDataService - Scheduling Alarm at %s with interval of %s", nextUpdate, interval);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, nextUpdate, interval,
                    PendingIntent.getBroadcast(this, 165435, new Intent(Strings.ACTION_UPDATE_UP_LAUNCHES), 0));
        } else {
            Timber.e("LaunchDataService - Error setting alarm, failed to change %s to milliseconds", notificationTimer);
        }
    }

    // Create a data map and put data in it
    private void sendToWear(Launch launch) {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/nextLaunch");

        putDataMapReq.getDataMap().putString(NAME_KEY, launch.getName());
        putDataMapReq.getDataMap().putInt(NAME_KEY, launch.getNetstamp());

        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Timber.d("onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.e("onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("onConnectionFailed %s", connectionResult.getErrorMessage());
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Timber.d("Google Client Disconnect");
            mGoogleApiClient.disconnect();
        }
    }
}
