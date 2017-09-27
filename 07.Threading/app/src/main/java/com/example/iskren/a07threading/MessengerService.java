package com.example.iskren.a07threading;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * Created by iskren on drugs.
 */

public class MessengerService extends Service {
    private static final String TAG = "SampleThreading";

    /** Command to the service to display a message */
    static final int MSG_SAY_HELLO = 1;
    static final String KEY_NAME = "name";
    static final String KEY_MY_PAR = "my_par";

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Bundle b = msg.getData();
                    // Class cls = MyParcelable.class;
                    b.setClassLoader(MyParcelable.class.getClassLoader());
                    MyParcelable mp = b.getBundle("b").getParcelable("a");
                    Log.i(TAG, "Hi, my name is " + msg.getData().getString(KEY_NAME));
                    Log.i(TAG, String.format("Str: %s Int: %d", mp.str, mp.i));
                    // Toast.makeText(getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return mMessenger.getBinder();
    }
}