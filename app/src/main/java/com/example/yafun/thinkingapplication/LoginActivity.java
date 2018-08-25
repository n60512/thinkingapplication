package com.example.yafun.thinkingapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    // declare variable
    private Boolean againConnect;
    private EditText edtId,edtPwd;
    private Button btnSignIn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get whether network is connect
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null) againConnect = extras.getBoolean("connNetwork");

        if(againConnect==false){
            againConnect = networkCheck();
            intent.putExtra("connNetwork",againConnect);
        }

        // set variable value
        edtId = (EditText)findViewById((R.id.edtId));
        edtPwd = (EditText)findViewById((R.id.edtPwd));
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        // if btnSignIn click
        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // if is correct account then finish this activity back to main_activity
                if(true){
                //if(edtId.getText().toString().equals("test01")&&edtPwd.getText().toString().equals("test01")){

                        // toast message
                        Toast.makeText(LoginActivity.this,"登入成功",Toast.LENGTH_SHORT).show();

                        // get intent to put result value
                        Intent intent = LoginActivity.this.getIntent();
                        intent.putExtra("username",edtId.getText().toString());

                        // return result to original activity
                        LoginActivity.this.setResult(RESULT_OK,intent);
                        LoginActivity.this.finish();
                }
                // else show login fail
                else{
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("登入失敗")
                            .setPositiveButton("OK",null)
                            .show();
                }
            }
        });

    }

    // check whether connected to network
    private Boolean networkCheck(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        // if connected to network
        if (ni != null && ni.isConnected()) {
            return true;
        }
        // else can't connect
        else if (ni == null) {
            againConnect = false;
            // set alertdialog
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
            alertDialog.setTitle("連線失敗");
            alertDialog.setMessage("網路連線失敗,\n請按重試按鈕重新連線。");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("重試", null);
            // custom dialog show
            final AlertDialog alert = alertDialog.create();
            alert.show();
            // custom button function
            Button btn = alert.getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo ni = cm.getActiveNetworkInfo();
                    if (ni != null && ni.isConnected()) {
                        // if already connected to network
                        againConnect = true;
                        alert.dismiss();
                    }
                }
            });
        }
        if(againConnect==true) return true;
        else return false;
    }
}
