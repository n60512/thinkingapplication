package com.example.yafun.thinkingapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private  Boolean loggedin = false;
    private Button btnGame1,btnGame2,btnGame3,btnGame4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!loggedin){
            Intent it = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(it);
        }
        btnGame1 = (Button)findViewById(R.id.btnGame1);
        btnGame2 = (Button)findViewById(R.id.btnGame2);
        btnGame3 = (Button)findViewById(R.id.btnGame3);
        btnGame4 = (Button)findViewById(R.id.btnGame4);

        btnGame1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.btnGame1){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("一筆畫遊戲作答說明")
                            .setMessage("此測驗在\"8分鐘\"內須完成圖像的作答，並且為圖片輸入作品名稱。\n" +
                                        "圖像作答需一筆畫完成，並且不能超過作圖區。\n" +
                                        "在作答時間之內可連續作答，每次的作答紀錄均會在右側的作答歷程中顯示。\n" +
                                        "結束作答可在頁面上按送出按鍵繳交作答，繼續作畫。")
                            .setPositiveButton("開始測驗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this,Game1Activity.class);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        });

        btnGame2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.btnGame2){
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
                                    Intent intent = new Intent(MainActivity.this,Game2Activity.class);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        });

        btnGame3.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.btnGame3){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("簡圖聯想遊戲作答說明")
                            .setMessage("此測驗在\"8分鐘內\"針對題目給予的圖片輸入文字作答\n" +
                                        "在作答時間之內可連續作答，每次的作答紀錄均會在右側的作答歷程中顯示。\n" +
                                        "若提早結束作答可在頁面上按\"送出\"按鍵繳交作答。")
                            .setPositiveButton("開始測驗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this,Game3Activity.class);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        });

        btnGame4.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(view.getId()==R.id.btnGame4){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("圖繪展開遊戲作答說明")
                            .setMessage("此測驗在\"20分鐘內\"須完成圖像的作答，並且為圖片\"輸入作品名稱\"。\n" +
                                        "在作答時間之內可連續作答，每次的作答紀錄均會在右側的作答歷程中顯示。\n" +
                                        "結束作答可在頁面上按\"送出\"按鍵繳交作答，繼續作畫。")
                            .setPositiveButton("開始測驗", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this,Game4Activity.class);
                                    startActivity(intent);
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,1,"登出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case 1:
                loggedin = false;
                finish();
                if (!loggedin){
                    Intent it = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(it);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
