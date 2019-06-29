package com.example.yafun.thinkingapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class SignUpActivity extends AppCompatActivity {

    // declare variable
    private EditText edtId, edtPwd, edClass, email, edSchool,edAge;
    private RadioGroup rg;
    private Button btnSignUp;
    private Spinner spinreside,spinreligon;
    private ConnServer conn,connRecord;
    private boolean permission = false;
    SharedPreferences member,imagetest;
    private RadioButton male, female;
    private CheckBox privacy;
    private String livingplace, myreligion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // set variable value
        edtId = (EditText) findViewById((R.id.edtId));
        edtPwd = (EditText) findViewById((R.id.edtPwd3));
        edClass = (EditText)  findViewById(R.id.edClass);
        email = (EditText) findViewById(R.id.edemail);
        edSchool = (EditText) findViewById(R.id.edSchool);
        rg = (RadioGroup)findViewById(R.id.rg1);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        male = (RadioButton) findViewById(R.id.radioButton2);
        female = (RadioButton) findViewById(R.id.radioButton);
        edAge = (EditText) findViewById((R.id.edAge));
        privacy = (CheckBox) findViewById(R.id.checkBox);

        String[] residelist = getResources().getStringArray(R.array.spn_list);
        spinreside = (Spinner)findViewById(R.id.residespinner);
        spinreside.setOnItemSelectedListener(spnOnItemSelected);
        ArrayAdapter<String> _Adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, residelist);
        spinreside.setAdapter(_Adapter);

        String[] religonlist = getResources().getStringArray(R.array.spn_list2);
        spinreligon = (Spinner)findViewById(R.id.religionspinner2);
        spinreligon.setOnItemSelectedListener(spnOnItemSelected2);
        ArrayAdapter<String> _Adapter2=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, religonlist);
        spinreligon.setAdapter(_Adapter2);

        privacy.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String showMsg = "《隱私權同意政策》\n\n\t思考力活動為國立臺灣師範大學數位遊戲學習實驗室及資料探勘實驗室所共同開發之數位遊戲app，思考力活動開發團隊對於每位使用者的隱私權絕對尊重並保護重視。遵循《個人資料保護法》之規定，在未經您的同意之下，我們絕不會將您的個人資料提供予任何與本APP服務無關之第三方。請您妥善保密自己的使用帳號、密碼及個人資料，不要將任何個人資料，尤其是密碼提供給第三者。";
                if(isChecked)
                {
                    // Create an AlertDialog object.
                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();

                    // Set prompt message.
                    alertDialog.setMessage(showMsg);

                    // Show the alert dialog.
                    alertDialog.show();
                }
            }
        });
        SharedPreferences tmp;
        tmp = getSharedPreferences("member", MODE_PRIVATE);
        SharedPreferences.Editor editor = tmp.edit();
        editor.clear().commit();
        tmp = getSharedPreferences("drawing_record", MODE_PRIVATE);
        tmp.edit().clear().commit();
        tmp = getSharedPreferences("drawingmult_record", MODE_PRIVATE);
        tmp.edit().clear().commit();


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get text from id and passord field
                final String account = edtId.getText().toString();
                final String password = edtPwd.getText().toString();
                final String classdata = edClass.getText().toString();
                final String emailtext= email.getText().toString();
                final String school= edSchool.getText().toString();
                final String age = edAge.getText().toString();
                final String living = livingplace.toString();
                final String religion = myreligion.toString();
                //Toast.makeText(SignUpActivity.this, "你點選的是:"+livingplace+myreligion, Toast.LENGTH_SHORT).show();
                final String sex;
                if(male.isChecked()){
                    sex="male";
                }else{
                    sex="female";
                }

                if (edtId.getText().toString().equals("") || edtPwd.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "帳號及密碼欄位為必填項目", Toast.LENGTH_SHORT).show();
                }else {
                    if (isNetworkConnected()){
                        // Initialize  AsyncLogin() class with param
                        new SignUpActivity.AsyncLogin().execute(account, password, classdata, emailtext, school, sex,age,living,religion);
                    }
                    else
                        Toast.makeText(SignUpActivity.this, "請確認網路連線狀況", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private AdapterView.OnItemSelectedListener spnOnItemSelected = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            String str=parent.getItemAtPosition(position).toString();
            livingplace = parent.getItemAtPosition(position).toString();
            //Toast.makeText(SignUpActivity.this, "你點選的是:"+id, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }
    };

    private AdapterView.OnItemSelectedListener spnOnItemSelected2 = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            String str=parent.getItemAtPosition(position).toString();
            myreligion = parent.getItemAtPosition(position).toString();
            //Toast.makeText(SignUpActivity.this, "你點選的是:"+id, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }
    };

    /**
     * AsyncLogin Class
     */
    private class AsyncLogin extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            Log.i("Start AsyncLogin", params[0] + "," + params[1] + "," + params[2] + "," + params[3]+ "," + params[4] + "," + params[5]+ "," + params[6]+ "," + params[7]+ "," + params[8]);

            // Connect to database && check login information
            conn = new ConnServer();
            // 設定 permission;確認登入
            permission =  conn.CheckSignUp(params[0], params[1], params[2] , params[3], params[4], params[5], params[6], params[7], params[8]);

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if(permission) {
                //toast message
                Toast.makeText(SignUpActivity.this, "註冊成功", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                new AlertDialog.Builder(SignUpActivity.this)
                        .setMessage("註冊失敗")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }
}
