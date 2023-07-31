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

package me.calebjones.spacelaunchnow.common.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.PathInterpolator;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.browser.customtabs.CustomTabsIntent;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.customtab.CustomTabActivityHelper;
import me.calebjones.spacelaunchnow.common.customtab.SLNWebViewFallback;
import me.calebjones.spacelaunchnow.data.models.main.DatePrecision;
import me.calebjones.spacelaunchnow.data.models.main.Launch;
import me.calebjones.spacelaunchnow.data.models.main.LaunchStatus;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;
import timber.log.Timber;

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

    public static String getYouTubeID(String vidURL) {
        final String regex = "(youtu\\.be\\/|youtube\\.com\\/(watch\\?(.*&)?v=|(embed|v)\\/|c\\/))([a-zA-Z0-9_-]{11}|[a-zA-Z].*)";
        final Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(vidURL);
        Timber.v("Checking for match of %s", vidURL);
        if (matcher.find() && (matcher.group(1) != null || matcher.group(2) != null) && matcher.group(5) != null) {
            return matcher.group(5);
        }
        return null;
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean("open_links_in_app", true)) {
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

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
                CustomTabActivityHelper.openCustomTab(activity, intentBuilder.build(), Uri.parse(url), new SLNWebViewFallback());
            } else {
                Toast.makeText(activity, "ERROR: URL is malformed - sorry! " + url, Toast.LENGTH_SHORT);
            }
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(browserIntent);
        }
    }

    public static void openCustomTab(Context context, String url) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPref.getBoolean("open_links_in_app", true)) {

            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .addDefaultShareMenuItem()
                    .setShowTitle(true)
                    .build();

            // This is optional but recommended
            CustomTabsHelper.addKeepAliveExtra(context, customTabsIntent.intent);

            // This is where the magic happens...
            CustomTabsHelper.openCustomTab(context, customTabsIntent,
                    Uri.parse(url),
                    new WebViewFallback());
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        }
    }

    private static PendingIntent createPendingShareIntent(Context context, String url) {
        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType("text/plain");
        actionIntent.putExtra(Intent.EXTRA_TEXT, url);
        return PendingIntent.getActivity(context, 0, actionIntent, PendingIntent.FLAG_MUTABLE);
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

    public static void setCategoryIcon(ImageView imageView, String type) {
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
        imageView.setColorFilter(Color.WHITE);
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
        return Glide.with(context)
                .asBitmap()
                .load(imageURL)
                .submit(200, 200)
                .get();
    }

    public static SimpleDateFormat getSimpleDateFormatForUI(String pattern) {
        if(Prefs.getBoolean("locale_changer", true)) {
            String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), pattern);
            return new SimpleDateFormat(format, Locale.getDefault());
        } else {
            return new SimpleDateFormat(pattern, Locale.US);
        }
    }

    public static SimpleDateFormat getSimpleDateFormatForUIWithPrecision(DatePrecision datePrecision) {
        String pattern = "MMMM dd, yyyy";
        if (datePrecision != null) {
            switch (datePrecision.getId()) {
                case 0:
                    pattern = "MMMM d, yyyy";
                    break;
                case 1:
                    pattern = "MMMM d, yyyy";
                    break;
                case 2:
                    pattern = "'NET' MMMM d, yyyy";
                    break;
                case 3:
                    pattern = "MMMM d, yyyy";
                    break;
                case 4:
                    pattern = "MMMM d, yyyy";
                    break;
                case 5:
                    pattern = "MMMM d, yyyy";
                    break;
                case 6:
                    pattern = "MMMM d, yyyy";
                    break;
                case 7:
                    pattern = "MMMM yyyy";
                    break;
                case 8:
                    pattern = "QQQ yyyy";
                    break;
                case 9:
                    pattern = "QQQ yyyy";
                    break;
                case 10:
                    pattern = "QQQ yyyy";
                    break;
                case 11:
                    pattern = "QQQ yyyy";
                    break;
                case 12:
                    pattern = "'H1' yyyy";
                    break;
                case 13:
                    pattern = "'H2' yyyy";
                    break;
                case 14:
                    pattern = "'NET' yyyy";
                    break;
                case 15:
                    pattern = "'FY' yyyy";
                    break;
                case 16:
                    pattern = "'Decade' yyyy";
                    break;
                default:
                    pattern = "MMMM dd, yyyy";
                    break;
            }
        }

        if(Prefs.getBoolean("locale_changer", true)) {
            return new SimpleDateFormat(pattern, Locale.getDefault());
        } else {
            return new SimpleDateFormat(pattern, Locale.US);
        }
    }

    public static SimpleDateFormat getSimpleDateTimeFormatForUIWithPrecision(DatePrecision datePrecision) {

        String pattern = "EEEE, MMMM dd, yyyy - hh:mm a zzz";
        if (datePrecision != null) {
            switch (datePrecision.getId()) {
                // Second
                case 0:
                    pattern = "EEEE, MMMM dd, yyyy - hh:mm:ss a zzz";
                    break;
                // Minute
                case 1:
                    pattern = "EEEE, MMMM dd, yyyy - hh:mm a zzz";
                    break;
                case 2:
                    pattern = "'NET' EEEE, MMMM dd, yyyy - hh:mm a zzz";
                    break;
                case 3:
                    pattern = "'Morning (local)' EEEE, MMMM dd, yyyy";
                    break;
                case 4:
                    pattern = "'Afternoon (local)' EEEE, MMMM dd, yyyy";
                    break;
                case 5:
                    pattern = "EEEE, MMMM dd, yyyy";
                    break;
                case 6:
                    pattern = "'Week of' MMMM d, yyyy";
                    break;
                case 7:
                    pattern = "MMMM yyyy";
                    break;
                case 8:
                    pattern = "QQQ yyyy";
                    break;
                case 9:
                    pattern = "QQQ yyyy";
                    break;
                case 10:
                    pattern = "QQQ yyyy";
                    break;
                case 11:
                    pattern = "QQQ yyyy";
                    break;
                case 12:
                    pattern = "'H1' yyyy";
                    break;
                case 13:
                    pattern = "'H2' yyyy";
                    break;
                case 14:
                    pattern = "'NET' yyyy";
                    break;
                case 15:
                    pattern = "'FY' yyyy";
                    break;
                case 16:
                    pattern = "'Decade' yyyy's'";
                    break;
                default:
                    pattern = "EEEE, MMMM dd, yyyy - hh:mm a zzz";
                    break;
            }
        }

        if(Prefs.getBoolean("locale_changer", true)) {
            return new SimpleDateFormat(pattern, Locale.getDefault());
        } else {
            return new SimpleDateFormat(pattern, Locale.US);
        }
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
            return text.substring(0, max - 3) + "...";

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
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes;
        if (different == 0) {
            elapsedMinutes = 0;
        } else {
            elapsedMinutes = different / minutesInMilli;
        }

        if (elapsedDays > 0) {
            return (String.format(Locale.ENGLISH, "Open for %d day(s), %d hour(s) and %d minute(s).\n",
                    elapsedDays, elapsedHours, elapsedMinutes));
        } else if (elapsedHours > 0) {
            if (elapsedMinutes == 0) {
                return (String.format(Locale.ENGLISH, "Open for %d hour(s).\n",
                        elapsedHours));
            } else {
                return (String.format(Locale.ENGLISH, "Open for %d hour(s) and %d minute(s).\n",
                        elapsedHours, elapsedMinutes));
            }
        } else if (elapsedMinutes > 0) {
            return (String.format(Locale.ENGLISH, "Open for %d minute(s).\n",
                    elapsedMinutes));
        } else {
            return "";
        }
    }


    public static int getTitleTextColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        double lum = (((0.299 * red) + ((0.587 * green) + (0.114 * blue))));
        return lum > 220 ? 0xFF000000 : 0xFFFFFFFF;
    }

    public static int getSecondaryTitleTextColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        double lum = (((0.299 * red) + ((0.587 * green) + (0.114 * blue))));
        return lum > 220 ? 0x80000000 : 0xFFFFFFFF;
    }

    public static boolean getIconColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        double lum = (((0.299 * red) + ((0.587 * green) + (0.114 * blue))));
        return lum < 220;
    }

    public static ContextWrapper changeLang(Context context, String lang_code){
        Locale sysLocale;

        Resources rs = context.getResources();
        Configuration config = rs.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = config.getLocales().get(0);
        } else {
            sysLocale = config.locale;
        }
        if (!lang_code.equals("") && !sysLocale.getLanguage().equals(lang_code)) {
            Locale locale = new Locale(lang_code);
            Locale.setDefault(locale);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context = context.createConfigurationContext(config);
            } else {
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            }
        }

        return new ContextWrapper(context);
    }

    private final static AtomicInteger c = new AtomicInteger(0);

    public static int getUniqueId() {
        return c.incrementAndGet();
    }

    public static SimpleDateFormat getSLNDateTimeFormat(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SimpleDateFormat fullDate;

        if (sharedPref.getString("time_format", "Default").contains("12-Hour")) {
            fullDate = Utils.getSimpleDateFormatForUI("MMMM d, yyyy h:mm a zzz");
        } else if (sharedPref.getString("time_format", "Default").contains("24-Hour")) {
            fullDate = Utils.getSimpleDateFormatForUI("MMMM d, yyyy HH:mm zzz");
        } else if (DateFormat.is24HourFormat(context)) {
            fullDate = Utils.getSimpleDateFormatForUI("MMMM d, yyyy HH:mm zzz");
        } else {
            fullDate = Utils.getSimpleDateFormatForUI("MMMM d, yyyy h:mm a zzz");
        }

        return fullDate;
    }

    public static String getStatusBasedDateFormat(Date net, LaunchStatus status) {
        String launchDate;

        if (status.getId() == 2) {
            // If launch status is To Be Determined - only show Month and Year.
            String date = Utils.getSimpleDateFormatForUI("MMMM, yyyy").format(net);
            launchDate = "NET " + date;
        } else if (status.getId() == 8) {
            // If launch status is To Be Confirmed - only show Month, Day, and Year.
            launchDate = Utils.getSimpleDateFormatForUI("MMMM d, yyyy").format(net);
        } else {
            launchDate = Utils.getSimpleDateFormatForUI("MMMM d, yyyy").format(net);
        }

        return launchDate;
    }

    public static String getStatusBasedDateTimeFormat(Date net, LaunchStatus status, Context context) {
        String launchDate;

        if (status.getId() == 2) {
            // If launch status is To Be Determined - only show Month and Year.
            String date = Utils.getSimpleDateFormatForUI("MMMM, yyyy").format(net);
            launchDate = "NET " + date;

        } else if (status.getId() == 8) {
            // If launch status is To Be Confirmed - only show Month, Day, and Year.
            launchDate = Utils.getSimpleDateFormatForUI("MMMM d, yyyy").format(net);
        } else {
            launchDate = Utils.getSLNDateTimeFormat(context).format(net);
        }

        return launchDate;
    }


}


