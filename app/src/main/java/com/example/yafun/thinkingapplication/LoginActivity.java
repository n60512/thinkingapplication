package com.example.yafun.thinkingapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText edtId,edtPwd;
    String loginid,loginpwd;
    Button btnSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtId = (EditText)findViewById((R.id.edtId));
        edtPwd = (EditText)findViewById((R.id.edtPwd));
        loginid = edtId.getText().toString();
        loginpwd = edtPwd.getText().toString();
        btnSignin = (Button) findViewById(R.id.btnSignIn);

        btnSignin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(true){
                //if(loginid.equals("test01")&&loginpwd.equals("test01")){
                        Toast.makeText(LoginActivity.this,"登入成功",Toast.LENGTH_SHORT).show();
                        finish();
                }
                else{
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("登入失敗")
                            .setPositiveButton("OK",null)
                            .show();
                }
            }
        });

    }
}
