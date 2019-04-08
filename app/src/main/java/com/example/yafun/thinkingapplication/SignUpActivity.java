package com.example.yafun.thinkingapplication;

import android.content.Intent;
import android.content.SharedPreferences;
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
                String showMsg = "《隱私權同意政策》\n同意收集個人資料，並用於學術研究";
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

                    // Initialize  AsyncLogin() class with param
                    new SignUpActivity.AsyncLogin().execute(account, password, classdata, emailtext, school, sex,age,living,religion);
                }


            }
        });
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
            conn = new ConnServer(params[0], params[1], params[2] , params[3], params[4], params[5], params[6], params[7], params[8]);

            // 設定 permission;確認登入
            permission = conn.checkSignUp();

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            if(permission){
                //toast message
                Toast.makeText(SignUpActivity.this, "註冊成功", Toast.LENGTH_SHORT).show();

                //Intent backtologin = new Intent(SignUpActivity.this, LoginActivity.class);
                //startActivity(backtologin);
                finish();
//            }else{
//                Toast.makeText(SignUpActivity.this, "註冊失敗", Toast.LENGTH_SHORT).show();
//            }
//
//
//            member = getSharedPreferences("member", MODE_PRIVATE);
//                //member.edit().remove().commit();
//
//                String content = conn.getMemberInf();               //  Get member information , type:String
//                Log.d("getMemberInf",content);
//                try {
//                    JSONObject jsonObj = new JSONObject(content);   // JSONObject
//                    Iterator<String> keys = jsonObj.keys();         // Iterator
//
//                    while (keys.hasNext()) {
//                        //Log.d(key,jsonObj.getString(key));
//                        String key = (String) keys.next();
//                        member.edit()
//                                .putString(key, jsonObj.getString(key))
//                                .commit();
//                    }
//                } catch (JSONException e) {
//                    Log.d("JSONException", e.getLocalizedMessage());
//                } finally {
//                    Log.d("test",
//                            getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
//                    );
//                }
//
//
//                // toast message
//                Toast.makeText(SignUpActivity.this, "註冊成功", Toast.LENGTH_SHORT).show();
//
//                // get intent to put result value
//                Intent intent = SignUpActivity.this.getIntent();
//                intent.putExtra("username", edtId.getText().toString());
//
//                // return result to original activity
//                SignUpActivity.this.setResult(RESULT_OK, intent);
//                SignUpActivity.this.finish();
            }
            // else show login fail
            else {
                //Log.d("login result", "fail");
                new AlertDialog.Builder(SignUpActivity.this)
                        .setMessage("註冊失敗")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
//
//        @Override
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//        }
//
//        private void writeShardPreferences(String database,String account){
//            connRecord = new ConnServer();//get Record
//            String[] tmparr = connRecord.PersonalRecord(database,account);
//            imagetest = getSharedPreferences(database+"_record", MODE_PRIVATE);
//            Log.d(database+"_record","success");//data
//            if(tmparr!=null) {
//                for (int i = 0; i < tmparr.length; i++) {
//                    try {
//                        JSONArray data = new JSONArray(tmparr[i]);
//                        //Log.d("JSONArray",data.get(1).toString());//data
//
//                        Log.d("JSONArray", data.get(0).toString());
//                        imagetest.edit()
//                                .putString(data.get(0).toString(), data.get(1).toString())
//                                .commit();
//
//                    } catch (JSONException e) {
//                        Log.d("Err", "");
//                    }
//                }
//            }
//        }
    }
}
