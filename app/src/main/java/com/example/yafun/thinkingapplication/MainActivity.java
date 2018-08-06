package com.example.yafun.thinkingapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // declare variable
    private  Boolean loggedin = false;
    private Button btnDraw,btnAttribute,btnAssociate,btnExpand;
    private TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if not logged in, start login activity
        if (!loggedin){
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivityForResult(intent,111);
        }

        // set variable value
        btnDraw = (Button)findViewById(R.id.btnDraw);
        btnAttribute = (Button)findViewById(R.id.btnAttribute);
        btnAssociate = (Button)findViewById(R.id.btnAssociate);
        btnExpand = (Button)findViewById(R.id.btnExpand);
        txtName = (TextView)findViewById(R.id.txtName);

        // if btnDraw click
        btnDraw.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.btnDraw){
                    // show alert dialog
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("一筆畫遊戲作答說明")
                            .setMessage("此測驗在\"8分鐘\"內須完成圖像的作答，並且為圖片輸入作品名稱。\n" +
                                        "圖像作答需一筆畫完成，並且不能超過作圖區。\n" +
                                        "在作答時間之內可連續作答，每次的作答紀錄均會在右側的作答歷程中顯示。\n" +
                                        "結束作答可在頁面上按送出按鍵繳交作答，繼續作畫。")
                            .setPositiveButton("開始測驗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this,DrawActivity.class);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        });

        // if btnAttribute click
        btnAttribute.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.btnAttribute){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("屬性聯想遊戲作答說明")
                            .setMessage("此測驗在\"10分鐘\"選出兩張(含)以上相關連性質的圖片。\n" +
                                        "在下方輸入相關的文字敘述，作答完按下存檔送出。\n" +
                                        "例如:在圖片中選出【衣服】和【雞蛋】，聯想到\"顏色間具有相同性質\"\n" +
                                        "在下方就可以輸入【白色】，作答完按下存檔即可送出。\n" +
                                        "在作答時間之內可連續作答，每次的作答紀錄均會在下側的作答歷程中顯示。\n" +
                                        "結束作答可在頁面上按\"送出\"按鍵繳交答案，繼續作答。")
                            .setPositiveButton("開始測驗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this,AttributeActivity.class);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        });

        // if btnAssociate click
        btnAssociate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.btnAssociate){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("簡圖聯想遊戲作答說明")
                            .setMessage("此測驗在\"8分鐘內\"針對題目給予的圖片輸入文字作答\n" +
                                        "在作答時間之內可連續作答，每次的作答紀錄均會在右側的作答歷程中顯示。\n" +
                                        "若提早結束作答可在頁面上按\"送出\"按鍵繳交作答。")
                            .setPositiveButton("開始測驗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this,AssociateActivity.class);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        });

        // if btnExpand click
        btnExpand.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.btnExpand){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("圖繪展開遊戲作答說明")
                            .setMessage("此測驗在\"20分鐘內\"須完成圖像的作答，並且為圖片\"輸入作品名稱\"。\n" +
                                        "在作答時間之內可連續作答，每次的作答紀錄均會在右側的作答歷程中顯示。\n" +
                                        "結束作答可在頁面上按\"送出\"按鍵繳交作答，繼續作畫。")
                            .setPositiveButton("開始測驗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this,ExpandActivity.class);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        });
    }

    // set toobar's menu ( logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,"登出");
        return super.onCreateOptionsMenu(menu);
    }

    // logout action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case 1:
                loggedin = false;
                finish();
                if (!loggedin){
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivityForResult(intent,111);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // get activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check request&result code
        if (requestCode == 111 && resultCode == RESULT_OK){

                // get data and set
                String name = data.getStringExtra("username");
                txtName.setText("Hi, " + name + " 同學");
        }
    }
}
