package com.example.yafun.thinkingapplication;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.AsyncTask;
import android.content.SharedPreferences;

import org.json.*;

import java.util.Iterator;


public class LoginActivity extends AppCompatActivity {

    // declare variable
    private EditText edtId, edtPwd;
    private Button btnSignIn;
    private ConnServer conn;
    private boolean permission = false;
    SharedPreferences member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set variable value
        edtId = (EditText) findViewById((R.id.edtId));
        edtPwd = (EditText) findViewById((R.id.edtPwd));
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

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
            permission = conn.checkLogin();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if (permission) {
                Log.d("login result", "successful");

                member = getSharedPreferences("member", MODE_PRIVATE);
                //member.edit().remove().commit();

                String content = conn.getMemberInf();               //  Get member information , type:String

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
    }
}
