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
import java.util.concurrent.atomic.AtomicInteger;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.content.database.ListPreferences;
import me.calebjones.spacelaunchnow.data.models.launchlibrary.Launch;
import me.calebjones.spacelaunchnow.utils.customtab.CustomTabActivityHelper;
import me.calebjones.spacelaunchnow.utils.customtab.WebViewFallback;
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

        if (sharedPreference.isNightModeActive(context)) {
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
        return PendingIntent.getActivity(context, 0, actionIntent, 0);
    }

    public static Intent buildShareIntent(Launch launch) {
        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM dd, yyyy hh:mm a zzz");
        df.toLocalizedPattern();

        Date date = launch.getWindowstart();
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

    public static int getCategoryIcon(String type, Boolean night) {
        if (type != null) {
            switch (type) {
                case "Earth Science":
                    if (night) {
                        return R.drawable.ic_earth_white;
                    } else {
                        return R.drawable.ic_earth;
                    }
                case "Planetary Science":
                    if (night) {
                        return R.drawable.ic_planetary_white;
                    } else {
                        return R.drawable.ic_planetary;
                    }

                case "Astrophysics":
                    if (night) {
                        return R.drawable.ic_astrophysics_white;
                    } else {
                        return R.drawable.ic_astrophysics;
                    }

                case "Heliophysics":
                    if (night) {
                        return R.drawable.ic_heliophysics_alt_white;
                    } else {
                        return R.drawable.ic_heliophysics_alt;
                    }

                case "Human Exploration":
                    if (night) {
                        return R.drawable.ic_human_explore_white;
                    } else {
                        return R.drawable.ic_human_explore;
                    }
                case "Robotic Exploration":
                    if (night) {
                        return R.drawable.ic_robotic_explore_white;
                    } else {
                        return R.drawable.ic_robotic_explore;
                    }
                case "Government/Top Secret":
                    if (night) {
                        return R.drawable.ic_top_secret_white;
                    } else {
                        return R.drawable.ic_top_secret;
                    }
                case "Tourism":
                    if (night) {
                        return R.drawable.ic_tourism_white;
                    } else {
                        return R.drawable.ic_tourism;
                    }
                case "Unknown":
                    if (night) {
                        return R.drawable.ic_unknown_white;
                    } else {
                        return R.drawable.ic_unknown;
                    }
                case "Communications":
                    if (night) {
                        return R.drawable.ic_satellite_white;
                    } else {
                        return R.drawable.ic_satellite;
                    }
                case "Resupply":
                    if (night) {
                        return R.drawable.ic_resupply_white;
                    } else {
                        return R.drawable.ic_resupply;
                    }
                default:
                    if (night) {
                        return R.drawable.ic_unknown_white;
                    } else {
                        return R.drawable.ic_unknown;
                    }
            }
        } else {
            if (night) {
                return R.drawable.ic_unknown_white;
            } else {
                return R.drawable.ic_unknown;
            }
        }
    }

    public static void setCategoryIcon(ImageView imageView, String type, Boolean night) {
        if (type != null) {
            switch (type) {
                case "Earth Science":
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_earth_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_earth);
                    }
                    break;
                case "Planetary Science":
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_planetary_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_planetary);
                    }
                    break;
                case "Astrophysics":
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_astrophysics_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_astrophysics);
                    }
                    break;
                case "Heliophysics":
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_heliophysics_alt_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_heliophysics_alt);
                    }
                    break;
                case "Human Exploration":
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_human_explore_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_human_explore);
                    }
                    break;
                case "Robotic Exploration":
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_robotic_explore_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_robotic_explore);
                    }
                    break;
                case "Government/Top Secret":
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_top_secret_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_top_secret);
                    }
                    break;
                case "Tourism":
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_tourism_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_tourism);
                    }
                    break;
                case "Unknown":
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_unknown_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_unknown);
                    }
                    break;
                case "Communications":
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_satellite_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_satellite);
                    }
                    break;
                case "Resupply":
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_resupply_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_resupply);
                    }
                    break;
                default:
                    if (night) {
                        imageView.setImageResource(R.drawable.ic_unknown_white);
                    } else {
                        imageView.setImageResource(R.drawable.ic_unknown);
                    }
                    break;
            }
        } else {
            if (night) {
                imageView.setImageResource(R.drawable.ic_unknown_white);
            } else {
                imageView.setImageResource(R.drawable.ic_unknown);
            }
        }
    }

    public static void setCategoryIcon(RemoteViews remoteViews, String type, Boolean night, Integer id) {
        if (type != null) {
            switch (type) {
                case "Earth Science":
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_earth_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_earth);
                    }
                    break;
                case "Planetary Science":
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_planetary_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_planetary);
                    }
                    break;
                case "Astrophysics":
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_astrophysics_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_astrophysics);
                    }
                    break;
                case "Heliophysics":
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_heliophysics_alt_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_heliophysics_alt);
                    }
                    break;
                case "Human Exploration":
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_human_explore_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_human_explore);
                    }
                    break;
                case "Robotic Exploration":
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_robotic_explore_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_robotic_explore);
                    }
                    break;
                case "Government/Top Secret":
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_top_secret_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_top_secret);
                    }
                    break;
                case "Tourism":
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_tourism_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_tourism);
                    }
                    break;
                case "Unknown":
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_unknown_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_unknown);
                    }
                    break;
                case "Communications":
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_satellite_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_satellite);
                    }
                    break;
                case "Resupply":
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_resupply_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_resupply);
                    }
                    break;
                default:
                    if (night) {
                        remoteViews.setImageViewResource(id, R.drawable.ic_unknown_white);
                    } else {
                        remoteViews.setImageViewResource(id, R.drawable.ic_unknown);
                    }
                    break;
            }
        } else {
            if (night) {
                remoteViews.setImageViewResource(id, R.drawable.ic_unknown_white);
            } else {
                remoteViews.setImageViewResource(id, R.drawable.ic_unknown);
            }
        }
    }

    public static String getFormattedDateFromTimestamp(long timestampInMilliSeconds) {
        Date date = new Date();
        date.setTime(timestampInMilliSeconds);
        return new SimpleDateFormat("h:mm a z - MMM d, yyyy ", Locale.US).format(date);
    }

    public static Bitmap getBitMapFromUrl(Context context, String imageURL) {
        try {
            return GlideApp.with(context)
                    .asBitmap()
                    .load(imageURL)
                    .submit(200, 200)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Timber.e(e);
            return null;
        }
    }

    public static SimpleDateFormat getSimpleDateFormatForUI(String pattern) {
        String format =  DateFormat.getBestDateTimePattern(Locale.getDefault(), pattern);
        return new SimpleDateFormat(format, Locale.getDefault());
    }
}

