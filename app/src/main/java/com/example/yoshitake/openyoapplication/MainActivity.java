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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

//TODO:起動時に自動的エンドポイント,アカウントがセットされてしまうのをなんとかしたい
//TODO:アカウントのCRUDアクションを整えるべき？
//TODO:そもそもUIがクソ
public class MainActivity extends Activity {
    private RequestQueue mQueue;
    private GoogleCloudMessaging gcm;
    private Context context;
    private SQLiteDatabase db;
    YoUtils yoClient;
    AccountDBHelper DBHelper;
    ArrayAdapter<String> endPointAdapter;
    YoAccountdapter accountAdapter;

    @InjectView(R.id.toUsername)
    EditText toUsername;
    @InjectView(R.id.username)
    EditText userName;
    @InjectView(R.id.password)
    EditText password;
    @InjectView(R.id.endpointEditText)
    EditText endpointEditText;
    @InjectView(R.id.endpointSpinner)
    Spinner endpointSpinner;
    @InjectView(R.id.accountSpinner)
    Spinner accountSpinner;

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
        yoClient.setProjectNumber(this.getString(R.string.project_number));
        yoClient.createUser(userName.getText().toString(),password.getText().toString(),mQueue,context);
    }
    @OnClick(R.id.saveButton)
    void saveAccount(){
        if(yoClient.getUserNmae()!=null && yoClient.getApi_token()!=null){
            DBHelper.insertNewAccount(db,yoClient);
            accountAdapter.add(yoClient);
            accountSpinner.setAdapter(accountAdapter);
        }
    }
    @OnClick(R.id.saveEndpointButton)
    void saveEndPoint(){
        endPointAdapter.clear();
        DBHelper.insertNewEndpointt(db,endpointEditText.getText().toString());
        ArrayList<String> endpointList = DBHelper.selectAllEndpoint(db);
        for(int i = 0;i < endpointList.size();i++){
            endPointAdapter.add(endpointList.get(i));
        }
        endpointSpinner.setAdapter(endPointAdapter);
        yoClient.setEndPointUrl(endpointEditText.getText().toString());

        ArrayList<YoUtils> AccountList = DBHelper.selectAccounts(db,yoClient.getEndPointUrl());
        accountAdapter = new YoAccountdapter(getApplicationContext(),R.layout.yo_account_item,AccountList);
        accountSpinner.setAdapter(accountAdapter);
        accountAdapter.setDropDownViewResource(R.layout.dropdown_item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        //このあたり綺麗にしないといけない
        context = getApplication();
        yoClient = new YoUtils();
        yoClient.setEndPointUrl("");
        mQueue = Volley.newRequestQueue(this);
        DBHelper = new AccountDBHelper(this);
        db = DBHelper.getWritableDatabase();
        endPointAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        ArrayList<String> endpointList = DBHelper.selectAllEndpoint(db);
        for(int i = 0;i < endpointList.size();i++){
            endPointAdapter.add(endpointList.get(i));
        }
        endpointSpinner.setAdapter(endPointAdapter);
        endPointAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endpointSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                Spinner spinner = (Spinner) parent;
                String item = (String) spinner.getSelectedItem();
                Toast.makeText(getApplicationContext(), "エンドポイントが"+item+"に設定されました", Toast.LENGTH_LONG).show();
                yoClient.setEndPointUrl(item);

                ArrayList<YoUtils> AccountList = DBHelper.selectAccounts(db,yoClient.getEndPointUrl());
                accountAdapter = new YoAccountdapter(getApplicationContext(),R.layout.yo_account_item,AccountList);
                accountSpinner.setAdapter(accountAdapter);
                accountAdapter.setDropDownViewResource(R.layout.dropdown_item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                YoUtils item =  (YoUtils)spinner.getSelectedItem();
                Toast.makeText(getApplicationContext(), item.getUserNmae(), Toast.LENGTH_LONG).show();
                yoClient = item;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//このアプリケーションではエンドポイント、アカウント情報をDBで管理する。
    static final String DB = "account.db";
    static final int DB_VERSION = 1;
    static final String CREATE_ACCOUNT_TABLE = "create table account_table ( _id integer primary key autoincrement not null, " +
            "endpoint text not null," +
            "username text not null," +
            "api_token text UNIQUE not null );";
    static final String CREATE_ENDPOINT_TABLE =
            "create table endpoint_table ( _id integer primary key autoincrement not null," +
            "endpoint text  UNIQUE not null);";

    static final String DROP_TABLE = "drop table mytable;";

    private static class AccountDBHelper extends SQLiteOpenHelper {
        public AccountDBHelper(Context context) {
            super(context, DB, null, DB_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_ACCOUNT_TABLE);
            db.execSQL(CREATE_ENDPOINT_TABLE);
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

        public void insertNewAccount(SQLiteDatabase db,YoUtils yoClient){
            if(yoClient.getEndPointUrl()==null||yoClient.getUserNmae()==null||yoClient.getApi_token()==null)
                return;
            ContentValues val = new ContentValues();
            val.put("endpoint",yoClient.getEndPointUrl());
            val.put("username",yoClient.getUserNmae());
            val.put("api_token",yoClient.getApi_token());
            try {
                db.insert("account_table",null,val);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        public ArrayList<String> selectAllEndpoint(SQLiteDatabase db){
            Cursor cursor = null;
            cursor = db.query("endpoint_table",null,null,null,null,null,null);
            ArrayList<String> endpointList = new ArrayList<String>();
            int index = cursor.getColumnIndex("endpoint");
            while(cursor.moveToNext()){
                String endpoint = cursor.getString(index);
                endpointList.add(endpoint);
            }

            return endpointList;
        }

        public ArrayList<YoUtils> selectAccounts(SQLiteDatabase db,String endpoint){
            Cursor cursor = null;
            String[] strArg = {endpoint};
            cursor = db.query("account_table",null,"endpoint=?",strArg,null,null,null);
            //cursor = db.query("account_table",null,null,null,null,null,null);
            ArrayList<YoUtils> accountList = new ArrayList<YoUtils>();
            int endPointIndex = cursor.getColumnIndex("endpoint");
            int nameIndex = cursor.getColumnIndex("username");
            int tokenIndex = cursor.getColumnIndex("api_token");

            while(cursor.moveToNext()){
                YoUtils yoAccount = new YoUtils();
                yoAccount.setEndPointUrl(cursor.getString(endPointIndex));
                yoAccount.setUsername(cursor.getString(nameIndex));
                yoAccount.setApi_token(cursor.getString(tokenIndex));
                accountList.add(yoAccount);
            }
            return  accountList;
        }
    }

    public class YoAccountdapter extends ArrayAdapter<YoUtils> {
        private LayoutInflater layoutInflater_;

        public YoAccountdapter(Context context, int textViewResourceId, List<YoUtils> objects) {
            super(context, textViewResourceId, objects);
            layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            YoUtils item = (YoUtils)getItem(position);
            if (null == convertView) {
                convertView = layoutInflater_.inflate(R.layout.yo_account_item, null);
            }
            TextView textView;
            textView = (TextView)convertView.findViewById(R.id.yoAccountName);
            textView.setText(item.getUserNmae());
            return convertView;
        }
    }
}
