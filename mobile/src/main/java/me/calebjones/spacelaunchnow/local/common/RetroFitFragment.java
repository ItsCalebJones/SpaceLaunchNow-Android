package me.calebjones.spacelaunchnow.local.common;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import io.realm.RealmList;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.data.networking.RetrofitBuilder;
import me.calebjones.spacelaunchnow.utils.Utils;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.calebjones.spacelaunchnow.data.networking.RetrofitBuilder.getGson;

abstract public class RetroFitFragment extends BaseFragment {

    private OkHttpClient client;
    private OkHttpClient newsClient;
    private Retrofit spaceLaunchNowRetrofit;
    private Retrofit newsRetrofit;
    private Context context;

    private Interceptor NEWS_REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            if (Utils.isNetworkAvailable(context)) {
                int maxAge = 60 * 5; // Five minute Cache
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24; // one day
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };

    private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            if (Utils.isNetworkAvailable(context)) {
                int maxAge = 60 * 60; // Hour Cache
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24; // one day
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();

        //setup cache
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        client = new OkHttpClient()
                .newBuilder()
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache)
                .build();

        newsClient = new OkHttpClient()
                .newBuilder()
                .addNetworkInterceptor(NEWS_REWRITE_CACHE_CONTROL_INTERCEPTOR)
                .cache(cache)
                .build();

        ListPreferences sharedPreference = ListPreferences.getInstance(context);

        String BASE_URL = sharedPreference.getNetworkEndpoint();
        String NEWS_URL = Constants.NEWS_BASE_URL;

        spaceLaunchNowRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();

        newsRetrofit = new Retrofit.Builder()
                .baseUrl(NEWS_URL)
                .client(newsClient)
                .addConverterFactory(GsonConverterFactory.create(getNewsGson()))
                .build();
    }

    public void startActivity(Intent intent, Bundle bundle) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActivity().startActivity(intent, bundle);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle bundle) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getActivity().startActivityForResult(intent, requestCode, bundle);
        }
    }

    public Retrofit getSpaceLaunchNowRetrofit() {
        return spaceLaunchNowRetrofit;
    }

    public Retrofit getNewsRetrofit() { return  newsRetrofit;}

    public Gson getNewsGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new me.calebjones.spacelaunchnow.data.helpers.Utils.EpochDateConverter())
                .registerTypeAdapter(getToken(), new TypeAdapter<RealmList<RealmStr>>() {

                    @Override
                    public void write(JsonWriter out, RealmList<RealmStr> value) throws io.realm.internal.IOException {
                        // Ignore
                    }

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
    }

    private static Type getToken(){
        return new TypeToken<RealmList<RealmStr>>() {
        }.getType();
    }
}
