package com.example.iskren.a02ndk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.txt_view)).setText(stringFromJNI2());
    }

    public native String  stringFromJNI2();

    static {
        System.loadLibrary("hello-jni");
    }
}
