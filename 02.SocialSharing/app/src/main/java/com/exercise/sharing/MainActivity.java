package com.exercise.sharing;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("CONSUMER_KEY", "CONSUMER_SECRET"))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    public void sendBundle(View view) {
        Intent intent = BundleActivity.createIntent(this);
        intent.putExtra(BundleActivity.EXTRA_STRING, "string data");
        intent.putExtra(BundleActivity.EXTRA_INTEGER, 1);

        startActivity(intent);
    }

    public void simpleShare(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void simpleChooser(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_BUG_REPORT);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");

        startActivity(Intent.createChooser(sendIntent, "Some title..."));
    }

    public void simpleEmail(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"first.last@mail.to", "second.last@mail.to"});
        sendIntent.putExtra(Intent.EXTRA_CC, new String[]{"cc.name@mail.to"});
        sendIntent.putExtra(Intent.EXTRA_BCC, new String[]{"bcc.name@mail.to"});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "mail is about");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Some title..."));
    }

    public void simpleImage(View view) {
        generateImage();
        File image = new File(getExternalCacheDir() + "/image.png");

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Sending image"));
    }

    public void simpleMultiple(View view) {
        File image1 = new File(getExternalCacheDir() + "/image.png");
        File image2 = new File(getExternalCacheDir() + "/image.png");

        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        imageUris.add(Uri.fromFile(image1)); // Add your image URIs here
        imageUris.add(Uri.fromFile(image2));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"first.last@mail.to", "second.last@mail.to"});
        shareIntent.putExtra(Intent.EXTRA_CC, new String[]{"cc.name@mail.to"});
        shareIntent.putExtra(Intent.EXTRA_BCC, new String[]{"bcc.name@mail.to"});
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "mail is about");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share images to.."));
    }

    public void useShareCompat(View view) {

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/html")
                .setHtmlText(simpleHTML)
                .setSubject("Definitely read this")
                .addEmailTo("my.name@slim-shady.is")
                .getIntent();

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }

    }

    public void shareFB(View view) {
        callbackManager = CallbackManager.Factory.create();

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
    }

    public void shareFBPhoto(View view) {
        callbackManager = CallbackManager.Factory.create();

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .setCaption("Sharing the icon!")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
    }

    public void shareFBDirect(View view) {
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Warning this will directly post to your wall in FB")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> permissionNeeds = Arrays.asList("publish_actions");
                        loginManager.logInWithPublishPermissions(MainActivity.this, permissionNeeds);
                        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                                SharePhoto photo = new SharePhoto.Builder()
                                        .setBitmap(image)
                                        .setCaption("Sharing the icon!")
                                        .build();

                                SharePhotoContent content = new SharePhotoContent.Builder()
                                        .addPhoto(photo)
                                        .build();

                                ShareApi.share(content, null);
                            }

                            @Override
                            public void onCancel() {
                                System.out.println("onCancel");
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                System.out.println("onError");
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel",null)
                .create();

        dialog.show();
    }

    public void shareTwitter(View view) {
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("just setting up my Twitter Kit.");
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }


    private void generateImage() {
        File image = new File(getExternalCacheDir() + "/image.png");
        if (!image.exists()) {
            FileOutputStream os = null;
            InputStream is = null;
            try {
                is = getResources().getAssets().open("image.png");
                os = new FileOutputStream(image);
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
}
