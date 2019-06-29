package com.example.yafun.thinkingapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout.LayoutParams;
import android.content.Context;
import android.net.ConnectivityManager;

import org.json.*;

import java.io.IOException;
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {
    // Declare Object Variable
    private TextView TextView2,TextView3;
    private EditText edtId, edtPwd;
    private Button btnSignIn;
    private Button btnSignUp;

    private ConnServer conn,connRecord;
    private boolean PERMISSION = false;     //  是否登入
    SharedPreferences member,imagetest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /// 全屏幕模式
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        new SettingLayout().execute();

        // set variable value
        edtId = (EditText) findViewById((R.id.edtId));
        edtPwd = (EditText) findViewById((R.id.edtPwd));
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);


        SharedPreferences userXML;
        userXML = getSharedPreferences("member", MODE_PRIVATE);
        SharedPreferences.Editor editor = userXML.edit();
        editor.clear().commit();
        userXML = getSharedPreferences("drawing_record", MODE_PRIVATE);
        userXML.edit().clear().commit();
        userXML = getSharedPreferences("drawingmult_record", MODE_PRIVATE);
        userXML.edit().clear().commit();
        userXML = getSharedPreferences("oneimagetest_record", MODE_PRIVATE);
        userXML.edit().clear().commit();


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get text from id and passord field
                final String account = edtId.getText().toString();
                final String password = edtPwd.getText().toString();

                if (edtId.getText().toString().equals("") || edtPwd.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "請輸入帳號及密碼", Toast.LENGTH_SHORT).show();
                }else {
                    if (isNetworkConnected()){
                        // Initialize  AsyncLogin() class with id and password
                        new AsyncLogin().execute(account, password);
                    }
                    else
                        Toast.makeText(LoginActivity.this, "請確認網路連線狀況", Toast.LENGTH_SHORT).show();
                }

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

    @Override
    public void onBackPressed () {
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    public static float getDPI(Context context){
        return ((float) context.getResources().getDisplayMetrics().densityDpi);
    }
    private class SettingLayout extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
        }
        @Override
        protected void onPreExecute() {
            final Point size = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(size);
            Log.d("此手機尺寸為", size.x+"X"+size.y);

            TextView2 = (TextView)findViewById(R.id.textView2);
            TextView3 = (TextView)findViewById(R.id.textView3);
            edtId = (EditText) findViewById((R.id.edtId));
            edtPwd = (EditText) findViewById((R.id.edtPwd));
            btnSignIn = (Button) findViewById(R.id.btnSignIn);
            btnSignUp = (Button) findViewById(R.id.btnSignUp);

            final LayoutParams TV2_LP = (LayoutParams) TextView2.getLayoutParams();
            final LayoutParams TV3_LP = (LayoutParams) TextView3.getLayoutParams();
            final LayoutParams SignInBtn_LP = (LayoutParams) btnSignIn.getLayoutParams();
            final LayoutParams SignUpBtn_LP = (LayoutParams) btnSignUp.getLayoutParams();
            final LayoutParams Edt_LP = (LayoutParams) edtId.getLayoutParams();
            final LayoutParams Pwd_LP = (LayoutParams) edtPwd.getLayoutParams();

            // 此設 px
            TV2_LP.topMargin = Math.round(
                    getTransformPx(convertPixelsToDp(TV2_LP.topMargin,getApplicationContext()),size.x)
            );
            TV3_LP.topMargin = Math.round(
                    getTransformPx(convertPixelsToDp(TV3_LP.topMargin,getApplicationContext()),size.x)
            );


            /// 18-9 螢幕屏比
            if (size.x == 1440 & size.y == 2880){
                SignInBtn_LP.topMargin = Math.round(
                        getTransformPx(250,size.x)
                );
            }else {
                SignInBtn_LP.topMargin = Math.round(
                        getTransformPx(convertPixelsToDp(SignInBtn_LP.topMargin,getApplicationContext()),size.x)
                );
            }
            SignUpBtn_LP.topMargin = Math.round(
                    getTransformPx(convertPixelsToDp(SignUpBtn_LP.topMargin,getApplicationContext()),size.x)
            );
            Edt_LP.topMargin = Math.round(
                    getTransformPx(convertPixelsToDp(Edt_LP.topMargin,getApplicationContext()),size.x)
            );
            Pwd_LP.topMargin = Math.round(
                    getTransformPx(convertPixelsToDp(Pwd_LP.topMargin,getApplicationContext()),size.x)
            );

            // 此是設dp
            TextView2.setTextSize(Math.round(
                    convertPixelsToDp(
                            getTransformPx(convertPixelsToDp(TextView2.getTextSize(),getApplicationContext()),size.x)
                            ,getApplicationContext())
            ));
            TextView3.setTextSize(Math.round(
                    convertPixelsToDp(
                            getTransformPx(convertPixelsToDp(TextView3.getTextSize(),getApplicationContext()),size.x)
                            ,getApplicationContext())
            ));
            edtId.setTextSize(Math.round(
                    convertPixelsToDp(
                            getTransformPx(convertPixelsToDp(edtId.getTextSize(),getApplicationContext()),size.x),getApplicationContext())
            ));
            edtPwd.setTextSize(Math.round(
                    convertPixelsToDp(
                            getTransformPx(convertPixelsToDp(edtPwd.getTextSize(),getApplicationContext()),size.x)
                            ,getApplicationContext())
            ));
            btnSignUp.setTextSize(Math.round(
                    convertPixelsToDp(
                            getTransformPx(convertPixelsToDp(btnSignUp.getTextSize(),getApplicationContext()),size.x)
                            ,getApplicationContext())
            ));
            btnSignIn.setTextSize(Math.round(
                    convertPixelsToDp(
                            getTransformPx(convertPixelsToDp(btnSignIn.getTextSize(),getApplicationContext()),size.x)
                            ,getApplicationContext())
            ));

            TextView2.setLayoutParams(TV2_LP);
            TextView3.setLayoutParams(TV3_LP);
            edtId.setLayoutParams(Edt_LP);
            edtPwd.setLayoutParams(Pwd_LP);
            btnSignUp.setLayoutParams(SignUpBtn_LP);
            btnSignIn.setLayoutParams(SignInBtn_LP);
        }
        @Override
        protected void onProgressUpdate(Void... values) {
        }
        /**
         *  getTransformPx
         *  傳入當前物件DP與螢幕寬，回傳當前屏幕比的 DP 轉 PX
         * @param currentDP     當前物件DP
         * @param screenWidth   當前螢幕寬
         * @return  Px_of_Current   回傳與預設屏幕比率的 PX
         */
        private float getTransformPx(float currentDP,float screenWidth){

            float rate = screenWidth/(float)1080;
            float DefaultPx = currentDP * (float)(420.0/160.0); // 420 dpi
            float currentPx = DefaultPx * rate;
            /*
            //  Debug Console
            Log.d("Rate",Float.toString(rate));
            Log.d("DefaultPx",Float.toString(DefaultPx));
            Log.d("Px_of_Current",Float.toString(currentPx));
            */
            return currentPx;
        }
    }


    /**
     * AsyncLogin Class
     */
    private class AsyncLogin extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            // Log.d("Start AsyncLogin", params[0] + "," + params[1]);
            conn = new ConnServer();    //  Connect to database && check login information


            PERMISSION = conn.CheckLogin(params[0], params[1]); // 設定 permission;確認登入

            writeShardPreferences("drawing",params[0]);         // 待修
            writeShardPreferences("drawingmult",params[0]);     // 待修
            writeShardPreferences("oneimagetest",params[0]);     // 待修
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (PERMISSION) {
                member = getSharedPreferences("member", MODE_PRIVATE);
                //member.edit().remove().commit();
                String UserData = conn.getMemberData();               //  Get member information , type:String [ 一組 JSON 序列]
                Log.d("User Information",UserData);
                try {
                    JSONObject jsonObj = new JSONObject(UserData);      // JSONObject to parse user data
                    Iterator<String> keys = jsonObj.keys();             // Iterator

                    while (keys.hasNext()) {
                        // Log.d(key,jsonObj.getString(key));
                        String key = (String) keys.next();
                        /// Put user data into SharedPreferences (key,value pair into SharedPreferences)
                        member.edit()
                                .putString(key, jsonObj.getString(key))
                                .commit();
                    }
                } catch (org.json.JSONException e) {
                    Log.d("JSONException", e.getLocalizedMessage());
                } finally {
                    Log.d("[Testing] Userid",
                            getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                    );
                }

                Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();     // toast message
                Intent intent = LoginActivity.this.getIntent();                                                 // get intent to put result value
                intent.putExtra("username", edtId.getText().toString());

                LoginActivity.this.setResult(RESULT_OK, intent);              //  return result to original activity
                LoginActivity.this.finish();                                    //  finish login activity
            }
            else {
                /// Login Fail
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

        /**
         *  writeShardPreferences
         *  將歷史作答紀錄寫入 ShardPreferences
         * @param database
         * @param account
         */
        private void writeShardPreferences(String database,String account) {
            if (PERMISSION) {
                connRecord = new ConnServer();  //  get Record
                /**
                 *  recordList
                 *   drawing && drawingmult 時為 [ [data,imag],[data,imag],... ]
                 *   oneimage 則為 [ data ,data,... ]
                 */
                String[] recordList = connRecord.PersonalRecord(database, account);
                imagetest = getSharedPreferences(database + "_record", MODE_PRIVATE);       // Store historic answer into Shared Preference

                if (recordList != null) {
                    if (database.equals("drawingmult") || database.equals("drawing")){
                        Log.d(database + "_record", "success"); //data
                        for (int i = 0; i < recordList.length; i++) {
                            try {
                                JSONArray data = new JSONArray(recordList[i]);
                                Log.d("JSONArray", data.get(0).toString());
                                //Log.d("JSONArray",data.get(1).toString());    // image
                                imagetest.edit()
                                        .putString(data.get(0).toString(), data.get(1).toString())
                                        .commit();
                            } catch (org.json.JSONException e) {
                                Log.d("Err", "");
                            }
                        }
                    }
                    else if (database.equals("oneimagetest")){
                        Log.d(database + "_record", "success"); //data
                        for (int i = 0; i < recordList.length; i++) {
                            try {
                                Log.d("One Image Array", recordList[i]);
                                imagetest.edit()
                                        .putString(Integer.toString(i),recordList[i])
                                        .commit();
                            } catch (Exception e) {
                                Log.d("Err", "");
                            }
                        }
                    }
                }

            }
        }
    }
}
