package me.calebjones.spacelaunchnow.content.loader;

import android.content.Context;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import me.calebjones.spacelaunchnow.content.database.DatabaseManager;
import me.calebjones.spacelaunchnow.content.models.natives.RocketDetails;
import timber.log.Timber;


public class VehicleLoader extends AsyncTask<String, Void, Integer> {
    public static List<RocketDetails> vehicleList;

    private Context mContext;
    public VehicleLoader (Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream inputStream = null;
        Integer result = 0;
        HttpURLConnection urlConnection = null;

        try {
            Timber.d(params[0]);

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

                result = addToDB(response);

                return result;
            } else {
                return 0;
            }

        } catch (Exception e) {
            Timber.d(e.getLocalizedMessage());
        }
        return 0; //"Failed to fetch data!";
    }

    private Integer addToDB(StringBuilder response) {
        DatabaseManager databaseManager = new DatabaseManager(mContext);

        try {
            JSONArray responseArray = new JSONArray(response.toString());
            for (int i = 0; i < responseArray.length(); i++) {
                RocketDetails launchVehicle = new RocketDetails();

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
            }
            //Success
            return 1;
        } catch (JSONException e) {
            Crashlytics.logException(e);
            Timber.e(e.getLocalizedMessage());
            //Error
            return 0;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        Timber.d("Vehicle Loader: %s ", result);
    }
}
