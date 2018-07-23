package com.example.yafun.thinkingapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Game1Activity extends AppCompatActivity {

    private TextView txtTimer1;
    private CountDownTimer timer;
    private final long TIME = 30*1000L;
    private final long INTERVAL = 1000L;

    private EditText edtName;
    private Button btnOk, btnClr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);

        txtTimer1 = (TextView)findViewById(R.id.txtTimer1);
        startTimer();

        edtName = (EditText)findViewById(R.id.edtName1);
        btnOk = (Button)findViewById(R.id.btnOk1);
        btnClr = (Button)findViewById(R.id.btnClr1);
        btnClr.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                edtName.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.btnSubmit:
                // 暫停計時
                timer.cancel();
                new AlertDialog.Builder(Game1Activity.this)
                        .setMessage("確定提早交卷嗎?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // submit the sheet
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 恢復計時
                            }
                        }).setCancelable(false).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            long time = l / 1000;

            if(time<60){
                txtTimer1.setText(String.format("00 分 %02d 秒",time));
            }
            else{
                txtTimer1.setText(String.format("%02d 分 %02d 秒",time/60,time%60));
            }
        }

        @Override
        public void onFinish() {
            new AlertDialog.Builder(Game1Activity.this)
                    .setMessage("時間結束。")
                    .setPositiveButton("返回首頁", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            txtTimer1.setText(String.format("00 分 00 秒"));
                            timer.cancel();
                            finish();
                        }
                    }).setCancelable(false).show();
            // submit the sheet
        }
    }

    private void startTimer(){
        if(timer==null){
            timer = new MyCountDownTimer(TIME,INTERVAL);
        }
        timer.start();
    }

}
