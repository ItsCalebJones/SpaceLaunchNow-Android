package me.calebjones.spacelaunchnow.wear.content;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.lang.reflect.Type;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import me.calebjones.spacelaunchnow.data.StringRealmListConverter;
import me.calebjones.spacelaunchnow.data.models.realm.LaunchRealm;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.data.networking.interfaces.LibraryRequestInterface;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchResponse;
import me.calebjones.spacelaunchnow.wear.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class ContentManager {

    private Realm realm;
    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private Retrofit retrofit;
    private ContentCallback contentCallback;

    public ContentManager(Context context, ContentCallback callback) {
        realm = Realm.getDefaultInstance();
        retrofit = getRetrofit();
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

    public RealmResults<LaunchRealm> getLaunchList(int category) {
        return realm.where(LaunchRealm.class).findAll();
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
                .registerTypeAdapter(token, new TypeAdapter<RealmList<RealmStr>>() {

                    @Override
                    public void write(JsonWriter out, RealmList<RealmStr> value) throws io.realm.internal.IOException {
                        // Ignore
                    }
                    @SuppressWarnings("Ambigious")
                    @Override
                    public RealmList<RealmStr> read(JsonReader in) throws io.realm.internal.IOException, java.io.IOException {
                        RealmList<RealmStr> list = new RealmList<RealmStr>();
                        in.beginArray();
                        while (in.hasNext()) {
                            list.add(new RealmStr(in.nextString()));
                        }
                        in.endArray();
                        return list;
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
        Call<LaunchResponse> call;
        final RealmList<LaunchRealm> items = new RealmList<>();

        call = request.getWearNextLaunch();

        call.enqueue(new Callback<LaunchResponse>() {

            @Override
            public void onResponse(Call<LaunchResponse> call, Response<LaunchResponse> response) {
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
            public void onFailure(Call<LaunchResponse> call, Throwable t) {
                Timber.e(t.getLocalizedMessage());
            }

        });
    }

    public interface ContentCallback {
        void dataLoaded();
    }


}
