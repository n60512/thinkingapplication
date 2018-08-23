package com.example.yafun.thinkingapplication;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DrawActivity extends AppCompatActivity {

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

    //pic
    private int serverResponseCode = 0;
    private String upLoadServerUri = null;
    private String imagepath = "http://140.122.91.218/thinkingapp/connDB/upload_file.php";
    Intent data;
    String imgID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set view set title
        setContentView(R.layout.drawerlayout_draw);
        setTitle("一筆畫遊戲");

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

        // start timer
        startTimer();

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
                    //upload
                    saveImage(baseBitmap);
                    Uri selectedImageUri = getImageUri(imgvDraw.getContext(), baseBitmap);
                    Log.e("Uri", selectedImageUri + "");
                    imagepath = getPath(selectedImageUri);
                    Log.e("imagepath", imagepath);
                    Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
                    uploadFile(imagepath);
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        Log.e("getContentResolver", inContext.getContentResolver() + "");
        Log.e("inImage", inImage + "");
        String path = MediaStore.Images.Media.insertImage(DrawActivity.this.getContentResolver(), inImage, edtName.getText().toString(), null);
        Log.e("pathpath", path + "");
        return Uri.parse(path);
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void saveImage(Bitmap bitmap) {
        FileOutputStream fOut;
        try {
            File dir = new File("/sdcard/thinkingTest/");
            if (!dir.exists()) {
                dir.mkdir();
            }

            String tmp = "/sdcard/thinkingTest/" + imgID + ".jpg";
            fOut = new FileOutputStream(tmp);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

            try {
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int uploadFile(String sourceFileUri) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            Log.e("uploadFile", "Source File not exist :" + imagepath);

            runOnUiThread(new Runnable() {
                public void run() {
                    Log.e("Source File not exist :" + imagepath, "");
                }
            });

            return 0;
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file your server. \n\n";
                            //messageText.setText(msg);
                            Toast.makeText(DrawActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (MalformedURLException ex) {
                //dialog.dismiss();
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.e("MalformedURLcheckscript", "");
                        Toast.makeText(DrawActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                //dialog.dismiss();
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.e("GotExceptionseelogcat ", "");
                        Toast.makeText(DrawActivity.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("UpfiletoserverException", "Exception : " + e.getMessage(), e);
            }
            //dialog.dismiss();
            return serverResponseCode;
        } // End else block
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
                                            conn[index] = new ConnServer("drawing", content, "test01");
                                            imgID = conn[index].getImageID();
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
