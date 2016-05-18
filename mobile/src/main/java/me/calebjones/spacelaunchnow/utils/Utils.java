/*
 * Copyright 2015, Tanmay Parikh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.calebjones.spacelaunchnow.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.PathInterpolator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.models.Launch;
import me.calebjones.spacelaunchnow.utils.customtab.CustomTabActivityHelper;
import me.calebjones.spacelaunchnow.utils.customtab.WebViewFallback;

public class Utils {

    public final static int COLOR_ANIMATION_DURATION = 1000;
    public final static int DEFAULT_DELAY = 0;

    public static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=178200")
                    .build();
        }
    };

    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static boolean isLollopopUp() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isNumeric(String input) {
        try {
            int num = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void animateViewColor(View v, int startColor, int endColor) {

        ObjectAnimator animator = ObjectAnimator.ofObject(v, "backgroundColor",
                new ArgbEvaluator(), startColor, endColor);

        animator.setInterpolator(new PathInterpolator(0.4f, 0f, 1f, 1f));
        animator.setDuration(COLOR_ANIMATION_DURATION);
        animator.start();
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Scale and set the pivot when the animation will start from
     *
     * @param v the view to set the pivot
     */
    public static void configureHideYView(View v) {

        v.setScaleY(0);
        v.setPivotY(0);
    }

    /**
     * Reduces the X & Y from a view
     *
     * @param v the view to be scaled
     * @return the ViewPropertyAnimation to manage the animation
     */
    public static ViewPropertyAnimator hideViewByScaleXY(View v) {

        return hideViewByScale(v, DEFAULT_DELAY, 0, 0);
    }

    /**
     * Reduces the Y from a view
     *
     * @param v the view to be scaled
     * @return the ViewPropertyAnimation to manage the animation
     */
    public static ViewPropertyAnimator hideViewByScaleY(View v) {

        return hideViewByScale(v, DEFAULT_DELAY, 1, 0);
    }

    /**
     * Reduces the X from a view
     *
     * @param v the view to be scaled
     * @return the ViewPropertyAnimation to manage the animation
     */
    public static ViewPropertyAnimator hideViewByScalyInX(View v) {

        return hideViewByScale(v, DEFAULT_DELAY, 0, 1);
    }

    /**
     * Reduces the X & Y
     *
     * @param v     the view to be scaled
     * @param delay to start the animation
     * @param x     integer to scale
     * @param y     integer to scale
     * @return the ViewPropertyAnimation to manage the animation
     */
    private static ViewPropertyAnimator hideViewByScale(View v, int delay, int x, int y) {

        ViewPropertyAnimator propertyAnimator = v.animate().setStartDelay(delay)
                .scaleX(x).scaleY(y);

        return propertyAnimator;
    }

    /**
     * Shows a view by scaling
     *
     * @param v the view to be scaled
     * @return the ViewPropertyAnimation to manage the animation
     */
    public static ViewPropertyAnimator showViewByScale(View v) {

        ViewPropertyAnimator propertyAnimator = v.animate().setStartDelay(DEFAULT_DELAY)
                .scaleX(1).scaleY(1);

        return propertyAnimator;
    }

    public static String getBaseURL(Context context) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        if (ListPreferences.getInstance(context.getApplicationContext()).isDebugEnabled()) {
            return "https://launchlibrary.net/dev/launch/1950-01-01/" + String.valueOf(formattedDate) + "?sort=desc&limit=1000";
        } else {
            return "https://launchlibrary.net/1.2/launch/1950-01-01/" + String.valueOf(formattedDate) + "?sort=desc&limit=1000";
        }
    }

    public static String getEndDate(Context context) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        return String.valueOf(formattedDate);
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static void openCustomTab(Activity activity, Context context, String url) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        ListPreferences sharedPreference = ListPreferences.getInstance(context);

        if (sharedPreference.getNightMode()) {
            intentBuilder.setToolbarColor(ContextCompat.getColor(
                    (context), R.color.darkPrimary));
        } else {
            intentBuilder.setToolbarColor(ContextCompat.getColor(
                    (context), R.color.colorPrimary));
        }

        intentBuilder.setShowTitle(true);

        PendingIntent actionPendingIntent = createPendingShareIntent(context, url);
        intentBuilder.setActionButton(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_menu_share_white), "Share", actionPendingIntent);


        intentBuilder.setStartAnimations(activity,
                R.anim.slide_in_right, R.anim.slide_out_left);
        intentBuilder.setExitAnimations(activity,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        CustomTabActivityHelper.openCustomTab(
                activity, intentBuilder.build(), Uri.parse(url), new WebViewFallback());
    }

    private static PendingIntent createPendingShareIntent(Context context, String url) {
        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType("text/plain");
        actionIntent.putExtra(Intent.EXTRA_TEXT, url);
        return PendingIntent.getActivity(context, 0, actionIntent, 0);
    }

    public static Intent buildShareIntent(Launch launch) {
        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a zzz");
        df.toLocalizedPattern();

        Date date = new Date(launch.getWindowstart());
        String launchDate = df.format(date);
        String mission;

        Intent sendIntent = new Intent();

        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, launch.getName());

        if (launch.getMissions().size() > 0) {
            mission = launch.getMissions().get(0).getDescription();
        } else {
            mission = "";
        }

        if (launch.getVidURL() != null) {
            if (launch.getLocation().getPads().size() > 0 && launch.getLocation().getPads().
                    get(0).getAgencies().size() > 0) {
                //Get the first CountryCode
                String country = launch.getLocation().getPads().
                        get(0).getAgencies().get(0).getCountryCode();
                country = (country.substring(0, 3));

                sendIntent.putExtra(Intent.EXTRA_TEXT, mission
                        + launch.getRocket().getName() + " launching from "
                        + launch.getLocation().getName() + " " + country + "\n \n"
                        + launchDate
                        + "\n\nWatch: " + launch.getVidURL() + "\n"
                        + " \n\nvia Space Launch Now and Launch Library");
            } else {
                sendIntent.putExtra(Intent.EXTRA_TEXT, mission
                        + launch.getName() + " launching from "
                        + launch.getLocation().getName() + "\n \n"
                        + launchDate
                        + "\n\nWatch: " + launch.getVidURL() + "\n"
                        + " \n\nvia Space Launch Now and Launch Library");
            }
        } else {
            if (launch.getLocation().getPads().size() > 0 && launch.getLocation().getPads().
                    get(0).getAgencies().size() > 0) {
                //Get the first CountryCode
                String country = launch.getLocation().getPads().
                        get(0).getAgencies().get(0).getCountryCode();
                country = (country.substring(0, 3));

                sendIntent.putExtra(Intent.EXTRA_TEXT, mission
                        + launch.getName() + " launching from "
                        + launch.getLocation().getName() + " " + country + "\n \n"
                        + launchDate
                        + " \n\nvia Space Launch Now and Launch Library");
            } else {
                sendIntent.putExtra(Intent.EXTRA_TEXT, mission
                        + launch.getName() + " launching from "
                        + launch.getLocation().getName() + "\n \n"
                        + launchDate
                        + " \n\nvia Space Launch Now and Launch Library");
            }
        }
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    public static boolean checkPlayServices(Context context) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if (result != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    public static String getTypeName(int type) {
        switch (type) {
            case 1:
                return "Earth Science";
            case 2:
                return "Planetary Science";
            case 3:
                return "Astrophysics";
            case 4:
                return "Heliophysics";
            case 5:
                return "Human Exploration";
            case 6:
                return "Robotic Exploration";
            case 7:
                return "Government/Top Secret";
            case 8:
                return "Tourism";
            case 9:
                return "Unknown";
            case 10:
                return "Communications";
            case 11:
                return "Resupply";
            default:
                return "Unknown";
        }
    }

    public static int getVersionCode(Context context) {
        try {
            ComponentName comp = new ComponentName(context, context.getClass());
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
            return pinfo.versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public static String getVersionName(Context context) {
        try {
            ComponentName comp = new ComponentName(context, context.getClass());
            return context.getPackageManager().getPackageInfo(comp.getPackageName(), 0).versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
