package com.example.yoshitake.openyoapplication;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yoshitake on 2014/09/25.
 */
public class YoUtils {
    public static void sendYo(String endpointUrl, String api_token,String to,RequestQueue mQueue,final Context context){
        String parameters = "?api_ver=0.1&api_token="+api_token+"&username=";
        String requestUrl = endpointUrl+"/yo/" + parameters + to;
        mQueue = Volley.newRequestQueue(context);
        mQueue.add(new StringRequest(Request.Method.POST, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // JSONObjectのパース、List、Viewへの追加等
                        Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();
    }

    static void sendYoAll(String endpointUrl, String api_token,RequestQueue mQueue,final Context context){
        //TODO:url(エンドポイント)はユーザー指定にする。
        String parameters = "?api_ver=0.1&api_token="+api_token;
        String requestUrl = endpointUrl + "/yoall/"+parameters;
        mQueue = Volley.newRequestQueue(context);
        mQueue.add(new StringRequest(Request.Method.POST, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // JSONObjectのパース、List、Viewへの追加等
                        Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();
    }

    static void countTotalFriends(String endpointUrl, String api_token,RequestQueue mQueue,final Context context){
        //TODO:url(エンドポイント)はユーザー指定にする。
        String parameters = "?api_ver=0.1&api_token="+api_token;
        String requestUrl = endpointUrl +"/friends_count/"+ parameters;
        mQueue = Volley.newRequestQueue(context);
        mQueue.add(new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // JSONObjectのパース、List、Viewへの追加等
                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();
    }

    static void listFriends(String endpointUrl, String api_token,RequestQueue mQueue,final Context context){
        //TODO:url(エンドポイント)はユーザー指定にする。
        String parameters = "?api_ver=0.1&api_token="+api_token;
        String requestUrl = endpointUrl+"/list_friends/" + parameters;
        mQueue = Volley.newRequestQueue(context);
        mQueue.add(new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // JSONObjectのパース、List、Viewへの追加等
                        Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();
    }

    void forPushNotification(){
        /*String url = "https://android.googleapis.com/gcm/send";
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
    */}
}
