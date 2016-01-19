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

import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Agency;
import me.calebjones.spacelaunchnow.content.models.Family;
import me.calebjones.spacelaunchnow.content.models.Rocket;
import me.calebjones.spacelaunchnow.content.models.Strings;
import timber.log.Timber;


/**
 * This grabs rockets from LaunchLibrary
 *
 */
public class AgencyDataService extends IntentService {

    public static List<Agency> agencyList;
    private SharedPreferences sharedPref;
    private SharedPreference sharedPreference;

    public AgencyDataService() {
        super("AgencyDataService");
    }

    public void onCreate() {
        Timber.d("AgencyDataService - onCreate");
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.sharedPreference = SharedPreference.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("AgencyDataService - Intent received!");
        if (intent != null) {
            String action = intent.getAction();
            if (Strings.ACTION_GET_AGENCY.equals(action)) {
                getAgency();
            }
        }
    }

    private void getAgency() {
        cleanAgencyCache();
        InputStream inputStream = null;
        Integer result = 0;
        HttpURLConnection urlConnection = null;
        try {
            /* forming th java.net.URL object */
            String value = this.sharedPref.getString("value", "5");
            URL url = new URL(Strings.AGENCY_URL);

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
                Timber.d("LaunchDataService - Upcoming Launches list:  %s ", agencyList.size());
                this.sharedPreference.setAgenciesList(agencyList);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Strings.ACTION_SUCCESS_VEHICLES);
                AgencyDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
            }

        } catch (Exception e) {
            Timber.e("VehicleDataService - ERROR: ", e.getLocalizedMessage());
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_FAILURE_AGENCY);
            AgencyDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void parseUpcomingResult(String result) throws JSONException {
        try {
            /*Initialize array if null*/
            agencyList = new ArrayList<>();

            JSONObject response = new JSONObject(result);
            JSONArray rocketsArray = response.optJSONArray("rockets");

            for (int i = 0; i < rocketsArray.length(); i++) {
                Agency agency = new Agency();
                JSONObject rocketObj = rocketsArray.optJSONObject(i);

                agency.setId(rocketObj.optInt("id", 0));
                agency.setName(rocketObj.optString("name"));
                agency.setInfoURL(rocketObj.optString("infoURL"));
                agency.setWikiURL(rocketObj.optString("wikiURL"));

                agencyList.add(agency);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void cleanAgencyCache() {
        this.sharedPreference.removeAgencies();
    }

}
