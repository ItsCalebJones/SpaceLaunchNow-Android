package me.calebjones.spacelaunchnow.utils.customtab;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import me.calebjones.spacelaunchnow.ui.activity.WebviewActivity;
import me.calebjones.spacelaunchnow.utils.customtab.CustomTabActivityHelper;

/**
 * A Fallback that opens a Webview when Custom Tabs is not available
 */
public class WebViewFallback implements CustomTabActivityHelper.CustomTabFallback {
    @Override
    public void openUri(Activity activity, Uri uri) {
        Intent intent = new Intent(activity, WebviewActivity.class);
        intent.putExtra(WebviewActivity.EXTRA_URL, uri.toString());
        activity.startActivity(intent);
    }
}