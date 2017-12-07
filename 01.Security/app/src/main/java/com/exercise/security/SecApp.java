package com.exercise.security;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * @author Philip
 * @since 2017-12-06
 */

public class SecApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ConnectionUtil.init(this);

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }
}
