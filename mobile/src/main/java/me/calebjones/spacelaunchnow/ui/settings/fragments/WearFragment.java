package me.calebjones.spacelaunchnow.ui.settings.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatSeekBar;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.GoogleApiClient;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.SwitchPreferences;
import me.calebjones.spacelaunchnow.content.jobs.UpdateWearJob;
import me.calebjones.spacelaunchnow.content.wear.WearWatchfaceManager;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;
import timber.log.Timber;

public class WearFragment extends BaseSettingFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private GoogleApiClient mGoogleApiClient;
    private SwitchPreferences switchPreferences;
    private WearWatchfaceManager wearWatchfaceManager;
    private static final String HOUR_KEY = "me.calebjones.spacelaunchnow.wear.hourmode";
    private static final String UTC_KEY = "me.calebjones.spacelaunchnow.wear.utcmode";
    private static final String BACKGROUND_KEY = "me.calebjones.spacelaunchnow.wear.background";

    //Fragment lifecycle methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wear_preferences);
        setUpPreferences();
        setName("Wear Fragment");
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
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Timber.i("Wear preference %s changed.", key);
        Analytics.getInstance().sendPreferenceEvent(key);

        UpdateWearJob.scheduleJobNow();
    }

    //Class Methods
    private void setUpPreferences() {
        Preference dynamicBackground = findPreference("supporter_dynamic_background");
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (SupporterHelper.isSupporter()) {
            dynamicBackground.setEnabled(true);
            dynamicBackground.setSelectable(true);
        } else {
            dynamicBackground.setEnabled(false);
            dynamicBackground.setSelectable(false);
            dynamicBackground.setTitle(dynamicBackground.getTitle() + getString(R.string.supporter_feature));
        }

        Preference blurSettings = findPreference("wear_blur_dialog");
        blurSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                final String key = preference.getKey();
                Analytics.getInstance().sendPreferenceEvent(key, "Preference clicked.");
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

                int blurProgress = sharedPreferences.getInt("BLUR_WEAR", Constants.DEFAULT_BLUR);
                final int radiusProgress = sharedPreferences.getInt("RADIUS_WEAR", Constants.DEFAULT_RADIUS);
                int dimProgress = sharedPreferences.getInt("DIM_WEAR", Constants.DEFAULT_DIM);
                int greyProgress = sharedPreferences.getInt("GREY_WEAR", Constants.DEFAULT_GREY);

                blurSeekBar.setProgress(blurProgress);
                radiusSeekBar.setProgress(radiusProgress);
                dimSeekBar.setProgress(dimProgress);
                greySeekBar.setProgress(greyProgress);

                View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
                View neutralAction = dialog.getActionButton(DialogAction.NEUTRAL);

                positiveAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String result = String.format("Blur %s - Radius %s - Dim %s - Grey %s", blurSeekBar.getProgress(), radiusSeekBar.getProgress(), dimSeekBar.getProgress(), greySeekBar.getProgress());
                        Analytics.getInstance().sendPreferenceEvent(key, result);
                        Timber.v(result);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("BLUR_WEAR", blurSeekBar.getProgress());
                        editor.putInt("RADIUS_WEAR", radiusSeekBar.getProgress());
                        editor.putInt("DIM_WEAR", dimSeekBar.getProgress());
                        editor.putInt("GREY_WEAR", greySeekBar.getProgress());
                        editor.apply();


                        UpdateWearJob.scheduleJobNow();
                        dialog.dismiss();
                    }
                });

                neutralAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Analytics.getInstance().sendPreferenceEvent(key, "Blur settings to default.");
                        Timber.v("Blur %s - Radius %s - Dim %s - Grey %s", blurSeekBar.getProgress(), radiusSeekBar.getProgress(), dimSeekBar.getProgress(), greySeekBar.getProgress());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("BLUR_WEAR", Constants.DEFAULT_BLUR);
                        editor.putInt("RADIUS_WEAR", Constants.DEFAULT_RADIUS);
                        editor.putInt("DIM_WEAR", Constants.DEFAULT_DIM);
                        editor.putInt("GREY_WEAR", Constants.DEFAULT_GREY);
                        editor.apply();

                        blurSeekBar.setProgress(Constants.DEFAULT_BLUR);
                        radiusSeekBar.setProgress(Constants.DEFAULT_RADIUS);
                        dimSeekBar.setProgress(Constants.DEFAULT_DIM);
                        greySeekBar.setProgress(Constants.DEFAULT_GREY);

                        UpdateWearJob.scheduleJobNow();
                    }
                });

                dialog.show();
                return true;
            }
        });
    }
}
