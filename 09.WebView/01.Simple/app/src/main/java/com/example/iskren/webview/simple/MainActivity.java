package com.example.iskren.webview.simple;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SampleWebView";
    private static final String ENDPOINT_URL = "http://192.168.0.112:3000/";

    private boolean warmedUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.webview_url);
        ((Button) findViewById(R.id.webview_start)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goButtonClicked();
            }
        });

        //goButtonClicked();
    }

    private void goButtonClicked() {
        if (!warmedUp) {

            Log.i(TAG, "in thread " + Thread.currentThread().getName());
            (new AsyncTask() {

                @Override
                protected Object doInBackground(Object[] params) {
                    doHttpRequest();
                    doHttpRequest();
                    doHttpRequest();
                    return null;
                }
            }).execute();
            warmedUp = true;
        } else {
            openWebActivity(ENDPOINT_URL);
        }
    }

    private void doHttpRequest() {
        HttpURLConnection conn = null;
        try {
            Log.i(TAG, "in async thread " + Thread.currentThread().getName());
            URL url = new URL(ENDPOINT_URL);
            // URL url = new URL("http://wap.telenor.bg/jsp/headers2.jsp");
            conn = (HttpURLConnection) url.openConnection();

            CookieManager cm = CookieManager.getInstance();
            String cookie = cm.getCookie(ENDPOINT_URL);
            if (cookie != null && cookie.length() > 0) {
                Log.i(TAG, "Setting cookie " + cookie);
                conn.setRequestProperty("Cookie", cookie);
            } else {
                Log.i(TAG, "No cookie");
            }
            InputStream in = conn.getInputStream();

            for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
                Log.i(TAG, "RESPONSE HEADER " + entry.getKey() + " " +
                        TextUtils.join(",", entry.getValue()));
                if (entry.getKey() != null && entry.getKey().equalsIgnoreCase("set-cookie")) {
                    for (String cookieLine : entry.getValue()) {
                        Log.i(TAG, "Setting cookie " + cookieLine);
                        cm.setCookie(ENDPOINT_URL, cookieLine);
                    }
                }
            }
            String res = readInputStream(in);
            //((TextView) findViewById(R.id.textview)).setText(res);
            Log.i(TAG, "GOT TEXT\n" + res);
        } catch (MalformedURLException e) {
            Log.e(TAG, "ERR " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "ERR " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String readInputStream(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        int bytes;
        while ((bytes = in.read(buffer)) != -1) {
            result.write(buffer, 0, bytes);
        }

        return result.toString("UTF-8");
    }

    private void openWebActivity(String url) {
        // String url = ((EditText) findViewById(R.id.webview_url)).getText().toString();
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(WebActivity.EXTRA_KEY_URL, url);
        startActivity(intent);
    }
}
