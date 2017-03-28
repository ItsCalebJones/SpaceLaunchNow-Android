package me.calebjones.spacelaunchnow.content.services;

import android.content.Intent;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.realm.Mission;
import me.calebjones.spacelaunchnow.data.networking.interfaces.LibraryService;
import me.calebjones.spacelaunchnow.content.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.UpdateRecord;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.MissionResponse;
import me.calebjones.spacelaunchnow.utils.Analytics;
import me.calebjones.spacelaunchnow.utils.FileUtils;
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
        
        getMissions();
        mRealm.close();
    }

    private void getMissions() {
        LibraryService request = retrofit.create(LibraryService.class);
        Call<MissionResponse> call = null;
        Response<MissionResponse> launchResponse;
        RealmList<Mission> items = new RealmList<>();
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
                if (launchResponse.isSuccessful()) {
                    total = launchResponse.body().getTotal();
                    count = launchResponse.body().getCount();
                    offset = offset + count;
                    Timber.v("Count: %s", offset);
                    Collections.addAll(items, launchResponse.body().getMissions());
                } else {
                    throw new IOException(launchResponse.errorBody().string());
                }

                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(items);
                mRealm.commitTransaction();
            }

            Timber.v("Success!");

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Constants.ACTION_SUCCESS_MISSIONS);

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UpdateRecord updateRecord = new UpdateRecord();
                    updateRecord.setType(Constants.ACTION_GET_MISSION);
                    updateRecord.setDate(new Date());
                    updateRecord.setSuccessful(true);
                    realm.copyToRealmOrUpdate(updateRecord);
                }
            });

            FileUtils.saveSuccess(true, Constants.ACTION_GET_MISSION, this);

            Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_MISSION, call.request().url().toString(), true);

            this.sendBroadcast(broadcastIntent);

        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());

            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UpdateRecord updateRecord = new UpdateRecord();
                    updateRecord.setType(Constants.ACTION_GET_MISSION);
                    updateRecord.setDate(new Date());
                    updateRecord.setSuccessful(false);
                    realm.copyToRealmOrUpdate(updateRecord);
                }
            });

            FileUtils.saveSuccess(false, Constants.ACTION_GET_MISSION  + " " + e.getLocalizedMessage(), this);

            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("error", e.getLocalizedMessage());
            broadcastIntent.setAction(Constants.ACTION_FAILURE_MISSIONS);

            Analytics.from(this).sendNetworkEvent(Constants.ACTION_GET_MISSION, call.request().url().toString(), false, e.getLocalizedMessage());

            this.sendBroadcast(broadcastIntent);
        }
    }

    private void getMissionById(int id) {
        LibraryService request = retrofit.create(LibraryService.class);
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
                RealmList<Mission> items = new RealmList<>(launchResponse.body().getMissions());

                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(items);
                mRealm.commitTransaction();
            }
        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());
        }
    }
}
