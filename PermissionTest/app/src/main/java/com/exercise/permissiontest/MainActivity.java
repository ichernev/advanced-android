package com.exercise.permissiontest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button btnDoNetwork;
    TextView tvDeviceId;
    Button btnGetInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDeviceId = (TextView) findViewById(R.id.deviceId);
        btnGetInfo = (Button) findViewById(R.id.getInfo);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean hasPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        btnGetInfo.setEnabled(hasPermission);
    }

    public void doPermissionCheck(View view) {
        //Toast.makeText(this, "Permission Check", Toast.LENGTH_SHORT).show();

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        String deviceId = tm.getDeviceId();

        tvDeviceId.setText(deviceId);
    }
















    public void doNetwork(View view) {
        //TODO
        //Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
        networkTest();
    }




    private void networkTest() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                HttpURLConnection urlConnection = null;

                try {
                    URL url = new URL("http://www.android.com/");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    in.read();
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
                return null;
            }
        }.execute();
    }
}
