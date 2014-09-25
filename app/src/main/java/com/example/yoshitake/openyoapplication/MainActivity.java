package com.example.yoshitake.openyoapplication;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {
    private RequestQueue mQueue;
    private GoogleCloudMessaging gcm;
    private Context context;
    String regId;
    String url;
    String api_token;

    @InjectView(R.id.toUsername)
    EditText toUsername;
    @InjectView(R.id.username)
    EditText userName;
    @InjectView(R.id.password)
    EditText password;

    @OnClick(R.id.sendYoButton)
    void sendYo(){
        YoUtils.sendYo(url,api_token,toUsername.getText().toString(),mQueue,context);
    }

    @OnClick(R.id.sendAllButton)
    void sendAll(){
        YoUtils.sendYoAll(url,api_token,mQueue,context);
    }

    @OnClick(R.id.countTotalFriendsButton)
    void countTotalFriends(){
            YoUtils.countTotalFriends(url,api_token,mQueue,context);
    }

    @OnClick(R.id.listFriendsButton)
    void listFriends(){
        YoUtils.listFriends(url,api_token,mQueue,context);
    }

    @OnClick(R.id.createUserButton)
    void createUser(){
        YoUtils.createUser(url,api_token,mQueue,context,userName.getText().toString(),password.getText().toString());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        context = getApplication();
        url = "https://OpenYo.nna774.net";
        api_token = getString(R.string.yo_api_token);
        mQueue = Volley.newRequestQueue(this);
        gcm = GoogleCloudMessaging.getInstance(this);
        //YoUtils.registerInBackground();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    String regid = gcm.register(getString(R.string.project_number));
                    msg = "Device registered, registration ID=" + regid;
                    regId = regid;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                Log.d("meg",msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }*/
}
