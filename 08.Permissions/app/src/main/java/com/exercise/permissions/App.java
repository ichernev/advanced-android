package com.exercise.permissions;

import android.app.Application;
import android.widget.Toast;

/**
 * @author Philip
 * @since 2017-12-04
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Foreground.init(this);

        Foreground.get().addListener(new Foreground.Listener() {
            @Override
            public void onBecameForeground() {
                super.onBecameForeground();
            }
        });


//        Foreground.get().addListener(new Foreground.Listener() {
//            @Override
//            public void onBecameForeground() {
//                super.onBecameForeground();
//                Toast.makeText(App.this, "onBecameForeground", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onBecameBackground() {
//                super.onBecameBackground();
//                Toast.makeText(App.this, "onBecameBackground", Toast.LENGTH_SHORT).show();
//            }
//
//
//        });
    }
}
