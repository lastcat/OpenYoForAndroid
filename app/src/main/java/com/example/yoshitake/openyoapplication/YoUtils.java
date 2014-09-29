package com.example.yoshitake.openyoapplication;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yoshitake on 2014/09/25.
 */
public class YoUtils {
    //TODO:各動作後のToastをちゃんとする（ステータスコードとかでエラーメッセージわける）
    //TODO:CountTotalFrineds,listFriendsをどうするか考える。
    private String endPointUrl;
    private String username;
    private  String api_token;
    private GoogleCloudMessaging gcm;
    private String  projectNumber;

    public void setEndPointUrl(String endPointUrl){
        this.endPointUrl = endPointUrl;
    }
    public void setUsername(String userName){
        this.username = userName;
    }
    public void setApi_token(String api_token){
        this.api_token = api_token;
    }
    public void setProjectNumber(String projectNumber){
        this.projectNumber = projectNumber;
    }

    public void sendYo(String to ,RequestQueue mQueue,final Context context){
        if(this.api_token==null||this.endPointUrl==null){
            Toast.makeText(context,"エンドポイントが指定されていない、またはアカウントが作成されていません。",Toast.LENGTH_LONG).show();
            return;
        }
        //api_verに対応する必要はあるだろうか。
        String parameters = "?api_ver=0.1&api_token="+api_token+"&username=";
        String requestUrl = endPointUrl+"/yo/" + parameters + to;
        mQueue.add(new StringRequest(Request.Method.POST, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //すべてのresponseをjson対応にしないといけない
                        Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                        Log.d("username", username);
                        Log.d("api_token",api_token);
                        Log.d("response:",response);
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
        mQueue.add(new StringRequest(Request.Method.POST, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                        Log.d("response:",response);
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }));
        mQueue.start();
    }

    public void countTotalFriends(RequestQueue mQueue,final Context context){
        if(this.api_token==null||this.endPointUrl==null){
            Toast.makeText(context,"エンドポイントが指定されていない、またはアカウントが作成されていません。",Toast.LENGTH_LONG).show();
            return;
        }
        String parameters = "?api_ver=0.1&api_token="+api_token;
        String requestUrl = endPointUrl +"/friends_count/"+ parameters;
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

    public void listFriends(RequestQueue mQueue,final Context context){
        if(this.api_token==null||this.endPointUrl==null){
            Toast.makeText(context,"エンドポイントが指定されていない、またはアカウントが作成されていません。",Toast.LENGTH_LONG).show();
            return;
        }
        String parameters = "?api_ver=0.1&api_token="+api_token;
        String requestUrl = endPointUrl+"/list_friends/" + parameters;
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

    public void createUser(final String username, final String password, final RequestQueue mQueue,final Context context){
        final YoUtils yoClient = this;
        String parameters = "?api_ver=0.1";
        final String requestUrl = this.endPointUrl + "/config/create_user/"+ parameters + "&username="+username+"&password="+password;
        mQueue.add(new JsonObjectRequest(Request.Method.POST, requestUrl,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = "";
                        try {
                            result = response.getString("result");
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.d("api_token!",result + "end");
                        yoClient.setApi_token(result);
                        yoClient.setUsername(username);
                        yoClient.registerID(password,mQueue,context);
                    }
                },

                new Response.ErrorListener() {
                    @Override public void onErrorResponse(VolleyError error) {
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
                        //gcm、ここでしか使わないからインスタンスが持ってるのやっぱり微妙な気がするなぁ。ここで生成してそれでいいのでは。
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    String regid = gcm.register(projectNumber);
                    registerRegId(password, regid, mQueue);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                //log.dの何かがおかしい。
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
    //DB設計……エンドポイント,username,apitokenのテーブルでいいと考える。
    //エンドポイント選択時には重複を除いたエンドポイントリストを出力し、そのあと選択されたエンドポイントで検索かけてヒットしたユーザーを選択させる

    //残り重複なしのエンドポイントのリストを返す関数と、選択されたエンドポイントに対応するusernameのリストを返す関数が必要
    //それが書けたら右上のセッティングボタンと紐づける
    //現在どのエンドポイントの何のアカウントなのかは常に表示しておくと良さそう

}
