package com.example.yafun.thinkingapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExpandActivity extends AppCompatActivity {

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
    private final long TIME = 1201*1000L;
    private final long INTERVAL = 1000L;

    // timer state
    private boolean isPaused = false;
    private long timeRemaining = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view set title
        setContentView(R.layout.drawerlayout_expand);
        setTitle("圖繪展開遊戲");

        // set variable value
        edtName = (EditText)findViewById(R.id.edtExpandName);
        btnOk = (Button)findViewById(R.id.btnExpandOk);
        btnClr = (Button)findViewById(R.id.btnExpandClr);
        txtExpandTimer = (TextView)findViewById(R.id.txtExpandTimer);
        imgvExpand = (ImageView)findViewById(R.id.imgvExpand);

        imgImgDraw = (ImageView)findViewById(R.id.imgImgDraw);
        txtImgName = (TextView)findViewById(R.id.txtImgName);
        llImg = (LinearLayout)findViewById(R.id.llImg);
        lvExpand = (ListView)findViewById(R.id.lvExpand);

        // new adapter with context and set
        adapter = new myAdapter(ExpandActivity.this,mlist);
        lvExpand.setAdapter(adapter);

        // start timer
        startTimer();

        // paint initialize
        paint = new Paint();
        paint.setStrokeWidth(6);
        paint.setColor(Color.GRAY);
        imgvExpand.setOnTouchListener(touch);

        btnOk.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(baseBitmap==null){
                    Toast.makeText(ExpandActivity.this,"請完成作畫",Toast.LENGTH_SHORT).show();
                }
                else if(edtName.getText().toString().equals("")){
                    Toast.makeText(ExpandActivity.this,"請輸入作品名稱",Toast.LENGTH_SHORT).show();
                }
                // clear all
                else if(baseBitmap!=null&&!TextUtils.isEmpty(edtName.getText().toString())){
                    mlist.add(new listContext(baseBitmap, edtName.getText().toString()));
                    adapter.notifyDataSetChanged();

                    edtName.setText("");
                    baseBitmap = Bitmap.createBitmap(imgvExpand.getWidth(),imgvExpand.getHeight(),Bitmap.Config.ARGB_8888);
                    baseBitmap = resizeImage(baseBitmap,320,360);
                    canvas = new Canvas(baseBitmap);
                    canvas.drawColor(0xfffffff0);
                    imgvExpand.setImageBitmap(baseBitmap);
                    baseBitmap = null;
                }
            }
        });

        // btnClr click
        btnClr.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                edtName.setText("");
                if(baseBitmap!=null){
                    baseBitmap = Bitmap.createBitmap(imgvExpand.getWidth(),imgvExpand.getHeight(),Bitmap.Config.ARGB_8888);
                    baseBitmap = resizeImage(baseBitmap,320,360);
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
                new AlertDialog.Builder(ExpandActivity.this)
                        .setMessage("確定提早交卷嗎?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            // if yes stop the timer and submit the sheet
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                timer.cancel();
                                timer = null;
                                // commit content to database
                                Thread thread = new Thread(){
                                    public void run(){
                                        int count = adapter.getCount();
                                        ConnServer[] conn = new ConnServer[count];
                                        for(int index=0; index<count; index++){
                                            String content = adapter.getItem(index).getName();
                                            conn[index] = new ConnServer("drawingmult",content,"test01");
                                            String imgID = conn[index].getImageID();
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
                                            txtExpandTimer.setText(String.format("%02d 分 %02d 秒", time / 60, time % 60));
                                            timeRemaining = l - 1000;
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        txtExpandTimer.setText(String.format("00 分 00 秒"));
                                        new AlertDialog.Builder(ExpandActivity.this)
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
            timer = new ExpandActivity.MyCountDownTimer(TIME,INTERVAL);
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
                txtExpandTimer.setText(String.format("%02d 分 %02d 秒",time/60,time%60));
                timeRemaining = l-1000;
            }
        }

        // if timer finish
        @Override
        public void onFinish() {
            txtExpandTimer.setText(String.format("00 分 00 秒"));
            new AlertDialog.Builder(ExpandActivity.this)
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

    // draw of touch event
    private View.OnTouchListener touch = new View.OnTouchListener() {
        float startX;
        float startY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){

                case MotionEvent.ACTION_DOWN:
                    if(baseBitmap == null){
                        baseBitmap = Bitmap.createBitmap(imgvExpand.getWidth(),imgvExpand.getHeight(),Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(baseBitmap);
                        canvas.drawColor(0xfffffff0);
                    }
                    startX = event.getX();
                    startY = event.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float stopX = event.getX();
                    float stopY = event.getY();
                    canvas.drawLine(startX,startY,stopX,stopY,paint);
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
    public class listContext{
        private Bitmap image;
        private String name;
        public listContext(Bitmap image,String name){
            this.image = image;
            this.name = name;
        }

        public void setImage(Bitmap image){
            this.image = image;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Bitmap getImage(){
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

        public myAdapter(Context context, List<listContext> listcontext){
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
            if(convertView==null){
                convertView = mInflater.inflate(R.layout.imgitem,null);
                holder = new ViewHolder((ImageView)convertView.findViewById(R.id.imgImgDraw),
                        (TextView)convertView.findViewById(R.id.txtImgName));
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }
            // set convertview with holder
            listContext context = (listContext)getItem(position);
            holder.imgImgDraw.setImageBitmap(context.getImage());
            holder.txtImgName.setText(context.getName());
            return convertView;
        }

        // holder structure
        private class ViewHolder{
            ImageView imgImgDraw;
            TextView txtImgName;
            public ViewHolder(ImageView imgImgDraw, TextView txtImgName){
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

    // intercept back
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            // no action
            return true;
        }
        return false;
    }
}
