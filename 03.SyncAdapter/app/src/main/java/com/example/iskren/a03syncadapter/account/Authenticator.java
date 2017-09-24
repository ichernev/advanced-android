package com.example.iskren.a03syncadapter.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by iskren on 2017-09-24.
 */

public class Authenticator extends AbstractAccountAuthenticator {
    private static final String TAG = "SampleSyncAdapter";

    public Authenticator(Context ctx) {
        super(ctx);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse respnse,
                                 String accountType) {
        Log.i(TAG, "editProperties called");
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType,
                             String authTokenType,
                             String[] requiredFeatures,
                             Bundle options) throws NetworkErrorException {
        Log.i(TAG, "addAccount called");
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     Account account,
                                     Bundle accountCredentials) throws NetworkErrorException {
        Log.i(TAG, "confirmCredentials called");
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
                               Account account,
                               String authTokenType,
                               Bundle options) throws NetworkErrorException {
        Log.i(TAG, "getAuthToken called");
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        Log.i(TAG, "getAuthTokenLabel called");
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                                    Account account,
                                    String authTokenType,
                                    Bundle options) throws NetworkErrorException {
        Log.i(TAG, "updateCredentials called");
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account,
                              String[] features) throws NetworkErrorException {
        Log.i(TAG, "hasFeatures called");
        throw new UnsupportedOperationException();
    }
}
