package com.example.iskren.a03syncadapter.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by iskren on 2017-09-24.
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SampleSyncAdapter";

    private ContentResolver resolver;

    public SyncAdapter(Context ctx, boolean autoInitialize) {
        super(ctx, autoInitialize);
        Log.i(TAG, "created SyncAdapter OLD");
        resolver = ctx.getContentResolver();
        Log.i(TAG, "got content resolver " + resolver);
    }

    public SyncAdapter(Context ctx, boolean autoInitialize, boolean allowParallelSyncs) {
        super(ctx, autoInitialize, allowParallelSyncs);
        Log.i(TAG, "created SyncAdapter NEW");
        resolver = ctx.getContentResolver();
        Log.i(TAG, "got content resolver " + resolver);
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {
        Log.i(TAG, "perform sync");
        Log.i(TAG, String.format("performing sync %s %s %s %s",
                account, extras, authority, syncResult));
    }

    @Override
    public void onSecurityException(Account account, Bundle extras, String authority, SyncResult syncResult) {
        Log.i(TAG, "onSecurityException");
        super.onSecurityException(account, extras, authority, syncResult);
    }

    @Override
    public void onSyncCanceled() {
        Log.i(TAG, "onSyncCanceled");
        super.onSyncCanceled();
    }

    @Override
    public void onSyncCanceled(Thread thread) {
        Log.i(TAG, "onSyncCanceled(thread)");
        super.onSyncCanceled(thread);
    }

}
