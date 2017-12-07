package com.exercise.security;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * @author Philip
 * @since 2017-12-07
 */

public abstract class SimpleTask extends AsyncTask<Void, Void, String> {

    WeakReference<TextView> tvRef;
    WeakReference<ProgressBar> loaderRef;

    public SimpleTask(TextView v, ProgressBar loader) {
        tvRef = new WeakReference<TextView>(v);
        loaderRef = new WeakReference<ProgressBar>(loader);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        TextView tv = tvRef.get();
        if (tv != null) {
            tv.setText("");
        }

        ProgressBar pb = loaderRef.get();
        if (pb != null) {
            pb.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        TextView tv = tvRef.get();
        if (tv != null) {
            tv.setText(s);
        }

        ProgressBar pb = loaderRef.get();
        if (pb != null) {
            pb.setVisibility(View.GONE);
        }
    }

}
