package me.calebjones.spacelaunchnow.common.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;

import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.customtab.CustomTabActivityHelper;
import me.calebjones.spacelaunchnow.common.customtab.SLNWebViewFallback;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

public class LinkHandler {

    private static PendingIntent createPendingShareIntent(Context context, String url) {
        Intent actionIntent = new Intent(Intent.ACTION_SEND);
        actionIntent.setType("text/plain");
        actionIntent.putExtra(Intent.EXTRA_TEXT, url);
        return PendingIntent.getActivity(context, 0, actionIntent, PendingIntent.FLAG_MUTABLE);
    }

    public static void openCustomTab(Activity activity, Context context, String url) {
        if (!URLUtil.isValidUrl(url)) {
            Toast.makeText(activity, "ERROR: URL is malformed - sorry! " + url, Toast.LENGTH_SHORT).show();
            return;
        }
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

            CustomTabActivityHelper.openCustomTab(activity, intentBuilder.build(), Uri.parse(url), new SLNWebViewFallback());
        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(browserIntent);
        }
    }

    public static void openCustomTab(Context context, String url) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (!URLUtil.isValidUrl(url)) {
            Toast.makeText(context, "ERROR: URL is malformed - sorry! " + url, Toast.LENGTH_SHORT).show();
            return;
        }

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
}
