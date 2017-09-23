package com.example.iskren.webview.simple;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

public class WebActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_URL = "url";

    private WebView webView = null;

    static class JsObject {
        @JavascriptInterface
        public String toString() { return "injectedObject"; }

        @JavascriptInterface
        public String join(String[] els, String sep) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < els.length; i += 1) {
                if (i != 0) {
                    res.append(sep);
                }
                res.append(els[i]);
            }
            return res.toString();
        }

        @JavascriptInterface
        public void consume(double[] els) {
            for (Object el : els) {
                Log.i("TAG", "got " + el.getClass().getName());
            }
        }

        @JavascriptInterface
        public String returnJSON() {
            JSONArray ar = new JSONArray();
            ar.put("foo");
            ar.put("bar");
            ar.put("baz");
            try {
                return ar.toString(4);
            } catch (JSONException e) {
                return "Bad stuff";
            }
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

        webView.loadData("<h1 id='x'>hi</h1>", "text/html", null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript("console.log(5)", null);
            webView.evaluateJavascript("console.log(injectedObject.join(['1', '2'], 'xxx'))", null); // 1xxx2
            webView.evaluateJavascript("console.log(injectedObject.join([1, '2'], 'xxx'))", null); // nullxxx2
            webView.evaluateJavascript("injectedObject.consume([1, 2, 3])", null);
            webView.evaluateJavascript("injectedObject.consume([1, '2', true, Math.PI, /ala/])", null);
            webView.evaluateJavascript("document.write(JSON.stringify(injectedObject.returnArray()))", null);
            //webView.evaluateJavascript("document.write(injectedObject.toString())", null);
        }
        // webView.loadUrl("javascript:console.log(injectedObject.toString())");
    }
}
