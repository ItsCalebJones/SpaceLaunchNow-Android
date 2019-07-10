package me.calebjones.spacelaunchnow.ui.debug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;


import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.content.calendar.CalendarSyncManager;
import me.calebjones.spacelaunchnow.common.prefs.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.Products;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.utils.FileUtils;
import timber.log.Timber;

public class DebugPresenter implements DebugContract.Presenter {

    private final DebugContract.View debugView;
    private DebugContract.Navigator navigator;
    private ListPreferences sharedPreference;
    private Realm realm;
    private Context context;

    public DebugPresenter(Context context, DebugContract.View view, ListPreferences preferences) {
        debugView = view;
        debugView.setPresenter(this);
        sharedPreference = preferences;
        this.context = context;
    }

    @Override
    public void onHomeClicked() {
        navigator.goHome();
    }

    @Override
    public void setNavigator(@NonNull DebugContract.Navigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public void toggleSupporterSwitch(boolean selected) {
        debugView.showSupporterSnackbar(selected);
        sharedPreference.setDebugSupporter(selected);
        realm = Realm.getDefaultInstance();
        if (selected) {
            realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(SupporterHelper.getProduct(SupporterHelper.SKU_TWO_DOLLAR)));
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(SupporterHelper.getProduct(SupporterHelper.SKU_TWO_DOLLAR));
            realm.commitTransaction();
            realm.close();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("weather", true);
            editor.apply();
        } else {
            realm.executeTransaction(realm -> realm.delete(Products.class));
            realm.close();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("weather", false);
            editor.apply();
        }
    }

    @Override
    public void endpointSelectorClicked(String selection) {
        sharedPreference.setNetworkEndpoint(selection);
        DataClient.create(context.getString(R.string.sln_token), selection);

        //Delete from Database
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(realm -> {
            RealmResults<Launch> results = realm.where(Launch.class).findAll();
            results.deleteAllFromRealm();
        }, () -> {
        });
        realm.close();

    }

    @Override
    public void syncNextLaunchClicked(Context context) {
    }

    @Override
    public void jobEventButtonClicked(Context context) {

    }

    @Override
    public void syncBackgroundSyncClicked(Context context) {
        CalendarSyncManager calendarSyncManager = new CalendarSyncManager(context);
        calendarSyncManager.syncAllEevnts();
    }


    @Override
    public void syncVehiclesClicked(Context context) {
    }

    @Override
    public void downloadLogsClicked(Activity activity) {
        File textFile = new File(activity.getCacheDir(), "success.txt");
        Uri uriForFile = FileProvider.getUriForFile(activity, "me.calebjones.spacelaunchnow", textFile);
        activity.grantUriPermission("me.calebjones.spacelaunchnow", uriForFile, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = ShareCompat.IntentBuilder.from(activity)
                .setStream(uriForFile) // uri from FileProvider
                .setType("text/plain")
                .getIntent()
                .setAction(Intent.ACTION_SEND) //Change if needed
                .setDataAndType(uriForFile, "text/plain")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activity.startActivity(Intent.createChooser(intent, "Save File"));
    }

    @Override
    public void deleteFilesClicked(Context context) {
        try {
            FileUtils.delete(FileUtils.getSuccessFile(context));
            debugView.showSnackbarMessage("Successfully deleted file.");
        } catch (IOException e) {
            Timber.e(e.getLocalizedMessage());
        }
    }

    @Override
    public boolean getSupporterStatus() {
        return sharedPreference.isDebugSupporterEnabled();
    }

    @Override
    public void start() {

    }
}
