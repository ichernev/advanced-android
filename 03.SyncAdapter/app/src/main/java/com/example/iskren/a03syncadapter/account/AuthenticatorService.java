package com.example.iskren.a03syncadapter.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by iskren on 2017-09-24.
 */

public class AuthenticatorService extends Service {

    private Authenticator instance;

    @Override
    public void onCreate() {
        instance = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return instance.getIBinder();
    }
}
