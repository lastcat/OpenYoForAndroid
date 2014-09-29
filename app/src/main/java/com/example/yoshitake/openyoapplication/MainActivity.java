package com.example.yoshitake.openyoapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

//TODO:リストから一つのエンドポイントを選ぶと、アカウント切り替えが右上のnavbarでできるようにする。エンドポイントも右上でとりえあえず設定
public class MainActivity extends Activity {
    private RequestQueue mQueue;
    private GoogleCloudMessaging gcm;
    private Context context;
    String url;
    String api_token;
    YoUtils yoClient;

    @InjectView(R.id.toUsername)
    EditText toUsername;
    @InjectView(R.id.username)
    EditText userName;
    @InjectView(R.id.password)
    EditText password;

    @OnClick(R.id.sendYoButton)
    void sendYo(){
        yoClient.sendYo(toUsername.getText().toString(),mQueue,context);
    }

    @OnClick(R.id.sendAllButton)
    void sendAll(){
        yoClient.sendYoAll(mQueue,context);
    }

    @OnClick(R.id.countTotalFriendsButton)
    void countTotalFriends(){
        yoClient.countTotalFriends(mQueue,context);
    }

    @OnClick(R.id.listFriendsButton)
    void listFriends(){
        yoClient.listFriends(mQueue,context);
    }

    @OnClick(R.id.createUserButton)
    void createUser(){
        //CreateUserはAPIを叩いて、userをcreateするとともに、yoClientをその新規作成したアカウントにする。
        yoClient.setProjectNumber(this.getString(R.string.project_number));
        yoClient.createUser(userName.getText().toString(),password.getText().toString(),mQueue,context);
        //TODO:DB登録関数 この時点のyoClientからすべての情報は取れる。
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        context = getApplication();
        //url = "https://OpenYo.nna774.net";
        yoClient = new YoUtils();
        mQueue = Volley.newRequestQueue(this);


        //実際には選択したItemの情報で初期化する
        //これはcreateアカウントしか使わない
        //そもそもContext渡してるならこれいらないのでは
        //yoClient.setGcm(GoogleCloudMessaging.getInstance(this));
        //これだけが今は必要か。
        yoClient.setEndPointUrl("https://OpenYo.nna774.net");
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

//このアプリケーションではエンドポイント、アカウント情報をDBで管理する。
    static final String DB = "account.db";
    static final int DB_VERSION = 1;
    static final String CREATE_TABLE = "create table account_table ( _id integer primary key autoincrement not null, " +
            "endpoint text not null," +
            "username text not null," +
            "api_token text UNIQUE not null );"+
            "create table endpoint_table ( _id integer primary key autoincrement not null," +
            "endpoint text  UNIQUE not null);";

    static final String DROP_TABLE = "drop table mytable;";

    private static class AccountDBHelper extends SQLiteOpenHelper {
        public AccountDBHelper(Context context) {
            super(context, DB, null, DB_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
        public void insertNewEndpointt(SQLiteDatabase db,String endpointUrl){
            ContentValues val = new ContentValues();
            val.put("endpoint",endpointUrl);
            try {
                db.insert("endpoint_table", null, val);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        public void insertNewAccount(SQLiteDatabase db,String endPointUrl,String username,String api_token){
            ContentValues val = new ContentValues();
            val.put("endpoint",endPointUrl);
            val.put("username",username);
            val.put("api_token",api_token);
            try {
                db.insert("account_table",null,val);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        //endpointの一覧を返す。
        //続き書かないと。
        public void selectAllEndpoint(SQLiteDatabase db){
            Cursor cursor = null;
            //これでいいの
            cursor = db.query("endpoint_table",null,null,null,null,null,null);
            String endpointList = "";

        }
        //endpointを指定するとusernameとapi_tokenの組を返す関数が必要?（見えるのはusernameだけでよい）
        //リスト作るのにつかう。


    }
}
