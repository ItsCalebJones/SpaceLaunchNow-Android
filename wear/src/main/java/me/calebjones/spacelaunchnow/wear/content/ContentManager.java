package me.calebjones.spacelaunchnow.wear.content;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.data.networking.RetrofitBuilder;
import me.calebjones.spacelaunchnow.data.networking.interfaces.WearService;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchWearResponse;
import me.calebjones.spacelaunchnow.wear.model.LaunchCategories;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.wear.model.Constants.*;


public class ContentManager {

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
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private Retrofit retrofit;
    private ContentCallback contentCallback;
    private SharedPreferences sharedPreferences;
    private Context context;
    SharedPreferences.Editor prefsEditor;

    public ContentManager(Context context, ContentCallback callback) {
        this.context = context;
        realm = Realm.getDefaultInstance();
        retrofit = RetrofitBuilder.getWearRetrofit();
        sharedPreferences = context.getSharedPreferences("timestamp", 0);
        this.contentCallback = callback;
    }

    @SuppressLint("HandlerLeak")
    public void init(){
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = mConnectivityManager.getActiveNetwork();

        if (activeNetwork != null) {
            int bandwidth = mConnectivityManager.getNetworkCapabilities(activeNetwork).getLinkDownstreamBandwidthKbps();
            Timber.v("Network available - Bandwidth: %s/kbps", bandwidth);
            if (shouldGetFresh(0)) {
                getFreshData();
            }
            contentCallback.networkState(UI_STATE_NETWORK_CONNECTED);
        } else {
            // request high-bandwidth network
            Timber.v("Network unavailable.");
            contentCallback.networkState(UI_STATE_REQUEST_NETWORK);
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_CONNECTIVITY_TIMEOUT:
                        Timber.d("Network connection timeout");
                        contentCallback.networkState(UI_STATE_CONNECTION_TIMEOUT);
                        unregisterNetworkCallback();
                        break;
                }
            }
        };
    }

    public void releaseHighBandwidthNetwork() {
        mConnectivityManager.bindProcessToNetwork(null);
        unregisterNetworkCallback();
    }

    public RealmResults<Launch> getLaunchList(int category) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        Date date = calendar.getTime();

        if (category > 0) {
            return realm.where(Launch.class).equalTo("lsp.id", category).greaterThanOrEqualTo("net", date).sort("net").findAll();
        } else {
            return realm.where(Launch.class).greaterThanOrEqualTo("net", date).sort("net").findAll();
        }
    }

    // Determine if there is a high-bandwidth network exists. Checks both the active
    // and bound networks. Returns false if no network is available (low or high-bandwidth).
    public int isNetworkHighBandwidth() {
        Network network = mConnectivityManager.getBoundNetworkForProcess();
        network = network == null ? mConnectivityManager.getActiveNetwork() : network;
        if (network == null) {
            return NETWORK_UNAVAILABLE;
        }

        // requires android.permission.ACCESS_NETWORK_STATE
        int bandwidth = mConnectivityManager
                .getNetworkCapabilities(network).getLinkDownstreamBandwidthKbps();

        if (bandwidth >= MIN_NETWORK_BANDWIDTH_KBPS) {
            return NETWORK_CONNECTED;
        } else {
            return NETWORK_CONNECTED_SLOW;
        }
    }

    public void requestHighBandwidthNetwork() {
        // Before requesting a high-bandwidth network, ensure prior requests are invalidated.
        unregisterNetworkCallback();

        contentCallback.networkState(UI_STATE_REQUESTING_NETWORK);
        Timber.d("Requesting high-bandwidth network");

        // Requesting an unmetered network may prevent you from connecting to the cellular
        // network on the user's watch or phone; however, unless you explicitly ask for permission
        // to a access the user's cellular network, you should request an unmetered network.
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        mNetworkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(final Network network) {
                mHandler.removeMessages(MESSAGE_CONNECTIVITY_TIMEOUT);

                if (!mConnectivityManager.bindProcessToNetwork(network)) {
                    Timber.e("ConnectivityManager.bindProcessToNetwork()" + " requires android.permission.INTERNET");
                    contentCallback.networkState(UI_STATE_REQUEST_NETWORK);
                } else {
                    Timber.d("Network available");
                    contentCallback.networkState(UI_STATE_NETWORK_CONNECTED);
                }
            }


            @Override
            public void onCapabilitiesChanged(Network network,
                                              NetworkCapabilities networkCapabilities) {
                Timber.d("Network capabilities changed");
            }

            @Override
            public void onLost(Network network) {
                Timber.d("Network lost");
                contentCallback.networkState(UI_STATE_REQUEST_NETWORK);
            }
        };

        // requires android.permission.CHANGE_NETWORK_STATE
        mConnectivityManager.requestNetwork(request, mNetworkCallback);

        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_CONNECTIVITY_TIMEOUT),
                NETWORK_CONNECTIVITY_TIMEOUT_MS);
    }

    private void unregisterNetworkCallback() {
        if (mNetworkCallback != null) {
            Timber.d("Unregistering network callback");
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
            mNetworkCallback = null;
        }
    }

    public void addWifiNetwork() {
        // requires android.permission.CHANGE_WIFI_STATE
        context.startActivity(new Intent(ACTION_ADD_NETWORK_SETTINGS));
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

        call = request.getWearNextLaunch(fmt.format(date), 5);
        Timber.v("Calling - %s", call.request().url().url().toString());
        call.enqueue(new Callback<LaunchWearResponse>() {

            @Override
            public void onResponse(Call<LaunchWearResponse> call, Response<LaunchWearResponse> response) {
                if (response.isSuccessful()) {
                    Timber.v("Successful - %s", call.request().url().url().toString());
                    if (response.body() != null) {
                        Launch[] launch = response.body().getLaunches();
                        if (launch != null && launch.length > 0) {
                            Collections.addAll(items, launch);
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
                        }
                    }
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

    public void getFreshData(final int category) {
        if (category == 0 ) {
            getFreshData();
            return;
        }
        final WearService request = retrofit.create(WearService.class);
        Call<LaunchWearResponse> call;
        final RealmList<Launch> items = new RealmList<>();

        //Get Date String
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        call = request.getWearNextLaunch(fmt.format(date), category, 5);
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
                    updateLastSync(category);
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

    public void setCategory(int category) {
        if (shouldGetFresh(category)) {
            Timber.v("Data is stale for %s - refreshing.", LaunchCategories.findByKey(category));
            getFreshData(category);
        } else {
            Timber.v("Data is still fresh for %s.", LaunchCategories.findByKey(category));
            contentCallback.dataLoaded();
        }
    }

    public interface ContentCallback {
        void dataLoaded();

        void errorLoading(String error);

        void networkState(int state);
    }

    private void updateLastSync(int category) {
        this.prefsEditor = this.sharedPreferences.edit();
        this.prefsEditor.putLong(LaunchCategories.findByKey(category), System.currentTimeMillis());
        this.prefsEditor.apply();
    }

    private boolean shouldGetFresh(int category) {
        long lastSync = sharedPreferences.getLong(LaunchCategories.findByKey(category), 0);
        long timeSinceSync = System.currentTimeMillis() - lastSync;
        Timber.v("Time since last update %d", timeSinceSync);
        if (timeSinceSync > 36000000) {
            return true;
        } else {
            return false;
        }
    }
}
