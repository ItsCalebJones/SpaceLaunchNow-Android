package me.calebjones.spacelaunchnow;

import android.app.Application;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import me.calebjones.spacelaunchnow.content.database.SharedPrefs;


public class LaunchApplication extends Application {

    private static LaunchApplication mInstance;
    public static final String TAG = "Space Launch Now";
    public static final String LAUNCH_URL = "https://launchlibrary.net/1.1.1/launch/next/5";
//    public static String PREVIOUS_LAUNCH_URL = "https://launchlibrary.net/1.1.1/launch/2015-01-01/%s/?sort=desc&limit=20";

    // Disable HTTP/2 for interop with NGINX 1.9.5.
    // TODO: remove this hack after 2015-12-31.
    public OkHttpClient client = new OkHttpClient();

    public static synchronized LaunchApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        int cacheSize = 50 * 1024 * 1024;
        Cache cache = new Cache(getCacheDir(), cacheSize);
        client.setCache(cache);

        SharedPrefs.create(this);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
