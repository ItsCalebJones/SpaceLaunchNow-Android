package me.calebjones.spacelaunchnow.ui.settings.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.services.UpdateWearService;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.content.models.Constants.DEFAULT_BLUR;
import static me.calebjones.spacelaunchnow.content.models.Constants.DEFAULT_DIM;
import static me.calebjones.spacelaunchnow.content.models.Constants.DEFAULT_GREY;
import static me.calebjones.spacelaunchnow.content.models.Constants.DEFAULT_RADIUS;

public class WearFragment extends BaseSettingFragment implements SharedPreferences.OnSharedPreferenceChangeListener,
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

        getActivity().startService(new Intent(getActivity(), UpdateWearService.class));
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
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //TODO implement dynamic background
        if (SupporterHelper.isSupporter()) {
            dynamicBackground.setEnabled(true);
            dynamicBackground.setSelectable(true);
        } else {
            dynamicBackground.setEnabled(false);
            dynamicBackground.setSelectable(false);
            dynamicBackground.setTitle(dynamicBackground.getTitle() + " (Supporter Feature)");
        }

        Preference blurSettings = findPreference("wear_blur_dialog");
        blurSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title("Blur Settings")
                        .customView(R.layout.blur_settings, true)
                        .positiveText("Ok")
                        .neutralText("Default")
                        .negativeText("Cancel")
                        .positiveColor(ContextCompat.getColor(getActivity(), R.color.primary))
                        .neutralColor(ContextCompat.getColor(getActivity(), R.color.primary_light))
                        .build();

                final AppCompatSeekBar blurSeekBar = (AppCompatSeekBar) dialog.getCustomView().findViewById(R.id.blur_seekbar);
                final AppCompatSeekBar radiusSeekBar = (AppCompatSeekBar) dialog.getCustomView().findViewById(R.id.radius_seekbar);
                final AppCompatSeekBar dimSeekBar = (AppCompatSeekBar) dialog.getCustomView().findViewById(R.id.dim_seekbar);
                final AppCompatSeekBar greySeekBar = (AppCompatSeekBar) dialog.getCustomView().findViewById(R.id.grey_seekbar);

                int blurProgress = sharedPreferences.getInt("BLUR_WEAR", DEFAULT_BLUR);
                final int radiusProgress = sharedPreferences.getInt("RADIUS_WEAR", DEFAULT_RADIUS);
                int dimProgress = sharedPreferences.getInt("DIM_WEAR", DEFAULT_DIM);
                int greyProgress = sharedPreferences.getInt("GREY_WEAR", DEFAULT_GREY);

                blurSeekBar.setProgress(blurProgress);
                radiusSeekBar.setProgress(radiusProgress);
                dimSeekBar.setProgress(dimProgress);
                greySeekBar.setProgress(greyProgress);

                View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);

                positiveAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Timber.v("Blur %s - Radius %s - Dim %s - Grey %s", blurSeekBar.getProgress(), radiusSeekBar.getProgress(), dimSeekBar.getProgress(), greySeekBar.getProgress());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("BLUR_WEAR", blurSeekBar.getProgress());
                        editor.putInt("RADIUS_WEAR", radiusSeekBar.getProgress());
                        editor.putInt("DIM_WEAR", dimSeekBar.getProgress());
                        editor.putInt("GREY_WEAR", greySeekBar.getProgress());
                        editor.apply();


                        getActivity().startService(new Intent(getActivity(), UpdateWearService.class));
                        dialog.dismiss();
                    }
                });

                neutralAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Timber.v("Blur %s - Radius %s - Dim %s - Grey %s", blurSeekBar.getProgress(), radiusSeekBar.getProgress(), dimSeekBar.getProgress(), greySeekBar.getProgress());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("BLUR_WEAR", DEFAULT_BLUR);
                        editor.putInt("RADIUS_WEAR", DEFAULT_RADIUS);
                        editor.putInt("DIM_WEAR", DEFAULT_DIM);
                        editor.putInt("GREY_WEAR", DEFAULT_GREY);
                        editor.apply();

                        blurSeekBar.setProgress(DEFAULT_BLUR);
                        radiusSeekBar.setProgress(DEFAULT_RADIUS);
                        dimSeekBar.setProgress(DEFAULT_DIM);
                        greySeekBar.setProgress(DEFAULT_GREY);
                    }
                });

                dialog.show();
                return true;
            }
        });
    }
}
