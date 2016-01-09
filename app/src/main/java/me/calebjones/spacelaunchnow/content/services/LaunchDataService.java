package me.calebjones.spacelaunchnow.content.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
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
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.calebjones.spacelaunchnow.LaunchApplication;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SharedPreference;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.content.models.Location;
import me.calebjones.spacelaunchnow.content.models.LocationAgency;
import me.calebjones.spacelaunchnow.content.models.Mission;
import me.calebjones.spacelaunchnow.content.models.Pad;
import me.calebjones.spacelaunchnow.content.models.Rocket;
import me.calebjones.spacelaunchnow.content.models.RocketAgency;
import timber.log.Timber;


/**
 * Created by cjones on 11/10/15.
 * If it is a new post then notify the user and save to DB.
 */
public class LaunchDataService extends IntentService {

    public static final int RESTART_CODE = 135325;
    public static final String EXTRA_NUM = "number";
    public static List<Launch> upcomingLaunchList;
    public static List<Launch> previousLaunchList;
    private AlarmManager alarmManager;
    private SharedPreferences sharedPref;
    private SharedPreference sharedPreference;

    public LaunchDataService() {
        super("LaunchDataService");
    }

    public void onCreate() {
        Timber.d("LaunchDataService - UpComingLaunchService onCreate");
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.sharedPreference = SharedPreference.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("LaunchDataService - Intent received:  %s ", intent.getAction());
        String action = intent.getAction();
        if (Strings.ACTION_GET_ALL.equals(action)){
            if (this.sharedPref.getBoolean("background", true)) {
                scheduleLaunchUpdates();
            }
            Timber.d("LaunchDataService - onHandleIntent: %s | Background: %s",action, this.sharedPref.getBoolean("background", true));
            getUpcomingLaunches();
            getPreviousLaunches(intent.getStringExtra("URL"));
        } else if (Strings.ACTION_GET_UP_LAUNCHES.equals(action)) {
            if (this.sharedPref.getBoolean("background", true)) {
                scheduleLaunchUpdates();
            }
            Timber.d("LaunchDataService - onHandleIntent: %s | Background: %s",action, this.sharedPref.getBoolean("background", true));
            getUpcomingLaunches();
        } else if (Strings.ACTION_GET_PREV_LAUNCHES.equals(action)) {
            Timber.d("LaunchDataService - onHandleIntent:  %s ", action);
            getPreviousLaunches(intent.getStringExtra("URL"));
        } else {
            Timber.e("LaunchDataService - onHandleIntent: ERROR - Unknown Intent %s", action);
        }
    }

    private void getPreviousLaunches(String sUrl) {
        LaunchDataService.this.cleanCachePrevious();
        InputStream inputStream = null;
        Integer result = 0;
        HttpURLConnection urlConnection = null;
        try {
            /* forming th java.net.URL object */
            URL url = new URL(sUrl);

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

                parsePreviousResult(response.toString());
                Timber.d("LaunchDataService - Previous Launches list:  %s ", previousLaunchList.size());
                    this.sharedPreference.setPreviousLaunches(previousLaunchList);
                if (!this.sharedPreference.getPreviousFirstBoot()) {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(Strings.ACTION_SUCCESS_PREV_LAUNCHES);
                    LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
                }
            }


        } catch (Exception e) {
            Timber.e("LaunchDataService - getPreviousLaunches ERROR: %s", e.getLocalizedMessage());
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_FAILURE_PREV_LAUNCHES);
            LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void getUpcomingLaunches() {
        LaunchDataService.this.cleanCacheUpcoming();
        InputStream inputStream = null;
        Integer result = 0;
        HttpURLConnection urlConnection = null;
        try {
            /* forming th java.net.URL object */
            String value = this.sharedPref.getString("upcoming_value", "5");
            URL url = new URL("https://launchlibrary.net/1.1.1/launch/next/" + value);

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
                Timber.d("LaunchDataService - Upcoming Launches list:  %s ", upcomingLaunchList.size());

                //TODO Remove this before release.
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
                mBuilder.setContentTitle("LaunchData Worked!")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setAutoCancel(true);

                NotificationManager mNotifyManager = (NotificationManager)
                        getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyManager.notify(Strings.NOTIF_ID, mBuilder.build());

                this.sharedPreference.setUpComingLaunches(upcomingLaunchList);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Strings.ACTION_SUCCESS_UP_LAUNCHES);
                LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
            }


        } catch (Exception e) {
            Timber.e("LaunchDataService - getUpcomingLaunches ERROR: %s", e.getLocalizedMessage());

            //TODO Remove this before release.
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mBuilder.setContentTitle("LaunchData Failed!")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setAutoCancel(true);

            NotificationManager mNotifyManager = (NotificationManager)
                    getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyManager.notify(Strings.NOTIF_ID, mBuilder.build());

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_FAILURE_UPC_LAUNCHES);
            LaunchDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void cleanCacheUpcoming() {
        this.sharedPreference.removeUpcomingLaunches();
    }

    private void cleanCachePrevious() {
        this.sharedPreference.removePreviousLaunches();
    }

    public void parseUpcomingResult(String result) throws JSONException {
        try {

            /*Initialize array if null*/
            upcomingLaunchList = new ArrayList<>();

            JSONObject response = new JSONObject(result);
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
                launch.setVidURL(launchesObj.optString("vidURL"));

                //Start Parsing Rockets
                if (rocketObj != null) {
                    RocketAgency rocketAgency = new RocketAgency();

                    rocket.setId(rocketObj.optInt("id"));
                    rocket.setName(rocketObj.optString("name"));
                    rocket.setFamilyname(rocketObj.optString("familyname"));
                    rocket.setConfiguration(rocketObj.optString("configuration"));

                    JSONArray agencies = rocketObj.optJSONArray("agencies");
                    if (agencies != null) {
                        List<RocketAgency> rocketList = new ArrayList<>();
                        for (int a = 0; a < agencies.length(); a++) {
                            JSONObject agencyObj = agencies.optJSONObject(a);
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
                    JSONArray pads = locationObj.optJSONArray("pads");

                    if (pads != null) {
                        List<Pad> locationPadsList = new ArrayList<>();
                        for (int a = 0; a < pads.length(); a++) {
                            JSONObject padsObj = pads.optJSONObject(a);
                            location.setId(padsObj.optInt("id"));
                            location.setName(padsObj.optString("name"));
                            Pad locationPads = new Pad();
                            locationPads.setName(padsObj.optString("name"));
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
                        mission.setId(missionObj.optInt("id"));
                        mission.setName(missionObj.optString("name"));
                        mission.setDescription(missionObj.optString("description"));

                        missionList.add(mission);
                    }
                    launch.setMissions(missionList);
                }

                upcomingLaunchList.add(launch);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parsePreviousResult(String result) throws JSONException {
        try {

            /*Initialize array if null*/
            previousLaunchList = new ArrayList<>();

            JSONObject response = new JSONObject(result);
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
                launch.setVidURL(launchesObj.optString("vidURL"));

                //Start Parsing Rockets
                if (rocketObj != null) {
                    RocketAgency rocketAgency = new RocketAgency();

                    rocket.setId(rocketObj.optInt("id"));
                    rocket.setName(rocketObj.optString("name"));
                    rocket.setFamilyname(rocketObj.optString("familyname"));
                    rocket.setConfiguration(rocketObj.optString("configuration"));

                    JSONArray agencies = rocketObj.optJSONArray("agencies");
                    if (agencies != null) {
                        List<RocketAgency> rocketList = new ArrayList<>();
                        for (int a = 0; a < agencies.length(); a++) {
                            JSONObject agencyObj = agencies.optJSONObject(a);
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

                    JSONArray pads = locationObj.optJSONArray("pads");

                    if (pads != null) {
                        List<Pad> locationPadsList = new ArrayList<>();
                        for (int a = 0; a < pads.length(); a++) {
                            JSONObject padsObj = pads.optJSONObject(a);
                            location.setId(padsObj.optInt("id"));
                            location.setName(padsObj.optString("name"));

                            locationPads.setName(padsObj.optString("name"));
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
                        mission.setId(missionObj.optInt("id"));
                        mission.setName(missionObj.optString("name"));
                        mission.setDescription(missionObj.optString("description"));

                        missionList.add(mission);
                    }
                    launch.setMissions(missionList);
                }

                previousLaunchList.add(launch);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            Timber.d("LaunchDataService - Notification Timer: %s to millisecond %s",notificationTimer, interval);

            long nextUpdate = Calendar.getInstance().getTimeInMillis() + interval;
            Timber.d("LaunchDataService - Scheduling Alarm at %s with interval of %s",nextUpdate, interval);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, nextUpdate, interval,
                    PendingIntent.getBroadcast(this, 165435, new Intent(Strings.ACTION_UPDATE_UP_LAUNCHES), 0));
        } else {
            Timber.e("LaunchDataService - Error setting alarm, failed to change %s to milliseconds", notificationTimer);
        }
    }
}
