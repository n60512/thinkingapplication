package com.example.yafun.thinkingapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // declare variable
    private Boolean againConnect = false;
    private Boolean loggedin = false;
    private Boolean guideSet = false;
    private Button btnDraw, btnAttribute, btnAssociate, btnExpand;
    private TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // test

        // if not logged in, start login activity
        if (!loggedin) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("connNetwork", againConnect);
            startActivityForResult(intent, 111);
        }
        // else only check network
        else {
            // check whether connect to network
            againConnect = networkCheck();
        }

        // set variable value
        btnDraw = (Button) findViewById(R.id.btnDraw);
        btnAttribute = (Button) findViewById(R.id.btnAttribute);
        btnAssociate = (Button) findViewById(R.id.btnAssociate);
        btnExpand = (Button) findViewById(R.id.btnExpand);
        txtName = (TextView) findViewById(R.id.txtName);

        // if btnDraw click
        btnDraw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btnDraw) {
                    // show alert dialog
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("一筆畫遊戲作答說明")
                            .setMessage("【 作答時間 : 8 分鐘 】\n" +
                                    "圖像作答需一筆畫完成，\n並且在下方為圖片輸入名稱。\n" +
                                    "時間內可連續作答，作答紀錄均會在左側的作答歷程中顯示。\n")
                            .setPositiveButton("開始測驗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Boolean connected = networkCheck();
                                    if (connected == true) {
                                        Intent intent = new Intent(MainActivity.this, DrawActivity.class);
                                        intent.putExtra("guideSet", guideSet);
                                        startActivityForResult(intent, 123);
                                    }
                                }
                            }).show();
                }
            }
        });

        // if btnAttribute click
        btnAttribute.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btnAttribute) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("屬性聯想遊戲作答說明")
                            .setMessage("【 作答時間 : 10 分鐘 】\n" +
                                    "選出兩張(含)以上相關連性質的圖片，並輸入相關的文字敘述。\n" +
                                    "例如 : 選出【衣服】和【雞蛋】，聯想到\"顏色間具有相同性質\"，" +
                                    "即可輸入【白色】，並送出作答。\n" +
                                    "時間內可連續作答，作答紀錄均會在左側的作答歷程中顯示。\n")
                            .setPositiveButton("開始測驗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Boolean connected = networkCheck();
                                    if (connected == true) {
                                        Intent intent = new Intent(MainActivity.this, AttributeActivity.class);
                                        intent.putExtra("guideSet", guideSet);
                                        startActivityForResult(intent, 123);
                                    }
                                }
                            }).show();
                }
            }
        });

        // if btnAssociate click
        btnAssociate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btnAssociate) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("簡圖聯想遊戲作答說明")
                            .setMessage("【 作答時間 : 8 分鐘 】 \n" +
                                    "針對題目給予的圖片輸入文字敘述，\n" +
                                    "在時間內可連續作答，每次的作答紀錄均會在左側的作答歷程中顯示。\n")
                            .setPositiveButton("開始測驗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Boolean connected = networkCheck();
                                    if (connected == true) {
                                        Intent intent = new Intent(MainActivity.this, AssociateActivity.class);
                                        intent.putExtra("guideSet", guideSet);
                                        startActivityForResult(intent, 123);
                                    }
                                }
                            }).show();
                }
            }
        });

        // if btnExpand click
        btnExpand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btnExpand) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("圖繪展開遊戲作答說明")
                            .setMessage("【 測驗時間 : 20 分鐘 】\n" +
                                    "完成圖像的作答，\n並且在下方為圖像輸入作品名稱。\n" +
                                    "作答時間之內可連續作答，每次的作答紀錄均會在左側的作答歷程中顯示。\n")
                            .setPositiveButton("開始測驗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Boolean connected = networkCheck();
                                    if (connected == true) {
                                        Intent intent = new Intent(MainActivity.this, ExpandActivity.class);
                                        intent.putExtra("guideSet", guideSet);
                                        startActivityForResult(intent, 123);
                                    }
                                }
                            }).show();
                }
            }
        });
    }

    // set toobar's menu ( logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, "登出");
        return super.onCreateOptionsMenu(menu);
    }

    // logout action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                loggedin = false;
                if (!loggedin) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("connNetwork", againConnect);
                    startActivityForResult(intent, 111);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // check whether connected to network
    private Boolean networkCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        // if connected to network
        if (ni != null && ni.isConnected()) {
            return true;
        }
        // else can't connect
        else if (ni == null) {
            againConnect = false;
            // set alertdialog
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("連線失敗");
            alertDialog.setMessage("網路連線失敗,\n請按重試按鈕重新連線。");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("重試", null);
            // custom dialog show
            final AlertDialog alert = alertDialog.create();
            alert.show();
            // custom button function
            Button btn = alert.getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo ni = cm.getActiveNetworkInfo();
                    if (ni != null && ni.isConnected()) {
                        // if already connected to network
                        againConnect = true;
                        alert.dismiss();
                    }
                }
            });
        }
        if (againConnect == true) return true;
        else return false;
    }

    // get activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check request&result code
        if (requestCode == 111 && resultCode == RESULT_OK) {
            // get data and set
            //String name = data.getStringExtra("username");
            txtName.setText("Hi, " + getSharedPreferences("member", MODE_PRIVATE).getString("id", "null") + " 同學");
            loggedin = true;
            // get whether connected to network
            againConnect = data.getExtras().getBoolean("connNetwork");
            if (againConnect == false) againConnect = networkCheck();
        } else if (requestCode == 123 && resultCode == RESULT_OK) {
            // set already show the guide dialog
            guideSet = true;
        }
    }

}
