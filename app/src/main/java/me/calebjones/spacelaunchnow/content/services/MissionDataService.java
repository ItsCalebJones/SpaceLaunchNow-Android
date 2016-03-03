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
import java.util.Collections;
import java.util.List;

import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.Mission;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.utils.Utils;
import timber.log.Timber;


/**
 * Created by cjones on 11/10/15.
 * If it is a new post then notify the user and save to DB.
 */
public class MissionDataService extends IntentService {

    public static List<Mission> missionList;
    private SharedPreferences sharedPref;
    private SharedPreference sharedPreference;

    public MissionDataService() {
        super("MissionDataService");
    }

    public void onCreate() {
        Timber.d("MissionDataService - onCreate");
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.sharedPreference = SharedPreference.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("MissionDataService - Intent received:  %s ", intent.getAction());
        getMissionLaunches();
    }

    private void getMissionLaunches() {
        MissionDataService.this.cleanCachePrevious();
        InputStream inputStream = null;
        Integer result = 0;
        HttpURLConnection urlConnection = null;
        try {
            /* forming th java.net.URL object */
            URL url = new URL(Strings.MISSION_URL);

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

                parseMissionsResult(response.toString());
                Timber.d("getMissionLaunches - Mission list:  %s ", missionList.size());

                Collections.reverse(missionList);
                this.sharedPreference.setMissionList(missionList);
                this.sharedPreference.syncMissions();

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Strings.ACTION_SUCCESS_MISSIONS);
                MissionDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
            }


        } catch (Exception e) {
            Timber.e("getMissionLaunches ERROR: %s", e.getLocalizedMessage());
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_FAILURE_MISSIONS);
            MissionDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void cleanCachePrevious() {
        this.sharedPreference.removeMissionsList();
    }

    public void parseMissionsResult(String result) throws JSONException {
        try {

            /*Initialize array if null*/
            missionList = new ArrayList<>();

            JSONObject response = new JSONObject(result);
            JSONArray missionArray = response.optJSONArray("missions");

            for (int i = 0; i < missionArray.length(); i++) {
                JSONObject missionObj = missionArray.optJSONObject(i);
                JSONObject launchObj  = missionObj.optJSONObject("launch");

                Mission mission = new Mission();
                mission.setId(missionObj.optInt("id"));
                mission.setType(missionObj.optInt("type"));
                mission.setTypeName(Utils.getTypeName(missionObj.optInt("type")));
                mission.setName(missionObj.optString("name"));
                mission.setDescription(missionObj.optString("description"));
                mission.setInfoURL(missionObj.optString("infoURL"));
                mission.setWikiURL(missionObj.optString("wikiURL"));

                if (launchObj != null) {
                 Launch launch = new Launch();
                    launch.setId(launchObj.optInt("id", 0));
                    launch.setName(launchObj.optString("name"));
                    launch.setNet(launchObj.optString("net"));
                    mission.setLaunch(launch);
                }
                missionList.add(mission);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
