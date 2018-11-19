package com.example.yafun.thinkingapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;
import android.content.SharedPreferences;

import org.json.*;
import java.util.Map;
import java.util.Iterator;


public class LoginActivity extends AppCompatActivity {

    // declare variable
    private EditText edtId, edtPwd;
    private Button btnSignIn;
    private Button btnSignUp;
    private ConnServer conn,connRecord;
    private boolean permission = false;
    SharedPreferences member,imagetest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set variable value
        edtId = (EditText) findViewById((R.id.edtId));
        edtPwd = (EditText) findViewById((R.id.edtPwd));
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);


        SharedPreferences tmp;
        tmp = getSharedPreferences("member", MODE_PRIVATE);
        SharedPreferences.Editor editor = tmp.edit();
        editor.clear().commit();
        tmp = getSharedPreferences("drawing_record", MODE_PRIVATE);
        tmp.edit().clear().commit();
        tmp = getSharedPreferences("drawingmult_record", MODE_PRIVATE);
        tmp.edit().clear().commit();


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get text from id and passord field
                final String account = edtId.getText().toString();
                final String password = edtPwd.getText().toString();

                // Initialize  AsyncLogin() class with id and password
                new AsyncLogin().execute(account, password);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toreg = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(toreg);
            }
        });
    }

    /**
     * AsyncLogin Class
     */
    private class AsyncLogin extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            Log.d("Start AsyncLogin", params[0] + "," + params[1]);

            // Connect to database && check login information
            conn = new ConnServer(params[0], params[1]);
            // 設定 permission;確認登入
            permission = conn.checkLogin();

            //1003 load data
            /*connRecord = new ConnServer();//get Record
            String[] tmparr = connRecord.PersonalRecord("drawing",params[0]);
            imagetest = getSharedPreferences("drawing_record", MODE_PRIVATE);

            for(int i = 0; i < tmparr.length; i++) {
                try{
                    JSONArray data = new JSONArray(tmparr[i]);
                    //Log.d("JSONArray",data.get(1).toString());//data

                    Log.d("JSONArray",data.get(0).toString());
                    imagetest.edit()
                            .putString(data.get(0).toString(), data.get(1).toString())
                            .commit();

                }catch (org.json.JSONException e){
                    Log.d("Err","");
                }
            }*/



            writeShardPreferences("drawing",params[0]);
            writeShardPreferences("drawingmult",params[0]);

            /*
            Map<String,?> keys = imagetest.getAll();

            for(Map.Entry<String,?> entry : keys.entrySet()){
                Log.d("map values",entry.getKey() + ": " +
                        entry.getValue().toString());
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (permission) {
                Log.d("login result", "successful");

                member = getSharedPreferences("member", MODE_PRIVATE);
                //member.edit().remove().commit();

                String content = conn.getMemberInf();               //  Get member information , type:String
                Log.d("getMemberInf",content);
                try {
                    JSONObject jsonObj = new JSONObject(content);   // JSONObject
                    Iterator<String> keys = jsonObj.keys();         // Iterator

                    while (keys.hasNext()) {
                        //Log.d(key,jsonObj.getString(key));
                        String key = (String) keys.next();
                        member.edit()
                                .putString(key, jsonObj.getString(key))
                                .commit();
                    }
                } catch (org.json.JSONException e) {
                    Log.d("JSONException", e.getLocalizedMessage());
                } finally {
                    Log.d("test",
                            getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                    );
                }


                // toast message
                Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();

                // get intent to put result value
                Intent intent = LoginActivity.this.getIntent();
                intent.putExtra("username", edtId.getText().toString());

                // return result to original activity
                LoginActivity.this.setResult(RESULT_OK, intent);
                LoginActivity.this.finish();
            }
            // else show login fail
            else {
                Log.d("login result", "fail");
                new AlertDialog.Builder(LoginActivity.this)
                        .setMessage("登入失敗")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        private void writeShardPreferences(String database,String account){
            connRecord = new ConnServer();//get Record
            String[] tmparr = connRecord.PersonalRecord(database,account);
            imagetest = getSharedPreferences(database+"_record", MODE_PRIVATE);
            Log.d(database+"_record","success");//data
            if(tmparr!=null) {
                for (int i = 0; i < tmparr.length; i++) {
                    try {
                        JSONArray data = new JSONArray(tmparr[i]);
                        //Log.d("JSONArray",data.get(1).toString());//data

                        Log.d("JSONArray", data.get(0).toString());
                        imagetest.edit()
                                .putString(data.get(0).toString(), data.get(1).toString())
                                .commit();

                    } catch (org.json.JSONException e) {
                        Log.d("Err", "");
                    }
                }
            }
        }
    }
}
