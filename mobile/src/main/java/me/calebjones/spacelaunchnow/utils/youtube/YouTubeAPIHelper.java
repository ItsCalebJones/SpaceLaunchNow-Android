package me.calebjones.spacelaunchnow.utils.youtube;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.networking.interfaces.SpaceLaunchNowService;
import me.calebjones.spacelaunchnow.utils.Utils;
import me.calebjones.spacelaunchnow.utils.youtube.models.Video;
import me.calebjones.spacelaunchnow.utils.youtube.models.VideoResponse;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.calebjones.spacelaunchnow.data.networking.RetrofitBuilder.getGson;

public class YouTubeAPIHelper {
    private YouTubeService request;
    private String apiKey;

    public YouTubeAPIHelper(Context context, String apiKey){
        request =  getYouTubeRetrofit(context).create(YouTubeService.class);
        this.apiKey = apiKey;
    }

    private static Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {
        okhttp3.Response originalResponse = chain.proceed(chain.request());
            int maxStale = 60 * 60 * 24; // one day
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
    };

    private static Retrofit getYouTubeRetrofit(Context context) {

        String BASE_URL = "https://www.googleapis.com";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(defaultClient(context))
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();
        return retrofit;
    }

    private static OkHttpClient defaultClient(Context context) {
        File httpCacheDirectory = new File(context.getCacheDir(), "responses-youtube");
        int cacheSize = 1024 * 1024; // 1 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient.Builder client = new OkHttpClient().newBuilder();
        client.cache(cache);
        client.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(15, TimeUnit.SECONDS);
        client.writeTimeout(15, TimeUnit.SECONDS);
        return client.build();
    }

    public Call<VideoResponse> getVideoById(String videoId, Callback<VideoResponse> callback) {
        Call<VideoResponse> call;

        call = request.getVideoById(videoId, apiKey);

        call.enqueue(callback);

        return call;
    }
}
