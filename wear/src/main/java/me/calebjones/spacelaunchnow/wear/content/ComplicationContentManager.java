package me.calebjones.spacelaunchnow.wear.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Handler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmList;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.networking.RetrofitBuilder;
import me.calebjones.spacelaunchnow.data.networking.interfaces.WearService;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchWearResponse;
import me.calebjones.spacelaunchnow.wear.model.LaunchCategories;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;


public class ComplicationContentManager {

    // The minimum network bandwidth required by the app for high-bandwidth operations.
    private static final int MIN_NETWORK_BANDWIDTH_KBPS = 100;

    // Handler for dealing with network connection timeouts.
    private Handler mHandler;

    // Intent action for sending the user directly to the add Wi-Fi network activity.
    private static final String ACTION_ADD_NETWORK_SETTINGS =
            "com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS";

    // Message to notify the network request timout handler that too much time has passed.
    private static final int MESSAGE_CONNECTIVITY_TIMEOUT = 1;

    // How long the app should wait trying to connect to a sufficient high-bandwidth network before
    // asking the user to add a new Wi-Fi network.
    private static final long NETWORK_CONNECTIVITY_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(5);

    private Realm realm;
    private ConnectivityManager mConnectivityManager;
    private Retrofit retrofit;
    private ContentCallback contentCallback;
    private SharedPreferences sharedPreferences;
    private Context context;
    SharedPreferences.Editor prefsEditor;

    public ComplicationContentManager(Context context, ContentCallback callback) {
        this.context = context;
        this.contentCallback = callback;
        realm = Realm.getDefaultInstance();
        retrofit = RetrofitBuilder.getWearRetrofit();
        sharedPreferences = context.getSharedPreferences("timestamp", 0);
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    // Determine if there is a high-bandwidth network exists. Checks both the active
    // and bound networks. Returns false if no network is available (low or high-bandwidth).
    public boolean isNetworkAvailable() {
        Network network = mConnectivityManager.getBoundNetworkForProcess();
        network = network == null ? mConnectivityManager.getActiveNetwork() : network;
        if (network == null) {
            return false;
        }

        // requires android.permission.ACCESS_NETWORK_STATE
        int bandwidth = mConnectivityManager
                .getNetworkCapabilities(network).getLinkDownstreamBandwidthKbps();

        if (bandwidth >= MIN_NETWORK_BANDWIDTH_KBPS) {
            return true;
        } else {
            return false;
        }
    }

    public void cleanup() {
        realm.close();
    }

    public void getFreshData() {
        final WearService request = retrofit.create(WearService.class);
        Call<LaunchWearResponse> call;
        final RealmList<Launch> items = new RealmList<>();

        //Get Date String
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        call = request.getWearNextLaunch(fmt.format(date), 10);
        Timber.v("Calling - %s", call.request().url().url().toString());
        call.enqueue(new Callback<LaunchWearResponse>() {

            @Override
            public void onResponse(Call<LaunchWearResponse> call, Response<LaunchWearResponse> response) {
                if (response.isSuccessful()) {
                    Timber.v("Successful! - %s", call.request().url().url().toString());
                    Collections.addAll(items, response.body().getLaunches());
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            for (Launch item: items){
                                item.getLocation().setPrimaryID();
                            }
                            realm.copyToRealmOrUpdate(items);
                        }
                    });
                    updateLastSync(0);
                    contentCallback.dataLoaded();
                } else {
                    try {
                        contentCallback.errorLoading(response.errorBody().string());
                    } catch (IOException e) {
                        contentCallback.errorLoading("Unknown Error");
                        e.printStackTrace();
                    }
                    Timber.e("Error: %s", response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LaunchWearResponse> call, Throwable t) {
                contentCallback.errorLoading(t.getLocalizedMessage());
                Timber.e(t.getLocalizedMessage());
            }

        });
    }

    public void getFreshData(int agency) {
        final WearService request = retrofit.create(WearService.class);
        Call<LaunchWearResponse> call;
        final RealmList<Launch> items = new RealmList<>();

        //Get Date String
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        call = request.getWearNextLaunch(fmt.format(date), agency, 5);
        Timber.v("Calling - %s", call.request().url().url().toString());
        call.enqueue(new Callback<LaunchWearResponse>() {

            @Override
            public void onResponse(Call<LaunchWearResponse> call, Response<LaunchWearResponse> response) {
                if (response.isSuccessful()) {
                    Timber.v("Successful! - %s", call.request().url().url().toString());
                    Collections.addAll(items, response.body().getLaunches());
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            for (Launch item: items){
                                item.getLocation().setPrimaryID();
                            }
                            realm.copyToRealmOrUpdate(items);
                        }
                    });
                    updateLastSync(0);
                    contentCallback.dataLoaded();
                } else {
                    try {
                        contentCallback.errorLoading(response.errorBody().string());
                    } catch (IOException e) {
                        contentCallback.errorLoading("Unknown Error");
                        e.printStackTrace();
                    }
                    Timber.e("Error: %s", response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LaunchWearResponse> call, Throwable t) {
                contentCallback.errorLoading(t.getLocalizedMessage());
                Timber.e(t.getLocalizedMessage());
            }

        });
    }

    public void getLaunchByAgency(int category) throws IOException {
        if (shouldGetFresh(category)) {
            Timber.v("Data is stale for %s - refreshing.", LaunchCategories.findByKey(category));
            getFreshData(category);
        } else {
            Timber.v("Data is still fresh for %s.", LaunchCategories.findByKey(category));
        }
    }

    public interface ContentCallback {

        void dataLoaded();

        void errorLoading(String error);
    }


    private void updateLastSync(int category) {
        this.prefsEditor = this.sharedPreferences.edit();
        this.prefsEditor.putLong(LaunchCategories.findByKey(category), System.currentTimeMillis());
        this.prefsEditor.apply();
    }

    public boolean shouldGetFresh(int category) {
        long lastSync = sharedPreferences.getLong(LaunchCategories.findByKey(category), 0);
        long timeSinceSync = System.currentTimeMillis() - lastSync;
        Timber.v("Time since last update %d", timeSinceSync);
        if (timeSinceSync > 600000) {
            return true;
        } else {
            return false;
        }
    }
}
