package com.example.yafun.thinkingapplication;


import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.io.ByteArrayOutputStream;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.json.*;

public class ConnServer {

    private String database = null;
    private String webRequest = null;      //  web request;type:json

    private String account = null;
    private String password = null;
    private String memberData = null;
    private String classdata = null;
    private String emailtext = null;
    private String school = null;
    private String sex = null;
    private String age = null;
    private String living = null;
    private String religion = null;

    public ConnServer(){
    }

    /**
     * Loging
     *
     * @param useraccount
     * @param userpasswd
     */
    public ConnServer(String useraccount, String userpasswd) {
        this.account = useraccount;
        this.password = userpasswd;
    }

    /**
     * 處理遊戲 drawing , drawingmult 建構子
     *
     * @param database
     * @param content
     * @param crtuser
     */
    public ConnServer(String database, String content, String crtuser, Bitmap image) {
        this.database = database;
        switch (this.database) {
            case "drawing":
                draw(content, crtuser, toBase64(image));
                break;
            case "drawingmult":
                draw(content, crtuser, toBase64(image));
                break;
            default:
                ;
        }
    }

    /**
     * oneimage 建構子
     * @param database
     * @param content
     * @param crtuser
     */
    public ConnServer(String database, String content, String crtuser,String chosenImage) {
        this.database = database;

        if (this.database.equals("oneimage")) {
            oneimage(content, crtuser,chosenImage);
        } else {
        }

    }

    /**
     * 處理遊戲 association 建構子
     *
     * @param database
     * @param description
     * @param chosenList
     * @param crtuser
     */
    public ConnServer(String database, String description, String[] chosenList, String crtuser) {
        this.database = database;

        int index = 0;
        String tmpText = "";

        //1116 整合格式
        for (String option : chosenList)
            tmpText = tmpText + option;

        //String chosenContent = tmpText.substring(0, tmpText.length() - 1) ;     // column_create('chosen1','0','chosen2','0' ...)
        String chosenContent = tmpText;

        showMessage("chosenContent_test", chosenContent); //for test

        if (this.database.equals("association")) {
            association(description, chosenContent, crtuser);
        } else {
        }

    }

    /**
     * @param Name    pictureName
     * @param crtuser userName
     */
    private void draw(String Name, String crtuser, String base64img) {

        JSONObject obj;
        //Object jsonOb = null;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://13.228.235.102/thinkingapp/connDB/drawing.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("database", this.database));
        params.add(new BasicNameValuePair("Name", Name));
        params.add(new BasicNameValuePair("base64img", base64img));
        params.add(new BasicNameValuePair("crtuser", crtuser));


        try {

            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));    //  set post params
            HttpResponse response = client.execute(request);        //  get web response
            HttpEntity resEntity = response.getEntity();
            webRequest = EntityUtils.toString(resEntity);             //  取得網頁 REQUEST

            showMessage("webRequest", webRequest);

            obj = new JSONObject(this.webRequest);  // parse web request
            String webResponse = obj.getJSONObject(this.database).getString("response");
            showMessage(this.database, webResponse);

        } catch (java.io.IOException e) {
            showMessage("IOException", e.getMessage());
        } catch (org.json.JSONException e) {
            showMessage("JSON Error", e.getMessage());
        }
    }

    /**
     * oneimage 建構子
     *
     * @param Data
     * @param crtuser
     * @param
     */
    private void oneimage(String Data, String crtuser,String ChosenImage) {

        JSONObject obj;
        //Object jsonOb = null;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://13.228.235.102/thinkingapp/connDB/oneimage.php");


        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("database", this.database));
        params.add(new BasicNameValuePair("Data", Data));
        //params.add(new BasicNameValuePair("imageID", imageID));
        params.add(new BasicNameValuePair("crtuser", crtuser));
        params.add(new BasicNameValuePair("ChosenImage", ChosenImage));

        //showMessage("oneimage imageID", imageID);
        try {

            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));    //  set post params
            HttpResponse response = client.execute(request);        //  get web response

            HttpEntity resEntity = response.getEntity();
            webRequest = EntityUtils.toString(resEntity);             //  取得網頁 REQUEST

            showMessage("webRequest", webRequest);

            obj = new JSONObject(this.webRequest);  // parse web request
            String webResponse = obj.getJSONObject(this.database).getString("response");
            showMessage(this.database, webResponse);

        } catch (java.io.IOException e) {
            showMessage("IOException", e.getMessage());
        } catch (org.json.JSONException e) {
            showMessage("JSON Error", e.getMessage());
        }
    }

    /**
     * Game association connection
     *
     * @param description
     * @param chosenContent
     * @param crtuser
     */
    private void association(String description, String chosenContent, String crtuser) {

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://13.228.235.102/thinkingapp/connDB/association.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("Description", description));
        params.add(new BasicNameValuePair("ChosenContent", chosenContent));
        params.add(new BasicNameValuePair("crtuser", crtuser));

        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));    //  set post params
            HttpResponse response = client.execute(request);        //  get web response
            HttpEntity resEntity = response.getEntity();
            webRequest = EntityUtils.toString(resEntity);             //  取得網頁 REQUEST

            showMessage("association webRequest", webRequest);

        } catch (java.io.IOException e) {
            showMessage("IOException", e.getMessage());
        }
    }

    /**
     * Check Login  (20190411)
     *
     * @param userAccount
     * @param userPasswd
     * @return  boolean
     */
    public boolean CheckLogin(String userAccount,String userPasswd) {

        JSONObject obj;
        String webResponse = null;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://13.228.235.102/thinkingapp/connDB/login.php");  // Post request

        /// Setting Params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("account", userAccount));
        params.add(new BasicNameValuePair("password", userPasswd));

        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));    //  Setting post params
            HttpResponse response = client.execute(request);
            HttpEntity resEntity = response.getEntity();            //  Get web response

            this.webRequest = EntityUtils.toString(resEntity);             //  取得網頁 REQUEST

            obj = new JSONObject(this.webRequest);            // parse web request
            webResponse = obj.getString("response");    // store webResponse
            showMessage("Response", webResponse);

            if (webResponse.equals("successful")) {
                this.memberData = obj.getString("member");   // store member information
                showMessage("Member Data", this.memberData);
                return true;
            }
            else
                return false;

        } catch (java.io.IOException e) {
            showMessage("IOException", e.getMessage());
        } catch (org.json.JSONException e) {
            showMessage("JSON Error", e.getMessage());
        } finally {
            if (webResponse.equals("successful"))
                return true;
            else
                return false;
        }
    }

    /**
     * Check Sign Up
     * @return boolean
     */
    public boolean CheckSignUp(String account, String password, String classdata, String emailtext, String school, String sex,String age,String living,String religion){

        JSONObject obj;
        String webResponse = null;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://13.228.235.102/thinkingapp/connDB/signup.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("account", account));
        params.add(new BasicNameValuePair("password", password));
        params.add(new BasicNameValuePair("classdata", classdata));
        params.add(new BasicNameValuePair("emailtext", emailtext));
        params.add(new BasicNameValuePair("school", school));
        params.add(new BasicNameValuePair("sex", sex));
        params.add(new BasicNameValuePair("age", age));
        params.add(new BasicNameValuePair("living", living));
        params.add(new BasicNameValuePair("religion", religion));

        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));    //  set post params
            HttpResponse response = client.execute(request);        //  get web response

            HttpEntity resEntity = response.getEntity();
            this.webRequest = EntityUtils.toString(resEntity);             //  取得網頁 REQUEST

            obj = new JSONObject(this.webRequest);            // parse web request
            webResponse = obj.getString("response");    // store webResponse
            showMessage("Response", webResponse);

        } catch (java.io.IOException e) {
            showMessage("IOException", e.getMessage());
        } catch (org.json.JSONException e) {
            showMessage("JSON Error", e.getMessage());
        } finally {

            if (webResponse.equals("successful"))
                return true;
            else if (webResponse.equals("fail"))
                return false;
            else
                return false;
        }
    }

    /**
     * convert bitmap to base64
     *
     * @param bitmap
     * @return
     */
    public String toBase64(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();
        //bitmap.recycle();   //  recycle resource

        return android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    /**
     * update anwser time 上傳並更新當前使用者的紀錄時間
     *
     * @param game
     * @param currentTime
     */
    public void updateAnwsertime(String game, String currentTime, String userID) {

        JSONObject obj;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://13.228.235.102/thinkingapp/connDB/update_answertime.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("game", game));
        params.add(new BasicNameValuePair("currentTime", currentTime));
        params.add(new BasicNameValuePair("userID", userID));

        showMessage("updateAnwsertime console", (game+" "+currentTime+" "+userID));

        try {

            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));    //  set post params
            HttpResponse response = client.execute(request);        //  get web response

            HttpEntity resEntity = response.getEntity();
            webRequest = EntityUtils.toString(resEntity);             //  取得網頁 REQUEST

            showMessage("webRequest", webRequest);

            obj = new JSONObject(this.webRequest);  // parse web request
            String webResponse = obj.getString("response");    // store webResponse
            showMessage("updateRes", webResponse);

        } catch (java.io.IOException e) {
            showMessage("IOException", e.getMessage());
        } catch (org.json.JSONException e) {
            showMessage("JSON Error", e.getMessage());
        }
    }

    /**
     *  select 個人作答紀錄(圖)
     * @param database  選擇的資料表
     * @param crtuser   使用者名稱
     */
    public String[] PersonalRecord(String database, String crtuser) {

        showMessage("[Database]", database);

        JSONObject obj;
        String webResponse = null;
        String answerRecord[] = null;
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://13.228.235.102/thinkingapp/connDB/select_personalRecord.php");

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("database", database));
        params.add(new BasicNameValuePair("crtuser", crtuser));

        try {

            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));    //  set post params
            HttpResponse response = client.execute(request);        //  get web response

            HttpEntity resEntity = response.getEntity();
            this.webRequest = EntityUtils.toString(resEntity);             //  取得網頁 REQUEST
            showMessage("webRequest", this.webRequest);

            obj = new JSONObject(this.webRequest);            // parse web request
            //webResponse = obj.getString("response");    // store webResponse

            String answerRecordo = obj.getString("AnswerRecord");   // store member information
            showMessage("AnswerRecord", answerRecordo);

            JSONArray data = obj.getJSONArray("AnswerRecord");

            answerRecord = new String[data.length()];
            for(int i = 0; i < data.length(); i++) {
                answerRecord[i] = data.get(i).toString();
                // showMessage("i_"+i, data.get(i).toString());
            }

        } catch (java.io.IOException e) {
            showMessage("IOException", e.getMessage());
        } catch (org.json.JSONException e) {
            showMessage("JSON Error", e.getMessage());
        }finally {
            return answerRecord;
        }
    }

    private void showMessage(String title, String content) {
        Log.d(title, content);
    }



    /**
     * getMemberInf
     *
     * @return memberInf
     */
    public String getMemberData() {
        return memberData;
    }
}
