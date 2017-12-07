package com.exercise.security;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.SafetyNetClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    TextView tvInfo;
    ProgressBar pbLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = (TextView) findViewById(R.id.respInfo);
        pbLoader = (ProgressBar) findViewById(R.id.loader);

    }

    @UiThread
    public void createRequest(View view) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                tvInfo.setText("");
                pbLoader.setVisibility(View.VISIBLE);
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = ConnectionUtil.getInstance().badCert();
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                pbLoader.setVisibility(View.GONE);
                tvInfo.setText(s);
            }
        }.execute();

    }

    @UiThread
    public void reCaptcha(View v) {
        SafetyNetClient client = SafetyNet.getClient(this);
        String key = getString(R.string.SITE_KEY);
        client.verifyWithRecaptcha(key)
                .addOnSuccessListener(this,
                        new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                // Indicates communication with reCAPTCHA service was
                                // successful.
                                final String userResponseToken = response.getTokenResult();
                                if (!userResponseToken.isEmpty()) {
                                    // Validate the user response token using the
                                    // reCAPTCHA siteverify API.

                                    new SimpleTask(tvInfo, pbLoader){

                                        @Override
                                        protected String doInBackground(Void... params) {
                                            return ConnectionUtil.getInstance().verifyReCaptcha(userResponseToken);
                                        }

                                    }.execute();


                                }
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            // An error occurred when communicating with the
                            // reCAPTCHA service. Refer to the status code to
                            // handle the error appropriately.
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.d("SafetyNet", "Error: " + CommonStatusCodes
                                    .getStatusCodeString(statusCode));
                        } else {
                            // A different, unknown type of error occurred.
                            Log.d("SafetyNet", "Error: " + e.getMessage());
                        }
                    }
                });
    }

    @UiThread
    public void logProviders(View view) {
        Crypto.logProviders();
    }

    @UiThread
    public void doCrypting(View view) {
        try {
            generateHtmlFile();

            File src = new File(getExternalCacheDir() + "/test.html");
            File dest = new File(getExternalCacheDir() + "/test.enc");
            File dec = new File(getExternalCacheDir() + "/test.dec.html");

            String original = getFileAsString(src);

            Crypto.encrypt("12344321", src, dest);
            String encrypted = getFileAsString(dest);

            Crypto.decrypt("12344321", dest, dec);
            String decrypted = getFileAsString(dec);

            tvInfo.setText(original +
                    "\n\n---- Enc ----\n" +
                    encrypted +
                    "\n\n---- Dec ----\n" +
                    decrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileAsString(File file) {

        try {
            InputStream in = new FileInputStream(file);
            byte[] b  = new byte[(int)file.length()];
            int len = b.length;
            int total = 0;

            while (total < len) {
                int result = in.read(b, total, len - total);
                if (result == -1) {
                    break;
                }
                total += result;
            }

            return new String( b , "UTF-8" );
        }catch (Exception e) {
            return "err: " + e.getMessage();
        }
    }

    private void generateHtmlFile() {
        File file = new File(getExternalCacheDir() + "/test.html");
        if (!file.exists()) {
            FileOutputStream os = null;
            InputStream is = null;
            try {
                is = new ByteArrayInputStream(simpleHTML.getBytes());
                os = new FileOutputStream(file);
                int count = 0;
                byte[] buf = new byte[1024];
                while ((count = is.read(buf)) > 0) {
                    os.write(buf, 0, count);
                }
                os.flush();
            } catch (IOException e) {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    private static final String simpleHTML = "" +
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<body>\n" +
            "\n" +
            "<h1>My First Heading</h1>\n" +
            "\n" +
            "<p>My first paragraph.</p>\n" +
            "\n" +
            "</body>\n" +
            "</html>\n";

    @UiThread
    public void insertData(View view) {
        // Defines a new Uri object that receives the result of the insertion
        Uri mNewUri;

        // Defines an object to contain the new values to insert
        ContentValues mNewValues = new ContentValues();
        mNewValues.put(SimpleDatabase.COL_TITLE, "google");
        mNewValues.put(SimpleDatabase.COL_URL, "http://google.com");

        mNewUri = getContentResolver().insert(SimpleProvider.CONTENT_URI, mNewValues);
        Log.d("INSERTED", "URI:"+ mNewUri);

        mNewValues = new ContentValues();
        mNewValues.put(SimpleDatabase.COL_TITLE, "telenor");
        mNewValues.put(SimpleDatabase.COL_URL, "http://telenor.bg");

        mNewUri = getContentResolver().insert(SimpleProvider.CONTENT_URI, mNewValues);
        Log.d("INSERTED", "URI:"+ mNewUri);
    }

    @UiThread
    public void getDataFromCP(View view) {
        String[] projection = { SimpleDatabase.ID, SimpleDatabase.COL_TITLE };

        Cursor linksCursor = getContentResolver().query(SimpleProvider.CONTENT_URI, projection, null, null, null);

        StringBuilder builder = new StringBuilder("Contents: \n");
        while (linksCursor.moveToNext()) {

            // Gets the value from the column.
            String id = linksCursor.getString(0);
            String title = linksCursor.getString(1);

            builder.append("Id: ").append(id).append(" ").append(title).append("\n");
            // end of while loop
        }

        tvInfo.setText(builder.toString());

        linksCursor.close();

    }
}
