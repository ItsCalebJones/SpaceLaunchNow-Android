package me.calebjones.spacelaunchnow.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.calebjones.spacelaunchnow.R;

public class WebViewFallbackActivity extends AppCompatActivity {
    public static final String EXTRA_URL = "extra.url";

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.progressView)
    ProgressBar bar;

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

        webView.loadUrl(url);
    }

    @OnClick(R.id.fab) void shareLink(View view){
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}
