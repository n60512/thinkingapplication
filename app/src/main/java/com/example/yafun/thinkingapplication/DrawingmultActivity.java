package com.example.yafun.thinkingapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DrawingmultActivity extends AppCompatActivity {

    private Boolean guideSet;

    // declare variable
    private EditText edtName;
    private Button btnOk, btnClr;
    private TextView txtExpandTimer;
    private CountDownTimer timer;

    private ImageView imgvExpand;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;

    private ImageView imgImgDraw;
    private TextView txtImgName;
    private LinearLayout llImg;
    private ListView lvExpand;
    List<listContext> mlist = new ArrayList<listContext>();
    private myAdapter adapter;

    // timer count
    private long TIME = 1201 * 1000L;
    private final long INTERVAL = 1000L;

    // timer state
    private boolean isPaused = false;
    private long timeRemaining = 0;

    SharedPreferences drawingmult_record;
    private ConnServer connUpdate = new ConnServer();
    private int RecordLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view set title
        setContentView(R.layout.drawerlayout_expand);
        setTitle("圖繪展開遊戲");

        // check guide dialog then start timer
        guideView();

        // set variable value
        edtName = (EditText) findViewById(R.id.edtExpandName);
        btnOk = (Button) findViewById(R.id.btnExpandOk);
        btnClr = (Button) findViewById(R.id.btnExpandClr);
        txtExpandTimer = (TextView) findViewById(R.id.txtExpandTimer);
        imgvExpand = (ImageView) findViewById(R.id.imgvExpand);

        imgImgDraw = (ImageView) findViewById(R.id.imgImgDraw);
        txtImgName = (TextView) findViewById(R.id.txtImgName);
        llImg = (LinearLayout) findViewById(R.id.llImg);
        lvExpand = (ListView) findViewById(R.id.lvExpand);

        // new adapter with context and set
        adapter = new myAdapter(DrawingmultActivity.this, mlist);
        lvExpand.setAdapter(adapter);

        // paint initialize
        paint = new Paint();
        paint.setStrokeWidth(6);
        paint.setColor(Color.GRAY);
        imgvExpand.setOnTouchListener(touch);


        drawingmult_record = getSharedPreferences("drawingmult_record", MODE_PRIVATE);
        Map<String,?> keys = drawingmult_record.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            //Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
            Log.d("DataName", entry.getKey());

            byte[] decodedString = Base64.decode(entry.getValue().toString(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            mlist.add(new DrawingmultActivity.listContext(decodedByte,entry.getKey().toString()));//add to list
        }
        Log.d("MapSize", Integer.toString(keys.size()));
        RecordLength = (keys.size());//依據作答紀錄設置list起始位置


        btnOk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baseBitmap == null) {
                    Toast.makeText(DrawingmultActivity.this, "請完成作畫", Toast.LENGTH_SHORT).show();
                } else if (edtName.getText().toString().equals("")) {
                    Toast.makeText(DrawingmultActivity.this, "請輸入作品名稱", Toast.LENGTH_SHORT).show();
                }
                // clear all
                else if (baseBitmap != null && !TextUtils.isEmpty(edtName.getText().toString())) {
                    mlist.add(new listContext(baseBitmap, edtName.getText().toString()));
                    adapter.notifyDataSetChanged();

                    edtName.setText("");
                    baseBitmap = Bitmap.createBitmap(imgvExpand.getWidth(), imgvExpand.getHeight(), Bitmap.Config.ARGB_8888);
                    baseBitmap = resizeImage(baseBitmap, 320, 360);
                    canvas = new Canvas(baseBitmap);
                    canvas.drawColor(0xfffffff0);
                    imgvExpand.setImageBitmap(baseBitmap);
                    baseBitmap = null;
                }
            }
        });

        // btnClr click
        btnClr.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                edtName.setText("");
                if (baseBitmap != null) {
                    baseBitmap = Bitmap.createBitmap(imgvExpand.getWidth(), imgvExpand.getHeight(), Bitmap.Config.ARGB_8888);
                    baseBitmap = resizeImage(baseBitmap, 320, 360);
                    canvas = new Canvas(baseBitmap);
                    canvas.drawColor(0xfffffff0);
                    imgvExpand.setImageBitmap(baseBitmap);
                    baseBitmap = null;
                }
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
                new AlertDialog.Builder(DrawingmultActivity.this)
                        .setMessage("確定提早交卷嗎?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            // if yes stop the timer and submit the sheet
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                /// Thread for timer update
                                Thread timerthread = new Thread() {
                                    public void run() {
                                        Long currentTimer = timeRemaining / 1000;
                                        Log.d("剩餘時間(秒數)",(currentTimer).toString());
                                        connUpdate.updateAnwsertime(
                                                "drawingmult",
                                                currentTimer.toString(),
                                                getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                                        );

                                        Log.d("剩餘時間(sp)",(currentTimer).toString());
                                        getSharedPreferences("member", MODE_PRIVATE)
                                                .edit()
                                                .putString("drawingmult",currentTimer.toString())
                                                .commit();
                                    }
                                };
                                timerthread.start();

                                //timer.cancel();
                                timer = null;
                                // commit content to database
                                Thread thread = new Thread() {
                                    public void run() {
                                        int count = adapter.getCount();
                                        // create datalist and upload data to server
                                        ConnServer[] conn = new ConnServer[count];
                                        for (int index = RecordLength; index < count; index++) {
                                            // get adapter info.
                                            String content = adapter.getItem(index).getName();
                                            Bitmap uploadimg = adapter.getItem(index).getImage();
                                            // connect to Server
                                            conn[index] = new ConnServer(
                                                    "drawingmult",
                                                    content,
                                                    getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"),
                                                    uploadimg
                                            );

                                            drawingmult_record
                                                    .edit()
                                                    .putString(content,conn[index].toBase64(uploadimg))
                                                    .commit();

                                            uploadimg.recycle();   //  recycle resource
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
                                            txtExpandTimer.setText(String.format("%02d 分 %02d 秒", time / 60, time % 60));
                                            timeRemaining = l - 1000;
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        txtExpandTimer.setText(String.format("00 分 00 秒"));
                                        new AlertDialog.Builder(DrawingmultActivity.this)
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
                                                                        "drawingmult",
                                                                        currentTimer.toString(),
                                                                        getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                                                                );

                                                                Log.d("剩餘時間(sp)",(currentTimer).toString());
                                                                getSharedPreferences("member", MODE_PRIVATE)
                                                                        .edit()
                                                                        .putString("drawingmult",currentTimer.toString())
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
                                                int count = adapter.getCount();
                                                // create datalist and upload data to server
                                                ConnServer[] conn = new ConnServer[count];
                                                for (int index = RecordLength; index < count; index++) {
                                                    // get adapter info.
                                                    String content = adapter.getItem(index).getName();
                                                    Bitmap uploadimg = adapter.getItem(index).getImage();
                                                    // connect to Server
                                                    conn[index] = new ConnServer(
                                                            "drawingmult",
                                                            content,
                                                            getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"),
                                                            uploadimg
                                                    );

                                                    drawingmult_record
                                                            .edit()
                                                            .putString(content,conn[index].toBase64(uploadimg))
                                                            .commit();

                                                    uploadimg.recycle();   //  recycle resource
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
            Long ltest = Long.parseLong(getSharedPreferences("member", MODE_PRIVATE).getString("drawingmult", "null"));
            Log.d("一筆畫遊戲_讀秒",ltest.toString());

            TIME = (Long.parseLong(getSharedPreferences("member", MODE_PRIVATE).getString("drawingmult", "null"))+1) * 1000L;

            timer = new DrawingmultActivity.MyCountDownTimer(TIME, INTERVAL);
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
                txtExpandTimer.setText(String.format("%02d 分 %02d 秒", time / 60, time % 60));
                timeRemaining = l - 1000;
            }
        }

        // if timer finish
        @Override
        public void onFinish() {
            txtExpandTimer.setText(String.format("00 分 00 秒"));
            new AlertDialog.Builder(DrawingmultActivity.this)
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
                                            "drawingmult",
                                            currentTimer.toString(),
                                            getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                                    );

                                    Log.d("剩餘時間(sp)",(currentTimer).toString());
                                    getSharedPreferences("member", MODE_PRIVATE)
                                            .edit()
                                            .putString("drawingmult",currentTimer.toString())
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
                    int count = adapter.getCount();
                    // create datalist and upload data to server
                    ConnServer[] conn = new ConnServer[count];
                    for (int index = RecordLength; index < count; index++) {
                        // get adapter info.
                        String content = adapter.getItem(index).getName();
                        Bitmap uploadimg = adapter.getItem(index).getImage();
                        // connect to Server
                        conn[index] = new ConnServer(
                                "drawingmult",
                                content,
                                getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"),
                                uploadimg
                        );
                        drawingmult_record
                                .edit()
                                .putString(content,conn[index].toBase64(uploadimg))
                                .commit();

                        uploadimg.recycle();   //  recycle resource
                    }
                }
            };
            thread.start();
        }
    }

    // draw of touch event
    private View.OnTouchListener touch = new View.OnTouchListener() {
        float startX;
        float startY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    if (baseBitmap == null) {
                        baseBitmap = Bitmap.createBitmap(imgvExpand.getWidth(), imgvExpand.getHeight(), Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(baseBitmap);
                        canvas.drawColor(0xfffffff0);
                    }
                    startX = event.getX();
                    startY = event.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float stopX = event.getX();
                    float stopY = event.getY();
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                    startX = event.getX();
                    startY = event.getY();
                    imgvExpand.setImageBitmap(baseBitmap);
                    break;

                case MotionEvent.ACTION_UP:
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    // list context class
    public class listContext {
        private Bitmap image;
        private String name;

        public listContext(Bitmap image, String name) {
            this.image = image;
            this.name = name;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Bitmap getImage() {
            return image;
        }

        public String getName() {
            return name;
        }

    }

    // my adapter
    public class myAdapter extends BaseAdapter {

        // inflater context
        private LayoutInflater mInflater;
        // data context list
        private List<listContext> mdatas;

        public myAdapter(Context context, List<listContext> listcontext) {
            mInflater = LayoutInflater.from(context);
            this.mdatas = listcontext;
        }

        @Override
        public int getCount() {
            return mdatas.size();
        }

        @Override
        public listContext getItem(int position) {
            return mdatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mdatas.indexOf(getItem(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // set holder
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.imgitem, null);
                holder = new ViewHolder(
                        (ImageView) convertView.findViewById(R.id.imgImgDraw),
                        (TextView) convertView.findViewById(R.id.txtImgName)
                );
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // set convertview with holder
            listContext context = (listContext) getItem(position);
            holder.imgImgDraw.setImageBitmap(context.getImage());
            holder.txtImgName.setText(context.getName());
            return convertView;
        }

        // holder structure
        private class ViewHolder {
            ImageView imgImgDraw;
            TextView txtImgName;

            public ViewHolder(ImageView imgImgDraw, TextView txtImgName) {
                this.imgImgDraw = imgImgDraw;
                this.txtImgName = txtImgName;
            }
        }
    }

    // resize image
    public Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    // guide dialog view
    private void guideView(){
        // get whether guideSet is set
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null) guideSet = extras.getBoolean("guideSet");
        this.setResult(RESULT_OK,intent);

        // if first time, show the guide
        if(guideSet==false){
            // set guide dialog view
            final Dialog dialog = new Dialog(this,R.style.Dialog_Fullscreen);
            dialog.setContentView(R.layout.dialog_guide);
            // animation start
            ImageView iv_click = (ImageView)dialog.findViewById(R.id.iv_click);
            ImageView iv_drawable = (ImageView)dialog.findViewById(R.id.iv_drawable);
            animationStart(iv_click,iv_drawable);
            // end dialog view
            ImageView iv_gotit = (ImageView)dialog.findViewById(R.id.iv_gotit);
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
    private void animationStart(ImageView iv_click, ImageView iv_drawable){
        // animation route
        Animation am_click = new TranslateAnimation(Animation.RELATIVE_TO_SELF,-0.95f,Animation.RELATIVE_TO_SELF,3f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
        am_click.setDuration(2500);
        am_click.setRepeatCount(-1);
        am_click.setStartOffset(500);
        iv_click.setAnimation(am_click);
        am_click.start();

        Animation am_drawable = new TranslateAnimation(Animation.RELATIVE_TO_SELF,-1f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f,Animation.RELATIVE_TO_SELF,0f);
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
