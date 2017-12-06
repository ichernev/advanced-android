package com.exercise.sharing;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class BundleActivity extends AppCompatActivity {

    public static final String EXTRA_STRING = "EXTRA_STRING";
    public static final String EXTRA_INTEGER = "EXTRA_INTEGER";

    TextView tvBundleData;
    TextView tvSCBundleData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundle);

        tvBundleData = (TextView) findViewById(R.id.bundle_data);
        tvSCBundleData = (TextView) findViewById(R.id.sc_bundle_data);

        showReceivedIntent();

        handleReceivedIntent();


    }

    private void showReceivedIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        Set<String> categories = intent.getCategories();
        ComponentName componentName = intent.getComponent();
        String data = intent.getDataString();
        String type = intent.getType();
        int flags = intent.getFlags();
        Uri receivedUri = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);

        String strInfo = intent.getStringExtra(EXTRA_STRING);
        int intInfo = intent.getIntExtra(EXTRA_INTEGER, -1);

        tvBundleData.setText("Action: " + action +
                "\nCategories: " + (categories == null ? "N/A" : Arrays.toString(categories.toArray(new String[categories.size()]))) +
                "\nComponentName: " + componentName.getClassName() +
                "\nData: " + data +
                "\nType: " + type +
                "\nFlags: " + flags +
                "\nStream: " + receivedUri +
                "\nSample Info: " + strInfo + "; " + intInfo
        );

        //use ShareCompat
        ShareCompat.IntentReader intentReader = ShareCompat.IntentReader.from(this);

        if (intentReader.isShareIntent()) {
            String[] emailTo = intentReader.getEmailTo();
            String subject = intentReader.getSubject();
            String text = intentReader.getHtmlText();
            Uri uri = intentReader.getStream();
            // Compose an email

            tvSCBundleData.setText("Calling Activity: " + intentReader.getCallingActivity() +
                    "\nemailTo: " + emailTo +
                    "\nsubject: " + subject +
                    "\ntext: " + text +
                    "\nuri: " + uri +
                    "\nApp: " + intentReader.getCallingApplicationLabel() +
                    "\ntype: " + intentReader.getType());
        }

    }


    private void handleReceivedIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                Toast.makeText(this, "text/plain received", Toast.LENGTH_SHORT).show();
            } else if (type.startsWith("image/")) {
                Uri receivedUri = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
                Toast.makeText(this, "image/ received" + receivedUri, Toast.LENGTH_SHORT).show();
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Toast.makeText(this, "Multi image received received", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle other intents, such as being started from the home screen
            Toast.makeText(this, "action " + action, Toast.LENGTH_SHORT).show();
        }
    }

    private ShareActionProvider mShareActionProvider;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.main, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent(createShareIntent());
        // Return true to display menu
        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Provider...");
        return shareIntent;
    }

    /**
     * Use {@link #createIntent(Context, String)}
     * @param context
     * @return
     */
    @Deprecated
    public static Intent createIntent(Context context) {
        return createIntent(context, "Ne prati nishto");
    }

    public static Intent createIntent(Context context, @NonNull String string) {
        Intent intent = new Intent(context, BundleActivity.class);

        intent.putExtra(EXTRA_STRING, string);

        return intent;

    }
}
