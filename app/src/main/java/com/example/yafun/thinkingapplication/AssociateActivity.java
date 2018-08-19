package com.example.yafun.thinkingapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

public class AssociateActivity extends AppCompatActivity {

    // declare variable
    private EditText edtName;
    private Button btnOk, btnClr;
    private TextView txtAssociateTimer;
    private CountDownTimer timer;

    private int imgA_number;
    private ImageView photoA;

    private ListView lvAssociate;

    //timer cont
    private final long TIME = 481*1000L;
    private final long INTERVAL = 1000L;

    // timer state
    private boolean isPaused = false;
    private long timeRemaining = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view set title
        setContentView(R.layout.drawerlayout_associate);
        setTitle("簡圖聯想遊戲");

        while(true){
            try {
                imgA_number = (int)(Math.random()*100+1);
                java.net.URL urlA = new java.net.URL("http://140.122.91.218/thinkingapp/associationrulestest/image/" + imgA_number +".jpg");
                java.net.HttpURLConnection ucA = (java.net.HttpURLConnection) urlA
                        .openConnection();
                ucA.setRequestProperty("User-agent", "IE/6.0");
                ucA.setReadTimeout(30000);
                ucA.connect();

                int statusA = ucA.getResponseCode();

                if(statusA == 404){
                    continue;
                }
                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Bitmap mBitmap = getBitmapFormURL("http://140.122.91.218/thinkingapp/associationrulestest/image/" + imgA_number + ".jpg");

                            runOnUiThread(new Runnable(){
                                @Override
                                public void run(){
                                    photoA = (ImageView)findViewById(R.id.imgAssociate);
                                    photoA.setImageBitmap (mBitmap);
                                }}
                            );
                        }}).start();
                    break;
                }
            }catch (java.net.MalformedURLException e) {
                e.printStackTrace();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }


        // set variable value
        edtName = (EditText)findViewById(R.id.edtAssociateName);
        btnOk = (Button)findViewById(R.id.btnAssociateOk);
        btnClr = (Button)findViewById(R.id.btnAssociateClr);
        txtAssociateTimer = (TextView)findViewById(R.id.txtAssociateTimer);

        lvAssociate = (ListView)findViewById(R.id.lvAssociate);
        // new string list with title
        final List<String> listData = new ArrayList<String>(Arrays.asList("名稱"));
        // array adapter with string list
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listData);
        lvAssociate.setAdapter(arrayAdapter);

        // start timer
        startTimer();

        btnOk.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // add string when click
                if(!TextUtils.isEmpty(edtName.getText().toString())){
                    listData.add(edtName.getText().toString());
                    arrayAdapter.notifyDataSetChanged();
                    edtName.setText("");
                }
                else{
                    Toast.makeText(AssociateActivity.this,"請輸入作品名稱",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // btnClr click
        btnClr.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                edtName.setText("");
            }
        });
    }

    // create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // set menu's function
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            // btnSubmit click
            case R.id.btnSubmit:
                // timer pause and show alert dialog
                isPaused = true;
                new AlertDialog.Builder(AssociateActivity.this)
                        .setMessage("確定提早交卷嗎?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            // if yes stop the timer and submit the sheet
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                timer = null;
                                finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            // if no then resume the timer
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isPaused = false;
                                // set remain time to new timer
                                long millisInFuture = timeRemaining;
                                long countDownInterval = 1000;
                                timer = new CountDownTimer(millisInFuture, countDownInterval) {
                                    @Override
                                    public void onTick(long l) {
                                        long time = l / 1000;
                                        if (isPaused) {
                                            timer.cancel();
                                        } else {
                                            txtAssociateTimer.setText(String.format("%02d 分 %02d 秒", time / 60, time % 60));
                                            timeRemaining = l - 1000;
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        txtAssociateTimer.setText(String.format("00 分 00 秒"));
                                        new AlertDialog.Builder(AssociateActivity.this)
                                                .setMessage("時間結束。")
                                                .setPositiveButton("返回首頁", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        timer.cancel();
                                                        timer = null;
                                                        finish();
                                                    }
                                                }).setCancelable(false).show();
                                    }
                                }.start();
                            }
                        }).setCancelable(false).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // timer start function
    private void startTimer(){
        if(timer==null){
            // use MyCountDownTimer set myself context
            timer = new AssociateActivity.MyCountDownTimer(TIME,INTERVAL);
        }
        timer.start();
    }

    // CountDownTimer function
    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        // counting time
        @Override
        public void onTick(long l) {
            long time = l / 1000;
            if(isPaused){
                timer.cancel();
            }
            else{
                txtAssociateTimer.setText(String.format("%02d 分 %02d 秒",time/60,time%60));
                timeRemaining = l-1000;
            }
        }

        // if timer finish
        @Override
        public void onFinish() {
            txtAssociateTimer.setText(String.format("00 分 00 秒"));
            new AlertDialog.Builder(AssociateActivity.this)
                    .setMessage("時間結束。")
                    .setPositiveButton("返回首頁", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            timer.cancel();
                            timer = null;
                            finish();
                        }
                    }).setCancelable(false).show();
            // submit the sheet
        }
    }

    // intercept back
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            // no action
            return true;
        }
        return false;
    }
    public static Bitmap getBitmapFormURL(String src){
        try{
            URL url = new URL(src);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            InputStream input = conn.getInputStream();
            Bitmap mBitmap = BitmapFactory.decodeStream(input);
            return mBitmap;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
        //return null;
    }

}
