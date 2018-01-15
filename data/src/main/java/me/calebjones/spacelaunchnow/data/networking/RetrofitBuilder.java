package me.calebjones.spacelaunchnow.data.networking;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import io.realm.RealmList;
import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_LESS_FAVORABLE;
import static android.os.Process.THREAD_PRIORITY_LOWEST;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;


public class RetrofitBuilder {

    private static Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
                int maxAge = 60 * 60 * 24 * 3; // Three day cache
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
        }
    };

    public static Retrofit getLibraryRetrofit(String version) {

        Retrofit retrofit = new Retrofit.Builder()
                .client(defaultClient())
                .baseUrl(Constants.LIBRARY_BASE_URL + version + "/")
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();
        return retrofit;
    }

    public static Retrofit getLibraryRetrofitThreaded(String version) {
        Executor httpExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
                        r.run();
                    }
                }, "Retrofit-Idle-Background");
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.LIBRARY_BASE_URL + version + "/")
                .client(defaultClient())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .callbackExecutor(httpExecutor)
                .build();
        return retrofit;
    }

    public static Retrofit getLibraryRetrofitLowestThreaded(String version) {
        Executor httpExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                return new Thread(new Runnable() {
                    @Override
                    public void run() {
                        android.os.Process.setThreadPriority(THREAD_PRIORITY_MORE_FAVORABLE);
                        r.run();
                    }
                }, "Retrofit-Idle-Background");
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.LIBRARY_BASE_URL + version + "/")
                .client(defaultClient())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .callbackExecutor(httpExecutor)
                .build();
        return retrofit;
    }


    public static Retrofit getSpaceLaunchNowRetrofit() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(defaultClient())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();
        return retrofit;
    }

    private static OkHttpClient defaultClient() {
        OkHttpClient.Builder client = new OkHttpClient().newBuilder();
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(15, TimeUnit.SECONDS);
        client.writeTimeout(15, TimeUnit.SECONDS);
        return client.build();
    }

    public static Gson getGson(){
        return new GsonBuilder()
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

