package me.calebjones.spacelaunchnow.data.networking;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.lang.reflect.Type;

import io.realm.RealmList;
import io.realm.RealmObject;
import me.calebjones.spacelaunchnow.data.models.Constants;
import me.calebjones.spacelaunchnow.data.models.realm.RealmStr;
import me.calebjones.spacelaunchnow.data.networking.interfaces.LibraryService;
import me.calebjones.spacelaunchnow.data.networking.responses.launchlibrary.LaunchResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LibraryClient {

//    private final String mCacheControl;
    private final LibraryService mService;
    private static LibraryClient mInstance;

    private LibraryClient(){
        Retrofit retrofit = getRetrofit();

        //TODO figure out caching strategy
//        CacheControl cacheControl =
//                new CacheControl.Builder().maxAge(forecastConfiguration.getCacheMaxAge(), TimeUnit.SECONDS)
//                        .build();
//        mCacheControl = cacheControl.toString();
        mService = retrofit.create(LibraryService.class);

    }

    private Retrofit getRetrofit(){
        Type token = new TypeToken<RealmList<RealmStr>>() {
        }.getType();

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.LIBRARY_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return  retrofit;
    }

    /**
     * Applications must call create to configure the LibraryClient singleton
     */
    public static void create() {
        mInstance = new LibraryClient();
    }

    /**
     * Singleton accessor
     * <p/>
     * Will throw an exception if {@link #create()} was never called
     *
     * @return the LibraryClient singleton
     */
    public static LibraryClient getInstance() {
        if (mInstance == null) {
            throw new AssertionError("Did you forget to call create() ?");
        }
        return mInstance;
    }

    public Call<LaunchResponse> getLaunchById(int launchID, @NonNull Callback<LaunchResponse> launchCallback) {
        Call<LaunchResponse> LaunchCall = mService.getLaunchByID(launchID);

        LaunchCall.enqueue(launchCallback);

        Log.v("LibraryClient", "Creating getLaunchByID for Launch: " + launchID);

        return LaunchCall;
    }
}
