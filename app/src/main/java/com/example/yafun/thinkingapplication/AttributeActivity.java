package com.example.yafun.thinkingapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AttributeActivity extends AppCompatActivity {

    // declare variable
    private EditText edtName;
    private Button btnOk, btnClr;
    private TextView txtAttributeTimer;
    private CountDownTimer timer;

    private LinearLayout lLayoutA,lLayoutB,lLayoutC,lLayoutD,lLayoutE;
    private ImageView imgA, imgB, imgC, imgD, imgE;
    private boolean clickA = false, clickB = false, clickC = false, clickD = false, clickE = false;

    private TextView txtListName, txtListSelect;
    private LinearLayout llList;
    private ListView lvAttribute;
    // list arr for save data
    List<listContext> mlist = new ArrayList<listContext>();
    // declare my adapter
    private myAdapter adapter;

    //timer count
    private final long TIME = 601*1000L;
    private final long INTERVAL = 1000L;

    // timer state
    private boolean isPaused = false;
    private long timeRemaining = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view set title
        setContentView(R.layout.drawerlayout_attribute);
        setTitle("屬性聯想遊戲");

        // set variable value
        edtName = (EditText)findViewById(R.id.edtAttributeName);
        btnOk = (Button)findViewById(R.id.btnAttributeOk);
        btnClr = (Button)findViewById(R.id.btnAttributeClr);
        txtAttributeTimer = (TextView)findViewById(R.id.txtAttributeTimer);

        lLayoutA = (LinearLayout)findViewById(R.id.lLayoutA);
        lLayoutB = (LinearLayout)findViewById(R.id.lLayoutB);
        lLayoutC = (LinearLayout)findViewById(R.id.lLayoutC);
        lLayoutD = (LinearLayout)findViewById(R.id.lLayoutD);
        lLayoutE = (LinearLayout)findViewById(R.id.lLayoutE);
        imgA = (ImageView)findViewById(R.id.imgA);
        imgB = (ImageView)findViewById(R.id.imgB);
        imgC = (ImageView)findViewById(R.id.imgC);
        imgD = (ImageView)findViewById(R.id.imgD);
        imgE = (ImageView)findViewById(R.id.imgE);

        txtListName = (TextView)findViewById(R.id.txtListName);
        txtListSelect = (TextView)findViewById(R.id.txtListSelect);
        llList = (LinearLayout)findViewById(R.id.llList);
        lvAttribute = (ListView)findViewById(R.id.lvAttribute);
        // add title to mlist
        mlist.add(new listContext("名稱","選擇"));
        // new adapter with context and set
        adapter = new myAdapter(AttributeActivity.this,mlist);
        lvAttribute.setAdapter(adapter);

        // start timer
        startTimer();

        // img click action
        imgA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickA==false){
                    lLayoutA.setBackgroundColor(0xffaaaaaa);
                    clickA = true;
                }
                else{
                    lLayoutA.setBackgroundColor(0x00ffffff);
                    clickA = false;
                }

            }
        });

        imgB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickB==false){
                    lLayoutB.setBackgroundColor(0xffaaaaaa);
                    clickB = true;
                }
                else{
                    lLayoutB.setBackgroundColor(0x00ffffff);
                    clickB = false;
                }
            }
        });

        imgC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickC==false){
                    lLayoutC.setBackgroundColor(0xffaaaaaa);
                    clickC = true;
                }
                else{
                    lLayoutC.setBackgroundColor(0x00ffffff);
                    clickC = false;
                }
            }
        });

        imgD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickD==false){
                    lLayoutD.setBackgroundColor(0xffaaaaaa);
                    clickD = true;
                }
                else{
                    lLayoutD.setBackgroundColor(0x00ffffff);
                    clickD = false;
                }
            }
        });

        imgE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickE==false){
                    lLayoutE.setBackgroundColor(0xffaaaaaa);
                    clickE = true;
                }
                else{
                    lLayoutE.setBackgroundColor(0x00ffffff);
                    clickE = false;
                }
            }
        });

        // btnOk click
        btnOk.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                // click then add data to mlist and change adapter
                String choose = "";
                if(clickA==true) choose = choose + "A";
                if(clickB==true) choose = choose + "B";
                if(clickC==true) choose = choose + "C";
                if(clickD==true) choose = choose + "D";
                if(clickE==true) choose = choose + "E";
                mlist.add(new listContext(edtName.getText().toString(),choose));

                adapter.notifyDataSetChanged();


                // clear all
                edtName.setText("");
                clickA = false;
                clickB = false;
                clickC = false;
                clickD = false;
                clickE = false;
                lLayoutA.setBackgroundColor(0x00ffffff);
                lLayoutB.setBackgroundColor(0x00ffffff);
                lLayoutC.setBackgroundColor(0x00ffffff);
                lLayoutD.setBackgroundColor(0x00ffffff);
                lLayoutE.setBackgroundColor(0x00ffffff);

            }
        });

        // btnClr click
        btnClr.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                edtName.setText("");
                clickA = false;
                clickB = false;
                clickC = false;
                clickD = false;
                clickE = false;
                lLayoutA.setBackgroundColor(0x00ffffff);
                lLayoutB.setBackgroundColor(0x00ffffff);
                lLayoutC.setBackgroundColor(0x00ffffff);
                lLayoutD.setBackgroundColor(0x00ffffff);
                lLayoutE.setBackgroundColor(0x00ffffff);
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
                new AlertDialog.Builder(AttributeActivity.this)
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
                                timer = new CountDownTimer(millisInFuture, countDownInterval){
                                    @Override
                                    public void onTick(long l) {
                                        long time = l / 1000;
                                        if(isPaused){
                                            timer.cancel();
                                        }
                                        else{
                                            txtAttributeTimer.setText(String.format("%02d 分 %02d 秒",time/60,time%60));
                                            timeRemaining = l-1000;
                                        }
                                    }
                                    @Override
                                    public void onFinish() {
                                        txtAttributeTimer.setText(String.format("00 分 00 秒"));
                                        new AlertDialog.Builder(AttributeActivity.this)
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
            timer = new AttributeActivity.MyCountDownTimer(TIME,INTERVAL);
        }
        timer.start();
    }

    // CountDownTimer function
    private class MyCountDownTimer extends CountDownTimer {
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
                txtAttributeTimer.setText(String.format("%02d 分 %02d 秒",time/60,time%60));
                timeRemaining = l-1000;
            }
        }

        // if timer finish
        @Override
        public void onFinish() {
            txtAttributeTimer.setText(String.format("00 分 00 秒"));
            new AlertDialog.Builder(AttributeActivity.this)
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

    // list context class
    public class listContext{
        private String name;
        private String select;
        public listContext(String name,String select){
            this.name = name;
            this.select = select;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSelect(String select) {
            this.select = select;
        }

        public String getName() {
            return name;
        }

        public String getSelect() {
            return select;
        }
    }

    // my adapter
    public class myAdapter extends BaseAdapter{

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
        public Object getItem(int position) {
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
                convertView = mInflater.inflate(R.layout.listitem,null);
                holder = new ViewHolder((TextView)convertView.findViewById(R.id.txtListName),
                        (TextView)convertView.findViewById(R.id.txtListSelect));
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }
            // set convertview with holder
            listContext context = (listContext)getItem(position);
            holder.txtListName.setText(context.getName());
            holder.txtListSelect.setText(context.getSelect());
            return convertView;
        }

        // holder structure
        private class ViewHolder{
            TextView txtListName;
            TextView txtListSelect;
            public ViewHolder(TextView txtListName, TextView txtListSelect){
                this.txtListName = txtListName;
                this.txtListSelect = txtListSelect;
            }
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

}
