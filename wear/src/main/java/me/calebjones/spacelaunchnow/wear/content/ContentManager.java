package me.calebjones.spacelaunchnow.wear.content;

import android.content.Context;
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

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchWear;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.data.networking.interfaces.LibraryRequestInterface;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchWearResponse;
import me.calebjones.spacelaunchnow.wear.Constants;
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

    public ContentManager(Context context, ContentCallback callback, int category) {
        realm = Realm.getDefaultInstance();
        retrofit = getRetrofit();
        this.contentCallback = callback;

        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = mConnectivityManager.getActiveNetwork();

        if (activeNetwork != null) {
            int bandwidth = mConnectivityManager.getNetworkCapabilities(activeNetwork).getLinkDownstreamBandwidthKbps();
            Timber.v("Network available - Bandwidth: %s/kbps", bandwidth);
            getFreshData();
            getFreshData(AGENCY_SPACEX);
            getFreshData(AGENCY_ROSCOSMOS);
            getFreshData(AGENCY_ULA);
            getFreshData(AGENCY_NASA);
            getFreshData(AGENCY_CASC);
        } else {
            // request high-bandwidth network
            Timber.v("Network unavailable.");
        }
    }

    public RealmResults<LaunchWear> getLaunchList(int category) {
        if (category > 0 ) {
            return realm.where(LaunchWear.class).equalTo("agency", category).findAllSorted("net");
        } else {
            return realm.where(LaunchWear.class).findAllSorted("net");
        }
    }

    public void cleanup() {
        realm.close();
    }

    private static Retrofit getRetrofit() {
        // Note there is a bug in GSON 2.5 that can cause it to StackOverflow when working with RealmObjects.
        // To work around this, use the ExclusionStrategy below or downgrade to 1.7.1
        // See more here: https://code.google.com/p/google-gson/issues/detail?id=440
        Type token = new TypeToken<RealmList<RealmStr>>() {}.getType();

        Gson gson = new GsonBuilder()
                .setDateFormat("MMMM dd, yyyy HH:mm:ss zzz")
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        return new Builder()
                .baseUrl(Constants.LIBRARY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private void getFreshData() {
        final LibraryRequestInterface request = retrofit.create(LibraryRequestInterface.class);
        Call<LaunchWearResponse> call;
        final RealmList<LaunchWear> items = new RealmList<>();

        //Get Date String
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        call = request.getWearNextLaunch(fmt.format(date), 5);

        call.enqueue(new Callback<LaunchWearResponse>() {

            @Override
            public void onResponse(Call<LaunchWearResponse> call, Response<LaunchWearResponse> response) {
                if (response.isSuccessful()) {
                    Timber.v("Successful!");
                    Collections.addAll(items, response.body().getLaunches());
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(items);
                    realm.commitTransaction();
                    contentCallback.dataLoaded();
                }
                Timber.e("Error: %s", response.errorBody());
            }

            @Override
            public void onFailure(Call<LaunchWearResponse> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }

        });
    }

    private void getFreshData(final int category) {
        final LibraryRequestInterface request = retrofit.create(LibraryRequestInterface.class);
        Call<LaunchWearResponse> call;
        final RealmList<LaunchWear> items = new RealmList<>();

        //Get Date String
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();

        call = request.getWearNextLaunch(fmt.format(date), category, 5);

        call.enqueue(new Callback<LaunchWearResponse>() {

            @Override
            public void onResponse(Call<LaunchWearResponse> call, Response<LaunchWearResponse> response) {
                if (response.isSuccessful()) {
                    Timber.v("Successful!");
                    Collections.addAll(items, response.body().getLaunches());
                    realm.beginTransaction();
                    for (LaunchWear item: items){
                        item.setAgency(category);
                        realm.copyToRealmOrUpdate(item);
                    }
                    realm.commitTransaction();
                    contentCallback.dataLoaded();
                }
                Timber.e("Error: %s", response.errorBody());
            }

            @Override
            public void onFailure(Call<LaunchWearResponse> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }

        });
    }

    public interface ContentCallback {
        void dataLoaded();
    }


}
