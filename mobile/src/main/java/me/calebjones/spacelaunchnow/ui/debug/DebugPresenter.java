package me.calebjones.spacelaunchnow.ui.debug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.content.services.LaunchDataService;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.Launch;
import me.calebjones.spacelaunchnow.data.models.Products;
import me.calebjones.spacelaunchnow.data.networking.DataClient;
import me.calebjones.spacelaunchnow.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.utils.FileUtils;
import timber.log.Timber;

public class DebugPresenter implements DebugContract.Presenter {

    private final DebugContract.View debugView;
    private DebugContract.Navigator navigator;
    private ListPreferences sharedPreference;
    private Realm realm;

    public DebugPresenter(DebugContract.View view, ListPreferences preferences) {
        debugView = view;
        debugView.setPresenter(this);
        sharedPreference = preferences;
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
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(SupporterHelper.getProduct(SupporterHelper.SKU_TWO_DOLLAR));
                }
            });
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(Products.class);
                }
            });
        }
        realm.close();
    }

    @Override
    public void toggleDebugLaunchesClicked(boolean selected, Context context) {
        debugView.showDebugLaunchSnackbar(selected);
        sharedPreference.setDebugLaunch(selected);
        if (selected) {
            sharedPreference.setDebugLaunch(true);
            DataClient.create("dev");
        } else {
            sharedPreference.setDebugLaunch(false);
            DataClient.create("1.2.1");
        }


        //Delete from Database
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Launch> results = realm.where(Launch.class).findAll();
                results.deleteAllFromRealm();
            }
        });
        realm.close();

        context.startService(new Intent(context, LaunchDataService.class).setAction(Constants.ACTION_GET_ALL_DATA));
    }

    @Override
    public void syncNextLaunchClicked(Context context) {
        context.startService(new Intent(context, LaunchDataService.class).setAction(Constants.ACTION_GET_NEXT_LAUNCH));
    }

    @Override
    public void syncBackgroundSyncClicked(Context context) {
        context.startService(new Intent(context, LaunchDataService.class).setAction(Constants.ACTION_UPDATE_BACKGROUND));
    }

    @Override
    public void syncVehiclesClicked(Context context) {
        context.startService(new Intent(context, LaunchDataService.class).setAction(Constants.ACTION_GET_VEHICLES_DETAIL));
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
    public boolean getDebugStatus() {
        return sharedPreference.isDebugEnabled();
    }

    @Override
    public void start() {

    }
}
