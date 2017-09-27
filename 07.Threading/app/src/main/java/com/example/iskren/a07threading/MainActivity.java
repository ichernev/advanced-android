package com.example.iskren.a07threading;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SampleThread";

    public static final int ADD_MSG = 0;
    public static final int MUL_MSG = 1;

    LooperThread [] lt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, String.format("%s FIRST: process %d thread %s",
                getClass().getName(), android.os.Process.myPid(), android.os.Process.myTid()));

        Intent intent = new Intent(this, MainService.class);
        startService(intent);

        Handler h = new MyHandler();

        // h.sendMessage(h.obtainMessage(ADD_MSG, 2, 2));

        /*
        for (int i = 0; i < 5; ++i) {
            new MyAsyncTask(h).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, i);
        }*/

        lt = new LooperThread[3];
        for (int i = 0; i < 3; ++i) {
            lt[i] = new LooperThread(h);
            lt[i].start();
        }
        //lt[0].mHandler.post(new TellNext(0, 0));

        /*
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, MainService.class);
                Log.i(TAG, String.format("%s SECOND: process %d thread %s",
                        getClass().getName(), android.os.Process.myPid(), android.os.Process.myTid()));
                startService(intent);
            }
        }, 2 * Constants.SECOND);
        */

        /*
        final int x = 5;
        final VarHolder<Integer> y = new VarHolder<>(5);
        */

    }

    class TellNext implements Runnable {
        private int threadId;
        private int level;

        public TellNext(int threadId, int level) {

            this.threadId = threadId;
            this.level = level;
        }

        @Override
        public void run() {

            if (level == 5) {
                return;
            }
            Log.i(TAG, String.format("%s level %d: process %d thread %s",
                    "Looper" + threadId, level, android.os.Process.myPid(), android.os.Process.myTid()));

            int nxt = (threadId + 1) % 3;
            MainActivity.this.lt[nxt].mHandler.post(new TellNext(nxt, nxt == 0 ? level + 1 : level));
        }
    }

    class MyHandler extends Handler {
        int total = 0;
        @Override
        public void handleMessage(Message msg) {
            total += 1;
            if (total == 3) {
                MainActivity.this.lt[0].mHandler.post(new TellNext(0, 0));
            }
            /*
            switch (msg.what) {
                case ADD_MSG:
                    Log.i(TAG, String.format("%d + %d = %d", msg.arg1, msg.arg2, msg.arg1 + msg.arg2));
                    break;
                case MUL_MSG:
                    Log.i(TAG, String.format("%d * %d = %d", msg.arg1, msg.arg2, msg.arg1 * msg.arg2));
                    break;
                default:
                    Log.e(TAG, "unknown");
            }*/
        }
    }

    class MyAsyncTask extends AsyncTask<Integer, Void, Void> {

        private Handler h;
        private int id;

        public MyAsyncTask(Handler h) {
            this.h = h;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            id = params[0];
            h.sendMessage(h.obtainMessage(ADD_MSG, id, id));

            Log.i(TAG, String.format("%s worker[%d]: process %d thread %s",
                    getClass().getName(), id, android.os.Process.myPid(), android.os.Process.myTid()));
            try {
                Thread.sleep(1 * Constants.SECOND);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            h.sendMessage(h.obtainMessage(MUL_MSG, id, id));

            Log.i(TAG, String.format("%s worker[%d] ready: process %d thread %s",
                    getClass().getName(), id, android.os.Process.myPid(), android.os.Process.myTid()));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, String.format("%s back on main[%d] ready: process %d thread %s",
                    getClass().getName(), id, android.os.Process.myPid(), android.os.Process.myTid()));
        }
    }

    class LooperThread extends Thread {
        public Handler mHandler;
        private Handler notif;

        public LooperThread(Handler notif) {
            this.notif = notif;
        }

        public void run() {
            Looper.prepare();
            notif.sendMessage(notif.obtainMessage());
            mHandler = new Handler();

            Looper.loop();
        }
    }
}
