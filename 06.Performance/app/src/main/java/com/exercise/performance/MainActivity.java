package com.exercise.performance;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Trace;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    TextView tvError;
    SlowTextView tvSlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvError = (TextView) findViewById(R.id.error);

        tvSlow = (SlowTextView) findViewById(R.id.error);

        init();

    }

    private void init() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(new String[]{"row1", "row2", "row3"});
        mRecyclerView.setAdapter(mAdapter);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @UiThread
    public void veryLongPause(View view) {
//        Trace.beginSection("Java perf");


        String s = "";
        StringBuilder b = new StringBuilder(1000);
        for (int i = 0; i < 10000; i++) {
            s = s + "i" + i;
//            b.append("i").append(i);
        }

        tvError.setText("Java");

//        Trace.endSection();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void dumpRandomNumbers() {
        Trace.beginSection("Data Structures");

        // First we need a sorted list of the numbers to iterate through.
        Integer[] sortedNumbers = SampleData.coolestRandomNumbers.clone();
        Arrays.sort(sortedNumbers);

        for (int i = 0; i < sortedNumbers.length; i++) {
            Integer currentNumber = sortedNumbers[i];
            for (int j = 0; j < SampleData.coolestRandomNumbers.length; j++) {
                if (currentNumber.compareTo(SampleData.coolestRandomNumbers[j]) == 0) {
                    Log.i("Dump", currentNumber + ": #" + j);
                }
            }
        }
        Trace.endSection();
    }

    public static class SampleData {
        public static Integer[] coolestRandomNumbers = new Integer[3000];
        static {
            Random randomGenerator = new Random();
            for (int i=0; i<3000; i++) {
                coolestRandomNumbers[i] = randomGenerator.nextInt();
            }
        }
    }
}
