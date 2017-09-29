/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.iskren.a02ndk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Retrieve our TextView and set its content.
         * the text is retrieved by calling a native
         * function.
         */

        setContentView(R.layout.activity_main);
        TextView tv = (TextView)findViewById(R.id.textview);
        tv.setText( stringFromJNI() + "\n" + runWithArgs(true, 5, Math.PI, "Iskren", new Object() {
            @Override
            public String toString() {
                //throw new RuntimeException("Catch me");
                return "Foo";
            }
        }));

        byte[] b = new byte[] {1, 2, 3};
        byte[] b2 = reversedByteArray(b);
        for (int i = 0; i < 3; ++i) {
            asrt(b[i] == i + 1, "b" + i);
            asrt(b2[i] == 3 - i, "b2" + i);
        }
        Log.i("JNI", "all is gooood");
    }
    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */

    private void asrt(boolean b, String s) {
        if (!b) {
            Log.e("JNI", "WTF " + s);
        }
    }

    public native String  stringFromJNI();

    public native String  runWithArgs(boolean b, int i, double d, String s, Object o);

    public native byte[] reversedByteArray(byte[] inp);

    /* This is another native method declaration that is *not*
     * implemented by 'hello-jni'. This is simply to show that
     * you can declare as many native methods in your Java code
     * as you want, their implementation is searched in the
     * currently loaded native libraries only the first time
     * you call them.
     *
     * Trying to call this function will result in a
     * java.lang.UnsatisfiedLinkError exception !
     */
    public native String  unimplementedStringFromJNI();

    /* this is used to load the 'hello-jni' library on application
     * startup. The library has already been unpacked into
     * /data/data/com.example.hellojni/lib/libhello-jni.so at
     * installation time by the package manager.
     */
    static {
        System.loadLibrary("hello-jni");
    }
}
