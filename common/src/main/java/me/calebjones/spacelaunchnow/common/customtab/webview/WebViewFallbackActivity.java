package me.calebjones.spacelaunchnow.common.customtab.webview;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.widget.Toolbar;
import me.calebjones.spacelaunchnow.common.R;
import me.calebjones.spacelaunchnow.common.base.BaseActivity;

public class WebViewFallbackActivity extends BaseActivity {
    public static final String EXTRA_URL = "extra.url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_fallback);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String url = getIntent().getStringExtra(EXTRA_URL);
        WebView webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        setTitle("Space Launch Now - " + url);

        webView.loadUrl(url);
    }

}
