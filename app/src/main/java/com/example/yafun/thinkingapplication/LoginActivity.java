package com.example.yafun.thinkingapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private EditText edtId,edtPwd;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
}
