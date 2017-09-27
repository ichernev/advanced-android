package com.example.iskren.a07threading;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by iskren on drugs.
 */

public class MainService extends Service {

    private static final String TAG = "SampleThread";

    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startId) {
        Log.i(TAG, String.format("%s : process %d thread %s",
                getClass().getName(), android.os.Process.myPid(), android.os.Process.myTid()));

        /*
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                (new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        Log.i(TAG, String.format("%s worker: process %d thread %s",
                                getClass().getName(), android.os.Process.myPid(), android.os.Process.myTid()));
                        try {
                            Thread.sleep(1 * Constants.SECOND);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Log.i(TAG, String.format("%s worker ready: process %d thread %s",
                                getClass().getName(), android.os.Process.myPid(), android.os.Process.myTid()));

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        Log.i(TAG, String.format("%s main ready: process %d thread %s",
                                getClass().getName(), android.os.Process.myPid(), android.os.Process.myTid()));
                    }

                }).execute();
            }

        }, 2 * Constants.SECONDS);
        */

        return START_NOT_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
