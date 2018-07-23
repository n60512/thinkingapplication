package com.example.yafun.thinkingapplication;

import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Game2Activity extends AppCompatActivity {

    private TextView txtTimer2;
    private CountDownTimer timer;
    private final long TIME = 30*1000L;
    private final long INTERVAL = 1000L;

    private EditText edtName;
    private Button btnOk, btnClr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);

        txtTimer2 = (TextView)findViewById(R.id.txtTimer2);
        startTimer();

        edtName = (EditText)findViewById(R.id.edtName2);
        btnOk = (Button)findViewById(R.id.btnOk2);
        btnClr = (Button)findViewById(R.id.btnClr2);
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
                new AlertDialog.Builder(Game2Activity.this)
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
                txtTimer2.setText(String.format("00 分 %02d 秒",time));
            }
            else{
                txtTimer2.setText(String.format("%02d 分 %02d 秒",time/60,time%60));
            }
        }

        @Override
        public void onFinish() {
            new AlertDialog.Builder(Game2Activity.this)
                    .setMessage("時間結束。")
                    .setPositiveButton("返回首頁", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            txtTimer2.setText(String.format("00 分 00 秒"));
                            timer.cancel();
                            finish();
                        }
                    }).setCancelable(false).show();
            // submit the sheet
        }
    }

    private void startTimer(){
        if(timer==null){
            timer = new Game2Activity.MyCountDownTimer(TIME,INTERVAL);
        }
        timer.start();
    }
}
