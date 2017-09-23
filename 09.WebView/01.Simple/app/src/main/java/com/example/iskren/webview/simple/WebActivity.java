package com.example.iskren.webview.simple;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebMessage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WebActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_URL = "url";

    private WebView webView = null;

    private String baseUrl;

    class JsObject {
        @JavascriptInterface
        public String toString() { return "injectedObject"; }

        @JavascriptInterface
        public void scriptLoaded() {
            (new Handler(WebActivity.this.getMainLooper())).post(new Runnable() {
                @Override
                public void run() {
                    WebActivity.this.doPostWebMessage();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Log.i("TAG", "Hello world");

        String url = (String) getIntent().getExtras().get(EXTRA_KEY_URL);

        webView = (WebView) findViewById(R.id.webview);

        // Otherwise the loadUrl below just spawns a real browser
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage message) {
                Log.i("TAG", message.toString());
                return false;
            }
        });

        // so that its like a normal browser
        webView.getSettings().setJavaScriptEnabled(true);

        // make it debuggable -- only in test!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.addJavascriptInterface(new JsObject(), "injectedObject");
//
//        webView.loadData("<h1 id='x'>hi</h1>", "text/html", null);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            webView.evaluateJavascript("console.log(5)", null);
//            webView.evaluateJavascript("console.log(injectedObject.join(['1', '2'], 'xxx'))", null); // 1xxx2
//            webView.evaluateJavascript("console.log(injectedObject.join([1, '2'], 'xxx'))", null); // nullxxx2
//            webView.evaluateJavascript("injectedObject.consume([1, 2, 3])", null);
//            webView.evaluateJavascript("injectedObject.consume([1, '2', true, Math.PI, /ala/])", null);
//            webView.evaluateJavascript("document.write(JSON.stringify(injectedObject.returnArray()))", null);
//            //webView.evaluateJavascript("document.write(injectedObject.toString())", null);
//        }
//        // webView.loadUrl("javascript:console.log(injectedObject.toString())");
        baseUrl = "file:///android_asset/index.html";
        webView.loadDataWithBaseURL(
                baseUrl,
                readAssetFile(this, "index.html"),
                "text/html",
                "utf-8",
                null);

    }

    private void doPostWebMessage() {
        Log.i("TAG", "Now we can post!!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            webView.postWebMessage(new WebMessage("You can haz data"), Uri.parse(""));
        }
    }

    private String readAssetFile(Context ctx, String filename) {
        try {
            InputStream inputStream = ctx.getAssets().open(filename);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } catch (IOException e) {
            return null;
        }
    }

}
