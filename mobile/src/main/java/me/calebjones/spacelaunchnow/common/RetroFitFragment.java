package me.calebjones.spacelaunchnow.common;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;

import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.Constants;
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
    private Retrofit libraryRetrofit;
    private Retrofit spaceLaunchNowRetrofit;
    private Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();

        //setup cache
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        client = new OkHttpClient()
                .newBuilder()
                .cache(cache)
                .build();

        ListPreferences sharedPreference = ListPreferences.getInstance(context);

        String version;

        if (sharedPreference.isDebugEnabled()) {
            version = "dev";
        } else {
            version = "1.2.1";
        }

        libraryRetrofit = RetrofitBuilder.getLibraryRetrofit(version);
        spaceLaunchNowRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
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

    public Retrofit getLibraryRetrofit() {
        return libraryRetrofit;
    }

    public Retrofit getSpaceLaunchNowRetrofit() {
        return spaceLaunchNowRetrofit;
    }
}
