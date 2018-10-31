package me.calebjones.spacelaunchnow.ui.webview;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.calebjones.spacelaunchnow.R;
import me.calebjones.spacelaunchnow.common.BaseActivity;
import me.calebjones.spacelaunchnow.utils.analytics.Analytics;

public class WebViewFallbackActivity extends BaseActivity {
    public static final String EXTRA_URL = "extra.url";

    @BindView(R.id.progressView)
    ProgressBar bar;

    public WebViewFallbackActivity() {
        super("Web View Activity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_fallback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        String url = getIntent().getStringExtra(EXTRA_URL);
        WebView webView = (WebView)findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        setTitle("Space Launch Now - " + url);

        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                if(progress < 100 && bar.getVisibility() == ProgressBar.GONE){
                    bar.setVisibility(ProgressBar.VISIBLE);
                }
                bar.setProgress(progress);
                if(progress == 100) {
                    bar.setVisibility(ProgressBar.GONE);
                }
            }
        });
        Analytics.getInstance().sendScreenView("Web View Activity", "Loaded URL: " + url);

        webView.loadUrl(url);
    }

}
