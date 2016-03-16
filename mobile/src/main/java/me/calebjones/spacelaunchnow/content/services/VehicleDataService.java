package me.calebjones.spacelaunchnow.content.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.calebjones.spacelaunchnow.content.database.DatabaseManager;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Family;
import me.calebjones.spacelaunchnow.content.models.Rocket;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.RocketDetails;
import timber.log.Timber;


/**
 * This grabs details from my own hosted JSON file.
 */
public class VehicleDataService extends IntentService {

    public static List<Rocket> vehicleList;
    private SharedPreferences sharedPref;
    private SharedPreference sharedPreference;

    public VehicleDataService() {
        super("VehicleDataService");
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
        Timber.d("VehicleDataService - Intent received!");
        if (intent != null) {
            String action = intent.getAction();
            if (Strings.ACTION_GET_VEHICLES_DETAIL.equals(action)) {
                sharedPreference.setLastVehicleUpdate(System.currentTimeMillis());
                getVehicleDetails();
                getVehicles();
            }
            if (Strings.ACTION_GET_VEHICLES.equals(action)) {
                getVehicles();
            }
        }
    }

    private void getVehicleDetails() {
        InputStream inputStream = null;
        Integer result = 0;
        HttpURLConnection urlConnection = null;
        try {
            /* forming th java.net.URL object */
            String value = this.sharedPref.getString("value", "5");
            URL url = new URL("http://calebjones.me/app/launchvehicle.json");

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

                result = addToDB(response);
                if (result == 1) {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(Strings.ACTION_SUCCESS_VEHICLE_DETAILS);
                    VehicleDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
                } else {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(Strings.ACTION_FAILURE_VEHICLE_DETAILS);
                    VehicleDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
                }
            }


        } catch (Exception e) {
            Timber.e("VehicleDataService - ERROR: %s", e.getLocalizedMessage());
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_FAILURE_VEHICLE_DETAILS);
            VehicleDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void getVehicles() {
        VehicleDataService.this.cleanVehiclesCache();
        InputStream inputStream = null;
        Integer result = 0;
        HttpURLConnection urlConnection = null;
        try {
            /* forming th java.net.URL object */
            URL url = new URL(Strings.VEHICLE_URL);

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

                parseUpcomingResult(response.toString());
                Timber.d("Vehicle list:  %s ", vehicleList.size());
                this.sharedPreference.setVehiclesList(vehicleList);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Strings.ACTION_SUCCESS_VEHICLES);
                VehicleDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
            }


        } catch (Exception e) {
            Timber.e("VehicleDataService - ERROR: %s", e.getLocalizedMessage());
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_FAILURE_VEHICLES);
            VehicleDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void parseUpcomingResult(String result) throws JSONException {
        try {
            /*Initialize array if null*/
            vehicleList = new ArrayList<>();
            JSONObject response = new JSONObject(result);
            JSONArray rocketsArray = response.optJSONArray("rockets");
            for (int i = 0; i < rocketsArray.length(); i++) {
                Rocket rocket = new Rocket();
                JSONObject rocketObj = rocketsArray.optJSONObject(i);
                JSONObject familyObj = rocketObj.optJSONObject("family");

                rocket.setId(rocketObj.optInt("id", 0));
                rocket.setName(rocketObj.optString("name"));
                rocket.setConfiguration(rocketObj.optString("configuration"));
                rocket.setInfoURL(rocketObj.optString("infoURL"));
                rocket.setWikiURL(rocketObj.optString("wikiURL"));
                rocket.setImageURL(rocketObj.optString("imageURL"));

                if (familyObj != null) {
                    Family family = new Family();
                    family.setId(familyObj.optInt("id", 0));
                    family.setName(familyObj.optString("name"));
                    family.setAgencies(familyObj.optString("agencies"));

                    rocket.setFamily(family);
                }
                vehicleList.add(rocket);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void cleanVehiclesCache() {
        this.sharedPreference.removeVehicles();
    }

    private Integer addToDB(StringBuilder response) {
        DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());

        try {
            JSONArray responseArray = new JSONArray(response.toString());
            Timber.d("addToDB - Database Size: %s (Expect 0 here)", databaseManager.getCount());
            if (databaseManager.getCount() > 0){
                databaseManager.rebuildDB(databaseManager.getWritableDatabase());
            }
            Timber.d("addToDB - Adding: %s...", response.toString().substring(0, (response.toString().length() / 2)));
            for (int i = 0; i < responseArray.length(); i++) {
                RocketDetails launchVehicle = new RocketDetails();

                JSONObject vehicleObj = responseArray.optJSONObject(i);
                launchVehicle.setDescription(vehicleObj.optString("Description"));
                launchVehicle.setWikiURL(vehicleObj.optString("WikiURL"));
                launchVehicle.setInfoURL(vehicleObj.optString("InfoURL"));
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
            Timber.e("VehicleDataService - " + e.getLocalizedMessage());
            //Error
            return 0;
        }
    }
}
