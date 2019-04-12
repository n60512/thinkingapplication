package com.example.yafun.thinkingapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneimageActivity extends AppCompatActivity {

    private Boolean guideSet;

    /// Declare Variable
    private EditText edtName;
    private Button btnOk, btnClr;
    private TextView txtOneimageTimer;
    private CountDownTimer timer;
    private ImageView imgAssociate;
    private ListView lvAssociate;
    private String imageID = null;

    private ArrayAdapter<String> arrayAdapter;

    /// Timer Cont
    private long TIME = 481 * 1000L;
    private final long INTERVAL = 1000L;

    /// Timer State
    private boolean isPaused = false;
    private long timeRemaining = 0;

    private ConnServer connUpdate = new ConnServer();
    private int RecordLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view set title
        setContentView(R.layout.drawerlayout_associate);
        setTitle("簡圖聯想遊戲");     //對應 oneimage

        guideView();    // 教學 dialogS

        // set variable value
        edtName = (EditText) findViewById(R.id.edtAssociateName);
        btnOk = (Button) findViewById(R.id.btnAssociateOk);
        btnClr = (Button) findViewById(R.id.btnAssociateClr);
        txtOneimageTimer = (TextView) findViewById(R.id.txtAssociateTimer);

        imgAssociate = (ImageView) findViewById(R.id.imgAssociate);


        /**
         *  Loaing image from Server
         */
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    imageID = "as2";
                    if (imageID != null) Log.d("Load image thread", "Successed");
                    else Log.d("Load image thread", "Failed");
                    Log.d("image_number", imageID);
                    Picasso.get().load("http://140.122.91.218/thinkingapp/oneimagetest/image/" + imageID + ".png?1").into(imgAssociate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.run();


        /// 左側滑出作答紀錄
        lvAssociate = (ListView) findViewById(R.id.lvAssociate);
        final List<String> listData = new ArrayList<String>(Arrays.asList("名稱"));   // new string list with title
        // array adapter with string list
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listData);
        lvAssociate.setAdapter(arrayAdapter);


        ///1001
        String tmpString[] = this.connUpdate.PersonalRecord("oneimagetest", getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"));
        if (tmpString != null) {
            this.RecordLength = tmpString.length;
            for (int i = 0; i < tmpString.length; i++) {
                //Log.d("record_testing", tmpString[i]);    //testing
                listData.add(tmpString[i]);
                arrayAdapter.notifyDataSetChanged();
            }
        }

        btnOk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add string when click
                if (!TextUtils.isEmpty(edtName.getText().toString())) {
                    listData.add(edtName.getText().toString());
                    arrayAdapter.notifyDataSetChanged();
                    edtName.setText("");
                } else {
                    Toast.makeText(OneimageActivity.this, "請輸入作品名稱", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // btnClr click
        btnClr.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                edtName.setText("");
            }
        });
    }

    // create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // set menu's function
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // btnSubmit click
            case R.id.btnSubmit:
                // timer pause and show alert dialog
                isPaused = true;
                new AlertDialog.Builder(OneimageActivity.this)
                        .setMessage("確定提早交卷嗎?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            // if yes stop the timer and submit the sheet
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Long currentTimer = timeRemaining / 1000;
                                Log.d("剩餘時間(秒數)",(currentTimer).toString());
                                connUpdate.updateAnwsertime(
                                        "oneimage",
                                        currentTimer.toString(),
                                        getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                                        );

                                Log.d("剩餘時間(sp)",(currentTimer).toString());
                                getSharedPreferences("member", MODE_PRIVATE)
                                        .edit()
                                        .putString("oneimage",currentTimer.toString())
                                        .commit();


                                timer = null;
                                Thread thread = new Thread() {
                                    public void run() {
                                        int count = arrayAdapter.getCount() - 1;
                                        ConnServer[] conn = new ConnServer[count];
                                        for (int index = RecordLength; index < count; index++) {
                                            String content = arrayAdapter.getItem(index + 1);
                                            //Log.d("oneimageName", content);

                                            conn[index] = new ConnServer("oneimage", content, getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"));
                                        }
                                    }
                                };
                                thread.start();
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
                                            //timer.cancel();
                                            timer = null;
                                        } else {
                                            txtOneimageTimer.setText(String.format("%02d 分 %02d 秒", time / 60, time % 60));
                                            timeRemaining = l - 1000;
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        txtOneimageTimer.setText(String.format("00 分 00 秒"));
                                        new AlertDialog.Builder(OneimageActivity.this)
                                                .setMessage("時間結束。")
                                                .setPositiveButton("返回首頁", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        /// Thread for timer update
                                                        Thread timerthread = new Thread() {
                                                            public void run() {
                                                                Long currentTimer = timeRemaining / 1000;
                                                                Log.d("剩餘時間(秒數)",(currentTimer).toString());
                                                                connUpdate.updateAnwsertime(
                                                                        "oneimage",
                                                                        currentTimer.toString(),
                                                                        getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                                                                );

                                                                Log.d("剩餘時間(sp)",(currentTimer).toString());
                                                                getSharedPreferences("member", MODE_PRIVATE)
                                                                        .edit()
                                                                        .putString("oneimage",currentTimer.toString())
                                                                        .commit();
                                                            }
                                                        };
                                                        timerthread.start();

                                                        //timer.cancel();
                                                        timer = null;
                                                        finish();
                                                    }
                                                }).setCancelable(false).show();
                                        Thread thread = new Thread() {
                                            public void run() {
                                                int count = arrayAdapter.getCount() - 1;
                                                ConnServer[] conn = new ConnServer[count];
                                                for (int index = RecordLength; index < count; index++) {
                                                    String content = arrayAdapter.getItem(index + 1);
                                                    //Log.d("oneimageName", content);

                                                    conn[index] = new ConnServer("oneimage", content, getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"));
                                                }
                                            }
                                        };
                                        thread.start();
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
    private void startTimer() {
        if (timer == null) {
            // use MyCountDownTimer set myself context


            Long ltest = Long.parseLong(getSharedPreferences("member", MODE_PRIVATE).getString("oneimage", "null"));
            Log.d("簡圖聯想遊戲_讀秒",ltest.toString());

            TIME = (Long.parseLong(getSharedPreferences("member", MODE_PRIVATE).getString("oneimage", "null"))+1) * 1000L;
            timer = new OneimageActivity.MyCountDownTimer(TIME, INTERVAL);
        }
        timer.start();
    }

    // CountDownTimer function
    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        // counting time
        @Override
        public void onTick(long l) {
            long time = l / 1000;
            if (isPaused) {
                //timer.cancel();
                timer = null;
            } else {
                txtOneimageTimer.setText(String.format("%02d 分 %02d 秒", time / 60, time % 60));
                timeRemaining = l - 1000;
            }
        }

        // if timer finish
        @Override
        public void onFinish() {
            txtOneimageTimer.setText(String.format("00 分 00 秒"));
            new AlertDialog.Builder(OneimageActivity.this)
                    .setMessage("時間結束。")
                    .setPositiveButton("返回首頁", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            /// Thread for timer update
                            Thread timerthread = new Thread() {
                                public void run() {
                                    Long currentTimer = timeRemaining / 1000;
                                    Log.d("剩餘時間(秒數)",(currentTimer).toString());
                                    connUpdate.updateAnwsertime(
                                            "oneimage",
                                            currentTimer.toString(),
                                            getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                                    );

                                    Log.d("剩餘時間(sp)",(currentTimer).toString());
                                    getSharedPreferences("member", MODE_PRIVATE)
                                            .edit()
                                            .putString("oneimage",currentTimer.toString())
                                            .commit();
                                }
                            };
                            timerthread.start();

                            //timer.cancel();
                            timer = null;
                            finish();
                        }
                    }).setCancelable(false).show();
            // submit the sheet
            Thread thread = new Thread() {
                public void run() {
                    int count = arrayAdapter.getCount() - 1;
                    ConnServer[] conn = new ConnServer[count];
                    for (int index = 0; index < count; index++) {
                        String content = arrayAdapter.getItem(index + 1);
                        Log.d("oneimageName", content);
                        conn[index] = new ConnServer("oneimage", content, getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"));
                    }
                }
            };
            thread.start();
        }
    }

    // guide dialog view
    private void guideView() {
        // get whether guideSet is set
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) guideSet = extras.getBoolean("guideSet");
        this.setResult(RESULT_OK, intent);

        // if first time, show the guide
        if (guideSet == false) {
            // set guide dialog view
            final Dialog dialog = new Dialog(this, R.style.Dialog_Fullscreen);
            dialog.setContentView(R.layout.dialog_guide);
            // animation start
            ImageView iv_click = (ImageView) dialog.findViewById(R.id.iv_click);
            ImageView iv_drawable = (ImageView) dialog.findViewById(R.id.iv_drawable);
            animationStart(iv_click, iv_drawable);
            // end dialog view
            ImageView iv_gotit = (ImageView) dialog.findViewById(R.id.iv_gotit);
            iv_gotit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // start timer
                    startTimer();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        // else only start the timer
        else startTimer();
    }

    // start animation
    private void animationStart(ImageView iv_click, ImageView iv_drawable) {
        // animation route
        Animation am_click = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.95f, Animation.RELATIVE_TO_SELF, 3f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        am_click.setDuration(2500);
        am_click.setRepeatCount(-1);
        am_click.setStartOffset(500);
        iv_click.setAnimation(am_click);
        am_click.start();

        Animation am_drawable = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        am_drawable.setDuration(2000);
        am_drawable.setRepeatCount(-1);
        am_drawable.setStartOffset(1000);
        iv_drawable.setAnimation(am_drawable);
        am_drawable.start();
    }

    // intercept back
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // no action
            return true;
        }
        return false;
    }

    // post random image number
    private String randomSetImage() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String webRequest = null;
        String random_image;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://140.122.91.218/thinkingapp/oneimagetest/fileCount.php");

        try {
            HttpResponse response = client.execute(request);
            HttpEntity resEntity = response.getEntity();
            webRequest = EntityUtils.toString(resEntity);
            Log.d("webRequest", webRequest);

            JSONObject obj = new JSONObject(webRequest);  // parse web request
            random_image = obj.getString("random1");
            Log.d("random_number", random_image);
            return random_image;

        } catch (java.io.IOException e) {
            Log.d("IOException", e.getMessage());
        } catch (org.json.JSONException e) {
            Log.d("JSON Error", e.getMessage());
        }
        return null;
    }

}
