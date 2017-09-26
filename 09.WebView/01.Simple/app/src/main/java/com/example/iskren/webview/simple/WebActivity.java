package com.example.iskren.webview.simple;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebMessage;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
        webView.setWebViewClient(new WebViewClient() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest (
                    WebView view, WebResourceRequest request) {
                WebResourceResponse res = handleInterceptRequest(view, request.getUrl());
                if (res == null) {
                    return super.shouldInterceptRequest(view, request);
                } else {
                    return res;
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String x) {
                WebResourceResponse res = handleInterceptRequest(view, Uri.parse(x));
                if (res == null) {
                    return super.shouldInterceptRequest(view, x);
                } else {
                    return res;
                }
            }

            private WebResourceResponse handleInterceptRequest(WebView view, Uri uri) {
                String req = uri.toString();
                if (req.endsWith("script.js")) {
                    try {
                        InputStream is = getAssets().open(req.substring(baseUrl.length()));
                        return new WebResourceResponse("application/javascript", "utf-8", is);
                    } catch (IOException e) {
                        WebResourceResponse res = new WebResourceResponse(
                                "application/javascript", "utf-8", null);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            res.setStatusCodeAndReasonPhrase(404, "Not Found");
                        }
                        return res;
                    }
                } else {
                    return null;
                }
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage message) {
                Log.i("TAG", message.toString());
                return false;
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, true);
            }
        });

        // so that its like a normal browser
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);


        // make it debuggable -- only in test!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

//        webView.addJavascriptInterface(new JsObject(), "injectedObject");
//
//        webView.loadData("<h1 id='x'>hi</h1>", "text/html", null);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            webView.evaluateJavascript("[1,2,true, /ala/]", new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    Log.i("TAG", "Got back " + value);
//                }
//            });
//        }
//            webView.evaluateJavascript("console.log(5)", null);
//            webView.evaluateJavascript("console.log(injectedObject.join(['1', '2'], 'xxx'))", null); // 1xxx2
//            webView.evaluateJavascript("console.log(injectedObject.join([1, '2'], 'xxx'))", null); // nullxxx2
//            webView.evaluateJavascript("injectedObject.consume([1, 2, 3])", null);
//            webView.evaluateJavascript("injectedObject.consume([1, '2', true, Math.PI, /ala/])", null);
//            webView.evaluateJavascript("document.write(JSON.stringify(injectedObject.returnArray()))", null);
//            //webView.evaluateJavascript("document.write(injectedObject.toString())", null);
//        }
//        // webView.loadUrl("javascript:console.log(injectedObject.toString())");
        baseUrl = "https://iskren.info/"; // "file:///android_asset/index.html";
        webView.loadDataWithBaseURL(
                baseUrl,
                readAssetFile(this, "index.html"),
                "text/html",
                "utf-8",
                null);

        //webView.loadUrl(url);
//        final Handler h = new Handler(this.getMainLooper());
//        final Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                webView.loadUrl("javascript:document.write(\"win\")");
//                h.postDelayed(this, 2000);
//            }
//        };
//        h.postDelayed(r, 1000);
    }

    private void doPostWebMessage() {
        Log.i("TAG", "Now we can post!!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            webView.postWebMessage(new WebMessage("You can haz data"), Uri.parse(baseUrl));
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
