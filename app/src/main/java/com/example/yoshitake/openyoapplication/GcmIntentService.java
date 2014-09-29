package com.example.yoshitake.openyoapplication;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.os.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Yoshitake on 2014/09/23.
 */
public class GcmIntentService extends IntentService {
    private Handler mHandler = new Handler();
    private static final String TAG = "GcmIntentService";
    public GcmIntentService() {
        super("GcmIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            Log.d("OUOUO","KITAKITAKITA");
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.d(TAG,"messageType: " + messageType + ",body:" + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.d(TAG,"messageType: " + messageType + ",body:" + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.d(TAG, "messageType: " + messageType + ",body:" + extras.toString());
                mHandler.post(new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Toast Message from IntentService", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
