package com.example.yafun.thinkingapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class AssociationrulesActivity extends AppCompatActivity {

    private boolean ifFinished = false;
    private boolean guideSet;

    // declare variable
    private EditText edtName;
    private Button btnOk, btnClr;
    private TextView txtAttributeTimer;
    private CountDownTimer timer;

    private ImageView imgvA, imgvB, imgvC, imgvD, imgvE;
    private LinearLayout lLayoutA, lLayoutB, lLayoutC, lLayoutD, lLayoutE;
    private boolean clickA = false, clickB = false, clickC = false, clickD = false, clickE = false;
    private int count = 0;
    Dictionary dict = new Hashtable();

    private TextView txtListName, txtListSelect;
    private LinearLayout llList;
    private ListView lvAttribute;
    // list arr for save data
    ArrayList<listContext> mlist = new ArrayList<listContext>();
    // declare my adapter
    private myAdapter adapter;

    //timer count
    private long TIME = 601 * 1000L;
    private final long INTERVAL = 1000L;

    // timer state
    private boolean isPaused = false;
    private long timeRemaining = 0;


    private ConnServer connUpdate = new ConnServer();
    private int RecordLength = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view set title
        setContentView(R.layout.drawerlayout_attribute);
        setTitle("屬性聯想遊戲"); //associationrulestest

        // check guide dialog then start timer
        guideView();

        // set variable value
        edtName = (EditText) findViewById(R.id.edtAttributeName);
        btnOk = (Button) findViewById(R.id.btnAttributeOk);
        btnClr = (Button) findViewById(R.id.btnAttributeClr);
        txtAttributeTimer = (TextView) findViewById(R.id.txtAttributeTimer);

        imgvA = (ImageView) findViewById(R.id.imgA);
        imgvB = (ImageView) findViewById(R.id.imgB);
        imgvC = (ImageView) findViewById(R.id.imgC);
        imgvD = (ImageView) findViewById(R.id.imgD);
        imgvE = (ImageView) findViewById(R.id.imgE);
        lLayoutA = (LinearLayout) findViewById(R.id.lLayoutA);
        lLayoutB = (LinearLayout) findViewById(R.id.lLayoutB);
        lLayoutC = (LinearLayout) findViewById(R.id.lLayoutC);
        lLayoutD = (LinearLayout) findViewById(R.id.lLayoutD);
        lLayoutE = (LinearLayout) findViewById(R.id.lLayoutE);

        // set random image
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ArrayList<String> random_list = randomSetImage();
                    if (random_list != null) Log.d("thread_try", "Successed");
                    else Log.d("thread_catch", "Failed");
                    dict.put("A", "A");
                    dict.put("B", "B");
                    dict.put("C", "C");
                    dict.put("D", "D");
                    dict.put("E", "E");

                    /*Picasso.get().load("http://140.122.91.218/thinkingapp/associationrulestest/image/" + dict.get("A") + ".png").into(imgvA);
                    Picasso.get().load("http://140.122.91.218/thinkingapp/associationrulestest/image/" + dict.get("B") + ".png").into(imgvB);
                    Picasso.get().load("http://140.122.91.218/thinkingapp/associationrulestest/image/" + dict.get("C") + ".png").into(imgvC);
                    Picasso.get().load("http://140.122.91.218/thinkingapp/associationrulestest/image/" + dict.get("D") + ".png").into(imgvD);
                    Picasso.get().load("http://140.122.91.218/thinkingapp/associationrulestest/image/" + dict.get("E") + ".png").into(imgvE);*/

                    Picasso.get().load("http://140.122.91.218/thinkingapp/associationrulestest/image/" + dict.get("A") + ".png").into(imgvA);
                    Picasso.get().load("http://140.122.91.218/thinkingapp/associationrulestest/image/" + dict.get("B") + ".png").into(imgvB);
                    Picasso.get().load("http://140.122.91.218/thinkingapp/associationrulestest/image/" + dict.get("C") + ".png").into(imgvC);
                    Picasso.get().load("http://140.122.91.218/thinkingapp/associationrulestest/image/" + dict.get("D") + ".png").into(imgvD);
                    Picasso.get().load("http://140.122.91.218/thinkingapp/associationrulestest/image/" + dict.get("E") + ".png").into(imgvE);
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });
        thread.run();

        txtListName = (TextView) findViewById(R.id.txtListName);
        txtListSelect = (TextView) findViewById(R.id.txtListSelect);
        llList = (LinearLayout) findViewById(R.id.llList);
        lvAttribute = (ListView) findViewById(R.id.lvAttribute);
        // add title to mlist
        ArrayList<String> initial = new ArrayList<String>() {{
            add("選擇");
        }};
        mlist.add(new listContext("名稱", initial));
        // new adapter with context and set
        adapter = new myAdapter(AssociationrulesActivity.this, mlist);
        lvAttribute.setAdapter(adapter);


        String tmpString[] = this.connUpdate.PersonalRecord("associationrulestest", getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"));
        if (tmpString != null) {
            this.RecordLength = tmpString.length;

            for (int i = 0; i < tmpString.length; i++) {

                ArrayList<String> choose_list = new ArrayList<String>();
                Log.d("record_testing", tmpString[i]);    //testing

                String test = tmpString[i].replace("[", "").replace("]", "").replace("\"", "");
                String[] test2 = test.split(",");
                for (int k = 0; k < test2.length; k++) {
                    Log.d("record_testing", test2[k]);    //testing

                    if (k > 0) {
                        String tmpStr = "";
                        tmpStr=test2[k];
                        //choose_list.add(test2[k]);
                        choose_list.add(tmpStr);
                    }
                }
                mlist.add(new listContext(test2[0], choose_list));


                //listData.add(tmpString[i]);
                //arrayAdapter.notifyDataSetChanged();
            }
        }


        // img click action
        imgvA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickA == false) {
                    lLayoutA.setBackgroundColor(0xffaaaaaa);
                    clickA = true;
                    count++;
                } else {
                    lLayoutA.setBackgroundColor(0x00ffffff);
                    clickA = false;
                    count--;
                }

            }
        });

        imgvB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickB == false) {
                    lLayoutB.setBackgroundColor(0xffaaaaaa);
                    clickB = true;
                    count++;
                } else {
                    lLayoutB.setBackgroundColor(0x00ffffff);
                    clickB = false;
                    count--;
                }
            }
        });

        imgvC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickC == false) {
                    lLayoutC.setBackgroundColor(0xffaaaaaa);
                    clickC = true;
                    count++;
                } else {
                    lLayoutC.setBackgroundColor(0x00ffffff);
                    clickC = false;
                    count--;
                }
            }
        });

        imgvD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickD == false) {
                    lLayoutD.setBackgroundColor(0xffaaaaaa);
                    clickD = true;
                    count++;
                } else {
                    lLayoutD.setBackgroundColor(0x00ffffff);
                    clickD = false;
                    count--;
                }
            }
        });

        imgvE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickE == false) {
                    lLayoutE.setBackgroundColor(0xffaaaaaa);
                    clickE = true;
                    count++;
                } else {
                    lLayoutE.setBackgroundColor(0x00ffffff);
                    clickE = false;
                    count--;
                }
            }
        });

        // btnOk click
        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (count < 2) {
                    Toast.makeText(AssociationrulesActivity.this, "請選擇兩張(含)以上圖片", Toast.LENGTH_SHORT).show();
                } else if (edtName.getText().toString().equals("")) {
                    Toast.makeText(AssociationrulesActivity.this, "請輸入作品名稱", Toast.LENGTH_SHORT).show();
                } else {
                    // click then add data to mlist and change adapter
                    ArrayList<String> choose_list = new ArrayList<String>();
                    //String choose = "";
                    if (clickA == true) choose_list.add("A"); //choose = choose + "A";
                    if (clickB == true) choose_list.add("B"); //choose = choose + "B";
                    if (clickC == true) choose_list.add("C"); //choose = choose + "C";
                    if (clickD == true) choose_list.add("D"); //choose = choose + "D";
                    if (clickE == true) choose_list.add("E"); //choose = choose + "E";
                    //for(String chosen:choose_list)
                    //choose += chosen;
                    //Log.d("chosen string",choose);
                    mlist.add(new listContext(edtName.getText().toString(), choose_list));

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
                    count = 0;
                }

            }
        });

        // btnClr click
        btnClr.setOnClickListener(new Button.OnClickListener() {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Do stuff you want here
        ifFinished = true;
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
                new AlertDialog.Builder(AssociationrulesActivity.this)
                        .setMessage("確定提早交卷嗎?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            // if yes stop the timer and submit the sheet
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ///1001
                                Long currentTimer = timeRemaining / 1000;
                                Log.d("剩餘時間(秒數)",(currentTimer).toString());
                                connUpdate.updateAnwsertime(
                                        "associationrules",
                                        currentTimer.toString(),
                                        getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                                );

                                Log.d("剩餘時間(sp)",(currentTimer).toString());
                                getSharedPreferences("member", MODE_PRIVATE)
                                        .edit()
                                        .putString("associationrules",currentTimer.toString())
                                        .commit();


                                timer = null;
                                Thread thread = new Thread() {
                                    public void run() {
                                        int count = adapter.getCount() - 1;
                                        ConnServer[] conn = new ConnServer[count];
                                        for (int index = RecordLength; index < count; index++) {
                                            String content = adapter.getItem(index + 1).getName();
                                            ArrayList<String> select = adapter.getItem(index + 1).getSelect();
                                            String[] chosenImgID = new String[select.size()];
                                            for (int idIndex = 0; idIndex < select.size(); idIndex++) {
                                                String key = select.get(idIndex);
                                                String value = "" + dict.get(key);
                                                chosenImgID[idIndex] = value;
                                                Log.d("chosenID", value);
                                            }
                                            conn[index] = new ConnServer("association", content, chosenImgID,
                                                    getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"));
                                        }
                                    }
                                };
                                thread.start();
                                dialogInterface.dismiss();
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
                                            timer = null;
                                        } else {
                                            txtAttributeTimer.setText(String.format("%02d 分 %02d 秒", time / 60, time % 60));
                                            timeRemaining = l - 1000;
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        txtAttributeTimer.setText(String.format("00 分 00 秒"));
                                        new AlertDialog.Builder(AssociationrulesActivity.this)
                                                .setMessage("時間結束.。")
                                                .setPositiveButton("返回首頁", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        /// Thread for timer update
                                                        Thread timerthread = new Thread() {
                                                            public void run() {
                                                                Long currentTimer = timeRemaining / 1000;
                                                                Log.d("剩餘時間(秒數)",(currentTimer).toString());

                                                                connUpdate.updateAnwsertime(
                                                                        "associationrules",
                                                                        currentTimer.toString(),
                                                                        getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                                                                );
                                                                Log.d("剩餘時間(sp)",(currentTimer).toString());
                                                                getSharedPreferences("member", MODE_PRIVATE)
                                                                        .edit()
                                                                        .putString("associationrules",currentTimer.toString())
                                                                        .commit();
                                                            }
                                                        };
                                                        timerthread.start();

                                                        timer.cancel();
                                                        timer = null;
                                                        dialogInterface.dismiss();
                                                        finish();
                                                    }
                                                }).setCancelable(false).show();

                                        Thread thread = new Thread() {
                                            public void run() {
                                                int count = adapter.getCount() - 1;
                                                ConnServer[] conn = new ConnServer[count];
                                                for (int index = RecordLength; index < count; index++) {
                                                    String content = adapter.getItem(index + 1).getName();
                                                    ArrayList<String> select = adapter.getItem(index + 1).getSelect();
                                                    String[] chosenImgID = new String[select.size()];
                                                    for (int idIndex = 0; idIndex < select.size(); idIndex++) {
                                                        String key = select.get(idIndex);
                                                        String value = "" + dict.get(key);
                                                        chosenImgID[idIndex] = value;
                                                        Log.d("chosenID", value);
                                                    }
                                                    conn[index] = new ConnServer("association", content, chosenImgID, getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"));
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

            ///1001
            Long ltest = Long.parseLong(getSharedPreferences("member", MODE_PRIVATE).getString("associationrules", "null"));
            Log.d("關聯聯想遊戲_讀秒",ltest.toString());
            TIME = (Long.parseLong(getSharedPreferences("member", MODE_PRIVATE).getString("associationrules", "null"))+1) * 1000L;

            timer = new AssociationrulesActivity.MyCountDownTimer(TIME, INTERVAL);
        }
        timer.start();
    }

    // CountDownTimer function
    private class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long l) {
            long time = l / 1000;
            Log.d("timer", String.valueOf(time));
            if (isPaused) {
                timer.cancel();
                timer = null;
            } else {
                txtAttributeTimer.setText(String.format("%02d 分 %02d 秒", time / 60, time % 60));
                timeRemaining = l - 1000;
            }
        }
        @Override
        public void onFinish() {
            txtAttributeTimer.setText(String.format("00 分 00 秒"));
            AlertDialog.Builder finish_timer_AlertDialog = new AlertDialog.Builder(AssociationrulesActivity.this);
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
                                            "associationrules",
                                            currentTimer.toString(),
                                            getSharedPreferences("member", MODE_PRIVATE).getString("id", "null")
                                    );

                                    Log.d("剩餘時間(sp)",(currentTimer).toString());
                                    getSharedPreferences("member", MODE_PRIVATE)
                                            .edit()
                                            .putString("associationrules",currentTimer.toString())
                                            .commit();
                                }
                            };
                            timerthread.start();

                            //timer.cancel();
                            timer = null;
                            dialogInterface.dismiss();
                            finish();
                        }
                    }).setCancelable(false);

            if (!ifFinished) {  // if activity isn't destroy
                finish_timer_AlertDialog.show();

                // submit the sheet
                Thread thread = new Thread() {
                    public void run() {
                        int count = adapter.getCount() - 1;
                        ConnServer[] conn = new ConnServer[count];
                        for (int index = RecordLength; index < count; index++) {
                            String content = adapter.getItem(index + 1).getName();
                            ArrayList<String> select = adapter.getItem(index + 1).getSelect();
                            String[] chosenImgID = new String[select.size()];
                            for (int idIndex = 0; idIndex < select.size(); idIndex++) {
                                String key = select.get(idIndex);
                                String value = "" + dict.get(key);
                                chosenImgID[idIndex] = value;
                                Log.d("chosenID", value);
                            }
                            conn[index] = new ConnServer("association", content, chosenImgID, getSharedPreferences("member", MODE_PRIVATE).getString("id", "null"));
                        }
                    }
                };
                thread.start();
            }
        }
    }

    // list context class
    public class listContext {
        private String name;
        private ArrayList<String> select;

        public listContext(String name, ArrayList<String> select) {
            this.name = name;
            this.select = select;
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

        public ArrayList<String> getSelect() {
            return select;
        }
    }

    // my adapter
    public class myAdapter extends BaseAdapter {

        // inflater context
        private LayoutInflater mInflater;
        // data context list
        private ArrayList<listContext> mdatas;

        public myAdapter(Context context, ArrayList<listContext> listcontext) {
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
                convertView = mInflater.inflate(R.layout.listitem, null);
                holder = new ViewHolder((TextView) convertView.findViewById(R.id.txtListName),
                        (TextView) convertView.findViewById(R.id.txtListSelect));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // set convertview with holder
            listContext context = (listContext) getItem(position);
            holder.txtListName.setText(context.getName());
            String temp = "";
            for (String str_sel : context.getSelect())
                temp += str_sel;
            holder.txtListSelect.setText(temp);
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

    // post random image number
    private ArrayList<String> randomSetImage() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String webRequest = null;
        ArrayList<String> num_list = new ArrayList<String>();

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://140.122.91.218/thinkingapp/associationrulestest/fileCount.php");

        try {
            HttpResponse response = client.execute(request);
            HttpEntity resEntity = response.getEntity();
            webRequest = EntityUtils.toString(resEntity);
            Log.d("webRequest", webRequest);

            JSONObject obj = new JSONObject(webRequest);  // parse web request
            for (int i = 1; i <= 5; i++) {
                num_list.add(obj.getString("random" + i));
            }
            return num_list;

        } catch (java.io.IOException e) {
            Log.d("IOException", e.getMessage());
        } catch (org.json.JSONException e) {
            Log.d("JSON Error", e.getMessage());
        }
        return null;
    }
}
