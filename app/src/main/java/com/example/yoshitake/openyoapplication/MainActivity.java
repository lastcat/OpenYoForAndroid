package com.example.yoshitake.openyoapplication;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

    @InjectView(R.id.textView)
    TextView tv;



    @OnClick(R.id.button)
    void sendYo(){
        String url = "http://OpenYo.nna774.net/yo/";
        String api_token = getString(R.string.yo_api_token);
        String parameters = "?api_ver=0.1&api_token="+api_token+"&username=nona7";
        //String createUser = "http://OpenYo.nna774.net/create_user/?api_ver=0.1&username=testcat";
        String requestUrl = url + parameters;
        mQueue = Volley.newRequestQueue(this);
        mQueue.add(new StringRequest(Request.Method.POST, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // JSONObjectのパース、List、Viewへの追加等
                        tv.setText(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();


    }

    @OnClick(R.id.button2)
    void sendPush(){
        String url = "https://android.googleapis.com/gcm/send";
        final String api_key = getString(R.string.api_key);

        JSONObject sendData = new JSONObject();
        try {
            sendData.put("registration_ids", regId);
            sendData.put("dara","hoge");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        JsonObjectRequest json = new JsonObjectRequest(Request.Method.POST,url,sendData,
                new Response.Listener<JSONObject>(){
                    public void onResponse(JSONObject result) {
                        //返答時コールバック処理
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                // Add BASIC AUTH HEADER
                Map<String, String> newHeaders = new HashMap<String, String>();
                newHeaders.putAll(headers);
                newHeaders.put("Authorization", "key = " + api_key);
                return newHeaders;
            };
        };
        mQueue = Volley.newRequestQueue(this);

        mQueue.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        context = getApplication();
        gcm = GoogleCloudMessaging.getInstance(this);
        registerInBackground();
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

    private void registerInBackground() {
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
    }
}
