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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.PathInterpolator;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.GlideApp;
import me.calebjones.spacelaunchnow.common.prefs.ListPreferences;
import me.calebjones.spacelaunchnow.common.prefs.ThemeHelper;
import me.calebjones.spacelaunchnow.utils.customtab.CustomTabActivityHelper;
import me.calebjones.spacelaunchnow.utils.customtab.WebViewFallback;

public class Utils {

    public final static int COLOR_ANIMATION_DURATION = 1000;
    public final static int DEFAULT_DELAY = 0;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void animateViewColor(View v, int startColor, int endColor) {

        ObjectAnimator animator = ObjectAnimator.ofObject(v, "backgroundColor",
                new ArgbEvaluator(), startColor, endColor);

        animator.setInterpolator(new PathInterpolator(0.4f, 0f, 1f, 1f));
        animator.setDuration(COLOR_ANIMATION_DURATION);
        animator.start();
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

    public static String getEndDate(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        return String.valueOf(formattedDate);
    }

    public static String getStartDate(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);

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
        int color;
        if (ThemeHelper.isDarkMode(activity)){
            color = R.color.darkPrimary;
        } else {
            color = R.color.colorPrimary;
        }
        intentBuilder.setToolbarColor(ContextCompat.getColor((context), color));

        intentBuilder.setShowTitle(true);

        PendingIntent actionPendingIntent = createPendingShareIntent(context, url);
        intentBuilder.setActionButton(BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_menu_share_white), "Share", actionPendingIntent);
        intentBuilder.setCloseButtonIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_arrow_back));


        intentBuilder.setStartAnimations(activity,
                R.anim.slide_in_right, R.anim.slide_out_left);
        intentBuilder.setExitAnimations(activity,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        if (URLUtil.isValidUrl(url)) {
            CustomTabActivityHelper.openCustomTab(activity, intentBuilder.build(), Uri.parse(url), new WebViewFallback());
        } else {
            Toast.makeText(activity, "ERROR: URL is malformed - sorry! " + url, Toast.LENGTH_SHORT);
        }
    }

    private static PendingIntent createPendingShareIntent(Context context, String url) {
        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType("text/plain");
        actionIntent.putExtra(Intent.EXTRA_TEXT, url);
        return PendingIntent.getActivity(context,
                0,
                actionIntent,
                PendingIntent.FLAG_IMMUTABLE);
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

    public static int reverseNumber(int num, int min, int max) {
        return (max + min) - num;
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
            return "Unknown";
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static int getCategoryIcon(String type) {
        if (type != null) {
            switch (type) {
                case "Earth Science":
                    return R.drawable.ic_earth;
                case "Planetary Science":
                    return R.drawable.ic_planetary;
                case "Astrophysics":
                    return R.drawable.ic_astrophysics;
                case "Heliophysics":
                    return R.drawable.ic_heliophysics_alt;
                case "Human Exploration":
                    return R.drawable.ic_human_explore;
                case "Robotic Exploration":
                    return R.drawable.ic_robotic_explore;
                case "Government/Top Secret":
                    return R.drawable.ic_top_secret;
                case "Tourism":
                    return R.drawable.ic_tourism;
                case "Unknown":
                    return R.drawable.ic_unknown;
                case "Communications":
                    return R.drawable.ic_satellite;
                case "Resupply":
                    return R.drawable.ic_resupply;
                default:
                    return R.drawable.ic_unknown;
            }
        } else {
            return R.drawable.ic_unknown;
        }
    }

    public static void setCategoryIcon(ImageView imageView, String type, Boolean night) {
        if (type != null) {
            switch (type) {
                case "Earth Science":
                    imageView.setImageResource(R.drawable.ic_earth);
                    break;
                case "Planetary Science":
                    imageView.setImageResource(R.drawable.ic_planetary);
                    break;
                case "Astrophysics":
                    imageView.setImageResource(R.drawable.ic_astrophysics);
                    break;
                case "Heliophysics":
                    imageView.setImageResource(R.drawable.ic_heliophysics_alt);
                    break;
                case "Human Exploration":
                    imageView.setImageResource(R.drawable.ic_human_explore);
                    break;
                case "Robotic Exploration":
                    imageView.setImageResource(R.drawable.ic_robotic_explore);
                    break;
                case "Government/Top Secret":
                    imageView.setImageResource(R.drawable.ic_top_secret);
                    break;
                case "Tourism":
                    imageView.setImageResource(R.drawable.ic_tourism);
                    break;
                case "Unknown":
                    imageView.setImageResource(R.drawable.ic_unknown);
                    break;
                case "Communications":
                    imageView.setImageResource(R.drawable.ic_satellite);
                    break;
                case "Resupply":
                    imageView.setImageResource(R.drawable.ic_resupply);
                    break;
                default:
                    imageView.setImageResource(R.drawable.ic_unknown);
                    break;
            }
        } else {
            imageView.setImageResource(R.drawable.ic_unknown);
        }

        if (night) {
            imageView.setColorFilter(Color.WHITE);
        } else {
            imageView.setColorFilter(Color.BLACK);
        }
    }

    public static void setCategoryIcon(RemoteViews remoteViews, String type, Boolean night, Integer id) {
        if (type != null) {
            switch (type) {
                case "Earth Science":
                    remoteViews.setImageViewResource(id, R.drawable.ic_earth);
                    break;
                case "Planetary Science":
                    remoteViews.setImageViewResource(id, R.drawable.ic_planetary);
                    break;
                case "Astrophysics":
                    remoteViews.setImageViewResource(id, R.drawable.ic_astrophysics);
                    break;
                case "Heliophysics":
                    remoteViews.setImageViewResource(id, R.drawable.ic_heliophysics_alt);
                    break;
                case "Human Exploration":
                    remoteViews.setImageViewResource(id, R.drawable.ic_human_explore);
                    break;
                case "Robotic Exploration":
                    remoteViews.setImageViewResource(id, R.drawable.ic_robotic_explore);
                    break;
                case "Government/Top Secret":
                    remoteViews.setImageViewResource(id, R.drawable.ic_top_secret);
                    break;
                case "Tourism":
                    remoteViews.setImageViewResource(id, R.drawable.ic_tourism);
                    break;
                case "Unknown":
                    remoteViews.setImageViewResource(id, R.drawable.ic_unknown);
                    break;
                case "Communications":
                    remoteViews.setImageViewResource(id, R.drawable.ic_satellite);
                    break;
                case "Resupply":
                    remoteViews.setImageViewResource(id, R.drawable.ic_resupply);
                    break;
                default:
                    remoteViews.setImageViewResource(id, R.drawable.ic_unknown);
                    break;
            }
        } else {
            remoteViews.setImageViewResource(id, R.drawable.ic_unknown);
        }
    }


    public static String getFormattedDateFromTimestamp(long timestampInMilliSeconds) {
        Date date = new Date();
        date.setTime(timestampInMilliSeconds);
        return new SimpleDateFormat("h:mm a z - MMM d, yyyy ", Locale.US).format(date);
    }

    public static Bitmap getBitMapFromUrl(Context context, String imageURL) throws ExecutionException, InterruptedException {
        return GlideApp.with(context)
                .asBitmap()
                .load(imageURL)
                .submit(200, 200)
                .get();
    }

    public static SimpleDateFormat getSimpleDateFormatForUI(String pattern) {
        String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), pattern);
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    public static SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault());
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private final static String NON_THIN = "[^iIl1\\.,']";

    private static int textWidth(String str) {
        return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
    }

    public static String ellipsize(String text, int max) {

        if (textWidth(text) <= max)
            return text;

        // Start by chopping off at the word before max
        // This is an over-approximation due to thin-characters...
        int end = text.lastIndexOf(' ', max - 3);

        // Just one long word. Chop it off.
        if (end == -1)
            return text.substring(0, max-3) + "...";

        // Step forward as long as textWidth allows.
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);

            // No more spaces.
            if (newEnd == -1)
                newEnd = text.length();

        } while (textWidth(text.substring(0, newEnd) + "...") < max);

        return text.substring(0, end) + "...";
    }

    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;

        if (elapsedDays > 0) {
            return (String.format(Locale.ENGLISH,"Open for %d days, %d hours and %d minutes.\n",
                    elapsedDays, elapsedHours, elapsedMinutes));
        } else if (elapsedHours > 0){
            return (String.format(Locale.ENGLISH,"Open for %d hours and %d minutes.\n",
                    elapsedHours, elapsedMinutes));
        } else if (elapsedMinutes > 0){
            return (String.format(Locale.ENGLISH,"Open for %d minutes.\n",
                    elapsedHours, elapsedMinutes));
        } else {
            return "";
        }
    }


}

