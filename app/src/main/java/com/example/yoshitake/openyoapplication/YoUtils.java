package com.example.yoshitake.openyoapplication;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.security.auth.callback.Callback;

/**
 * Created by Yoshitake on 2014/09/25.
 */
public class YoUtils {
    //TODO:各動作後のToastをちゃんとする→現状一応成否のみ。
    private String endPointUrl;
    private String username;
    private  String api_token;
    private GoogleCloudMessaging gcm;
    private String  projectNumber;
    public int numberOfFriends;
    private ArrayList<String> friendsList;

    public void setEndPointUrl(String endPointUrl){
        this.endPointUrl = endPointUrl;
    }
    public void setUsername(String userName){
        this.username = userName;
    }
    public void setApi_token(String api_token){
        Log.d("set!",api_token);
        this.api_token = api_token;
    }
    public void setProjectNumber(String projectNumber){
        this.projectNumber = projectNumber;
    }
    public String getUserNmae(){return this.username;}
    public String getApi_token(){return this.api_token;}
    public String getEndPointUrl(){return this.endPointUrl;}

    @Override
    public String toString(){
        return this.username;
    }

    public List<String> getFriendsList(){
        return friendsList;
    }

    public void sendYo(final String to ,RequestQueue mQueue,final Context context){
        if(this.api_token==null||this.endPointUrl==null){
            Toast.makeText(context,"エンドポイントが指定されていない、またはアカウントが作成されていません。",Toast.LENGTH_LONG).show();
            return;
        }
        //api_verに対応する必要はあるだろうか。
        String parameters = "?api_ver=0.1&api_token="+api_token+"&username=";
        String requestUrl = endPointUrl+"/yo/" + parameters + to;
        mQueue.add(new JsonObjectRequest(Request.Method.POST, requestUrl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String responseCode = "";
                        try{
                            responseCode = response.getString("code");
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        if(responseCode.equals("200")) {
                            Toast.makeText(context, "Send Yo to " + to + "!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(context,"Yoの送信に失敗しました",Toast.LENGTH_LONG).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();

    }

    public  void sendYoAll(RequestQueue mQueue,final Context context){
        if(this.api_token==null||this.endPointUrl==null){
            Toast.makeText(context,"エンドポイントが指定されていない、またはアカウントが作成されていません。",Toast.LENGTH_LONG).show();
            return;
        }
        String parameters = "?api_ver=0.1&api_token="+api_token;
        String requestUrl = endPointUrl + "/yoall/"+parameters;
        mQueue.add(new JsonObjectRequest(Request.Method.POST, requestUrl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String responseCode="";
                        try {
                            responseCode = response.getString("code");
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        if(responseCode.equals("200")){
                            Toast.makeText(context, "send Yo all!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(context,"Yoに失敗しました",Toast.LENGTH_LONG).show();
                        }
                        //Log.d("response:",response);
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();
    }
//現状未使用(ListFriendsのLength取ればいいと考えているため。)
    public void countTotalFriends(RequestQueue mQueue,final Context context){
        final YoUtils yoClient = this;
        if(this.api_token==null||this.endPointUrl==null){
            Toast.makeText(context,"エンドポイントが指定されていない、またはアカウントが作成されていません。",Toast.LENGTH_LONG).show();
            return;
        }
        String parameters = "?api_ver=0.1&api_token="+api_token;
        String requestUrl = endPointUrl +"/friends_count/"+ parameters;
        mQueue.add(new JsonObjectRequest(Request.Method.GET, requestUrl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        try {
                             yoClient.numberOfFriends = Integer.parseInt(response.getString("result"));
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();
    }

    public void listFriends(RequestQueue mQueue,final Context context){
        if(this.api_token==null||this.endPointUrl==null){
            Toast.makeText(context,"エンドポイントが指定されていない、またはアカウントが作成されていません。",Toast.LENGTH_LONG).show();
            return;
        }
        String parameters = "?api_ver=0.1&api_token="+api_token;
        String requestUrl = endPointUrl+"/list_friends/" + parameters;
        mQueue.add(new JsonObjectRequest(Request.Method.GET, requestUrl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String responseCode = "";
                        try {
                            JSONArray friends = response.getJSONArray("result");
                            responseCode = response.getString("code");
                            if(responseCode.equals("200")) {
                                for (int i = 0; i < friends.length(); i++) {
                                    friendsList.add(friends.getString(i));
                                }
                            }
                            else{
                                Toast.makeText(context,"Yoリストの取得に失敗しました",Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();
    }

    public void createUser(final String username, final String password, final RequestQueue mQueue,final Context context){

        String parameters = "?api_ver=0.1";
        final String requestUrl = this.endPointUrl + "/config/create_user/"+ parameters + "&username="+username+"&password="+password;
        mQueue.add(new JsonObjectRequest(Request.Method.POST, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = "";
                        String responseCode = "";
                        try {
                            result = response.getString("result");
                            responseCode = response.getString("code");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(responseCode.equals("200")) {
                            Toast.makeText(context, "Created User Account. if you want to save this account, push save Button", Toast.LENGTH_LONG).show();
                            YoUtils.this.setApi_token(result);
                            YoUtils.this.setUsername(username);
                            YoUtils.this.registerID(password, mQueue, context);
                        }
                        else{
                            Toast.makeText(context,"アカウントの取得に失敗しました。",Toast.LENGTH_SHORT).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();
    }

    private void registerID(final String password,final RequestQueue mQueue,final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    String regid = gcm.register(projectNumber);
                    registerRegId(password, regid, mQueue);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                Log.d("meg", msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }

    private void registerRegId(String password, final String regId,RequestQueue mQueue){
        String requestUrl = endPointUrl + "/config/add_gcm_id/"+"?api_ver=0.1"+"&username="+username+"&password="+password+"&proj_num="+
            projectNumber + "&reg_id="+regId;
        mQueue.add(new StringRequest(Request.Method.POST, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response",response);
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();
    }

}
