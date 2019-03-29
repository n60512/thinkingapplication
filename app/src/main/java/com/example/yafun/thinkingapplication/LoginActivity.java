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
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout.LayoutParams;
import android.content.Context;

import org.json.*;
import java.util.Iterator;


public class LoginActivity extends AppCompatActivity {

    // declare variable
    private TextView TextView2,TextView3;
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

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 全屏幕模式
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
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
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
            final  Point size = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(size);
            Log.d("手機尺寸為", size.x+"X"+size.y);

            TextView2 = (TextView)findViewById(R.id.textView2);
            TextView3 = (TextView)findViewById(R.id.textView3);
            edtId = (EditText) findViewById((R.id.edtId));
            edtPwd = (EditText) findViewById((R.id.edtPwd));
            btnSignIn = (Button) findViewById(R.id.btnSignIn);
            btnSignUp = (Button) findViewById(R.id.btnSignUp);

            final LayoutParams lp_tv2 = (LayoutParams) TextView2.getLayoutParams();
            final LayoutParams lp_tv3 = (LayoutParams) TextView3.getLayoutParams();
            final LayoutParams lp_btSig = (LayoutParams) btnSignIn.getLayoutParams();
            final LayoutParams lp_btSup = (LayoutParams) btnSignUp.getLayoutParams();
            final LayoutParams lp_edtId = (LayoutParams) edtId.getLayoutParams();
            final LayoutParams lp_edPwd = (LayoutParams) edtPwd.getLayoutParams();

            // 此設 px
            lp_tv2.topMargin = Math.round(
                    getTransformPx(convertPixelsToDp(lp_tv2.topMargin,getApplicationContext()),size.x)
            );
            lp_tv3.topMargin = Math.round(
                    getTransformPx(convertPixelsToDp(lp_tv3.topMargin,getApplicationContext()),size.x)
            );
            lp_btSig.topMargin = Math.round(
                    getTransformPx(convertPixelsToDp(lp_btSig.topMargin,getApplicationContext()),size.x)
            );
            lp_btSup.topMargin = Math.round(
                    getTransformPx(convertPixelsToDp(lp_btSup.topMargin,getApplicationContext()),size.x)
            );
            lp_edtId.topMargin = Math.round(
                    getTransformPx(convertPixelsToDp(lp_edtId.topMargin,getApplicationContext()),size.x)
            );
            lp_edPwd.topMargin = Math.round(
                    getTransformPx(convertPixelsToDp(lp_edPwd.topMargin,getApplicationContext()),size.x)
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
                            getTransformPx(convertPixelsToDp(edtId.getTextSize(),getApplicationContext()),size.x)
                            ,getApplicationContext())
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



            TextView2.setLayoutParams(lp_tv2);
            TextView3.setLayoutParams(lp_tv3);
            edtId.setLayoutParams(lp_edtId);
            edtPwd.setLayoutParams(lp_edPwd);
            btnSignUp.setLayoutParams(lp_btSup);
            btnSignIn.setLayoutParams(lp_btSig);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        private float getTransformPx(float currentDP,float screenWidth){
            // 傳入當前物件DP與螢幕寬，回傳當前屏幕比的 DP 轉 PX
            float rate = ((float)screenWidth/(float)1080);
            float Px_of_1080 = currentDP * (float)(420.0/160.0);
            float Px_of_Current = Px_of_1080*rate;

            Log.d("rate",Float.toString((float)screenWidth/(float)1080));
            Log.d("Px_of_1080",Float.toString(currentDP * (float)(420.0/160.0)));
            Log.d("Px_of_Current",Float.toString(Px_of_Current));

            //(float)(393.75* ((float)screenWidth/(float)1080))
            return Px_of_Current;

        }
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

            writeShardPreferences("drawing",params[0]);
            writeShardPreferences("drawingmult",params[0]);

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

        private void writeShardPreferences(String database,String account) {
            if (permission) {
                connRecord = new ConnServer();//get Record
                String[] tmparr = connRecord.PersonalRecord(database, account);
                imagetest = getSharedPreferences(database + "_record", MODE_PRIVATE);
                Log.d(database + "_record", "success");//data
                if (tmparr != null) {
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
}
