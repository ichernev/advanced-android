package com.example.iskren.a05location;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = "SampleLocation";

    private LocationManager locationManager;
    private MyLocationListener locationListener;

    private static final int OPQ = 0xFF000000;
    private static final int GREEN = 0x00ff00 | OPQ;
    private static final int RED = 0xff0000 | OPQ;
    private static final int BLUE = 0x0000ff | OPQ;

    private static final int LOCATION_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        updatePermUI();

        ((Button) findViewById(R.id.gps_permission)).setOnClickListener(this);
        ((Button) findViewById(R.id.gps_permission)).setOnClickListener(this);

        ((ToggleButton) findViewById(R.id.gps_toggle)).setOnCheckedChangeListener(this);
        ((ToggleButton) findViewById(R.id.network_toggle)).setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String provider = buttonView.getId() == R.id.gps_toggle ?
                LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;

        if (isChecked) {
            if ((provider.equals(LocationManager.GPS_PROVIDER) && hasFinePerm()) ||
                    (provider.equals(LocationManager.NETWORK_PROVIDER) && hasCoarsePerm())) {
                try {
                    locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
                } catch (SecurityException e) {
                    Log.e(TAG, "Looks line a race condition to me -- no permission for provider " +
                            provider + " " + e.toString());
                    updatePermUI();
                }
            } else {
                buttonView.setChecked(!isChecked);
            }
        } else {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onClick(View v) {
        String[] perms;
        if (v.getId() == R.id.gps_permission) {
            perms = new String[]{ Manifest.permission.ACCESS_FINE_LOCATION };
        } else {
            perms = new String[] { Manifest.permission.ACCESS_COARSE_LOCATION };
        }
        ActivityCompat.requestPermissions(this,
                perms,
                LOCATION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        Log.i(TAG, "got perm result " + TextUtils.join(",", permissions) +
                " " + TextUtils.join(",", Arrays.asList(grantResults)));
        switch (requestCode) {
            case LOCATION_REQUEST: {
                updatePermUI();
            }
        }
    }

    private boolean hasFinePerm() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasCoarsePerm() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void updatePermUI() {
        boolean hasGPS = hasFinePerm();
        setLabelState(R.id.gps_label, hasGPS);
        // setButtonState(R.id.gps_permission, !hasGPS);

        boolean hasNet = hasCoarsePerm();
        setLabelState(R.id.network_label, hasNet);
        // setButtonState(R.id.network_permission, !hasNet);
    }

    private void setLabelState(int id, boolean state) {
        TextView textView = (TextView) findViewById(id);

        if (state) {
            textView.setText("ON");
            textView.setTextColor(BLUE);
            textView.setBackgroundColor(GREEN);
        } else {
            textView.setText("OFF");
            textView.setTextColor(BLUE);
            textView.setBackgroundColor(RED);
        }
    }

    private void setButtonState(int id, boolean state) {
        Button button = (Button) findViewById(id);
        button.setEnabled(state);
    }

    class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location location) {
            Log.i(TAG, String.format("received location %s", location));
            findViewById(R.id.location_log);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = (TextView) findViewById(R.id.location_log);
                    CharSequence existing = tv.getText();
                    tv.setText(location.toString() + "\n" + existing);
                }
            });
            // Called when a new location is found by the network location provider.
            //makeUseOfNewLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, String.format("provider %s changed status %d %s", provider, status, extras));
        }

        public void onProviderEnabled(String provider) {
            Log.i(TAG, String.format("provider %s is ON", provider));
        }

        public void onProviderDisabled(String provider) {
            Log.i(TAG, String.format("provider %s is OFF", provider));
        }
    };

}
