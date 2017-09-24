package com.example.iskren.a03syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProvider;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SampleSyncAdapter";

    // The authority for the sync adapter's content provider
    // Make sure it matches AndroidManifest <provider android:authorities="XXX">
    public static final String AUTHORITY = "com.example.iskren.a03syncadapter.provider";
    // An account type, in the form of a domain name
    // Make sure it matches @xml/authenticator android:accountType="XXX"
    public static final String ACCOUNT_TYPE = "iskren.example.com";
    // The account name
    public static final String ACCOUNT_NAME = "noname";

    private static boolean accountSetupFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ensure account is created
        getSyncAccount(this);
    }

    @Nullable
    public static Account getSyncAccount(Context context) {
        accountSetupFailed = false;

        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        /**
         * Check if the account is already present
         */
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        Account account = null;
        if (accounts.length == 0) {
            Log.i(TAG, "got NO accounts from AM");
            // Create the account type and default account
            Account newAccount = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);

            if (accountManager.addAccountExplicitly(newAccount, null, null)) {
                Log.i(TAG, "successfully added new dummy account");
                account = newAccount;
            } else {
                Log.e(TAG, "failed to add account");
            }
        } else if (accounts.length == 1) {
            Log.i(TAG, "found one account from AM");
            if (accounts[0].name.equals(ACCOUNT_NAME)) {
                account = accounts[0];
            } else {
                Log.e(TAG, "account with weird name present " + accounts[0].name);
            }
        } else {
            Log.e(TAG, "Found multiple accounts " + accounts.length);
        }

        if (account != null) {
            return account;
        }

        accountSetupFailed = true;
        return null;
    }
}
