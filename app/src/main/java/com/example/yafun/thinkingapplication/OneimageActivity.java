package com.example.yafun.thinkingapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import java.util.Map;

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

    ArrayList<listContext> mlist = new ArrayList<listContext>();
    private myAdapter adapter;


    /// Timer Cont
    private long TIME = 481 * 1000L;
    private final long INTERVAL = 1000L;

    /// Timer State
    private boolean isPaused = false;
    private boolean ifFinished = false;
    private long timeRemaining = 0;


    SharedPreferences oneimagetest_record;
    private int ChosenImgNum = 1;

    private ConnServer connUpdate = new ConnServer();
    private int RecordLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view set title
        setContentView(R.layout.drawerlayout_associate);
        setTitle("簡圖聯想遊戲");     //對應 oneimage

        // set variable value
        edtName = (EditText) findViewById(R.id.edtAssociateName);
        btnOk = (Button) findViewById(R.id.btnAssociateOk);
        btnClr = (Button) findViewById(R.id.btnAssociateClr);
        txtOneimageTimer = (TextView) findViewById(R.id.txtAssociateTimer);
        imgAssociate = (ImageView) findViewById(R.id.imgAssociate);
        imgAssociate.setImageResource(R.drawable.oneimage_2);   // 預設

        oneimagetest_record = getSharedPreferences("oneimagetest_record", MODE_PRIVATE);
        ChosenImgNum = ChoseImage();    // 這邊會接不到

        // 左側滑出作答紀錄
        lvAssociate = (ListView) findViewById(R.id.lvAssociate);

        // add title to mlist

        mlist.add(new listContext("名稱", "選擇"));
        // new adapter with context and set
        adapter = new myAdapter(OneimageActivity.this, mlist);
        lvAssociate.setAdapter(adapter);

        Map<String,?> keys = oneimagetest_record.getAll();

        Log.d("Testing", "================================");
        Log.d("MapSize", Integer.toString(keys.size()));
        this.RecordLength = keys.size();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());

            String user_record = entry.getValue().toString().
                    replace("[", "").
                    replace("]", "").
                    replace("\"", "");

            String[] single_record_row = user_record.split(",");
            Log.d("single_record_row",single_record_row[0] + ": " + single_record_row[1]);
            mlist.add(new OneimageActivity.listContext(single_record_row[0], single_record_row[1]));
            adapter.notifyDataSetChanged();
        }


        btnOk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add string when click
                if (!TextUtils.isEmpty(edtName.getText().toString())) {
                    mlist.add(new OneimageActivity.listContext(edtName.getText().toString(),
                            String.valueOf(ChosenImgNum)));
                    adapter.notifyDataSetChanged();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Do stuff you want here
        ifFinished = true;
    }

    public void setChosenImgNum(int index){
        ChosenImgNum = index;
    }
    public int getChosenImgNum(){
        return ChosenImgNum;
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
                                Thread timerthread = new Thread() {
                                    public void run() {
                                        Long currentTimer = timeRemaining / 1000;
                                        Log.d("剩餘時間(秒數)",(currentTimer).toString());

                                        connUpdate.updateAnwsertime(
                                                "oneimage",
                                                currentTimer.toString(),
                                                getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                                        );
                                        /// 剩餘時間寫入 SharedPreferences
                                        getSharedPreferences("member", MODE_PRIVATE)
                                                .edit()
                                                .putString("oneimage",currentTimer.toString())
                                                .commit();
                                    }
                                };
                                timerthread.start();

                                timer = null;
                                Thread thread = new Thread() {
                                    public void run() {
                                        int count = adapter.getCount() - 1;
                                        ConnServer[] conn = new ConnServer[count];
                                        for (int index = RecordLength; index < count; index++) {
                                            listContext content = adapter.getItem(index + 1);   // !!!

                                            conn[index] = new ConnServer("oneimage", content.name,
                                                    getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"),
                                                    String.valueOf(getChosenImgNum())
//                                                    oneimagetest_record.getString("ChosenImage", "null")
                                            );

                                            String insert_format = String.format("[\"%s\" ,%s]", content.name, getChosenImgNum());
                                            Log.d("One image 此次新增","["+Integer.toString(index)+"]:" + insert_format);
                                            oneimagetest_record
                                                    .edit()
                                                    .putString(Integer.toString(index), insert_format)
                                                    .commit();
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
                                            timer = null;
                                        } else {
                                            txtOneimageTimer.setText(String.format("%02d 分 %02d 秒", time / 60, time % 60));
                                            timeRemaining = l - 1000;
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        txtOneimageTimer.setText(String.format("00 分 00 秒"));
                                        /// Time out dialog
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
                                                                /// 剩餘時間寫入 SharedPreferences
                                                                getSharedPreferences("member", MODE_PRIVATE)
                                                                        .edit()
                                                                        .putString("oneimage",currentTimer.toString())
                                                                        .commit();
                                                            }
                                                        };
                                                        timerthread.start();
                                                        timer = null;
                                                        finish();
                                                    }
                                                }).setCancelable(false).show();

                                        /// Time out 資料全數送出
                                        Thread thread = new Thread() {
                                            public void run() {
                                                int count = adapter.getCount() - 1;
                                                ConnServer[] conn = new ConnServer[count];
                                                for (int index = RecordLength; index < count; index++) {
                                                    listContext content = adapter.getItem(index + 1);   // !!!

                                                    conn[index] = new ConnServer("oneimage", content.name,
                                                            getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"),
                                                            String.valueOf(getChosenImgNum())
//                                                            oneimagetest_record.getString("ChosenImage", "null")
                                                    );

                                                    Log.d("One image 此次新增","["+Integer.toString(index)+"]:" + content.name);
                                                    String insert_format = String.format("[\"%s\" ,%s]", content.name, getChosenImgNum());
                                                    oneimagetest_record
                                                            .edit()
                                                            .putString(Integer.toString(index), insert_format)
                                                            .commit();
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

        /// 時間結束
        @Override
        public void onFinish() {
            txtOneimageTimer.setText(String.format("00 分 00 秒"));
            AlertDialog.Builder finish_timer_AlertDialog = new AlertDialog.Builder(OneimageActivity.this);
            finish_timer_AlertDialog
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

                                    /// 剩餘時間寫入 SharedPreferences
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
                    }).setCancelable(false);

            if (!ifFinished) {    // if activity isn't destroy
                finish_timer_AlertDialog.show();

                // submit the sheet
                Thread thread = new Thread() {
                    public void run() {
                        int count = adapter.getCount() - 1;
                        ConnServer[] conn = new ConnServer[count];
                        for (int index = RecordLength; index < count; index++) {
                            listContext content = adapter.getItem(index + 1);   // !!!

                            conn[index] = new ConnServer("oneimage", content.name,
                                    getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"),
                                    String.valueOf(getChosenImgNum())
//                                oneimagetest_record.getString("ChosenImage", "null")
                            );

                            Log.d("One image 此次新增", "[" + Integer.toString(index) + "]:" + content.name);

                            String insert_format = String.format("[\"%s\" ,%s]", content.name, getChosenImgNum());
                            oneimagetest_record
                                    .edit()
                                    .putString(Integer.toString(index), insert_format)
                                    .commit();
                        }
                    }
                };
                thread.start();
            }
        }
    }

    private int ChoseImage(){
        final Dialog dialog = new Dialog(this,R.style.AppTheme);
        dialog.setContentView(R.layout.choseimage);
        dialog.setCancelable(false);
        dialog.show();

        final int[] oneimages = {(R.drawable.oneimage_1),
                (R.drawable.oneimage_2),
                (R.drawable.oneimage_3),
                (R.drawable.oneimage_4),
                (R.drawable.oneimage_5)} ;

        ImageView[] imgv = {(ImageView) dialog.findViewById(R.id.imageView1),
                (ImageView) dialog.findViewById(R.id.imageView2),
                (ImageView) dialog.findViewById(R.id.imageView3),
                (ImageView) dialog.findViewById(R.id.imageView4),
                (ImageView) dialog.findViewById(R.id.imageView5)} ;

        for (int i = 0;i < imgv.length  ;i++){
            final int ChosenImgNum = i;
            imgv[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // int tmp = v.getId();
                    // Log.d("[Chose]",Integer.toString(tmp));
                    Log.d("[Chose]",Integer.toString(ChosenImgNum + 1));
                    imgAssociate.setImageResource(oneimages[ChosenImgNum]);

//                    oneimagetest_record
//                            .edit()
//                            .putString("ChosenImage",Integer.toString(ChosenImgNum + 1))
//                            .commit();  /////1111
//                    Log.d("[Chose_SF]",oneimagetest_record.getString("ChosenImage", "null"));
                    setChosenImgNum(ChosenImgNum + 1);

                    dialog.dismiss();
                    guideView();    // 教學 dialog
                }
            });
        }
        return getChosenImgNum();
    }

    public class listContext {
        private String name;
        private String ChosenImage;
        private ArrayList<String> select;

        public listContext(String name, ArrayList<String> select) {
            this.name = name;
            this.select = select;
        }

        public listContext(String name, String ChosenImage) {
            this.name = name;
            this.ChosenImage = ChosenImage;
        }
        public void setName(String name) {
            this.name = name;
        }

        public void setSelect(ArrayList<String> select) {
            this.select = select;
        }

        public String getName() {
            return name;
        }

        public String getChosenImage(){
            return  ChosenImage;
        }

        public ArrayList<String> getSelect() {
            return select;
        }
    }

    // my adapter
    public class myAdapter extends BaseAdapter {

        // inflater context
        private LayoutInflater mInflater;
        // data context list
        private ArrayList<OneimageActivity.listContext> mdatas;

        public myAdapter(Context context, ArrayList<OneimageActivity.listContext> listcontext) {
            mInflater = LayoutInflater.from(context);
            this.mdatas = listcontext;
        }

        @Override
        public int getCount() {
            return mdatas.size();
        }

        @Override
        public OneimageActivity.listContext getItem(int position) {
            return mdatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mdatas.indexOf(getItem(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // set holder
            OneimageActivity.myAdapter.ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem, null);
                holder = new OneimageActivity.myAdapter.ViewHolder((TextView) convertView.findViewById(R.id.txtListName),
                        (TextView) convertView.findViewById(R.id.txtListSelect));
                convertView.setTag(holder);
            } else {
                holder = (OneimageActivity.myAdapter.ViewHolder) convertView.getTag();
            }
            // set convertview with holder
            OneimageActivity.listContext context = (OneimageActivity.listContext) getItem(position);
            holder.txtListName.setText(context.getName());
            holder.txtListSelect.setText(context.getChosenImage());
            return convertView;
        }

        // holder structure
        private class ViewHolder {
            TextView txtListName;
            TextView txtListSelect;

            public ViewHolder(TextView txtListName, TextView txtListSelect) {
                this.txtListName = txtListName;
                this.txtListSelect = txtListSelect;
            }
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
}
