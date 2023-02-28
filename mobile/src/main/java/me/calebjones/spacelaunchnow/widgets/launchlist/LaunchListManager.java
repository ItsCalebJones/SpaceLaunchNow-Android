package me.calebjones.spacelaunchnow.widgets.launchlist;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterActivity;
import me.calebjones.spacelaunchnow.common.ui.supporter.SupporterHelper;
import me.calebjones.spacelaunchnow.widgets.WidgetBroadcastReceiver;
import timber.log.Timber;


public class LaunchListManager {

    private Context context;
    private AppWidgetManager appWidgetManager;

    public LaunchListManager(Context context) {
        this.context = context;
        appWidgetManager = AppWidgetManager.getInstance(context);
    }

    public void updateAppWidget(int appWidgetId) {
        Timber.d("Updating appWidgetId %s", appWidgetId);
        if (SupporterHelper.isSupporter()) {
            buildWidget(appWidgetId);
        } else {
            buildSupporterWidget(appWidgetId);
        }

    }

    private void buildSupporterWidget(int appWidgetId) {
        Timber.d("Building Support layout - %s", appWidgetId);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_become_supporter);
        int colorWhite = 0xFFFFFFFF;
        int colorSecondaryWhite = 0xB3FFFFFF;
        int colorBackground = 0xFF303030;
        int colorAccent = 0xFF455a64;
        int widgetPrimaryTextColor = sharedPref.getInt("widget_text_color", colorWhite);
        int widgetAccentColor = sharedPref.getInt("widget_list_accent_color", colorAccent);
        int widgetBackgroundColor = sharedPref.getInt("widget_background_color", colorBackground);
        int widgetSecondaryTextColor = sharedPref.getInt("widget_secondary_text_color", colorSecondaryWhite);
        int widgetTitleTextColor = sharedPref.getInt("widget_title_text_color", colorWhite);
        int widgetIconsColor = sharedPref.getInt("widget_icon_color", colorWhite);

        if (sharedPref.getBoolean("widget_theme_round_corner", true)) {
            remoteViews.setImageViewResource(R.id.bgcolor, R.drawable.rounded_bottom);
            remoteViews.setImageViewResource(R.id.title_background, R.drawable.rounded_top);
        } else {
            remoteViews.setImageViewResource(R.id.bgcolor, R.drawable.squared);
            remoteViews.setImageViewResource(R.id.title_background, R.drawable.squared);
        }

        remoteViews.setTextColor(R.id.title, widgetTitleTextColor);
        int widgetAlpha = Color.alpha(widgetBackgroundColor);
        int red = Color.red(widgetBackgroundColor);
        int green = Color.green(widgetBackgroundColor);
        int blue = Color.blue(widgetBackgroundColor);
        remoteViews.setInt(R.id.bgcolor, "setColorFilter", Color.rgb(red,green,blue));
        remoteViews.setInt(R.id.bgcolor, "setAlpha", widgetAlpha);

        widgetAlpha = Color.alpha(widgetAccentColor);
        red = Color.red(widgetAccentColor);
        green = Color.green(widgetAccentColor);
        blue = Color.blue(widgetAccentColor);
        remoteViews.setInt(R.id.title_background, "setColorFilter", Color.rgb(red,green,blue));
        remoteViews.setInt(R.id.title_background, "setAlpha", widgetAlpha);

        remoteViews.setInt(R.id.widget_refresh_button, "setColorFilter", widgetTitleTextColor);

        remoteViews.setTextColor(R.id.supporter_message, widgetPrimaryTextColor);
        remoteViews.setTextViewText(R.id.title, "Space Launch Now");
        remoteViews.setImageViewResource(R.id.widget_refresh_button, R.drawable.ic_refresh_black);

        Intent nextIntent = new Intent(context, WidgetBroadcastReceiver.class);
        PendingIntent refreshPending = PendingIntent.getBroadcast(context, 0,
                nextIntent,
                PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPending);

        Intent openAppIntent = null;
        try {
            openAppIntent = new Intent(context, Class.forName("me.calebjones.spacelaunchnow.ui.main.MainActivity"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        openAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        remoteViews.setOnClickPendingIntent(R.id.title, actionPendingIntent);

        Intent supportIntent = new Intent(context, SupporterActivity.class);
        supportIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent supportPendingIntent = PendingIntent.getActivity(context,
                0,
                supportIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        remoteViews.setOnClickPendingIntent(R.id.supporter_message, supportPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    private void buildWidget(int appWidgetId) {
        Timber.d("Building full layout - %s", appWidgetId);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_launch_list);

        int colorWhite = 0xFFFFFFFF;
        int colorSecondaryWhite = 0xB3FFFFFF;
        int colorBackground = 0xFF303030;
        int colorAccent = 0xFF455a64;
        int widgetPrimaryTextColor = sharedPref.getInt("widget_text_color", colorWhite);
        int widgetAccentColor = sharedPref.getInt("widget_list_accent_color", colorAccent);
        int widgetBackgroundColor = sharedPref.getInt("widget_background_color", colorBackground);
        int widgetSecondaryTextColor = sharedPref.getInt("widget_secondary_text_color", colorSecondaryWhite);
        int widgetTitleTextColor = sharedPref.getInt("widget_title_text_color", colorWhite);
        int widgetIconsColor = sharedPref.getInt("widget_icon_color", colorWhite);

        if (sharedPref.getBoolean("widget_theme_round_corner", true)) {
            remoteViews.setImageViewResource(R.id.bgcolor, R.drawable.rounded_bottom);
            remoteViews.setImageViewResource(R.id.title_background, R.drawable.rounded_top);
        } else {
            remoteViews.setImageViewResource(R.id.bgcolor, R.drawable.squared);
            remoteViews.setImageViewResource(R.id.title_background, R.drawable.squared);
        }
        remoteViews.setTextColor(R.id.title, widgetTitleTextColor);

        int widgetAlpha = Color.alpha(widgetBackgroundColor);
        int red = Color.red(widgetBackgroundColor);
        int green = Color.green(widgetBackgroundColor);
        int blue = Color.blue(widgetBackgroundColor);
        remoteViews.setInt(R.id.bgcolor, "setColorFilter", Color.rgb(red,green,blue));
        remoteViews.setInt(R.id.bgcolor, "setAlpha", widgetAlpha);

        widgetAlpha = Color.alpha(widgetAccentColor);
        red = Color.red(widgetAccentColor);
        green = Color.green(widgetAccentColor);
        blue = Color.blue(widgetAccentColor);
        remoteViews.setInt(R.id.title_background, "setColorFilter", Color.rgb(red,green,blue));
        remoteViews.setInt(R.id.title_background, "setAlpha", widgetAlpha);
        remoteViews.setInt(R.id.widget_refresh_button, "setColorFilter", widgetTitleTextColor);

        Intent svcIntent = new Intent(context, LaunchListWidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        remoteViews.setRemoteAdapter(R.id.launch_list, svcIntent);
        remoteViews.setTextViewText(R.id.title, "Space Launch Now");
        remoteViews.setImageViewResource(R.id.widget_refresh_button, R.drawable.ic_refresh_black);

        Intent nextIntent = new Intent(context, WidgetBroadcastReceiver.class);
        PendingIntent refreshPending = PendingIntent.getBroadcast(context,
                0,
                nextIntent,
                PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPending);

        if (sharedPref.getBoolean("widget_refresh_enabled", false)) {
            remoteViews.setViewVisibility(R.id.widget_refresh_button, View.GONE);
        } else if (!sharedPref.getBoolean("widget_refresh_enabled", false)) {
            remoteViews.setViewVisibility(R.id.widget_refresh_button, View.VISIBLE);
        }

        Intent openAppIntent = null;
        try {
            openAppIntent = new Intent(context, Class.forName("me.calebjones.spacelaunchnow.ui.main.MainActivity"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        openAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        remoteViews.setOnClickPendingIntent(R.id.title, actionPendingIntent);


        // Sets up the intent that points to the StackViewService that will
        // provide the views for this collection.
        Intent intent = new Intent(context, LaunchListWidgetProvider.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        // When intents are compared, the extras are ignored, so we need to
        // embed the extras into the data so that the extras will not be
        // ignored.
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));


        // This section makes it possible for items to have individualized
        // behavior. It does this by setting up a pending intent template.
        // Individuals items of a collection cannot set up their own pending
        // intents. Instead, the collection as a whole sets up a pending
        // intent template, and the individual items set a fillInIntent
        // to create unique behavior on an item-by-item basis.
        Intent detailIntent = new Intent(context, LaunchListWidgetProvider.class);
        // Set the action for the intent.
        // When the user touches a particular view, it will have the effect of
        // broadcasting TOAST_ACTION.
        detailIntent.setAction(LaunchListWidgetProvider.DETAIL_ACTION);
        detailIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent detailPendingIntent = PendingIntent.getBroadcast(context, 0,
                detailIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        remoteViews.setPendingIntentTemplate(R.id.launch_list, detailPendingIntent);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.launch_list);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
}
