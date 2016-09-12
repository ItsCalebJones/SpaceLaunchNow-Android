package me.calebjones.spacelaunchnow.settings.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;

import de.mrapp.android.preference.activity.PreferenceFragment;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.supporter.SupporterHelper;
import timber.log.Timber;

public class WearFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private SwitchPreferences switchPreferences;
    private Context context;
    private static final String HOUR_KEY = "me.calebjones.spacelaunchnow.wear.hourmode";
    private static final String BACKGROUND_KEY = "me.calebjones.spacelaunchnow.wear.background";

    //Fragment lifecycle methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wear_preferences);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();

        setUpPreferences();
    }

    @Override
    public void onResume() {
        Timber.v("onResume - setting OnSharedPreferenceChangeListener");
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        Timber.v("onPause - removing OnSharedPreferenceChangeListener");
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Timber.d("Google Client Disconnect");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Timber.i("Wear preference %s changed.", key);

        if (key.equals("wear_hour_mode")) {
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/config");
            putDataMapReq.getDataMap().putBoolean(HOUR_KEY, sharedPreferences.getBoolean(key, false));
            putDataMapReq.getDataMap().putLong("time", new Date().getTime());

            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
        }
    }

    //Google API client methods
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Class Methods
    private void setUpPreferences() {
        Preference dynamicBackground = findPreference("supporter_dynamic_background");
        dynamicBackground.setEnabled(false);
        dynamicBackground.setSelectable(false);
        dynamicBackground.setTitle(dynamicBackground.getTitle() + " (Coming Soon)");

        //TODO implement dynamic background
        if (SupporterHelper.isSupporter()) {
            dynamicBackground.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    return true;
                }
            });
        } else {
            dynamicBackground.setEnabled(false);
            dynamicBackground.setSelectable(false);
            dynamicBackground.setTitle(dynamicBackground.getTitle() + " (Supporter Feature)");
        }
    }
}
