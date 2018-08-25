package com.example.yafun.thinkingapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.media.Image;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

public class DrawActivity extends AppCompatActivity {

    private Boolean guideSet;

    // declare variable
    private EditText edtName;
    private Button btnOk, btnClr;
    private TextView txtDrawTimer;
    private CountDownTimer timer;

    private ImageView imgvDraw;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    boolean isTouch = false;

    private ImageView imgImgDraw;
    private TextView txtImgName;
    private LinearLayout llImg;
    private ListView lvDraw;
    List<listContext> mlist = new ArrayList<listContext>();
    private myAdapter adapter;

    // timer count
    private final long TIME = 481 * 1000L;
    private final long INTERVAL = 1000L;

    // timer state
    private boolean isPaused = false;
    private long timeRemaining = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view set title
        setContentView(R.layout.drawerlayout_draw);
        setTitle("一筆畫遊戲");

        // check guide dialog then start timer
        guideView();

        // set variable value
        edtName = (EditText) findViewById(R.id.edtDrawName);
        btnOk = (Button) findViewById(R.id.btnDrawOk);
        btnClr = (Button) findViewById(R.id.btnDrawClr);
        txtDrawTimer = (TextView) findViewById(R.id.txtDrawTimer);
        imgvDraw = (ImageView) findViewById(R.id.imgvDraw);

        imgImgDraw = (ImageView) findViewById(R.id.imgImgDraw);
        txtImgName = (TextView) findViewById(R.id.txtImgName);
        llImg = (LinearLayout) findViewById(R.id.llImg);
        lvDraw = (ListView) findViewById(R.id.lvDraw);

        // new adapter with context and set
        adapter = new myAdapter(DrawActivity.this, mlist);
        lvDraw.setAdapter(adapter);

        // paint initialize
        paint = new Paint();
        paint.setStrokeWidth(6);
        paint.setColor(Color.GRAY);
        imgvDraw.setOnTouchListener(touch);

        // btnOk click
        btnOk.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (baseBitmap == null) {
                    Toast.makeText(DrawActivity.this, "請完成作畫", Toast.LENGTH_SHORT).show();
                } else if (edtName.getText().toString().equals("")) {
                    Toast.makeText(DrawActivity.this, "請輸入作品名稱", Toast.LENGTH_SHORT).show();
                }
                // clear all
                else if (baseBitmap != null && !TextUtils.isEmpty(edtName.getText().toString())) {
                    mlist.add(new listContext(baseBitmap, edtName.getText().toString()));
                    adapter.notifyDataSetChanged();

                    //clear
                    edtName.setText("");
                    baseBitmap = Bitmap.createBitmap(imgvDraw.getWidth(), imgvDraw.getHeight(), Bitmap.Config.ARGB_8888);
                    baseBitmap = resizeImage(baseBitmap, 320, 360);
                    canvas = new Canvas(baseBitmap);
                    canvas.drawColor(0xfffffff0);
                    imgvDraw.setImageBitmap(baseBitmap);
                    baseBitmap = null;
                    isTouch = false;
                }
            }
        });


        // btnClr click
        btnClr.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                isTouch = false;
                edtName.setText("");
                if (baseBitmap != null) {
                    baseBitmap = Bitmap.createBitmap(imgvDraw.getWidth(), imgvDraw.getHeight(), Bitmap.Config.ARGB_8888);
                    baseBitmap = resizeImage(baseBitmap, 320, 360);
                    canvas = new Canvas(baseBitmap);
                    canvas.drawColor(0xfffffff0);
                    imgvDraw.setImageBitmap(baseBitmap);
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
                new AlertDialog.Builder(DrawActivity.this)
                        .setMessage("確定提早交卷嗎?")
                        // submit sheet
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            // if yes stop the timer and submit the sheet
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                timer.cancel();
                                timer = null;
                                // commit content to database
                                Thread thread = new Thread() {
                                    public void run() {
                                        int count = adapter.getCount();
                                        ConnServer[] conn = new ConnServer[count];
                                        for (int index = 0; index < count; index++) {
                                            String content = adapter.getItem(index).getName();
                                            Bitmap uploadimg = adapter.getItem(index).getImage();
                                            // connect to Server
                                            conn[index] = new ConnServer("drawing", content, "test01", uploadimg);
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
                                            timer.cancel();
                                        } else {
                                            txtDrawTimer.setText(String.format("%02d 分 %02d 秒", time / 60, time % 60));
                                            timeRemaining = l - 1000;
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        txtDrawTimer.setText(String.format("00 分 00 秒"));
                                        new AlertDialog.Builder(DrawActivity.this)
                                                .setMessage("時間結束。")
                                                .setPositiveButton("返回首頁", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        timer.cancel();
                                                        timer = null;
                                                        finish();
                                                    }
                                                }).setCancelable(false).show();
                                        // commit content to database
                                        Thread thread = new Thread() {
                                            public void run() {
                                                int count = adapter.getCount();
                                                ConnServer[] conn = new ConnServer[count];
                                                for (int index = 0; index < count; index++) {
                                                    String content = adapter.getItem(index).getName();
                                                    Bitmap uploadimg = adapter.getItem(index).getImage();
                                                    // connect to Server
                                                    conn[index] = new ConnServer("drawing", content, "test01", uploadimg);
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
            timer = new MyCountDownTimer(TIME, INTERVAL);
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
                timer.cancel();
            } else {
                txtDrawTimer.setText(String.format("%02d 分 %02d 秒", time / 60, time % 60));
                timeRemaining = l - 1000;
            }
        }

        // if timer finish
        @Override
        public void onFinish() {
            txtDrawTimer.setText(String.format("00 分 00 秒"));
            new AlertDialog.Builder(DrawActivity.this)
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
            // commit content to database
            Thread thread = new Thread() {
                public void run() {
                    int count = adapter.getCount();
                    ConnServer[] conn = new ConnServer[count];
                    for (int index = 0; index < count; index++) {
                        String content = adapter.getItem(index).getName();
                        Bitmap uploadimg = adapter.getItem(index).getImage();
                        // connect to Server
                        conn[index] = new ConnServer("drawing", content, "test01", uploadimg);
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
                        baseBitmap = Bitmap.createBitmap(imgvDraw.getWidth(), imgvDraw.getHeight(), Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(baseBitmap);
                        canvas.drawColor(0xfffffff0);
                    }
                    startX = event.getX();
                    startY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isTouch != true) {
                        float stopX = event.getX();
                        float stopY = event.getY();
                        canvas.drawLine(startX, startY, stopX, stopY, paint);
                        startX = event.getX();
                        startY = event.getY();
                        imgvDraw.setImageBitmap(baseBitmap);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    isTouch = true;
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
            mdatas = listcontext;
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
                holder = new ViewHolder((ImageView) convertView.findViewById(R.id.imgImgDraw),
                        (TextView) convertView.findViewById(R.id.txtImgName));
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
