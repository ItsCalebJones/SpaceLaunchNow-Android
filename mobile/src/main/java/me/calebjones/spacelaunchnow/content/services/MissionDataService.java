package me.calebjones.spacelaunchnow.content.services;

import android.content.Intent;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.interfaces.LibraryRequestInterface;
import me.calebjones.spacelaunchnow.content.models.Strings;
import me.calebjones.spacelaunchnow.content.models.realm.MissionRealm;
import me.calebjones.spacelaunchnow.content.responses.launchlibrary.MissionResponse;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;



//TODO point to library data service
public class MissionDataService extends BaseService {

    public MissionDataService() {
        super("MissionDataService");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.listPreference = ListPreferences.getInstance(getApplicationContext());
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return super.onStartCommand(intent, flags, startId);
    }

    //TODO Write two handle cases for getMissionsLaunches() and getMissionByID()
    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("MissionDataService - Intent received:  %s ", intent.getAction());

        // Create a new empty instance of Realm
        mRealm = Realm.getDefaultInstance();
        getMissionLaunches();
        mRealm.close();
    }

    private void getMissionLaunches() {
        LibraryRequestInterface request = retrofit.create(LibraryRequestInterface.class);
        Call<MissionResponse> call;
        Response<MissionResponse> launchResponse;
        RealmList<MissionRealm> items = new RealmList<>();
        int offset = 0;
        int total = 10;
        int count;

        try {
            while (total != offset) {
                if (listPreference.isDebugEnabled()) {
                    call = request.getDebugAllMissions(offset);
                } else {
                    call = request.getAllMisisons(offset);
                }
                launchResponse = call.execute();
                total = launchResponse.body().getTotal();
                count = launchResponse.body().getCount();
                offset = offset + count;
                Timber.v("Count: %s", offset);
                Collections.addAll(items, launchResponse.body().getMissions());

                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(items);
                mRealm.commitTransaction();
            }

            Timber.v("Success!");

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Strings.ACTION_SUCCESS_MISSIONS);

            MissionDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);

        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());

            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("error", e.getLocalizedMessage());
            broadcastIntent.setAction(Strings.ACTION_FAILURE_MISSIONS);

            MissionDataService.this.getApplicationContext().sendBroadcast(broadcastIntent);
        }
    }

    private void getMissionById(int id) {
        LibraryRequestInterface request = retrofit.create(LibraryRequestInterface.class);
        Call<MissionResponse> call;

        if (listPreference.isDebugEnabled()) {
            call = request.getDebugMissionByID(id);
        } else {
            call = request.getMissionByID(id);
        }

        Response<MissionResponse> launchResponse;
        try {
            launchResponse = call.execute();
            if (launchResponse.isSuccessful()) {
                RealmList<MissionRealm> items = new RealmList<>(launchResponse.body().getMissions());

                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(items);
                mRealm.commitTransaction();
            }
        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());
        }
    }
}
