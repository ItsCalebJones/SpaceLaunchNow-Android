package me.calebjones.spacelaunchnow.content.loader;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.content.database.DatabaseManager;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.LaunchVehicle;
import me.calebjones.spacelaunchnow.ui.activity.LaunchDetail;

/**
 * Created by cjones on 12/24/15.
 */
public class VehicleLoader extends AsyncTask<String, Void, String> {
    public static List<LaunchVehicle> vehicleList;

    private Context mContext;
    public VehicleLoader (Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {
        InputStream inputStream = null;
        Integer result = 0;
        HttpURLConnection urlConnection = null;

        try {
            Log.d(LaunchApplication.TAG, params[0]);

            /* forming th java.net.URL object */
            URL url = new URL(params[0]);

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

                return response.toString();
            } else {
                return null;
            }

        } catch (Exception e) {
            Log.d(LaunchApplication.TAG, e.getLocalizedMessage());
        }
        return null; //"Failed to fetch data!";
    }

    @Override
    protected void onPostExecute(String result) {
        DatabaseManager databaseManager = new DatabaseManager(mContext);
        try {
            JSONArray response = new JSONArray(result);
            for (int i = 0; i < response.length(); i++) {
                LaunchVehicle launchVehicle = new LaunchVehicle();

                JSONObject vehicleObj = response.optJSONObject(i);
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
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
