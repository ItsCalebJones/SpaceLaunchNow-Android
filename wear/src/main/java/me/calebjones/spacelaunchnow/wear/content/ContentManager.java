package me.calebjones.spacelaunchnow.wear.content;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.data.networking.RetrofitBuilder;
import me.calebjones.spacelaunchnow.data.networking.interfaces.WearService;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchWearResponse;
import me.calebjones.spacelaunchnow.wear.model.LaunchCategories;
import me.calebjones.spacelaunchnow.wear.model.WearConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static me.calebjones.spacelaunchnow.wear.model.Constants.*;

public class ContentManager {

    private Realm realm;
    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private Retrofit retrofit;
    private ContentCallback contentCallback;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor prefsEditor;

    public ContentManager(Context context, ContentCallback callback, int category) {
        realm = Realm.getDefaultInstance();
        retrofit = RetrofitBuilder.getWearRetrofit();
        sharedPreferences = context.getSharedPreferences("timestamp", 0);
        this.contentCallback = callback;

        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = mConnectivityManager.getActiveNetwork();

        if (activeNetwork != null) {
            int bandwidth = mConnectivityManager.getNetworkCapabilities(activeNetwork).getLinkDownstreamBandwidthKbps();
            Timber.v("Network available - Bandwidth: %s/kbps", bandwidth);
            getFreshData();
        } else {
            // request high-bandwidth network
            Timber.v("Network unavailable.");
        }
    }

    public RealmResults<Launch> getLaunchList(int category) {
        if (category > 0 ) {
            return realm.where(Launch.class).equalTo("lsp.id", category).sort("net").findAll();
        } else {
            return realm.where(Launch.class).sort("net").findAll();
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

        call = request.getWearNextLaunch(fmt.format(date), 5);
        Timber.v("Calling - %s", call.request().url().url().toString());
        call.enqueue(new Callback<LaunchWearResponse>() {

            @Override
            public void onResponse(Call<LaunchWearResponse> call, Response<LaunchWearResponse> response) {
                if (response.isSuccessful()) {
                    Timber.v("Successful - %s", call.request().url().url().toString());
                    Collections.addAll(items, response.body().getLaunches());
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealmOrUpdate(items);
                        }
                    });
                    updateLastSync(0);
                    contentCallback.dataLoaded();
                } else {
                    Timber.e("Error: %s", response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LaunchWearResponse> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }

        });
    }

    public void getFreshData(final int category) {
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
                            realm.copyToRealmOrUpdate(items);
                        }
                    });
                    updateLastSync(category);
                    contentCallback.dataLoaded();
                } else {
                    Timber.e("Error: %s", response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LaunchWearResponse> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }

        });
    }

    public void setCategory(int category) {
        if (shouldGetFresh(category)){
            Timber.v("Data is stale for %s - refreshing.", LaunchCategories.findByKey(category));
            getFreshData(category);
        } else {
            Timber.v("Data is still fresh for %s.", LaunchCategories.findByKey(category));
            contentCallback.dataLoaded();
        }
    }

    public interface ContentCallback {
        void dataLoaded();
    }

    private void updateLastSync(int category){
        this.prefsEditor = this.sharedPreferences.edit();
        this.prefsEditor.putLong(LaunchCategories.findByKey(category), System.currentTimeMillis());
        this.prefsEditor.apply();
    }

    private boolean shouldGetFresh(int category){
        long lastSync = sharedPreferences.getLong(LaunchCategories.findByKey(category), 0);
        long timeSinceSync = System.currentTimeMillis() - lastSync;
        Timber.v("Time since last update %d", timeSinceSync);
        Map<String, ?> test = sharedPreferences.getAll();
        if (timeSinceSync > 36000000){
            return true;
        } else {
            return false;
        }
    }
}
