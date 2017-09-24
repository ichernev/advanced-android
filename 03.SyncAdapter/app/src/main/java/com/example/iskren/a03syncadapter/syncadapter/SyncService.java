package com.example.iskren.a03syncadapter.syncadapter;

import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by iskren on 2017-09-24.
 */

public class SyncService extends Service {

    private static final String TAG = "SampleSyncAdapter";

    private SyncAdapter syncAdapter;
    private static final Object syncAdapterLock = new Object();

    @Override
    public void onCreate() {
        Log.i(TAG, "SyncService onCreate");
        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "SyncService onBind");
        return syncAdapter.getSyncAdapterBinder();
    }
}
