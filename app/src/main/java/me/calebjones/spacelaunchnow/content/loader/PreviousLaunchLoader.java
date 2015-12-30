package me.calebjones.spacelaunchnow.content.loader;

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
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.Location;
import me.calebjones.spacelaunchnow.content.models.LocationAgency;
import me.calebjones.spacelaunchnow.content.models.Mission;
import me.calebjones.spacelaunchnow.content.models.Pad;
import me.calebjones.spacelaunchnow.content.models.Rocket;
import me.calebjones.spacelaunchnow.content.models.RocketAgency;

/**
 * Class that parses the next 10 upcoming launches.
 */
public class PreviousLaunchLoader extends AsyncTask<String, Void, List<Launch>>{
    public static List<Launch> launchArrayList;
    public static String cache;
    public int count, total;


    @Override
    protected void onPreExecute() {
        launchArrayList = new ArrayList<>();
    }

    @Override
    protected List<Launch> doInBackground(String... params) {
        InputStream inputStream = null;
        Integer result = 0;
        HttpURLConnection urlConnection = null;

        try {
            Log.d(LaunchApplication.TAG + "Prev", params[0]);

            /* forming th java.net.URL object */
            URL url = new URL(params[0]);

            urlConnection = (HttpURLConnection) url.openConnection();

                /* for Get request */
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();

                /* 200 represents HTTP OK */
            if (statusCode ==  200) {

                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    response.append(line);
                }

                parseResult(response.toString());
                return launchArrayList;
            }else{
                return null;
            }

        } catch (Exception e) {
            Log.d(LaunchApplication.TAG + "Prev", e.getLocalizedMessage());
        }
        return null; //"Failed to fetch data!";
    }

    public static List<Launch> getWords(){
        if (launchArrayList == null){
            launchArrayList = new ArrayList<>();
        }
        return launchArrayList;
    }

    @Override
    protected void onPostExecute(List<Launch> result) {
            /* Download complete. Lets update UI */
        if (result != null) {
//                BlogFragment.setList(feedItemList);
            Log.d(LaunchApplication.TAG + "Prev", "Succeeded fetching data! - Launcher Loader");
        } else Log.e(LaunchApplication.TAG + "Prev", "Failed to fetch data!");
    }

    public void parseResult(String result) throws JSONException {
        try {
            /*Initialize array if null*/
            if (null == launchArrayList) {
                launchArrayList = new ArrayList<>();
            }

            JSONObject response = new JSONObject(result);
            count = response.optInt("count");
            total = response.optInt("total");
            JSONArray launchesArray = response.optJSONArray("launches");

            for (int i = 0; i < launchesArray.length(); i++) {
                JSONObject launchesObj = launchesArray.optJSONObject(i);
                JSONObject rocketObj = launchesObj.optJSONObject("rocket");
                JSONObject locationObj = launchesObj.optJSONObject("location");

                Launch launch = new Launch();
                Rocket rocket = new Rocket();
                Mission mission = new Mission();
                Location location = new Location();

                launch.setName(launchesObj.optString("name"));
                launch.setId(launchesObj.optInt("id"));
                launch.setNet(launchesObj.optString("net"));
                launch.setWindowstart(launchesObj.optString("windowstart"));
                launch.setWindowend(launchesObj.optString("windowend"));
                launch.setNetstamp(launchesObj.optInt("netstamp"));
                launch.setWsstamp(launchesObj.optInt("wsstamp"));
                launch.setWestamp(launchesObj.optInt("westamp"));
                launch.setStatus(launchesObj.optInt("status"));

                Log.d(LaunchApplication.TAG + "Prev", "Launch " + i + ": " + launch.getName());

                //Start Parsing Rockets
                if (rocketObj != null){
                    RocketAgency rocketAgency = new RocketAgency();

                    rocket.setId(rocketObj.optInt("id"));
                    rocket.setName(rocketObj.optString("name"));
                    rocket.setFamilyname(rocketObj.optString("familyname"));
                    rocket.setConfiguration(rocketObj.optString("configuration"));
                    Log.d(LaunchApplication.TAG + "Prev", "Launch " + i + ": Rocket  - " + rocket.getName());

                    JSONArray agencies = rocketObj.optJSONArray("agencies");
                    if (agencies != null){
                        List<RocketAgency> rocketList = new ArrayList<>();
                        for (int a = 0; a < agencies.length(); a++){
                            JSONObject agencyObj = launchesArray.optJSONObject(a);
                            rocketAgency.setName(agencyObj.optString("name"));
                            rocketAgency.setAbbrev(agencyObj.optString("abbrev"));
                            rocketAgency.setCountryCode(agencyObj.optString("countryCode"));
                            rocketAgency.setType(agencyObj.optInt("type"));
                            rocketAgency.setInfoURL(agencyObj.optString("infoURL"));
                            rocketAgency.setWikiURL(agencyObj.optString("wikiURL"));

                            Log.d(LaunchApplication.TAG + "Prev", "Launch " + i +
                                    ": Rocket Agency - " + rocketAgency.getName());
                            rocketList.add(rocketAgency);
                        }
                        rocket.setAgencies(rocketList);
                    }
                    launch.setRocket(rocket);
                }

                //Start Parsing Locations
                if (locationObj != null){
                    LocationAgency locationAgency = new LocationAgency();
                    Pad locationPads = new Pad();

                    JSONArray pads = locationObj.optJSONArray("pads");

                    if (pads != null){
                        List<Pad> locationPadsList = new ArrayList<>();
                        for (int a = 0; a < pads.length(); a++){
                            JSONObject padsObj = pads.optJSONObject(a);
                            location.setId(padsObj.optInt("id"));
                            location.setName(padsObj.optString("name"));
                            Log.d(LaunchApplication.TAG + "Prev", "Launch " + i +
                                    ": Location  - " + location.getName());
                            locationPads.setName(padsObj.optString("name"));
                            locationPads.setLatitude(padsObj.optDouble("latitude"));
                            locationPads.setLongitude(padsObj.optDouble("longitude"));
                            locationPads.setMapURL(padsObj.optString("mapURL"));
                            locationPads.setInfoURL(padsObj.optString("infoURL"));
                            locationPads.setWikiURL(padsObj.optString("wikiURL"));

                            Log.d(LaunchApplication.TAG + "Prev", "Launch " + i + ": Pad - " +
                                    locationPads.getName());
                            JSONArray padAgencies = padsObj.optJSONArray("agencies");
                            if (padAgencies != null){
                                List<LocationAgency> locationAgencies = new ArrayList<>();
                                for (int b = 0; b < padAgencies.length(); b++){
                                    JSONObject padAgenciesObj = padAgencies.optJSONObject(b);
                                    locationAgency.setName(padAgenciesObj.optString("name"));
                                    Log.d(LaunchApplication.TAG + "Prev", "Launch " + i
                                            + ": Pad Agency- " + locationAgency.getName());
                                    locationAgency.setAbbrev(padAgenciesObj.optString("abbrev"));
                                    locationAgency.setCountryCode(padAgenciesObj
                                            .optString("countryCode"));
                                    locationAgency.setType(padAgenciesObj.optInt("type"));
                                    locationAgency.setInfoURL(padAgenciesObj.optString("infoURL"));
                                    locationAgency.setWikiURL(padAgenciesObj.optString("wikiURL"));

                                    Log.d(LaunchApplication.TAG + "Prev", "Launch " + i
                                            + ": Pad Agency - " + locationPads.getName());
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
                    for (int c = 0; c < missions.length(); c++){
                        JSONObject missionObj = missions.optJSONObject(c);
                        mission.setId(missionObj.optInt("id"));
                        mission.setName(missionObj.optString("name"));
                        mission.setDescription(missionObj.optString("description"));

                        missionList.add(mission);
                        Log.d(LaunchApplication.TAG + "Prev", "Launch " + i + ": Mission  - " + missionList.get(c).getDescription());
                    }
                    launch.setMissions(missionList);
                }

                launchArrayList.add(launch);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
