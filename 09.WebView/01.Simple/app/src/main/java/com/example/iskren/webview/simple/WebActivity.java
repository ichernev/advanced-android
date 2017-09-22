package com.example.iskren.webview.simple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_URL = "url";

    private WebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        String url = (String) getIntent().getExtras().get(EXTRA_KEY_URL);

        webView = (WebView) findViewById(R.id.webview);

        // Otherwise the loadUrl below just spawns a real browser
        webView.setWebViewClient(new WebViewClient());

        // so that its like a normal browser
        webView.getSettings().setJavaScriptEnabled(true);

        // fire it up!
        webView.loadUrl(url);
    }
}
