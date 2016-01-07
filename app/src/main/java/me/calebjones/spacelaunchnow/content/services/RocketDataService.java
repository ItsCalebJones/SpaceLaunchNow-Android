package me.calebjones.spacelaunchnow.content.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.content.database.DatabaseManager;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.LaunchVehicle;
import timber.log.Timber;


/**
 * Created by cjones on 11/10/15.
 * If it is a new post then notify the user and save to DB.
 */
public class RocketDataService extends IntentService {

    public static List<Launch> vehicleList;
    private SharedPreferences sharedPref;
    private SharedPreference sharedPreference;

    public RocketDataService() {
        super("RocketDataService");
    }

    public void onCreate() {
        Timber.d("LaunchDataService - onCreate");
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.sharedPreference = SharedPreference.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("RocketDataService - Intent received!");
        //TODO grab the latest Upcoming and Previous launches save to SharedPreferences.
        if (intent != null) {
            String action = intent.getAction();
            if (Strings.ACTION_GET_ROCKETS.equals(action)) {

                DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());
                databaseManager.rebuildDB(databaseManager.getWritableDatabase());

                getRockets();
            }
        }
    }

    private void getRockets() {
        InputStream inputStream = null;
        Integer result = 0;
        HttpURLConnection urlConnection = null;
        try {
            /* forming th java.net.URL object */
            String value = this.sharedPref.getString("value", "5");
            URL url = new URL("http://calebjones.me/app/launchvehicle.json");

            urlConnection = (HttpURLConnection) url.openConnection();

                /* for Get request */
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

                result = addToDB(response);
                if (result == 1) {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(Strings.ACTION_SUCCESS_ROCKETS);
                    RocketDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
                } else {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(Strings.ACTION_FAILURE_ROCKETS);
                    RocketDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
                }
            }


        } catch (Exception e) {
            Timber.e("RocketDataService - ERROR: ", e.getLocalizedMessage());
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_FAILURE_ROCKETS);
            RocketDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private Integer addToDB(StringBuilder response) {
        DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());

        try {
            JSONArray responseArray = new JSONArray(response.toString());
            Timber.d("addToDB - Database Size: %s (Expect 0 here)", databaseManager.getCount());
            Timber.d("addToDB - Adding: %s...", response.toString().substring(0,(response.toString().length() / 2)));
            for (int i = 0; i < responseArray.length(); i++) {
                LaunchVehicle launchVehicle = new LaunchVehicle();

                JSONObject vehicleObj = responseArray.optJSONObject(i);
                launchVehicle.setLVName(vehicleObj.optString("LV_Name"));
                launchVehicle.setLVFamily(vehicleObj.optString("LV_Family"));
                launchVehicle.setLVSFamily(vehicleObj.optString("LV_SFamily"));
                launchVehicle.setLVManufacturer(vehicleObj.optString("LV_Manufacturer"));
                launchVehicle.setLVVariant(vehicleObj.optString("LV_Variant"));
                launchVehicle.setLVAlias(vehicleObj.optString("LV_Alias"));
                launchVehicle.setMinStage(vehicleObj.optInt("Min_Stage"));
                launchVehicle.setMaxStage(vehicleObj.optInt("Max_Stage"));
                launchVehicle.setLength(vehicleObj.optString("Length"));
                launchVehicle.setDiameter(vehicleObj.optString("Diameter"));
                launchVehicle.setLaunchMass(vehicleObj.optString("Launch_Mass"));
                launchVehicle.setLEOCapacity(vehicleObj.optString("LEO_Capacity"));
                launchVehicle.setGTOCapacity(vehicleObj.optString("GTO_Capacity"));
                launchVehicle.setTOThrust(vehicleObj.optString("TO_Thrust"));
                launchVehicle.setClass_(vehicleObj.optString("Class"));
                launchVehicle.setApogee(vehicleObj.optString("Apogee"));
                launchVehicle.setImageURL(vehicleObj.optString("ImageURL"));

                databaseManager.addPost(launchVehicle);
                Timber.v("addToDB - adding " + launchVehicle.getLVName() + "...");
            }
            //Success
            Timber.d("addToDB - Success! Database Size:  %s ", databaseManager.getCount());
            return 1;
        } catch (JSONException e) {
            Timber.e("RocketDataService - "+ e.getLocalizedMessage());
            //Error
            return 0;
        }
    }
}
